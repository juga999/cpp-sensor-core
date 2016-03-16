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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.URIRelativeLocationConverter;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.internal.core.index.FileContentKey;
import org.eclipse.cdt.internal.core.index.IIndexFragmentFile;
import org.eclipse.cdt.internal.core.index.IWritableIndex;
import org.eclipse.cdt.internal.core.index.IndexBasedFileContentProvider;
import org.eclipse.cdt.internal.core.index.WritableCIndex;
import org.eclipse.cdt.internal.core.pdom.IndexerInputAdapter;
import org.eclipse.cdt.internal.core.pdom.PDOMWriter;
import org.eclipse.cdt.internal.core.pdom.WritablePDOM;
import org.eclipse.cdt.internal.core.pdom.dom.IPDOMLinkageFactory;
import org.eclipse.cdt.internal.core.pdom.dom.c.PDOMCLinkageFactory;
import org.eclipse.cdt.internal.core.pdom.dom.cpp.PDOMCPPLinkageFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cppsensor.core.utils.CppFileContentProvider;

/**
 * @author jgaston
 *
 */
public class CppIndexer extends PDOMWriter implements AutoCloseable {

  private static final Logger log = LoggerFactory.getLogger("Indexer");

  private static final String INDEX_TMP_FILE_PREFIX = CppIndexer.class.getName();

  private static final String INDEX_TMP_FILE_SUFFIX = ".pdom";

  private final String rootPath;

  private final IProgressMonitor nullMonitor = new NullProgressMonitor();

  private final IncludeFileContentProvider fileContentProvider =
      new CppFileContentProvider();

  private File pdomFile = null;

  private WritablePDOM pdom = null;

  private IWritableIndex writableIndex = null;

  private Pattern[] excludedPathsPatterns = null;

  private boolean isReady = false;

  private IncludeFileContentProvider indexBasedCContentProvider = null;

  private IncludeFileContentProvider indexBasedCPPContentProvider = null;

  private boolean verbose = false;

  public CppIndexer(IndexerInputAdapter indexInputAdapter,
      String rootPath,  String[] excludedPaths) {
    super(indexInputAdapter);
    this.rootPath = rootPath;
    setShowProblems(true);
    setShowActivity(true);

    if (excludedPaths != null && excludedPaths.length > 0) {
      excludedPathsPatterns = new Pattern[excludedPaths.length];
      for (int i = 0; i < excludedPaths.length; ++i) {
        excludedPathsPatterns[i] = Pattern.compile(
            Pattern.quote(excludedPaths[i]), Pattern.CASE_INSENSITIVE);
      }
    }

  }

  public boolean open() {
    CppDefaultRegistry.init();

    URIRelativeLocationConverter indexLocationConverter;
    try {
      indexLocationConverter = new URIRelativeLocationConverter(new URI("/"));
    } catch (URISyntaxException e) {
      log.error("Invalid URI used to initialize the writable PDOM", e);
      return false;
    }

    try {
      pdomFile = File.createTempFile(INDEX_TMP_FILE_PREFIX, INDEX_TMP_FILE_SUFFIX);
      pdomFile.deleteOnExit();
    } catch (IOException e) {
      log.error("Failed to create the temporary index file.", e);
      pdomFile = null;
      return false;
    }

    Map<String, IPDOMLinkageFactory> linkageFactoryMappings = new HashMap<>();
    linkageFactoryMappings.put(ILinkage.C_LINKAGE_NAME, new PDOMCLinkageFactory());
    linkageFactoryMappings.put(ILinkage.CPP_LINKAGE_NAME, new PDOMCPPLinkageFactory());

    try {
      pdom = new WritablePDOM(pdomFile, indexLocationConverter, linkageFactoryMappings);
      writableIndex = new WritableCIndex(pdom);
      isReady = true;
    } catch (CoreException e) {
      log.error("Failed to create the writable PDOM instance", e);
    }

    return isReady;
  }

  @Override
  public void close() {
    if (isReady) {
      try {
        pdom.acquireWriteLock(nullMonitor);
        pdom.close();
        pdom.releaseWriteLock();
      } catch (CoreException | InterruptedException e) {
        log.trace("Failed to cleanly close the indexer data writer", e);
      } finally {
        pdom = null;
      }
      writableIndex = null;
      pdomFile = null;

      isReady = false;
    }

    CppDefaultRegistry.release();
  }

