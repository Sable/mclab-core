package mclint.patterns.rewrite;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

public class Rewrites {
  public static List<Rewrite> fromCharSource(CharSource source) throws IOException {
    return source.readLines(new LineProcessor<List<Rewrite>>() {
      private List<Rewrite> refactorings = new ArrayList<>();
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
    return fromCharSource(Files.asCharSource(file, StandardCharsets.UTF_8));
  }
  
  public static List<Rewrite> fromString(String string) throws IOException {
    return fromCharSource(CharSource.wrap(string));
  }
  
  public static List<Rewrite> fromResource(URL resource) throws IOException {
    return fromCharSource(Resources.asCharSource(resource, StandardCharsets.UTF_8));
  }
}
