package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.components.constant.CharConstant;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.value.Value;

// if next is a string, consumes it (otherwise no match)
// if the value of the string is known, check whether it's among the types emmitted by the
// argument expression (which should emit 1 result). If it is, emite that type.
// if it isn't, emit an error
public class CPTypeString extends CP{
    CP tree;
    HashSet<String> allowedTypes = new HashSet<String>();
    Map<String,ClassReference> types = new HashMap<String,ClassReference>();
    public CPTypeString(CP tree){
        this.tree = tree;
        //get all allowed types from tree
        List<Set<ClassReference>> treeResult = tree.match(
                false, new ClassPropMatch(), new LinkedList<ClassReference>(), new LinkedList<Value<?>>()).getAllResults();
        if (treeResult.size() != 1) throw new UnsupportedOperationException(
                "typeString arguments neeed emit one result, got "+treeResult+" for "+this);
        for (ClassReference c : treeResult.get(0)){
            if (!(c instanceof PrimitiveClassReference)) throw new UnsupportedOperationException(
                "typeString arguments neeed emit primitive types, got "+c+" for "+this);
            allowedTypes.add(c.toString());
            types.put(c.toString(), c);
        }
    }
    public String toString() {
        return "typeString("+tree+")";
    }
    public ClassPropMatch match(boolean isLeft,
            ClassPropMatch previousMatchResult,
            List<ClassReference> inputClasses,
            List<? extends Value<?>> inputValues) {
        int i = previousMatchResult.getNumMatched();
        if (previousMatchResult.getNumMatched() < inputClasses.size() &&
                inputClasses.get(i).equals(PrimitiveClassReference.CHAR)){
            //consume element
            ClassPropMatch next = previousMatchResult.next();
            //if the next value is not known, just return whatever 
            if ((inputValues == null) || inputValues.get(i) == null
                    || !(inputValues.get(i) instanceof HasConstant) 
                    || (((HasConstant)inputValues.get(i)).getConstant() == null)){
                return tree.match(false, next, new LinkedList<ClassReference>(), new LinkedList<Value<?>>());
            }
            //else we have a value
            Constant constant = ((HasConstant)(inputValues.get(i))).getConstant();
            if (!(constant instanceof CharConstant)){
                return next.error();
            }
            //check whether the value is in the list of results
            String value = ((CharConstant)(constant)).getValue();
            if (allowedTypes.contains(value)) {
                return next.emit(types.get(value));
            } else {
                return next.error();
            }
        } else {
            //next arg not a string
            return null;
        }
    }
    
    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(1);
    	list.add(tree);
    	return list;
    }

}