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
import jetbrains.buildServer.vcs.SVcsModification
import jetbrains.buildServer.vcs.VcsModificationHistory
import jetbrains.buildserver.groovyPlug.GroovyPropertyProvider
import org.jetbrains.annotations.Nullable

/**
 * @author Yegor.Yarko
 * Date: 15.01.2009
 */

public class GroovyPropertyProvider implements ParametersPreprocessor {
  private static final Logger LOG = Logger.getInstance(GroovyPropertyProvider.class.getName());

  VcsModificationHistory vcsModificationHistory;

  GroovyPropertyProvider() {
    LOG.info("GroovyPropertyProvider initialized.");
  }

  public void fixRunBuildParameters(SRunningBuild build, Map<String, String> runParameters, Map<String, String> buildParams) {
    LOG.debug("GroovyPropertyProvider asked for properties for build (buildId=" + build.buildId + ")");
    Map<String, String> buildParamsToAdd = new HashMap<String, String>();

    addLastModification(buildParamsToAdd, build);

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

  void addLastModification(java.util.Map<java.lang.String, java.lang.String> buildParametersToAdd, SRunningBuild build) {
    SVcsModification lastModification = getLastModification(build);
    if (lastModification != null) {
      Date lastChangeDate = lastModification.getVcsDate();
      String formattedDate = (new SimpleDateFormat("yyyyMMdd'T'HHmmssZ")).format(lastChangeDate);
      buildParametersToAdd.put("system.build.lastChange.time", formattedDate);
      buildParametersToAdd.put("env.BUILD_VCS_LASTCHANGE_TIMESTAMP", formattedDate);
    }
  }

}