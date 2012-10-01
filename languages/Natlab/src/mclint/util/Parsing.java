package mclint.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.genericFile.FileFile;
import ast.CompilationUnits;
import ast.Program;

/**
 * A slightly nicer interface to natlab.Parse.
 * (TODO: refactor natlab.Parse to make this class unnecessary.)
 * @author isbadawi
 */
public class Parsing {
  public static CompilationUnits files(List<String> mfiles) {
    List<CompilationProblem> errors = new ArrayList<CompilationProblem>();
    CompilationUnits code = Parse.parseFiles(mfiles, errors);
    if (!errors.isEmpty()) {
      System.err.println("Could not run analyses; the following errors occured during parsing:");
      for (CompilationProblem error : errors) {
        System.err.println(error);
      }
      System.exit(1);
    }
    for (int i = 0; i < mfiles.size(); i++) {
      code.getProgram(i).setFile(new FileFile(mfiles.get(i)));
    }
    return code;
  }
  
  public static CompilationUnits files(String... mfiles) {
    return files(Arrays.asList(mfiles));
  }
  
  public static Program file(String mfile) {
    return files(new String[] {mfile}).getProgram(0);
  }
  
  public static Program reader(Reader code) {
    List<CompilationProblem> errors = new ArrayList<CompilationProblem>();
    Program program = Parse.parseFile("none>", code, errors);
    if (!errors.isEmpty()) {
      System.err.println("Could not run analyses; the following errors occured during parsing:");
      for (CompilationProblem error : errors) {
        System.err.println(error);
      }
      System.exit(1);
    }
    return program;
  }
  
  public static Program string(String code) {
    return reader(new StringReader(code));
  }
  
    private Parsing() {}
}
