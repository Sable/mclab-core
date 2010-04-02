package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ast.ParameterizedExpr;
import ast.Stmt;
import ast.ForStmt;
import ast.AssignStmt;
import ast.IntLiteralExpr;

/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 * 
 */

public class DependenceAnalysisDriver
{
	private ForStmt forNode;
	//private ConstraintsGraph cGraph;
	private ForStmt forStmtArray[];
	private SVPCTest svpcTest;
	private AcyclicTest acyclicTest;
	private GCDTest gcdTest;
	private int loopIndex;
	private ConstraintsToolBox cToolBox;
	private int loopNumber;
	
public DependenceAnalysisDriver(ForStmt fNode)	
{	
	forNode=fNode;	
	forStmtArray=new ForStmt[forNode.getNumChild()+1];
	loopIndex=0;
	forStmtArray[loopIndex]=forNode;
	System.out.println(forNode.getNumChild());
}
	/*
	 * This function does the following 
	 * 1.Checks for tightly nested loops.
	 */	
private void isTightlyNestedLoop(ForStmt forStmt)
{
			 
  Stmt stmt=forStmt.getStmt(0);
  if(stmt instanceof ForStmt)
  {		  loopIndex++;			  
		  ForStmt tForStmt=(ForStmt)stmt;
		  forStmtArray[loopIndex]=tForStmt;
		  forNode=tForStmt;				  
		  isTightlyNestedLoop(tForStmt);				  
  }//end of if
			
}//end of function
    

	/*
	 * This function does the following.
	 * 1.This function traverses each statement of forLoop.It starts from the second statement as the first statement of every loop is lNum=/*loopNumber
	 * 2.Compares the RHS and LHS of the statement,creates constraints.	   
	 * 3.Compares each statement of the loop with other statement for same array access. 
	 * 4.For each comparison(whether RHS and LHS of the same statement of different statement) it calls ApplyTests function.
	 * 5.Constraints are created per statement. 
	 * e.g. 1:a(i)=a(i)+c(i)
	 * 		2:d(i)=a(i)
	 */
//TODO:Set start and end Range once the heuristic engine is incorporated. 
public void traverseLoopStatements()
{
//	File file;
	//RandomAccessFile raf=null;
	//try {file = new File("Results.txt");
      //  raf = new RandomAccessFile(file, "rw");     
    //}catch (IOException e) 
    //{  System.out.println("IOException:Couldnot open the new file");
      //  e.printStackTrace();
    // }//end of catch
	
	isTightlyNestedLoop(forNode);
	Vector<DependenceData> dataVector=new Vector<DependenceData>();
	DependenceData dData=null;
	Stmt s=forNode.getStmt(0);//TODO:put a check to see if first statement is the loop number statement or not.
    AssignStmt aStmt=(AssignStmt)s;    
    if((aStmt.getRHS()) instanceof IntLiteralExpr){
       loopNumber=((IntLiteralExpr)aStmt.getRHS()).getValue().getValue().intValue();       
    }
	int nStmts=forNode.getNumStmt();
	cToolBox=new ConstraintsToolBox(loopIndex+1,forStmtArray);
	System.out.println("No of Statements"+nStmts);
	boolean aFlag=false;
	for(int i=1;i<nStmts;i++) //not to include first statement of the loop which is lNum.
	{
	  s=forNode.getStmt(i);
	  if(s instanceof AssignStmt)
	  {
		AssignStmt aStmt1=(AssignStmt)s;		
		for(int j=i;j<nStmts;j++)
		{  
		   dData=new DependenceData();//This DS would be written to dependenceFile.xml
		   dData.setLoopNo(loopNumber);
		   dData.setNestingLevel(loopIndex);
	       if(i==j)
		   {  System.out.println("i am in same statements block");
    		//try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getPrettyPrinted() +'\n');	     
	 	    //}catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
	 	    
			  aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS(),dData);//compare LHS(sameStmt) with RHS(sameStmt)						  
			//if(aFlag){ApplyTests(cToolBox,file);}		  
		   }//end of if
		  else
		   {Stmt bStmt=forNode.getStmt(j);
		    if(bStmt instanceof AssignStmt)  //TODO:Needs to handle when there is an if statement in the loop.
		    {  System.out.println("i am in different statements block");
			   AssignStmt aStmt2=(AssignStmt)bStmt;
			   System.out.println(aStmt1.getPrettyPrinted());
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS(),dData); //compare LHS(previousStmt) with RHS(nextStmt)
			   //try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getLHS().getPrettyPrinted()+ "  " + aStmt2.getRHS().getPrettyPrinted() +'\n');	     
		 	   // }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch		 	    
			   //if(aFlag){ApplyTests(cToolBox,file);}
			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS(),dData);//compare LHS(previousStmt) with LHS(nextStmt)
			   //try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getLHS().getPrettyPrinted()+ "  " + aStmt2.getLHS().getPrettyPrinted() +'\n');	     
		 	    //}catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
			   //if(aFlag){ApplyTests(cToolBox,file);}
			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS(),dData);//compare RHS(previousStmt) with LHS(nextStmt)
			   //System.out.println(aStmt1.getRHS().getPrettyPrinted());
			   //System.out.println(aStmt2.getLHS().getPrettyPrinted());
			   //try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getRHS().getPrettyPrinted()+ "  " + aStmt2.getRHS().getPrettyPrinted() +'\n');	     
		 	    //}catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
		 	   //if(aFlag){ApplyTests(cToolBox,file);}							  
			 }//end of if
			}//end of else
	       if(aFlag){dataVector.add(dData);}
		}//end of inner for loop		
	}//end of if	
  }//end of for 
 	
 if(dataVector.size()!=0){writeToXMLFile(dataVector);}
  //try{raf.close();}catch(IOException e){System.out.println("IOException:Couldnot close the file");}	
}//end of traverseLoopStatements function

