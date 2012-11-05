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

package jetbrains.buildserver.groovyPlug;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.vcs.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Yegor.Yarko
 *         Date: 19.06.2009
 */
public class DataUtil {
  private static final Logger LOG = Logger.getInstance(DataUtil.class.getName());

  VcsModificationHistory vcsModificationHistory;

  @NotNull
  Map<SVcsRoot, SVcsModification> getLastModificationsRevisions(@NotNull SBuild build) {
    final Map<SVcsRoot, SVcsModification> rootVersions = new HashMap<SVcsRoot, SVcsModification>();

    final SBuildType buildType = build.getBuildType();
    if (buildType == null) return rootVersions;

    List<SVcsModification> modifications = build.getChanges(SelectPrevBuildPolicy.SINCE_FIRST_BUILD, true);
    if (modifications.isEmpty()) return rootVersions;

    final Set<SVcsRoot> parentRoots = new HashSet<SVcsRoot>();
    final Set<Long> parentRootIds = new HashSet<Long>();
    int expectedRevisionsNum = 0;

    for (VcsRootInstanceEntry re: build.getVcsRootEntries()) {
      final SVcsRoot parent = re.getVcsRoot().getParent();
      parentRoots.add(parent);
      parentRootIds.add(parent.getId());
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Searching for last included revisions. Root entries number: " + expectedRevisionsNum + ", total modifications: " + modifications.size());
    }

    for (SVcsModification modification : modifications) {
      if (modification.isPersonal()) continue;
      final SVcsRoot parentRoot = modification.getVcsRoot().getParent();
      if (parentRootIds.contains(parentRoot.getId()) && !rootVersions.containsKey(parentRoot)) {
        rootVersions.put(parentRoot, modification);
        expectedRevisionsNum--;
      }

      if (expectedRevisionsNum == 0) break;
    }

    for (SVcsRoot parent: parentRoots) {
      if (rootVersions.containsKey(parent)) continue;
      rootVersions.put(parent, null);
    }

    return rootVersions;
  }

  public void setVcsModificationHistory(VcsModificationHistory vcsModificationHistory) {
    this.vcsModificationHistory = vcsModificationHistory;
  }

  public VcsModificationHistory getVcsModificationHistory() {
    return vcsModificationHistory;
  }
}
