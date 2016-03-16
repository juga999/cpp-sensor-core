/**
 * Copyright (C) 2016 Julien Gaston
 * cpp-sensor@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package cppsensor.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public class UnitTestProject {

  private static final Logger log = LoggerFactory.getLogger(UnitTestProject.class);

  private File projectDir = null;

  private Collection<File> projectFiles = null;

  private Set<String> includeDirs = new HashSet<String>();

  public static File getFile(String relPath) {
    URL url = UnitTestProject.class.getResource(
        File.separator + relPath);
    File file = null;
    try {
      file = new File(url.toURI());
    } catch (URISyntaxException e) {
      log.error("Failed to locate resource "+url, e);
    }

    return file;
  }

  public UnitTestProject(String relPath) {
    projectDir = getFile(relPath);

    Assert.assertNotNull(projectDir);

    projectFiles = FileUtils.listFiles(
        projectDir, new UnitTestIOFilter(includeDirs), TrueFileFilter.INSTANCE);
  }

  public File getProjectDir() {
    return projectDir;
  }

  public Collection<File> getProjectFiles() {
    return projectFiles;
  }

  public Set<String> getIncDirs() {
    return includeDirs;
  }

  public String[] getArrayOfIncDirs() {
    String incDirs[] = new String[includeDirs.size()];
    includeDirs.toArray(incDirs);
    return incDirs;
  }

  public void addIncDir(String dir) {
    includeDirs.add(dir);
  }

}
