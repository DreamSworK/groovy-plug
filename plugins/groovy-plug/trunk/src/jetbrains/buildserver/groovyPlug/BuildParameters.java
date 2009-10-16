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
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yegor.Yarko
 *         Date: 15.10.2009
 */
public class BuildParameters {
  private static final Logger LOG = Logger.getInstance(BuildParameters.class.getName());

  private Map<String, String> myParameters;

  public BuildParameters() {
    myParameters = new HashMap<String, String>();
  }

  public void addSystem(@NotNull final String name, @NotNull final String value) {
    LOG.debug("Adding system property '" + name + "' with value =" + value);
    myParameters.put("system." + name, value);
  }

  public void addEnvironment(@NotNull final String name, @NotNull final String value) {
    LOG.debug("Adding environment variable '" + name + "' with value =" + value);
    String convertedName = jetbrains.buildServer.util.StringUtil.replaceNonAlphaNumericChars(name.toUpperCase(), '_');
    myParameters.put("env." + convertedName, value);
  }

  public void addConfiguration(@NotNull final String name, @NotNull final String value) {
    LOG.debug("Adding configuration parameter '" + name + "' with value =" + value);
    myParameters.put(name, value);
  }

  public void addEnvAndSystem(@NotNull final String name, @NotNull final String value) {
    addSystem(name, value);
    addEnvironment(name, value);
  }

  public Map<String, String> getParameters() {
    return new HashMap<String, String>(myParameters);
  }
}
