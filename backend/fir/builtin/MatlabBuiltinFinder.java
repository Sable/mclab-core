package fir.builtin;

import java.util.LinkedList;
import fir.builtin.matlabBuiltins.*;

/* this helps finding a builtin Matlab function based on name/signature */

public class MatlabBuiltinFinder extends BuiltinFinder {
	Pair[] list = //list of builtin functions given with name
		{
		new Pair("acos",aCos.class),
		new Pair("all",All.class),
		new Pair("asin",aSin.class),
		new Pair("asinh",aSinh.class),
		new Pair("atan",aTan.class),
		new Pair("cos",Cos.class),
		new Pair("cosh",Cosh.class),
		new Pair("exp",Exp.class),
		new Pair("exist",Exist.class),
		new Pair("isdouble",isDouble.class),
		new Pair("log",Log.class),
		new Pair("sin",Sin.class),
		new Pair("sqrt",Sqrt.class),
        new Pair("tan",Tan.class)
 
		};
	
}
