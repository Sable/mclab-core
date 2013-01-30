package mclint;

import junit.framework.TestCase;
import mclint.AnalysisKit;
import mclint.util.Parsing;
import ast.ASTNode;
import ast.Program;

public abstract class McLintTestCase extends TestCase {
  protected AnalysisKit kit;
  
  protected void parse(String code) {
    kit = AnalysisKit.forAST(Parsing.string(code));
  }
  
  protected void assertEquivalent(String expectedCode, ASTNode<?> program) {
    Program expected = Parsing.string(expectedCode);
    assertEquals(expected.getPrettyPrinted(), program.getPrettyPrinted());
  }
}
