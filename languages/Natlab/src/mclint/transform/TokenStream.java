package mclint.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import matlab.MatlabLexer;

import org.antlr.runtime.ANTLRReaderStream;
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

public class TokenStream implements Iterable<TokenStreamFragment.Node> {
  private TokenStreamFragment stream;
  private Table<Integer, Integer, TokenStreamFragment.Node> byPosition;

  public static TokenStream create(String code) {
    return new TokenStream(TokenStreamFragment.createTopLevel(tokenize(code))).index();
  }
  
  @Override public Iterator<TokenStreamFragment.Node> iterator() {
    return TokenStreamFragment.create(stream.getStart().getNext(), stream.getEnd().getPrevious()).iterator();
  }

  public String asString() {
    return Joiner.on("").join(Iterables.transform(this, GET_TEXT));
  }

  private static boolean isSynthetic(ASTNode<?> node) {
    return node.getStartLine() == 0 && !(node instanceof ast.List);
  }

  public void removeAstNode(ASTNode<?> node) {
    fragment(node).remove();
  }
  
  public void insertAstNode(ASTNode<?> node, ASTNode<?> newNode, int i) {
    TokenStreamFragment oldFragment = fragment(node);
    TokenStreamFragment newFragment;
    if (isSynthetic(newNode)) {
      newFragment = synthesizeNewTokens(newNode);
    } else {
      newFragment = fragment(newNode).copy();
      newNode.setTokenStreamFragment(newFragment);
    }

    if (node.getNumChild() == 0) {
      // TODO(isbadawi): This is brittle, assumes statements lists I think
      newFragment.spliceAfter(
          TokenStreamFragment.single(nextMatchingNode(hasText("\n"), oldFragment.getStart())));
    } else if (i == 0) {
      newFragment.spliceBefore(fragment(node.getChild(0)));
    } else if (i >= node.getNumChild()) {
      newFragment.spliceAfter(fragment(node.getChild(node.getNumChild() - 1)));
    } else {
      newFragment.spliceAfter(fragment(node.getChild(i - 1)));
    }
  }
  
  private TokenStreamFragment.Node leadingWhitespace(TokenStreamFragment.Node start) {
    if (start.getPrevious() == null) {
      return start;
    }
    return previousMatchingNode(newlineOrText, start.getPrevious()).getNext();
  }

  private TokenStreamFragment.Node trailingWhitespace(TokenStreamFragment.Node start) {
    return nextMatchingNode(newlineOrText, start).getPrevious();
  }

  private Predicate<TokenStreamFragment.Node> newlineOrText = Predicates.or(
      hasText("\n"),
      Predicates.not(hasText("[ \\t]")));

  private TokenStreamFragment.Node previousMatchingNode(Predicate<TokenStreamFragment.Node> predicate, TokenStreamFragment.Node start) {
    TokenStreamFragment.Node node;
    for (node = start; node != null && node.getToken() != null; node = node.getPrevious()) {
      if (predicate.apply(node)) {
        return node;
      }
    }
    return node;
  }

  private TokenStreamFragment.Node nextMatchingNode(Predicate<TokenStreamFragment.Node> predicate, TokenStreamFragment.Node start) {
    for (TokenStreamFragment.Node node = start; node != null && node.getToken() != null; node = node.getNext()) {
      if (predicate.apply(node)) {
        return node;
      }
    }
    return null;
  }

  // Tokens are from ANTLR, where columns are 0-based, and lines are 1-based.
  // But the AST is from Beaver, where they are both 1-based.
  // This is why we subtract 1 from the column.

  // The "token" fragment is the portion of the token stream that you get by just looking
  // at the node positions, and not applying any whitespace heuristics.
  private TokenStreamFragment tokenFragment(ASTNode<?> node) {
    return TokenStreamFragment.create(
        byPosition.get(node.getStartLine(), node.getStartColumn() - 1),
        byPosition.get(node.getEndLine(), node.getEndColumn() - 1));
  }
  
  private TokenStreamFragment baseFragment(ASTNode<?> node) {
    if (node.hasTokenStreamFragment() || isSynthetic(node)) {
      return node.tokenize();
    }
    return tokenFragment(node);
  }
  
  private TokenStreamFragment fragment(ASTNode<?> node) {
    if (node instanceof ast.List){
      node = node.getParent();
    }
    TokenStreamFragment baseFragment = baseFragment(node);
    TokenStreamFragment.Node startNode = leadingWhitespace(baseFragment.getStart());

    TokenStreamFragment.Node endNode = baseFragment.getEnd();
    // If this is the last, but not the only, statement on the line
    if (startNode.getToken().getCharPositionInLine() != 0 &&
        endNode.getToken().getText().equals("\n")) {
      endNode = trailingWhitespace(endNode);
    }
    return TokenStreamFragment.create(startNode, endNode);
  }


  private TokenStreamFragment synthesizeNewTokens(ASTNode<?> node) {
    return node.tokenize().spliceBefore(TokenStreamFragment.fromSingleToken("\n"));
  }

  static Function<TokenStreamFragment.Node, String> GET_TEXT = new Function<TokenStreamFragment.Node, String>() {
    @Override public String apply(TokenStreamFragment.Node node) {
      return node.getToken().getText();
    }
  };

  private static Predicate<TokenStreamFragment.Node> hasText(String text) {
    return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
  }

  private TokenStream(TokenStreamFragment stream) {
    this.stream = stream;
  }

  private TokenStream index() {
    byPosition = HashBasedTable.create();
    for (TokenStreamFragment.Node tokenNode : this) {
      Token token = tokenNode.getToken();
      int startColumn = token.getCharPositionInLine();
      int endColumn = startColumn + token.getText().length() - 1;
      byPosition.put(token.getLine(), startColumn, tokenNode);
      byPosition.put(token.getLine(), endColumn, tokenNode);
    }
    return this;
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
