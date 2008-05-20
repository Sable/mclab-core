package lambda;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(LambdaScannerTests.class);
		suite.addTestSuite(LambdaParserTests.class);
		suite.addTestSuite(SubstTests.class);
		suite.addTestSuite(ReductionTests.class);
		//$JUnit-END$
		return suite;
	}

}
