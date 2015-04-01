package mclint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ast.CompilationUnits;
import com.google.common.base.Preconditions;

public class Project {
  private Path projectRoot;
  private Map<Path, MatlabProgram> programs = new HashMap<>();

  public static Project at(final Path projectRoot) throws IOException {
    Preconditions.checkArgument(Files.isDirectory(projectRoot),
        "project root %s is not a directory", projectRoot);
    final Project project = new Project(projectRoot);

    Files.walk(projectRoot)
        .filter(Files::isRegularFile)
        .filter(path -> path.toString().endsWith(".m"))
        .map(path -> projectRoot.relativize(path).normalize())
        .forEach(project::addMatlabProgram);

    return project;
  }

  private Project(Path projectRoot) {
    this.projectRoot = projectRoot;
  }

  public Path getProjectRoot() {
    return projectRoot;
  }

  public MatlabProgram addMatlabProgram(Path program) {
    MatlabProgram matlabProgram = MatlabProgram.at(program, this);
    programs.put(matlabProgram.getPath(), matlabProgram);
    return matlabProgram;
  }
  
  public MatlabProgram getMatlabProgram(Path rootRelativePath) {
    return programs.get(rootRelativePath);
  }
  
  public MatlabProgram getMatlabProgram(String rootRelativePath) {
    return getMatlabProgram(getProjectRoot().resolve(rootRelativePath));
  }

  public Collection<MatlabProgram> getMatlabPrograms() {
    return Collections.unmodifiableCollection(programs.values());
  }

  public CompilationUnits asCompilationUnits() {
    CompilationUnits units = new CompilationUnits();
    units.setProject(this);
    for (MatlabProgram program : getMatlabPrograms()) {
      units.addProgram(program.parse());
    }
    return units;
  }

  public void write() throws IOException {
    write(projectRoot);
  }

  public void write(Path root) throws IOException {
    for (MatlabProgram program : getMatlabPrograms()) {
      program.write(root);
    }
  }
}