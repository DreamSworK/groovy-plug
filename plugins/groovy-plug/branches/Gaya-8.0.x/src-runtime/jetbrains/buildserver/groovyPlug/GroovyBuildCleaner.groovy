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

import jetbrains.buildServer.serverSide.DataCleaner
import java.sql.Connection
import jetbrains.buildServer.serverSide.CleanupLevel
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.SBuild
import org.jetbrains.annotations.NotNull
import jetbrains.buildServer.serverSide.SBuildServer

/**
 * @author Yegor.Yarko
 * Date: 02.09.2009
 */
class GroovyBuildCleaner implements DataCleaner {
  private static final Logger LOG = Logger.getInstance(GroovyBuildCleaner.class.getName());

  private SBuildServer myServer;

  def GroovyBuildCleaner() {
    LOG.debug("GroovyBuildCleaner initialized");
  }

  public void setServer(SBuildServer server){
    myServer = server;
  }

  void performCleanup(Connection connection, long buildId, CleanupLevel cleanupLevel) {
    try {
      LOG.debug("Perform cleanup started for buildId=" + buildId + " and cleanup level " + cleanupLevel)
      def build = myServer.findBuildInstanceById(buildId)
      if (build != null){
        LOG.info("Clean request: " + cleanupLevel + " for build " + describe(build));
      }else{
        LOG.info("Clean request: " + cleanupLevel + " for not found build with id " + buildId);
      }
      LOG.debug("Perform cleanup finished for buildId=" + buildId + " and cleanup level " + cleanupLevel)
    } catch (RuntimeException e) {
      LOG.error("Critical error during custom build cleanup", e);
    }

  }

  String describe(SBuild build) {
    String result = "";
    def buildType = build.getBuildType()
    if (buildType == null) {
      result += "unknown";
    } else {
      result += buildType.getFullName();
    }

    result += " # " + build.getBuildNumber();
    return result;
  }

}
