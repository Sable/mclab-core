package fir.builtin;

import java.util.LinkedList;

import fir.builtin.matlabBuiltins.*;
/* this helps finding a builtin Matlab function based on name/signature */
import fir.builtin.matlabBuiltins.isDouble;

public class MatlabBuiltinFinder extends BuiltinFinder {
	Pair[] list = //list of builtin functions given with name
		{   new Pair("acos",aCos.class),
			new Pair("asin",aSin.class),
			new Pair("atan",aTan.class),
			new Pair("atan2",aTan2.class),
			new Pair("cos",Cos.class),
			new Pair("exp",Exp.class),
			new Pair("isdouble",isDouble.class),
			new Pair("log",Log.class),
			new Pair("sin",Sin.class),
			new Pair("tan",Tan.class),

		
		};
	
}
