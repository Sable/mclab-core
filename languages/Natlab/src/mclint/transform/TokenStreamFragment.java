package mclint.transform;

import java.util.Arrays;
import java.util.Iterator;

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

public class TokenStreamFragment implements Iterable<TokenStreamFragment.Node> {
  public static class Node {
    public static Function<Node, Token> GET_TOKEN = new Function<Node, Token>() {
      @Override public Token apply(Node node) {
        return node.getToken();
      }
    };
    
    public static Function<Node, String> GET_TEXT = new Function<Node, String>() {
      @Override public String apply(Node node) {
        return node.getToken().getText();
      }
    };
    
    public static Predicate<Node> hasText(final String text) {
      return Predicates.compose(Predicates.containsPattern(text), GET_TEXT);
    }

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
    return TokenStreamFragment.fromTokens(Iterables.transform(this, Node.GET_TOKEN));
  }
  
  public void remove() {
    getStart().previous.next = getEnd().next;
    getEnd().next.previous = getStart().previous;
  }
  
  // Splices this fragment in before the given fragment.
  // Returns the combined fragment (which may or may not be useful).
  public TokenStreamFragment spliceBefore(TokenStreamFragment fragment) {
    getStart().previous = fragment.getStart().previous;
    if (getStart().previous != null) {
      getStart().previous.next = getStart();
    }
    fragment.getStart().previous = getEnd();
    getEnd().next = fragment.getStart();
    return TokenStreamFragment.create(getStart(), fragment.getEnd());
  }
  
  // Splices this fragment in after the given fragment.
  // Returns the combined fragment (which may or may not be useful).
  public TokenStreamFragment spliceAfter(TokenStreamFragment fragment) {
    getStart().previous = fragment.getEnd();
    getEnd().next = fragment.getEnd().next;
    if (getEnd().next != null) {
      getEnd().next.previous = getEnd();
    }
    fragment.getEnd().next = getStart();
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
    return Joiner.on("").join(Iterables.transform(this, Node.GET_TEXT));
  }
  
  private TokenStreamFragment(TokenStreamFragment.Node start, TokenStreamFragment.Node end) {
    this.start = start;
    this.end = end;
  }
}