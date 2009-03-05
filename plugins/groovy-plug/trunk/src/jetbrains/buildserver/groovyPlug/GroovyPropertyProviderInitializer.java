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

import jetbrains.buildServer.serverSide.ParametersPreprocessor;
import jetbrains.buildServer.serverSide.ServerExtensionHolder;
import jetbrains.buildServer.serverSide.parameters.BuildParameterReferencesProvider;

/**
 * @author Yegor.Yarko
 *         Date: 16.01.2009
 */

public class GroovyPropertyProviderInitializer {
  ParametersPreprocessor myPropertyProvider;
  BuildParameterReferencesProvider myReferencePropertyProvider;
  private ServerExtensionHolder myExtensionsHolder;

  public GroovyPropertyProviderInitializer(ServerExtensionHolder extensionHolder) {
    this.myExtensionsHolder = extensionHolder;
  }

  public void setPropertyProvider(ParametersPreprocessor myPropertyProvider) {
    this.myPropertyProvider = myPropertyProvider;
  }

  public void setReferencePropertyProvider(BuildParameterReferencesProvider referencePropertyProvider) {
    this.myReferencePropertyProvider = referencePropertyProvider;
  }

  public void init(){
    myExtensionsHolder.registerExtension(ParametersPreprocessor.class, "myPropertyProvider", myPropertyProvider);
    myExtensionsHolder.registerExtension(BuildParameterReferencesProvider.class, "myReferencePropertyProvider", myReferencePropertyProvider);
  }
}
