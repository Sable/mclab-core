package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.value.Value;

//If there is another argument to consume, matches if it is
//scalar, or if it's shape is unknown, without consuming the argument.
//Can be used to check if the next argument is scalar.
public class CPScalar extends CP{
    public String toString() { return "scalar"; }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        if (!isLeft) throw new UnsupportedOperationException("class propagation error: 'any' can not be used on the right hand side");
        int i = previousMatchResult.getNumMatched(); 
        if (i >= inputClasses.size())
            return null; //no more elements to match
        if (inputValues == null || inputValues.get(i) == null) return previousMatchResult;
        //find if the value is scalar nor not 
        Value<?> value = inputValues.get(i);
        //check if scalar constant
        if (value instanceof HasConstant){
            Constant constant = ((HasConstant)value).getConstant();
            if (constant != null){
                return constant.isScalar()?previousMatchResult:null;
            } 
        }
        //check if scalar
        if ((value instanceof HasShape) && !(((HasShape<?>)value).getShape().maybeScalar())){
            return null;
        }
        //match!
        return previousMatchResult;
    }
}