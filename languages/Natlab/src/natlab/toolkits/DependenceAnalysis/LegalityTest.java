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


public class LegalityTest {
    
  /*public static void main(String args[]){
	  	parseDependenceFile();
  }*/
  private Vector<DependenceData> dataVector;//=new Vector<DependenceData>();	
  
  //this function tests legality of a transformation and then passes it to the transformer.
  /*
   * TODO:Check whether user has provided some annotations for the type of transformation that needs to be applied.
   * TODO:If user has not provided any information about the type of transformation that needs to be applied then apply these uniModular transformations.
   */
  public void testLegality(){
	  
	for(int i=0;i<dataVector.size();i++){	    
		 //LoopData lData=(LoopData)table.get(it.next());
		 DependenceData dData=(DependenceData)dataVector.get(i);
	     int nLevel=dData.getNestingLevel();
	     String dependence=dData.getDependence();
	     if(nLevel==1 && dependence=="n"){//writeToXMLFile(dData.getLoopNo(),"Reversal");}   //Case1:when there is no dependence and loop is not nested.
	    	 //TODO:Apply Loop Reversal.Call the Loop Reversal routine from comp 621 project.
	     }
	     else if(nLevel>1){
	    	 applyUniModularTransformations(dData);
	     }
	 }  
  }
  
