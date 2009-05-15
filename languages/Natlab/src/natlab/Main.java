package natlab;

import natlab.options.Options;
import natlab.ast.*;
import natlab.server.*;

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
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;



public class Main
{
    private static Options options;
    private static final int SERVER_PORT = 47146; //default server port
    
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
                    if( !quiet )
                        System.err.println("exhaustive list");
                }
                if( options.d() ){
                    if( !quiet )
                        System.err.println("dynamic linking");
                }
                if( options.server() ){
                    //in server mode
                    if( !quiet )
                        System.err.println("server mode");
                    
                    //setup port number
                    int portNumber = SERVER_PORT;
                    if( options.sp().length() > 0 )
                        portNumber = Integer.parseInt(options.sp());
                    
                    if( !quiet )
                        System.err.println("opening server on port " + portNumber );
                    //start the server
                    ServerSocket serverSocket = null;
                    try{
                        serverSocket = new ServerSocket( portNumber );
                    }
                    catch( IOException e ){
                        System.err.println("Server could not be opened on port "+portNumber);
                        System.exit(1);
                    }
                    if( !quiet )
                        System.err.println("Server started");

                    //wait for and accept a connection
                    Socket clientSocket = null;

                    try{
                        clientSocket = serverSocket.accept();
                    }catch( IOException e ){
                        System.err.println("accept client failed");
                        System.exit(1);
                        return;
                    }
                    
                    if( !quiet )
                        System.err.println("Client connected");
                    
                    BufferedReader in;
                    PrintWriter out;
                    try{
                        out = new PrintWriter( clientSocket.getOutputStream(), true );
                        in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
                    }catch( IOException e){
                        System.err.println("Server input stream creation failed");
                        System.exit(1);
                        return;
                    }
                    int inCharInt = 0;
                    StringBuffer commandBuf;
                    XMLCommandHandler xmlCH = new XMLCommandHandler();
                    boolean shutdown = false;
                    //loop until told to shutdown
                    while( !shutdown ){
                        
                        commandBuf = new StringBuffer();
                        try{
                            //loop while reading in from socket
                            //stop when a null byte is reached
                            while( true ){
                                inCharInt = in.read();
                                if( inCharInt == 0 ){
                                    break;
                                }
                                commandBuf.append((char)inCharInt);
                            }                                
                        }catch( IOException e ){}
                        if( xmlCH.parse( commandBuf.toString() ) ){
                            boolean parsing = false;
                            Reader source = new StringReader( "" );
                            String fileName = "";
                            String cmd = xmlCH.getCommand();
                            StringBuffer serverErrors = new StringBuffer();
                            if( cmd.equalsIgnoreCase( "parsefile" ) ){
                                fileName = xmlCH.getBody();
                                if( !quiet ){
                                    System.out.println(" parsefile cmd ");
                                    System.out.println("  parse file "+fileName);
                                }
                                parsing = true;
                                if( options.matlab() ){
                                    if( !quiet )
                                        System.out.println(" translating");
                                    source = translateFile( fileName, serverErrors );
                                }
                                try{
                                    source = new FileReader( fileName );
                                }catch( FileNotFoundException e){
                                    //TODO-JD: send filenotfound error to client
                                }
                            }
                            else if( cmd.equalsIgnoreCase( "parsetext" ) ){
                                String programText = xmlCH.getBody();
                                if( !quiet ){
                                    System.out.println(" parsetext cmd ");
                                    System.out.println("  text to parse:");
                                    System.out.println( programText );
                                }
                                fileName = "source/text";
                                parsing = true;
                                if( options.matlab() ){
                                    if( !quiet )
                                        System.out.println(" translating");
                                    source = translateFile( fileName, programText, serverErrors );
                                }
                                else
                                    source = new StringReader( programText );
                            }
                            else if( cmd.equalsIgnoreCase( "shutdown" ) ){
                                if( !quiet ){
                                    System.out.println(" shutdown cmd ");
                                }
                                shutdown = true;
                            }
                            else{
                                if( !quiet ){
                                    System.out.println(" ignored cmd ");
                                }
                            }

                            if( parsing ){
                                if( options.matlab() ){
                                    /*if( !quiet )
                                        System.err.println(" translating ");
                                    source = translateFile( fileName, source, serverErrors );
                                    */
                                    if( serverErrors.length() > 0 ){
                                        //TODO-JD: send errors
                                        if( !quiet )
                                            System.err.println("errors\n" + serverErrors);
                                    }
                                    if( source == null ){
                                        //TODO-JD: send errors
                                        if( !quiet )
                                            System.err.println("skipping file");
                                        continue;
                                    }
                                }
                                if( !quiet )
                                    System.err.println(" parsing ");
                                Program prog = parseFile( fileName, source, serverErrors );
                                if( serverErrors.length() > 0){
                                    //TODO-JD: send errors
                                    out.print("<errorlist><error>Error has occured, more information to come later</error></errorlist>\0");
                                    continue;
                                }
                                if( prog == null ){
                                    //TODO-JD: send errors
                                    out.print("<errorlist><error>Error has occured, more information to come later</error></errorlist>\0");
                                    continue;
                                }
                                CompilationUnits cu = new CompilationUnits();
                                cu.addProgram( prog );
                                if( !quiet )
                                    System.err.println(" sending resp: \n" + cu.XMLtoString(cu.ASTtoXML()));

                                out.print(cu.XMLtoString(cu.ASTtoXML())+ "\0");
                                out.flush();
                                if( !quiet )
                                    System.err.println(" resp sent");
                            }
                        }
                    }
                            
                }
                else if( options.getFiles().size() == 0 ){
                    System.err.println("No files provided, must have at least one file.");
                }
                else{
                    //parse each file and put them in a list of Programs
		    LinkedList<Program> programs = new LinkedList<Program>();
                    for( Object o : options.getFiles() ){
                        //When processing files there are currently two options.
                        //Either the matlab flag is set or it isn't.
                        //When it is set, each file is translated to natlab.
                        //The resulting string is put into a StringReader and 
                        //assigned to fileReader. Otherwise fileReader is 
                        //assigned a FileReader instance pointing to the 
                        //current file. In either case, fileReader has 
                        //the correct value.
                        String file = (String) o;
                        Reader fileReader = new StringReader("");

                        if( options.matlab() ){
                            //translate each file from matlab to natlab
                            //if successful the result will end up as a StringReader instance 
                            // in fileReader variable
                            //try{
                            if( !quiet )
                                System.err.println("Translating "+file+" to Natlab");
                            fileReader = translateFile( file, errors );

                                /*BufferedReader in = new BufferedReader( new FileReader( file ) );
                                
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
                                System.exit(1);
                            }
                                */
                            if( errors.length() > 0 )
                                System.err.print( errors.toString() );
                            if( fileReader == null ){
                                System.err.println("\nSkipping " + file);
                                break;
                            }
                        }
                        else{
                            //treat as a natlab input, set fileReader to a new 
                            //FileReader instance pointing to the current file
                            try{
                                fileReader = new FileReader( file );
                            }catch(FileNotFoundException e){
                                System.err.println("File "+file+" not found!\nAborting");
                                System.exit(1);
                            }
                        }
                        if( !quiet )
                            System.err.println("Parsing: " + file);
                        //parse the file
                        Program prog = parseFile( file,  fileReader, errors );

                        //report errors
                        if( errors.length() > 0 )
                            System.err.print( errors.toString() );
                        
                        if( prog == null ){
                            System.err.println("\nSkipping " + file);
                            break;
                        }
                        programs.add( prog );
                    }
                    //Take all resulting Program nodes and place them in a
                    //CompilationUnits instance
		    CompilationUnits cu = new CompilationUnits();
		    for( Program p : programs ){
			cu.addProgram( p );
		    }
		    if( options.xml() ){
		    	//System.out.println(cu.ASTtoXML());
		    	System.out.print(cu.XMLtoString(cu.ASTtoXML()));
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

    private static Reader translateFile(String fName, StringBuffer errBuf)
    {
        BufferedReader in = null;
        PositionMap prePosMap = null;
        try{
            in = new BufferedReader( new FileReader( fName ) );
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                in = new BufferedReader( new FileReader( fName ) );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        errBuf.append(prob+"\n");
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
            }
        }catch(FileNotFoundException e){
            errBuf.append("File "+fName+" not found!\nAborting\n");
            return null;
        }
        catch(IOException e){
            errBuf.append("Error translating "+fName+"\n");
            errBuf.append(e.getMessage());
            errBuf.append("\n\nAborting\n");
            return null;
        }

        return finishTranslateFile(fName, in, prePosMap, errBuf);

    }
            
    private static Reader translateFile(String fName, String source, StringBuffer errBuf)
    {
        BufferedReader in = null;
        PositionMap prePosMap = null;
        try{
            in = new BufferedReader( new StringReader( source ) );
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                in = new BufferedReader( new StringReader( fName ) );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        errBuf.append(prob+"\n");
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
            }
        }catch(FileNotFoundException e){
            errBuf.append("File "+fName+" not found!\nAborting\n");
            return null;
        }
        catch(IOException e){
            errBuf.append("Error translating "+fName+"\n");
            errBuf.append(e.getMessage());
            errBuf.append("\n\nAborting\n");
            return null;
        }

        return finishTranslateFile(fName, in, prePosMap, errBuf);
    }
    //Translate a fiven file and return a reader to access the translated version
    private static Reader finishTranslateFile(String fName, Reader in, 
                                              PositionMap prePosMap, StringBuffer errBuf)
    {
        try{
            /*
            BufferedReader in = new BufferedReader( file );
            PositionMap prePosMap = null;
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                //in.reset(); //just reset the reader
                file.reset();
                in = new BufferedReader( file );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        errBuf.append(prob+"\n");
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
             }
            */
            
            OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
            List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
            String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);
            
            if( problems.isEmpty() ){
                PositionMap posMap = offsetTracker.buildPositionMap();

                if( prePosMap != null ){
                    posMap = new CompositePositionMap(posMap, prePosMap);
                }
                //TODO-JD: do something with the posMap
                return new StringReader(destText);
            }
            else{
                for(TranslationProblem prob : problems){
                    errBuf.append(prob+"\n");
                }
                return null;
            }
        }catch(FileNotFoundException e){
            errBuf.append("File "+fName+" not found!\nAborting\n");
            return null;
        }
        catch(IOException e){
            errBuf.append("Error translating "+fName+"\n");
            errBuf.append(e.getMessage());
            errBuf.append("\n\nAborting\n");
            return null;
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