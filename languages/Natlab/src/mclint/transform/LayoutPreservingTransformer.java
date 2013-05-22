package mclint.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import matlab.MatlabLexer;
import mclint.util.AstUtil;
import mclint.util.Parsing;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;

import ast.ASTNode;
import ast.Program;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;

/**
 * A utility for performing layout-preserving AST transformations.
 * 
 * Given the text of an input program, this class maintains a token stream together with an AST
 * and keeps them synchronized as the AST changes.
 *
 */
class LayoutPreservingTransformer implements Transformer {
  private List<Token> tokens;
  private Table<Integer, Integer, Integer> tokensByPosition;
  private Program program;

  static LayoutPreservingTransformer of(Reader source) throws IOException {
    String code = CharStreams.toString(source);
    Program program = Parsing.string(code);
    List<Token> tokens = getTokens(new StringReader(code));
    Table<Integer, Integer, Integer> tokensByPosition = indexByPosition(tokens);
    return new LayoutPreservingTransformer(program, tokens, tokensByPosition);
  }
  
  @Override
  public void remove(ASTNode<?> node) {
    tokensOf(node).clear();
    AstUtil.remove(node);
  }
  
  @Override
  public void replace(ASTNode<?> oldNode, ASTNode<?> newNode) {
    List<Token> tokens = tokensOf(oldNode);
    tokens.clear();
    List<Token> newTokens = getTokens(newNode);
    
    tokens.addAll(newTokens);
    newTokens.get(0).setLine(oldNode.getStartLine());
    newTokens.get(0).setCharPositionInLine(oldNode.getStartColumn() - 1);
    
    AstUtil.replace(oldNode, newNode);
  }
  
  private Token newline() {
    return new ClassicToken(1, "\n");
  }
  
  @Override
  public void insert(ASTNode<?> node, ASTNode<?> newNode, int i) {
    List<Token> tokens = null;
    if (i >= node.getNumChild()) {
      tokens = tokensOf(node.getChild(node.getNumChild() - 1));
    } else {
      tokens = tokensOf(node.getChild(i - 1));
    }
    tokens.addAll(getTokens(newNode));
    tokens.add(newline());
    node.insertChild(newNode, i);
  }

  private List<Token> tokensOf(ASTNode<?> node) {
    return tokens.subList(startIndex(node), endIndex(node) + 1);
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

  public Program getProgram() {
    return program;
  }
  
  public String reconstructText() {
    StringBuilder sb = new StringBuilder();
    for (Token token : tokens) {
      sb.append(token.getText());
    }
    return sb.toString();
  }
  
  private LayoutPreservingTransformer(Program program, List<Token> tokens,
      Table<Integer, Integer, Integer> tokensByPosition) {
    this.program = program;
    this.tokens = tokens;
    this.tokensByPosition = tokensByPosition;
  }
  
  private static Table<Integer, Integer, Integer> indexByPosition(List<Token> tokens) {
    Table<Integer, Integer, Integer> table = HashBasedTable.create();
    int index = 0;
    for (Token token : tokens) {
      table.put(token.getLine(), token.getCharPositionInLine(), index);
      table.put(token.getLine(),
          token.getCharPositionInLine() + token.getText().length() - 1, index);
      index++;
    }
    return table;
  }

  private static List<Token> getTokens(ASTNode<?> node) {
    try {
      return getTokens(new StringReader(node.getPrettyPrinted()));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private static List<Token> getTokens(Reader source) throws IOException {
    List<Token> tokens = Lists.newArrayList();
    MatlabLexer lexer = new MatlabLexer(new ANTLRReaderStream(source));
    Token token = lexer.nextToken();
    while (token != Token.EOF_TOKEN) {
      tokens.add(token);
      token = lexer.nextToken();
    }
    return tokens;
  }
}
