package natlab;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class WeederTest extends TestCase {
  private List<CompilationProblem> getParseErrors(String code) {
    List<CompilationProblem> errors = new ArrayList<>();
    Parse.parseMatlabFile("<none>", new StringReader(code), errors);
    return errors;
  }
  
  private void assertHasError(String error, String code) {
    List<CompilationProblem> errors = getParseErrors(code);
    
    assertEquals(1, errors.size());
    assertTrue(errors.get(0).toString().contains(error));
  }

  private void assertInvalidLhs(String code) {
    assertHasError("Invalid left-hand side of assignment", code);
  }
  
  private void assertInvalidBreak(String code) {
    assertHasError("Break statement outside loop or script", code);
  }
  
  private void assertInvalidContinue(String code) {
    assertHasError("Continue statement outside loop or script", code);
  }
  
  private void assertNoErrors(String code) {
    assertEquals(0, getParseErrors(code).size());
  }
  
  public void testNonLvalueInsideLhsMatrix() {
    assertInvalidLhs("[a + b, c] = foo()");
  }
  
  public void testNonLvalueOnLhs() {
    assertInvalidLhs("a + b = foo()");
  }
  
  public void testValidAssignments() {
    assertNoErrors("a = 6;");
    assertNoErrors("[a, b] = 6;");
  }
  
  public void testBreakAtFunctionTopLevel() {
    assertInvalidBreak(new StringBuilder()
        .append("function f\n")
        .append("  break\n")
        .append("end").toString());
  }
  
  public void testContinueAtTopLevel() {
    assertInvalidContinue(new StringBuilder()
        .append("function f\n")
        .append("  continue\n")
        .append("end").toString());
  }
  
  public void testValidUsesOfBreakAndContinue() {
    assertNoErrors("continue");
    assertNoErrors("break");
    assertNoErrors(new StringBuilder()
        .append("function f\n")
        .append("  for i = 1:10\n")
        .append("    break\n")
        .append("    continue\n")
        .append("  end\n")
        .append("end").toString());
  }
}
