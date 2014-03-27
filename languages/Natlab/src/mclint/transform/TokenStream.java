package mclint.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import matlab.MatlabLexer;
import mclint.transform.TokenStream.TokenNode;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import ast.ASTNode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

class TokenStream implements Iterable<TokenNode> {
  static class TokenNode {
    private Token token;
    private TokenNode previous;
    private TokenNode next;
    
    public static TokenNode create(List<Token> tokens) {
      TokenNode head = new TokenNode(null, null, null);
      TokenNode node = head;
      for (Token token : tokens) {
        node.next = new TokenNode(token, node, null);
        node = node.next;
      }
      return head;
    }

    public TokenNode(Token token, TokenNode previous, TokenNode next) {
      this.token = token;
      this.previous = previous;
      this.next = next;
    }

    public Token getToken() {
      return token;
    }

    public TokenNode getPrevious() {
      return previous;
    }

    public TokenNode getNext() {
      return next;
    }
    
    private List<Token> asTokenList(TokenNode until) {
      List<Token> tokens = Lists.newArrayList();
      for (TokenNode node = this; node != until.getNext(); node = node.getNext()) {
        tokens.add(node.getToken());
      }
      return tokens;
    }
    
    public TokenNode copyUntil(TokenNode until) {
      return TokenNode.create(asTokenList(until)).getNext();
    }
    
    public void removeUntil(TokenNode until) {
      this.previous.next = until.next;
      until.next.previous = this.previous;
    }
    
    public void spliceBefore(TokenNode newStart, TokenNode newEnd) {
      newStart.previous = this.previous;
      this.previous.next = newStart;
      newEnd.next = this;
      this.previous = newEnd;
    }
    
    public void spliceAfter(TokenNode newStart, TokenNode newEnd) {
      newStart.previous = this;
      newEnd.next = this.next;
      if (this.next != null) {
        this.next.previous = newEnd;
      }
      this.next = newStart;
    }
  }
  private TokenNode head;
  private Table<Integer, Integer, TokenNode> byPosition;

  public static TokenStream create(String code) {
    List<Token> tokens = tokenize(code);
    TokenNode node = TokenNode.create(tokens);
    return new TokenStream(node).index();
  }
  
  @Override public Iterator<TokenNode> iterator() {
    return new AbstractSequentialIterator<TokenNode>(head.getNext()) {
      protected TokenNode computeNext(TokenNode previous) {
        return previous.getNext();
      }
    };
  }

  public String asString() {
    return Joiner.on("").join(Iterables.transform(this, GET_TEXT));
  }

  private static boolean isSynthetic(ASTNode<?> node) {
    return node.getStartLine() == 0 && !(node instanceof ast.List);
  }

  public void removeAstNode(ASTNode<?> node) {
    startNode(node).removeUntil(endNode(node));
  }
  
  public void insertAstNode(ASTNode<?> node, ASTNode<?> newNode, int i) {
    TokenNode startNode;
    TokenNode endNode;
    if (isSynthetic(newNode)) {
      startNode = synthesizeNewTokens(newNode);
    } else {
      startNode = startNode(newNode).copyUntil(endNode(newNode));
    }
    for (endNode = startNode; endNode.getNext() != null; endNode = endNode.getNext()) {}

    if (node.getNumChild() == 0) {
      // TODO(isbadawi): This is brittle, assumes statements lists I think
      nextMatchingNode(hasText("\n"), startNode(node)).spliceAfter(startNode, endNode);
    } else if (i == 0) {
      startNode(node.getChild(0)).spliceBefore(startNode, endNode);
    } else if (i >= node.getNumChild()) {
      endNode(node.getChild(node.getNumChild() - 1)).spliceAfter(startNode, endNode);
    } else {
      endNode(node.getChild(i - 1)).spliceAfter(startNode, endNode);
    }
  }
  
  private TokenNode leadingWhitespace(TokenNode start) {
    return previousMatchingNode(newlineOrText, start.getPrevious()).getNext();
  }

  private TokenNode trailingWhitespace(TokenNode start) {
    return nextMatchingNode(newlineOrText, start).getPrevious();
  }

  private Predicate<TokenNode> newlineOrText = Predicates.or(
      hasText("\n"),
      Predicates.not(hasText("[ \\t]")));

  private TokenNode previousMatchingNode(Predicate<TokenNode> predicate, TokenNode start) {
    TokenNode node;
    for (node = start; node.getToken() != null; node = node.getPrevious()) {
      if (predicate.apply(node)) {
        return node;
      }
    }
    return node;
  }

  private TokenNode nextMatchingNode(Predicate<TokenNode> predicate, TokenNode start) {
    for (TokenNode node = start; node != null; node = node.getNext()) {
      if (predicate.apply(node)) {
        return node;
      }
    }
    return null;
  }

  // Tokens are from ANTLR, where columns are 0-based, and lines are 1-based.
  // But the AST is from Beaver, where they are both 1-based.
  // This is why we subtract 1 from the column.

  // The "token" index is the index into token stream that you get by just looking
  // at the node positions, and not applying any whitespace heuristics.
  private TokenNode tokenStartNode(ASTNode<?> node) {
    if (node instanceof ast.List){
      node = node.getParent();
    }
    return byPosition.get(node.getStartLine(), node.getStartColumn() - 1);
  }

  private TokenNode tokenEndNode(ASTNode<?> node) {
    if (node instanceof ast.List) {
      node = node.getParent();
    }
    return byPosition.get(node.getEndLine(), node.getEndColumn() - 1);
  }

  private TokenNode startNode(ASTNode<?> node) {
    return leadingWhitespace(tokenStartNode(node));
  }

  private TokenNode endNode(ASTNode<?> node) {
    TokenNode startNode = startNode(node);
    TokenNode endNode = tokenEndNode(node);
    // If this is the last, but not the only, statement on the line
    if (startNode.getToken().getCharPositionInLine() != 0 &&
        endNode.getToken().getText().equals("\n")) {
      endNode = trailingWhitespace(endNode);
    }
    return endNode;
  }


  private TokenNode synthesizeNewTokens(ASTNode<?> node) {
    // TODO(isbadawi): Somehow associate nodes with tokens here
    List<Token> tokens = tokenize(node.getPrettyPrinted());
    tokens.add(new ClassicToken(1, "\n"));
    return TokenNode.create(tokens).getNext();
  }

  private static Function<TokenNode, String> GET_TEXT = new Function<TokenNode, String>() {
    @Override public String apply(TokenNode node) {
      return node.getToken().getText();
    }
  };

  private static Predicate<TokenNode> hasText(String text) {
    return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
  }

  private TokenStream(TokenNode head) {
    this.head = head;
  }

  private TokenStream index() {
    byPosition = HashBasedTable.create();
    for (TokenNode tokenNode : this) {
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
