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

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.parser.AbstractParserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jgaston
 *
 */
public final class CppParserLogService extends AbstractParserLogService {

  private static final Logger log = LoggerFactory.getLogger("Parser");

  private static final String TRACE_NO_GUARD =
      CCorePlugin.PLUGIN_ID + "/debug/scanner/missingIncludeGuards";

  @Override
  public boolean isTracing(String traceOption) {
    if (TRACE_NO_GUARD.compareTo(traceOption) == 0) {
      return false;
    } else {
      return isTracing();
    }
  }

  @Override
  public boolean isTracing() {
    return true;
  }

  @Override
  public boolean isTracingExceptions() {
    return true;
  }

  @Override
  public void traceLog(String msg) {
    log.trace(msg);
  }

  @Override
  public void errorLog(String message) {
    log.error(message);
  }

}
