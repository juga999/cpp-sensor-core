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

package cppsensor.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jgaston
 *
 */
public class CppCommonSymbols {

  public static final Map<String, String> C_SYBMOLS_DEF = new HashMap<String, String>();

  public static final Map<String, String> CPP_SYBMOLS_DEF = new HashMap<String, String>();

  static {
    C_SYBMOLS_DEF.put("_GNU_SOURCE", "1");
    C_SYBMOLS_DEF.put("__GNUC__", "4");
    C_SYBMOLS_DEF.put("__GNUC_MINOR__", "7");
    C_SYBMOLS_DEF.put("WCHAR_MIN", "INT_MIN");
    C_SYBMOLS_DEF.put("WCHAR_MAX", "INT_MAX");

    CPP_SYBMOLS_DEF.put("_GNU_SOURCE", "1");
    CPP_SYBMOLS_DEF.put("__GNUC__", "4");
    CPP_SYBMOLS_DEF.put("__GNUC_MINOR__", "7");
    CPP_SYBMOLS_DEF.put("WCHAR_MIN", "INT_MIN");
    CPP_SYBMOLS_DEF.put("WCHAR_MAX", "INT_MAX");
    CPP_SYBMOLS_DEF.put("__cplusplus", "201201L");
  }

}
