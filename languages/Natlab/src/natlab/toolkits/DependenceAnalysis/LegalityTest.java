package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import ast.List;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import ast.ASTNode;
import ast.AssignStmt;
import ast.ElseBlock;
import ast.Expr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;


public class LegalityTest {
	
private String dirName;  
private Vector<DependenceData> dataVector;
private Hashtable<String,LinkedList<PredictedData>> pTable; 
private Vector<int[]> distanceVector=new Vector<int[]>();
private LinkedList<ForStmt> forStmtList; 
private LinkedList<ForStmt> fusionList=new LinkedList<ForStmt>();
private boolean rangeInfo=false;
private int fusionCount=0;
private String fileName;
private static int childNo=0;
private float lNo;
private boolean fFlag=false;
private LoopFusion lFusion=new LoopFusion(fusionList);

public boolean isFFlag() {
	return fFlag;
}
public void setFFlag(boolean flag) {
	fFlag = flag;
}
public float getLNo() {
	return lNo;
}
public void setLNo(float no) {
	lNo = no;
}
public int getChildNo() {
	return childNo;
}
public void setChildNo(int childNo) {
	this.childNo = childNo;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public LegalityTest() {
	//interchangeCount=0;
}
public boolean isRangeInfo() {
	return rangeInfo;
}

public void setRangeInfo(boolean rangeInfo) {
	this.rangeInfo = rangeInfo;
}

public LinkedList<ForStmt> getForStmt() {
	return forStmtList;
}

public void setForStmt(LinkedList<ForStmt> forStmt) {
	this.forStmtList = forStmt;
} 
private void quickSort(int low, int n){
	int hi=n-1;
	int lo = low;    
    if (lo >= n) {
      return;
    }    
    int mid=((DependenceData)dataVector.get((lo+hi)/2)).getEndRange();    
    while (lo < hi) {
      //while (lo<hi && array[lo] < mid) {
    	while(lo<hi && ((DependenceData)dataVector.get(lo)).getEndRange()<mid){
        lo++;
      }
      //while (lo<hi && array[hi] > mid) {
    	while (lo<hi && ((DependenceData)dataVector.get(hi)).getEndRange()>=mid) {
        hi--;
      }
      if (lo < hi) {        
    	DependenceData T = ((DependenceData)dataVector.get(lo));  	
    	dataVector.set(lo, ((DependenceData)dataVector.get(hi)));    	
    	dataVector.set(hi,T);
      }
    }
    if (hi < lo) {
      int T = hi;
      hi = lo;
      lo = T;
    }
    quickSort(low, lo);
    quickSort(lo == low ? lo+1 : lo, n);
}

/*
 * This function tests whether a loop is parallelizable or not. 
 * 
 */
public void testParallelization(){
	int tCount=0;
	if(parTest1())tCount++;
	if(parTest3())tCount++;
	int count=0;	
	for(int i=0;i<dataVector.size();i++){  //this is first test there should be no flow dependency.
	   	DependenceData data=(DependenceData)dataVector.get(i);
	   	if(data.getDependence().equals("n") && tCount==2) {data.setPar("parallelizable"); System.out.println("Parallelizable");}
	   	else if(data.getDependence().equals("y")){
	   		int[] tempArr= new int[data.getDistanceArray().length];
	   		for(int j=0;j<tempArr.length;j++){
	   		  if(tempArr[j]==0) count++; 	
	   		}//end of for
	   		if(count==tempArr.length && tCount==2) {data.setPar("parallelizable");System.out.println("Parallelizable");}
	   	}//end of else if
	}//end of for
}//end of function

private boolean parTest1(){
	//this is second test to check within the list of indices,exactly one index variable should involve the loop variable.
	LinkedList<Expr> eList=null;
	ForStmt forStmt=forStmtList.get(0);
	DependenceData data=(DependenceData)dataVector.get(0);
	LinkedList<String> vList=new LinkedList<String>();
	vList.add(data.getLVarName());
	if(data.getNestingLevel()>1){	  
	  for(int k=0;k<data.getNestingLevel()+1;k++){
		  NestedLoop nLoop=data.getNLoopList().get(k);
		  vList.add(nLoop.getLVarName());
	  }//end of for
	}//end of if	
	
	for(int i=0;i<forStmt.getNumStmt();i++){
		//System.out.println(forStmt.getStmt(i).getPrettyPrinted());
	  if(!(forStmt.getStmt(i) instanceof ForStmt)){	
		if(((AssignStmt)forStmt.getStmt(i)).getLHS() instanceof ParameterizedExpr){
			ParameterizedExpr pExpr=(ParameterizedExpr)((AssignStmt)forStmt.getStmt(i)).getLHS();
			List<Expr> list=(List<Expr>)pExpr.getArgList();
			eList=new LinkedList<Expr>();
			for(int l=0;l<list.getNumChild();l++){
				eList.add(list.getChild(l));
			}			
			if(eList.containsAll(vList) && vList.size()>1)return false;			
		}//end of if
	}//end of for
  }//end of if
	return true;
}

/*
 * This function tests whether loop index variables have consecutive increasing integers or not.
 */
private boolean parTest3(){	
	ForStmt forStmt=forStmtList.get(0);
	RangeExpr rExpr=((RangeExpr)((AssignStmt)forStmt.getAssignStmt()).getRHS());
	if(rExpr.getUpper() instanceof IntLiteralExpr){
	  int value=((IntLiteralExpr)rExpr.getUpper()).getValue().getValue().intValue();
	  if(value!=1 || value!=2) return false;
	}
	return true;
}



/*
 * This function is called for every "for loop" whether there is dependence or not.It checks for the presence of Loop 
 * transformation annotations.If annotations are present the calls the respective transformation.
 * 
 */
public void checkForLoopAnnotations(){
	
   for(int i=0;i<forStmtList.size();i++){	   
	ForStmt forStmt=forStmtList.get(i);	
	if(forStmt.isEligibleForLoopReversal()){		
	 	if(!this.isRangeInfo()){}   	    
	 	else{	 
	 		LoopReversal lreversal=new LoopReversal();	 		
			int upper=0;		
			quickSort(0,dataVector.size());
			for(int k=0;k<dataVector.size();k++){
			 DependenceData data=(DependenceData)dataVector.get(k);				
			 if(upper!=data.getEndRange() && data.getDependence().equals("n")){
			   data.setFNode(forStmt);		     				  
		       data.setTransformation("LoopReversal");
		       lreversal.ApplyLoopReversal(data,forStmt);	     	  
			 }//end of if
			   //lower=data.getStartRange();
			   upper=data.getEndRange();		
			}//end of for		   
			ASTNode node=forStmt.getParent();
			int a=node.getIndexOfChild(forStmt);			
			ElseBlock eBlock=new ElseBlock();
			eBlock.addStmt(forStmt);
			(lreversal.getIfStmt()).setElseBlock(eBlock);
			//System.out.println(lreversal.getIfStmt().getPrettyPrinted());
			node.removeChild(a);
			if(a<0)node.insertChild(lreversal.getIfStmt(), 0);
			else node.insertChild(lreversal.getIfStmt(), a);
			//setChildNo(a+1); 
	   }//end of else
     }//end of LoopReversal.	
	else if(forStmt.isEligibleForLoopInterchange()){  	
		if(!this.isRangeInfo()){}
		else{//needs to change this for range info.
			LoopInterchange lInterchange=new LoopInterchange();	 		
			int upper=0;		
			quickSort(0,dataVector.size());
			for(int k=0;k<dataVector.size();k++){
			 DependenceData data=(DependenceData)dataVector.get(k);			 
			 if(upper!=data.getEndRange() && data.getDependence().equals("n")){			
		     	data.setTransformation("LoopInterchange");
		     	lInterchange.ApplyLoopInterchange(data, forStmtList, k);		     	
			  }//end of if
			 upper=data.getEndRange();
			}//end of for
			ASTNode node=forStmtList.get(0).getParent();
			int a=node.getIndexOfChild(forStmtList.get(0));			
			ElseBlock eBlock=new ElseBlock();
			eBlock.addStmt(forStmtList.get(0));
			(lInterchange.getIfStmt()).setElseBlock(eBlock);			
			node.removeChild(a);
			if(a<0)node.insertChild(lInterchange.getIfStmt(), 0);
			else node.insertChild(lInterchange.getIfStmt(), a);
		}//end of else		 
     }//end of LoopInterchange.
	
	else if(forStmt.isEligibleForLoopFusion()){
		
		if(fusionCount==2){
			fusionCount=0;			
			lFusion.ApplyLoopFusion();
			fusionList=new LinkedList<ForStmt>(); //reset the list
		}
		else{
			fusionCount++;
			fusionList.add(forStmt);
			lFusion.setVList(dataVector);
		}		
	}//end of LoopFusion
	
    else if(forStmt.isEligibleForLoopFission()){    	
    	for(int k=0;k<dataVector.size();k++){
			DependenceData data=(DependenceData)dataVector.get(k);
			if(data.getDependence().equals("n")){
     	      LoopFission lfission=new LoopFission(forStmt,i);//i indicates the loop that needs to be divided into two.
     	     data.setTransformation("LoopFission");
     	      lfission.ApplyLoopFission();
		   }//end of if
    	}//end of for
     }//end of LoopFission	
   }//end of for
   //checkForLoopFusion(forStmtList.get(0));    
}
//private void checkForLoopFusion(ForStmt fStmt){
	
	//if(fStmt.isEligibleForLoopFusion()){
	  // fusionList.add(fStmt);
	//} 
    	
//}
/*
 * This function screens the input and checks for a range dependence for all the statements
 */
private boolean screenInput(int value){
  //int upper=((DependenceData)dataVector.get(0)).getEndRange();
  for(int i=0;i<dataVector.size();i++){
	  DependenceData data=((DependenceData)dataVector.get(i));
	  if(value==data.getEndRange()){
		  if(data.getDependence().equals("y"))return true;
	  }//end of if
  }//end of for
  return false;
  
  
}


/*
 * This function does the following.
 * 1.This function tests legality of a transformation.If transformation is legal it then passes it to the transformer.
 * 2.This function is called only with -auto flag.
*/
public void testLegality(){	
	quickSort(0,dataVector.size());
    DependenceData dData=(DependenceData)dataVector.get(0);
    int nLevel=dData.getNestingLevel();	
	int upper=0;	
	boolean flag=false;
	if(nLevel==0){  //This is for reversal of single loop
	   LoopReversal lreversal = new LoopReversal();
	   ForStmt forStmt=forStmtList.get(0);	
       for(int i=0;i<dataVector.size();i++){		
		 DependenceData data=(DependenceData)dataVector.get(i);	
		 if(upper!=data.getEndRange()){
			 flag=screenInput(data.getEndRange());
			 upper=data.getEndRange();
			 if(!flag){ //if no dependence
			    data.setTransformation("LoopReversal");
			    lreversal.ApplyLoopReversal(data,forStmt);	
			    
			 }//end of if
		 }//end of if
		 if(flag){//if there is dependence then check lexicographical order of the vectors.
			 distanceVector.add(data.getDistanceArray());
			 if(applyReversal(data)){
				   data.setTransformation("LoopReversal");
				   lreversal.ApplyLoopReversal(data, forStmt);
			 }//end of if
		 }//end of if		 	 
   	 } //end of for
     ASTNode node=forStmt.getParent();
	 int a=node.getIndexOfChild(forStmt);
	 //ASTNode node1=node.getChild(a+1);
	 node.removeChild(a+1);
	 ElseBlock eBlock=new ElseBlock();
	 eBlock.addStmt(forStmt);
	 (lreversal.getIfStmt()).setElseBlock(eBlock);	
	 node.removeChild(a);
	 
	 //node.removeChild(a+1);
	 //System.out.println(node.getChild(a+1).getPrettyPrinted());
	 if(a<0)node.insertChild(lreversal.getIfStmt(), 0);
	else node.insertChild(lreversal.getIfStmt(), a);  
   }//end of if
	
  else{ //This is when we have a nested loop,then explore the possibility of interchange and reversal
	  //Case1:Try Apply Loop Interchange if the loop is nested 
	  //System.out.println(forStmtList.get(0).getPrettyPrinted());	 
	  int count=0;
	  LoopInterchange lInterchange=new LoopInterchange();
	  pairData();
	  for(int i=0;i<dataVector.size();i++){		
	    DependenceData data=(DependenceData)dataVector.get(i);
	    //DependenceData[] datArray=new DependenceData[2];	    
		if(upper!=data.getEndRange()){
		   flag=screenInput(data.getEndRange());
		   upper=data.getEndRange();
		   if(!flag){ //if no dependence
			  data.setTransformation("LoopInterchange");
			  lInterchange.ApplyLoopInterchange(data,forStmtList,count);
			  count++;
			 }//end of if
		 }//end of if
		if(flag){//if there is dependence then check lexicographical order of the vectors.
		  distanceVector.add(data.getDistanceArray());
		   if(applyInterchange(data)){
			 data.setTransformation("LoopInterchange");
			 lInterchange.ApplyLoopInterchange(data,forStmtList,count);
			 count++;
			 }//end of if
		  }//end of if		 	 
	   } //end of for
	   ASTNode node=forStmtList.get(0).getParent();
	   int a=node.getIndexOfChild(forStmtList.get(0));			
	   ElseBlock eBlock=new ElseBlock();
	   eBlock.addStmt(forStmtList.get(0));
	   (lInterchange.getIfStmt()).setElseBlock(eBlock);
	   //System.out.println(lInterchange.getIfStmt().getPrettyPrinted());
	   node.removeChild(a);
	   node.removeChild(a+1);
	   if(a<0)node.insertChild(lInterchange.getIfStmt(), 0);
	   else node.insertChild(lInterchange.getIfStmt(), a);
  }//end of else.   	
}//end of function

/*
 * This function makes pairs for nested loop ranges.
 */
private void pairData(){
  //System.out.println("frererewrewrwe"+dataVector.size()); 	
  for(int i=0;i<dataVector.size();i++){
	  System.out.println(((DependenceData)dataVector.get(i)).getEndRange());
  }
}


/*
 * This function tests whether interchange is legal or not.
 */
private boolean applyInterchange(DependenceData lData){	  
	  /*
	   * Case:Trying applying loop Interchange on all the loops starting from the outermost loop
	   * 
	   */	  
	int level=1;	
	Interchange interchange=new Interchange(lData.getNestingLevel()+1,lData.getNestingLevel()+1);   
  	 interchange.setMatrix(level); 
  	 for(int k=0;k<distanceVector.size();k++){
  		if(!interchange.applyInterchange(distanceVector.get(k)))return false;			 			
	 }//end of for
  	 return true; 
}
  
   /*
   * Apply a combination of LoopReversal and LoopInterchange.
   * 
   * 
   */
  private void applyUniModularTransformations(DependenceData lData){
	  
	  /*
	   * Case:Apply reversal on all the loops starting from the outermost loop.
	   * Case3:Apply Interchange loop1 and loop2.
	   * Case4:Apply loop reversal on loop1 and then interchange the two loops.	   
	   */
	  //String transformation="nil";
	  //boolean fFlag[]=new boolean[distanceVector.size()];
	  
	  int count=0;
	  int level=1;//This field tells on which nested loop to apply reversal.level1 means apply reversal on loop 1 or outermost loop.
	  TransformationCombinations tCombination=new TransformationCombinations();
	  for(int i=0;i<lData.getNestingLevel();i++){
		  tCombination.createReversalInterchangeMatrix(level, lData.getNestingLevel());		  
          for(int j=1;j<=lData.getNestingLevel();j++){		  
			  for(int k=0;k<distanceVector.size();k++){			  
				  //Case4:Apply loop reversal on loop1 and then interchange the two loops.		  		  
				  if(tCombination.applyCombination(distanceVector.get(k))){//{writeToXMLFile(lData,"Reversal+Interchange");return;}		
					  count++;
				  }//end of if   
			   }//end of for
			  if(count==dataVector.size()){ 
				  ForStmt fStmt=forStmtList.get(i);
				  //LoopReversal reversal=new LoopReversal(fStmt);
				  //reversal.ApplyLoopReversal();
				  
				  ForStmt fStmt2=forStmtList.get(j);
				  LoopInterchange interchange=new LoopInterchange();
				  interchange.ApplyLoopInterchange(fStmt, fStmt2);
				  
				  writeToXMLFile(lData,"Reversal+Interchange");				  
			   }//end of if
			  count=0;
          }//end of 2nd for
          level++;
	  }//end of 1st for
	  
	  System.out.println("Reversal+Interchange not applicable");
      applyUniModularTransformations(lData,level);		  
	  
	  
		  /*else
		  {  level++;
		     tCombination.createInterchangeReversalMatrix(level, lData.getNestingLevel());//reversal of 2nd loop and then interchange.
		     if(tCombination.applyCombination(lData)){writeToXMLFile(lData,"Interchange+Reversal");return;}
		     else{//Case1:
		    	 level=1;
		    	 Reversal reversal=new Reversal(lData.getNestingLevel()+1,lData.getNestingLevel()+1);
		   	     reversal.setMatrix(level);
		   	     if(reversal.applyReversal(lData)){writeToXMLFile(lData,"Reversal");return;}
		   	     else{//Case2:
		   	       level++; 	 
		   		   reversal.setMatrix(level);
		   		   if(reversal.applyReversal(lData)){writeToXMLFile(lData,"Reversal");return;}
		   		   else{//Case3:
		   			      Interchange interchange=new Interchange(lData.getNestingLevel()+1,lData.getNestingLevel()+1);
		   			      interchange.setMatrix(lData.getNestingLevel());//indicate the loop nesting level.
		   			      if(interchange.applyInterchange(lData))writeToXMLFile(lData,"Interchange");
		   		   }   	    	 
		        }//end of case2 else 
		      }//end of case1 else
		   }//end of main else.*/
	 
 }//end of function
  
  
  /*
   * This function interchanges the two loops and then applies reversal on the outer loop.
   * 
   */
  private void applyUniModularTransformations(DependenceData lData,int level){
	  
	     level=1;
	     int count=0;
	     TransformationCombinations tCombination=new TransformationCombinations();
	     for(int i=0;i<lData.getNestingLevel();i++){
		     tCombination.createInterchangeReversalMatrix(level, lData.getNestingLevel());//reversal of 2nd loop and then interchange.
		     for(int j=1;j<=lData.getNestingLevel();j++){
		      for(int k=0;k<distanceVector.size();k++){   			  
				if(tCombination.applyCombination(distanceVector.get(k))){		
					  count++;
				}//end of if   
			  }//end of for
			 if(count==dataVector.size()){ 
				 ForStmt fStmt2=forStmtList.get(j);
				 ForStmt fStmt=forStmtList.get(i);
				 LoopInterchange interchange=new LoopInterchange();
				 interchange.ApplyLoopInterchange(fStmt, fStmt2);					 
				 
				 //LoopReversal reversal=new LoopReversal(fStmt);
				 //reversal.ApplyLoopReversal();			  
				 
				 writeToXMLFile(lData,"Interchange+Reversal");			  
			 }
			 count=0;
           }//end of 2nd for
	       level++;		     
	     }//end of 1st for	     
		System.out.println("Interchange+Reversal not applicable");
		applyReversal(lData);			 
  }//end of function.
  
  /*
   * This function does the following
   * 1.Applies reversal 
   * 
   */  
  private boolean applyReversal(DependenceData lData){	  
	  /*
	   * Case:Trying applying loop reversal on all the loops starting from the outermost loop
	   * 
	   */	  
	  int level=1;
	  int count=0;
	  Reversal reversal=new Reversal(lData.getNestingLevel()+1,lData.getNestingLevel()+1);
      if(lData.getNestingLevel()==0){ //For a single loop
    	 reversal.setMatrix(level); 
    	 for(int k=0;k<distanceVector.size();k++){
 			if(!reversal.applyReversal(distanceVector.get(k)))return false; 			
 		  }//end of for
    	  return true;
      }
      else return true;
     /* else{
	   for(int i=0;i<lData.getNestingLevel();i++){
		  reversal.setMatrix(level);		  
		  for(int k=0;k<distanceVector.size();k++){
			if(reversal.applyReversal(distanceVector.get(k))){count++;} 
		  }//end of for
		  if(count==dataVector.size()){ 
			  
			  ForStmt fStmt=forStmtList.get(i);
			  //LoopReversal rev=new LoopReversal(fStmt);
		      //rev.ApplyLoopReversal();			  
			  writeToXMLFile(lData,"Reversal");	  
		  }
		  else{
			  System.out.println("Reversal not applicable");
		  }
		  count=0;		  
	  }//end of 1st for
     }//end of else*/
  }//end of apply Reversal
  
 // private void applyReversal(DependenceData lData,int level){
	  
	  /*
	   * Case:Try applying reversal on all the loops starting from the outermost loop.
	   * 
	   */	  
	/*  int count=0;
	  Reversal reversal=new Reversal(lData.getNestingLevel()+1,lData.getNestingLevel()+1);
	  reversal.setMatrix(level);
	  for(int i=0;i<distanceVector.size();i++){
		if(reversal.applyReversal(distanceVector.get(i))){count++;} 
	  }
	  if(count==dataVector.size()){ //TODO:actually call the reversal function from comp621
		  writeToXMLFile(lData,"Reversal of 2nd loop");
	  }
	  else{
		  System.out.println("Reversal of 2nd loop not applicable");
	  }
	 
  }*/
  
  
  /*
   * This function writes the transformation data to xml file.
   *      
   */
  private void writeToXMLFile(DependenceData data,String Transformation){	  
	  try {
		     StringTokenizer st = new StringTokenizer(fileName,".");
		     String fName=st.nextToken();
	         File file = new File(dirName + "/" +"transformation"+ dirName +".xml");
	         boolean exists = file.exists();
		     DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
             Document document;
	         if(!exists){      	  
	              document = documentBuilder.newDocument();
	              Element rootElement = document.createElement("TD"); // creates a element
	              Element loopElement = document.createElement("LoopNo"); //create another element
	            //creates an Attribute of element1 as type and sets its value to String
	              loopElement.setAttribute(new String("Number"), Float.toString(data.getLoopNo()));
	              loopElement.setAttribute(new String("TransformationsType"), Transformation);	              
	              loopElement.setAttribute(new String("StartRange"), Integer.toString(data.getStartRange()));
	              loopElement.setAttribute(new String("EndRange"), Integer.toString(data.getEndRange()));
	              rootElement.appendChild(loopElement); // add element1 under rootElement
		          document.appendChild(rootElement); // add the rootElement to the document	              
	         }
	         else{
	        	 document = documentBuilder.parse(file);
	             Node rootNode = document.getDocumentElement();
	             Element newNode;
	             //NodeList listNodes=rootNode.getChildNodes();	             
	             newNode = document.createElement("LoopNo");
	             newNode.setAttribute(new String("Number"), Float.toString(data.getLoopNo()));
		         newNode.setAttribute(new String("TransformationsType"), Transformation);
		         newNode.setAttribute(new String("StartRange"), Integer.toString(data.getStartRange()));
	             newNode.setAttribute(new String("EndRange"), Integer.toString(data.getEndRange()));
	             rootNode.appendChild(newNode); 
	          }
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
             Transformer transformer = transformerFactory.newTransformer();
             DOMSource source = new DOMSource(document);
             StreamResult result = new StreamResult(file);
             transformer.transform(source, result);
	         
	      } catch (Exception e) {System.out.println(e.getMessage());}  	        
  }

public Vector<DependenceData> getDataVector() {
	return dataVector;
}

public void setDataVector(Vector<DependenceData> dataVector) {
	this.dataVector = dataVector;
}

public String getDirName() {
	return dirName;
}

public void setDirName(String dirName) {
	this.dirName = dirName;
}

/*public static LinkedList<ForStmt> getFusionList() {
	return fusionList;
}

public static void setFusionList(LinkedList<ForStmt> fusionList) {
	LegalityTest.fusionList = fusionList;
}*/

public Hashtable<String, LinkedList<PredictedData>> getPTable() {
	return pTable;
}
public void setPTable(Hashtable<String, LinkedList<PredictedData>> table) {
	pTable = table;
}
}
