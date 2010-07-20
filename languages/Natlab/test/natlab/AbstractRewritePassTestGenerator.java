package natlab;

import java.io.IOException;
import java.io.PrintWriter;


public class AbstractRewritePassTestGenerator extends AbstractTestGenerator
{

    protected AbstractRewritePassTestGenerator(String relativeFilename)
    {
        super( relativeFilename );
    }
    protected String className;
    protected String transformationName;
    protected String rewritePkg;

    protected void printHeader( PrintWriter testFileWriter )
    {
        testFileWriter.println("package natlab;");
        testFileWriter.println("");
        testFileWriter.println("import ast.*;");
        testFileWriter.println("import natlab.toolkits.rewrite."+rewritePkg+".*;");
        testFileWriter.println("import natlab.Main;");
        testFileWriter.println("");
        testFileWriter.println("public class " + className + " extends RewritePassTestBase");
        testFileWriter.println("{");
    }
    protected void printMethod( PrintWriter testFileWriter, String testName )
    {
        String methodName = "test_" + testName;
        String inFileName = "test/" + testName + ".in";
        String outFileName = "test/" + testName + ".out";
        testFileWriter.println("    public void " + methodName + "() throws Exception");
        testFileWriter.println("    {");
        testFileWriter.println("        ASTNode actual = parseFile( \"" + inFileName + "\" );");
        testFileWriter.println("        "+transformationName+" rewrite = new "+transformationName+"( actual );");
        testFileWriter.println("        actual = rewrite.transform();");
        testFileWriter.println("");
        testFileWriter.println("        ASTNode expected = parseFile( \"" + outFileName + "\");");
        testFileWriter.println("        ");
        testFileWriter.println("        assertEquiv( actual, expected );");
        testFileWriter.println("    }");
    }
    protected void printFooter( PrintWriter testFileWriter )
    {
        testFileWriter.println("}");
        testFileWriter.println("");
    }
}