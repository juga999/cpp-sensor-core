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

import java.nio.charset.StandardCharsets;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.internal.core.indexer.IStandaloneScannerInfoProvider;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cppsensor.core.utils.CppFileContentProvider;

/**
 * @author jgaston
 *
 */
public class CppParser {

  private static final Logger log = LoggerFactory.getLogger("Parser");

  private final IParserLogService logger = new CppParserLogService();

  private String encoding = StandardCharsets.UTF_8.name();

  private IStandaloneScannerInfoProvider scanInfoProvider = null;

  private IncludeFileContentProvider fileContentProvider = null;

  public void setScanInfoProvider(IStandaloneScannerInfoProvider provider) {
    scanInfoProvider = provider;
  }

  public void setEncoding(String enc) {
    encoding = enc;
  }

  public IASTTranslationUnit parse(final String path) {
    if (!checkScanInfoProvider()) {
      return null;
    }

    try {
      return getTranslationUnit(path, getFileContentProvider(), null);
    } catch (Exception e) {
      log.error("Failed to parse file "+path, e);
      return null;
    }
  }

  protected IASTTranslationUnit getTranslationUnit(final String path,
      IncludeFileContentProvider fileContentProvider, IIndex index) throws Exception {

    if (path == null) {
      return null;
    }

    final FileContent content = FileContent.createForExternalFileLocation(
        path, getEncoding());
    if (content == null) {
      log.error("Could not get content of file "+path);
      return null;
    }

    AbstractCLikeLanguage language;
    if (CppFileContentProvider.isCFile(path)) {
      language = GCCLanguage.getDefault();
    } else {
      language = GPPLanguage.getDefault();
    }

    try {
      return language.getASTTranslationUnit(content,
          getScanInfoProvider().getScannerInformation(path),
          fileContentProvider, index, getLangOptions(path), logger);
    } catch (CoreException e) {
      log.error("Failed to parse file "+path, e);
      return null;
    }
  }

  protected boolean checkScanInfoProvider() {
    if (getScanInfoProvider() == null) {
      log.error("Parser not correctly initialized. Missing scan info provider.");
      return false;
    } else {
      return true;
    }
  }

  protected IStandaloneScannerInfoProvider getScanInfoProvider() {
    return scanInfoProvider;
  }

  protected String getEncoding() {
    return encoding;
  }

  protected IncludeFileContentProvider getFileContentProvider() {
    if (fileContentProvider == null) {
      fileContentProvider = new CppFileContentProvider();
    }
    return fileContentProvider;
  }

  private int getLangOptions(String path) {
    int options =
        ILanguage.OPTION_NO_IMAGE_LOCATIONS |
        ILanguage.OPTION_PARSE_INACTIVE_CODE;

    if (isSourceFile(path)) {
      options |= ILanguage.OPTION_IS_SOURCE_UNIT;
    }

    return options;
  }

  private boolean isSourceFile(String path) {
    int dotPos = path.lastIndexOf(".");
    return (
        dotPos > 0 &&
        path.substring(dotPos).toLowerCase().startsWith(".c"));
  }

}
