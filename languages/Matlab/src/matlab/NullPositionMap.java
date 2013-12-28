package matlab;

/**
 * A Position map that always returns the input. Used as a default map in the AST.
 */
public class NullPositionMap extends PositionMap {
  @Override public TextPosition getPreTranslationPosition(TextPosition dest) {
    return dest;
  }
}
