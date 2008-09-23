package annotations;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates annotations.AnnotationTypeQueryPassTests from provided list file. */
public class TypeQueryPassTestGenerator extends AbstractTestGenerator
{
    private TypeQueryPassTestGenerator()
    {
        super("/annotations/AnnotationTypeQueryPassTests.java");
    }
    public static void main(String[] args) throws IOException
    {
        new TypeQueryPassTestGenerator().generate(args);
    }

    protected void printHeader(PrintWriter testFileWriter) 
    {
        testFileWriter.println("package annotations;");
        testFileWriter.println();
        testFileWriter.println("import annotations.ast.*;");
        testFileWriter.println();
        testFileWriter.println("import java.io.*;");
        testFileWriter.println("public class AnnotationTypeQueryPassTests extends TypeQueryPassTestBase");
        testFileWriter.println("{");
    }

    protected void printMethod( PrintWriter testFileWriter, String testName){
        String methodName = "test_" + testName;
        String inFileName = "test/" + testName + ".in";
        String queryFileName = "test/" + testName + ".query";
        String outFileName = "test/" + testName + ".out";

        testFileWriter.println();
        testFileWriter.println("    public void " + methodName + "() throws Exception");
        testFileWriter.println("    {");
        testFileWriter.println("        AnnotationScanner scanner = getScanner(\"" + inFileName + "\");");
        testFileWriter.println("        AnnotationParser parser = new AnnotationParser();");
        testFileWriter.println("        Annotation annotation = (Annotation) parser.parse(scanner);");
        testFileWriter.println("        AnnotationScanner qScanner = getScanner(\"" + queryFileName + "\");");
        testFileWriter.println("        AnnotationParser qParser = new AnnotationParser();");
        testFileWriter.println("        Annotation queryAnnot = (Annotation) qParser.parse(qScanner);");
        testFileWriter.println("        BufferedReader in =  new BufferedReader( new FileReader(\"" + outFileName +"\") );");
        testFileWriter.println("        String expected, actual;");
        testFileWriter.println("        Entity query;");
        testFileWriter.println("        for( Stmt stmt : queryAnnot.getStmts() ){");
        testFileWriter.println("            if( !in.ready() )");
        testFileWriter.println("                break;");
        testFileWriter.println("            query = ((TypeStmt)stmt).getEntity();");
        testFileWriter.println("            expected = in.readLine().trim();");
        testFileWriter.println("            actual = annotation.getTypeInfo(query).getStructureString().trim();");
        testFileWriter.println("            assertEquals( expected, actual );");
        testFileWriter.println("        }");
        testFileWriter.println("    }");
        
    }
    protected void printFooter(PrintWriter testFileWriter){
        testFileWriter.println("}");
        testFileWriter.println();
    }
}
