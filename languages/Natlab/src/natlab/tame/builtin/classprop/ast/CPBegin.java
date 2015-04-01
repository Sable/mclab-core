package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//begin - matches if the current read-index is 0, emits nothing
public class CPBegin extends CP{
    public String toString() { return "begin"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        return (previousMatchResult.getNumMatched() == 0)?previousMatchResult:null;
    }
}