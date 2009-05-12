package natlab;

import junit.framework.TestCase;
import natlab.server.XMLCommandHandler;

public class XMLCommandPassTests extends TestCase
{
    private static String cmd = "command1";
    private static String body = "this is the body\nthis is the body";

    private String genBasicXML()
    {
        String xml = "";
        xml += "<"+cmd+">\n";
        xml += body+"\n";
        xml += "</"+cmd+">";
        return xml;
    }
    private String genXMLspaces()
    {
        String xml = "";
        xml += "<"+cmd+">\n\n\n";
        xml += body+"   \n";
        xml += "</"+cmd+">";
        return xml;
    }
    public void test_xmlcommandpass_parse() throws Exception
    {
        String xml = genBasicXML();
        XMLCommandHandler xmlCH = new XMLCommandHandler();
        assertTrue( xmlCH.parse(xml) );
    }

    public void test_xmlcommandpass_command() throws Exception
    {
        String xml = genBasicXML();
        XMLCommandHandler xmlCH = new XMLCommandHandler();
        xmlCH.parse(xml);
        assertEquals(cmd, xmlCH.getCommand());
    }
    public void test_xmlcommandpass_body1() throws Exception
    {
        String xml = genBasicXML();
        XMLCommandHandler xmlCH = new XMLCommandHandler();
        xmlCH.parse(xml);
        assertEquals(body, xmlCH.getBody());
    }
    public void test_xmlcommandpass_body2() throws Exception
    {
        String xml = genXMLspaces();
        XMLCommandHandler xmlCH = new XMLCommandHandler();
        xmlCH.parse(xml);
        assertEquals(body, xmlCH.getBody());
    }

}