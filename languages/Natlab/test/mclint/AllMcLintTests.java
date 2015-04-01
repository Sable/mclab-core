package mclint;

import junit.framework.TestSuite;
import mclint.refactoring.RenameVariableTest;
import mclint.transform.IndentationHeuristicsTest;
import mclint.transform.LayoutPreservingTransformerTest;

public class AllMcLintTests {
  public static TestSuite suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(RenameVariableTest.class);
    suite.addTestSuite(LayoutPreservingTransformerTest.class);
    suite.addTestSuite(IndentationHeuristicsTest.class);
    return suite;
  }
}
