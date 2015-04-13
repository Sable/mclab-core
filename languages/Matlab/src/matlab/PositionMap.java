package matlab;

/**
 * A map from positions in one block of text to positions in another block of
 * text.
 * 
 * Main use is to map characters in a post-translation Natlab file back to the
 * corresponding characters in the original Matlab file.
 * 
 * Abstract since different translations are likely to represent the map in
 * different ways according to the information available.
 */
public abstract class PositionMap {
    public abstract TextPosition getPreTranslationPosition(TextPosition dest);

    public TextPosition getPreTranslationPosition(int destLine, int destCol) {
        return getPreTranslationPosition(new TextPosition(destLine, destCol));
    }
}
