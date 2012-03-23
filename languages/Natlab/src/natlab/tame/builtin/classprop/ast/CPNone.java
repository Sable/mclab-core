package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//none - matches anything without advancing the numMatched, emits nothing
public class CPNone extends CP{
    public String toString() { return "none"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        return previousMatchResult;
    }
    
}