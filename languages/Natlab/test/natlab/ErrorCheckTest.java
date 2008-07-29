package natlab;

import java.io.*;
import junit.framework.TestCase;
import natlab.ast.*;

public class ErrorCheckTest extends TestCase
{
    private final String inFileName = "test/errorcheck.in";

    public void test_ErrorCheckTest_function() throws Exception
    {
        CommentBuffer commentBuffer = new CommentBuffer();
        NatlabScanner scanner = new NatlabScanner(new BufferedReader(new FileReader(inFileName)));
        scanner.setCommentBuffer(commentBuffer);
        NatlabParser parser = new NatlabParser();
        parser.setCommentBuffer(commentBuffer);
        Program prog = (Program) parser.parse(scanner);
        
        assertTrue("weeding errors were not caught", prog.errorCheck());
    }
}