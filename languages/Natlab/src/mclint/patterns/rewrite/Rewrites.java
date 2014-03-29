package mclint.patterns.rewrite;

import java.io.File;
import java.io.IOException;
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

public class Rewrites {
  public static <T extends Reader> List<Rewrite>
      fromInputSupplier(InputSupplier<T> supplier) throws IOException{
    return CharStreams.readLines(supplier,  new LineProcessor<List<Rewrite>>() {
      private List<Rewrite> refactorings = Lists.newArrayList();
      private final Splitter SPLITTER = Splitter.on("->").trimResults().omitEmptyStrings();

      @Override
      public List<Rewrite> getResult() {
        return Collections.unmodifiableList(refactorings);
      }

      @Override
      public boolean processLine(String line) {
        List<String> parts = SPLITTER.splitToList(line);
        refactorings.add(Rewrite.of(parts.get(0), parts.get(1), Rewrite.Visit.Expressions));
        return true;
      }
    });
  }

  public static List<Rewrite> fromFile(File file) throws IOException {
    return fromInputSupplier(Files.newReaderSupplier(file, Charsets.UTF_8));
  }
  
  public static List<Rewrite> fromString(String string) throws IOException {
    return fromInputSupplier(CharStreams.newReaderSupplier(string));
  }
  
  public static List<Rewrite> fromResource(URL resource) throws IOException {
    return fromInputSupplier(Resources.newReaderSupplier(resource, Charsets.UTF_8));
  }
}
