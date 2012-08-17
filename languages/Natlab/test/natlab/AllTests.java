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
		//TODO-AC: uncomment
		suite.addTestSuite(NatlabParserPassTests.class);
		suite.addTestSuite(NatlabParserFailTests.class);

		suite.addTestSuite(SymbolTablePassTests.class);
		suite.addTestSuite(SymbolTableFailTests.class);
		suite.addTestSuite(SymbolTableSanityTests.class);

		//suite.addTestSuite(LookupLValuePassTests.class);
		//suite.addTestSuite(LookupLValueFailTests.class);
		//suite.addTestSuite(LookupLValueSanityTests.class);

		suite.addTestSuite(ErrorCheckTest.class);
		suite.addTestSuite(XMLCommandPassTests.class);

                //rewrite tests
                suite.addTestSuite(TransformedNodeTests.class);

                //suite.addTestSuite(MultireturnSimplifyPassTests.class);
                //suite.addTestSuite(LeftThreeAddressSimplifyPassTests.class);
                //suite.addTestSuite(SimpleIfPassTests.class);
                suite.addTestSuite(MultiRewritePassTests.class);

        suite.addTestSuite(MScriptInlinerTest.class);
        suite.addTestSuite(FEvalToCallTest.class);
        suite.addTestSuite(FunctionInlinerTest.class);
        suite.addTestSuite(ExtractFunctionTest.class);

		//$JUnit-END$
		return suite;
	}

}
