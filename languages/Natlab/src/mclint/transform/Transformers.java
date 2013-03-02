package mclint.transform;

import java.io.IOException;
import java.io.Reader;

import ast.Program;

/**
 * Factory for transformers.
 */
public class Transformers {
  /**
   * Create a basic transformer for the given AST. Basic transformers just do the straightforward
   * tree operations.
   */
  public static Transformer basic(Program program) {
    return BasicTransformer.of(program);
  }
  
  /**
   * Create a layout-preserving transformation for the given program text. Layout-preserving
   * transformers care about preserving e.g. whitespace, which is why they need the actual text
   * and not just the AST.
   */
  public static Transformer layoutPreserving(Reader source) throws IOException {
    return LayoutPreservingTransformer.of(source);
  }
}
