package mclint;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class ProjectTest extends TestCase {
  private File projectRoot;

  @Override
  protected void setUp() {
    projectRoot = Files.createTempDir();
  }

  private void create(String path) throws IOException {
    File file = new File(projectRoot, path);
    Files.createParentDirs(file);
    file.createNewFile();
  }

  public void testSingleFile() throws IOException {
    create("f.m");

    Project project = Project.at(projectRoot);
    MatlabProgram f = Iterables.getOnlyElement(project.getMatlabPrograms());
    assertEquals("f.m", f.getPath());
    assertFalse(f.isPrivate());
    assertEquals("", f.getPackage());
  }

  public void testPrivateFile() throws IOException {
    create("private/f.m");

    Project project = Project.at(projectRoot);
    MatlabProgram f = Iterables.getOnlyElement(project.getMatlabPrograms());
    assertEquals("private/f.m", f.getPath());
    assertTrue(f.isPrivate());
    assertEquals("", f.getPackage());
  }

  public void testFileInPackage() throws IOException {
    create("+pkg/f.m");
    create("+pkg/+nested/f.m");

    Project project = Project.at(projectRoot);
    FluentIterable<MatlabProgram> programs = FluentIterable.from(project.getMatlabPrograms());
    assertTrue(programs.anyMatch(new Predicate<MatlabProgram>() {
      @Override public boolean apply(MatlabProgram program) {
        return program.getPath().equals("+pkg/f.m")
            && !program.isPrivate()
            && program.getPackage().equals("pkg");
      }
    }));
    assertTrue(programs.anyMatch(new Predicate<MatlabProgram>() {
      @Override public boolean apply(MatlabProgram program) {
        return program.getPath().equals("+pkg/+nested/f.m")
            && !program.isPrivate()
            && program.getPackage().equals("pkg.nested");
      }
    }));
  }
}
