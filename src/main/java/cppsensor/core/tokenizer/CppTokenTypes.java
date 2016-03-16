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

package cppsensor.core.tokenizer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.parser.IToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public class CppTokenTypes {

  private static final Logger log = LoggerFactory.getLogger("Tokenizer");

  public static final Set<String> PREPROCESSOR_KEYWORDS = new HashSet<String>();

  public static final Set<String> KEYWORDS = new HashSet<String>();

  public static final Set<Integer> STRING_TOKEN_TYPES = new HashSet<Integer>();

  public static final Set<Integer> NUMBER_TOKEN_TYPES = new HashSet<Integer>();

  private static final String ECLIPSE_CDT_KEYWORDS_CLASS =
      "org.eclipse.cdt.core.parser.Keywords";

  static {
    initKeywords();
  }

  private static void initKeywords() {
    STRING_TOKEN_TYPES.add(IToken.tSTRING);
    STRING_TOKEN_TYPES.add(IToken.tLSTRING);
    STRING_TOKEN_TYPES.add(IToken.tUTF16STRING);
    STRING_TOKEN_TYPES.add(IToken.tUTF32STRING);
    STRING_TOKEN_TYPES.add(IToken.tCHAR);
    STRING_TOKEN_TYPES.add(IToken.tLCHAR);
    STRING_TOKEN_TYPES.add(IToken.tUTF16CHAR);
    STRING_TOKEN_TYPES.add(IToken.tUTF32CHAR);

    NUMBER_TOKEN_TYPES.add(IToken.tINTEGER);
    NUMBER_TOKEN_TYPES.add(IToken.tFLOATINGPT);

    PREPROCESSOR_KEYWORDS.add("include");
    PREPROCESSOR_KEYWORDS.add("if");
    PREPROCESSOR_KEYWORDS.add("ifdef");
    PREPROCESSOR_KEYWORDS.add("ifndef");
    PREPROCESSOR_KEYWORDS.add("elif");
    PREPROCESSOR_KEYWORDS.add("endif");
    PREPROCESSOR_KEYWORDS.add("define");
    PREPROCESSOR_KEYWORDS.add("undef");
    PREPROCESSOR_KEYWORDS.add("error");
    PREPROCESSOR_KEYWORDS.add("pragma");
    PREPROCESSOR_KEYWORDS.add("line");
    PREPROCESSOR_KEYWORDS.add("defined");
    PREPROCESSOR_KEYWORDS.add("_Pragma");
    PREPROCESSOR_KEYWORDS.add("__VA_ARGS__");

    try {
      Class<?> cls = Class.forName(ECLIPSE_CDT_KEYWORDS_CLASS);
      if (cls != null) {
        Field[] fields = cls.getFields();
        for (Field field : fields) {
          if (field.getType().equals(String.class)) {
            KEYWORDS.add((String)field.get(null));
          }
        }
      }
    } catch (Exception e) {
      log.error("Failed to get list of keywords", e);
    }
  }

}
