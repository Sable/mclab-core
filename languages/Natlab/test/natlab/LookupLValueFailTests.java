package natlab;

import java.io.*;
import junit.framework.TestCase;
import ast.*;
import natlab.SymbolTableEntry;

public class LookupLValueFailTests extends TestCase
{
    private final String inFileName = "test/lookupLValueTestScript.in";

    public void test_lookuplvscriptfail_rhs1() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        Name se = script.lookupLValue( "a6" );
        assertNull("lookup did not returned null",se);
    }
    public void test_lookuplvscriptfail_nonexist() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        Name se = script.lookupLValue( "a0" );
        assertNull("lookup did not returned null",se);
    }

    public void test_lookuplvscriptfail_index() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        Name se = script.lookupLValue( "a8" );
        assertNull("lookup did not returned null",se);
    }
    public void test_lookuplvscriptfail_cellindex() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        Name se = script.lookupLValue( "a9" );
        assertNull("lookup did not returned null",se);
    }
}