package matlab;

import java.util.Collections;
import java.util.List;

public class TranslationException extends Exception {
    private final List<TranslationProblem> problems;
    
    public TranslationException(TranslationProblem problem) {
        this(Collections.singletonList(problem));
    }
    
    public TranslationException(List<TranslationProblem> problems) {
        this.problems = Collections.unmodifiableList(problems);
    }
    
    public List<TranslationProblem> getProblems() {
        return problems;
    }
}
