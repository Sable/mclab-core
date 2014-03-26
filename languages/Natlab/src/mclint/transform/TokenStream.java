package mclint.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import matlab.MatlabLexer;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import ast.ASTNode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

// A stream of tokens returned by a lexer, with operations for manipulating it
class TokenStream {
  private List<Token> tokens;
  // Maps <line, column> to index into tokens
  private Table<Integer, Integer, Integer> tokensByPosition;
  
  public static TokenStream create(String code) {
    return new TokenStream(tokenize(code)).index();
  }
  
  public TokenStream index() {
    tokensByPosition = indexByPosition(tokens);
    return this;
  }
  
  public String asString() {
    return Joiner.on("").join(Iterables.transform(tokens, GET_TEXT));
  }
  
  private static boolean isSynthetic(ASTNode<?> node) {
    return node.getStartLine() == 0;
  }

  public List<Token> getTokensForAstNode(ASTNode<?> node) {
    if (isSynthetic(node)) {
      return synthesizeNewTokens(node);
    }
    int startIndex = leadingWhitespace(startIndex(node));
    int endIndex = endIndex(node);
    // If this is the last, but not the only, statement on the line 
    if (tokens.get(startIndex).getCharPositionInLine() != 0 &&
        tokens.get(endIndex).getText().equals("\n")) {
      endIndex = trailingWhitespace(endIndex);
    }
    return tokens.subList(startIndex, endIndex + 1);
  }
  
  private int leadingWhitespace(int start) {
    return lastMatchingIndex(newlineOrText, start - 1) + 1;
  }
  
  private int trailingWhitespace(int start) {
    return firstMatchingIndex(newlineOrText, start) - 1;
  }
  
  private Predicate<Token> newlineOrText = Predicates.or(
      hasText("\n"), 
      Predicates.not(hasText("[ \\t]")));
  
  private int lastMatchingIndex(Predicate<Token> predicate, int start) {
    for (int i = start; i >= 0; i--) {
      if (predicate.apply(tokens.get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  private int firstMatchingIndex(Predicate<Token> predicate, int start) {
    for (int i = start; i < tokens.size(); ++i) {
      if (predicate.apply(tokens.get(i))) {
        return i;
      }
    }
    return -1;
  }

  // Tokens are from ANTLR, where columns are 0-based, and lines are 1-based.
  // But the AST is from Beaver, where they are both 1-based.
  // This is why we subtract 1 from the column.
  private int startIndex(ASTNode<?> node) {
    return tokensByPosition.get(node.getStartLine(), node.getStartColumn() - 1);
  }
  
  private int endIndex(ASTNode<?> node) {
    return tokensByPosition.get(node.getEndLine(), node.getEndColumn() - 1);
  }

  
  private List<Token> synthesizeNewTokens(ASTNode<?> node) {
    List<Token> tokens = tokenize(node.getPrettyPrinted());
    // TODO(isbadawi): This looks out of place 
    tokens.add(new ClassicToken(1, "\n"));
    return tokens;
  }
  
  private static Function<Token, String> GET_TEXT = new Function<Token, String>() {
    @Override public String apply(Token token) {
      return token.getText();
    }
  };
  
  private static Predicate<Token> hasText(String text) {
    return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
  }

  private TokenStream(List<Token> tokens) {
    this.tokens = tokens;
  }

  private static Table<Integer, Integer, Integer> indexByPosition(List<Token> tokens) {
    Table<Integer, Integer, Integer> table = HashBasedTable.create();
    int index = 0;
    for (Token token : tokens) {
      int startColumn = token.getCharPositionInLine();
      int endColumn = startColumn + token.getText().length() - 1;
      table.put(token.getLine(), startColumn, index);
      table.put(token.getLine(), endColumn, index);
      index++;
    }
    return table;
  }
  
  private static List<Token> tokenize(String code) {
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
