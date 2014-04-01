package mclint.transform;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import com.google.common.base.Joiner;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
  
  public static TokenStreamFragment create(List<Token> tokens) {
    TokenStreamFragment.Node head = new TokenStreamFragment.Node(tokens.get(0), null, null);
    TokenStreamFragment.Node tail = head;
    for (Token token : tokens.subList(1, tokens.size())) {
      tail.next = new TokenStreamFragment.Node(token, tail, null);
      tail = tail.next;
    }
    return create(head, tail);
  }
  
  public static TokenStreamFragment fromSingleToken(String token) {
    return create(Arrays.asList((Token) new ClassicToken(1, token)));
  }
  
  public static TokenStreamFragment single(TokenStreamFragment.Node node) {
    return create(node, node);
  }
  
  public static TokenStreamFragment create(TokenStreamFragment.Node start) {
    TokenStreamFragment.Node end;
    for (end = start; end.getNext() != null; end = end.getNext()) {}
    return create(start, end);
  }
  
  public static TokenStreamFragment createTopLevel(List<Token> tokens) {
    TokenStreamFragment fragment = TokenStreamFragment.create(tokens);
    TokenStreamFragment.Node head = new TokenStreamFragment.Node(null, null, fragment.getStart());
    fragment.getStart().previous = head;
    TokenStreamFragment.Node tail = new TokenStreamFragment.Node(null, fragment.getEnd(), null);
    fragment.getEnd().next = tail;
    return create(head, tail);
  }
  
  public static TokenStreamFragment create(TokenStreamFragment.Node start, TokenStreamFragment.Node end) {
    return new TokenStreamFragment(start, end);
  }
  
  private List<Token> asTokenList() {
    List<Token> tokens = Lists.newArrayList();
    for (TokenStreamFragment.Node node : this) {
      tokens.add(node.getToken());
    }
    return tokens;
  }

  public TokenStreamFragment copy() {
    return TokenStreamFragment.create(asTokenList());
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
    return Joiner.on("").join(Iterables.transform(this, TokenStream.GET_TEXT));
  }
  
  private TokenStreamFragment(TokenStreamFragment.Node start, TokenStreamFragment.Node end) {
    this.start = start;
    this.end = end;
  }
}