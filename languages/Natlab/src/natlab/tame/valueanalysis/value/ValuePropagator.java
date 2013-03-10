package natlab.tame.valueanalysis.value;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.classes.reference.BuiltinClassReference;
import natlab.tame.classes.reference.BuiltinCompoundClassReference;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.FunctionHandleClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;

/**
 * Propagate values for builtin functions.
 * 
 * Some of the cases where the argument types are not matrizes are
 * implemented in this class - this way, a ValueProagator for some
 * given MatrixValue type has to only implement the cases for functions
 * which operate on MatrixValues.
 * 
 * @author ant6n
 */

public abstract class ValuePropagator<V extends Value<V>> 
    extends BuiltinVisitor<Args<V>, Res<V>>{
    
    /**
     * produces the abstract interpretation result of calling the given builtin
     * This is the public interface that users of this class should use
     * @return
     */
    public Res<V> call(String builtin,Args<V> args){
    	Builtin b = Builtin.getInstance(builtin);
        if (b == null){
            throw new UnsupportedOperationException("builtin "+builtin+" not found");
        }
        return call(b, args); 
    }
    /**
     * same as the other call, but with the first argument a builtin
     */
    public Res<V> call(Builtin b,Args<V> args){
        return b.visit(this, args);        
    }
    protected ValueFactory<V> factory;
    /**
     * constructor takes in a MatrixValueFactory
     * TODO - do we need more args?
     */
    public ValuePropagator(ValueFactory<V> factory){
        this.factory = factory;
    }
    
    /**
     * returns the value factory of this value propagator
     */
    public ValueFactory<V> getFactory(){
    	return this.factory;
    }
    
    
    /** general Value propagations *********************************************
     * The following are cases relating to cell arrays, structs, etc., which should
     * be the same for all kinds of values. These should be implemented using 
     * builtins themselves
     */
    
    
    
    /**
     * given directions, returns the dominant argument class for a concatenation
     * operation. All arguments that are of another class should be converted to
     * the dominant class to concatenate.
     * 
     * Returns null if the combination of arguments is invalid
     *
     * TODO - should this be in a separate tools/class?
     * should this be in 
     */
    public ClassReference getDominantCatArgClass(Args<V> args){
        if (args.size() == 0) return PrimitiveClassReference.DOUBLE;
        boolean hasChar = false, hasInt = false, hasSingle = false, hasDouble = false, 
            hasLogical = false, hasStruct = false, hasCell = false;
        BuiltinClassReference intType = null;
        for (ClassReference aClass : args.getClassList()){
           if (aClass instanceof PrimitiveClassReference){
               BuiltinClassReference pClass = (BuiltinClassReference)aClass;
               if (pClass == PrimitiveClassReference.DOUBLE){
                   hasDouble = true;
               } else if (pClass == PrimitiveClassReference.SINGLE){
                   hasSingle = true;
               } else if (pClass == PrimitiveClassReference.CHAR){
                   hasChar = true;
               } else if (pClass == PrimitiveClassReference.LOGICAL){                   
                   hasLogical = true;
               } else if (pClass.isInt()){
                   hasInt = true;
                   intType = (intType == null)?pClass:intType;
               }
           } else if (aClass instanceof FunctionHandleClassReference){
               return null;
           } else if (aClass instanceof BuiltinCompoundClassReference){
               if (((BuiltinCompoundClassReference)aClass) == BuiltinCompoundClassReference.STRUCT){
                   hasStruct = true;
               } else if (((BuiltinCompoundClassReference)aClass) == BuiltinCompoundClassReference.STRUCT){
                   hasStruct = true;
               }
           } else {
               throw new UnsupportedOperationException("bad class "+aClass.getName());
           }
        }
        if (hasStruct || hasCell){
            if (hasStruct && hasCell) return null;
            if (hasStruct) return BuiltinCompoundClassReference.STRUCT;
            return BuiltinCompoundClassReference.CELL;
        }
        if (hasChar){
            if (hasLogical) return null;
            return PrimitiveClassReference.CHAR;
        }
        if (hasInt) return intType;
        if (hasSingle) return PrimitiveClassReference.SINGLE;
        if (hasDouble) return PrimitiveClassReference.DOUBLE;
        return PrimitiveClassReference.LOGICAL;
    }
    
    
}


