package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//class1 | class2
public  class CPUnion extends CP{
    CP class1,class2;
    public CPUnion(CP class1, CP class2){
        this.class1 = class1; this.class2 = class2;
    }
    public String toString() {return print(class1)+"|"+print(class2);} 
    public ClassPropMatch match(boolean isLeft,ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
        if (isLeft){ //isleft - try left and right, then return the longer succesfull match
            ClassPropMatch match1 = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
            ClassPropMatch match2 = class2.match(isLeft, previousMatchResult, inputClasses, inputValues);
            if (ClassPropTool.DEBUG){
                System.out.println("|left: before: "+previousMatchResult+" expr:"+class1+" result: "+match1);
                System.out.println("|right:before: "+previousMatchResult+" expr:"+class2+" result: "+match2);
            }
            if (match1 == null) return match2;
            if (match2 == null) return match1;
            if (match1.getNumMatched() >= match2.getNumMatched()) return match1;
            return match2;
        } else { //isright
            ClassPropMatch match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
            return match.union(class2.match(isLeft, previousMatchResult, inputClasses, inputValues));
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