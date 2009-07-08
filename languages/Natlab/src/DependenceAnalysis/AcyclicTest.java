package natlab.toolkits.DependenceAnalysis;

import natlab.ast.ForStmt;

/*
 * Author:Amina Aslam
 * Date:2 Jul,2009
 * When there is a constraint that is bound on both sides by a variable then Acyclic Test used to eliminate that constraint and transform 
 * it in a form on which GCD Test can be applied.
 * 
 */

/*
 * 
 * TO Do:
 * 1.Create a separate setUpperAndLowerBounds() function for AcyclicTest.
 * 2.Create a graph for all the constraints in the equation.
 * 
 */

public class AcyclicTest {
	
	public AcyclicTest(ForStmt forStmtArray[])
	{
		int size=forStmtArray.length;
		System.out.println("Array length is " + size);
		
	}

}
