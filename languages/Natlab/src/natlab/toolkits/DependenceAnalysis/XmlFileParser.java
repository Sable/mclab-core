package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 


public class XmlFileParser {
	
	private void parseXmlFile(){
		try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("test.xml");

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + 
                 doc.getDocumentElement().getNodeName());
             

            NodeList LoopList = doc.getElementsByTagName("LoopNo");
            int nNestedLoops = LoopList.getLength();
            System.out.println("Total no of people : " + nNestedLoops);

            for(int s=0; s< nNestedLoops; s++){

            	Node nestedLoopNode = LoopList.item(s);
                Element firstPersonElement = (Element)nestedLoopNode;
               for(int j=0;j<nestedLoopList.getLength();j++){
                    Node nestedLoopItem = nestedLoopList.item(j);
               }
                //if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
                    Element firstNameElement = (Element)firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    System.out.println("First Name : " + 
                           ((Node)textFNList.item(0)).getNodeValue().trim());

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
                    Element lastNameElement = (Element)lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    System.out.println("Last Name : " + 
                           ((Node)textLNList.item(0)).getNodeValue().trim());

                    //----
                    NodeList ageList = firstPersonElement.getElementsByTagName("age");
                    Element ageElement = (Element)ageList.item(0);

                    NodeList textAgeList = ageElement.getChildNodes();
                    System.out.println("Age : " + 
                           ((Node)textAgeList.item(0)).getNodeValue().trim());

                    //------


                //}//end of if clause


            }//end of for loop with s var


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
        //System.exit (0);

	}

}
