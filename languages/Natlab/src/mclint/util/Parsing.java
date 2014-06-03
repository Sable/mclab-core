package mclint.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.FileFile;
import ast.CompilationUnits;
import ast.Program;

/**
 * A slightly nicer interface to natlab.Parse.
 * (TODO: refactor natlab.Parse to make this class unnecessary.)
 * @author isbadawi
 */
public class Parsing {
  private static void abortIfNotEmpty(List<CompilationProblem> errors) {
    if (!errors.isEmpty()) {
      System.err.println("The following errors occured during parsing:");
      System.err.println(errors.stream().map(Object::toString).collect(Collectors.joining("\n")));
      System.exit(1);
    }
  }
  
  public static CompilationUnits files(List<String> mfiles) {
    List<CompilationProblem> errors = new ArrayList<>();
    CompilationUnits code = Parse.parseMatlabFiles(mfiles, errors);
    abortIfNotEmpty(errors);
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
    List<CompilationProblem> errors = new ArrayList<>();
    Program program = Parse.parseMatlabFile("<none>", code, errors);
    abortIfNotEmpty(errors);
    program.setFile(new FileFile("<none>"));
    return program;
  }

  public static Program string(String code) {
    return reader(new StringReader(code));
  }

  private Parsing() {}
}
