package matlab;

public abstract class PositionMap {
    public abstract TextPosition getPreTranslationPosition(TextPosition dest);

    public TextPosition getPreTranslationPosition(int destLine, int destCol) {
        return getPreTranslationPosition(new TextPosition(destLine, destCol));
    }

    public TextPosition getPreTranslationPosition(int destPos) {
        return getPreTranslationPosition(new TextPosition(destPos));
    }
}
