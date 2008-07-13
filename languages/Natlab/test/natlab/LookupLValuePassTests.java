package natlab;

import java.io.*;
import junit.framework.TestCase;
import natlab.ast.Program;
import natlab.SymbolTableEntry;

public class LookupLValuePassTests extends TestCase
{
    private final String inFileName = "test/lookupLValueTestScript.in";

    public void test_lookuplvscriptpass_simple() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( "a1" );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", se.getSymbol(), "a1" );
    }

    public void test_lookuplvscriptpass_dot() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( "a2" );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", "a2", se.getSymbol() );
    }

    public void test_lookuplvscriptpass_paramaterized() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( "a3" );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", "a3", se.getSymbol() );
    }
    public void test_lookuplvscriptpass_cell() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( "a7" );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", "a7", se.getSymbol() );
    }
    public void test_lookuplvscriptpass_array1() throws Exception
    {
        String sName = "a10";
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( sName );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", sName, se.getSymbol() );
    }
    public void test_lookuplvscriptpass_array2() throws Exception
    {
        String sName = "a11";
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program script = (Program) parser.parse(scanner);
        
        SymbolTableEntry se = script.lookupLValue( sName );
        assertNotNull("lookup returned null",se);
        assertEquals("lookup returned wrong symbol", sName, se.getSymbol() );
    }
}