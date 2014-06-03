package natlab.toolkits.path;

import java.io.IOException;

import junit.framework.TestCase;
import natlab.toolkits.filehandling.GenericFile;

public class FolderHandlerTest extends TestCase {
  private void assertPathAndContents(GenericFile f, String path, String contents) 
    throws IOException {
    assertNotNull(f);
    assertEquals(path, f.getPath());
    assertEquals(contents, f.getContents());
  }

  public void testSimpleLookup() throws IOException {
    GenericFile dir = MockFile.directory("dir").with("f.m", "function f\n end");
    FolderHandler handler = FolderHandler.getFolderHandler(dir);
    
    assertPathAndContents(handler.lookupFunctions("f"), "dir/f.m", "function f\n end");
  }
  
  public void testInvalidLookups() {
    GenericFile dir = MockFile.directory("dir").with("f.m", "function f\n end");
    FolderHandler handler = FolderHandler.getFolderHandler(dir);
    
    assertNull(handler.lookupPrivateFunctions("f"));
    assertNull(handler.lookupPackage("f"));
    assertNull(handler.lookupClasses("f"));
  }
  
  public void testPrivateLookup() throws IOException {
    GenericFile env = MockFile.directory("dir")
        .with(MockFile.directory("private")
            .with("f.m", "function f\n end"));
    FolderHandler handler = FolderHandler.getFolderHandler(env);
    
    assertPathAndContents(handler.lookupPrivateFunctions("f"), "dir/private/f.m", "function f\n end");
  }
  
  public void testPackageLookup() throws IOException {
    GenericFile env = MockFile.directory("dir")
        .with(MockFile.directory("+pkg")
            .with("f.m", "function f\n end"));
    
    FolderHandler pkgHandler = FolderHandler.getFolderHandler(FolderHandler.getFolderHandler(env)
        .lookupPackage("pkg"));
    
    assertPathAndContents(pkgHandler.lookupFunctions("f"), "dir/+pkg/f.m", "function f\n end");
  }
  
  public void testClassLookup() throws IOException {
    GenericFile env = MockFile.directory("dir")
        .with(MockFile.directory("@cls")
            .with("cls.m", "function cls\n end")
            .with("f.m", "function f(this)\n end"));
    FolderHandler handler = FolderHandler.getFolderHandler(env);
    
    GenericFile cls = handler.lookupClasses("cls");
    assertPathAndContents(cls, "dir/@cls/cls.m", "function cls\n end");
    
    GenericFile f1 = handler.lookupSpecialized("f", "cls");
    GenericFile f2 = handler.lookupSpecializedAll("f").get("cls");
    GenericFile f3 = handler.getAllSpecialized("cls").get(0);
    assertPathAndContents(f1, "dir/@cls/f.m", "function f(this)\n end");
    assertSame(f1, f2);
    assertSame(f1, f3);
  }
}
