package mclint;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ast.CompilationUnits;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Project {
  private File projectRoot;
  private List<MatlabProgram> programs = Lists.newArrayList();

  public static Project at(File projectRoot) {
    Preconditions.checkArgument(projectRoot.isDirectory(),
        "project root %s is not a directory", projectRoot.getPath());
    return new Project(projectRoot.getAbsoluteFile());
  }

  private Project(File projectRoot) {
    this.projectRoot = projectRoot;

    collectFiles(new File("."));
  }

  // Note, the below is explicitly trying to work with paths relative to the project root.
  private void collectFiles(File directory) {
    File absoluteDir = new File(projectRoot, directory.getPath());
    for (String file : absoluteDir.list()) {
      File relativeFile = new File(directory, file);
      File absoluteFile = new File(absoluteDir, file);
      if (absoluteFile.isFile() && Files.getFileExtension(file).equals("m")) {
        File normalizedRelativeFile = new File(Files.simplifyPath(relativeFile.getPath()));
        addMatlabProgram(MatlabProgram.at(normalizedRelativeFile, this));
      } else if (absoluteFile.isDirectory()) {
        collectFiles(relativeFile);
      }
    }
  }

  public File getProjectRoot() {
    return projectRoot;
  }

  public void addMatlabProgram(MatlabProgram program) {
    programs.add(program);
  }

  public Iterable<MatlabProgram> getMatlabPrograms() {
    return Collections.unmodifiableList(programs);
  }

  public CompilationUnits asCompilationUnits() {
    CompilationUnits units = new CompilationUnits();
    units.setProject(this);
    for (MatlabProgram program : programs) {
      units.addProgram(program.parse());
    }
    return units;
  }

  public void write() throws IOException {
    write(projectRoot);
  }

  public void write(File root) throws IOException {
    for (MatlabProgram program : programs) {
      program.write();
    }
  }
}
