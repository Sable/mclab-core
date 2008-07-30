package fir.type;
/* types in FIR consist of
 * - general information
 * 		the information that is generic, i.e. the intrinsic and the shape
 * - information specific for variables
 *      the variables that denote shape
 *      
 *      
 * TODO
 *   other types, matlab type
 *   unknown type?
 *   partially known type
 *   scalar integer,double etc
 *   union type
 */


public abstract class Type {
	Intrinsic intrinsic;
	public Intrinsic getIntrinsic(){return intrinsic;}
	int maxRank;
	int minRank;
	Shape shape;
	public Shape getShape(){return shape;}
	//a union type is one that has to be represented by multiple fortran data variables,
	//i.e. for variables with multiple possible intrinsic types
	public boolean isUnionType(){
		return false; //not supported yet
	}
	
	public boolean isFixedRank(){
		return maxRank == minRank;
	}
	
	//get
	public int getMaxRank(){
		return maxRank;
	}
	public int getMinRank(){
		return minRank;
	}
	
	//query
	public boolean isShapeKnown(){
		return true; //TODO
	}
	
}
