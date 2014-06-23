package mclint.transform;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;

public class TokenStreamFragment implements Iterable<TokenStreamFragment.Node> {
  public static class Node {
    private Token token;
    Node previous;
    Node next;
    
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

  private TokenStreamFragment.Node start;
  private TokenStreamFragment.Node end;
  
  public TokenStreamFragment.Node getStart() {
    return start;
  }
  
  public TokenStreamFragment.Node getEnd() {
    return end;
  }
  
  public static TokenStreamFragment fromTokens(Iterable<Token> tokenIterable) {
    FluentIterable<Token> tokens = FluentIterable.from(tokenIterable);
    TokenStreamFragment.Node head = new TokenStreamFragment.Node(tokens.get(0), null, null);
    TokenStreamFragment.Node tail = head;
    for (Token token : tokens.skip(1)) {
      tail.next = new TokenStreamFragment.Node(token, tail, null);
      tail = tail.next;
    }
    return create(head, tail);
  }
  
  public static TokenStreamFragment fromSingleToken(String token) {
    return fromTokens(Arrays.asList((Token) new ClassicToken(1, token)));
  }
  
  public static TokenStreamFragment fromSingleNode(TokenStreamFragment.Node node) {
    return create(node, node);
  }
  
  public static TokenStreamFragment create(TokenStreamFragment.Node start, TokenStreamFragment.Node end) {
    return new TokenStreamFragment(start, end);
  }
  
  public static TokenStreamFragment withSentinelNodes(TokenStreamFragment fragment) {
    Node head = new Node(null, null, fragment.getStart());
    fragment.getStart().previous = head;
    Node tail = new Node(null, fragment.getEnd(), null);
    fragment.getEnd().next = tail;
    return create(head, tail);
  }
  
  public TokenStreamFragment withoutSentinelNodes() {
    return create(getStart().getNext(), getEnd().getPrevious());
  }

  public TokenStreamFragment copy() {
    return TokenStreamFragment.fromTokens(StreamSupport.stream(spliterator(), false)
        .map(Node::getToken)
        .collect(Collectors.toList()));
  }
  
  private static void link(Node from, Node to) {
    if (from != null) from.next = to;
    if (to != null) to.previous = from;
  }
  
  public void remove() {
    link(getStart().previous, getEnd().next);
  }
  
  // Splices this fragment in before the given fragment.
  // Returns the combined fragment (which may or may not be useful).
  public TokenStreamFragment spliceBefore(TokenStreamFragment fragment) {
    link(fragment.getStart().previous, getStart());
    link(getEnd(), fragment.getStart());
    return TokenStreamFragment.create(getStart(), fragment.getEnd());
  }
  
  // Splices this fragment in after the given fragment.
  // Returns the combined fragment (which may or may not be useful).
  public TokenStreamFragment spliceAfter(TokenStreamFragment fragment) {
    link(getEnd(), fragment.getEnd().next);
    link(fragment.getEnd(), getStart());
    return TokenStreamFragment.create(fragment.getStart(), getEnd());
  }
  
  public Iterator<TokenStreamFragment.Node> iterator() {
    return new AbstractSequentialIterator<TokenStreamFragment.Node>(getStart()) {
      @Override protected TokenStreamFragment.Node computeNext(TokenStreamFragment.Node previous) {
        return previous == getEnd() ? null : previous.getNext();
      }
    };
  }
  
  public String asString() {
    return StreamSupport.stream(spliterator(), false)
        .map(node -> node.getToken().getText())
        .collect(Collectors.joining(""));
  }
  
  private TokenStreamFragment(TokenStreamFragment.Node start, TokenStreamFragment.Node end) {
    this.start = start;
    this.end = end;
  }
}