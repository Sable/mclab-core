package mclint.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import matlab.MatlabLexer;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Token;

import com.google.common.collect.Lists;

public class TokenUtil {
  public static List<Token> tokenize(String code) {
    List<Token> tokens = Lists.newArrayList();
    MatlabLexer lexer = new MatlabLexer(getAntlrStream(code));
    Token token = lexer.nextToken();
    while (token != Token.EOF_TOKEN) {
      tokens.add(token);
      token = lexer.nextToken();
    }
    return tokens;
  }

  private static ANTLRReaderStream getAntlrStream(String code) {
    try {
      return new ANTLRReaderStream(new StringReader(code));
    } catch (IOException e) {
      // Will never happen, since it's a StringReader...
      return null;
    }
  }
}
