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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.internal.core.parser.scanner.AbstractCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.FileCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.ILexerLog;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer;
import org.eclipse.cdt.internal.core.parser.scanner.LocationMap;
import org.eclipse.cdt.internal.core.parser.scanner.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public class CppTokenizer implements ILexerLog {

  private static final Logger log = LoggerFactory.getLogger("Tokenizer");

  private final ICppTokensConsumer consumer;

  private String encoding = StandardCharsets.UTF_8.name();

  public CppTokenizer(ICppTokensConsumer consumer) {
    this.consumer = consumer;
  }

  public void setEncoding(String enc) {
    encoding = enc;
  }

  public String getEncoding() {
    return encoding;
  }

  @Override
  public void handleProblem(int problemID, char[] info, int offset, int endOffset) {
    log.error("tokenizer problem: " + String.copyValueOf(info));
  }

  @Override
  public void handleComment(boolean isBlockComment, int offset, int endOffset,
      AbstractCharArray input) {
    if (consumer == null) {
      return;
    }

    consumer.onComment(isBlockComment, offset, endOffset - offset);
  }

  public void tokenize(String path) {
    if (consumer == null) {
      return;
    }

    try (FileInputStream in = new FileInputStream(path)) {
      AbstractCharArray content = FileCharArray.create(path, encoding, in);

      LocationMap locationMap = new LocationMap(new Lexer.LexerOptions());
      locationMap.pushTranslationUnit(path, content);

      Lexer lexer = new Lexer(content, new Lexer.LexerOptions(), this, null);

      int tokType = Lexer.tBEFORE_INPUT;
      while (tokType != IToken.tEND_OF_INPUT) {
        Token t = lexer.nextToken();
        tokType = t.getType();
        if (tokType != Lexer.tNEWLINE && tokType != IToken.tEND_OF_INPUT) {
          IASTFileLocation location =
              locationMap.getMappedFileLocation(t.getOffset(), t.getLength());
          if (location != null) {
            consumer.onToken(t, location.getStartingLineNumber());
          } else {
            tokType = IToken.tEND_OF_INPUT;
          }
        }
      }
    } catch(IOException | OffsetLimitReachedException e) {
      log.error("Failed to get tokens from "+path);
    }

    consumer.onEndOfInput();

  }

}
