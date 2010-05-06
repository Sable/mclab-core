package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import java.sql.Timestamp;
import java.util.*;

/*.........................
 * Heuristic Engine Driver
 * Author:Amina Aslam.
 * Creation Date:April7,2010
 * This class does the following.
 * 1.It parses input data file.
 * 2.Apply Heuristics to the data.
 * 
 */

public class HeuristicEngineDriver {
	private Hashtable<String,LinkedList> loopTable=new Hashtable<String,LinkedList>();
	private String fileName;
	private String dirName;
	private Hashtable<String,LinkedList<PredictedData>> table;
	//private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public HeuristicEngineDriver(String fName){
		StringTokenizer st = new StringTokenizer(fName,".");		
		fileName=st.nextToken();	  	
	  	fileName=fileName+".xml";
	  	//System.out.println(fileName);
	}	
	public void parseXmlFile(){
		StringTokenizer st = new StringTokenizer(fileName,".");		
		dirName=st.nextToken();	  
		 try 
		  {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(dirName+"/"+fileName));
	        ProfiledData inData;//=null;
	        
	        // normalize text representation
	        doc.getDocumentElement ().normalize ();
	        //System.out.println ("Root element of the doc is " + 
	         //    doc.getDocumentElement().getNodeName());
	        

	        NodeList nestedNodeList = doc.getElementsByTagName("NestedLoop");
	        int totalPersons = nestedNodeList.getLength();
	       // System.out.println("Total no of Loops : " + totalPersons);

	        for(int s=0; s<nestedNodeList.getLength() ; s++){	                        
	            Element nestedElement = (Element)nestedNodeList.item(s);
	            //System.out.println(firstPersonNode.getNodeName());
	            if(nestedElement.getNodeType() == Node.ELEMENT_NODE){
	                                
	                float lNo=Float.parseFloat(nestedElement.getAttribute("Number"));
	                inData=new ProfiledData();
	                inData.setLoopNo(lNo); //setting loopNo in inData.
	                //System.out.println("LoopNo::::"+lNo);
	                
	              //-------setting the lower Bound--------------//
	                NodeList lowerBoundList = nestedElement.getElementsByTagName("LowerBound");               
	                Element lowerBoundElement = (Element)lowerBoundList.item(0);
	               // System.out.println("IIdsf"+lowerBoundElement.getNodeName());
	                
	                
	                NodeList variableNameList = lowerBoundElement.getElementsByTagName("VariableName");
	                Element variableNameElement = (Element)variableNameList.item(0);

	                NodeList textFNList = variableNameElement.getChildNodes(); //Setting the loopVariableName
	                String lVName=((Node)textFNList.item(0)).getNodeValue().trim();
	                
	                //System.out.println("Variable Name : " +lVName); 
	                       //((Node)textFNList.item(0)).getNodeValue().trim());
	                inData.setLVName(lVName);

	                //-------
	                NodeList rangeList = lowerBoundElement.getElementsByTagName("Range");
	                Element rangeElement = (Element)rangeList.item(0);
	                //System.out.println("Range is:::"+rangeElement.getNodeName());
	                
	                NodeList startList = rangeElement.getElementsByTagName("start");
	                Element startElement = (Element)startList.item(0);          

	                NodeList textLNList = startElement.getChildNodes();
	                //System.out.println("start : " + 
	                    //   ((Node)textLNList.item(0)).getNodeValue().trim());
	                int lbStart=Integer.parseInt(((Node)textLNList.item(0)).getNodeValue()); //setting start of lowerBound.
	                ProfiledLowerBound lBound=inData.getNewLBound();
	                lBound.setStart(lbStart);

	                
	                NodeList endList = rangeElement.getElementsByTagName("end");
	                Element endElement = (Element)endList.item(0);               

	                NodeList textLNList1 = endElement.getChildNodes();
	                //System.out.println("end : " + 
	                    //   ((Node)textLNList1.item(0)).getNodeValue().trim());
	                int lbEnd=Integer.parseInt(((Node)textLNList1.item(0)).getNodeValue()); //setting end of lowerBound.               
	                lBound.setEnd(lbEnd);
	                inData.setLBound(lBound);
	                
	              
	                //............For Upper Bound................//
	               
	                NodeList upperBoundList = nestedElement.getElementsByTagName("UpperBound");               
	                Element upperBoundElement = (Element)upperBoundList.item(0);
	                //System.out.println("IIdsfuuuuuuu"+upperBoundElement.getNodeName());
	                //-------
	                NodeList uvariableNameList = upperBoundElement.getElementsByTagName("VariableName");
	                Element uvariableNameElement = (Element)uvariableNameList.item(0);

	                NodeList utextFNList = uvariableNameElement.getChildNodes();
	               // System.out.println("Variable Name : " + 
	                   //    ((Node)utextFNList.item(0)).getNodeValue().trim());

	                //-------
	                NodeList urangeList = upperBoundElement.getElementsByTagName("Range");
	                Element urangeElement = (Element)urangeList.item(0);
	               // System.out.println("Range is:::"+urangeElement.getNodeName());
	                
	                NodeList ustartList = urangeElement.getElementsByTagName("start");
	                Element ustartElement = (Element)ustartList.item(0);
	                

	                NodeList utextLNList = ustartElement.getChildNodes();
	               // System.out.println("start : " + 
	                    //   ((Node)utextLNList.item(0)).getNodeValue().trim());
	                
	                int ubStart=Integer.parseInt(((Node)utextLNList.item(0)).getNodeValue());                
	                UpperBound uBound=inData.getNewUBound();
	                uBound.setStart(ubStart); //setting the upper bound..
	                

	                
	                NodeList uendList = urangeElement.getElementsByTagName("end");
	                Element uendElement = (Element)uendList.item(0);
	                

	                NodeList utextLNList1 = uendElement.getChildNodes();
	                //System.out.println("end : " + 
	                    //   ((Node)utextLNList1.item(0)).getNodeValue().trim());
	                int ubEnd=Integer.parseInt(((Node)utextLNList1.item(0)).getNodeValue());        
	                uBound.setEnd(ubEnd); //setting the upper bound..
	                inData.setUBound(uBound);
	                
	                
	                //.......For LoopIncrement Factor...........//                
	                NodeList lifList = nestedElement.getElementsByTagName("LoopIncrementFactor");               
	                Element lifElement = (Element)lifList.item(0);
	                //System.out.println("IIdsfuuuuuuulif"+lifElement.getNodeName());
	                
	                //-------
	                NodeList lifrangeList = lifElement.getElementsByTagName("Range");
	                Element lifrangeElement = (Element)lifrangeList.item(0);
	              //  System.out.println("Range is:::"+lifrangeElement.getNodeName());
	                
	                NodeList lifstartList = lifrangeElement.getElementsByTagName("start");
	                Element lifstartElement = (Element)lifstartList.item(0);
	                             

	                NodeList liftextLNList = lifstartElement.getChildNodes();
	                //System.out.println("start : " + 
	                   //    ((Node)liftextLNList.item(0)).getNodeValue().trim());
	                int lifStart=Integer.parseInt(((Node)liftextLNList.item(0)).getNodeValue());                                                
	                ProfiledLIF lif=inData.getNewLoopIncFac();
	                lif.setStart(lifStart);
	                
	                NodeList lifendList = lifrangeElement.getElementsByTagName("end");
	                Element lifendElement = (Element)lifendList.item(0);
	                

	                NodeList liftextLNList1 = lifendElement.getChildNodes();
	                //System.out.println("end : " + 
	                    //   ((Node)liftextLNList1.item(0)).getNodeValue().trim());
	                int lifEnd=Integer.parseInt(((Node)liftextLNList1.item(0)).getNodeValue());                              
	                lif.setEnd(lifEnd);
	                inData.setLoopIncFac(lif);
	                if(loopTable.containsKey(nestedElement.getAttribute("Number"))){ //get the already existing linked list for the key and add the value to it
	                	//this is to insert data from the different runs of the same loop into the same list.
	                	LinkedList tList=(LinkedList)loopTable.get(nestedElement.getAttribute("Number"));
	                	tList.add(inData);           	
	                }
	                else{// create a new linked list for this key and insert it in the hashtable.
	                	LinkedList list=new LinkedList();
	                	list.add(inData);
	                	loopTable.put(nestedElement.getAttribute("Number"),list);                	
	                }
	             }//end of if clause
	        }//end of for loop with s var
	        callIntroSort();
	        
	  		//writeToXMLFile(predictedValues);
	        //writeToXMLFile(table);	        
	    }catch (SAXParseException err) {
	    System.out.println ("** Parsing error" + ", line " 
	         + err.getLineNumber () + ", uri " + err.getSystemId ());
	    System.out.println(" " + err.getMessage ());

	    }catch (SAXException e) {
	    Exception x = e.getException ();
	    ((x == null) ? e : x).printStackTrace ();

	    }catch (Throwable t) {
	    t.printStackTrace ();
	   } 
	    
	    HeuristicEngine hEngine=new HeuristicEngine(loopTable);
        table=hEngine.computeRegionDivisors();
	    writeToXMLFile(table);
	    

	}
	
	public Hashtable<String, LinkedList<PredictedData>> getTable() {
		return table;
	}
	/*
	 * TODO:Needs to fix this currently just IntroSort is not fully implemented 
	 */
	private void callIntroSort(){
	    Set s=loopTable.keySet();
	    Iterator it=s.iterator();
	    while(it.hasNext()){
	    	LinkedList l=(LinkedList)loopTable.get(it.next());		    	
	    	Sorter b= new Sorter(l, new ComparableComparator());
	    	try{
	  	     b.IntroSort(0, l.size());
	  		}catch(Exception e){}	
	  		//for(int i=0;i<l.size();i++){		  			
	  			//System.out.print(((UpperBound)((ProfiledData)l.get(i)).getUBound()).getEnd()+" ");
	  		//}
	  		//System.out.println("  ");
	    }  		
  }
	
