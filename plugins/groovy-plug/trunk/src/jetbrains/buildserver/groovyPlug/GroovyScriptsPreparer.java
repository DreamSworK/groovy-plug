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
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.serverSide.ServerPaths;

import java.io.File;

/**
 * @author Yegor.Yarko
 *         Date: 19.01.2009
 */
public class GroovyScriptsPreparer {
  private static final Logger LOG = Logger.getInstance(GroovyScriptsPreparer.class.getName());

  private ServerPaths myServerPaths;
  private String mySourceResourceDir;
  private String myTargetDirUnderConfig;
  private String[] myFileNames;

  public GroovyScriptsPreparer(ServerPaths serverPaths) {
    myServerPaths = serverPaths;
  }

  public void init() {
    final String targetDir = new File(myServerPaths.getConfigDir(), myTargetDirUnderConfig).getAbsolutePath();
    new File(targetDir).mkdirs();
    for (String fileName : myFileNames) {
      copyResource(mySourceResourceDir, targetDir, fileName);
    }
  }

  private void copyResource(String sourceDir, String targetDir, String fileName) {
    final File targetFile = new File(targetDir, fileName);
    if (!targetFile.exists()) {
      final String sourceFile = sourceDir + "/" + fileName;
      FileUtil.copyResource(getClass(), sourceFile, targetFile);
      if (targetFile.exists()) {
        LOG.info("Created default script: " + targetFile.getAbsolutePath());
      } else {
        LOG.warn("A required file is minning: " + targetFile.getAbsolutePath() + ". Probably missing resourse:" + sourceFile);
      }
    }
  }

  public void setSourceResourceDir(String mySourceFileInResources) {
    this.mySourceResourceDir = mySourceFileInResources;
  }

  public void setTargetDirUnderConfig(String myTargetDirUnderConfig) {
    this.myTargetDirUnderConfig = myTargetDirUnderConfig;
  }

  public void setFileNames(String[] fileNames) {
    this.myFileNames = fileNames;
  }
}