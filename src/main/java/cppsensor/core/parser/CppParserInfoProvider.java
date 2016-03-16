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

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.parser.ExtendedScannerInfo;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.internal.core.indexer.IStandaloneScannerInfoProvider;

import cppsensor.core.common.CppCommonSymbols;

/**
 * @author jgaston
 *
 */
public class CppParserInfoProvider implements IStandaloneScannerInfoProvider {

  private String[] includePaths = new String[] {};

  private String[] builtinIncFiles = new String[] {};

  private IScannerInfo scanInfoForC = null;

  private IScannerInfo scanInfoForCPP = null;

  @Override
  public IScannerInfo getScannerInformation(String path) {
    if (path.endsWith(".c")) {
      return getScannerInfoForC();
    } else {
      return getScannerInfoForCPP();
    }
  }

  @Override
  public IScannerInfo getDefaultScannerInformation(int linkageID) {
    if (linkageID == ILinkage.C_LINKAGE_ID) {
      return getScannerInfoForC();
    } else {
      return getScannerInfoForCPP();
    }
  }

  public void setIncludePaths(String[] paths) {
    if (paths != null) {
      includePaths = paths;
    }
  }

  public void setBuiltinIncFiles(String[] files) {
    if (files != null) {
      builtinIncFiles = files;
    }
  }

  private IScannerInfo getScannerInfoForC() {
    if (scanInfoForC == null) {
      scanInfoForC = new ExtendedScannerInfo(
          CppCommonSymbols.C_SYBMOLS_DEF, includePaths,
          new String[] {}, builtinIncFiles);
    }

    return scanInfoForC;
  }

  private IScannerInfo getScannerInfoForCPP() {
    if (scanInfoForCPP == null) {
      scanInfoForCPP = new ExtendedScannerInfo(
          CppCommonSymbols.CPP_SYBMOLS_DEF, includePaths,
          new String[] {}, builtinIncFiles);
    }

    return scanInfoForCPP;
  }
}
