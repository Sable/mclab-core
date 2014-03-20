package mclint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import mclint.transform.Transformer;
import mclint.transform.Transformers;
import mclint.util.Parsing;
import ast.Program;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

// TODO(isbadawi): There is lots of awkward interrelated state in this class
// -- file, code, ast, the transformers, etc. are all kinds of views on the same thing,
// except that changes might be made to some of them...
// Need to think harder about modeling this.
public class MatlabProgram {
  private Project project;
  private Path file;

  private String code;
  private Program ast;
  
  public static MatlabProgram at(Path file) throws IOException {
    Path root = file.getParent();
    return Project.at(root).getMatlabProgram(root.relativize(file));
  }
  
  public static MatlabProgram at(Path file, Project project) {
    return new MatlabProgram(file, project);
  }

  private MatlabProgram(Path file, Project project) {
    this.project = project;
    this.file = file;
  }

  public Project getProject() {
    return project;
  }

  /**
   * Returns the path of this program relative to the project root.
   */
  public Path getPath() {
    return file;
  }

  public boolean isPrivate() {
    return file.getParent() != null
        && "private".equals(file.getParent().getFileName().toString());
  }

  public String getPackage() {
    List<String> packageComponents = Lists.newArrayList();
    for (Path componentPath : file) {
      String component = componentPath.toString();
      if (!component.startsWith("+")) {
        break;
      }
      packageComponents.add(component.substring(1));
    }
    return Joiner.on(".").join(packageComponents);
  }

  public Program parse() {
    if (ast == null) {
      ast = Parsing.file(project.getProjectRoot().resolve(file).toString());
      ast.setMatlabProgram(this);
    }
    return ast;
  }

  public AnalysisKit analyze() {
    return AnalysisKit.forAST(ast);
  }

  public Transformer getBasicTransformer() {
    return Transformers.basic(ast);
  }

  public Transformer getLayoutPreservingTransformer() {
    try {
      return Transformers.layoutPreserving(
          new InputStreamReader(Files.newInputStream(file)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public void write() throws IOException {
    write(project.getProjectRoot());
  }

  public void write(Path root) throws IOException {
    Path outputFile = root.resolve(file);
    Files.createDirectories(outputFile.getParent());
    // TODO really what should happen is Project and MatlabPrograms get passed around everywhere,
    // and changes are made to their code (e.g. by assigning transformer.reconstructText()).
    if (code == null) {
      code = ast.getPrettyPrinted();
    }
    Files.write(outputFile, code.getBytes(StandardCharsets.UTF_8));
  }
}
