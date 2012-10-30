package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import natlab.CompilationProblem;
import natlab.Parse;
import natlab.options.Options;
import ast.Program;

import com.google.common.collect.Lists;

public class DependenceAnalysisTool {
  private static boolean quiet;
  private static void log(String message) {
    if (!quiet) {
      System.err.println(message);
    }
  }
  /* 
   * This function computes dependence information on a bunch of files
   * in a directory or on a single file.
   */ 
  public static void run(Options options, String name) {
    quiet = options.quiet();
    log("Dependence Tester");
    if (options.bj()) {
      log("Dependence Analysis with Banerjee's Test");
    }
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