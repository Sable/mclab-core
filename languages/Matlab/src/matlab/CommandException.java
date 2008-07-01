package matlab;

import java.util.Collections;

public class CommandException extends TranslationException {
    public CommandException(int line, int col, String msg) {
        super(Collections.singletonList(new Problem(line, col, msg)));
    }
}
