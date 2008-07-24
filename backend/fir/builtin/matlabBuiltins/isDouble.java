package fir.builtin.matlabBuiltins;

import fir.ast.Constant;
import fir.codegen.CodeGenInterfacer;
import fir.table.Signature;
import fir.table.SignatureType;
import fir.type.Type;

//is Double
public class isDouble extends isType {
	public Type getReturnType(Type[] inputTypes){return null;} //null if complicated, or not possible
	public Type[] getReturnTypes(Type[] inputTypes,int numberOfOutputs){return null;}
	public boolean isDefined(Type[] inputTypes){return false;} //false if input is not possible (i.e. sin(logical))
	public boolean isDefined(Type[] inputTypes,int numberOfOutputs){return false;}
	public boolean isDefined(SignatureType signature){return false;} //also includes output types
	
	//constant propagation -- returns null if propagation not possible
	public Constant getResult(Constant[] inputs){return null;}
	public Constant[] getResults(Constant[] inputs,int numberOfOutputs){return null;}
	
	//generate code
	public void generate(CodeGenInterfacer interfacer,Signature signature){}
	public void generateWithConstants(CodeGenInterfacer interfacer,Signature signature,Constant[] constants){}	

}
