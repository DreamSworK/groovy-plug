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
import jetbrains.buildServer.serverSide.parameters.AbstractBuildParametersProvider;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;

import jetbrains.buildServer.vcs.VcsRootEntry;
import jetbrains.buildServer.vcs.SVcsRoot;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.VcsModificationHistory;
import jetbrains.buildserver.groovyPlug.Util;
import org.jetbrains.annotations.Nullable;

/**
 * @author Yegor.Yarko
 *         Date: 04.03.2009
 */
public class GroovyReferencePropertiesProvider extends AbstractBuildParametersProvider {
  private static final Logger LOG = Logger.getInstance(GroovyReferencePropertiesProvider.class.getName());

  DataUtil dataProvider;

  GroovyReferencePropertiesProvider() {
    LOG.info("GroovyReferencePropertiesProvider initialized.");
  }

  @NotNull
  @Override
  public Map<String, String> getParameters(@NotNull SBuild build) {
    LOG.debug("Processing build: " + build);
    final Map<String, String> result = new HashMap<String, String>();
    addBuildStartTime(result, build);
    addRunParameters(result, build);
    addTriggeredBy(result, build);
    addLastModificationsRevisions(result, build);
    return result;
  }

  private void addRunParameters(Map<String, String> buildParametersToAdd, SBuild build) {
    SBuildType buildType = build.getBuildType();
    if (buildType == null) return;

    for (Map.Entry<String, String> runParameter : buildType.getRunParameters().entrySet()) {
      buildParametersToAdd.put("runParam." + runParameter.getKey(), runParameter.getValue());
    }
  }

  void addBuildStartTime(Map<java.lang.String, java.lang.String> buildParametersToAdd, SBuild build) {
    Date buildStartTime = build.getStartDate();
    Util.addDateTimeProperty(buildParametersToAdd, buildStartTime, "yyyyMMdd", "build.start.date");
    Util.addDateTimeProperty(buildParametersToAdd, buildStartTime, "HHmmss", "build.start.time");
  }

  void addTriggeredBy(HashMap<String,String> buildParametersToAdd, SBuild build) {
    if (build.getTriggeredBy().getUser() != null) {
      buildParametersToAdd.put("build.triggeredBy.username", build.getTriggeredBy().getUser().getUsername());
    }
    buildParametersToAdd.put("build.triggeredBy", build.getTriggeredBy().getAsString());
  }

 void addLastModificationsRevisions(Map<String, String> buildParametersToAdd, SBuild build) {
   final Map<SVcsRoot, String> rootVersions = dataProvider.getLastModificationsRevisions(build);

   for (Map.Entry<SVcsRoot, String> versionEntry: rootVersions.entrySet()) {
     String name = jetbrains.buildServer.util.StringUtil.replaceNonAlphaNumericChars(versionEntry.getKey().getName(), (char) '_');
     buildParametersToAdd.put("build.vcs.lastIncluded.revision." + name, versionEntry.getValue());
   }
   if (build.getVcsRootEntries().size() == 1 && rootVersions.size() == 1) {
     String version = rootVersions.entrySet().iterator().next().getValue();
     if (version != null) {
       buildParametersToAdd.put("build.vcs.lastIncluded.revision", version);
     }
   }
 }
}