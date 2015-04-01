package natlab.tame.classes;

import natlab.tame.classes.reference.BuiltinClassReference;
import natlab.tame.classes.reference.BuiltinCompoundClassReference;
import natlab.tame.classes.reference.FunctionHandleClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.toolkits.path.FileEnvironment;

import com.google.common.collect.ImmutableMap;

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
	
	 //we store the priority of all the classes in terms of a number, the higher the number, the higher the priority
     private static ImmutableMap<BuiltinClassReference,Integer> builtinPriorities =
             ImmutableMap.<BuiltinClassReference, Integer>builder()
                     .put(FunctionHandleClassReference.getInstance(), 6)
                     .put(BuiltinCompoundClassReference.CELL, 5)
                     .put(BuiltinCompoundClassReference.STRUCT, 5)
                     .put(PrimitiveClassReference.INT8, 4)
                     .put(PrimitiveClassReference.INT16, 4)
                     .put(PrimitiveClassReference.INT32, 4)
                     .put(PrimitiveClassReference.INT64, 4)
                     .put(PrimitiveClassReference.UINT8, 4)
                     .put(PrimitiveClassReference.UINT16, 4)
                     .put(PrimitiveClassReference.UINT32, 4)
                     .put(PrimitiveClassReference.UINT64, 4)
                     .put(PrimitiveClassReference.SINGLE, 3)
                     .put(PrimitiveClassReference.DOUBLE, 2)
                     .put(PrimitiveClassReference.CHAR, 2)
                     .put(PrimitiveClassReference.LOGICAL, 1)
                     .build();
}