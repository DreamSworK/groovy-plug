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


import com.intellij.openapi.diagnostic.Logger
import java.text.SimpleDateFormat
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.parameters.AbstractBuildParametersProvider
import jetbrains.buildServer.util.StringUtil
import jetbrains.buildServer.vcs.SVcsModification
import jetbrains.buildServer.vcs.SVcsRoot
import jetbrains.buildServer.vcs.VcsRootEntry
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author Yegor.Yarko
 *         Date: 04.03.2009
 */
public class GroovyPropertiesProvider extends AbstractBuildParametersProvider {
  private static final Logger LOG = Logger.getInstance(GroovyPropertiesProvider.class.getName());

  DataUtil dataProvider;

  GroovyPropertiesProvider() {
    LOG.info("GroovyPropertiesProvider initialized.");
  }

  @NotNull
  @Override
  public Map<String, String> getParameters(@NotNull final SBuild build, final boolean emulationMode) {
    LOG.debug("Processing build: " + build);
    final BuildParameters parameters = new BuildParameters();
    try {
      addBuildStartTime(parameters, build);
      addRunParameters(parameters, build);
      addTriggeredBy(parameters, build);
      addLastModificationsRevisions(parameters, build);
    } catch (Exception e) {
      LOG.error("Critical error occurred during parameters calculation", e);
    }
    LOG.debug("Finished processing build: " + build);
    return parameters.getParameters();
  }

  private void addRunParameters(@NotNull final BuildParameters parameters, @NotNull final SBuild build) {
    SBuildType buildType = build.getBuildType();
    if (buildType == null) return;

    for (Map.Entry<String, String> runParameter: buildType.getRunParameters().entrySet()) {
      parameters.addConfiguration("runParam." + runParameter.getKey(), runParameter.getValue());
    }
  }

  void addBuildStartTime(@NotNull final BuildParameters parameters, SBuild build) {
    Date buildStartTime = build.getStartDate();
    parameters.addEnvAndSystem("build.start.date", (new SimpleDateFormat("yyyyMMdd")).format(buildStartTime));
    parameters.addEnvAndSystem("build.start.time", (new SimpleDateFormat("HHmmss")).format(buildStartTime));
  }

  void addTriggeredBy(@NotNull final BuildParameters parameters, SBuild build) {
    if (build.getTriggeredBy().getUser() != null) {
      parameters.addConfiguration("build.triggeredBy.username", build.getTriggeredBy().getUser().getUsername());
    }
    parameters.addConfiguration("build.triggeredBy", build.getTriggeredBy().getAsString());
  }

  void addLastModificationsRevisions(@NotNull final BuildParameters parameters, SBuild build) {
    final Map<SVcsRoot, SVcsModification> rootVersions = dataProvider.getLastModificationsRevisions(build);

    for (VcsRootEntry rootEntry: build.getVcsRootEntries()){
      SVcsRoot root = (SVcsRoot)rootEntry.getVcsRoot()
      addModificationParams(parameters, root, rootVersions.get(root));
    }
    if (build.getVcsRootEntries().size() == 1) {
      if (rootVersions.size() == 1) {
        addModificationParams(parameters, null, rootVersions.entrySet().iterator().next().getValue());
      }else{
        addModificationParams(parameters, null, null);
      }
    }
  }

  private def addModificationParams(BuildParameters parameters, @Nullable SVcsRoot root, @Nullable SVcsModification modification) {
    String suffix = root == null ? "" : "." + StringUtil.replaceNonAlphaNumericChars(root.getName(), (char) '_');
    parameters.addEnvAndSystem("build.vcs.lastIncluded.revision" + suffix, getVersion(modification));
    parameters.addEnvAndSystem("build.vcs.lastIncluded.timestamp" + suffix, getDate(modification))
  }

  private @NotNull String getDate(@Nullable SVcsModification modification) {
    if (modification != null) {
      return (new SimpleDateFormat("yyyyMMdd'T'HHmmssZ")).format(modification.getVcsDate())
    }else{
      "";
    }
  }

  private @NotNull  String getVersion(@Nullable SVcsModification modification) {
    if (modification == null){
      return "";
    }
    String version = modification.getDisplayVersion()
    if (version == null) {
      version = modification.getVersion();
    }
    return version;
  }
}