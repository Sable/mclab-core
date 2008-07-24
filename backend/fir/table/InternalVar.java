package fir.table;

import fir.type.Intrinsic;

/* an internal variable represents a fortran variable, with its attributes */

public class InternalVar {
	Intrinsic intrinsic;
	int[] shape; //rank and dimensions -- a dimension of -1 is deferred/unknown/variable
}
