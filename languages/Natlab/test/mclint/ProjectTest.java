package mclint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import junit.framework.TestCase;

public class ProjectTest extends TestCase {
  private Path projectRoot;

  @Override
  protected void setUp() throws IOException {
    projectRoot = Files.createTempDirectory(null);
  }

  private void create(String path) throws IOException {
    Path file = projectRoot.resolve(path);
    Files.createDirectories(file.getParent());
    Files.createFile(file);
  }

  public void testSingleFile() throws IOException {
    create("f.m");

    Project project = Project.at(projectRoot);
    MatlabProgram f = project.getMatlabProgram("f.m");
    assertEquals("f.m", f.getPath().toString());
    assertFalse(f.isPrivate());
    assertEquals("", f.getPackage());
  }

  public void testPrivateFile() throws IOException {
    create("private/f.m");

    Project project = Project.at(projectRoot);
    MatlabProgram f = project.getMatlabProgram("private/f.m");
    assertEquals("private/f.m", f.getPath().toString());
    assertTrue(f.isPrivate());
    assertEquals("", f.getPackage());
  }

  public void testFileInPackage() throws IOException {
    create("+pkg/f.m");
    create("+pkg/+nested/f.m");

    Project project = Project.at(projectRoot);
    assertTrue(project.getMatlabPrograms().stream().anyMatch(program ->
        program.getPath().toString().equals("+pkg/f.m")
            && !program.isPrivate()
            && program.getPackage().equals("pkg")));

    assertTrue(project.getMatlabPrograms().stream().anyMatch(program ->
        program.getPath().toString().equals("+pkg/+nested/f.m")
            && !program.isPrivate()
            && program.getPackage().equals("pkg.nested")));
  }
}