   /*
   * Apply a combination of LoopReversal and LoopInterchange.
   * TODO:After checking the legality generate a mapping file which has following
   * 
   */
  private void applyUniModularTransformations(DependenceData lData){	  
	  /*
	   * Case1:Apply loop Reversal on loop 1.
	   * Case2:Apply loop Reversal on loop2.
	   * Case3:Apply Interchange loop1 and loop2.
	   * Case4:Apply loop reversal on loop1 and then interchange the two loops.	   
	   */
	  int level=1;
	  //Case4:
	  TransformationCombinations tCombination=new TransformationCombinations();
	  tCombination.createInterchangeReversalMatrix(level, lData.getNestingLevel());
	  if(tCombination.applyCombination(lData)){writeToXMLFile(lData.getLoopNo(),"Reversal+Interchange");return;}
	  else
	  {  level++;
	     tCombination.createInterchangeReversalMatrix(level, lData.getNestingLevel());//reversal of 2nd loop and then interchange.
	     if(tCombination.applyCombination(lData)){writeToXMLFile(lData.getLoopNo(),"Interchange+Reversal");return;}
	     else{//Case1:
	    	 level=1;
	    	 Reversal reversal=new Reversal(lData.getNestingLevel(),lData.getNestingLevel());
	   	     reversal.setMatrix(level);
	   	     if(reversal.applyReversal(lData)){writeToXMLFile(lData.getLoopNo(),"Reversal");return;}
	   	     else{//Case2:
	   	       level++; 	 
	   		   reversal.setMatrix(level);
	   		   if(reversal.applyReversal(lData)){writeToXMLFile(lData.getLoopNo(),"Reversal");return;}
	   		   else{//Case3:
	   			      Interchange interchange=new Interchange(lData.getNestingLevel(),lData.getNestingLevel());
	   			      interchange.setMatrix(lData.getNestingLevel());//indicate the loop nesting level.
	   			      if(interchange.applyInterchange(lData))writeToXMLFile(lData.getLoopNo(),"Interchange");
	   		   }   	    	 
	        }//end of case2 else 
	      }//end of case1 else
	   }//end of main else.
  }
  
  
  /*
   * TODO:1.Name of the transformation file should be the same as that of .m File
   *      
   */
  private static void writeToXMLFile(float loopNo,String Transformation){	  
	  try {
		  
	         File file = new File("transformation.xml");
	         boolean exists = file.exists();
		     DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
             Document document;
	         if(!exists)
	         {      	  
	              document = documentBuilder.newDocument();
	              Element rootElement = document.createElement("TD"); // creates a element
	              Element loopElement = document.createElement("LoopNo"); //create another element
	            //creates an Attribute of element1 as type and sets its value to String
	              loopElement.setAttribute(new String("Number"), Float.toString(loopNo));
	              loopElement.setAttribute(new String("TransformationsType"), Transformation);
	              rootElement.appendChild(loopElement); // add element1 under rootElement
		          document.appendChild(rootElement); // add the rootElement to the document	              
	         }
	         else
	         {
	        	 document = documentBuilder.parse(file);
	             Node rootNode = document.getDocumentElement();
	             Element newNode;
	             //NodeList listNodes=rootNode.getChildNodes();	             
	             newNode = document.createElement("LoopNo");
	             newNode.setAttribute(new String("Number"), Float.toString(loopNo));
		         newNode.setAttribute(new String("TransformationsType"), Transformation);
	             rootNode.appendChild(newNode);           

	         }
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
             Transformer transformer = transformerFactory.newTransformer();
             DOMSource source = new DOMSource(document);
             StreamResult result = new StreamResult(file);
             transformer.transform(source, result);
	         
	      } catch (Exception e) {System.out.println(e.getMessage());}  	        
  }
  
  

  
 /* public static void parseDependenceFile(){
	  Hashtable loopData =new Hashtable();
	try 
	  {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File("dependenceFile.xml"));
	        LoopData lData;
	        
	        // normalize text representation
	        doc.getDocumentElement().normalize();
	        System.out.println ("Root element of the doc is " + 
	             doc.getDocumentElement().getNodeName());
	        
        NodeList loopNodeList = doc.getElementsByTagName("LoopNo");
        for(int i=0;i<loopNodeList.getLength();i++){
	         lData=new LoopData();	         
	         Element loopNestedElement = (Element)loopNodeList.item(i);	         
	         if(loopNestedElement.getNodeType() == Node.ELEMENT_NODE){	        	 
	        	//To get attributes of the main loop; 
	        	int lNo=Integer.parseInt(loopNestedElement.getAttribute("LoopNumber"));
	        	lData.setLoopNo(lNo);
	            System.out.println("LoopNo::::"+lNo);
	        	int nLevel=Integer.parseInt(loopNestedElement.getAttribute("NestingLevel"));
	        	lData.setNestingLevel(nLevel);
	            System.out.println("NestingLevel::::"+nLevel);
	            int start=Integer.parseInt(loopNestedElement.getAttribute("Start"));
	        	lData.setStartRange(start);
	            System.out.println("Start::::"+start);
	            int end=Integer.parseInt(loopNestedElement.getAttribute("End"));
	        	lData.setEndRange(end);
	            System.out.println("End::::"+end);	
	            
	            
	            
	            //For getting dependency
	            NodeList dependencyList = loopNestedElement.getElementsByTagName("Dependence");	            
	            Element dependencyElement = (Element)dependencyList.item(0);	            
	            String dependency=dependencyElement.getAttribute("value");
                System.out.println("Dependency:::"+dependency);
                lData.setDependence(dependency.charAt(0));                
                
                //for getting array access
                NodeList distanceVectorsList = loopNestedElement.getElementsByTagName("DistanceVectors");
                Element distanceVectorElement = (Element)distanceVectorsList.item(0);
                NodeList arrayAccessList = distanceVectorElement.getElementsByTagName("ArrayAccess");
                System.out.println("Length of distance Vectors::"+arrayAccessList.getLength());
                for(int j=0;j<arrayAccessList.getLength();j++){ 	
                    Element arrayAccessElement = (Element)arrayAccessList.item(j);                	
                	String no=arrayAccessElement.getAttribute("value");
                	lData.setDistanceVectors(no);
    	            System.out.println("DependenceVector::::"+no+"  "+no.length());                   
    	        }
           //For getting information about nested loop.                
                NodeList nestedLoopList = loopNestedElement.getElementsByTagName("NestedLoop");
                NestedLoop nLoop=lData.getNestedLoop();
                for(int k=0;k<nestedLoopList.getLength();k++){
	               Element nestedLoopElement = (Element)nestedLoopList.item(k);	            
	               float nlNo=Float.parseFloat(nestedLoopElement.getAttribute("Number"));
	        	   System.out.println("Nested LoopNo::::"+nlNo);
	        	   nLoop.setLoopNo(nlNo);
	        	   int nlStart=Integer.parseInt(nestedLoopElement.getAttribute("Start"));
	        	   //lData.setStartRange(nlStart);
	        	   nLoop.setStartRange(nlStart);
	               System.out.println("Start::::"+nlStart);
	               int nlEnd=Integer.parseInt(nestedLoopElement.getAttribute("End"));
	        	  //lData.setEndRange(nlEnd);
	               nLoop.setStartRange(nlEnd);
	              System.out.println("End::::"+nlEnd);
            }//end of for loop
           loopData.put(lNo, lData);     
        }//end of if
	  }//end of for
	}catch (SAXParseException err) {
	    System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
		        System.out.println(" " + err.getMessage ());
           }catch (SAXException e) {
		        Exception x = e.getException ();
		        ((x == null) ? e : x).printStackTrace ();
		   }catch (Throwable t) {
		        t.printStackTrace ();
		  }
		   testLegality(loopData);
	}//end of function.*/

public Vector<DependenceData> getDataVector() {
	return dataVector;
}

public void setDataVector(Vector<DependenceData> dataVector) {
	this.dataVector = dataVector;
}
}
