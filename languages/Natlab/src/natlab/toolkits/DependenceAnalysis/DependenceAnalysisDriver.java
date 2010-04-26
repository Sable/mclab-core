package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import natlab.toolkits.analysis.ForVisitor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ast.ASTNode;
import ast.IfStmt;
import ast.Program;
import ast.Stmt;
import ast.ForStmt;
import ast.AssignStmt;
import ast.IntLiteralExpr;
import ast.SwitchStmt;


/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 * 
 */

public class DependenceAnalysisDriver extends ForVisitor
{
	private ForStmt forNode;
	
	//private ForStmt forStmtArray[];
	private LinkedList<ForStmt> forStmtArray=new LinkedList<ForStmt>();
	private int loopIndex;
	private ConstraintsToolBox cToolBox;
	private int loopNumber;
	private String fileName;
	private String dirName;
	
/*public DependenceAnalysisDriver(ForStmt fNode)	
{	
	forNode=fNode;	
	forStmtArray=new ForStmt[forNode.getNumChild()+1];
	loopIndex=0;
	forStmtArray[loopIndex]=forNode;
	//System.out.println(forNode.getNumChild());
}*/
	
public DependenceAnalysisDriver(){	
		cToolBox=new ConstraintsToolBox();
}

/*
 * This function does the following.
 * 1.It sets the fileName and fileName is that of a file which the output from Profiler component.
 * 
 */
public void setFileName(String fName){
	fileName="Profiled";
	StringTokenizer st = new StringTokenizer(fName,".");
	dirName=st.nextToken();
	fileName+=dirName+".m";
	//System.out.println("File for heuristic Engine is "+fileName);
}

public String getFileName() {	
	return dirName+"/"+fileName;
}

public void traverseFile(Program prog){
    prog.apply(this);    
}

public void caseLoopStmt(ASTNode node){
	
	if (node instanceof ForStmt){
		System.out.println("Dependence Analyzer caseLoopStmt is called by "+ node.getClass().getName());			
		ForStmt fNode=(ForStmt) node;
		forNode=fNode;
		traverseLoopStatements();
	}
	else{
		//ForVisitor fVisitor=new ForVisitor(); 
		//node.applyAllChild(fVisitor);
		node.applyAllChild(this);
	}
}

public void caseIfStmt(IfStmt node){
	System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
	IfStmt ifNode=(IfStmt) node;
	//ForVisitor fVisitor=new ForVisitor(); 
	//ifNode.applyAllChild(fVisitor);
}

public void caseSwitchStmt(SwitchStmt node){
	System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
	SwitchStmt sNode=(SwitchStmt)node;
	//ForVisitor fVisitor=new ForVisitor(); 
	//sNode.applyAllChild(fVisitor);
}
public void caseBranchingStmt(ASTNode node){}

public void caseASTNode(ASTNode node) {}

public void setPredictedLoopValues(Hashtable<String,LinkedList<PredictedData>> table){
		cToolBox.setPTable(table);
}


/*
* This function does the following 
* 1.Checks for tightly nested loops.
*/	
private void isTightlyNestedLoop(ForStmt forStmt){
			 
  Stmt stmt=forStmt.getStmt(1);
  if(stmt instanceof ForStmt){      			  
	  ForStmt tForStmt=(ForStmt)stmt;
	  //forStmtArray[loopIndex]=tForStmt;
	  forStmtArray.add(tForStmt);
	  forNode=tForStmt;				  
	  loopIndex++;
	  isTightlyNestedLoop(tForStmt);				  
  }//end of if
			
}//end of function

 private DependenceData prepareDependenceData(){
	 DependenceData dData=new DependenceData();//This DS would be written to dependenceFile.xml
	 dData.setLoopNo(loopNumber);
	 dData.setNestingLevel(loopIndex);	 
	 return dData;  
 }

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
	isTightlyNestedLoop(forNode);
	Vector<DependenceData> dataVector=new Vector<DependenceData>();
	//DependenceData dData=null;
	Stmt s=forNode.getStmt(0);
    if(s instanceof AssignStmt){AssignStmt aStmt=(AssignStmt)s;        
    	if((aStmt.getRHS()) instanceof IntLiteralExpr){
    		loopNumber=((IntLiteralExpr)aStmt.getRHS()).getValue().getValue().intValue();}
    }//end of if
	int nStmts=forNode.getNumStmt();
	//cToolBox=new ConstraintsToolBox(loopIndex+1,forStmtArray);
	
	cToolBox.setLoopIndex(loopIndex);
	cToolBox.setForStmtArray(forStmtArray);
	//cToolBox.setLoopNo(loopNumber);
	
	System.out.println("No of Statements"+nStmts);
	boolean aFlag=false;
	for(int i=1;i<nStmts;i++) //not to include first statement of the loop which is lNum.
	{
	  s=forNode.getStmt(i);
	  if(s instanceof AssignStmt)
	  {
		AssignStmt aStmt1=(AssignStmt)s;		
		for(int j=i;j<nStmts;j++){  
		   DependenceData dData=prepareDependenceData();
		   System.out.println("loop Index"+loopIndex);		   
		   dData.setStatementAccessed("S"+i+":"+"S"+j);
	       if(i==j){ 
	    	   System.out.println("i am in same statements block"); 	    
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS(),dData,dataVector);//compare LHS(sameStmt) with RHS(sameStmt)
			 }//end of if
		  else
		   {Stmt bStmt=forNode.getStmt(j);
		    if(bStmt instanceof AssignStmt)  //TODO:Needs to handle when there is an if statement in the loop.
		    {  System.out.println("i am in different statements block");
			   AssignStmt aStmt2=(AssignStmt)bStmt;
			   //System.out.println(aStmt1.getPrettyPrinted());
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS(),dData,dataVector); //compare LHS(previousStmt) with RHS(nextStmt)			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS(),dData,dataVector);//compare LHS(previousStmt) with LHS(nextStmt)			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS(),dData,dataVector);//compare RHS(previousStmt) with LHS(nextStmt)			   		  
			 }//end of if
			}//end of else
	       //if(aFlag){dataVector.add(dData);}
		}//end of inner for loop		
	}//end of if	
  }//end of for 
 	
 //if(dataVector.size()!=0){
	 writeToXMLFile(dataVector);//}
}//end of traverseLoopStatements function


