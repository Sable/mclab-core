package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//end - matches if all input elements have been matched, emits nothing
public class CPEnd extends CP{
    public String toString() { return "end"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        return (previousMatchResult.getNumMatched() == inputClasses.size())?previousMatchResult:null;
    }
}