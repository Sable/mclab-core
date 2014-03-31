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
  public static class Fragment implements Iterable<Node> {
    private Node start;
    private Node end;
    
    public Node getStart() {
      return start;
    }
    
    public Node getEnd() {
      return end;
    }
    
    public static Fragment create(List<Token> tokens) {
      Node head = new Node(tokens.get(0), null, null);
      Node tail = head;
      for (Token token : tokens.subList(1, tokens.size())) {
        tail.next = new Node(token, tail, null);
        tail = tail.next;
      }
      return create(head, tail);
    }
    
    public static Fragment single(Node node) {
      return create(node, node);
    }
    
    public static Fragment create(Node start) {
      Node end;
      for (end = start; end.getNext() != null; end = end.getNext()) {}
      return create(start, end);
    }
    
    public static Fragment createTopLevel(List<Token> tokens) {
      Fragment fragment = Fragment.create(tokens);
      Node head = new Node(null, null, fragment.getStart());
      fragment.getStart().previous = head;
      Node tail = new Node(null, fragment.getEnd(), null);
      fragment.getEnd().next = tail;
      return create(head, tail);
    }
    
    public static Fragment create(Node start, Node end) {
      return new Fragment(start, end);
    }
    
    private List<Token> asTokenList() {
      List<Token> tokens = Lists.newArrayList();
      for (Node node = getStart(); node != getEnd().getNext(); node = node.getNext()) {
        tokens.add(node.getToken());
      }
      return tokens;
    }

    public Fragment copy() {
      return Fragment.create(asTokenList());
    }
    
    public void remove() {
      getStart().previous.next = getEnd().next;
      getEnd().next.previous = getStart().previous;
    }
    
    public void spliceBefore(Fragment fragment) {
      getStart().previous = fragment.getStart().previous;
      if (getStart().previous != null) {
        getStart().previous.next = getStart();
      }
      fragment.getStart().previous = getEnd();
      getEnd().next = fragment.getStart();
    }
    
    public void spliceAfter(Fragment fragment) {
      getStart().previous = fragment.getEnd();
      getEnd().next = fragment.getEnd().next;
      if (getEnd().next != null) {
        getEnd().next.previous = getEnd();
      }
      fragment.getEnd().next = getStart();
    }
    
    public Iterator<Node> iterator() {
      return new AbstractSequentialIterator<Node>(getStart()) {
        @Override protected Node computeNext(Node previous) {
          return previous == getEnd() ? null : previous.getNext();
        }
      };
    }
    
    private Fragment(Node start, Node end) {
      this.start = start;
      this.end = end;
    }
  }

  public static class Node {
    private Token token;
    private Node previous;
    private Node next;
    
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
  }
  private Fragment stream;
  private Table<Integer, Integer, Node> byPosition;

  public static TokenStream create(String code) {
    return new TokenStream(Fragment.createTopLevel(tokenize(code))).index();
  }
  
  @Override public Iterator<Node> iterator() {
    return Fragment.create(stream.getStart().getNext(), stream.getEnd().getPrevious()).iterator();
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
    Fragment oldFragment = fragment(node);
    Fragment newFragment;
    if (isSynthetic(newNode)) {
      newFragment = synthesizeNewTokens(newNode);
    } else {
      newFragment = fragment(newNode).copy();
    }

    if (node.getNumChild() == 0) {
      // TODO(isbadawi): This is brittle, assumes statements lists I think
      newFragment.spliceAfter(
          Fragment.single(nextMatchingNode(hasText("\n"), oldFragment.getStart())));
    } else if (i == 0) {
      newFragment.spliceBefore(fragment(node.getChild(0)));
    } else if (i >= node.getNumChild()) {
      newFragment.spliceAfter(fragment(node.getChild(node.getNumChild() - 1)));
    } else {
      newFragment.spliceAfter(fragment(node.getChild(i - 1)));
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

  // The "token" fragment is the portion of the token stream that you get by just looking
  // at the node positions, and not applying any whitespace heuristics.
  private Fragment tokenFragment(ASTNode<?> node) {
    if (node instanceof ast.List){
      node = node.getParent();
    }
    return Fragment.create(
        byPosition.get(node.getStartLine(), node.getStartColumn() - 1),
        byPosition.get(node.getEndLine(), node.getEndColumn() - 1));
  }
  
  private Fragment fragment(ASTNode<?> node) {
    Fragment tokenFragment = tokenFragment(node);
    Node startNode = leadingWhitespace(tokenFragment.getStart());

    Node endNode = tokenFragment.getEnd();
    // If this is the last, but not the only, statement on the line
    if (startNode.getToken().getCharPositionInLine() != 0 &&
        endNode.getToken().getText().equals("\n")) {
      endNode = trailingWhitespace(endNode);
    }
    return Fragment.create(startNode, endNode);
  }


  private Fragment synthesizeNewTokens(ASTNode<?> node) {
    // TODO(isbadawi): Somehow associate nodes with tokens here
    List<Token> tokens = tokenize(node.getPrettyPrinted());
    tokens.add(new ClassicToken(1, "\n"));
    return Fragment.create(tokens);
  }

  private static Function<Node, String> GET_TEXT = new Function<Node, String>() {
    @Override public String apply(Node node) {
      return node.getToken().getText();
    }
  };

  private static Predicate<Node> hasText(String text) {
    return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
  }

  private TokenStream(Fragment stream) {
    this.stream = stream;
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
