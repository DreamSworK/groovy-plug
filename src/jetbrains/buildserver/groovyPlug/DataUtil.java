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

import jetbrains.buildServer.vcs.SVcsRoot;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.VcsRootEntry;
import jetbrains.buildServer.vcs.VcsModificationHistory;
import jetbrains.buildServer.serverSide.SBuild;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Yegor.Yarko
 *         Date: 19.06.2009
 */
public class DataUtil {
  private static final Logger LOG = Logger.getInstance(GroovyReferencePropertiesProvider.class.getName());

  VcsModificationHistory vcsModificationHistory;

  @Nullable
  SVcsModification getLastModification(SBuild build) {
    Long modificationId = build.getBuildPromotion().getLastModificationId();
    if (modificationId == null || modificationId == -1) {
      return null;
    }
    return vcsModificationHistory.findChangeById(modificationId);
  }

  @NotNull
  Map<SVcsRoot, String> getLastModificationsRevisions(SBuild build) {
    final List<VcsRootEntry> rootEntries = build.getVcsRootEntries();
    final Map<SVcsRoot, String> rootVersions = new HashMap<SVcsRoot, String>();

    List<SVcsModification> modifications = vcsModificationHistory.getAllModifications(build.getBuildType());
    int toFill = rootEntries.size();
    SVcsModification lastModification = getLastModification(build);
    if (lastModification != null) {
      LOG.debug("Searching for last included revisions. Root entries number: " + toFill + ",  last modification: " + lastModification +
                ", total modifications: " + modifications.size());
      for (SVcsModification modification : modifications) {
        if (modification.compareTo(lastModification) <= 0) {
          String version = modification.getDisplayVersion();
          if (rootVersions.get(modification.getVcsRoot()) == null && version != null) {
            rootVersions.put(modification.getVcsRoot(), version);
            --toFill;
          }
          if (toFill == 0) {
            break;
          }
        }
      }
    } else {
      LOG.debug("No modification is associated with the build " + build + ", no VCS versions are added");
    }

    return rootVersions;
  }

  public void setVcsModificationHistory(VcsModificationHistory vcsModificationHistory) {
    this.vcsModificationHistory = vcsModificationHistory;
  }

  public VcsModificationHistory getVcsModificationHistory() {
    return vcsModificationHistory;
  }
}
