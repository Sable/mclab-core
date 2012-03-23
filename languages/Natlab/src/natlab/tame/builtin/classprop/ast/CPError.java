package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//error - emits an error result - any ClassPropMatch that includes an error object will result in an error overall
public class CPError extends CP{
    public String toString() { return "error"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses,
            List<? extends Value<?>> inputValues) {
        return previousMatchResult.error();
    }
}