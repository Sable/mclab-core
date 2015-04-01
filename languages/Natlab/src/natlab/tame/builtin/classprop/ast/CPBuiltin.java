package natlab.tame.builtin.classprop.ast;

import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.FunctionHandleClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.value.Value;

//<builtin class>
public class CPBuiltin extends CP{
    ClassReference classRef;
    public CPBuiltin(String name){
        if (name.equalsIgnoreCase("function_handle")){//TODO - remove this(?!)
            this.classRef = FunctionHandleClassReference.getInstance();
        } else {
            this.classRef = PrimitiveClassReference.valueOf(name.toUpperCase());
        }
    }
    public String toString() {return classRef.getName();}
    public ClassPropMatch match(boolean isLeft,ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
        if (isLeft){ //isleft - try to match
            if (previousMatchResult.getNumMatched() < inputClasses.size() &&
                inputClasses.get(previousMatchResult.getNumMatched()).equals(classRef))
                return previousMatchResult.next();
            return null;
        } else { //isright - just emit the class
            return previousMatchResult.emit(classRef);
        }
    }
}