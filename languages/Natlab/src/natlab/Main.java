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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import natlab.options.Options;
import natlab.server.NatlabServer;
import natlab.tame.TamerTool;
import natlab.toolkits.DependenceAnalysis.HeuristicEngineDriver;
import natlab.toolkits.DependenceAnalysis.McFlatDriver;
import natlab.toolkits.DependenceAnalysis.ProfilerDriver;
import natlab.toolkits.analysis.defassigned.DefinitelyAssignedAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.filehandling.DirectoryFileFilter;
import ast.CompilationUnits;
import ast.Program;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

/**
 * Main entry point for McLab compiler. Includes a main method that
 * deals with command line options and performs the desired
 * functions. Also includes static methods used to simplify tasks such
 * as parsing program code and translating from matlab to natlab.
 */
public class Main {
  private static Options options;

  private static void log(String message) {
    logIf(true, message);
  }

  private static void logIf(boolean condition, String message) {
    if (!options.quiet() && condition) {
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

    logIf(options.e(), "exhaustive list");
    logIf(options.d(), "dynamic linking");

    if (options.show_pref()) {
      System.out.println("Preferences:");
      System.out.println(Joiner.on('\n').withKeyValueSeparator(" = ")
          .join(NatlabPreferences.getAllPreferences()));
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
      TamerTool.main(options);
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

    Map<String, Program> programMap = Maps.newTreeMap();
    for (Object o : options.getFiles()) {
      String file = (String) o;

      if(options.danalysis()) {
        dependenceAnalyzerOptions(file);
        log("Dependence Tester");
        logIf(options.bj(), "Dependence Analysis with Banerjee's Test");
      }

      log("Parsing: " + file);
      Program program = null;
      if (options.matlab()) {
        program = Parse.parseMatlabFile(file, errors);
      } else {
        program = Parse.parseFile(file, errors);
      }

      if (!errors.isEmpty()) {
        System.err.println(CompilationProblem.toStringAll(errors));
      }

      if (program == null) {
        System.err.println("Skipping " + file);
        break;
      }

      String filename = new File(file).getName();
      program.setName(filename.substring(0, filename.lastIndexOf('.')));
      program.setFullPath(file);
      programMap.put(program.getName(), program);
    }

    CompilationUnits cu = new CompilationUnits();
    for (Program p : programMap.values()) {
      cu.addProgram(p);
    }

    if (options.xml()) {
      System.out.print(cu.XMLtoString(cu.ASTtoXML()));
      return;
    }

    if (options.pretty()) {
      log("Pretty printing");
      
      if (options.od().length() == 0) {
        System.out.println(cu.getPrettyPrinted());
      } else {
        File outputDir = new File(options.od());
        try {
          if (!outputDir.exists() && !outputDir.mkdirs()) {
            System.err.println( "Could not create output directory." );
            System.err.println( "Some directories may have been created though." );
            System.exit(1);
          }
        } catch(SecurityException e) {
          System.err.println("Security error, is there a security manager active?");
          System.err.println(e);
          System.exit(1);
        }
        for (Map.Entry<String, Program> entry : programMap.entrySet()) {
          File outputFile = new File(outputDir, new File(entry.getKey()).getName());
          try {
            Files.write(entry.getValue().getPrettyPrinted(), outputFile, Charsets.UTF_8);
          } catch (IOException e) {
            System.err.println("Problem writing to file " + outputFile);
            System.err.println(e);
            System.exit(1);
          }
        }
      }
      return;
    }

    if (options.vfpreorder()) {
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
   * This function computes dependence information on a bunch of files
   * in a directory or on a single file.
   */ 
  private static void dependenceAnalyzerOptions(String name) {
    System.err.println("Dependence Analysis");  
    if(options.dir()){	  
      File file = new File(name);	
      boolean exists = file.exists();  
      if(exists){
        String[] fileName=file.list();
        for(int i=0;i<fileName.length;i++){
          if(!fileName[i].startsWith("drv") && fileName[i].endsWith(".m")) {
            List<CompilationProblem> errors = Lists.newArrayList();
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
              else {
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
          }
        }
      }
      System.exit(0);
    }

    if (options.prof()) {
      List<CompilationProblem> errors = Lists.newArrayList();
      Program program = Parse.parseMatlabFile(name, errors);
      if (program != null) {
        log("Profiling in file");	
        ProfilerDriver pDriver=new ProfilerDriver();
        StringTokenizer st = new StringTokenizer(name,".");
        String dirName=st.nextToken();
        pDriver.setDirName("Dep"+dirName);
        pDriver.setFileName(name);
        pDriver.traverseFile(program);      
        pDriver.generateInstrumentedFile(program);
      }
    }

    if (options.heur()) {
      StringTokenizer st = new StringTokenizer(name,".");
      String dirName = st.nextToken();
      log("heuristic engine");
      File file = new File("Dep"+dirName+"/"+name+".xml");
      if (file.exists()) { //this step will generate rangeData.xml 
        HeuristicEngineDriver hDriver = new HeuristicEngineDriver(name + ".m");
        hDriver.parseXmlFile();
      } else {
        System.err.println("Heuristic Engine:InComplete information Data file not present");
        return;
      }
    }

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
  }

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
