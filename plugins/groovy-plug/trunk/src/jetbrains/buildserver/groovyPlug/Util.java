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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @author Yegor.Yarko
 *         Date: 04.03.2009
 */
public class Util {
  public static void addDateTimeProperty(Map<String, String> buildParametersToAdd,
                                         Date date,
                                         String dateFormat,
                                         String systemParamName,
                                         String envParamName) {
    String formattedDate = (new SimpleDateFormat(dateFormat)).format(date);
    addProperty(buildParametersToAdd, systemParamName, envParamName, formattedDate);
  }

  public static void addProperty(@NotNull final Map<String, String> buildParametersToAdd,
                                 @Nullable final String systemParamName,
                                 @Nullable final String envParamName,
                                 @NotNull final String value) {
    if (systemParamName != null) buildParametersToAdd.put("system." + systemParamName, value);
    if (envParamName != null) buildParametersToAdd.put("env." + envParamName, value);
  }

  public static void addDateTimeProperty(Map<String, String> buildParametersToAdd,
                                         Date date,
                                         String dateFormat,
                                         String fullPropertyName) {
    String formattedDate = (new SimpleDateFormat(dateFormat)).format(date);
    buildParametersToAdd.put(fullPropertyName, formattedDate);
  }

}
