/*
 * Copyright 2000-2010 JetBrains s.r.o.
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

import jetbrains.buildServer.notification.NotificationContext
import jetbrains.buildServer.notification.TemplateProcessor
import jetbrains.buildServer.serverSide.WebLinks
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Yegor.Yarko
 * Date: 11.04.2010
 */
class GroovyTemplateProcessor implements TemplateProcessor{

  @Autowired
  public WebLinks webLinks;

/**
 * Adds groovyPlug.message and groovyPlug.helpUrl variables that can be used in any notification templates.
 * (See http://confluence.jetbrains.net/display/TCD5/Customizing+Notifications+(in+TeamCity+5.1) ) 
 * @param context
 * @return
 */
  Map<String, Object> fillModel(NotificationContext context) {
    Map<String, Object> properties = new HashMap<String, Object>();

    properties.put("message", "My custom message");
    properties.put("helpUrl", webLinks.getHelp(""));

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("groovyPlug", properties);
    return result;
  }
}
