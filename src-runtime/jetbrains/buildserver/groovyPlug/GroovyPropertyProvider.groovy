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
import java.text.SimpleDateFormat
import java.util.Map.Entry
import jetbrains.buildServer.serverSide.ParametersPreprocessor
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.serverSide.ServerExtensionHolder
import jetbrains.buildServer.vcs.SVcsModification
import jetbrains.buildServer.vcs.VcsModificationHistory
import org.jetbrains.annotations.Nullable
import jetbrains.buildServer.vcs.VcsRootEntry
import jetbrains.buildServer.vcs.SVcsRoot

/**
 * @author Yegor.Yarko
 * Date: 15.01.2009
 */

public class GroovyPropertyProvider implements ParametersPreprocessor {
  private static final Logger LOG = Logger.getInstance(GroovyPropertyProvider.class.getName());

  VcsModificationHistory vcsModificationHistory;
  ServerExtensionHolder extensionHolder;

  GroovyPropertyProvider() {
    LOG.info("GroovyPropertyProvider initialized.");
  }

  public void fixRunBuildParameters(SRunningBuild build, Map<String, String> runParameters, Map<String, String> buildParams) {
    LOG.debug("GroovyPropertyProvider asked for properties for build (buildId=" + build.buildId + ")");
    Map<String, String> buildParamsToAdd = new HashMap<String, String>();

    addLastModification(buildParamsToAdd, build);
    addBuildStartTime(buildParamsToAdd, build);
    addLastModificationsRevisions(buildParamsToAdd, build);

    addBuildParameters(buildParamsToAdd, buildParams)
  }

  private def addBuildParameters(HashMap<String, String> buildParamsToAdd, Map<String, String> resultingBuildParams) {
    if (!buildParamsToAdd.empty) {
      for (Entry<String, String> parameter: buildParamsToAdd.entrySet()) {
        LOG.debug("Adding build parameter '" + parameter.getKey() + "' with value =" + parameter.getValue());
        resultingBuildParams.put(parameter.getKey(), parameter.getValue());
      }
    }
    LOG.debug(buildParamsToAdd.size() + " parameters added.");
  }

  @Nullable SVcsModification getLastModification(SBuild build) {
    Long modificationId = build.getBuildPromotion().getLastModificationId();
    if (modificationId == null || modificationId == -1) {
      return null;
    }
    return vcsModificationHistory.findChangeById(modificationId);
  }

  void addLastModification(Map<java.lang.String, java.lang.String> buildParametersToAdd, SRunningBuild build) {
    SVcsModification lastModification = getLastModification(build);
    if (lastModification != null) {
      Date lastChangeDate = lastModification.getVcsDate();
      Util.addDateTimeProperty(buildParametersToAdd, lastChangeDate, "yyyyMMdd'T'HHmmssZ", "build.lastChange.time", "BUILD_VCS_LASTCHANGE_TIMESTAMP");
    }
  }

  void addBuildStartTime(Map<java.lang.String, java.lang.String> buildParametersToAdd, SRunningBuild build) {
    Date buildStartTime = build.getStartDate();
    Util.addDateTimeProperty(buildParametersToAdd, buildStartTime, "yyyyMMdd", "build.start.date", "BUILD_START_DATE");
    Util.addDateTimeProperty(buildParametersToAdd, buildStartTime, "HHmmss", "build.start.time", "BUILD_START_TIME");
  }

  void addLastModificationsRevisions(HashMap<java.lang.String, java.lang.String> buildParametersToAdd, SRunningBuild build) {
    final List<VcsRootEntry> rootEntries = build.getVcsRootEntries();
    final Map<SVcsRoot, String> rootVersions = new HashMap<SVcsRoot, String>();

    List<SVcsModification> modifications = vcsModificationHistory.getAllModifications(build.getBuildType());
    int toFill = rootEntries.size();

    for (SVcsModification modification: modifications) {
      String version = modification.getDisplayVersion();
      if (rootVersions.get(modification.getVcsRoot()) == null && version != null) {
        rootVersions.put(modification.getVcsRoot(), version);
        --toFill;
      }
      if (toFill == 0) {
        break
      }
    }

    for (Map.Entry<SVcsRoot, String> version: rootVersions.entrySet()) {

      String name = jetbrains.buildServer.util.StringUtil.replaceNonAlphaNumericChars(version.getKey().getName(), (char) '_');
      buildParametersToAdd.put("env.BUILD_REVISION_" + name, version.getValue());
      buildParametersToAdd.put("system.build.revision." + name, version.getValue());
    }
    if (rootEntries.size() == 1 && rootVersions.size() == 1) {
      String version = rootVersions.entrySet().iterator().next().getValue();
      if (version != null) {
        buildParametersToAdd.put("env.BUILD_REVISION", version);
        buildParametersToAdd.put("system.build.revision", version);
      }
    }
  }
}