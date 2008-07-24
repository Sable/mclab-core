/* this represents a general builtin function
 * 
 * It can be used for 
 *  - type inference/propagation/checking
 *  - constant propagation
 *  - code generation
 * 
 * for every program, there should only be one builtin
 */

package fir.builtin;
import fir.table.*;
import fir.type.*;
import fir.ast.*;
import fir.codegen.*;

public abstract class Builtin {
	
	//queries (information about the function)
	public boolean isElemental(){return false;}
	public boolean isFortranIntrinsic(){return false;}
	public abstract String getName();
	//..need more here?
	
	//get return type, type propagation, type checking
	public abstract Type getReturnType(Type[] inputTypes); //null if complicated, or not possible
	public abstract Type[] getReturnTypes(Type[] inputTypes,int numberOfOutputs);
	public abstract boolean isDefined(Type[] inputTypes); //false if input is not possible (i.e. sin(logical))
	public abstract boolean isDefined(Type[] inputTypes,int numberOfOutputs);
	public abstract boolean isDefined(SignatureType signature); //also includes output types
	
	//constant propagation -- returns null if propagation not possible
	public abstract Constant getResult(Constant[] inputs);
	public abstract Constant[] getResults(Constant[] inputs,int numberOfOutputs);
	
	//generate code
	public abstract void generate(CodeGenInterfacer interfacer,Signature signature);
	public abstract void generateWithConstants(CodeGenInterfacer interfacer,Signature signature,Constant[] constants);	
}