/*public void writeToXMLFile(Vector predictedValues){
	File file = new File(dirName+"/"+"RangeData"+fileName);		
	try 
	{   boolean exists = file.exists();	    
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    Document document;
	    Iterator it=predictedValues.iterator();        
	     	
	    if(!exists) //if file doesnot exist.
	    {      	
	     //  System.out.println("i am in xmlwrite"+htable.size()); 
	       document = documentBuilder.newDocument();
	       Element rootElement = document.createElement("HD"); // creates a element
	       
	       Element tsElement = document.createElement("RunNo"); // creates a element
	       Date d=new Date();
	       Timestamp ts=new Timestamp(d.getTime());
	       tsElement.setAttribute(new String("TimeStamp"), ts.toString()); 
	
	       while(it.hasNext()){
	    	   //System.out.println("i am in xmlwrite");
	    	    PredictedData pData=(PredictedData)it.next();	        	    
	            //System.out.println("i am in xmlwrite"+pData.getLoopNo()+" "+pData.getLowerBound());
	            //System.out.println("i am in xmlwrite");	                	            	
	            
	            Element loopElement = document.createElement("LoopNo"); //create another element
	            loopElement.setAttribute(new String("LoopNumber"), Float.toString(pData.getLoopNo()));
	            loopElement.setAttribute(new String("LoopVariableName"), pData.getLVName());
	            Element predictedLBElement = document.createElement("PredictedLowerBound"); //create another element
	            predictedLBElement.setAttribute(new String("Value"), Integer.toString(pData.getLowerBound()));
	            loopElement.appendChild(predictedLBElement);
	            Element predictedLIFElement = document.createElement("PredictedLoopIncFactor"); //create another element
	            predictedLIFElement.setAttribute(new String("Value"), Integer.toString(pData.getLoopIncFactor()));
	            loopElement.appendChild(predictedLIFElement);
	            Element predictedUBElement = document.createElement("PredictedUpperBound"); //create another element
	            predictedUBElement.setAttribute(new String("Value"), Integer.toString(pData.getUpperBound()));
	            loopElement.appendChild(predictedUBElement);
	            tsElement.appendChild(loopElement); // add element1 under rootElement            
	        }//end of while
	       rootElement.appendChild(tsElement);
	       document.appendChild(rootElement); // add the rootElement to the document
	        /*TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(document);
	        StreamResult result = new StreamResult(file);
	        transformer.transform(source, result);*/
	//    }//end of if
	  /*  else//if file exist
	    {    
	    		
	   	 document = documentBuilder.parse(file);	       	 
	   	 document.normalizeDocument();
	     Node rootElement = document.getDocumentElement();  
	     Element tsElement = document.createElement("RunNo"); // creates a element
	     Date d=new Date();
	     Timestamp ts=new Timestamp(d.getTime());
	     tsElement.setAttribute(new String("TimeStamp"), ts.toString());
	     
	     while(it.hasNext()){
	    	   //System.out.println("i am in xmlwrite");
	    	    PredictedData pData=(PredictedData)it.next();	        	    
	            //System.out.println("i am in xmlwrite"+pData.getLoopNo()+" "+pData.getLowerBound());
	            //System.out.println("i am in xmlwrite");	                	            	
	            
	            Element loopElement = document.createElement("LoopNo"); //create another element
	            loopElement.setAttribute(new String("LoopNumber"), Float.toString(pData.getLoopNo()));
	            loopElement.setAttribute(new String("LoopVariableName"), pData.getLVName());
	            Element predictedLBElement = document.createElement("PredictedLowerBound"); //create another element
	            predictedLBElement.setAttribute(new String("Value"), Integer.toString(pData.getLowerBound()));
	            loopElement.appendChild(predictedLBElement);
	            Element predictedLIFElement = document.createElement("PredictedLoopIncFactor"); //create another element
	            predictedLIFElement.setAttribute(new String("Value"), Integer.toString(pData.getLoopIncFactor()));
	            loopElement.appendChild(predictedLIFElement);
	            Element predictedUBElement = document.createElement("PredictedUpperBound"); //create another element
	            predictedUBElement.setAttribute(new String("Value"), Integer.toString(pData.getUpperBound()));
	            loopElement.appendChild(predictedUBElement);
	            tsElement.appendChild(loopElement); // add element1 under rootElement            
	        }//end of while
	     rootElement.appendChild(tsElement);
	     //rootElement.appendChild(tsElement); // add element1 under rootElement         
	   }//end of else*/      
	   /* TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(file);
	    transformer.transform(source, result);	     
	}//end of try
	catch (Exception e) {System.out.println(e.getMessage());}//end of for
	}//end of function call*/
	
	
  public void writeToXMLFile(Hashtable pTable){
		File file = new File(dirName+"/"+"RangeData"+fileName);	
		System.out.println("I am gerwnejrwejrwerjkkjidsjfdfds");
		try 
		{   boolean exists = file.exists();	    
		    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    Document document;
		    //Element rootElement=null;
		    //Node rElement=null;
		    Collection c=pTable.values();
    	    Iterator it=c.iterator();
		    
		    if(!exists) //if file doesnot exist.
		    {      	
		     //  System.out.println("i am in xmlwrite"+htable.size()); 
		       document = documentBuilder.newDocument();
		       
		    }
		    else{//if file exist		   		    		
		   	  document = documentBuilder.parse(file);	       	 
		   	  document.normalizeDocument();
		   	  
		    }
		       
		       Element tsElement = document.createElement("RunNo"); // creates a element
		       Date d=new Date();
		       Timestamp ts=new Timestamp(d.getTime());
		       tsElement.setAttribute(new String("TimeStamp"), ts.toString()); 
		
		       while(it.hasNext()){
		    	   LinkedList<PredictedData> tList=(LinkedList<PredictedData>)it.next();                	            	
		           for(int i=0;i<tList.size();i++){ 
		        	   PredictedData pData=(PredictedData)tList.get(i);
			            Element loopElement = document.createElement("LoopNo"); //create another element
			            loopElement.setAttribute(new String("LoopNumber"), Float.toString(pData.getLoopNo()));
			            loopElement.setAttribute(new String("LoopVariableName"), pData.getLVName());
			            Element predictedLBElement = document.createElement("PredictedLowerBound"); //create another element
			            predictedLBElement.setAttribute(new String("Value"), Integer.toString(pData.getLowerBound()));
			            loopElement.appendChild(predictedLBElement);
			            Element predictedLIFElement = document.createElement("PredictedLoopIncFactor"); //create another element
			            predictedLIFElement.setAttribute(new String("Value"), Integer.toString(pData.getLoopIncFactor()));
			            loopElement.appendChild(predictedLIFElement);
			            Element predictedUBElement = document.createElement("PredictedUpperBound"); //create another element
			            predictedUBElement.setAttribute(new String("Value"), Integer.toString(pData.getUpperBound()));
			            loopElement.appendChild(predictedUBElement);
			            tsElement.appendChild(loopElement); // add element1 under rootElement
		           }//end of for
		        }//end of while
		       
		       if(!exists){
		    	   Element rootElement = document.createElement("HD"); // creates a element
		    	   rootElement.appendChild(tsElement);
		    	   document.appendChild(rootElement); // add the rootElement to the document
		       }
		       
		       else{
		    	   Node rElement = document.getDocumentElement();
		    	   rElement.appendChild(tsElement);
		       }
		        /*TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        Transformer transformer = transformerFactory.newTransformer();
		        DOMSource source = new DOMSource(document);
		        StreamResult result = new StreamResult(file);
		        transformer.transform(source, result);*/
		    //}//end of if
		    
		     //Node rootElement = document.getDocumentElement();  
		     //Element tsElement = document.createElement("RunNo"); // creates a element
		     //Date d=new Date();
		     //Timestamp ts=new Timestamp(d.getTime());
		     //tsElement.setAttribute(new String("TimeStamp"), ts.toString());
		     
		     /*while(it.hasNext()){		         
                    LinkedList<PredictedData> tList=(LinkedList<PredictedData>)it.next();	
		    	    for(int i=0;i<tList.size();i++){ 
			        	PredictedData pData=(PredictedData)tList.get(i);
			            Element loopElement = document.createElement("LoopNo"); //create another element
			            loopElement.setAttribute(new String("LoopNumber"), Float.toString(pData.getLoopNo()));
			            loopElement.setAttribute(new String("LoopVariableName"), pData.getLVName());
			            Element predictedLBElement = document.createElement("PredictedLowerBound"); //create another element
			            predictedLBElement.setAttribute(new String("Value"), Integer.toString(pData.getLowerBound()));
			            loopElement.appendChild(predictedLBElement);
			            Element predictedLIFElement = document.createElement("PredictedLoopIncFactor"); //create another element
			            predictedLIFElement.setAttribute(new String("Value"), Integer.toString(pData.getLoopIncFactor()));
			            loopElement.appendChild(predictedLIFElement);
			            Element predictedUBElement = document.createElement("PredictedUpperBound"); //create another element
			            predictedUBElement.setAttribute(new String("Value"), Integer.toString(pData.getUpperBound()));
			            loopElement.appendChild(predictedUBElement);
			            tsElement.appendChild(loopElement); // add element1 under rootElement
		    	  }//end of for               
		        }//end of while
		     rootElement.appendChild(tsElement);
		     //rootElement.appendChild(tsElement); // add element1 under rootElement         
		   //}//end of else*/ 
		       
		       
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    DOMSource source = new DOMSource(document);
		    StreamResult result = new StreamResult(file);
		    transformer.transform(source, result);	     
		}//end of try
		catch (Exception e) {System.out.println(e.getCause());}//end of for
 }//end of function call
		
		


	

}
