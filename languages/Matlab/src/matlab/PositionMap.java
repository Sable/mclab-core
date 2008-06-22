package matlab;

public abstract class PositionMap {

    public abstract TextPosition sourceToDest(TextPosition source);
    public abstract TextPosition destToSource(TextPosition dest);

    public TextPosition sourceToDest(int sourceLine, int sourceCol) {
        return sourceToDest(new TextPosition(sourceLine, sourceCol));
    }
    public TextPosition destToSource(int destLine, int destCol) {
        return destToSource(new TextPosition(destLine, destCol));
    }

    public TextPosition sourceToDest(int sourcePos) {
        return sourceToDest(new TextPosition(sourcePos));
    }
    public TextPosition destToSource(int destPos) {
        return destToSource(new TextPosition(destPos));
    }
}
