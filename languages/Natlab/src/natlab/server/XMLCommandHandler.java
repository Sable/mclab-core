package natlab.server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.*;
import java.io.*;
import org.w3c.dom.*;

public class XMLCommandHandler
{

    private String command;
    private String body;

    public XMLCommandHandler()
    {}

    public boolean parse(String xml)
    {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setCoalescing(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Reader xmlData = new StringReader( xml );
            Document d = db.parse( new InputSource( xmlData ) );
            
            Element root = d.getDocumentElement();
            root.normalize();
            
            command = root.getNodeName().trim();
            body = null;
            if( root.hasChildNodes() ){
                body = root.getFirstChild().getNodeValue().trim();
                if( body == null )
                    return false;
            }
            
            return true;
        }catch(Exception e){
            //TODO-JD: fix error reporting
            System.err.println("ERROR parsing");
            return false;
        }
    }

    public String getCommand()
    {
        return command;
    }
    public String getBody()
    {
        return body;
    }
}