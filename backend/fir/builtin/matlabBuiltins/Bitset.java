package fir.builtin.matlabBuiltins;

import java.util.Vector;

import fir.ast.Call;
import fir.ast.Constant;
import fir.codegen.ExpressionInterfacer;
import fir.codegen.StatementInterfacer;
import fir.table.InternalVar;
import fir.type.Type;

public class Bitset extends MatlabBuiltin {

	@Override
	public void generate(ExpressionInterfacer interfacer, Call call,
			Vector<InternalVar> variable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generate(StatementInterfacer interfacer, Call call,
			Vector<InternalVar> variable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateWithConstants(ExpressionInterfacer interfacer,
			Call call, Vector<InternalVar> variable, Constant[] constants) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateWithConstants(StatementInterfacer interfacer,
			Call call, Vector<InternalVar> variable, Constant[] constants) {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefined(Type[] inputTypes, int numberOfOutputs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefined(Call call) {
		// TODO Auto-generated method stub
		return false;
	}

}
