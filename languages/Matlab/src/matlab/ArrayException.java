package matlab;

import org.antlr.runtime.RecognitionException;

public class ArrayException extends TranslationException {
    public ArrayException(int line, int col, Exception cause) {
        super(line, col, cause);
    }

    public ArrayException(RecognitionException cause) {
        //NB: in antlr, lines are one-indexed but columns are zero-indexed
        super(cause.line, cause.charPositionInLine + 1, cause);
    }
}
