package matlab;

/**
 * A problem encountered while cleaning up the (row and column) delimiters in
 * an array.
 */
public class ArrayTranslationProblem extends TranslationProblem {
    public ArrayTranslationProblem(int line, int col, String msg) {
        super(line, col, msg);
    }
}
