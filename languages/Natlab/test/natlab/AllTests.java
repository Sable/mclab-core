package natlab;

import junit.framework.Test;
import junit.framework.TestSuite;
import natlab.refactoring.*;

/** Top-level test suite.  Contains all tests. */
public class AllTests {
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //$JUnit-BEGIN$
    suite.addTestSuite(NatlabScannerTests.class);
    suite.addTestSuite(NatlabParserPassTests.class);
    suite.addTestSuite(NatlabParserFailTests.class);

    suite.addTestSuite(TransformedNodeTests.class);

    suite.addTestSuite(MultiRewritePassTests.class);

    suite.addTestSuite(MScriptInlinerTest.class);
    suite.addTestSuite(FEvalToCallTest.class);
    suite.addTestSuite(FunctionInlinerTest.class);
    suite.addTestSuite(ExtractFunctionTest.class);

    //$JUnit-END$
    return suite;
  }
}
