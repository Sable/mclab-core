package fir.builtin.matlabBuiltins;

import java.util.Vector;

import fir.ast.Call;
import fir.ast.Constant;
import fir.ast.Procedure;
import fir.codegen.ExpressionInterfacer;
import fir.codegen.StatementInterfacer;
import fir.table.InternalVar;
import fir.table.Variable;
import fir.type.Signature;
import fir.type.Type;

public class test extends MatlabBuiltin {
Procedure test;
Variable[] a;
	@Override
	public void generate(ExpressionInterfacer interfacer, Signature signature,
			Vector<InternalVar> variable) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type[] getReturnTypes(Type[] inputTypes, int numberOfOutputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefined(Type[] inputTypes) {
		return false;
	}

	@Override
	public boolean isDefined(Type[] inputTypes, int numberOfOutputs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefined(Signature signature) {
		// TODO Auto-generated method stub
		return false;
	}

}
