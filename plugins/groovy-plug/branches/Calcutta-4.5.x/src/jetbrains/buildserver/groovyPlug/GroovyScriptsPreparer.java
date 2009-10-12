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
import java.io.IOException;

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
      createOrUpdateFile(mySourceResourceDir, targetDir, fileName);
    }
  }

  private void updateMarkerFile(File markerFile) {
    if (!markerFile.delete()) {
      LOG.error("Cannot delete file: " + markerFile.getAbsolutePath());
    }

    try {
      markerFile.createNewFile();
    } catch (IOException e) {
      LOG.error("Cannot write file: " + markerFile.getAbsolutePath());
    }
  }

  private void createOrUpdateFile(String sourceDir, String targetDir, String fileName) {
    final String resourceFile = sourceDir + "/" + fileName;
    final File targetFile = new File(targetDir + "/" + fileName);
    final File targetOriginalFile = new File(targetDir + "/" + fileName + ".dist");
    if (targetFile.exists() && areDifferent(targetFile, targetOriginalFile)) {
      updateFile(resourceFile, targetOriginalFile);
      LOG.info("The file " + targetFile.getAbsolutePath() + " is modified since original, updated original copy only.");
      return;
    }
    LOG.info("Updating files " + targetFile.getAbsolutePath() + ", " + targetOriginalFile.getAbsolutePath()+ " from distribution.");
    updateFile(resourceFile, targetFile);
    updateFile(resourceFile, targetOriginalFile);
  }

  private boolean areDifferent(File file1, File file2) {
    return (file1.exists() != file2.exists()) ||
           ((file2.lastModified() - file1.lastModified()) >= 1000) ||
           (file1.length() != file2.length());
  }

  private void updateFile(String resourceFile, File targetFile) {
    if (targetFile.exists()) {
      if (!targetFile.delete()) {
        LOG.warn("Cannot save original file to file " + targetFile.getAbsolutePath() + " bacause the file cannot be deleted.");
        return;
      }
    }
    copyResource(resourceFile, targetFile);
  }

  private void copyResource(String resourceFile, File targetFile) {
    String operation = targetFile.exists() ? "Updated" : "Created";
    FileUtil.copyResource(getClass(), resourceFile, targetFile);
    if (targetFile.exists()) {
      LOG.info(operation + " file: " + targetFile.getAbsolutePath());
    } else {
      LOG.warn("A required file is missing: " + targetFile.getAbsolutePath() + ". Probably missing resource:" + resourceFile);
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