package fir.builtin.matlabBuiltins;

import java.util.Vector;

import fir.ast.Call;
import fir.ast.Constant;
import fir.codegen.ExpressionInterfacer;
import fir.codegen.StatementInterfacer;
import fir.table.InternalVar;
import fir.type.Signature;
import fir.type.Type;

public class All extends MatlabBuiltin {

	
	public void generate(ExpressionInterfacer interfacer, Call call,
			Vector<InternalVar> variable) {
		if(variable.length==1){
			interface.insertExpression(getName()+"("+call.getSignature.getArg(0)+")");
		}else if(variable.length==2) {
			interface.insertExpresion(getName()+"("+call.getSignature.getArg(0)+","+call.getSignature.getArg(1)+")");
		}
		

	}

	@Override
	public void generate(StatementInterfacer interfacer, Signature signature,
			Vector<InternalVar> variable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateWithConstants(ExpressionInterfacer interfacer,
			Signature signature, Vector<InternalVar> variable,
			Constant[] constants) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateWithConstants(StatementInterfacer interfacer,
			Signature signature, Vector<InternalVar> variable,
			Constant[] constants) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "all";
	}

	@Override
	public Constant getResult(Constant[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Constant[] getResults(Constant[] inputs, int numberOfOutputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getReturnType(Type[] inputTypes) {
		if(inputTypes.length<=2){
			return inputTypes[0];
		}else{
			return null;
		}
	}


	@Override
	public boolean isDefined(Type[] inputTypes) {
		if(inputTypes.length<=2){
			return true;
		}else{
		return false;
	}
		}

	

	
}
