package natlab;

import java.io.*;
import junit.framework.TestCase;
import natlab.ast.*;
import natlab.SymbolTableEntry;

public class LookupLValueSanityTests extends TestCase
{
    private final String inFileName = "test/lookupLValueTestScript.in";

    public void test_lookuplvscriptsanity_islvalue() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        Name n;

        n = script.lookupLValue( "a1" );
        assertNotNull("lookup returned null for a1",n);
        assertTrue("should be an L value for a1", n.isLValue());

        n = script.lookupLValue( "a2" );
        assertNotNull("lookup returned null for a2",n);
        assertTrue("should be an L value for a2", n.isLValue());

        n = script.lookupLValue( "a3" );
        assertNotNull("lookup returned null for a3",n);
        assertTrue("should be an L value for a3", n.isLValue());

        n = script.lookupLValue( "a4" );
        assertNotNull("lookup returned null for a4",n);
        assertTrue("should be an L value for a4", n.isLValue());

        n = script.lookupLValue( "a5" );
        assertNotNull("lookup returned null for a5",n);
        assertTrue("should be an L value for a5", n.isLValue());

        n = script.lookupLValue( "a7" );
        assertNotNull("lookup returned null for a7",n);
        assertTrue("should be an L value for a7", n.isLValue());

        n = script.lookupLValue( "a10" );
        assertNotNull("lookup returned null for a10",n);
        assertTrue("should be an L value for a10", n.isLValue());

        n = script.lookupLValue( "a11" );
        assertNotNull("lookup returned null for a11",n);
        assertTrue("should be an L value for a11", n.isLValue());
    }
}




