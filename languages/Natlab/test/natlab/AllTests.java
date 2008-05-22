package natlab;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(NatlabScannerTests.class);
		//$JUnit-END$
		return suite;
	}

}
