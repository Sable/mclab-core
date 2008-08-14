package fir.builtin.matlabBuiltins;

import fir.ast.Constant;

public class Cos extends Transcendental {

	@Override
	public String getName() {
		return "cos";
	}

	@Override
	public Constant getResult(Constant[] inputs) {
		return null;
	}

}
