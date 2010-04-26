package natlab.toolkits.DependenceAnalysis;

import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.MinusExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PlusExpr;
import ast.RangeExpr;

import ast.MTimesExpr;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import ast.BinaryExpr;


/*
 * Author:Amina Aslam
 * Date:06 Jul,2009
 * ConstraintsToolBox class creates constraints from the equations and puts them in ConstraintsGraph.
 */


public class ConstraintsToolBox {	
	//private ForStmt forNode;
	//private Stmt fStmt;
	//private String dependencyFlag="No";
	//private ForStmt forStmtArray[];
	private LinkedList<ForStmt> forStmtArray;
	private static int loopIndex=0;
	//private static boolean resultArray[];
	private ConstraintsGraph cGraph;
	private Hashtable<String,LinkedList<PredictedData>> pTable;
	private boolean rangeInfo=false;
	//private float loopNo;
	//private Vector<DependenceData> dataVector;
	
	//private Expr newExpr;
	//private Expr teExpr;

/*public float getLoopNo() {
	return loopNo;
}

public void setLoopNo(float loopNo) {
	this.loopNo = loopNo;
}*/

public ConstraintsToolBox(){
	cGraph=new ConstraintsGraph();
}

	
/*public Hashtable<String, LinkedList<PredictedData>> getPTable() {
		return pTable;
}*/


public void setPTable(Hashtable<String, LinkedList<PredictedData>> table){
		pTable = table;
		
}
/*public ForStmt[] getForStmtArray() {
	return forStmtArray;
}


public void setForStmtArray(ForStmt[] forStmtArray) {
	this.forStmtArray = forStmtArray;
}*/


public static int getLoopIndex() {
	return loopIndex;
}


public LinkedList<ForStmt> getForStmtArray() {
	return forStmtArray;
}


public void setForStmtArray(LinkedList<ForStmt> forStmtArray) {
	this.forStmtArray = forStmtArray;
}


public static void setLoopIndex(int loopIndex) {
	ConstraintsToolBox.loopIndex = loopIndex;
}
private void setRangeInfo(boolean rangeInfo) {
	this.rangeInfo = rangeInfo;
}
private boolean isRangeInfo(){
	return rangeInfo;
}
private void prepareDependenceData(Expr aExpr,Expr bExpr,DependenceData Data,Vector<DependenceData> dataVector){
   int counter=0;	
   if(!pTable.isEmpty()){
	 float lNo=Data.getLoopNo();  
	 if(pTable.containsKey(Float.toString(lNo))){		 
		setRangeInfo(true);	
		LinkedList<PredictedData> tList=pTable.get(Float.toString(Data.getLoopNo()));		
		for(int i=0;i<tList.size();i++){
			PredictedData pData=tList.get(i);
			DependenceData dData=Data.clone();
			dData.setLVarName(pData.getLVName());
			dData.setArrayAccess(aExpr.getPrettyPrinted()+"	 =	"+bExpr.getPrettyPrinted());
			dData.setStartRange(pData.getLowerBound());
			dData.setEndRange(pData.getUpperBound());			
			for(int j=0;j<dData.getNestingLevel();j++){
				NestedLoop nLoop=dData.getNestedLoop();
				lNo=(float) (lNo+j+0.1);
				if(pTable.containsKey(Float.toString(lNo))){
					LinkedList<PredictedData> list=pTable.get(Float.toString(lNo));
					PredictedData data=list.get(counter);
				    nLoop.setLoopNo(data.getLoopNo());
				    nLoop.setLVarName(data.getLVName());
				    nLoop.setStartRange(data.getLowerBound());
				    nLoop.setEndRange(data.getUpperBound());			    
				}//end of if 
			}//end of for
			counter++;
			makeEquationsForSubscriptExprs(aExpr,bExpr,dData);
			dataVector.add(dData);
		}//end of for
	 }//end of 2nd if
   } //end of 1st if

}

/*
	 * This function checks whether accessed arrays are the same or not.
	 * e.g. for int i=1:1:10
	 *  		a(i,j)=a(i+10,j-9)
	 *  	end
*/	
public boolean checkSameArrayAccess(Expr aExpr,Expr bExpr,DependenceData dData,Vector<DependenceData> dataVector){	
 boolean aFlag=false; //TODO:Needs to fix
 //LinkedList aList[]=new LinkedList[dData.getNestingLevel()];
 if(aExpr instanceof ParameterizedExpr){ 	
	Vector<ParameterizedExpr> params=new Vector<ParameterizedExpr>();
	tokenizeExpression(bExpr,params);
	Iterator it =params.iterator();
    while(it.hasNext()){   
    	Expr tExpr=(Expr)it.next();   	    				
    	if(aExpr.getVarName().equals(tExpr.getVarName())){		     	    	  
		   //DependenceData tdData=new DependenceData(); //This might be un-necessary,can use the incoming dData.
	 	   //tdData.setLoopNo(dData.getLoopNo());//Can use the same dData.
	 	   //tdData.setNestingLevel(dData.getNestingLevel());
	 	   //tdData.setStatementAccessed(dData.getStatementAccessed());
	 	   //makeEquationsForSubscriptExprs(aExpr,tExpr,tdData);
    	   //tdData.setArrayAccess(aExpr.getPrettyPrinted()+"	 =	"+tExpr.getPrettyPrinted());
    		prepareDependenceData(aExpr,tExpr,dData,dataVector);
    		//makeEquationsForSubscriptExprs(aExpr,tExpr,dData);	 	    	   
	 	   //if(ApplyTests()){
	 		 //tdData.setDependence('y'); 
	 		 // dData.setDependence('y');
		      //aFlag=true;
		     //dataVector.add(tdData);
		     //dataVector.add(dData);
		     //System.out.println("no of tdData"+tdData.toString());
		  //}
	      //else //tdData.setDependence('n');
	    	//  dData.setDependence('n');
	  }//end of if
    }//end of while
 }//end of if
 return aFlag;						
}//end of function checkSameArrayAccess.


/*
 * ApplyTests function applies appropriate test on the graph for a statement and then prints the results.
 */
public boolean ApplyTests(){
	
 boolean issvpcApplicable,isApplicable,isAcyclicApplicable=false;	
 boolean dFlag=false;
 if(cGraph.getGraphSize()>0){
	//ConstraintsGraph cGraph=cToolBox.getGraph();
	GCDTest gcdTest=new GCDTest();
	gcdTest.calculateGcd(cGraph);
	isApplicable=gcdTest.getIsSolution();
	dFlag=isApplicable;
	if(isApplicable){
	   //BanerjeeTest bTest=new BanerjeeTest(forStmtArray); //this should not be called here.Need to change its location. 
	   // bTest.directionVectorHierarchyDriver(cGraph); // same for this,need to change it.
		SVPCTest svpcTest=new SVPCTest();			 
		issvpcApplicable= svpcTest.checkDependence(cGraph);
		dFlag=issvpcApplicable;
		System.out.println("i am in SVPC test");			
		if (!issvpcApplicable)
		 {
		    System.out.println("Apply Acyclic test");
			AcyclicTest acyclicTest=new AcyclicTest();
			cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
		    isAcyclicApplicable=acyclicTest.getisApplicable();
		    dFlag=isAcyclicApplicable;
			if(isAcyclicApplicable)
			{   System.out.println("now apply SVPC Test");
				dFlag=svpcTest.checkDependence(cGraph);
			}//end of 4th if				
			//else{approximateRanges(cGraph);}
		}//end of 3rd if*/
	  }//end of 2nd if
 }//end of if 1st if*/
 
 return dFlag;
}//end of ApplyTests function

/*
 * This function tokenizes the loop statement and returns a parameterized expression.
 * e.g.f(ii, jj) = f(ii, jj)+mask(ii, jj)*(0.25*(f(ii-1, jj)+f(ii+1, jj)+f(ii, jj-1)+f(ii, jj+1))-f(ii, jj));
 * The function will put the tokens in a vector and will return the vector.
 * 
 */
private void tokenizeExpression(Expr expr, Vector<ParameterizedExpr> result){ 
 if(expr instanceof NameExpr)return; 
 else if(expr instanceof ParameterizedExpr){
	  result.add((ParameterizedExpr)expr);
  }//end of else if  
  else if (expr instanceof BinaryExpr){
	 BinaryExpr mExpr=(BinaryExpr)expr;	 
	 tokenizeExpression(mExpr.getLHS(), result);
	 tokenizeExpression(mExpr.getRHS(), result);
  }//end of else if
}//end of function

public boolean ApplyTests(AffineExpression aExpr,AffineExpression bExpr){	
	 boolean issvpcApplicable,isApplicable=false;	
	 boolean dFlag=false;	 
		//ConstraintsGraph cGraph=cToolBox.getGraph();
	GCDTest gcdTest=new GCDTest();
	gcdTest.calculateGcd(aExpr,bExpr);
	isApplicable=gcdTest.getIsSolution();
	dFlag=isApplicable;
	if(isApplicable){
	  SVPCTest svpcTest=new SVPCTest();			 
	  issvpcApplicable= svpcTest.checkDependence(aExpr,bExpr);
	  dFlag=issvpcApplicable;
	  System.out.println("i am in SVPC test");
   }    
   return dFlag; 
}
		
		
private void setBounds(AffineExpression aExpr,AffineExpression bExpr,DependenceData dData){	
	//LinkedList<PredictedData> tList=pTable.get(dData.getLoopNo());
	//PredictedData pData=(PredictedData)tList.get(0);
	//Data data;
	if(aExpr.getLoopVariable().equals(dData.getLVarName())){
		aExpr.setLBound(dData.getStartRange());  //Setting lower bound
		bExpr.setLBound(dData.getStartRange());
		
		aExpr.setUBound(dData.getEndRange()); //Setting Upper bound.
		bExpr.setUBound(dData.getEndRange());
		boolean flag=ApplyTests(aExpr,bExpr);
		if(flag){
			System.out.println("There is dependence for this equation");
			dData.setDependence("y");		
		}//end of if
		else{ 
			System.out.println("There is no dependence for this equation");
			dData.setDependence("n");
			//return;
	 }//end of else
	}
	else{
	   NestedLoop[] nLoopArray=dData.getNLoopArray();	  
	   for(int i=0;i<nLoopArray.length;i++){		
		if(aExpr.getLoopVariable().equals(nLoopArray[i].getLVarName())){
			aExpr.setLBound(nLoopArray[i].getStartRange());  //Setting lower bound
			bExpr.setLBound(nLoopArray[i].getStartRange());
			
			aExpr.setUBound(nLoopArray[i].getEndRange()); //Setting Upper bound.
			bExpr.setUBound(nLoopArray[i].getEndRange());
			boolean flag=ApplyTests(aExpr,bExpr);
			if(flag){
				System.out.println("There is dependence for this equation");
				dData.setDependence("y");		
			}//end of if
			else{ 
				System.out.println("There is no dependence for this equation");
				dData.setDependence("n");
				//return;
		 }//end of else
	   }//end of if			
	 }//end of for
	}//end of else  
 }//end of setBounds.
		

		
/*This function does following. 
		 * 
		 * 1.Makes equations from array subscript expression.		  
		 * TODO:Handle cases where LHS is not an instance of NameExpr.		 
		 * 
		 */
private void makeEquationsForSubscriptExprs(Expr LHSExpr,Expr RHSExpr,DependenceData dData){
			
  ParameterizedExpr paraLHSExpr=(ParameterizedExpr)LHSExpr;
  //resultArray=new boolean[paraLHSExpr.getNumArg()];   //instantiate a boolean array based on dimensions of array under dependence testing.
  int size=paraLHSExpr.getNumArg();
  int[] array=new int[size];
  int count=0;
  for(int i=0; i<paraLHSExpr.getNumArg();i++)/*Only this one is handled where array indexes are not swapped. a(i,j)=a(i+10,j-11)*/   // To handle multidimensional arrays. //This case is not handled. e.g.a(i,j)=a(j-11,i+10)	  									        			
    {		
		AffineExpression aExpr1=new AffineExpression();
		AffineExpression aExpr2=new AffineExpression();					 
		if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof PlusExpr)
		 {		 
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 aExpr2.setLoopVariable(pExpr.getLHS().getVarName());
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);
			 aExpr1.setIndexExpr(nExpr);
			 //PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setIndexExpr(pExpr.getLHS());
			 aExpr2.setKey("t"+i);
			 if(isRangeInfo()){
				 setBounds(aExpr1,aExpr2,dData);
				 array[count]=aExpr1.getC()-aExpr2.getC();
				 count++;
			 }
			 else{
				 setUpperAndLowerBounds(aExpr1,aExpr2);//TODO:If range data is missing then call this function of upper and lower bounds,in the case where range data is present 
			     if(pExpr.getRHS() instanceof IntLiteralExpr){
			       IntLiteralExpr iExpr=(IntLiteralExpr)pExpr.getRHS();				
				   aExpr2.setC(iExpr.getValue().getValue().intValue());
				   array[count]=aExpr1.getC()-aExpr2.getC();
				   count++;					
				   cGraph.addToGraph(aExpr1,aExpr2);							 
				}//end of nested if
			 }//end of else
		 }//end of main if			
		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MinusExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 MinusExpr mExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr.getLHS().getVarName());
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(nExpr);
			 //MinusExpr mExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr.getLHS());
			 if(isRangeInfo()){
				 setBounds(aExpr1,aExpr2,dData);
				 array[count]=aExpr1.getC()+aExpr2.getC();
				 count++;
			  }			 
			 else{
				 setUpperAndLowerBounds(aExpr1,aExpr2);			 
				 if(mExpr.getRHS() instanceof IntLiteralExpr){
					 IntLiteralExpr iExpr=(IntLiteralExpr)mExpr.getRHS();				
					 aExpr2.setC((iExpr.getValue().getValue().intValue()));
					 array[count]=aExpr1.getC()+aExpr2.getC();
					 count++;
					 //System.out.println("MinusExpr  "+aExpr2.getC());
					 cGraph.addToGraph(aExpr1,aExpr2);
				 }//end of nested if	
			  }//end of else
		   }//end of main else if
		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof NameExpr){
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);						 
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 NameExpr nExpr1=(NameExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(nExpr1.getVarName());
			 aExpr1.setC(0);			
			 aExpr1.setKey("t"+i);
			 aExpr1.setIndexExpr(nExpr);
			 //System.out.println(aExpr1.getKey());
			 aExpr2.setC(0);
			 array[count]=0;
			 count++;
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(((ParameterizedExpr)RHSExpr).getArg(i));
			 if(isRangeInfo())setBounds(aExpr1,aExpr2,dData);
			 else setUpperAndLowerBounds(aExpr1, aExpr2);
			 cGraph.addToGraph(aExpr1,aExpr2);						 
		  }//end of main else if	
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MTimesExpr){
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());						 
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 MTimesExpr mExpr1=(MTimesExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr1.getRHS().getVarName());
		     aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr1);
			 if(isRangeInfo()){
				 setBounds(aExpr1,aExpr2,dData);
				 array[count]=0;
				 count++;		
			 }
			 else{
				 setUpperAndLowerBounds(aExpr1,aExpr2);						 				
			     aExpr2.setC(0);
			     array[count]=0;
			     count++;						
			     cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of else
		 }//end of main else if
		
		/*
		 * TODO:Look into this case 2i=i+10
		 */			 
		/* else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof PlusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());						 
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(pExpr.getLHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(pExpr.getLHS());
				 //if(pExpr.getLHS() instanceof MTimesExpr)aExpr2.setIndexExpr((MTimesExpr)pExpr.getLHS());//for these equations 2i=2j+10
						 //else if(pExpr.getLHS() instanceof NameExpr)aExpr2.setIndexExpr((NameExpr)pExpr.getLHS());//for these equations 2i=j+10
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(pExpr.getRHS() instanceof IntLiteralExpr)			
			 {
				IntLiteralExpr iExpr=(IntLiteralExpr)pExpr.getRHS();				
				aExpr2.setC(iExpr.getValue().getValue().intValue());
				array[count]=iExpr.getValue().getValue().intValue();
				count++;
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MinusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 MinusExpr miExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(miExpr.getLHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(miExpr.getLHS());
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(miExpr.getRHS() instanceof IntLiteralExpr)			
			 {
				IntLiteralExpr iExpr=(IntLiteralExpr)miExpr.getRHS();				
				aExpr2.setC((iExpr.getValue().getValue().intValue())*-1);
				array[count]=(iExpr.getValue().getValue().intValue())*-1;
				count++;
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof NameExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 NameExpr nExpr=(NameExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(nExpr.getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(nExpr);
			 setUpperAndLowerBounds(aExpr1,aExpr2);
				 //if(miExpr.getRHS() instanceof IntLiteralExpr)			
						 //{
							//IntLiteralExpr iExpr=(IntLiteralExpr)miExpr.getRHS();				
			aExpr2.setC(0);
			array[count]=0;
			count++;
			cGraph.addToGraph(aExpr1,aExpr2);
						 //}//end of nested if						 
		 }//end of main else if

		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MTimesExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(nExpr);
			 MTimesExpr mExpr=(MTimesExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr);
		     setUpperAndLowerBounds(aExpr1,aExpr2);						 				
			 aExpr2.setC(0);
			 array[count]=0;
			 count++;
			 cGraph.addToGraph(aExpr1,aExpr2);												 
		}//end of else if*/		
	}//end of for			
  dData.setDistanceArray(array);
}//end of function makeEquationsForSubscriptExprs
		
									
/*
 * 		 * This function sets the upper and lower bounds of the affine expression based on the loop which bounds those expressions.
		 * e.g. for int i=1:1:10
		 * 			for int j=1:1:20
		 *  			a(i,j)=a(i-10,j+11)
		 *  		end
		 *  	end
		 *  TODO:Look into this case a(i,j)=a(j-10,i+11).Consult notebook on which you have solved it.
*/							
private void setUpperAndLowerBounds(AffineExpression aExpr1 , AffineExpression aExpr2)
{	
	int upperBound=0;
	int lowerBound=0;
	for(int i=0;i<loopIndex;i++)
 	{
    	AssignStmt assStmt= forStmtArray.get(i).getAssignStmt();					
		if(assStmt.getVarName().equals(aExpr1.getLoopVariable())) //this is to compare array subscript with loop index variable.
		{
	      if(assStmt.getRHS() instanceof RangeExpr)		 		
		   {
			 RangeExpr rExpr=(RangeExpr) assStmt.getRHS();						
			 if(rExpr.getUpper() instanceof IntLiteralExpr && rExpr.getLower() instanceof IntLiteralExpr)
			  {
				System.out.println("I am a constraint bounded on both sides by variables of IntLiteralExpression");
				IntLiteralExpr iExprUpper=(IntLiteralExpr) rExpr.getUpper();
							//upperBound=iExprUpper.getValue().getValue().intValue();
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
							//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setUpperBound(iExprUpper);
				aExpr1.setLowerBound(iExprLower);
				aExpr2.setUpperBound(iExprUpper);
				aExpr2.setLowerBound(iExprLower);
			}//end of 3rd if
			else if(rExpr.getUpper() instanceof PlusExpr)
			 {
				System.out.println("I am a constraint bounded on both sides by variables of PlusExpr");
				PlusExpr pExpr=(PlusExpr)rExpr.getUpper();
				aExpr1.setUpperBound(pExpr);
				aExpr2.setUpperBound(pExpr);
				//aExpr1.setPUBound(pExpr);
				//aExpr2.setPUBound(pExpr);
				if(rExpr.getLower() instanceof NameExpr)
			 	{  NameExpr nExpr=(NameExpr)rExpr.getLower();
					//aExpr1.setNLBound(nExpr);
					//aExpr2.setNLBound(nExpr);
					aExpr1.setLowerBound(nExpr);
					aExpr2.setLowerBound(nExpr);
				}//end of if
				else if(rExpr.getLower() instanceof IntLiteralExpr)
				{	IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
					//lowerBound=iExprLower.getValue().getValue().intValue();
					aExpr1.setLowerBound(iExprLower);									
					aExpr2.setLowerBound(iExprLower);
				}
				else if(rExpr.getLower() instanceof PlusExpr)
				 {
					PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
					aExpr1.setLowerBound(pLExpr);
					aExpr2.setLowerBound(pLExpr);									
				}	
				else if(rExpr.getLower() instanceof MinusExpr)
				 {
					MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
					aExpr1.setLowerBound(mLExpr);
					aExpr2.setLowerBound(mLExpr);
				}	
				else if(rExpr.getLower() instanceof MTimesExpr)
				{
					MTimesExpr mExpr=(MTimesExpr)rExpr.getLower();
					aExpr1.setLowerBound(mExpr);
					aExpr2.setLowerBound(mExpr);
				}
			}//end of else if
			else if(rExpr.getUpper() instanceof MinusExpr)
			 {
				System.out.println("I am a constraint bounded on both sides by variables");
				MinusExpr mExpr=(MinusExpr)rExpr.getUpper();
				aExpr1.setUpperBound(mExpr);
				aExpr2.setUpperBound(mExpr);
				if(rExpr.getLower() instanceof NameExpr)
				{
					NameExpr nExpr=(NameExpr)rExpr.getLower();
					aExpr1.setLowerBound(nExpr);
					aExpr2.setLowerBound(nExpr);
				}
				else if(rExpr.getLower() instanceof IntLiteralExpr)
				 {
					IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
								//lowerBound=iExprLower.getValue().getValue().intValue();
					aExpr1.setLowerBound(iExprLower);									
					aExpr2.setLowerBound(iExprLower);
				}
				else if(rExpr.getLower() instanceof PlusExpr)
				{
					PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
					aExpr1.setLowerBound(pLExpr);
					aExpr2.setLowerBound(pLExpr);									
				}	
				else if(rExpr.getLower() instanceof MinusExpr)
				{
					MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
					aExpr1.setLowerBound(mLExpr);
					aExpr2.setLowerBound(mLExpr);
				}
				else if(rExpr.getLower() instanceof MTimesExpr)
				{
					MTimesExpr muExpr=(MTimesExpr)rExpr.getLower();
					aExpr1.setLowerBound(muExpr);
					aExpr2.setLowerBound(muExpr);
				}							
		    }//end of else if
		 else if(rExpr.getUpper() instanceof MTimesExpr)
		  {
			 System.out.println("I am a constraint bounded on both sides by variables");
			 MTimesExpr muExpr=(MTimesExpr)rExpr.getUpper();
			 aExpr1.setUpperBound(muExpr);
			 aExpr2.setUpperBound(muExpr);
			if(rExpr.getLower() instanceof NameExpr)
			{
			    NameExpr nExpr=(NameExpr)rExpr.getLower();
				aExpr1.setLowerBound(nExpr);
				aExpr2.setLowerBound(nExpr);
			}
			else if(rExpr.getLower() instanceof IntLiteralExpr)
			{
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
				//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setLowerBound(iExprLower);									
				aExpr2.setLowerBound(iExprLower);
			}
			else if(rExpr.getLower() instanceof PlusExpr)
			{
				PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
				aExpr1.setLowerBound(pLExpr);
				aExpr2.setLowerBound(pLExpr);									
			}	
			else if(rExpr.getLower() instanceof MinusExpr)
			{
				MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
				aExpr1.setLowerBound(mLExpr);
				aExpr2.setLowerBound(mLExpr);
			}
			else if(rExpr.getLower() instanceof MTimesExpr)
			{
				MTimesExpr mulExpr=(MTimesExpr)rExpr.getLower();
				aExpr1.setLowerBound(mulExpr);
				aExpr2.setLowerBound(mulExpr);
			}							
		}//end of else if						
		else if(rExpr.getUpper() instanceof NameExpr)
		  {
			System.out.println("I am a constraint bounded on both sides by variables");
			NameExpr nExpr1=(NameExpr)rExpr.getUpper();
			aExpr1.setUpperBound(nExpr1);
			aExpr2.setUpperBound(nExpr1);
			if(rExpr.getLower() instanceof NameExpr)
			{
				NameExpr nExpr2=(NameExpr)rExpr.getLower();
				aExpr1.setLowerBound(nExpr2);
				aExpr2.setLowerBound(nExpr2);
		    }
			else if(rExpr.getLower() instanceof IntLiteralExpr)
			{
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
				//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setLowerBound(iExprLower);									
				aExpr2.setLowerBound(iExprLower);
			}
			else if(rExpr.getLower() instanceof PlusExpr)
			{
				PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
				aExpr1.setLowerBound(pLExpr);
			    aExpr2.setLowerBound(pLExpr);									
			}	
			else if(rExpr.getLower() instanceof MinusExpr)
			{
				MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
				aExpr1.setLowerBound(mLExpr);
				aExpr2.setLowerBound(mLExpr);
			}
			else if(rExpr.getLower() instanceof MTimesExpr)
			{
				MTimesExpr mulExpr=(MTimesExpr)rExpr.getLower();
				aExpr1.setLowerBound(mulExpr);
				aExpr2.setLowerBound(mulExpr);
			}							
		}//end of else if
	  }//end of 2nd if	
	}//end of 1st if
  }//end of for loop		
}//end of setUpperAndLowerBounds function.







}//end of class
