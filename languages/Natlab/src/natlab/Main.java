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

import mclint.McLint;
import natlab.options.Options;
import natlab.tame.TamerTool;
import ast.CompilationUnits;
import ast.Program;
import natlab.backends.x10.Mix10;

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
    if (!options.quiet()) {
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

    if (options.pref()) {
      NatlabPreferences.modify(options);
      return;
    }

    if (options.show_pref()) {
      System.out.println("Preferences:");
      System.out.println(Joiner.on('\n').withKeyValueSeparator(" = ")
          .join(NatlabPreferences.getAllPreferences()));
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

    if (options.mclint()) {
      McLint.main(options);
      return;
    }

    if (options.server()) {
      NatlabServer.create(options).start();
      return;
    }

    if (options.mix10c()){
    	Mix10.compile(options);
    }
    
    if (options.getFiles().isEmpty()) {
      System.err.println("No files provided, must have at least one file.");
      return;
    }

    List<String> files = options.getFiles();
    log("Parsing " + Joiner.on(", ").join(files));
    List<CompilationProblem> errors = Lists.newArrayList();
    CompilationUnits cu;
    if (!options.natlab()) {
      cu = Parse.parseMatlabFiles(files, errors);
    } else {
      cu = Parse.parseNatlabFiles(files, errors);
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
          File outputFile = new File(outputDir, program.getFile().getName());
          Files.createParentDirs(outputFile);
          Files.write(program.getPrettyPrinted(), outputFile, Charsets.UTF_8);
        }
      }
      return;
    }
  }
}
