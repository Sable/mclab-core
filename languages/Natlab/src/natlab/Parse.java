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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import matlab.CompositePositionMap;
import matlab.FunctionEndScanner;
import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;
import matlab.MatlabParser;
import matlab.OffsetTracker;
import matlab.PositionMap;
import matlab.TextPosition;
import matlab.TranslationProblem;

import org.antlr.runtime.ANTLRReaderStream;

import ast.CompilationUnits;
import ast.Program;
import ast.Stmt;
import beaver.Parser;

/**
 * Set of static methods to parse files.
 * Files should be read from genericfiles
 *   - there may be something that reads genericfiles somewhere ... in main, maybe?
 * 
 */
public class Parse {
  /**
   * code that should get executed before any parsing
   */
  private static void parsePreamble() {
    Stmt.setDefaultOutputSuppression(false);
  }

  /**
   * code that should get executed after any parsing
   */
  private static void parsePostscript() {
    Stmt.setDefaultOutputSuppression(true);
  }

  private static Reader fileReader(String fName, List<CompilationProblem> errors) {
    try {
      return new FileReader(fName);
    } catch (FileNotFoundException e) {
      errors.add(new CompilationProblem("File not found: %s\nAborting!\n", fName));
      return null;
    }
  }

  /**
   * Convert a Reader to a String by reading all its contents.
   * This is useful when a Reader might need to be read multiple times; using
   * mark() and reset() is not always practical, so in those cases, use this method, and
   * create as many StringReaders as you need.
   */
  private static String readerToString(Reader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader in = new BufferedReader(reader);
    String line = null;
    while ((line = in.readLine()) != null) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }

  /**
   * Parse a given file and return the Program ast node. This
   * expects the program to already be in natlab syntax.
   *
   * @param fName  The name of the file being parsed.
   * @param file   The reader object containing the source being
   * parsed.
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseFile(String fName, Reader file, List<CompilationProblem> errors) {
    parsePreamble();
    NatlabParser parser = new NatlabParser();
    NatlabScanner scanner = null;
    CommentBuffer cb = new CommentBuffer();
    parser.setCommentBuffer(cb);
    try {
      scanner = new NatlabScanner(file);
      scanner.setCommentBuffer(cb);
      try {
        Program program = (Program) parser.parse(scanner);
        if (parser.hasError()) {
          String delim = "],[";
          for (String error : parser.getErrors()) {
            // return an array of string with {line, column, msg}
            CompilationProblem parserError;
            try {
              String[] message = error.split(delim);
              parserError = new CompilationProblem(Integer.valueOf(message[0]).intValue(),
                  Integer.valueOf(message[1]).intValue(),
                  message[3]);
            } catch (PatternSyntaxException e) {
              parserError = new CompilationProblem(error);
            }
            errors.add(parserError);
          }
          program = null;
        }
        parsePostscript();
        return program;
      } catch(Parser.Exception e) {
        StringBuilder errorMessage = new StringBuilder(e.getMessage()).append("\n");
        for (String error : parser.getErrors()) {
          errorMessage.append(error).append("\n");
        }
        errors.add(new CompilationProblem(errorMessage.toString()));
        return null;
      } 
    } catch(IOException e) {
      errors.add(new CompilationProblem("Error parsing %s\n%s", fName, e.getMessage()));
      return null;
    }
  }

  /**
   * Parse a given file and return the Program ast node. This
   * expects the program to already be in natlab syntax.
   *
   * @param fName  The name of the file being parsed.
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseFile(String fName, List<CompilationProblem> errors) {
    Reader reader = fileReader(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseFile(fName, reader, errors);
  }

  /**
   * Parse a given file as a Matlab file and return the Program ast node.
   *
   * @param fName  The name of the file being parsed.
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseMatlabFile(String fName, List<CompilationProblem> errors) {
    Reader source = translateFile(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseFile(fName, source, errors);
  }

  /**
   * Parse a given file as a Matlab file and return the Program ast node.
   *
   * @param fName  The name of the file being parsed.
   * @param file   The reader object containing the source being
   * parsed.
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseMatlabFile(String fName, Reader file, List<CompilationProblem> errors) {
    // TODO - something should be done about the mapping file
    Reader natlabFile = Parse.translateFile(fName, file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseFile(fName, natlabFile, errors);
  }

  public static CompilationUnits parseMatlabFiles(List<String> files, List<CompilationProblem> errors) {
    CompilationUnits cu = new CompilationUnits();
    for (String fName : files) {
      cu.addProgram(parseMatlabFile(fName, errors));
    }
    return cu;
  }

  /**
   * Perform the reading and translation of a given file.
   *
   * @param fName    The name of the file to be translated.
   * @param errors  A list of errors for error collection.
   * 
   * @return A reader object giving access to the translated
   * source.
   */
  public static Reader translateFile(String fName, List<CompilationProblem> errors) {
    Reader reader = fileReader(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return translateFile(fName, reader, errors);
  }

  /**
   * Perform the translation of a given string containing source
   * code.
   *
   * @param fName    The name of the file to which the source
   * belongs.
   * @param source   The string containing the source code.
   * @param errors  A list of errors for error collection.
   * 
   * @return A reader object giving access to the translated
   * source.
   */
  public static Reader translateFile(String fName, String source, List<CompilationProblem> errors) {
    return translateFile(fName, new StringReader(source), errors);
  }

  /**
   * Perform the translation from a given Reader containing source code.
   *
   * @param fName    The name of the file to which the source belongs.
   * @param source   The string containing the source code.
   * @param errors  A list of errors for error collection.
   * 
   * @return A reader object giving access to the translated
   * source.
   */
  public static Reader translateFile(String fName, Reader source, List<CompilationProblem> errors)
  {
    PositionMap prePosMap = null;
    try {
      String sourceAsString = readerToString(source);
      BufferedReader in = new BufferedReader(new StringReader(sourceAsString));
      FunctionEndScanner.Result result = new FunctionEndScanner(in).translate();
      if (result instanceof NoChangeResult) {
        in = new BufferedReader(new StringReader(sourceAsString));
      } else if (result instanceof TranslationResult) {
        TranslationResult transResult = (TranslationResult) result;
        in = new BufferedReader(new StringReader(transResult.getText()));
        prePosMap = transResult.getPositionMap();
      } else if (result instanceof ProblemResult) {
        for (TranslationProblem problem : ((ProblemResult) result).getProblems()) {
          errors.add(new CompilationProblem(problem.getLine(), problem.getColumn(),
              problem.getMessage() + "\n"));
        }
        return null; // terminate early since extraction parser can't work without balanced 'end's
      }
      OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
      List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
      String destText = MatlabParser.translate(new ANTLRReaderStream(in),
          1, 1, offsetTracker, problems);
      if (problems.isEmpty()) {
        PositionMap posMap = offsetTracker.buildPositionMap();
        if (prePosMap != null) {
          posMap = new CompositePositionMap(posMap, prePosMap);
        }
        // TODO-JD: do something with the posMap
        return new StringReader(destText);
      }
      for (TranslationProblem problem : problems) {
        errors.add(new CompilationProblem(problem.getLine(), problem.getColumn(),
            problem.getMessage() + "\n"));
      }
      return null;
    } catch (IOException e) {
      errors.add(new CompilationProblem("Error translating %s\n%s", fName, e.getMessage()));
      return null;
    }
  }
}
