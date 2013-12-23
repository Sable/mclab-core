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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
import natlab.toolkits.filehandling.GenericFile;

import org.antlr.runtime.ANTLRReaderStream;

import ast.CompilationUnits;
import ast.Program;
import ast.Stmt;
import beaver.Parser;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * Set of static methods to parse files.
 * 
 * TODO(isbadawi): A lot of cruft in this class because it catches exceptions and populates a
 * List<CompilationProblem>. Investigate changing API to use (possibly wrapped) exceptions instead.
 */
public class Parse {
  private static Reader fileReader(String fName, List<CompilationProblem> errors) {
    try {
      return Files.newReader(new File(fName), Charsets.UTF_8);
    } catch (FileNotFoundException e) {
      errors.add(new CompilationProblem("File not found: %s\nAborting!", fName));
      return null;
    }
  }

  private static Reader fileReader(GenericFile file, List<CompilationProblem> errors) {
    try {
      return file.getReader();
    } catch (UnsupportedOperationException e) {
      errors.add(new CompilationProblem("Reading not supported for %s", file.getName()));
      return null;
    } catch (IOException e) {
      errors.add(new CompilationProblem("Error opening %s: %s", file.getName(), e.getMessage()));
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
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseNatlabFile(String fName, Reader file, List<CompilationProblem> errors) {
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
      List<String> errorMessages = ImmutableList.<String>builder()
          .add(e.getMessage())
          .addAll(parser.getErrors())
          .build();
      errors.add(new CompilationProblem(Joiner.on('\n').join(errorMessages)));
      return null;
    } catch (IOException e) {
      errors.add(new CompilationProblem("Error parsing %s: %s", fName, e.getMessage()));
      return null;
    }
    if (parser.hasError()) {
      for (String error : parser.getErrors()) {
        errors.add(new CompilationProblem(error));
      }
      return null;
    }
    Stmt.setDefaultOutputSuppression(true);
    if (program != null) {
      program.setFile(GenericFile.create(fName));
      Weeder.check(program, errors);
    }
    return program;
  }
  
  public static class TranslateResult {
    private Reader reader;
    private PositionMap positionMap;
    
    public TranslateResult(Reader reader, PositionMap positionMap) {
      this.reader = reader;
      this.positionMap = positionMap;
    }
    
    public Reader getReader() {
      return reader;
    }
    
    public PositionMap getPositionMap() {
      return positionMap;
    }
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
  public static TranslateResult translateFile(String fName, Reader source, List<CompilationProblem> errors)
  {
    PositionMap prePosMap = null;
    try {
      String sourceAsString = CharStreams.toString(source);
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
      List<TranslationProblem> problems = Lists.newArrayList();
      String destText = MatlabParser.translate(new ANTLRReaderStream(in),
          1, 1, offsetTracker, problems);
      if (problems.isEmpty()) {
        PositionMap posMap = offsetTracker.buildPositionMap();
        if (prePosMap != null) {
          posMap = new CompositePositionMap(prePosMap, posMap);
        }
        return new TranslateResult(new StringReader(destText), posMap);
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
  public static Program parseNatlabFile(String fName, List<CompilationProblem> errors) {
    Reader reader = fileReader(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseNatlabFile(fName, reader, errors);
  }

  /**
   * Parse a given file and return the Program ast node. This
   * expects the program to already be in natlab syntax.
   *
   * @param file The file to be parsed
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseNatlabFile(GenericFile file, List<CompilationProblem> errors) {
    Reader reader = fileReader(file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseNatlabFile(file.getName(), reader, errors);
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
    TranslateResult source = translateFile(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    Program result = parseNatlabFile(fName, source.getReader(), errors);
    if (result != null) {
      result.setPositionMap(source.getPositionMap());
    }
    return result;
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
    TranslateResult natlabFile = translateFile(fName, file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    Program result = parseNatlabFile(fName, natlabFile.getReader(), errors);
    result.setPositionMap(natlabFile.getPositionMap());
    return result;
  }

  /**
   * Parse a given file as a Matlab file and return the Program ast node.
   *
   * @param file   The file to be parsed.
   * @param errors   A list of errors for error collection.
   *
   * @return The Program node for the given file being parsed if no
   * errors. If an error occurs then null is returned. 
   */
  public static Program parseMatlabFile(GenericFile file, List<CompilationProblem> errors) {
    Reader reader = fileReader(file, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return parseMatlabFile(file.getName(), reader, errors);
  }

  private static CompilationUnits parseMultipleFiles(boolean matlab, List<String> files,
      List<CompilationProblem> errors) {
    CompilationUnits cu = new CompilationUnits();
    List<CompilationProblem> fileErrors = Lists.newArrayList();
    for (String fName : files) {
      Program program = matlab ? parseMatlabFile(fName, fileErrors) : parseNatlabFile(fName, fileErrors);
      if (program != null) {
        cu.addProgram(program);
      } else {
        errors.addAll(fileErrors);
      }
    }
    return cu;
  }

  /**
   * Parse several matlab files; use this instead of building up CompilationUnits manually.
   * Files which can't be parsed for whatever reason are skipped, with all their errors added
   * to the errors list.
   * @param files a list of filenames to be parsed
   * @param errors a list of errors for error collection
   * @return a CompilationUnits object containing those programs that were successfully parsed
   */
  public static CompilationUnits parseMatlabFiles(List<String> files, List<CompilationProblem> errors) {
    return parseMultipleFiles(true, files, errors);
  }

  /**
   * Parse several natlab files; use this instead of building up CompilationUnits manually.
   * Files which can't be parsed for whatever reason are skipped, with all their errors added
   * to the errors list.
   * @param files a list of filenames to be parsed
   * @param errors a list of errors for error collection
   * @return a CompilationUnits object containing those programs that were successfully parsed
   */
  public static CompilationUnits parseNatlabFiles(List<String> files, List<CompilationProblem> errors) {
    return parseMultipleFiles(false, files, errors);
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
  public static TranslateResult translateFile(String fName, List<CompilationProblem> errors) {
    Reader reader = fileReader(fName, errors);
    if (!errors.isEmpty()) {
      return null;
    }
    return translateFile(fName, reader, errors);
  }

  /**
   * Perform the reading and translation of a given file.
   *
   * @param file    the file to be translated
   * @param errors  A list of errors for error collection.
   * 
   * @return A reader object giving access to the translated
   * source.
   */
  public static TranslateResult translateFile(GenericFile file, List<CompilationProblem> errors) {
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
  public static TranslateResult translateFile(String fName, String source, List<CompilationProblem> errors) {
    return translateFile(fName, new StringReader(source), errors);
  }
}
