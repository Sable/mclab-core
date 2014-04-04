package mclint.transform;

import mclint.util.AstUtil;
import mclint.util.TokenUtil;

import org.antlr.runtime.Token;

import ast.ASTNode;
import ast.Function;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TokenStream {
  private TokenStreamFragment stream;
  private Table<Integer, Integer, TokenStreamFragment.Node> byPosition;

  public static TokenStream create(String code) {
    TokenStreamFragment fragment = 
        TokenStreamFragment.fromTokens(TokenUtil.tokenize(code));
    return new TokenStream(TokenStreamFragment.withSentinelNodes(fragment)).index();
  }

  public String asString() {
    return stream.withoutSentinelNodes().asString();
  }

  public void removeAstNode(ASTNode<?> node) {
    fragment(node).remove();
  }
  
  public void insertAstNode(ASTNode<?> node, ASTNode<?> newNode, int i) {
    TokenStreamFragment newFragment;
    if (AstUtil.isSynthetic(newNode)) {
      newFragment = synthesizeNewTokens(newNode);
    } else {
      newFragment = fragment(newNode).copy();
      newNode.setTokenStreamFragment(newFragment);
    }

    if (node.getNumChild() == 0) {
      // TODO(isbadawi): This is brittle, assumes statements lists I think
      newFragment.spliceAfter(
          TokenStreamFragment.fromSingleNode(
              nextMatchingNode(
                  TokenStreamFragment.Node.hasText("\n"), 
                  fragment(node).getStart())));
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
      TokenStreamFragment.Node.hasText("\n"),
      Predicates.not(TokenStreamFragment.Node.hasText("[ \\t]")));

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
    if (node.hasTokenStreamFragment() || AstUtil.isSynthetic(node)) {
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
    TokenStreamFragment tokens = node.tokenize();
    // This part is kind of lame. Depending on the kind of node we're inserting we may
    // need to insert tokens before/after to make sure we don't mess anything up.
    // For instance, if there's no newline after the "end" of a function a declaration,
    // and we just insert a new function after it, then we'll get a syntax error
    // ("endfunction [] =..."). There's probably a cleaner way of expressing this than
    // just tacking it all on here. Also for now I'm just adding tokens on unconditionally
    // -- it would be nicer to check somehow if they're really necessary.
    // TODO(isbadawi): Find a better place to put these things
    // TODO(isbadawi): Look at surrounding tokens instead of unconditionally adding newlines
    tokens = tokens.spliceBefore(TokenStreamFragment.fromSingleToken("\n"));
    if (node instanceof Function) {
      tokens = tokens.spliceAfter(TokenStreamFragment.fromSingleToken("\n"));
    }
    return tokens;
  }

  private TokenStream(TokenStreamFragment stream) {
    this.stream = stream;
  }

  private TokenStream index() {
    byPosition = HashBasedTable.create();
    for (TokenStreamFragment.Node tokenNode : this.stream.withoutSentinelNodes()) {
      Token token = tokenNode.getToken();
      int startColumn = token.getCharPositionInLine();
      int endColumn = startColumn + token.getText().length() - 1;
      byPosition.put(token.getLine(), startColumn, tokenNode);
      byPosition.put(token.getLine(), endColumn, tokenNode);
    }
    return this;
  }
}