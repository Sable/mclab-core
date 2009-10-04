package natlab;

import junit.framework.Test;
import junit.framework.TestSuite;

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

                suite.addTestSuite(FunctionVFDatumTest.class);
		//$JUnit-END$
		return suite;
	}

}
