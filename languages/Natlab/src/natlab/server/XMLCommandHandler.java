// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

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