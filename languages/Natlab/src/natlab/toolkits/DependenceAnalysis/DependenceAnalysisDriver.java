package natlab.toolkits.DependenceAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import ast.ElseBlock;
import ast.FPLiteralExpr;
import ast.IfBlock;
import ast.IfStmt;
import ast.List;
import ast.NameExpr;
import ast.Program;
import ast.Stmt;
import ast.ForStmt;
import ast.AssignStmt;
import ast.IntLiteralExpr;
import ast.StringLiteralExpr;
import ast.SwitchStmt;


/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 * 
 */

public class DependenceAnalysisDriver extends ForVisitor{
	
	private ForStmt forNode;	
	private LinkedList<ForStmt> forStmtArray;
	private int loopIndex;
	private ConstraintsToolBox cToolBox;
	private float loopNumber;
	private String fileName;
	private String dirName;
	private boolean rangeInfo=false;
	private int index=0;	
	private Program program;
	private int childNo=0;
	private boolean isRepeat=false;
	private int lId=0;
	private boolean aTrans=false;
	private LegalityTest lTest;
	private String transformation="";
	private int maxLoop;

	
public int getMaxLoop() {
		return maxLoop;
	}

	public void setMaxLoop(int maxLoop) {
		this.maxLoop = maxLoop;
	}

public DependenceAnalysisDriver(){	
		cToolBox=new ConstraintsToolBox();
}

/*
 * This function does the following.
 * 1.It sets the fileName and fileName is that of a file which is the output from Profiler component.
 * 
 */
public void setFileName(String fName){
	fileName="Profiled";	
	fileName+=fName+".m";	
}

public String getFileName() {	
	return dirName+"/"+fileName;
}

/*
 * This function traverses the input file for loops. This function is called with 
 * auto mode.
 * 
 */
public void traverseFile(Program prog,int no,String trans){	
  program=prog;
  lId=no;
  transformation=trans;
  program.apply(this);    
}


/*
 * This function traverses the input file for loops. This function is called with 
 * anno mode.
 * 
 */
public void traverseFile(Program prog){	
	  program=prog;	  
	  program.apply(this);    
}


public void caseLoopStmt(ASTNode node){
	
	if (node instanceof ForStmt){
		isRepeat=false;
		ASTNode n=((ForStmt)node).getParent();
		int a=n.getIndexOfChild(((ForStmt)node));
		//if(childNo==a){
			//isRepeat=true;
		//}		
		//if(!isRepeat){		  
		  forStmtArray=new LinkedList<ForStmt>();
	      ForStmt fNode=(ForStmt) node;
		  forNode=fNode;
		  loopIndex=0;	
		  lTest=new LegalityTest();
		  traverseLoopStatements();
		  childNo=lTest.getChildNo();	 
		//}
	}
	else{		
		node.applyAllChild(this);
	}
}

public void caseIfStmt(IfStmt node){
	
}

public void caseSwitchStmt(SwitchStmt node){	
	SwitchStmt sNode=(SwitchStmt)node;	
}
public void caseBranchingStmt(ASTNode node){}

public void caseASTNode(ASTNode node) {}

public void setPredictedLoopValues(Hashtable<String,LinkedList<PredictedData>> table){
		cToolBox.setPTable(table);
		this.setRangeInfo(true);//This is the case when range info is present.		
}

/*
* This function does the following 
* 
* 1.Checks for tightly nested loops.
* 
*/	
private void isTightlyNestedLoop(ForStmt forStmt){
 if(forStmt!=null){		   
	 for(int j=0;j<forStmt.getNumStmt();j++){  
		Stmt s=forStmt.getStmt(j);		
	    if(s instanceof AssignStmt){
	    	AssignStmt aStmt=(AssignStmt)s;	    	
	    	if((aStmt.getRHS()) instanceof FPLiteralExpr && (aStmt.getLHS()) instanceof NameExpr){
	    		NameExpr str=(NameExpr)aStmt.getLHS();
	    		if(str.getVarName().equals("lNum")){
	    		  loopNumber=((FPLiteralExpr)aStmt.getRHS()).getValue().getValue().floatValue();
	    		  index=j;	    		  
	    		  forStmtArray.add(forStmt);
	    		}
	        }//end of 3rd if    	
	     }//end of 2nd if
	    else if(s instanceof ForStmt){	    	
	      forStmtArray.add(forStmt); 	
	      forStmt=(ForStmt)s;			      
	      forNode=forStmt;	  
		  loopIndex++;
		  isTightlyNestedLoop(forStmt);
		  break;
	    }//end of else if	    		  
  }//end of for
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
public void traverseLoopStatements(){	
	isTightlyNestedLoop(forNode);
	Vector<DependenceData> dataVector=new Vector<DependenceData>();	
	int nStmts=forNode.getNumStmt();	
	lTest.setLNo(loopNumber);
	cToolBox.setLoopIndex(loopIndex+1);
	cToolBox.setForStmtArray(forStmtArray);
	
	boolean aFlag=false;
	for(int i=index+1;i<nStmts;i++){ //not to include first statement of the loop which is lNum.	
	  Stmt s=forNode.getStmt(i);	  
	  if(s instanceof AssignStmt){
		AssignStmt aStmt1=(AssignStmt)s;
		for(int j=i;j<nStmts;j++){  
			   DependenceData dData=prepareDependenceData();
			   //System.out.println("loop Index"+loopIndex);		   
			   dData.setStatementAccessed("S"+i+":"+"S"+j);
		       if(i==j){		    	   
				   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS(),dData,dataVector,true);//compare LHS(sameStmt) with RHS(sameStmt)				   
				 }//end of if
			  else{
				Stmt bStmt=forNode.getStmt(j);
			    if(bStmt instanceof AssignStmt){  
			       //System.out.println("i am in different statements block");
				   AssignStmt aStmt2=(AssignStmt)bStmt;
				   //System.out.println(aStmt1.getPrettyPrinted());
				   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS(),dData,dataVector,false); //compare LHS(previousStmt) with RHS(nextStmt)			   
				   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS(),dData,dataVector,false);//compare LHS(previousStmt) with LHS(nextStmt)			   
				   cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS(),dData,dataVector,false);//compare RHS(previousStmt) with LHS(nextStmt)
				 }//end of if
				}//end of else
		       //if(aFlag){dataVector.add(dData);}
			}//end of inner for loop		
		}//end of if  
	  else if(s instanceof IfStmt){
		  IfStmt ifstmt=(IfStmt)s;
		  List list=ifstmt.getIfBlockList();		  
		  IfBlock block=(IfBlock)list.getChild(0);
		  List<Stmt> stmtList=(List<Stmt>)block.getStmts();
		  for(int j=0;j<stmtList.getNumChild();j++){
			if(stmtList.getChild(j) instanceof AssignStmt){
			 AssignStmt aStmt1=(AssignStmt)stmtList.getChild(j);	
			 for(int k=j;k<stmtList.getNumChild();k++){
				 DependenceData dData=prepareDependenceData();				   		   
				   dData.setStatementAccessed("S"+j+":"+"S"+k);
			       if(j==k){		    	    	    
					   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS(),dData,dataVector,true);//compare LHS(sameStmt) with RHS(sameStmt)					   
					 }//end of if
				  else{
					Stmt bStmt=forNode.getStmt(j);
				    if(bStmt instanceof AssignStmt){  				       
					   AssignStmt aStmt2=(AssignStmt)bStmt;					   
					   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS(),dData,dataVector,false); //compare LHS(previousStmt) with RHS(nextStmt)			   
					   cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS(),dData,dataVector,false);//compare LHS(previousStmt) with LHS(nextStmt)			   
					   cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS(),dData,dataVector,false);//compare RHS(previousStmt) with LHS(nextStmt)
					 }//end of if
					}//end of else  
			      }	//end of inner for		  
			   }//end of if
			 }//end of for		  
	  }//end of else if			
  }//end of for
	
 lTest.setRangeInfo(this.isRangeInfo());
 lTest.setFileName(fileName);
 lTest.setDirName(dirName);	
 LegalityTest.setForStmt(forStmtArray);
 lTest.setDataVector(dataVector); 
 //lTest.testParallelization();
 if(aTrans) {
	 lTest.testParallelization(true);
	 testLegality(dataVector); 
 }
 else if(!aTrans){	 
	 //lTest.testParallelization(false);
	 boolean flag=lTest.checkForLoopAnnotations();
	 if(dataVector.size()!=0){		 	 
		 writeToXMLFile(dataVector);
	  }//end of if
	 if(flag){
	   transformation=lTest.getATrans();
	   lId=(int)loopNumber;
	   writeToFile(program);
	   flag=false;
	 }	 
 }
 
 //if(dataVector.size()!=0){	 
	 //lTest.setDataVector(dataVector);	 
	// writeToXMLFile(dataVector);
  //}//end of if 
}//end of traverseLoopStatements function

