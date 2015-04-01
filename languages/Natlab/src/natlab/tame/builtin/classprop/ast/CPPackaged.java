package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

/**
 * represents a CP tree packaged under a different name.
 * 
 */

public class CPPackaged extends CP{
	private CP tree;
	private String name;
	
	public CPPackaged(CP tree,String name){
		this.tree = tree; this.name = name;
	}
	
	@Override
	public ClassPropMatch match(boolean isLeft,
			ClassPropMatch previousMatchResult,
			List<ClassReference> inputClasses,
			List<? extends Value<?>> inputValues) {
		return tree.match(isLeft,previousMatchResult,inputClasses,inputValues);
	}

	@Override
	public String toString() {
		return name;
	}
	
    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(1);
    	list.add(tree);
    	return list;
    }

}
