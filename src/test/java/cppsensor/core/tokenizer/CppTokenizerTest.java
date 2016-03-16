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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.parser.IToken;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cppsensor.utils.UnitTestProject;

/**
 * @author jgaston
 *
 */
public class CppTokenizerTest {

  private static class TokensConsumer implements ICppTokensConsumer {

    private Map<Integer, String> tokens = new HashMap<>();

    private Map<Integer, Integer> comments = new HashMap<>();

    private Map<Integer, Boolean> commentsType = new HashMap<>();

    private boolean endOfInputReached = false;

    @Override
    public void onToken(IToken token, int lineNb) {
      if (!tokens.containsKey(lineNb)) {
        tokens.put(lineNb, token.toString());
      }
    }

    @Override
    public void onComment(boolean isBlockComment, int offset, int length) {
      comments.put(offset, length);
      commentsType.put(offset, isBlockComment);
    }

    @Override
    public void onEndOfInput() {
      endOfInputReached = true;
    }

    boolean isEndOfInputReached() {
      return endOfInputReached;
    }

    public Map<Integer, String> getTokens() {
      return tokens;
    }

    public Map<Integer, Integer> getComments() {
      return comments;
    }

    public Map<Integer, Boolean> getCommentsType() {
      return commentsType;
    }
  }

  private static UnitTestProject MAIN_PROJECT = null;

  @BeforeClass
  public static void setup() {
    MAIN_PROJECT = new UnitTestProject("tokenizer-test-project");
  }

  @Test
  public void shouldTokenizeSource() {
    File file = new File(MAIN_PROJECT.getProjectDir(), "src/employee.cc");

    TokensConsumer consumer = new TokensConsumer();

    CppTokenizer tokenizer = new CppTokenizer(consumer);

    tokenizer.tokenize(file.getAbsolutePath());

    Assert.assertEquals("#", consumer.getTokens().get(5));
    Assert.assertEquals("printf", consumer.getTokens().get(10));
    Assert.assertEquals("LOG", consumer.getTokens().get(18));
    Assert.assertEquals("}", consumer.getTokens().get(48));

    Assert.assertTrue(consumer.isEndOfInputReached());
  }

  @Test
  public void shouldTokenizeOtherSource() {
    File file = new File(MAIN_PROJECT.getProjectDir(), "src/util.cc");

    TokensConsumer consumer = new TokensConsumer();

    CppTokenizer tokenizer = new CppTokenizer(consumer);

    tokenizer.tokenize(file.getAbsolutePath());

    Assert.assertEquals("printf", consumer.getTokens().get(8));
    Assert.assertEquals("ASSERT", consumer.getTokens().get(12));
    Assert.assertEquals("LOG", consumer.getTokens().get(13));

    Assert.assertEquals((Integer)12, consumer.getComments().get(0));
    Assert.assertFalse(consumer.getCommentsType().get(0));
    Assert.assertEquals((Integer)21, consumer.getComments().get(32));
    Assert.assertTrue(consumer.getCommentsType().get(32));
  }

}
