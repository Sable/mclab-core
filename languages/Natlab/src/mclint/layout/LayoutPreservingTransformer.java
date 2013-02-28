package mclint.layout;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import matlab.MatlabLexer;
import mclint.util.Parsing;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Token;

import ast.Program;

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
public class LayoutPreservingTransformer {
  private List<Token> tokens;
  private Table<Integer, Integer, Integer> tokensByPosition;
  private Program program;

  public static LayoutPreservingTransformer of(Reader source) throws IOException {
    String code = CharStreams.toString(source);
    Program program = Parsing.string(code);
    List<Token> tokens = getTokens(new StringReader(code));
    Table<Integer, Integer, Integer> tokensByPosition = indexByPosition(tokens);
    return new LayoutPreservingTransformer(program, tokens, tokensByPosition);
  }

  public Program getProgram() {
    return (Program) program.fullCopy();
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
      table.put(token.getLine(), token.getCharPositionInLine(), index++);
    }
    return table;
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
