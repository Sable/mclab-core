package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//any - matches the next input, no matter what it is - throws error if used as rhs
public class CPAny extends CP{
    public String toString() { return "any"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        if (!isLeft) throw new UnsupportedOperationException("class propagation error: 'any' can not be used on the right hand side");
        return (previousMatchResult.getNumMatched() < inputClasses.size())?
                previousMatchResult.next():
                null;
    }
}