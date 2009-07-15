package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import natlab.ast.ForStmt;
/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 */

public class DependenceAnalysisDriver {
	
	private ForStmt forNode;
	private ConstraintsGraph cGraph;
	private SVPCTest svpcTest;
	private AcyclicTest acyclicTest;
	public DependenceAnalysisDriver(ForStmt fNode)	
	{	
		forNode=fNode;
	}
	public void createConstraints()
	{
		ConstraintsToolBox cToolBox=new ConstraintsToolBox(forNode);
		 cGraph=cToolBox.createContraints();
		 ApplyTests();
		
	}
	public void ApplyTests()
	{
		boolean isApplicable=false;
		if(cGraph.getGraphSize()!=0)
		{
			svpcTest=new SVPCTest();
			 
			isApplicable= svpcTest.checkDependence(cGraph);
			//cGraph.temp();
			 System.out.println("i am in SVPC test");
		}
		if (!isApplicable)
		{
			System.out.println("Apply Acyclic test");
			acyclicTest=new AcyclicTest();
			cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
			boolean isAcyclicApplicable=acyclicTest.getisApplicable();
			if(isAcyclicApplicable)
			{
				System.out.println("now apply SVPC Test");
				isApplicable= svpcTest.checkDependence(cGraph);
			}
			
		}
	}


}
