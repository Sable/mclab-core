package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//<n> - matches or emits argument n, negative numbers match from the back
public class CPNum extends CP{
    int num;
    public CPNum(int num){this.num = num;}
    public String toString() { return num+""; }
    public ClassPropMatch match(boolean isLeft,ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
        int index = num<0?(inputClasses.size()+num):num; //deal with negative index
        if (isLeft){ //isleft
            if (index < inputClasses.size() && index >= 0 &&
                previousMatchResult.getNumMatched() < inputClasses.size() &&
                inputClasses.get(previousMatchResult.getNumMatched()).
                    equals(inputClasses.get(index))) return previousMatchResult.next();
            return null;
        } else { //isright
            //TODO should catch the index out of bounds exception?
            return previousMatchResult.emit(inputClasses.get(index));
        }
    }
}