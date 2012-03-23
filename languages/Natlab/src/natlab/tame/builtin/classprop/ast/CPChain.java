package natlab.tame.builtin.classprop.ast;

import java.util.*;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//class1 & class2
public class CPChain extends CP{
    CP class1,class2;
    public CPChain(CP class1, CP class2){
        this.class1 = class1; this.class2 = class2;
    }
    public String toString() {return print(class1)+" "+print(class2);}        
    public ClassPropMatch match(boolean isLeft,ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
        if (isLeft){ //isleft - try to match first, then try to match second using the result
            ClassPropMatch match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
            if (match == null) return null;
            return class2.match(isLeft, match, inputClasses, inputValues);
        } else { //is right, emit first, then second
            ClassPropMatch match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
            return class2.match(isLeft, match, inputClasses, inputValues);
        }
    }
    

    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(2);
    	list.add(class1);
    	list.add(class2);
    	return list;
    }
}