package mclint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import mclint.transform.Transformer;
import mclint.transform.Transformers;
import mclint.util.Parsing;
import ast.Program;

// TODO(isbadawi): There is lots of awkward interrelated state in this class
// -- file, code, ast, the transformers, etc. are all kinds of views on the same thing,
// except that changes might be made to some of them...
// Need to think harder about modeling this.
public class MatlabProgram {
  private Project project;
  private Path file;

  private String code;
  private Program ast;
  private Transformer basicTransformer;
  private Transformer layoutPreservingTransformer;
  
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
  
  public Path getAbsolutePath() {
    return getProject().getProjectRoot().resolve(getPath());
  }

  public boolean isPrivate() {
    return file.getParent() != null
        && "private".equals(file.getParent().getFileName().toString());
  }

  public String getPackage() {
    return StreamSupport.stream(file.spliterator(), false)
        .map(Object::toString)
        .filter(component -> component.startsWith("+"))
        .map(component -> component.substring(1))
        .collect(Collectors.joining("."));
  }

  public Program parse() {
    if (ast == null) {
      ast = Parsing.file(getAbsolutePath().toString());
      ast.setMatlabProgram(this);
    }
    return ast;
  }

  public AnalysisKit analyze() {
    return AnalysisKit.forAST(ast);
  }

  public Transformer getBasicTransformer() {
    if (basicTransformer == null) {
      basicTransformer = Transformers.basic(ast);
    }
    return basicTransformer;
  }

  public Transformer getLayoutPreservingTransformer() {
    if (layoutPreservingTransformer == null) {
      try {
        layoutPreservingTransformer = Transformers.layoutPreserving(
            new InputStreamReader(Files.newInputStream(getAbsolutePath())));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return layoutPreservingTransformer;
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
