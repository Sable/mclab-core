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
import java.util.List;

import natlab.options.Options;
import natlab.server.NatlabServer;
import natlab.tame.TamerTool;
import natlab.toolkits.DependenceAnalysis.DependenceAnalysisTool;
import natlab.toolkits.analysis.defassigned.DefinitelyAssignedAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.filehandling.DirectoryFileFilter;
import ast.CompilationUnits;
import ast.Program;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Main entry point for McLab compiler. Includes a main method that
 * deals with command line options and performs the desired
 * functions.
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
  public static void main(String[] args) throws Exception {	
    if (args.length == 0) {
      System.err.println("No options given\nTry -help for usage");
      return;
    }
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

    if(options.danalysis()) {
      for (Object o : options.getFiles()) {
        DependenceAnalysisTool.run(options, (String) o);
      }
      return;
    }

    List<String> files = Lists.newLinkedList();
    for (Object o : options.getFiles()) {
      files.add((String) o);
    }
    log("Parsing " + Joiner.on(", ").join(files));
    List<CompilationProblem> errors = Lists.newArrayList();
    CompilationUnits cu;
    if (options.matlab()) {
      cu = Parse.parseMatlabFiles(files, errors);
    } else {
      cu = Parse.parseFiles(files, errors);
    }

    if (!errors.isEmpty()) {
      System.err.println(CompilationProblem.toStringAll(errors));
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
        for (Program program : cu.getPrograms()) {
          File outputFile = new File(outputDir, new File(program.getFullPath()).getName());
          Files.createParentDirs(outputFile);
          Files.write(program.getPrettyPrinted(), outputFile, Charsets.UTF_8);
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
}
