package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

//coerce(CP match and replace for all args, CP affeced expr)
//the match and replace is run on all arguments separately, and have
//to either not match or result in one single result
//example: coerce(char|logical>double, numerical&numerical>double)
public class CPCoerce extends CP{
    CP tree;
    CP replaceTree;
    public CPCoerce(CP replaceTree,CP tree){
        this.tree = tree;
        this.replaceTree = replaceTree;
    }
    public String toString() {
        return "coerce("+replaceTree+","+tree+")";
    }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
        //try to match every arg, replacing if necessary
        ArrayList<ClassReference> newInputClasses = 
            new ArrayList<ClassReference>(inputClasses);
        for(int i = 0; i < inputClasses.size(); i++){
            //do match for arg i
            ClassPropMatch match = 
                replaceTree.match(true, new ClassPropMatch(), Collections.singletonList(inputClasses.get(i)), null);
            //check whether it returned a match - and whether the match result is valid
            if (match != null){
                if (match.getNumEmittedResults() != 1 || match.getEmittedClass() == null){
                    throw new UnsupportedOperationException(
                            "argument coercion for builtin class propagation must result in one ouput "
                            +"for every argument, received "+match+" for "+this);
                }
                //override argument class with the singleton match result
                newInputClasses.set(i, match.getEmittedClass());                    
            }
        }
        return tree.match(isLeft, previousMatchResult, newInputClasses, inputValues);
    }
    
    
    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(2);
    	list.add(tree);
    	list.add(replaceTree);
    	return list;
    }
}