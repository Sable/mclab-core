package mclint;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import ast.CompilationUnits;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class Project {
  private Path projectRoot;
  private Map<Path, MatlabProgram> programs = Maps.newHashMap();

  public static Project at(final Path projectRoot) throws IOException {
    Preconditions.checkArgument(Files.isDirectory(projectRoot),
        "project root %s is not a directory", projectRoot);
    final Project project = new Project(projectRoot);

    Files.walkFileTree(projectRoot, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (file.toString().endsWith(".m")) {
          Path normalizedRelativeFile = projectRoot.relativize(file).normalize();
          project.addMatlabProgram(normalizedRelativeFile);
        }
        return FileVisitResult.CONTINUE;
      }
    });

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