package matlab;

import java.util.List;

public class ArrayException extends TranslationException {
    public ArrayException(Problem problem) {
        super(problem);
    }

    public ArrayException(List<Problem> problems) {
        super(problems);
    }
}
