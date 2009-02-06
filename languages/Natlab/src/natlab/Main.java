package natlab;

import natlab.options.Options;
import natlab.ast.*;

/*import matlab.MatlabParser;
import matlab.TranslationProblem;
import matlab.OffsetTracker;
import matlab.TextPosition;*/
import matlab.*;
import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;

import org.antlr.runtime.ANTLRReaderStream;


import beaver.Parser;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;



public class Main
{
    private static Options options;
    
    public static void main(String[] args)
    {
        boolean quiet; //controls the suppression of messages
        StringBuffer errors = new StringBuffer();
        options = new Options();

        if( processCmdLine( args ) ){

            if( options.help() ){
                System.err.println(options.getUsage());
            }
            else{
                quiet = options.quiet();  //check if quiet mode is active

                if( options.e() ){
                    System.err.println("exhaustive list");
                }
                if( options.d() ){
                    System.err.println("dynamic linking");
                }
                if( options.getFiles().size() == 0 ){
                    System.err.println("No files provided, must have at least one file.");
                }
                else{
		    LinkedList<Program> programs = new LinkedList<Program>();
                    for( Object o : options.getFiles() ){
                        String file = (String) o;
                        Reader fileReader = new StringReader("");
                        if( options.matlab() ){
                            try{
                                if( !quiet )
                                    System.err.println("Translating "+file+" to Natlab");
                                
                                BufferedReader in = new BufferedReader( new FileReader( file ) );
                                
                                FunctionEndScanner prescanner = new FunctionEndScanner(in);
                                FunctionEndScanner.Result result = prescanner.translate();
                                in.close();
                                
                                if(result instanceof NoChangeResult){
                                    in = new BufferedReader(new FileReader(file) ); //just re-open original file
                                }else if(result instanceof ProblemResult){
                                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                                        System.err.println(prob);
                                    }
                                    System.exit(0); //terminate early since extraction parser can't work without balanced 'end's
                                } else if(result instanceof TranslationResult){
                                    TranslationResult transResult = (TranslationResult) result;
                                    in = new BufferedReader(new StringReader(transResult.getText()));
                                }
                                
                                OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
                                List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
                                String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);
                                
                                
                                fileReader = new StringReader(destText);                            
                            }catch(FileNotFoundException e){
                                System.err.println("File "+file+" not found!\nAborting");
                                System.exit(1);
                            }
                            catch(IOException e){
                                System.err.println("Error translating "+file);
                                System.err.println(e.getMessage());
                                System.err.println("\n\nAborting");
                            }
                        }
                        else{
                            try{
                                fileReader = new FileReader( file );
                            }catch(FileNotFoundException e){
                                System.err.println("File "+file+" not found!\nAborting");
                                System.exit(1);
                            }
                        }
                        if( !quiet )
                            System.err.println("Parsing: " + file);
                        Program prog = parseFile( file,  fileReader, errors );

                        System.err.println( errors.toString() );
                        
                        if( prog == null ){
                            System.err.println("\nSkipping " + file);
                            break;
                        }
                        programs.add( prog );
                    }
		    CompilationUnits cu = new CompilationUnits();
		    for( Program p : programs ){
			cu.addProgram( p );
		    }
		    if( options.xml() ){
		    	//System.out.println(cu.ASTtoXML());
		    	System.out.println(cu.XMLtoString(cu.ASTtoXML()));
		    }
		    else if( options.pretty() ){
                        if( !quiet )
                            System.err.println("Pretty Printing");
			System.out.println(cu.getPrettyPrinted());
                    }
                }
            }
        }
    }

    //Parse a given file and return a Program ast node
    //if file does not exist or other problems, exit program
    private static Program parseFile(String fName, Reader file, StringBuffer errBuf )
    {
        NatlabParser parser = new NatlabParser();
        NatlabScanner scanner = null;
        CommentBuffer cb = new CommentBuffer();

        parser.setCommentBuffer(cb);

        try{
            scanner = new NatlabScanner( file );
            scanner.setCommentBuffer( cb );
            try{
                
                Program prog = (Program)parser.parse(scanner);
                if( parser.hasError() ){
                    for( String error : parser.getErrors())
                        errBuf.append(error + "\n");
                    prog = null;
                }
                return prog;

            }catch(Parser.Exception e){
                errBuf.append(e.getMessage());
                for(String error : parser.getErrors()) {
                    errBuf.append(error + "\n");
                }
                return null;
            } 
        }catch(FileNotFoundException e){
            errBuf.append( "File "+fName+" not found!\n" );
            return null;
        }
	catch(IOException e){
	    errBuf.append( "Problem parsing "+fName + "\n");
	    if( e.getMessage() != null )
		errBuf.append( e.getMessage() + "\n");
            return null;
	}
        finally{
            if(scanner != null) {
                scanner.stop();
            }
        }
    }
    private static boolean processCmdLine(String[] args)
    {
        try{
            options.parse( args );

            if( args.length == 0 ){
                System.out.println("No options given\n" +
                                   "Try -help for usage");
                return false;
            }
            return true;
        }catch( NullPointerException e ){
            System.err.println("options variable not initialized");
            throw e;
        }
    }
    
}