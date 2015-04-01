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

import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.*;

/**
 * @author Jesse Doherty
 */
public class MultiRewritePassTestGenerator extends AbstractRewritePassTestGenerator
{

    private MultiRewritePassTestGenerator(){
        super("/natlab/MultiRewritePassTests.java");
        className = "MultiRewritePassTests";
    }

    public static void main(String[] args) throws IOException
    {
        new MultiRewritePassTestGenerator().generate(args);
    }
        
    /**
     * Generates a test class based on a master list file of
     * simplifications to test. Each line of the master list file
     * contains the simplification class to be tested and the name of
     * the file containing the tests to execute. Each line is assumed
     * to be of the form {@literal class_name:test_list}. 
     */
    @Override
    protected void generate(String[] args) throws IOException {
        
        validateArgs( args );

        //name of file containing the list of simplifications and test lists
        String masterListFileName = args[0]; 
        //path to output directory (i.e. gen)
        String genDirName = args[1];
        
        Map<String,String> masterMap = getMasterMap( masterListFileName );

        String testFileName = genDirName + relativeFilename;
        PrintWriter testFileWriter = new PrintWriter(new FileWriter(testFileName));
        printHeader(testFileWriter);
        for( Map.Entry<String,String> entry : masterMap.entrySet() ){
            transformationName = entry.getKey();
            printMethodHeader( testFileWriter, entry.getValue() );
            List<String> testNames = getTestNames( entry.getValue()+".testlist" );
            for( String testName : testNames )
                //printMethod( testFileWriter, testName );
                printMethodBodyPart( testFileWriter, testName );
            printMethodFooter( testFileWriter );
        }
        printFooter(testFileWriter);
        testFileWriter.close();
        System.exit(0);
    }

    protected void printMethodHeader( PrintWriter testFileWriter, String testName )
    {
        String methodName = testName.replace('/','_');
        testFileWriter.println("    public void " + methodName + "() throws Exception");
        testFileWriter.println("    {");
        testFileWriter.println("        ASTNode actual, expected;");
        testFileWriter.println("        Simplifier simp;");
        testFileWriter.println("        boolean test = true;");
        testFileWriter.println("        StringBuilder errmsg = new StringBuilder();");
        testFileWriter.println("        ");
    }
    protected void printMethodBodyPart( PrintWriter testFileWriter, String subTestName )
    {
        String cleanName = "test_" + subTestName.replace('/','_');
        String inFileName = "test/" + subTestName + ".in";
        String outFileName = "test/" + subTestName + ".out";

        testFileWriter.println("        actual = parseFile( \"" + inFileName + "\" );");
        testFileWriter.println("        simp = new Simplifier( actual, "+transformationName+".getStartSet() );");
        testFileWriter.println("        actual = simp.simplify();");
        testFileWriter.println("        expected = parseFile( \"" + outFileName + "\");");
        testFileWriter.println("        test &= checkEquiv( actual, expected, \""+ cleanName +"\", errmsg);");
        testFileWriter.println("");
    }
    protected void printMethodFooter( PrintWriter testFileWriter )
    {
        testFileWriter.println("        assertTrue( errmsg.toString(), test );");
        testFileWriter.println("    }");
    }
    /**
     * Reads in a master file and returns a map from simplification
     * class name to test list file name. The master file is assumed
     * to be of the form {@literal class_name:test_list} where
     * {@literal class_name} is the name of the simplification class
     * to be used, and {@literal test_list} is the name of the file
     * containing the list of tests to execute for that
     * simplification.
     */
    protected Map<String,String> getMasterMap( String masterFileName ) throws IOException
    {
        Map<String,String> simpNames = new HashMap<String,String>();
        BufferedReader masterFileReader = new BufferedReader(new FileReader(masterFileName));
        
        int lineNumber = 0;
        while(masterFileReader.ready()){
            lineNumber++;
            String line = masterFileReader.readLine().trim();
            if(!isCommentLine(line)){
                String[] fields = line.split(":");
                validateMasterLine( masterFileName, lineNumber, fields );
                simpNames.put( fields[0].trim(), fields[1].trim() );
            }
        }
        return simpNames;
    }

    /**
     * Validates that the master file line contained exactly two
     * fields. If it doesn't then it prints an error msg and exits.
     */
    protected void validateMasterLine(String masterFileName, int lineNumber, String[] fields)
    {
        if( fields.length != 2 ){
            System.err.println( "error in simplification master file " + masterFileName + 
                                " at line " + lineNumber );
            System.exit(1);
        }
    }
                
}
