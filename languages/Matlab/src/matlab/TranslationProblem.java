package matlab;

/**
 * Describes the position and nature of a problem encountered while translating
 * a Matlab file to Natlab.
 */
public class TranslationProblem {
    private final int line;
    private final int col;
    private final String msg;

    public TranslationProblem(int line, int col, String msg) {
        this.line = line;
        this.col = col;
        this.msg = msg;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return col;
    }

    public String getMessage() {
        return msg;
    }
    
    public String toString() {
        return "[" + line + ", " + col + "]  " + msg;
    }
}