/*
 * 
 */

private void writeToXMLFile(Vector<DependenceData> dataVector){
	
	File file = new File(dirName+"/"+"dependence"+dirName+".xml");	
	System.out.println("i am in xmlwrite"+file.getName());
	String value="";
	try{  
		boolean exists = file.exists();	    
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document;
        Element rootElement;
        Node rootNode;
        if(!exists){ //if file doesnot exist.        
         	document = documentBuilder.newDocument();         	         	
        }
        else{
        	document = documentBuilder.parse(file);          	 
          	document.normalizeDocument();                    	
        }   	
            
	    Element loopElement = document.createElement("LoopNo"); //create another element
	    loopElement.setAttribute(new String("LoopNumber"), Integer.toString(loopNumber));            
	    loopElement.setAttribute(new String("NestingLevel"), Integer.toString(loopIndex));
	    Iterator<DependenceData> it=dataVector.iterator();            
	    while(it.hasNext()){
	    	DependenceData data=it.next();
	    	
	    	Element rangeElement = document.createElement("Range"); //create another element
	        rangeElement.setAttribute(new String("Start"), Integer.toString(data.getStartRange()));            
	        rangeElement.setAttribute(new String("End"), Integer.toString(data.getEndRange()));
	        rangeElement.setAttribute(new String("Dependence"),data.getDependence());
	        for(int i=0;i<data.getNLoopArray().length;i++){
	        	NestedLoop[] nLoop=data.getNLoopArray();
	        	
	        	Element nestedLoopElement = document.createElement("NestedLoop"); //create another element
	            nestedLoopElement.setAttribute(new String("Number"), Float.toString(nLoop[i].getLoopNo()));
	            
	            Element nLRangeElement = document.createElement("Range"); //create another element
	            nLRangeElement.setAttribute(new String("Start"), Float.toString(nLoop[i].getStartRange()));
	            nLRangeElement.setAttribute(new String("End"), Float.toString(nLoop[i].getEndRange()));
	            nLRangeElement.setAttribute(new String("Dependence"),data.getDependence());
	            
	            Element arrayAccessElement = document.createElement("LoopStmts");            	
	        	arrayAccessElement.setAttribute(new String("StmtNumbers"),data.getStatementAccessed() );
	        	arrayAccessElement.setAttribute(new String("Access"),data.getArrayAccess() );
	        	int[] array=data.getDistanceArray();
	        	value="";
	        	for(int j=0;j<array.length;j++){
	        		if(j<array.length-1) value+=array[j]+",";
	        		else value+=array[j];
	        		//System.out.println("Value"+value+array.length);
	        	}
	        	arrayAccessElement.setAttribute(new String("DistanceVector"),value);
	 
	            nLRangeElement.appendChild(arrayAccessElement);
	            nestedLoopElement.appendChild(nLRangeElement);
	            rangeElement.appendChild(nestedLoopElement); // add element1 under loopElement
	        }    	
	    	
	    	loopElement.appendChild(rangeElement); // add element1 under loopElement
	    }
	    
	    if(!exists){
	    	rootElement = document.createElement("AD"); // creates a element
	    	rootElement.appendChild(loopElement); // add element1 under rootElement            
		    document.appendChild(rootElement); // add the rootElement to the document	
	    }
	    else{
	    	rootNode = document.getDocumentElement();
	    	rootNode.appendChild(loopElement); // add element1 under rootElement
	    }
	           
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(file);
	    transformer.transform(source, result);
	 } catch (Exception e) {System.out.println(e.getMessage());}   

}//end of function writeToXMLFile 


     /*   else//if file exist
        {          
       	 document = documentBuilder.parse(file);
       	 
       	 document.normalizeDocument();
         Node rootElement = document.getDocumentElement();         
         Element loopElement = document.createElement("LoopNo"); //create another element         
         loopElement.setAttribute(new String("LoopNumber"), Integer.toString(loopNumber));            
         loopElement.setAttribute(new String("NestingLevel"), Integer.toString(loopIndex+1));
         Iterator it=dataVector.iterator();            
         while(it.hasNext()){
         	DependenceData data=(DependenceData)it.next();
         	Element arrayAccessElement = document.createElement("LoopStmts");
         	arrayAccessElement.setAttribute(new String("StmtNumbers"),data.getStatementAccessed() );
         	arrayAccessElement.setAttribute(new String("access"),data.getArrayAccess() );
         	int[] array=data.getDistanceArray();
         	value="";
         	for(int j=0;j<array.length;j++){
         		if(j<array.length-1) value+=array[j]+",";
        		else value+=array[j];
         	}
         	arrayAccessElement.setAttribute(new String("DistanceVector"),value);
         	loopElement.appendChild(arrayAccessElement); // add element1 under loopElement
         }                        
         rootElement.appendChild(loopElement); // add element1 under rootElement            
	     //document.appendChild(rootElement); // add the rootElement to the document
         
       }//end of else      
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
