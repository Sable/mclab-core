package natlab;

import natlab.options.Options;
//import natlab.toolkits.analysis.ForVisitor;
import ast.*;
import natlab.server.*;
import natlab.toolkits.DependenceAnalysis.DependenceAnalysisDriver;
import natlab.toolkits.DependenceAnalysis.HeuristicEngineDriver;
import natlab.toolkits.DependenceAnalysis.McFlatDriver;
import natlab.toolkits.DependenceAnalysis.Profiler;
import natlab.toolkits.analysis.ForVisitor;

import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.analysis.example.*;
import natlab.toolkits.analysis.test.*;
import natlab.toolkits.analysis.handlepropagation.*;
import natlab.toolkits.analysis.callgraph.*;
import natlab.example.*;
import natlab.toolkits.rewrite.multireturn.*;
import natlab.toolkits.rewrite.threeaddress.*;
import natlab.toolkits.analysis.isscalar.*;


import natlab.toolkits.filehandling.*;

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
import java.util.List;

import natlab.toolkits.DependenceAnalysis.ProfilerDriver;
 
import java.util.regex.PatternSyntaxException;


/**
 * Main entry point for McLab compiler. Includes a main method that
 * deals with command line options and performs the desired
 * functions. Also includes static methods used to simplify tasks such
 * as parsing program code and translating from matlab to natlab.
 */
public class Main
{
    private static Options options;
    private static final int SERVER_PORT = 47146; //default server port
    private static final long HEART_RATE = 4000; //in milliseconds
    private static final long HEART_DELAY = 5000; //delay till first heart beat check is made
    
