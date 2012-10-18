package mclint.refactoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Refactorings {
  public static List<Refactoring> fromReader(Reader r) throws IOException {
    List<Refactoring> refactorings = new ArrayList<Refactoring>();
    BufferedReader in = new BufferedReader(r);
    String line = null;
    while ((line = in.readLine()) != null) {
      String[] parts = line.trim().split("->");
      String from = parts[0].trim();
      String to = parts[1].trim();
      refactorings.add(Refactoring.of(from, to, Refactoring.Visit.Expressions));
    }
    return Collections.unmodifiableList(refactorings);
  }

  public static List<Refactoring> fromFile(String filename) throws IOException {
    return fromReader(new FileReader(filename));
  }
}
