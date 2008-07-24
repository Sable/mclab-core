package fir.builtin.matlabBuiltins;

import fir.ast.Constant;
import fir.type.*;


public class Sin extends Transcendental {
	@Override
	public String getName() {
		if (inputType[0].getIntrisic() instanceof Double)
			{return "dsin";
			}
		if(inputTypes[0].getIntrisinc() instanceof Complex){
			return "csin";
		}
		return "sin";
			}

	@Override
	public Constant getResult(Constant[] inputs) {
		return null;
	}

}