    /**
     * Main method deals with command line options and execution of
     * desired functions.
     */
    public static void main(String[] args) throws Exception
    {	

     // natlab.toolkits.analysis.functionhandle.TestAnalysis.main(args);
       // System.exit(0);

        //natlab.toolkits.analysis.functionhandle.TestAnalysis.main(args);
        //System.exit(0);

        boolean quiet; //controls the suppression of messages
        ArrayList errors = new ArrayList();
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
		
                //fortran neither uses the below parser, nor is a server
                if( options.fortran() ){ //begin fortran
                    if( options.getFiles().size() == 0 ){
                        System.err.println("No files provided, must have at least one file.");
                        System.exit(1);
                    }
                    System.out.println("compiling to fortran "+options.getFiles());
                    String[] fargs = {"-d",(String)options.getFiles().get(0)};
                    System.out.println("calling McFor with "+fargs);
                    McFor.main(fargs);
                    return;
                } //end fortran
                
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
                    //create flag object for keeping track of heart beats
                    HeartBeatFlag hbFlag = new HeartBeatFlag();
                    //create the timer that checks the heart beat signal
                    Timer heartBeat = new Timer();
                    if( !options.noheart() ){
                        //start the timer with a HeartBeatTask and an initial delay
                        heartBeat.schedule(new HeartBeatTask(hbFlag, out, quiet) ,HEART_DELAY, HEART_RATE );
                    }
                    //loop until told to shutdown
                    while( !shutdown ) {
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
                        if( xmlCH.parse( commandBuf.toString() ) ) {
                            boolean parsing = false;
                            Reader source = new StringReader( "" );
                            String fileName = "";
                            String cmd = xmlCH.getCommand();
                            ArrayList serverErrors = new ArrayList();
                            if( cmd.equalsIgnoreCase( "parsefile" ) ){
                                fileName = xmlCH.getBody();
                                if( !quiet ){
                                    System.err.println(" parsefile cmd ");
                                    System.err.println("  parse file "+fileName);
                                }
                                if( fileName == null ){
                                    String FNstr="";
                                    if( !quiet )
                                        FNstr+="  file name was null"+"/n";
                                    CompilationProblem FileNullcerror = new CompilationProblem(FNstr+="<errorlist><error>parsefile error: filename was null</error></errorlist>\0");
                                    errors.add(FileNullcerror);
                                    continue;
                                }
                                parsing = true;
                                if( options.matlab() ){
                                    if( !quiet )
                                        System.err.println(" translating");
                                    source = translateFile( fileName, serverErrors );
                                }
                                else{
                                    try{
                                        source = new FileReader( fileName );
                                    }catch( FileNotFoundException e){
                                        String FNFEstr="";
                                        if( !quiet )
                                            FNFEstr+="file: "+fileName+" not found"+"/n";
                                        CompilationProblem FileNotFoundcerror = new CompilationProblem(FNFEstr+="<errorlist><error>file: "+fileName+" not found</error></errorlist>\0");
                                        errors.add(FileNotFoundcerror);
                                    }
                                }
                            }
                            else if( cmd.equalsIgnoreCase( "parsetext" ) ){
                                String programText = xmlCH.getBody();
                                if( !quiet ){
                                    System.err.println(" parsetext cmd ");
                                    System.err.println("  text to parse:");
                                    System.err.println( programText );
                                }
                                if( programText == null ){
                                    if( !quiet )
                                        System.err.println("  program text was null");
                                    out.print("<errorlist><error>parsetext error: program text was null</error></errorlist>\0");
                                    out.flush();
                                    continue;
                                }
                                fileName = "source/text";
                                parsing = true;
                                if( options.matlab() ){
                                    if( !quiet )
                                        System.err.println(" translating");
                                    source = translateFile( fileName, programText, serverErrors );
                                }
                                else
                                    source = new StringReader( programText );
                            }
                            else if( cmd.equalsIgnoreCase( "shutdown" ) ) {
                                if( !quiet ){
                                    System.err.println(" shutdown cmd ");
                                }
                                out.print("<shutdown />\0");
                                out.flush();
                                shutdown = true;
                                //cancel the heartbeat timer - orly?
                                heartBeat.cancel();
                                try{
                                    clientSocket.close();
                                    out.close();
                                    in.close();
                                    serverSocket.close();
                                }catch(IOException e){}
                                
                                continue;
                            }
                            //when you get a heartbeat signal, set the heart beat flag.
                            else if( cmd.equalsIgnoreCase( "heartbeat" ) ) {
                                hbFlag.set();
                            }
                            else{
                                if( !quiet ){
                                    System.out.println(" ignored cmd ");
                                }
                            }
                            
                            if( parsing ) {
                                if( options.matlab() ){
                                    /*if( !quiet )
                                      System.err.println(" translating ");
                                      source = translateFile( fileName, source, serverErrors );
                                    */
                                    if( serverErrors.size() > 0 ){
                                        String serverEstr="";
                                        if( !quiet )
                                            serverEstr+="errors\n" + serverErrors+"/n";
                                        CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
                                        errors.add(Servercerror);
                                        continue;
                                        
                                    }
                                    if( source == null ){
                                        String fooEstr="";
                                        if( !quiet )
                                            fooEstr+="skipping file"+"/n";
                                        CompilationProblem Foocerror = new CompilationProblem(fooEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
                                        errors.add(Foocerror);
                                        continue;
                                    }
                                }
                                if( !quiet )
                                    System.err.println(" parsing ");
                                Program prog = parseFile( fileName, source, serverErrors );
                                if( serverErrors.size() > 0){
                                    String serverEstr="";
                                    if( !quiet )
                                        serverEstr+="errors\n" + serverErrors+"/n";
                                    CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured in translating, more information to come later</error></errorlist>\0");
                                    errors.add(Servercerror);
                                    continue;
                                }
                                if( prog == null ){
                                    String serverEstr="";
                                    if( !quiet )
                                        serverEstr+="errors\n" + serverErrors+"/n";
                                    CompilationProblem Servercerror = new CompilationProblem(serverEstr+="<errorlist><error>Error has occured, more information to come later</error></errorlist>\0");
                                    errors.add(Servercerror);
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
                    if(!quiet)
                        System.err.println( "server shutdown" );
                }
                // FOR JESSE PLAY
                //else if( options.play() ){
                    //Play.run();
                //}
                else if( options.getFiles().size() == 0 ){
                    System.err.println("No files provided, must have at least one file.");
                }
                else{ 
                    //Plain cmd line mode

                    //parse each file and put them in a list of Programs
                    ArrayList<Program> programs = new ArrayList<Program>( options.getFiles().size() );
                    ArrayList<String> fileNames = new ArrayList<String>( options.getFiles().size() );
                    
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

                        fileNames.add( file );
			
                        //checks if dependence analysis flag is set.
                        //If the flag is set then the type of dependence test that needs to be applied.
                        if(options.danalysis()){                            
                            //Program prog = null;
                            dependenceAnalyzerOptions(options,file);
                            if( !quiet )
                              System.err.println("Dependence Tester");
                              if(options.bj()){
                                if( !quiet )
                                 System.err.println("Dependence Analysis with Banerjee's Test");
                            } 
                        }
                        
                        if( options.matlab() ){
                            //translate each file from matlab to natlab
                            //if successful the result will end up as a StringReader instance 
                            // in fileReader variable
                            //try{
                            if( !quiet )
                                System.err.println("Translating "+file+" to Natlab");
                            fileReader = translateFile( file, errors );
                            
                            if( errors.size() > 0 )
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
                        if( errors.size() > 0 )
                            System.err.print( errors.toString() );
                        
                        if( prog == null ){
                            System.err.println("\nSkipping " + file);
                            break;
                        }
                        
                        //Weeding checks
                        //if(prog.errorCheck()){
                        //    System.err.println("\nWeeding Failed, Skipping: " + file);
                        //    break;
                        //}
                        
                        programs.add(prog);
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

                        //NOTE: This might be fragile, what if Program nodes are removed?
                        if( options.od().length() > 0 ){
                            File outputDir = new File( options.od() );
                            try{
                                if( !outputDir.exists() && !outputDir.mkdirs() ){
                                    System.err.println( "Could not create output directory." );
                                    System.err.println( "Some directories may have been created though." );
                                    System.exit(1);
                                }
                            }catch( SecurityException e ){
                                System.err.println( "Security error, is there a security manager active?" );
                                System.err.println( e );
                                System.exit(1);
                            }
                            FileWriter outFile;
                            String fileName;
                            for( int i = 0; i<fileNames.size(); i++ ){
                                    fileName = (new File(fileNames.get(i)) ).getName();
                                try{
                                    outFile = new FileWriter( new File( outputDir, fileName ) );
                                    outFile.write( cu.getProgram(i).getPrettyPrinted() );
                                    outFile.close();
                                }catch( IOException e ){
                                    System.err.println( "Problem writing to file "+new File( outputDir, fileName ) );
                                    System.err.println( e );
                                    System.exit(1);
                                }
                            }
                        }
                        else
                            System.out.println(cu.getPrettyPrinted());
                    }
                    else if( options.vfpreorder() ){
                        VFPreorderAnalysis a = new VFPreorderAnalysis( cu );
                        a.analyze();
                        System.out.println(cu.getPrettyPrinted());
                        System.out.println( a.getCurrentSet().toString() );

                        FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, VFStructuralForwardAnalysis.class );
                        System.out.println( testTool.run() );

                        System.out.println( "********\ndoing vf data collection");
                        VFDataCollector vfdata = new VFDataCollector( cu );
                        vfdata.analyze();

                        System.out.println( "********\ndoing use data collection");
                        UseDataCollector udata = new UseDataCollector( cu );
                        udata.analyze();

                        System.out.println( "********\ndoing assigned data collection");
                        AssignedDataCollector adata = new AssignedDataCollector( cu );
                        adata.analyze();

                        System.out.println( "use "+udata.getCurrentSet().toString() );
                        System.out.println( "v or f"+vfdata.getCurrentSet().toString() );

                        int[] counts = vfdata.countData("USE");
                        System.out.println( counts[0]+"/"+counts[1]+"/"+counts[2] );
                        System.out.println( "assigned "+adata.getCurrentSet().toString() );
                        counts = adata.countData("USE");
                        System.out.println( counts[0]+"/"+counts[1]+"/"+counts[2] );
                    }
                    else if( options.run() ){
                        //This is for 621 example
                        /*FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, DefiniteAssignment.class );
                        //System.out.println( testTool.run());

                        System.out.println("********************");

                        testTool = new FlowAnalysisTestTool( cu, LiveVar.class );
                        //System.out.println( testTool.run());
                        System.out.println("********************");


                        System.out.println( JesseRewrite.addInstruments( cu ).getPrettyPrinted() );*/
                        //FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, IsScalarSimpleAnalysis.class );
                        //System.out.println( testTool.run() );
                        
                        /*MultiReturnRewrite rr = new MultiReturnRewrite( cu );
                        ASTNode rrDone = rr.transform();
                        System.out.println( rrDone.getPrettyPrinted() );*/
                        //FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, VFStructuralForwardAnalysis.class );
                        //System.out.println( testTool.run() );

                        //RightThreeAddressRewrite rtar = new RightThreeAddressRewrite( cu );
                        //ASTNode rtarDone = rtar.transform();
                        //System.out.println( rtarDone.getPrettyPrinted() );
                        //FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, HandlePropagationAnalysis.class );
                        //System.out.println( "running");
                        //System.out.println( testTool.run() );
                        //System.out.println("\n\n********\n");
                        HashMap<String,ASTNode> programNameMap = new HashMap();
                        String fname;
                        for( int i = 0; i<fileNames.size(); i++ ){
                            String pname;
                            fname = fileNames.get(i);
                            ASTNode prog = cu.getProgram(i);
                            if( prog instanceof Script ){
                                pname = new File(fname).getName();
                                pname = pname.substring(0,pname.length()-2);
                                programNameMap.put(pname, prog);
                            }
                            else{
                                FunctionList funclist = (FunctionList)prog;
                                for( Function func : funclist.getFunctions() )
                                    programNameMap.put(func.getName(),func);
                            }
                        }
                        CallGraphBuilder graph = new CallGraphBuilder( cu, programNameMap );
                        graph.run();
                        System.out.println(graph);
                        FileWriter dotfile;
                        try{
                            dotfile = new FileWriter( new File( "graph1.dot" ) );
                            dotfile.write( graph.toDot(true));
                            dotfile.close();
                            dotfile = new FileWriter( new File( "graph2.dot" ) );
                            dotfile.write( graph.toDot(false));
                            dotfile.close();
                        }catch( IOException e){
                            System.err.println("no dot output produced for call graph");
                        }
                    }
                    else if( options.df() ){
                        String mainFileName = "";
                        if( options.main().length()>0 ){
                            mainFileName = options.main();
                        }
                        else if( options.in().size()>0 ){
                            mainFileName = (String)options.in().get(0);
                        }
                        else{
                            System.err.println("no main file given, aborting!");
                            System.exit(1);
                        }

                        List<String> inFileNames = options.in();
                        List<String> pathEntries = options.lp();

                        File f1 = new File("foo/../bar/");
                        File f2 = new File("bar/");
                        File f3 = new File("bar");

                        System.out.println( f1.compareTo(f2) );
                        System.out.println( f2.compareTo(f3) );
                        System.out.println( (new DirectoryFileFilter()).accept(f2) );
                        System.out.println( (new DirectoryFileFilter()).accept(f3) );
                            
                            
                    }
                }
            }
            //TODO-JD: probably shouldn't need to exit?
            System.exit(0);
        }
    }
    
/*  
 *  
 * This function computes dependence information on a bunch of files in a directory or on a single file..
 * 
 */ 
        
private static void dependenceAnalyzerOptions(Options options,String name){ //name is the directory name for dir option.
  System.err.println("Dependence Analysis");  
  if(options.dir()){	  
	  File file = new File(name);	
      boolean exists = file.exists();  
      if(exists){
    	  String[] fileName=file.list();
    	  for(int i=0;i<fileName.length;i++){
    		  if(!fileName[i].startsWith("drv") && fileName[i].endsWith(".m")){    			  
    			  ArrayList errors=new ArrayList();
    			  Reader fileReader = new StringReader("");
    			  fileReader=translateFile(name + "/" + fileName[i],errors);
    			  Program prog = parseFile(name +"/" + fileName[i],  fileReader, errors );	
    			  if(options.prof()){    				
    				  ProfilerDriver pDriver=new ProfilerDriver();
    				  pDriver.setDirName("Dep"+name);
    			      pDriver.setFileName(fileName[i]);
    			      pDriver.traverseFile(prog);     			      
    			      pDriver.generateInstrumentedFile(prog); 
    			  }
    			  if(options.auto()){
    				  McFlatDriver mDriver=new McFlatDriver();
    				  mDriver.setDirName("Dep"+name);//this sets the directory name where all files will be placed after dependence calculation.
    				  mDriver.setFileName(fileName[i]);
    				  mDriver.setFlag(true);
    				  mDriver.checkFiles(prog,true); //if this flag is true apply loop transformations automatically  
    			  }
    			  if(options.anno()){
    				  McFlatDriver mDriver=new McFlatDriver();
    				  mDriver.setDirName("Dep"+name);//this sets the directory name where all files will be placed after dependence calculation.
    				  mDriver.setFileName(fileName[i]);
    				  mDriver.setFlag(false);
    				  mDriver.checkFiles(prog,false); //if this flag is true apply loop transformations automatically  
    			  }   			      			  
    			  if(options.heur()){
    				  StringTokenizer st = new StringTokenizer(fileName[i],".");		
    				  String fName=st.nextToken();    				  
    				  File file1 = new File(name+"/"+fName+".xml");	
    			      boolean exists1 = file1.exists();      
    				  if(exists1){ //this step will generate rangeData.xml 
    					HeuristicEngineDriver hDriver=new HeuristicEngineDriver(fileName[i]);
    					hDriver.setDirName("Dep"+name);
    					hDriver.parseXmlFile();
    				  }
    				  else{
    					System.out.println("Heuristic Engine:InComplete information Data file not present");    					
    				  }	  
    			  }//end of if*/
    			 /* if(options.sda()){
    				  //System.out.println("Compiletime dependence analysis");    				  
    				  McFlatDriver mDriver=new McFlatDriver();
    				  mDriver.setDirName("Dep"+name);//this sets the directory name where all files will be placed after dependence calculation.
    				  mDriver.setFileName(fileName[i]);
    				  mDriver.checkFiles(prog,false); 
    				  //System.out.println(prog.getPrettyPrinted());
    			   }//end of if */ 
    		  }//end of if
    	  }//end of for  	  
      }//end of if      
      System.exit(0);
  }//end of if 
  
  if(options.prof()){ //This is the option when dependence calculation needs to be done on a single file
	  ArrayList errors=new ArrayList();
	  Reader fileReader = new StringReader("");
	  fileReader=translateFile(name ,errors);
	  Program prog = parseFile(name ,  fileReader, errors );  
	if(prog!=null){
	  System.out.println("Profiling in file");	
	  ProfilerDriver pDriver=new ProfilerDriver();
	  StringTokenizer st = new StringTokenizer(name,".");
	  String dirName=st.nextToken();
	  pDriver.setDirName("Dep"+dirName);
      pDriver.setFileName(name);
      pDriver.traverseFile(prog);      
      pDriver.generateInstrumentedFile(prog);
    }//end of if
  }//end of if
  
  if(options.heur()){
	  ArrayList errors=new ArrayList();
	  Reader fileReader = new StringReader("");
	  StringTokenizer st = new StringTokenizer(name,".");
	  String dirName=st.nextToken();
	  fileReader=translateFile(name ,errors);
	  Program prog = parseFile( name ,  fileReader, errors );
	  System.out.println("heuristic engine");
	  File file = new File("Dep"+dirName+"/"+name+".xml");	
      boolean exists = file.exists();      
	  if(exists){ //this step will generate rangeData.xml 
		HeuristicEngineDriver hDriver=new HeuristicEngineDriver(name+".m");
		hDriver.parseXmlFile();
	  }
	  else{
		System.out.println("Heuristic Engine:InComplete information Data file not present");
		return;
	  }	  
  }//end of if*/
  
  /*if(options.rda()){
	  ArrayList errors=new ArrayList();
	  Reader fileReader = new StringReader("");
	  fileReader=translateFile(name,errors);
	  Program prog = parseFile( name,  fileReader, errors );
	  System.out.println("Runtime dependence analysis");
	  parseProgramNode(prog,name,true);	    
   }//end of if*/  
  
  /*if(options.sda()){
	  System.out.println("Compiletime dependence analysis");
	  ArrayList errors=new ArrayList();
	  Reader fileReader = new StringReader("");
	  fileReader=translateFile(name ,errors);
	  Program prog = parseFile(name ,  fileReader, errors );
	  McFlatDriver mDriver=new McFlatDriver();
	  mDriver.setDirName("Dep"+name);//this sets the directory name where all files will be placed after dependence calculation.
	  mDriver.setFileName(name);
	  mDriver.checkFiles(prog,false);  
	  //parseProgramNode(prog,fileName,false);	    
   }*/  
  //cal = Calendar.getInstance();        
  //System.out.println(cal.getTime());
  System.exit(0);
     
 }//end of function
    
    
    /*
     * This program serves as a driver program for LoopAnalysis and Transformation framework.
     * 1.It instruments the input .m File.
     * 2.Then invokes the heuristicEngine to determine appropriate range values.
     * 3.Invokes dependence Analysis Driver and applies transformations on the loops if applicable.
     * 4.Then writes the transformed code to a new file.
     * 
     */
/*private static void parseProgramNode(Program prog,String testType,String fileName){
	
    	ProfilerDriver pDriver=new ProfilerDriver();
    	pDriver.setFileName(fileName);
    	pDriver.traverseFile(prog);
        //ForVisitor forVisitor = new ForVisitor();
        //forVisitor.setProfDriver(pDriver);
        //prog.apply(forVisitor);
        pDriver.generateInstrumentedFile(prog);
        
    	HeuristicEngineDriver hDriver=new HeuristicEngineDriver(fileName);
		hDriver.parseXmlFile();		
        
		
		DependenceAnalysisDriver dDriver=new DependenceAnalysisDriver();
		dDriver.setFileName(fileName);
		String fName=dDriver.getFileName();
		dDriver.setPredictedLoopValues(hDriver.getTable());		
		ArrayList errors = new ArrayList();
		  try{
              Reader fileReader = new FileReader( fName);
              Program program = parseFile( fName,  fileReader, errors );             
      		  dDriver.traverseFile(program);	
          }catch(FileNotFoundException e){
              System.err.println("File "+fName+" not found!\nAborting");
              System.exit(1);
          }
          
          //TODO:Put a check that if there is no change prog node then dont write to the file
         /* Writer output; 
          StringTokenizer st = new StringTokenizer(fName,".");
      	  String fiName=st.nextToken();
          File file = new File(fiName+"/"+"transformed"+ fiName + ".m");
          try {
			  output = new BufferedWriter(new FileWriter(file));
			  output.write(prog.getPrettyPrinted());
	          output.close();
		   } catch (IOException e) {			  
			  e.printStackTrace();
		 } */  	
		   
	    //Reader fileReader = new StringReader(fName);
	    //ArrayList errors = new ArrayList();
	    //System.out.println( fName );
	    //fileReader=translateFile(fName,errors);
		//Program program = parseFile( fName,  fileReader, errors );
		//System.out.println(program.dumpTreeAll());
		//dDriver.traverseFile(program);	
        
        //pDriver.traverseProgram(fileName); 
        //System.out.println(prog.getPrettyPrinted());
    	
    	
    	
    	//Profiler prof =new Profiler();
    	//prof.setProg(prog);
    	//prof.setFileName(fileName);
    	//System.out.println(prog.dumpTreeAll());
    	//prof.changeAST();
    	//ProfilerDriver profDriver =new ProfilerDriver(prog);
    	//profDriver.traverseProgram();
    //}
    
    /**
     * Perform the reading and translation of a given file.
     *
     * @param fName    The name of the file to be translated.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, ArrayList<CompilationProblem> errList)
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
                        CompilationProblem translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob+"\n");
                        errList.add(translationcproblem);
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
            }
        }catch(FileNotFoundException e){
            CompilationProblem FileNotFoundcerror = new CompilationProblem("File "+fName+" not found!\nAborting\n");
            errList.add(FileNotFoundcerror);
            return null;
        }
        catch(IOException e){
            CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
            errList.add( IOcerror);
            return null;
        }
        
        return finishTranslateFile(fName, in, prePosMap, errList);
        
    }
    /**
     * Perform the translation of a given string containing source
     * code.
     *
     * @param fName    The name of the file to which the source
     * belongs.
     * @param source   The string containing the source code.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, String source, ArrayList<CompilationProblem> errList)
    {
        BufferedReader in = null;
        PositionMap prePosMap = null;
        try{
            in = new BufferedReader( new StringReader( source ) );
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult){
                in = new BufferedReader( new StringReader( source ) );
            }else {
                in.close();
                if(result instanceof ProblemResult){
                    for(TranslationProblem prob : ((ProblemResult) result).getProblems()){
                        CompilationProblem translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob+"\n");
                        errList.add(translationcproblem);
                    }
                    return null; //terminate early since extraction parser can't work without balanced 'end's
                } else if(result instanceof TranslationResult){
                    TranslationResult transResult = (TranslationResult) result;
                    in = new BufferedReader(new StringReader(transResult.getText()));
                    prePosMap = transResult.getPositionMap();
                }
            }
        }catch(FileNotFoundException e){
            CompilationProblem FileNotFoundcerror = new CompilationProblem("File "+fName+" not found!\nAborting\n");
            errList.add(FileNotFoundcerror);
            return null;
        }
        catch(IOException e){
            CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
            errList.add( IOcerror);
            return null;
        }
        
        return finishTranslateFile(fName, in, prePosMap, errList);
    }
    /**
     * Perform the translation from a given Reader containing source code.
     *
     * @param fName    The name of the file to which the source belongs.
     * @param source   The string containing the source code.
     * @param errList  A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    public static Reader translateFile(String fName, Reader source, ArrayList<CompilationProblem> errList)
    {
    	//we'll just build a String out of the source and call the method that takes a String
    	//TODO - this should be done directly from the reader
    	BufferedReader buffer = new BufferedReader(source);
    	StringBuilder builder = new StringBuilder();
    	try{
    		while(true){
    			String line = buffer.readLine();
    			if (line == null) break;
    			builder.append(line);
    		}
    	}catch(IOException e){
    		CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
    		errList.add(IOcerror);
    		return null;    
    	}
    	return translateFile(fName,builder.toString(),errList);
    }
    
    /**
     * Translate a given file and return a reader to access the
     * translated version. This method is used by the translateFile
     * methods and performs the final part of the translation.
     *
     * @param fName     The name of the file to which the source
     * belongs.
     * @param in        The source.
     * @param prPosMap  The position map to map from the original
     * translated file positions and original file positions.
     * @param errList   A list of errors for error collection.
     * 
     * @return A reader object giving access to the translated
     * source.
     */
    private static Reader finishTranslateFile(String fName, BufferedReader in, 
                                              PositionMap prePosMap, ArrayList errList)
    {
        try{
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
                    CompilationProblem Translationcproblem = new CompilationProblem(prob.getLine(),prob.getColumn(),prob.getMessage()+"\n");
                    errList.add(Translationcproblem);
                    
                }
                return null;
            }
        }catch(FileNotFoundException e){
            CompilationProblem FileNotFoundcerror = new CompilationProblem("File "+fName+" not found!\nAborting\n");
            errList.add(FileNotFoundcerror);
            return null;
        }
        catch(IOException e){
            CompilationProblem IOcerror = new CompilationProblem("Error translating "+fName+"\n"+e.getMessage());
            errList.add( IOcerror);
            return null;
        }
        
    }
    
    /**
     * Parse a given file and return the Program ast node. This
     * expects the program to already be in natlab syntax.
     *
     * @param fName  The name of the file being parsed.
     * @param file   The reader object containing the source being
     * parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseFile(String fName, Reader file, ArrayList<CompilationProblem> errList )
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
                    String delim = "],[";
                    for( String error : parser.getErrors()){
                        //return an array of string with {line, column, msg}
                        CompilationProblem parserError;
                        try{
                            String[] message = error.split(delim);
                            parserError = new CompilationProblem( Integer.valueOf(message[0]).intValue(),
                                                                  Integer.valueOf(message[1]).intValue(),
                                                                  message[3]);
                        }
                        catch( PatternSyntaxException e ){
                            parserError = new CompilationProblem( error );
                        }
                        errList.add(parserError);}
                    prog = null;
                }
                return prog;
                
            }catch(Parser.Exception e){
                String ErrorString= e.getMessage()+"\n";
                for(String error : parser.getErrors()) {
                    ErrorString+= error + "\n";
                }
                CompilationProblem Parsercerror = new CompilationProblem(ErrorString);
                errList.add(Parsercerror);
                return null;
            } 
        }catch(FileNotFoundException e){
            CompilationProblem FileNotFoundcerror = new CompilationProblem("File "+fName+" not found!\nAborting\n");
            errList.add(FileNotFoundcerror);
            return null;
        }
        catch(IOException e){
            CompilationProblem IOcerror = new CompilationProblem("Error parsing "+fName+"\n"+e.getMessage());
            errList.add( IOcerror);
            return null;
        }
    }

    /**
     * Parse a given file and return the Program ast node. This
     * expects the program to already be in natlab syntax.
     *
     * @param fName  The name of the file being parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseFile(String fName, ArrayList<CompilationProblem> errList ){
    	try {
    		FileReader reader = new FileReader(fName);
    		Program program = parseFile(fName,reader,errList);    	
    		if (program == null){
    		    System.err.println(errList);    		    
    		}
    		
    		return program;
    	} catch (FileNotFoundException e){
    	    System.err.println("File "+fName+" not found!");
    	    return null;
    	}    
    }
    
    /**
     * Parse a given file as a Matlab file and return the Program ast node.
     *
     * @param fName  The name of the file being parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseMatlabFile(String fName, ArrayList<CompilationProblem> errList ){
    	Reader source = Main.translateFile( fName, errList );
        if( source == null ) return null;
        Program program = Main.parseFile( fName, source, errList );
        return program;
    }
    
    
    /**
     * Parse a given file as a Matlab file and return the Program ast node.
     *
     * @param fName  The name of the file being parsed.
     * @param file   The reader object containing the source being
     * parsed.
     * @param errList   A list of errors for error collection.
     *
     * @return The Program node for the given file being parsed if no
     * errors. If an error occurs then null is returned. 
     */
    public static Program parseMatlabFile(String fName, Reader file, ArrayList<CompilationProblem> errList ){
    	//TODO - something should be done about the mapping file
    	//translate into natlab
    	Reader natlabFile = translateFile(fName, file, errList);
    	//parse natlab
    	return parseFile( fName,  natlabFile,  errList );
    }

    
    private static boolean processCmdLine(String[] args)
    {
        try{
            options.parse( args );
            
            if( args.length == 0 ){
                System.err.println("No options given\n" +
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
