package matlab;

public class TranslationException extends Exception {
    private final int line;
    private final int col;

    protected TranslationException(int line, int col, String msg) {
        super(msg);
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }
}
