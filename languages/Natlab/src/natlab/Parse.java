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
import natlab.toolkits.filehandling.genericFile.GenericFile;

import org.antlr.runtime.ANTLRReaderStream;

import ast.CompilationUnits;
import ast.Program;
import ast.Stmt;
import beaver.Parser;

/**
 * Set of static methods to parse files.
 * 
 * TODO(isbadawi): A lot of cruft in this class because it catches exceptions and populates a
 * List<CompilationProblem>. Investigate changing API to use (possibly wrapped) exceptions instead.
 */
public class Parse {
  private static Reader fileReader(String fName, List<CompilationProblem> errors) {
    try {
      return new FileReader(fName);
    } catch (FileNotFoundException e) {
      errors.add(new CompilationProblem("File not found: %s\nAborting!\n", fName));
      return null;
    }
  }

  private static Reader fileReader(GenericFile file, List<CompilationProblem> errors) {
    try {
      return file.getReader();
    } catch (UnsupportedOperationException e) {
      errors.add(new CompilationProblem("Reading not supported for %s\n", file.getName()));
      return null;
    } catch (IOException e) {
      errors.add(new CompilationProblem("Error opening %s\n%s", file.getName(), e.getMessage()));
      return null;
    }
  }

  /**
   * Convert a Reader to a String by reading all its contents.
   * This is useful when a Reader might need to be read multiple times; using
   * mark() and reset() is not always practical, so in those cases, use this method, and
   * create as many StringReaders as you need.
   * 
   * TODO(isbadawi): Move this to a more general utils class instead of making it public static here
   */
  public static String readerToString(Reader reader) throws IOException {
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
    Stmt.setDefaultOutputSuppression(false);
    NatlabParser parser = new NatlabParser();
    NatlabScanner scanner = new NatlabScanner(file);
    CommentBuffer cb = new CommentBuffer();
    parser.setCommentBuffer(cb);
    scanner.setCommentBuffer(cb);
    Program program = null;
    try {
      program = (Program) parser.parse(scanner);
    } catch (Parser.Exception e) {
      StringBuilder errorMessage = new StringBuilder(e.getMessage()).append("\n");
      for (String error : parser.getErrors()) {
        errorMessage.append(error).append("\n");
      }
      errors.add(new CompilationProblem(errorMessage.toString()));
      return null;
    } catch (IOException e) {
      errors.add(new CompilationProblem("Error parsing %s\n%s", fName, e.getMessage()));
      return null;
    }
    if (parser.hasError()) {
      for (String error : parser.getErrors()) {
        // return an array of string with {line, column, msg}
        String[] message = error.split("\\],\\[");
        errors.add(new CompilationProblem(Integer.parseInt(message[0]),
            Integer.parseInt(message[1]), message[3]));
      }
      program = null;
    }
    Stmt.setDefaultOutputSuppression(true);
    return program;
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

  public static Program parseFile(GenericFile file, List<CompilationProblem> errors) {
    Reader reader = fileReader(file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseFile(file.getName(), reader, errors);
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
    Reader natlabFile = translateFile(fName, file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseFile(fName, natlabFile, errors);
  }

  public static Program parseMatlabFile(GenericFile file, List<CompilationProblem> errors) {
    Reader reader = fileReader(file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseMatlabFile(file.getName(), reader, errors);
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

  public static Reader translateFile(GenericFile file, List<CompilationProblem> errors) {
    Reader reader = fileReader(file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return translateFile(file.getName(), reader, errors);
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
