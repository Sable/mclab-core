package natlab;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import natlab.ast.*;
import natlab.toolkits.analysis.*;
import natlab.Main;
import natlab.CompilationProblem;

/**
 * A tool for testing the functionality of an analysis. You specify
 * the analysis you want to run and the AST you want to run it on and
 * it will generate a string representation of the AST with the
 * analysis information annotated.
 *
 * @author Jesse Doherty
 */
public class FlowAnalysisTestTool
{

    protected ASTNode ast;
    protected StructuralAnalysis analysis;

    /**
     * A constructor to specify analysis instance.
     *
     * @param analysis  The analysis to be run by the test tool. The
     * annotated information will be taken from the result of this
     * analysis.
    */
    public FlowAnalysisTestTool( StructuralAnalysis analysis )
    {
        this.analysis = analysis;
        this.ast = analysis.getTree();
    }

    public FlowAnalysisTestTool( ASTNode ast, Class<? extends StructuralAnalysis> analysisType )
        throws NoSuchMethodException, InstantiationException, IllegalAccessException,
               InvocationTargetException
    {
        analysis = analysisType.getConstructor( ASTNode.class ).newInstance(ast);
        this.ast = ast;
    }

    public FlowAnalysisTestTool( String fName, Class<? extends StructuralAnalysis> analysisType )
        throws NoSuchMethodException, InstantiationException, IllegalAccessException,
               InvocationTargetException, FileNotFoundException, Exception
    {
        //parse the file
        FileReader fileReader = new FileReader( fName );
        ArrayList<CompilationProblem> errList = new ArrayList<CompilationProblem>();
        Program prog = Main.parseFile( fName, fileReader, errList );
        if( prog == null )
            //TODO-JD create proper compilation exception
            throw new Exception( "Error parsing file:\n" + CompilationProblem.toStringAll( errList ) );
        ast = new CompilationUnits();
        ((CompilationUnits)ast).addProgram( prog );

        //create the analysis
        analysis = analysisType.getConstructor( ASTNode.class ).newInstance(ast);
        this.ast = ast;
    }

    /**
     * Runs the analysis and returns the resulting string. Will only
     * actualy run the analysis if the analysis's is analyzed status
     * is false. 
     *
     * @return Annotated string representation of AST.
     */
    public String run()
    {
        if( !analysis.isAnalyzed() )
            analysis.analyze();
        return ast.getAnalysisPrettyPrinted( analysis );
    }
}