private void writeToXMLFile(Vector<DependenceData> dataVector){
	
	File file = new File("dependenceFile.xml");//TODO:Name should be the same as that of the input .mFile
	System.out.println("i am in xmlWrite");
	String value="";
	try 
	{   boolean exists = file.exists();	    
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document;
        if(!exists) //if file doesnot exist.
        {
        	document = documentBuilder.newDocument();
            Element rootElement = document.createElement("AD"); // creates a element
            Element loopElement = document.createElement("LoopNo"); //create another element
            loopElement.setAttribute(new String("Number"), Integer.toString(loopNumber));            
            loopElement.setAttribute(new String("NestingLevel"), Integer.toString(loopIndex));
            Iterator it=dataVector.iterator();            
            while(it.hasNext()){
            	DependenceData data=(DependenceData)it.next();
            	Element arrayAccessElement = document.createElement("ArrayAccess");
            	arrayAccessElement.setAttribute(new String("access"),data.getArrayAccess() );
            	int[] array=data.getDistanceArray();
            	value="";
            	for(int j=0;j<array.length;j++){
            		value+=array[j]+",";
            		System.out.println("Value"+value+array.length);
            	}
            	arrayAccessElement.setAttribute(new String("DistanceValue"),value);
            	loopElement.appendChild(arrayAccessElement); // add element1 under loopElement
            }                        
            rootElement.appendChild(loopElement); // add element1 under rootElement            
	        document.appendChild(rootElement); // add the rootElement to the document
        }//end of if
        else//if file exist
        {
       	 document = documentBuilder.parse(file);
         Element rootElement = document.getDocumentElement();
         Element loopElement = document.createElement("LoopNo"); //create another element
         loopElement.setAttribute(new String("Number"), Integer.toString(loopNumber));            
         loopElement.setAttribute(new String("NestingLevel"), Integer.toString(loopIndex));
         Iterator it=dataVector.iterator();            
         while(it.hasNext()){
         	DependenceData data=(DependenceData)it.next();
         	Element arrayAccessElement = document.createElement("ArrayAccess");
         	arrayAccessElement.setAttribute(new String("access"),data.getArrayAccess() );
         	int[] array=data.getDistanceArray();
         	value="";
         	for(int j=0;j<array.length;j++){
         		value=array[j]+",";
         	}
         	arrayAccessElement.setAttribute(new String("DistanceValue"),value);
         	loopElement.appendChild(arrayAccessElement); // add element1 under loopElement
         }                        
         rootElement.appendChild(loopElement); // add element1 under rootElement            
	     document.appendChild(rootElement); // add the rootElement to the document
         
       }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
     } catch (Exception e) {System.out.println(e.getMessage());}   
	
}
	
	/*
	 * ApplyTests function applies appropriate test on the graph for a statement and then prints the results.
	 */