  public boolean write(IASTTranslationUnit tu) {
    if (!isReady) {
      return false;
    }

    boolean resultCacheCleared = false;

    try {
      List<FileInAST> files = new ArrayList<>();

      collectIncludedFiles(tu, files);

      Data data = new PDOMWriter.Data(
          tu, files.toArray(new FileInAST[0]), writableIndex);

      int storageLinkageID = process(tu, data);
      if (storageLinkageID != ILinkage.NO_LINKAGE_ID) {
        addSymbols(data, storageLinkageID, null, nullMonitor);
        pdom.acquireWriteLock(nullMonitor);
        writableIndex.flush();
        pdom.releaseWriteLock();
        resultCacheCleared = true;  // The cache was cleared while writing to the index.
      }
    } catch (CoreException | InterruptedException e) {
      log.error("Failed to write symbols from "+tu.getFilePath(), e);
    }

    if (!resultCacheCleared) {
      // If the result cache has not been cleared,
      // clear it under a write lock to reduce interference with index readers.
      clearResultCache();
    }

    return resultCacheCleared;
  }

  public <T> T executeLocked(ICppIndexReader<T> op, Object...args) {
    if (op == null) {
      return null;
    }

    try {
      pdom.acquireReadLock();
    } catch (InterruptedException e) {
      log.error("Failed to lock the index", e);
      return null;
    }

    T result = null;

    try {
      result = op.read(writableIndex, args);
    } catch (Exception e) {
      log.error("Error during read operation", e);
    } finally {
      pdom.releaseReadLock();
    }

    return result;
  }

  public IncludeFileContentProvider getCContentProvider() {
    if (isReady) {
      if (indexBasedCContentProvider == null) {
        indexBasedCContentProvider = new IndexBasedFileContentProvider(
            writableIndex, getInputAdapter(), ILinkage.C_LINKAGE_ID,
            fileContentProvider);
      }
      return indexBasedCContentProvider;
    } else {
      return null;
    }
  }

  public IncludeFileContentProvider getCPPContentProvider() {
    if (isReady) {
      if (indexBasedCPPContentProvider == null) {
        indexBasedCPPContentProvider = new IndexBasedFileContentProvider(
            writableIndex, getInputAdapter(), ILinkage.CPP_LINKAGE_ID,
            fileContentProvider);
      }
      return indexBasedCPPContentProvider;
    } else {
      return null;
    }
  }

  @Override
  protected void reportFileWrittenToIndex(FileInAST file,
      IIndexFragmentFile iFile) throws CoreException {
    if (verbose) {
      log.info("Written "+file.toString() + " to index");
    } else {
      log.trace("Written "+file.toString() + " to index");
    }
  }

  @Override
  protected void reportException(Throwable th) {
    log.error("Indexer exception", th);
  }

  @Override
  protected void trace(String message) {
    if (verbose) {
      log.info(message);
    } else {
      log.trace(message);
    }
  }

  private void collectIncludedFiles(IASTTranslationUnit tu, List<FileInAST> files)
      throws InterruptedException, CoreException {

    pdom.acquireReadLock();
    try {
      Set<FileContentKey> enteredFiles= new HashSet<>();

      int linkageId = tu.getLinkage().getLinkageID();
      IASTInclusionNode[] inclusions = tu.getDependencyTree().getInclusions();
      for (IASTInclusionNode inclusion : inclusions) {
        collectOrderedFileKeys(inclusion, linkageId, enteredFiles, files);
      }
    } catch(CoreException e) {
      throw e;
    } finally {
      pdom.releaseReadLock();
    }

  }

  private void collectOrderedFileKeys(IASTInclusionNode inclusion, int linkageId,
      Set<FileContentKey> enteredFiles, List<FileInAST> orderedFileKeys)
          throws CoreException {

    IASTPreprocessorIncludeStatement include = inclusion.getIncludeDirective();
    if (include == null || !include.createsAST()) {
      return;
    }

    IIndexFileLocation ifl= CppFileContentProvider.resolvePath(include.getPath());
    if (ifl == null) {
      return;
    }

    FileContentKey fileKey = new FileContentKey(linkageId, ifl, include.getSignificantMacros());
    boolean isFirstEntry = enteredFiles.add(fileKey);

    for (IASTInclusionNode element : inclusion.getNestedInclusions()) {
      collectOrderedFileKeys(element, linkageId, enteredFiles, orderedFileKeys);
    }

    if (isIndexable(ifl.getFullPath())) {
      IIndexFragmentFile fileInIndex = writableIndex.getWritableFile(
          linkageId, ifl, include.getSignificantMacros());
      if (isFirstEntry && fileInIndex == null) {
        orderedFileKeys.add(new FileInAST(include, fileKey));
      }
    }

  }

  private void clearResultCache() {
    try {
      writableIndex.acquireWriteLock(nullMonitor);
      writableIndex.clearResultCache();
    } catch(InterruptedException e) {
      log.error("Failed to clear result cache after index upate", e);
    } finally {
      writableIndex.releaseWriteLock();
    }
  }

  private boolean isIndexable(String path) {
    // Do not store in the index files within a main project
    if (rootPath != null && path.startsWith(rootPath)) {
      return false;
    }

    if (excludedPathsPatterns != null) {
      for (Pattern excludedPathPattern : excludedPathsPatterns) {
        if (excludedPathPattern.matcher(path).find()) {
          return false;
        }
      }
    }

    return true;
  }

}
