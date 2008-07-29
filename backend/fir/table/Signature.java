package fir.table;
import java.util.*;
import fir.type.*;

/* sets signature for function or procedure
 * 
 * - all arguments
 * - types
 * - rank/shape for partially known types
 * - shadows
 * - allocatable,pointer,target,in,out,inout,changeable(value,shape,type)
 * it is basically a Table
 * 
 * the signature represents how data is passed to a function/subroutine.
 * inputVars and outputVars are ordered like inputTypes and outputTypes of SiginatureType, respectively
 * internalVars denote the internal (i.e. fortran) ordering of the variables.
 */

public class Signature {
	SignatureType signatureType;
	Vector<VarData> inputVars;
	Vector<VarData> outputVars;
	Vector<InternalVar> internalVars;
	InternalVar outputVar; //may be null
	boolean isFunction;
	public boolean isFunction(){return isFunction;};
	
	public  String getarg(){return null;}
}
