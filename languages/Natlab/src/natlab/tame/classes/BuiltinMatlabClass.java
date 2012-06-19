package natlab.tame.classes;

import java.util.*;

import natlab.tame.classes.reference.BuiltinClassReference;
import natlab.tame.classes.reference.BuiltinCompoundClassReference;
import natlab.tame.classes.reference.FunctionHandleClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.*;

public class BuiltinMatlabClass extends OldMatlabClass{
	BuiltinClassReference classRef;
	
	protected BuiltinMatlabClass(BuiltinClassReference classRef,FileEnvironment fileEnvironment){
		super(classRef.getName(), fileEnvironment);
		this.classRef = classRef;
	}
	
	@Override
	public boolean isSuperior(MatlabClass other) {
		if (other instanceof BuiltinMatlabClass){
			return builtinPriorities.get(classRef) > builtinPriorities.get(((BuiltinMatlabClass)other).classRef);
		} else {
			return false; //anything not a builtin is higher in precedence
		}
	}

	@Override
	public boolean isInferior(MatlabClass other) {
		if (other instanceof BuiltinMatlabClass){
			return builtinPriorities.get(classRef) < builtinPriorities.get(((BuiltinMatlabClass)other).classRef);
		} else {
			return true; //anything not a builtin is higher in precedence
		}
	}
	
	
	static private HashMap<BuiltinClassReference,Integer> builtinPriorities = 
			new HashMap<BuiltinClassReference, Integer>();
	static{
		//we store the priority of all the classes in terms of a number, the higher the number, the higher the priority
		builtinPriorities.put(FunctionHandleClassReference.getInstance(), 5);
		builtinPriorities.put(BuiltinCompoundClassReference.CELL, 4);
		builtinPriorities.put(BuiltinCompoundClassReference.STRUCT, 4);
		builtinPriorities.put(PrimitiveClassReference.INT8, 3);
		builtinPriorities.put(PrimitiveClassReference.INT16, 3);
		builtinPriorities.put(PrimitiveClassReference.INT32, 3);
		builtinPriorities.put(PrimitiveClassReference.INT64, 3);
		builtinPriorities.put(PrimitiveClassReference.UINT8, 3);
		builtinPriorities.put(PrimitiveClassReference.UINT16, 3);
		builtinPriorities.put(PrimitiveClassReference.UINT32, 3);
		builtinPriorities.put(PrimitiveClassReference.UINT64, 3);
		builtinPriorities.put(PrimitiveClassReference.UINT64, 3);
		builtinPriorities.put(PrimitiveClassReference.DOUBLE, 2);
		builtinPriorities.put(PrimitiveClassReference.CHAR, 2);
		builtinPriorities.put(PrimitiveClassReference.LOGICAL, 1);
	}
	
	
	public static void main(String[] args) {
		MatlabClass mclass = new BuiltinMatlabClass(
				PrimitiveClassReference.DOUBLE,new FileEnvironment(GenericFile.create("/home/adubra/mclab/tests/main.m")));
	}
}



