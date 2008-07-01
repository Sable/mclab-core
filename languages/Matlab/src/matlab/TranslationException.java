package matlab;

import java.util.Collections;
import java.util.List;

public class TranslationException extends Exception {
    private final List<Problem> problems;
    
    public TranslationException(Problem problem) {
        this(Collections.singletonList(problem));
    }
    
    public TranslationException(List<Problem> problems) {
        this.problems = Collections.unmodifiableList(problems);
    }
    
    public List<Problem> getProblems() {
        return problems;
    }

    public static class Problem {
        private final int line;
        private final int col;
        private final String msg;

        public Problem(int line, int col, String msg) {
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
    }
}
