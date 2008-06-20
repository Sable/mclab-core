package matlab;

public class CommandException extends TranslationException {
    public CommandException(int line, int col, String msg) {
        super(line, col, msg);
    }
}
