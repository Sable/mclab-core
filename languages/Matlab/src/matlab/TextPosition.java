package matlab;

/**
 * A position in a block of text: line and column number.
 * Comparable to non-null TextPositions.
 */
public class TextPosition implements Comparable<TextPosition> {
    private final int line;
    private final int col;

    public TextPosition(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return col;
    }

    public int compareTo(TextPosition o) {
        if(o == null) {
            throw new NullPointerException("Cannot compareTo a null SourcePosition.");
        }
        int lineDif = this.line - o.line;
        if(lineDif != 0) {
            return lineDif;
        } else {
            int colDif = this.col - o.col;
            return colDif;
        }
    }

    public String toString() {
        return "(" + line + ", " + col + ")";
    }
}