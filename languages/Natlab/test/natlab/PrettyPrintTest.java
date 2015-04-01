package natlab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

import com.google.common.base.StandardSystemProperty;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ast.Program;
import beaver.Parser;

public class PrettyPrintTest extends TestCase {

	public static String genFile(String filename){
		
		CommentBuffer commentBuffer = new CommentBuffer();
		NatlabParser parser = new NatlabParser();
		parser.setCommentBuffer(commentBuffer);
		
		String outline = null;
	    	
		try {
		    
			NatlabScanner scanner = new NatlabScanner(new FileReader(filename));
			scanner.setCommentBuffer(commentBuffer);
			Program original = (Program) parser.parse(scanner);
			if(parser.hasError()) {
				System.out.println("**ERROR**");
				for(String error : parser.getErrors()) {
					System.out.println(error);
				}
			} else {
				outline = original.getPrettyPrinted();		
			}
			
			
		} catch(Parser.Exception e) {
			System.out.println("**ERROR**");
			System.out.println(e.getMessage());
			for(String error : parser.getErrors()) {
				System.out.println(error);
			}
		  }
		  catch(FileNotFoundException e) {
			e.printStackTrace();
			}
		  catch(Exception e) {
			e.printStackTrace();
		  }
		  return outline;
	}
	
	public static void testProgFile()
	{
		StringBuffer inline = null;
		String basePath = StandardSystemProperty.USER_DIR.value() + "/languages/Natlab/test/";
		//System.out.println(basePath);
		String filename = basePath + "prettyprinttest.in";
		try
		{
			Scanner reader = new Scanner(new File(filename));
		    inline = new StringBuffer();
		    while ( reader.hasNext() )
		    	inline.append(reader.nextLine() + '\n' );
		}
		catch(Exception e){ e.printStackTrace(); }
	    
	    Assert.assertNotNull(inline.toString());
	    
		System.out.println("=======================Input=================================");
		System.out.println(inline);
		
		String line1 = genFile(filename);
		Assert.assertNotNull(line1);
		writeToFile(line1, "parsedprettyprinttest.out" );
		
		String line2 = genFile("parsedprettyprinttest.out");
		Assert.assertNotNull(line2);
		writeToFile(line2, "parsedprettyprinttest.out" );
		
		String regex = "%Declare [a-z A-Z]*\n";
		
		System.out.println("=======================Input'=================================");
		System.out.println(line1);
		System.out.println("=======================Input''=================================");
		System.out.println(line2);

		// compare results; exclude those strings matching the pattern from the comparison
		
		Assert.assertTrue("Different ", line1.replaceAll(regex, "").equals(line2.replaceAll(regex, "")) );			
	}
	public static void writeToFile(String progText, String filename)
	{
		try
		{
			PrintStream writer = new PrintStream( new File(filename));
			writer.print(progText);
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
class TestForPrint 
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Simple test for prettyprint");
		suite.addTestSuite(PrettyPrintTest.class);
		
		return suite;
	}
}
