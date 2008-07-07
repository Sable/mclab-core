package matlab;

/**
 * A problem encountered while translating a command-style call to a function-
 * style call.
 */
public class CommandTranslationProblem extends TranslationProblem {
    public CommandTranslationProblem(int line, int col, String msg) {
        super(line, col, msg);
    }
}
