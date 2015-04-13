// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
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
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

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
    //protected String rewritePkg;

    protected void printHeader( PrintWriter testFileWriter )
    {
        testFileWriter.println("package natlab;");
        testFileWriter.println("");
        testFileWriter.println("import ast.*;");
        //testFileWriter.println("import natlab.toolkits.rewrite."+rewritePkg+".*;");
        testFileWriter.println("import natlab.toolkits.rewrite.Simplifier;");
        testFileWriter.println("import natlab.toolkits.rewrite.simplification.*;");
        testFileWriter.println("import natlab.toolkits.rewrite.Validator;");
        testFileWriter.println("import natlab.McLabCore;");
        testFileWriter.println("");
        testFileWriter.println("public class " + className + " extends RewritePassTestBase");
        testFileWriter.println("{");
    }
    protected void printMethod( PrintWriter testFileWriter, String testName )
    {
        String methodName = "test_" + testName.replace('/','_');
        String inFileName = "test/" + testName + ".in";
        String outFileName = "test/" + testName + ".out";
        testFileWriter.println("    public void " + methodName + "() throws Exception");
        testFileWriter.println("    {");
        testFileWriter.println("        ASTNode actual = parseFile( \"" + inFileName + "\" );");
        //testFileWriter.println("        "+transformationName+" rewrite = new "+transformationName+"( actual );");
        testFileWriter.println("        Simplifier simp = new Simplifier( actual, "+transformationName+".getStartSet() );");
        testFileWriter.println("        actual = simp.simplify();");
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