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

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class MatlabProgram {
  private Project project;
  private File file;
  private File absoluteFile;

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
    return Parsing.file(absoluteFile.getPath());
  }

  public AnalysisKit analyze() {
    return AnalysisKit.forAST(parse());
  }

  public Transformer getBasicTransformer() {
    return Transformers.basic(parse());
  }

  public Transformer getLayoutPreservingTransformer() {
    try {
      return Transformers.layoutPreserving(new FileReader(absoluteFile));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
