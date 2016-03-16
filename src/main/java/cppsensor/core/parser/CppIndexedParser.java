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

package cppsensor.core.parser;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.internal.core.indexer.IStandaloneScannerInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cppsensor.core.indexer.CppIndexer;
import cppsensor.core.indexer.CppIndexerInputAdapter;
import cppsensor.core.indexer.ICppIndexReader;
import cppsensor.core.utils.CppFileContentProvider;

/**
 * @author jgaston
 *
 */
public class CppIndexedParser extends CppParser {

  private static final Logger log = LoggerFactory.getLogger("Parser");

  private final CppIndexerInputAdapter indexerInputAdapter = new CppIndexerInputAdapter();

  private CppIndexer indexer = null;

  private boolean isReady = false;

  private TranslationUnitReader tuReader = new TranslationUnitReader();

  private IndexedFileChecker indexedFileChecker = new IndexedFileChecker();

  public CppIndexedParser(String rootPath) {
    this(rootPath, null);
  }

  public CppIndexedParser(String rootPath, String[] excludedPaths) {
    indexer = new CppIndexer(indexerInputAdapter, rootPath, excludedPaths);
  }

  @Override
  public void setEncoding(String enc) {
    super.setEncoding(enc);
    indexerInputAdapter.setEncoding(enc);
  }

  @Override
  public void setScanInfoProvider(IStandaloneScannerInfoProvider provider) {
    super.setScanInfoProvider(provider);
    indexerInputAdapter.setScanInfoProvider(provider);
  }

  public boolean isReady() {
    return isReady;
  }

  public boolean init() {
    if (isReady()) {
      log.warn("Parser already initialized!");
      return true;
    }

    isReady = indexer.open();

    return isReady();
  }

  public void release() {
    if (!isReady()) {
      return;
    }

    indexer.close();

    setScanInfoProvider(null);

    isReady = false;
  }

  @Override
  public IASTTranslationUnit parse(final String path) {
    if (!checkState()) {
      return null;
    }

    if (!checkScanInfoProvider()) {
      return null;
    }

    return indexer.executeLocked(tuReader, path);
  }

  public Boolean isFileIndexed(final String path) {
    if (!checkState()) {
      return null;
    }

    return indexer.executeLocked(indexedFileChecker, path);
  }

  public CppIndexer getIndexer() {
    return indexer;
  }

  private boolean checkState() {
    if (!isReady()) {
      log.error("Parser not initialized");
      return false;
    } else {
      return true;
    }
  }

  private class TranslationUnitReader implements ICppIndexReader<IASTTranslationUnit> {

    @Override
    public IASTTranslationUnit read(IIndex index, Object... args)
        throws Exception {

      final String path = (String)args[0];

      IncludeFileContentProvider indexContentProvider;

      if (CppFileContentProvider.isCFile(path)) {
        indexContentProvider = indexer.getCContentProvider();
      } else {
        indexContentProvider = indexer.getCPPContentProvider();
      }

      return getTranslationUnit(path, indexContentProvider, index);
    }

  }

  private class IndexedFileChecker implements ICppIndexReader<Boolean> {

    @Override
    public Boolean read(IIndex index, Object... args) throws Exception {

      final String path = (String)args[0];

      IIndexFile[] files = index.getFiles(CppFileContentProvider.resolvePath(path));

      return new Boolean(files != null && files.length > 0);

    }

  }

}
