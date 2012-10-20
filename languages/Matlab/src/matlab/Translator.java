package matlab;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import natlab.CompilationProblem;
import natlab.Parse;

/**
 * A utility for translating from Matlab source to Natlab source.
 * 
 * Exit status:
 *   0 - normal return: successful parse or list of errors
 *   1 - incorrect usage
 *   2 - I/O issue
 *   3 - unexpected parse issue
 */
public class Translator {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java matlab.Translator {basename}");
      System.exit(1);
    }
    List<CompilationProblem> errors = new ArrayList<CompilationProblem>();
    String basename = args[0];
    Reader natlab = Parse.translateFile(basename + ".m", errors);
    if (natlab == null) {
      System.out.println(errors);
      System.exit(0);
    }
    if (errors.isEmpty()) {
      try {
        new FileWriter(basename + ".n").write(Parse.readerToString(natlab));
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(2);
      }
    }
  }
}
