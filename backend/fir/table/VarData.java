package fir.table;

import java.util.Vector;

/* a fir variable consists of name,type,and the actual set of frotran variables
 * this class can wrap these up, i.e. provide the actual data variable, as well
 * as the shape and rank reference variables.
 * if the variable has a union type, than this class can wrap multiple vardata's
 * as well.
 * 
 * Basically, a Matlab variable might be represented by multiple fortran variables,
 * this class is intended to negotiate between a Matlab variable and their fortran
 * counterparts.
 */


public class VarData {
	boolean constantValue,constantShape,
			hasShapeReference,shapeReferenceComplete,
			constantRank,hasRankReference,isUnion,
			isShapeReference,isRankReference;

	InternalVar rankReference;
	Vector<InternalVar> shapeReferences;
	Variable var;
	
	boolean isConstantValue(){return constantValue;};
	boolean isConstantShape(){return constantShape;};
	boolean isShapeReference(){return isShapeReference;}
	Variable getVariable(){return var;};
	boolean hasShapeReferences(){return hasShapeReferences();};
	InternalVar getShapeReference(int dimension){
		return shapeReferences.get(dimension);
	};
	boolean hasRankReference(){return hasRankReference;};
	InternalVar getRankReference(){return rankReference;};
}
