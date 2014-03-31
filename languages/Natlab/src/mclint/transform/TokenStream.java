package mclint.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import matlab.MatlabLexer;
import mclint.transform.TokenStream.Node;

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

public class TokenStream implements Iterable<Node> {
  public static class Node {
    private Token token;
    private Node previous;
    private Node next;
    
    public static Node create(List<Token> tokens) {
      Node head = new Node(null, null, null);
      Node node = head;
      for (Token token : tokens) {
        node.next = new Node(token, node, null);
        node = node.next;
      }
      return head;
    }

    public Node(Token token, Node previous, Node next) {
      this.token = token;
      this.previous = previous;
      this.next = next;
    }

    public Token getToken() {
      return token;
    }

    public Node getPrevious() {
      return previous;
    }

    public Node getNext() {
      return next;
    }
    
    private List<Token> asTokenList(Node until) {
      List<Token> tokens = Lists.newArrayList();
      for (Node node = this; node != until.getNext(); node = node.getNext()) {
        tokens.add(node.getToken());
      }
      return tokens;
    }
    
    public Node copyUntil(Node until) {
      return Node.create(asTokenList(until)).getNext();
    }
    
    public void removeUntil(Node until) {
      this.previous.next = until.next;
      until.next.previous = this.previous;
    }
    
    public void spliceBefore(Node newStart, Node newEnd) {
      newStart.previous = this.previous;
      this.previous.next = newStart;
      newEnd.next = this;
      this.previous = newEnd;
    }
    
    public void spliceAfter(Node newStart, Node newEnd) {
      newStart.previous = this;
      newEnd.next = this.next;
      if (this.next != null) {
        this.next.previous = newEnd;
      }
      this.next = newStart;
    }
  }
  private Node head;
  private Table<Integer, Integer, Node> byPosition;

  public static TokenStream create(String code) {
    List<Token> tokens = tokenize(code);
    Node node = Node.create(tokens);
    return new TokenStream(node).index();
  }
  
  @Override public Iterator<Node> iterator() {
    return new AbstractSequentialIterator<Node>(head.getNext()) {
      protected Node computeNext(Node previous) {
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
    Node startNode;
    Node endNode;
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
  
  private Node leadingWhitespace(Node start) {
    return previousMatchingNode(newlineOrText, start.getPrevious()).getNext();
  }

  private Node trailingWhitespace(Node start) {
    return nextMatchingNode(newlineOrText, start).getPrevious();
  }

  private Predicate<Node> newlineOrText = Predicates.or(
      hasText("\n"),
      Predicates.not(hasText("[ \\t]")));

  private Node previousMatchingNode(Predicate<Node> predicate, Node start) {
    Node node;
    for (node = start; node.getToken() != null; node = node.getPrevious()) {
      if (predicate.apply(node)) {
        return node;
      }
    }
    return node;
  }

  private Node nextMatchingNode(Predicate<Node> predicate, Node start) {
    for (Node node = start; node != null; node = node.getNext()) {
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
  private Node tokenStartNode(ASTNode<?> node) {
    if (node instanceof ast.List){
      node = node.getParent();
    }
    return byPosition.get(node.getStartLine(), node.getStartColumn() - 1);
  }

  private Node tokenEndNode(ASTNode<?> node) {
    if (node instanceof ast.List) {
      node = node.getParent();
    }
    return byPosition.get(node.getEndLine(), node.getEndColumn() - 1);
  }

  private Node startNode(ASTNode<?> node) {
    return leadingWhitespace(tokenStartNode(node));
  }

  private Node endNode(ASTNode<?> node) {
    Node startNode = startNode(node);
    Node endNode = tokenEndNode(node);
    // If this is the last, but not the only, statement on the line
    if (startNode.getToken().getCharPositionInLine() != 0 &&
        endNode.getToken().getText().equals("\n")) {
      endNode = trailingWhitespace(endNode);
    }
    return endNode;
  }


  private Node synthesizeNewTokens(ASTNode<?> node) {
    // TODO(isbadawi): Somehow associate nodes with tokens here
    List<Token> tokens = tokenize(node.getPrettyPrinted());
    tokens.add(new ClassicToken(1, "\n"));
    return Node.create(tokens).getNext();
  }

  private static Function<Node, String> GET_TEXT = new Function<Node, String>() {
    @Override public String apply(Node node) {
      return node.getToken().getText();
    }
  };

  private static Predicate<Node> hasText(String text) {
    return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
  }

  private TokenStream(Node head) {
    this.head = head;
  }

  private TokenStream index() {
    byPosition = HashBasedTable.create();
    for (Node tokenNode : this) {
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
