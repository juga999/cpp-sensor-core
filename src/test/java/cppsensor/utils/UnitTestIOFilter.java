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
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public class UnitTestIOFilter implements IOFileFilter {

  private static final Logger log = LoggerFactory.getLogger(UnitTestIOFilter.class);

  private final Set<String> m_incDirs;

  public UnitTestIOFilter(Set<String> incDirs) {
    m_incDirs = incDirs;
  }

  @Override
  public boolean accept(File dir, String name) {
    return accept(new File(dir, name));
  }

  @Override
  public boolean accept(File file) {
    if (!file.isFile()) {
      return false;
    }

    String path = "";
    try {
      path = file.getCanonicalPath();
    } catch (IOException e) {
      log.error("Failed to get canonical path for file "+file, e);
      return false;
    }
    if (path.endsWith(".h")) {
      m_incDirs.add(file.getParent());
      return true;
    } else if (path.endsWith(".cc") || path.endsWith(".c")) {
      return true;
    } else {
      return false;
    }
  }

}
