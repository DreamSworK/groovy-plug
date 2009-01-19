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
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

/**
 * @author Yegor.Yarko
 *         Date: 19.01.2009
 */
public class GroovyScriptsPreparer implements InitializingBean {
  private static final Logger LOG = Logger.getInstance(GroovyScriptsPreparer.class.getName());

  private String mySourceResourceDir;
  private String myTargetDir;
  private String mySourceFileName;
  private String myTargetFileName;

  public void afterPropertiesSet() throws Exception {
    final File targetFile = new File(myTargetDir, myTargetFileName);
    if (!targetFile.exists()) {
      new File(myTargetDir).mkdirs();
      FileUtil.copyResource(getClass(), mySourceResourceDir + "/" + mySourceFileName, targetFile);
      LOG.info("Created default script: " + targetFile.getAbsolutePath());
    }
  }

  public void setMySourceResourceDir(String mySourceFileInResources) {
    this.mySourceResourceDir = mySourceFileInResources;
  }

  public String getMySourceResourceDir() {
    return mySourceResourceDir;
  }

  public void setMyTargetDir(String myTargetDir) {
    this.myTargetDir = myTargetDir;
  }

  public String getMyTargetDir() {
    return myTargetDir;
  }

  public void setMySourceFileName(String mySourceFileName) {
    this.mySourceFileName = mySourceFileName;
  }

  public String getMySourceFileName() {
    return mySourceFileName;
  }

  public void setMyTargetFileName(String myTargetFileName) {
    this.myTargetFileName = myTargetFileName;
  }

  public String getMyTargetFileName() {
    return myTargetFileName;
  }
}