/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildserver.groovyPlug

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.buildDistribution.StartBuildPrecondition
import jetbrains.buildServer.serverSide.buildDistribution.WaitReason
import jetbrains.buildServer.serverSide.buildDistribution.QueuedBuildInfo
import jetbrains.buildServer.serverSide.buildDistribution.BuildDistributorInput
import jetbrains.buildServer.serverSide.buildDistribution.SimpleWaitReason
import jetbrains.buildServer.serverSide.buildDistribution.BuildPromotionInfo
import jetbrains.buildServer.serverSide.buildDistribution.RunningBuildInfo
import jetbrains.buildServer.BuildAgent

/**
 * User: Yegor Yarko
 * Date: 15.03.2009
 *
 * Further possible improvements:
 * - provide lock waiting details in the WaitReason
 * - do not allow read-lock builds to start is there are write-locks builds earlier in the queue (?)  
 *
 */

public class BuildResourcesLock implements StartBuildPrecondition {
  private static final Logger LOG = Logger.getInstance(BuildResourcesLock.class.getName());

  @Override
  public WaitReason canStart(QueuedBuildInfo queuedBuild, Map<QueuedBuildInfo, BuildAgent> canBeStarted, BuildDistributorInput buildDistributorInput, boolean emulationMode) {
    HashSet locks = LocksUtil.getBuildLocks(queuedBuild.getBuildPromotionInfo())
    if (!emulationMode && LOG.isDebugEnabled()) {
      LOG.debug("Found locks " + locks + " for build promotion " + queuedBuild.getBuildPromotionInfo());
    }
    if (!LocksUtil.available(locks, getBuildPromotions(buildDistributorInput.getRunningBuilds(), canBeStarted.keySet()))) {
      return new SimpleWaitReason("Waiting for build locks to free.");
    }
    return null;
  }

  Collection<BuildPromotionInfo> getBuildPromotions(Collection<RunningBuildInfo> runningBuildInfos, Collection<QueuedBuildInfo> queuedBuildInfos) {
    ArrayList<BuildPromotionInfo> result = new ArrayList<BuildPromotionInfo>(runningBuildInfos.size() + queuedBuildInfos.size());
    for (RunningBuildInfo runningBuildInfo:runningBuildInfos){
      result.add(runningBuildInfo.getBuildPromotionInfo());
    }
    for (QueuedBuildInfo queuedBuildInfo:queuedBuildInfos){
      result.add(queuedBuildInfo.getBuildPromotionInfo());
    }
    return result;
  }
}

public class LockManager {
  Map<String, TakenLockInfo> takenLocks = new HashMap<String, TakenLockInfo>();

  boolean lock(Lock lock, BuildPromotionInfo build) {
    TakenLockInfo takenLock = takenLocks.get(lock.name);
    if (takenLock == null) {
      takenLock = new TakenLockInfo();
      takenLocks.put(lock.name, takenLock);
    }
    switch (lock.type) {
      case LockType.ReadLock: return takenLock.readLocksBuilds.add(build);
      case LockType.WriteLock: return takenLock.writeLocksBuilds.add(build);
    }
  }

  boolean canLock(Lock lock) {
    switch (lock.type) {
      case LockType.ReadLock:
        TakenLockInfo takenLock = takenLocks.get(lock.name);
        return takenLock != null ? takenLock.writeLocksBuilds.empty : true;    //todo: so what about "?" and NPE
      case LockType.WriteLock:
        TakenLockInfo takenLock = takenLocks.get(lock.name);
        return takenLock != null ? takenLock.writeLocksBuilds.empty && takenLock.readLocksBuilds.empty : true;    //todo: so what about "?" and NPE
    }
  }

  boolean lock(Collection<Lock> locks, BuildPromotionInfo build) {
    for (Lock lock: locks) {
      lock(lock, build);
    }
  }

  boolean canLock(Collection<Lock> locks) {
    for (Lock lock: locks) {
      if (!canLock(lock)) {
        return false;
      }
    }
    return true;
  }

}

class TakenLockInfo {
  Set<BuildPromotionInfo> readLocksBuilds = new HashSet<BuildPromotionInfo>();
  Set<BuildPromotionInfo> writeLocksBuilds = new HashSet<BuildPromotionInfo>();
}


public class LocksUtil {
  private static final Logger LOG = Logger.getInstance(LocksUtil.class.getName());

  static boolean available(Collection<Lock> locksToTake, Collection<BuildPromotionInfo> buildPromotionInfos) {
    LockManager locksManager = new LockManager();
    for (BuildPromotionInfo buildPromotionInfo: buildPromotionInfos) {
      locksManager.lock(getBuildLocks(buildPromotionInfo), buildPromotionInfo);
    }
    return locksManager.canLock(locksToTake);
  }

  static Collection<Lock> getBuildLocks(BuildPromotionInfo buildPromotionInfo) {
    Collection<Lock> result = new HashSet<Lock>();
    Map<String, String> buildParameters = buildPromotionInfo.getBuildParameters();

    for (Map.Entry<String, String> parameter: buildParameters.entrySet()) {
      Lock lock = getLock(parameter.getKey());
      if (lock != null) {
        result.add(lock);
      }
    }
    return result;
  }

  static Lock getLock(String buildParameterName) {
    if (!buildParameterName.startsWith("system.locks.")) {
      return null;
    }

    String lockParameterString = buildParameterName.substring("system.locks.".length());


    Lock result = new Lock();

    result.type = getLockType(lockParameterString);
    if (result.type == null) {
      LOG.warn("Error parsing lock type of '" + buildParameterName + "'. Supported values are " + LockType.values());
      return null;
    }

    try {
      result.name = lockParameterString.substring(result.type.name.length() + 1)
    } catch (IndexOutOfBoundsException e) {
      LOG.warn("Error parsing lock name of '" + buildParameterName + "'. Supported format is 'system.locks.[read|write]Lock.<lock name>'");
      return null;
    };

    if (result.name.length() == 0) {
      LOG.warn("Error parsing lock name of '" + buildParameterName + "'. Supported format is 'system.locks.[read|write]Lock.<lock name>'");
      return null;
    }

    return result;
  }

  private static def LockType getLockType(String lockParameterString) {
    for (LockType lockType: LockType.values()) {
      if (lockParameterString.startsWith(lockType.name)) {
        return lockType;
      }
    }
    return null;
  }
}


enum LockType {
  ReadLock("readLock"),
  WriteLock("writeLock");

  String name;

  LockType(String nameP) {
    name = nameP;
  }
};

class Lock {
  LockType type;
  String name;

  String toString() {
    return Lock.class.getName() + "{name:" + name + ", type:" + type + "}";
  }
}