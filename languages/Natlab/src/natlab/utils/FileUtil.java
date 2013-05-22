package natlab.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

public class FileUtil {
  /**
   * Split a file path in a platform-independent way.
   * 
   * For example, splitPath(new File("a/b/c/d.txt")) returns ["a", "b", "c", "d.txt"].
   */
  public static List<String> splitPath(File file) {
    LinkedList<String> components = Lists.newLinkedList();
    while (file != null) {
      components.addFirst(file.getName());
      file = file.getParentFile();
    }
    return components;
  }

  private FileUtil() {}
}
