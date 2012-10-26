package mclint.refactoring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

public class Refactorings {
  public static <T extends Reader> List<Refactoring>
      fromInputSupplier(InputSupplier<T> supplier) throws IOException{
    return CharStreams.readLines(supplier,  new LineProcessor<List<Refactoring>>() {
      private List<Refactoring> refactorings = Lists.newArrayList();
      private final Splitter SPLITTER = Splitter.on("->").trimResults().omitEmptyStrings();

      @Override
      public List<Refactoring> getResult() {
        return Collections.unmodifiableList(refactorings);
      }

      @Override
      public boolean processLine(String line) {
        Iterator<String> parts = SPLITTER.split(line).iterator();
        String from = parts.next();
        String to = parts.next();
        refactorings.add(Refactoring.of(from, to, Refactoring.Visit.Expressions));
        return true;
      }
    });
  }

  public static List<Refactoring> fromFile(File file) throws IOException {
    return fromInputSupplier(Files.newReaderSupplier(file, Charsets.UTF_8));
  }
  
  public static List<Refactoring> fromString(String string) throws IOException {
    return fromInputSupplier(CharStreams.newReaderSupplier(string));
  }
  
  public static List<Refactoring> fromResource(URL resource) throws IOException {
    return fromInputSupplier(Resources.newReaderSupplier(resource, Charsets.UTF_8));
  }
}
