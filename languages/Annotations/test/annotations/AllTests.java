package annotations;

import junit.framework.Test;
import junit.framework.TestSuite;

/** Top-level test suite.  Contains all tests. */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(AnnotationScannerTests.class);
		suite.addTestSuite(AnnotationParserPassTests.class);
		suite.addTestSuite(AnnotationParserFailTests.class);
		//$JUnit-END$
		return suite;
	}

}
