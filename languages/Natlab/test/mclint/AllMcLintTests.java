package mclint;

import junit.framework.TestSuite;
import mclint.refactoring.RenameVariableTest;

public class AllMcLintTests {
  public static TestSuite suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(RenameVariableTest.class);
    return suite;
  }
}
