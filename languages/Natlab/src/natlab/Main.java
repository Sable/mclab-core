// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import natlab.options.Options;
import natlab.server.NatlabServer;
import natlab.toolkits.DependenceAnalysis.HeuristicEngineDriver;
import natlab.toolkits.DependenceAnalysis.McFlatDriver;
import natlab.toolkits.DependenceAnalysis.ProfilerDriver;
import natlab.toolkits.analysis.defassigned.DefinitelyAssignedAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.filehandling.DirectoryFileFilter;
import ast.CompilationUnits;
import ast.Program;

import com.google.common.collect.Lists;

/**
 * Main entry point for McLab compiler. Includes a main method that
 * deals with command line options and performs the desired
 * functions. Also includes static methods used to simplify tasks such
 * as parsing program code and translating from matlab to natlab.
 */
public class Main
{
  private static Options options;


  private static boolean quiet;

  private static void log(String message) {
    logIf(true, message);
  }

  private static void logIf(boolean condition, String message) {
    if (!quiet && condition) {
      System.err.println(message);
    }
  }

  /**
   * Main method deals with command line options and execution of
   * desired functions.
   */
  public static void main(String[] args) throws Exception
  {	
    if (args.length == 0) {
      System.err.println("No options given\nTry -help for usage");
      return;
    }
    List<CompilationProblem> errors = Lists.newArrayList();
    options = new Options();
    options.parse(args);
    if (options.help()) {
      System.err.println(options.getUsage());
      return;
    }
    quiet = options.quiet();  //check if quiet mode is active

    logIf(options.e(), "exhaustive list");
    logIf(options.d(), "dynamic linking");

    //fortran neither uses the below parser, nor is a server
    /*                if( options.fortran() ){ //begin fortran
                    //if( options.getFiles().size() == 0 ){
                    //    System.err.println("No files provided, must have at least one file.");
                    //    System.exit(1);
                    //}
                    //System.out.println("compiling to fortran "+options.getFiles());
                    //String[] fargs = {"-d",(String)options.getFiles().get(0)};
                    //System.out.println("calling McFor with "+fargs);
                    natlab.tame.mc4.Mc4.main(args);
                    return;
                } //end fortran
     */

    if (options.show_pref()) {
      System.out.println("preferences:");
      Map<String,Object> prefs = NatlabPreferences.getAllPreferences();
      for (String key : prefs.keySet()) {
        System.out.println(key + "=" + prefs.get(key));
      }
    }

    if (options.pref()) {
      NatlabPreferences.modify(options);
      return;
    }

    if (options.version()) {
      System.out.println("The version of this release is: " + VersionInfo.getVersion());
      return;
    }

    if (options.tame()){
      //TODO - the parsing of the options should probably not be done by the tamer tool
      natlab.tame.TamerTool.main(options);
      return;
    }

    if (options.server()) {
      NatlabServer.create(options).start();
      return;
    }

    if (options.getFiles().isEmpty()) {
      System.err.println("No files provided, must have at least one file.");
      return;
    }
    //Plain cmd line mode

    //parse each file and put them in a list of Programs
    ArrayList<Program> programs = new ArrayList<Program>( options.getFiles().size() );
    ArrayList<String> fileNames = new ArrayList<String>( options.getFiles().size() );
    TreeMap<String, Program> programMap=new TreeMap<String, Program>();
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
        fileReader = Parse.translateFile( file, errors );

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
      Program prog = Parse.parseFile( file,  fileReader, errors );

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
      String[] parts=file.split(File.separator);
      String filename= parts[parts.length-1];
      prog.setName(filename.substring(0,filename.length()-2)); //#FIXME Soroush: find the extension? 

      prog.setFullPath(file);

      programMap.put(prog.getName(), prog);
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

      DefinitelyAssignedAnalysis a = new DefinitelyAssignedAnalysis( cu );
      a.analyze();
      /*
                        MScriptInliner inliner = new MScriptInliner(cu, programMap);
                        for (Program p: programMap.values()){
                    		if (p instanceof FunctionList){                    		
                    			FunctionList f = (FunctionList)p;
                    			inliner.inlineAllFunctions((Function)f.getChild(0).getChild(0));
                    		}
                        }
       */
      //VFFlowSensitiveAnalysis a = new VFFlowSensitiveAnalysis( cu );

      //a.analyze();
      //System.out.println(cu.getPrettyPrinted());
      //                        System.out.println("Sensitive"+ a.getCurrentOutSet().toString() );

      cu.setRootFolder(new natlab.toolkits.filehandling.genericFile.FileFile("/home/soroush/Examples/PathEx/"));
      //                        CDAnalysis c = new CDAnalysis( cu );
      FlowAnalysisTestTool testTool = new FlowAnalysisTestTool( cu, ReachingDefs.class);
      System.out.println(testTool.run());




      /*

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
       */
    }
    /*else if( options.run() ){
                        Validator v = new Validator( cu );
                        v.analyze();
                        if( v.isValid() )
                            System.out.println("VALID: The input is valid McLAST");
                        else{
                            System.out.println("INVALID: The input is not valid McLAST");
                            System.out.println("reasons are:");
                            System.out.println(v.getReasons());
                        }

                    }*/ //XU added block comment
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
            fileReader=Parse.translateFile(name + "/" + fileName[i],errors);
            Program prog = Parse.parseFile(name +"/" + fileName[i],  fileReader, errors );	
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
      fileReader=Parse.translateFile(name ,errors);
      Program prog = Parse.parseFile(name ,  fileReader, errors );  
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
      fileReader=Parse.translateFile(name ,errors);
      Program prog = Parse.parseFile( name ,  fileReader, errors );
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
}
