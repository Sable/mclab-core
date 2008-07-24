 package fir.builtin.matlabBuiltins;

import fir.ast.Constant;

public class Sin extends Transcendental {
	@Override
	public String getName() {
		return "sin";
			}

	@Override
	public Constant getResult(Constant[] inputs) {
		return null;
	}

}
