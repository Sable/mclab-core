package fir.builtin.matlabBuiltins;
/* example class .. very skeletal */
import java.util.Vector;

import fir.ast.*;
import fir.codegen.*;
import fir.type.*;
import fir.table.*;

//is Double
public class isDouble extends isType {
	public Type getReturnType(Type[] inputTypes){return null;} //null if complicated, or not possible
	public Type[] getReturnTypes(Type[] inputTypes,int numberOfOutputs){return null;}
	public boolean isDefined(Type[] inputTypes){return false;} //false if input is not possible (i.e. sin(logical))
	public boolean isDefined(Type[] inputTypes,int numberOfOutputs){return false;}
	public boolean isDefined(Signature signature){return false;} //also includes output types
	public String getName(){return "isDouble";}
	
	//constant propagation -- returns null if propagation not possible
	public Constant getResult(Constant[] inputs){return null;}
	public Constant[] getResults(Constant[] inputs,int numberOfOutputs){return null;}
	
	//generate code
	public void generate(ExpressionInterfacer interfacer,Signature signature,Vector<InternalVar> variable){}
	public void generate(StatementInterfacer interfacer,Signature signature,Vector<InternalVar> variable){}
	public void generateWithConstants(ExpressionInterfacer interfacer,Signature signature,Vector<InternalVar> variable,Constant[] constants){}	
	public void generateWithConstants(StatementInterfacer interfacer,Signature signature,Vector<InternalVar> variable,Constant[] constants){}
}

