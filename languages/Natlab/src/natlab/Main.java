package natlab;

import natlab.options.Options;
import natlab.ast.*;
import beaver.Parser;
import java.io.*;
import java.util.*;



public class Main
{
    private static Options options;
    
    public static void main(String[] args)
    {

        
        options = new Options();

        if( processCmdLine( args ) ){

            if( options.help() ){
                System.err.println(options.getUsage());
            }
            else{
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
                        System.err.println("Parsing: " + file);
			programs.add( parseFile( file ) );
                    }
		    CompilationUnits cu = new CompilationUnits();
		    for( Program p : programs ){
			cu.addProgram( p );
		    }
		    if( options.xml() ){
			System.out.println(cu.ASTtoXML());
		    }
		    /*else if( options.pretty() ){
			System.out.println(cu.getPrettyPrinted());
                        }*/
                }
            }
        }
    }

    //Parse a given file and return a Program ast node
    //if file does not exist or other problems, exit program
    private static Program parseFile(String fName)
    {
        NatlabParser parser = new NatlabParser();
        NatlabScanner scanner;
        CommentBuffer cb = new CommentBuffer();

        parser.setCommentBuffer(cb);

        try{
            scanner = new NatlabScanner(new FileReader( fName ) );
            scanner.setCommentBuffer( cb );
            try{
                return (Program)parser.parse(scanner);        
            }catch(Parser.Exception e){
                System.err.println("**ERROR**");
                System.err.println(e.getMessage());
                for(String error : parser.getErrors()) {
                    System.err.println(error);
                }
                System.err.println("Aborting");
                System.exit(1);
            }            
        }catch(FileNotFoundException e){
            System.err.println("File "+fName+" not found!\nAborting");
            System.exit(1);
        }
	catch(IOException e){
	    System.err.println("Problem parsing "+fName);
	    if( e.getMessage() != null )
		System.err.println( e.getMessage() );
	    System.err.println("Aborting");
	    System.exit(1);
	}
	    
        //should be dead due to exit calls
        return null;
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