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

package cppsensor.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.internal.core.index.IndexFileLocation;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public final class CppFileContentProvider extends InternalFileContentProvider {

  private static final Logger log = LoggerFactory.getLogger("FileContentProvider");

  private String encoding = StandardCharsets.UTF_8.name();

  public static boolean isCFile(final String path) {
    return path.toLowerCase().endsWith(".c");
  }

  public static IIndexFileLocation resolvePath(final String path) {
    IIndexFileLocation location = null;

    try {
      File f = new File(path);
      String canonicalPath = f.getCanonicalPath();
      location = new IndexFileLocation(
          new URI(canonicalPath), canonicalPath);
    } catch (IOException | URISyntaxException e) {
      log.error("Invalid file path", e);
    }

    return location;
  }

  @Override
  public InternalFileContent getContentForInclusion(IIndexFileLocation ifl,
      String astPath) {
    return (InternalFileContent) FileContent.createForExternalFileLocation(
        ifl.getURI().getPath(), encoding);
  }

  @Override
  public InternalFileContent getContentForInclusion(String filePath,
      IMacroDictionary macroDictionary) {
    if (!getInclusionExists(filePath)) {
      return null;
    }

    return (InternalFileContent) FileContent.createForExternalFileLocation(
        filePath, encoding);
  }

  public void setEncoding(String encoding) {
    if (encoding != null) {
      this.encoding = encoding;
    }
  }

}
