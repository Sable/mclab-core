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
			 isApplicable= svpcTest.checkDependence(cGraph);
		}
		if (!isApplicable)
		{
			System.out.println("Apply Acyclic test");
			//then apply Acyclic test
		}
	}


}