/*public void ApplyTests(ConstraintsToolBox cToolBox,File f)
{	boolean issvpcApplicable,isApplicable,isAcyclicApplicable=false;		
	if(cToolBox.getGraph()!=null)
	{
		ConstraintsGraph cGraph=cToolBox.getGraph();
		gcdTest=new GCDTest(f);
		gcdTest.calculateGcd(cGraph);
		isApplicable=gcdTest.getIsSolution();
		if(isApplicable)
		{
		   //BanerjeeTest bTest=new BanerjeeTest(forStmtArray); //this should not be called here.Need to change its location. 
		   // bTest.directionVectorHierarchyDriver(cGraph); // same for this,need to change it.
			svpcTest=new SVPCTest(f);			 
			issvpcApplicable= svpcTest.checkDependence(cGraph);			
			System.out.println("i am in SVPC test");			
			if (!issvpcApplicable)
			 {
			    System.out.println("Apply Acyclic test");
				acyclicTest=new AcyclicTest(f);
				cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
			    isAcyclicApplicable=acyclicTest.getisApplicable();
				if(isAcyclicApplicable)
				{   System.out.println("now apply SVPC Test");
					svpcTest.checkDependence(cGraph);
				}//end of 4th if				
				//else{approximateRanges(cGraph);}
			}//end of 3rd if
		  }//end of 2nd if
	 }//end of if 1st if
 }//end of ApplyTests function


/*
 * This function does the following
 * 1.This function calculates the results for different ranges of variables involved in loop bounds.
 * 2.Creates mapping for the loopRanges and Result's file.
 * 3.Then applies the tests on the new Ranges. 
 */
/*public void approximateRanges(ConstraintsGraph cGraph)
 {
	RangeApproximation rApprox=new RangeApproximation();
	int lRange=0,uRange=0;
	File file;
	RandomAccessFile raf=null;
	for(int i=0;i<260;i+=10)
	{ lRange=i+1;
	  uRange=i+10;
	  rApprox.substituteVariablesWithConstantValues(cGraph,lRange,uRange);
	  try {
	       file = new File("Range"+lRange+"-"+uRange+".txt");
           raf = new RandomAccessFile(file, "rw");
           raf.writeBytes("Applying Tests for the following range"+ lRange + "-" + uRange +'\n');
      }catch (IOException e) 
      {
      	System.out.println("IOException:Couldnot open the new file");
          e.printStackTrace();
       }//end of catch
      LoopBounds lBounds=new LoopBounds();
      lBounds.setLowerBound(lRange);
      lBounds.setUpperBound(uRange);
      RangeMap rMap=new RangeMap();
      rMap.createMapping(lBounds, "Range"+lRange+"-"+uRange+".txt");
	  ApplyTests(cToolBox,file);
	  try{
		  raf.close();
	  }catch(IOException e){System.out.println("IOException:Couldnot close the file");}
   }//end of for
 }//end of function call.*/

}
