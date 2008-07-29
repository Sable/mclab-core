package fir.builtin;

import java.util.LinkedList;
import fir.builtin.matlabBuiltins.*;

/* this helps finding a builtin Matlab function based on name/signature */

public class MatlabBuiltinFinder extends BuiltinFinder {
	Pair[] list = //list of builtin functions given with name
		{new Pair("isdouble",isDouble.class)
//      ,new Pair("sin",sin.class) -- second example
		};
	
}
