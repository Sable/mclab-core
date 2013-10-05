package natlab;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import natlab.options.Options;
import analysis.StructuralAnalysis;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Program;

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
        Program prog = parseFile( fName );
        ast = new CompilationUnits();
        ((CompilationUnits)ast).addProgram( prog );

        //create the analysis
        analysis = analysisType.getConstructor( ASTNode.class ).newInstance(ast);
        this.ast = ast;
    }
    
    public FlowAnalysisTestTool( String[] args, Class<? extends StructuralAnalysis> analysisType )
        throws NoSuchMethodException, InstantiationException, IllegalAccessException,
               InvocationTargetException, FileNotFoundException, Exception
    {

        //Generate the AST
        CompilationUnits cu = new CompilationUnits();
        Options opt = new Options();
        opt.parse( args );
        
        if( opt.getFiles().size() == 0 ){
            throw new Exception( "No files provided" );
        }
        for( Object o : opt.getFiles() ){
            String fName = (String) o;
            ArrayList<CompilationProblem> errList = new ArrayList<CompilationProblem>();
            if( !opt.natlab() ){
                //translate from matlab
                Reader source = Parse.translateFile( fName, errList ).getReader();
                if( source == null )
                    throw new Exception( "Error translating file "+ fName +":\n" + CompilationProblem.toStringAll( errList ) );
                Program prog = Parse.parseNatlabFile( fName, source, errList );
                if( prog == null )
                    throw new Exception( "Error parsing file "+ fName +":\n" + CompilationProblem.toStringAll( errList ) );
                cu.addProgram( prog );
            }
            else{
                cu.addProgram( parseFile( fName ) );
            }
        }
        
        //create the analysis
        analysis = analysisType.getConstructor( ASTNode.class ).newInstance(cu);
        this.ast = cu;
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
        return run(false,false);
    }
    /**
     * same as run(), but with two optional parameters
     * @param compact - if true, prints out only the out sets after a node, more compactly
     * @param hideComments - if true hides comments
     * @return
     */
    public String run(boolean compact,boolean hideComments){
        if( !analysis.isAnalyzed() )
            analysis.analyze();
        return ast.getAnalysisPrettyPrinted(analysis, compact, hideComments);
    }

    /**
     * Parses a given file. Takes in the filename and returns a
     * program node representing that file.
     *
     * @param fName  File to be parsed.
     *
     * @return  Program node representing the file.
     */
    protected Program parseFile( String fName ) throws FileNotFoundException, Exception
    {
        FileReader fileReader = new FileReader( fName );
        ArrayList<CompilationProblem> errList = new ArrayList<CompilationProblem>();
        Program prog = Parse.parseNatlabFile( fName, fileReader, errList );
        if( prog == null )
            //TODO-JD create proper compilation exception
            throw new Exception( "Error parsing file "+ fName +":\n" + CompilationProblem.toStringAll( errList ) );
        return prog;
    }
       
}