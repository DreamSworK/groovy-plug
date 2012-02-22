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

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.BuildAgent;
import jetbrains.buildServer.BuildType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Collection;
import java.util.List;

public class GroovyBuildServerListener extends BuildServerAdapter {
  public void agentRegistered(SBuildAgent agent, final long currentlyRunningBuildId) {
  }

  public void agentUnregistered(SBuildAgent agent) {
  }

  public void beforeAgentUnregistered(final SBuildAgent agent) {
  }

  public void agentRemoved(SBuildAgent agent) {
  }

  public void buildTypeAddedToQueue(SBuildType buildType) {
  }

  public void buildTypeAddedToQueue(@NotNull final SQueuedBuild queuedBuild) {
  }

  public void buildTypeRemovedFromQueue(SBuildType buildType,
                                        final User user,
                                        final String comment,
                                        final String requestor,
                                        final Date whenQueued) {
  }

  public void buildRemovedFromQueue(@NotNull SQueuedBuild queued,
                                        final User user,
                                        final String comment) {
    // deprecation workaround
    buildTypeRemovedFromQueue(queued.getBuildType(), user, comment, "", queued.getWhenQueued());
  }

  public void buildPinned(final @NotNull SBuild build, final @Nullable User user, final @Nullable String comment) {
  }

  public void buildUnpinned(final @NotNull SBuild build, final @Nullable User user, final @Nullable String comment) {
  }

  public void buildQueueOrderChanged() {
  }

  public void buildTypeRegistered(SBuildType buildType) {
  }

  public void buildTypeUnregistered(SBuildType buildType) {
  }

  public void buildTypeMoved(final SBuildType buildType, final SProject original) {
  }

  public void beforeBuildTypeDeleted(@NotNull final String buildTypeId) {
  }

  public void buildTypeDeleted(@NotNull final String buildTypeId) {
  }

  public void buildTypeActiveStatusChanged(SBuildType buildType) {
  }

  public void buildStarted(SRunningBuild build) {
  }

  public void changesLoaded(SRunningBuild build) {
  }

  public void buildChangedStatus(SRunningBuild build, final Status oldStatus, final Status newStatus) {
  }

  public void buildFinished(SRunningBuild build) {
  }

  public void beforeBuildFinish(final SRunningBuild runningBuild) {
  }

  public void beforeBuildFinish(final SRunningBuild runningBuild, final boolean buildFailed) {
  }

  public void messageReceived(SRunningBuild build, final BuildMessage1 message) {
  }

  public void responsibleChanged(@NotNull SBuildType bt,
                                 @NotNull ResponsibilityInfo oldValue,
                                 @NotNull ResponsibilityInfo newValue,
                                 final boolean isUserAction) {
  }


  public void beforeEntryDelete(final SFinishedBuild entry) {
  }

  public void entryDeleted(final SFinishedBuild entry) {
  }

  public void projectCreated(final String projectId) {
  }

  public void projectRemoved(final String projectId) {
  }

  public void projectPersisted(final String projectId) {
  }

  public void projectRestored(final String projectId) {
  }

  public void serverShutdown() {
  }

  public void serverStartup() {
  }

  public void pluginsLoaded() {
  }

  public void buildInterrupted(SRunningBuild build) {
  }

  public void changeAdded(final VcsModification modification, final VcsRoot root, final SRunningBuild startingBuild) {
  }

  public void changeAdded(@NotNull VcsModification modification, @NotNull VcsRoot root, @Nullable final Collection<SBuildType> buildTypes) {

  }

  public void agentStatusChanged(@NotNull final SBuildAgent agent, final boolean wasEnabled, final boolean wasAuthorized) {
  }

  public void cleanupStarted() {
  }

  public void cleanupFinished() {
  }

  public void serverShutdownComplete() {
  }

  public void sourcesVersionReleased(@NotNull final BuildAgent agent) {
  }

  public void sourcesVersionReleased(@NotNull final BuildType configuration) {
  }

  public void sourcesVersionReleased(@NotNull final BuildType configuration, @NotNull final BuildAgent agent) {
  }

  public void labelingFailed(final SBuild build, final VcsRoot root, final Throwable exception) {
  }

  public void labelingSucceed(final SBuild build, final BuildRevision revision) {
  }

  public void buildTagsChanged(final SBuild build, final List<String> oldTags, final List<String> newTags) {
  }

  public void buildTagsChanged(final SBuild build, final User user, final List<String> oldTags, final List<String> newTags) {
  }

  public void buildCommented(final @NotNull SBuild build, final @Nullable User user, final @Nullable String comment) {
  }
}
