package natlab.toolkits.path;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import natlab.toolkits.filehandling.GenericFile;

/**
 * Mock subclass of GenericFile for testing purposes.
 */
public class MockFile extends GenericFile {
  private static final long serialVersionUID = 7297715933791547224L;

  private GenericFile parent;
  private String name;
  private String contents;
  private List<GenericFile> children;
  
  private MockFile(String name) {
    this(name, "");
  }
  
  private MockFile(String name, String contents) {
    this.name = name;
    this.contents = contents;
    this.children = new ArrayList<>();
  }
  
  public static MockFile directory(String name) {
    return new MockFile(name);
  }
  
  public MockFile with(String name, String contents) {
    MockFile file = new MockFile(name, contents);
    children.add(file);
    file.setParent(this);
    return this;
  }
  
  public MockFile with(MockFile directory) {
    children.add(directory);
    directory.setParent(this);
    return this;
  }
  
  private void setParent(GenericFile parent) {
    this.parent = parent;
  }

  @Override
  public GenericFile getParent() {
    return parent;
  }

  @Override
  public Reader getReader() throws IOException {
    return new StringReader(contents);
  }


  @Override
  public boolean isDir() {
    return !children.isEmpty();
  }

  @Override
  public Collection<? extends GenericFile> listChildren() {
    return new ArrayList<>(children);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getPath() {
    if (getParent() != null) {
      return getParent().getPath() + "/" + getName();
    }
    return getName();
  }

  @Override
  public GenericFile getChild(String name) {
    return children.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
  }

  //// Don't care about these:
  
  @Override
  public long lastModifiedDate() {
    return 0;
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }

}
