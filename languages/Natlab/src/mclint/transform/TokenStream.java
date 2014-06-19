package mclint.transform;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import mclint.util.AstUtil;
import mclint.util.TokenUtil;

import org.antlr.runtime.Token;

import ast.ASTNode;
import ast.BinaryExpr;
import ast.EmptyStmt;
import ast.Function;
import ast.Stmt;

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
  
  public void replaceAstNode(ASTNode<?> oldNode, ASTNode<?> newNode) {
    TokenStreamFragment oldFragment = baseFragment(oldNode);
    TokenStreamFragment newFragment = synthesizeFragment(newNode);
    
    newFragment.spliceBefore(oldFragment);
    oldFragment.remove();
  }

  private String getIndentation(ASTNode<?> node) {
    TokenStreamFragment fragment = baseFragment(node);
    TokenStreamFragment.Node start = leadingWhitespace(fragment.getStart());
    TokenStreamFragment.Node end = fragment.getStart().getPrevious();
    return TokenStreamFragment.create(start, end).copy().asString();
  }
  
  private ASTNode<?> firstNonEmptyStmt(ASTNode<?> node, IntStream indices) {
    return indices
        .mapToObj(index -> (ASTNode<?>) node.getChild(index))
        .filter(stmt -> !(stmt instanceof EmptyStmt))
        .findFirst().orElse(null);
  }
  
  private ASTNode<?> getPreviousNonEmptyStmt(ASTNode<?> node, int i) {
    return firstNonEmptyStmt(node, IntStream.iterate(i, j -> j - 1).limit(i + 1));
  }
  
  private ASTNode<?> getNextNonEmptyStmt(ASTNode<?> node, int i) {
    return firstNonEmptyStmt(node, IntStream.range(i, node.getNumChild()));
  }
  
  private ASTNode<?> getNearestNonEmptyStmt(ASTNode<?> node, int i) {
    if (i == 0) {
      return getNextNonEmptyStmt(node, 0);
    }
    ASTNode<?> previous = getPreviousNonEmptyStmt(node, i - 1);
    return previous != null ? previous : getNextNonEmptyStmt(node, i - 1);
  }
  
  private String guessIndentation(ASTNode<?> node, int i) {
    ASTNode<?> nearest = getNearestNonEmptyStmt(node, i);
    return nearest != null ? getIndentation(nearest) : node.getIndent() + "  ";
  }
  
  private TokenStreamFragment synthesizeFragment(ASTNode<?> node) {
    if (AstUtil.isSynthetic(node)) {
      return synthesizeNewTokens(node);
    }
    return node.tokenize();
  }

  private boolean isInputParamList(ASTNode<?> node) {
    return node.getParent() instanceof Function &&
        ((Function) node.getParent()).getInputParams() == node;
  }

  private boolean isOutputParamList(ASTNode<?> node) {
    return node.getParent() instanceof Function &&
        ((Function) node.getParent()).getOutputParams() == node;
  }

  public void insertAstNode(ASTNode<?> node, ASTNode<?> newNode, int i) {
    TokenStreamFragment newFragment = synthesizeFragment(newNode);

    if (newNode instanceof Stmt) {
      String indentation = guessIndentation(node, i);
      newFragment = TokenStreamFragment
          .fromSingleToken(indentation)
          .spliceBefore(newFragment);
    }

    if (node.getNumChild() == 0) {
      if (newNode instanceof Stmt || newNode instanceof Function) {
        newFragment.spliceAfter(
            TokenStreamFragment.fromSingleNode(
                nextMatchingNode(
                    n -> n.getToken().getText().contains("\n"),
                    fragment(node).getStart())));
      } else if (isInputParamList(node)) {
        if (node.getStartLine() == 0 && node.getStartColumn() == 0) {
          newFragment = newFragment.spliceAfter(TokenStreamFragment.fromSingleToken("("));
          newFragment = newFragment.spliceBefore(TokenStreamFragment.fromSingleToken(")"));
          newFragment.spliceAfter(baseFragment(((Function) node.getParent()).getName()));
        } else {
          newFragment.spliceAfter(TokenStreamFragment.fromSingleNode(baseFragment(node).getStart()));
        }
      } else if (isOutputParamList(node)) {
        if (node.getStartLine() == 0 && node.getStartColumn() == 0) {
          newFragment = newFragment.spliceAfter(TokenStreamFragment.fromSingleToken("["));
          newFragment = newFragment.spliceBefore(TokenStreamFragment.fromSingleToken("] = "));
          newFragment.spliceBefore(baseFragment(((Function) node.getParent()).getName()));
        } else {
          newFragment.spliceAfter(TokenStreamFragment.fromSingleNode(baseFragment(node).getStart()));
        }
      }
    } else if (i == 0) {
      if (!(newNode instanceof Stmt || newNode instanceof Function)) {
        newFragment = newFragment.spliceBefore(TokenStreamFragment.fromSingleToken(", "));
      }
      newFragment.spliceBefore(fragment(node.getChild(0)));
    } else {
      if (!(newNode instanceof Stmt || newNode instanceof Function)) {
        newFragment = newFragment.spliceAfter(TokenStreamFragment.fromSingleToken(", "));
      }
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

  private Predicate<TokenStreamFragment.Node> newlineOrText = node ->
    node.getToken().getText().contains("\n") || !node.getToken().getText().matches(".*[ \\t].*");

  private TokenStreamFragment.Node previousMatchingNode(Predicate<TokenStreamFragment.Node> predicate, TokenStreamFragment.Node start) {
    TokenStreamFragment.Node node;
    for (node = start; node != null && node.getToken() != null; node = node.getPrevious()) {
      if (predicate.test(node)) {
        return node;
      }
    }
    return node;
  }

  private TokenStreamFragment.Node nextMatchingNode(Predicate<TokenStreamFragment.Node> predicate, TokenStreamFragment.Node start) {
    for (TokenStreamFragment.Node node = start; node != null && node.getToken() != null; node = node.getNext()) {
      if (predicate.test(node)) {
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

  // The "base" fragment is the token fragment, unless the node has an explicitly
  // set fragment (or is synthetic and we're going to make one now).
  private TokenStreamFragment baseFragment(ASTNode<?> node) {
    if (node.hasTokenStreamFragment() || AstUtil.isSynthetic(node)) {
      return node.tokenize();
    }
    return tokenFragment(node);
  }

  // The fragment is the portion of the token stream corresponding to the given node,
  // plus applying any heuristics for indentation, surrounding whitespace, etc.
  // It's the "extended" range of the node.
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
  
  @SuppressWarnings("unchecked")
  public <T extends ASTNode<?>> T copyAstNode(T node) {
    T copy = (T) node.fullCopy();
    TokenStreamFragment fragment = baseFragment(node).copy();
    // To be safe, add some parens around binary expressions (if they're not there already).
    if (node instanceof BinaryExpr) {
      String serialized = fragment.asString().trim();
      if (!(serialized.startsWith("(") && serialized.endsWith(")"))) {
        fragment = TokenStreamFragment.fromSingleToken("(").spliceBefore(fragment);
        fragment = TokenStreamFragment.fromSingleToken(")").spliceAfter(fragment);
      }
    }
    copy.setTokenStreamFragment(fragment);
    return copy;
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
    if (node instanceof Function || node instanceof Stmt) {
      tokens = tokens.spliceBefore(TokenStreamFragment.fromSingleToken("\n"));
    }
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
