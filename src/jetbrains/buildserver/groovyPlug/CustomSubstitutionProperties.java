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

import jetbrains.buildServer.serverSide.ServerPaths;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * @author Yegor.Yarko
 *         Date: 18.01.2009
 */
public class CustomSubstitutionProperties extends Properties implements InitializingBean {
  @NotNull
  ServerPaths myServerPaths;
  private String myGroovyPlugScriptsDir;
  private String myGroovyPlugSubDirPropertyName;

  CustomSubstitutionProperties(@NotNull ServerPaths paths) {
    myServerPaths = paths;
  }

  public void setGroovyPlugScriptsDir(String groovyPlugSubDir) {
    myGroovyPlugScriptsDir = groovyPlugSubDir;
  }

  public void setGroovyPlugScriptsDirPropertyName(String groovyPlugSubDirPropertyName) {
    myGroovyPlugSubDirPropertyName = groovyPlugSubDirPropertyName;
  }

  public String getGroovyPlugSubDirPropertyName() {
    return myGroovyPlugSubDirPropertyName;
  }

  public void afterPropertiesSet() throws Exception {
    final File home = new File(myServerPaths.getConfigDir(), myGroovyPlugScriptsDir).getAbsoluteFile();
    final URL url = home.toURI().toURL();
    final String urlString = url.toString();
    setProperty(myGroovyPlugSubDirPropertyName, urlString);
  }
}