private void testLegality(Vector<DependenceData> dataVector){
	if(lId==(int)loopNumber && !transformation.equals("All")){
		boolean flag=lTest.testLegality(transformation);
		if(flag){
			writeToFile(program);
			if(dataVector.size()!=0){	 
				 //lTest.setDataVector(dataVector);	 
				 writeToXMLFile(dataVector);
			  }//end of if
		}
	}
	else if(transformation.equals("All") && lId==getMaxLoop()){
		boolean flag=lTest.testLegality(transformation);
		if(flag){
			writeToFile(program);
		}
	}
}


/*
 * This function writes specialized version with loop transformations applied to a file
 */
private void writeToFile(Program prog){
	  StringTokenizer st = new StringTokenizer(fileName,".");
      String name =st.nextToken();
	  File f = new File(dirName);//this checks for the presence of directory	    
	  if(!f.exists()){
	     f.mkdir();
	  }
	  Writer output;        
	  File file = new File(dirName+"/"+lId+transformation+name+".m");
	  try {
		  output = new BufferedWriter(new FileWriter(file));
		  output.write(prog.getPrettyPrinted());
		 output.close();
	    } catch (IOException e) {			  
		 e.printStackTrace();
	  }
		
}

/*
 *This function writes the dependence information to a .xml file. 
 */

