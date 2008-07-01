package matlab;

import org.antlr.runtime.RecognitionException;

public class ArrayException extends TranslationException {
    public ArrayException(int line, int col, Exception cause) {
        super(line, col, cause);
    }

    public ArrayException(RecognitionException cause) {
        //NB: in antlr, lines are zero-indexed but columns are one-indexed
        super(cause.line + 1, cause.charPositionInLine, cause);
    }
}
