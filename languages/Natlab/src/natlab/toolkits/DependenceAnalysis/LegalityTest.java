package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import ast.ForStmt;


public class LegalityTest {
	
  private String dirName;
  
  private Vector<DependenceData> dataVector;	
  private Vector<int[]> distanceVector=new Vector<int[]>();
  private LinkedList<ForStmt> forStmt; 
 

public LinkedList<ForStmt> getForStmt() {
	return forStmt;
}

public void setForStmt(LinkedList<ForStmt> forStmt) {
	this.forStmt = forStmt;
}


//this function tests legality of a transformation and then passes it to the transformer.
  /*
   * TODO:Check whether user has provided some annotations for the type of transformation that needs to be applied.
   * TODO:If user has not provided any information about the type of transformation that needs to be applied then apply these uniModular transformations.
   */
  public void testLegality(){
	  
    DependenceData dData=(DependenceData)dataVector.get(0);
    int nLevel=dData.getNestingLevel();
	String dependence=dData.getDependence();
	
	if(nLevel>0){			
	   RangeData rData=new RangeData();	
       rData.setStart(dData.getStartRange());
       rData.setEnd(dData.getEndRange());
       //int[] dArray=dData.getDistanceArray();
       //distanceVector.add(dArray);
       for(int i=0;i<dataVector.size();i++){		
			DependenceData data=(DependenceData)dataVector.get(i);
			RangeData tRData=new RangeData();
			tRData.setStart(data.getStartRange());
			tRData.setEnd(data.getEndRange());
			if(tRData.equal(rData)){
			  	distanceVector.add(data.getDistanceArray());
			}
   		///else if(tRData.equal(rData)){
   			//break;
   		//}		 
   	 } //end of for
       applyUniModularTransformations(dData);  
   }
	
   if(nLevel==0 && dependence=="n"){//writeToXMLFile(dData.getLoopNo(),"Reversal");}   //Case1:when there is no dependence and loop is not nested.
   	 //TODO:Apply Loop Reversal.Call the Loop Reversal routine from comp 621 project.
    }
   
  }//end of function
  
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
				  ForStmt fStmt=forStmt.get(i);
				  LoopReversal reversal=new LoopReversal(fStmt);
				  reversal.ApplyLoopReversal();
				  
				  ForStmt fStmt2=forStmt.get(j);
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
				 ForStmt fStmt2=forStmt.get(j);
				 ForStmt fStmt=forStmt.get(i);
				 LoopInterchange interchange=new LoopInterchange();
				 interchange.ApplyLoopInterchange(fStmt, fStmt2);					 
				 
				 LoopReversal reversal=new LoopReversal(fStmt);
				 reversal.ApplyLoopReversal();			  
				 
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
  private void applyReversal(DependenceData lData){
	  
	  /*
	   * Case:Trying applying loop reversal on all the loops starting from the outermost loop
	   * 
	   */
	  int level=1;
	  int count=0;
	  Reversal reversal=new Reversal(lData.getNestingLevel()+1,lData.getNestingLevel()+1);
	  for(int i=0;i<lData.getNestingLevel();i++){
		  reversal.setMatrix(level);		  
		  for(int k=0;k<distanceVector.size();k++){
			if(reversal.applyReversal(distanceVector.get(k))){count++;} 
		  }//end of for
		  if(count==dataVector.size()){ 
			  
			  ForStmt fStmt=forStmt.get(i);
			  LoopReversal rev=new LoopReversal(fStmt);
		      rev.ApplyLoopReversal();			  
			  writeToXMLFile(lData,"Reversal");	  
		  }
		  else{
			  System.out.println("Reversal not applicable");
		  }
		  count=0;		  
	  }//end of 1st for
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
}
