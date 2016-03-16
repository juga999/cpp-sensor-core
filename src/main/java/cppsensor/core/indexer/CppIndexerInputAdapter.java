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

package cppsensor.core.indexer;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.internal.core.indexer.IStandaloneScannerInfoProvider;
import org.eclipse.cdt.internal.core.pdom.AbstractIndexerTask.UnusedHeaderStrategy;
import org.eclipse.cdt.internal.core.pdom.IndexerInputAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cppsensor.core.utils.CppFileContentProvider;

/**
 * @author jgaston
 *
 */
public class CppIndexerInputAdapter extends IndexerInputAdapter {

  private static final Logger log = LoggerFactory.getLogger("Indexer");

  private String encoding = StandardCharsets.UTF_8.name();

  private IStandaloneScannerInfoProvider scanInfoProvider = null;

  public void setEncoding(String enc) {
    encoding = enc;
  }

  public void setScanInfoProvider(IStandaloneScannerInfoProvider provider) {
    scanInfoProvider = provider;
  }

  @Override
  public Object getInputFile(IIndexFileLocation location) {
    return location.getFullPath();
  }

  @Override
  public long getLastModified(IIndexFileLocation location) {
    return new File(location.getFullPath()).lastModified();
  }

  @Override
  public long getFileSize(IIndexFileLocation location) {
    return new File(location.getFullPath()).length();
  }

  @Override
  public String getEncoding(IIndexFileLocation location) {
    return encoding;
  }

  @Override
  public IIndexFileLocation resolveFile(Object tu) {
    return CppFileContentProvider.resolvePath(tu.toString());
  }

  @Override
  public boolean isSourceUnit(Object tu) {
    return true;
  }

  @Override
  public boolean isFileBuildConfigured(Object tu) {
    return true;
  }

  @Override
  public boolean isIndexedOnlyIfIncluded(Object tu) {
    return false;
  }

  @Override
  public boolean isIndexedUnconditionally(IIndexFileLocation location) {
    return false;
  }

  @Override
  public boolean canBePartOfSDK(IIndexFileLocation ifl) {
    return false;
  }

  @Override
  public AbstractLanguage[] getLanguages(Object tu, UnusedHeaderStrategy strat) {
    if (CppFileContentProvider.isCFile(tu.toString())) {
      return new AbstractLanguage[] { GCCLanguage.getDefault() };
    } else {
      return new AbstractLanguage[] { GPPLanguage.getDefault() };
    }
  }

  @Override
  public IScannerInfo getBuildConfiguration(int linkageID, Object tu) {
    if (scanInfoProvider != null) {
      return scanInfoProvider.getScannerInformation(tu.toString());
    } else {
      log.error("no scan info provider !!");
      return null;
    }
  }

  @Override
  public FileContent getCodeReader(Object obj) {
    String str = obj.toString();
    String fileEncoding = getEncoding(null);
    return FileContent.createForExternalFileLocation(str, fileEncoding);
  }

  @Override
  public int getIndexingPriority(IIndexFileLocation location) {
    return 1;
  }

  @Override
  public IIndexFileLocation resolveASTPath(String astFilePath) {
    return CppFileContentProvider.resolvePath(astFilePath);
  }

  @Override
  public IIndexFileLocation resolveIncludeFile(String includePath) {
    if (doesIncludeFileExist(includePath)) {
      return CppFileContentProvider.resolvePath(includePath);
    } else {
      return null;
    }
  }

  @Override
  public boolean doesIncludeFileExist(String includePath) {
    return new File(includePath).exists();
  }

  @Override
  public String getASTPath(IIndexFileLocation ifl) {
    return ifl.getFullPath();
  }

  @Override
  public boolean isSource(String astFilePath) {
    return true;
  }

  @Override
  public long getFileSize(String astFilePath) {
    return new File(astFilePath).length();
  }

  @Override
  public boolean isCaseInsensitiveFileSystem() {
    return new File("a").equals(new File("A"));
  }

}