private void writeToXMLFile(Vector<DependenceData> dataVector){
	
	StringTokenizer st = new StringTokenizer(fileName,".");
	String fName=st.nextToken();
	File file = new File(dirName+"/"+"dependence"+fName+".xml");	
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
	    loopElement.setAttribute(new String("LoopNumber"), Float.toString(loopNumber));            
	    loopElement.setAttribute(new String("NestingLevel"), Integer.toString(loopIndex));
	    	    
	    Iterator<DependenceData> it=dataVector.iterator();            
	    while(it.hasNext()){
	    	DependenceData data=it.next();
	    	
	    	Element rangeElement = document.createElement("Range"); //create another element
	        rangeElement.setAttribute(new String("Start"), Integer.toString(data.getStartRange()));            
	        rangeElement.setAttribute(new String("End"), Integer.toString(data.getEndRange()));
	        rangeElement.setAttribute(new String("Dependence"),data.getDependence());
	        rangeElement.setAttribute(new String("Parallel"), data.getPar());	        
	        //if(data.getTransformation().equals("")){
	          rangeElement.setAttribute(new String("ValidTransformation"),data.getTransformation());
	       // }      
	        //Element transformedLoopElement=null;
	        //if(data.getFNode()!=null){          	
	          //rangeElement.setAttribute(new String("ForLoop"),data.getFNode().getPrettyPrinted());	          
	        //}
	        
	        if(data.getNestingLevel()>0){
		        for(int i=0;i<data.getNLoopList().size();i++){
		        	LinkedList<NestedLoop> nLoop=data.getNLoopList();
		        	
		        	Element nestedLoopElement = document.createElement("NestedLoop"); //create another element
		            nestedLoopElement.setAttribute(new String("Number"), Float.toString(nLoop.get(i).getLoopNo()));
		            
		            Element nLRangeElement = document.createElement("Range"); //create another element
		            nLRangeElement.setAttribute(new String("Start"), Float.toString(nLoop.get(i).getStartRange()));
		            nLRangeElement.setAttribute(new String("End"), Float.toString(nLoop.get(i).getEndRange()));
		            nLRangeElement.setAttribute(new String("Dependence"),data.getDependence());
		            
		            Element arrayAccessElement = document.createElement("LoopStmts");            	
		        	arrayAccessElement.setAttribute(new String("StmtNumbers"),data.getStatementAccessed() );
		        	arrayAccessElement.setAttribute(new String("Access"),data.getArrayAccess() );
		        	if(data.getDistanceArray()!=null){
		        	 int[] array=data.getDistanceArray();
		        	 value="";
		        	 if(array!=null){
			        	for(int j=0;j<array.length;j++){
			        		if(j<array.length-1) value+=array[j]+",";
			        		else value+=array[j];
			        		//System.out.println("Value"+value+array.length);
			        	}			        	
			        	arrayAccessElement.setAttribute(new String("DistanceVector"),value);
		        	  }
		        	}
		 
		            nLRangeElement.appendChild(arrayAccessElement);
		            nestedLoopElement.appendChild(nLRangeElement);
		            rangeElement.appendChild(nestedLoopElement); // add element1 under loopElement
		        } 
		      }//end of if
	        else if(data.getNestingLevel()==0){
	        	Element arrayAccessElement = document.createElement("LoopStmts");            	
	        	arrayAccessElement.setAttribute(new String("StmtNumbers"),data.getStatementAccessed() );
	        	arrayAccessElement.setAttribute(new String("Access"),data.getArrayAccess() );
	        	if(data.getDistanceArray()!=null){
	        	  int[] array=data.getDistanceArray();
	        	value="";
	        	if(array!=null){
		        	for(int j=0;j<array.length;j++){
		        		if(j<array.length-1) value+=array[j]+",";
		        		else value+=array[j];
		        		arrayAccessElement.setAttribute(new String("DistanceVector"),value);
		        		//System.out.println("Value"+value+array.length);
		        	}
	        	}	
	          }
	        	rangeElement.appendChild(arrayAccessElement); // add element1 under loopElement
	        }//end of else if	    	
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
	 } catch (Exception e) {e.printStackTrace();}   

}//end of function writeToXMLFile

public boolean isRangeInfo() {
	return rangeInfo;
}

public void setRangeInfo(boolean rangeInfo) {
	this.rangeInfo = rangeInfo;
}

public String getDirName() {
	return dirName;
}

public void setDirName(String dirName) {
	this.dirName = dirName;
}

public boolean isATrans() {
	return aTrans;
}

public void setATrans(boolean trans) {
	aTrans = trans;
}
}
