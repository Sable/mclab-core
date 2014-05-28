package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//class1 > class2
public class CPMap extends CP{
    CP class1,class2;
    public CPMap(CP class1, CP class2){
        this.class1 = class1; this.class2 = class2;
    }
    public String toString() {return print(class1)+"->"+print(class2);}        
    public ClassPropMatch match(boolean isLeft,ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
        //isleft/isright - try to match first, then try to match second using the result
        ClassPropMatch match = class1.match(true, previousMatchResult, inputClasses, inputValues);
        if (match == null){
            if (isLeft) return null; //if left we didn't properly match
            return previousMatchResult; //if right we'll just skip this
        }
        return class2.match(false, match, inputClasses, inputValues);
    }
    
    
    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(2);
    	list.add(class1);
    	list.add(class2);
    	return list;
    }
}