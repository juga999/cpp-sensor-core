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

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cppsensor.utils.UnitTestProject;

/**
 * @author jgaston
 *
 */
public class CppIndexedParserTest {

  private static CppIndexedParser PARSER = null;

  private static UnitTestProject MAIN_PROJECT = null;

  private static UnitTestProject LIB_PROJECT = null;

  @BeforeClass
  public static void setup() {
    MAIN_PROJECT = new UnitTestProject("parser-test-project");
    LIB_PROJECT = new UnitTestProject("simple-project");

    PARSER = new CppIndexedParser(
        MAIN_PROJECT.getProjectDir().getAbsolutePath(),
        new String[] {"employee/inc"});

    PARSER.init();

    CppParserInfoProvider infoProvider = new CppParserInfoProvider();
    infoProvider.setIncludePaths(LIB_PROJECT.getArrayOfIncDirs());
    PARSER.setScanInfoProvider(infoProvider);
  }

  @AfterClass
  public static void tearDown() {
    PARSER.release();
  }

  @Test
  public void shouldHandleInvalidInput() {
    IASTTranslationUnit tu = PARSER.parse("/foo/bar");
    Assert.assertNull(tu);
  }

  @Test
  public void shouldParseCFile() {
    {
      File file = new File(MAIN_PROJECT.getProjectDir(), "src/util.h");

      IASTTranslationUnit tu = PARSER.parse(file.getAbsolutePath());
      Assert.assertNotNull(tu);
    }
    {
      File file = new File(MAIN_PROJECT.getProjectDir(), "src/util.c");

      IASTTranslationUnit tu = PARSER.parse(file.getAbsolutePath());
      Assert.assertNotNull(tu);
    }
  }

  @Test
  public void shouldParseCppFile() {
    File file = new File(MAIN_PROJECT.getProjectDir(), "src/main.cc");

    IASTTranslationUnit tu = PARSER.parse(file.getAbsolutePath());
    Assert.assertNotNull(tu);
    Assert.assertTrue(PARSER.getIndexer().write(tu));

    Assert.assertFalse(PARSER.isFileIndexed(file.getAbsolutePath()));

    String[] paths = {
        "person/inc/id.h",
        "person/inc/person.h"
    };

    for (String path : paths) {
      File incFile = new File(LIB_PROJECT.getProjectDir(), path);
      Assert.assertTrue(PARSER.isFileIndexed(incFile.getAbsolutePath()));
    }

    file = new File(MAIN_PROJECT.getProjectDir(), "src/print.cc");
    PARSER.parse(file.getAbsolutePath());
    Assert.assertNotNull(tu);
    Assert.assertTrue(PARSER.getIndexer().write(tu));

  }

}
