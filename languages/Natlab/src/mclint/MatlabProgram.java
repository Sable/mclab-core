package mclint;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import mclint.transform.Transformer;
import mclint.transform.Transformers;
import mclint.util.Parsing;
import natlab.utils.FileUtil;
import ast.Program;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class MatlabProgram {
  private Project project;
  private File file;
  private File absoluteFile;

  private String code;
  private Program ast;

  public static MatlabProgram at(File file, Project project) {
    return new MatlabProgram(file, project);
  }

  private MatlabProgram(File file, Project project) {
    this.project = project;
    this.file = file;
    this.absoluteFile = new File(project.getProjectRoot(), file.getPath());
  }

  public Project getProject() {
    return project;
  }

  /**
   * Returns the path of this program relative to the project root.
   */
  public String getPath() {
    return file.getPath();
  }

  public boolean isPrivate() {
    return "private".equals(file.getParent());
  }

  public String getPackage() {
    List<String> packageComponents = Lists.newArrayList();
    for (String component : FileUtil.splitPath(file)) {
      if (!component.startsWith("+")) {
        break;
      }
      packageComponents.add(component.substring(1));
    }
    return Joiner.on(".").join(packageComponents);
  }

  public Program parse() {
    if (ast == null) {
      ast = Parsing.file(absoluteFile.getPath());
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
      return Transformers.layoutPreserving(new FileReader(absoluteFile));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public void write() throws IOException {
    write(project.getProjectRoot());
  }

  public void write(File root) throws IOException {
    File outputFile = new File(root, file.getPath());
    Files.createParentDirs(outputFile);
    // TODO really what should happen is Project and MatlabPrograms get passed around everywhere,
    // and changes are made to their code (e.g. by assigning transformer.reconstructText()).
    if (code == null) {
      code = ast.getPrettyPrinted();
    }
    Files.write(code, outputFile, Charsets.UTF_8);
  }
}
