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
 *   
 */


public abstract class Type {
	Intrinsic intrinsic;
	public Intrinsic getIntrinsic(){return intrinsic;}
	
	Shape shape;
	public Shape getShape(){return shape;}
}
