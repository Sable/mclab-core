package natlab.tame.builtin.classprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.classprop.ClassPropMatch;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;

/**
 * this refers to a special CP value whose value is filled in after parsing.
 * Before performing a match, the value has to be set.
 * This is used to implement the nodes
 *   - parent
 *   - natlab
 *   - matlab
 * Which depend on the context of the class propagation flow equation.
 * @author adubra
 */

public class CPVar extends CP {
	String name;
	CP value = null;
	
	public CPVar(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public ClassPropMatch match(boolean isLeft,
			ClassPropMatch previousMatchResult,
			List<ClassReference> inputClasses,
			List<? extends Value<?>> inputValues) {
		return value.match(isLeft, previousMatchResult, inputClasses, inputValues);
	}

	
	
    @Override
    public List<CP> getChildren() {
    	ArrayList<CP> list = new ArrayList<CP>(1);
    	if (value != null) list.add(value);
    	return list;
    }

	@Override
	public CP setVar(String name, CP value) {
		if (this.name.equals(name)){
			this.value = value;
		}
		return this;
	}
}


