package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPVertcatExpr<V extends Value<V>> extends SPAbstractVectorExpr<V>{
	static boolean Debug = false;
	SPArglist<V> vl;
	
	public SPVertcatExpr() {
		
	}
	
	public SPVertcatExpr(SPArglist<V> vl) {
		this.vl = vl;
		//System.out.println("[]");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		if(isPatternSide==true){
			if (Debug) System.out.println("just get into SPVertcatExpr, setIsInsideVertcat is true!");
			previousMatchResult.setIsInsideVertcat(true);
			previousMatchResult.setNumInVertcat(0);//reset it
			//TODO what if vl is null?
			ShapePropMatch<V> match = vl.match(isPatternSide, previousMatchResult, argValues, num);
			if(match.getIsError()==true){
				Shape<V> errorShape = (new ShapeFactory<V>()).newShapeFromIntegers(null);
				errorShape.flagHasError();
				HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
				uppercase.put("vertcat", errorShape);
				ShapePropMatch<V> errorMatch = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
				match.setIsInsideVertcat(false);
				match.comsumeArg();
				return errorMatch;
			}
			match.setIsInsideVertcat(false);
			match.comsumeArg();
			return match;
		}
		else{
			//a vertcat in output side, it should return a shape, and now, I think it should return an ArrayList<Integer>, then call newShapeFromIntegers...
			String[] arg = vl.toString().split(",");
			if(arg[0].equals("1")){
				ArrayList<DimValue> al = new ArrayList<DimValue>(2);
				al.add(new DimValue(1, null));
				try{
					al.add(new DimValue(Integer.parseInt(arg[1]), null));
					Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
					previousMatchResult.addToOutput(shape);
					return previousMatchResult;
				}
				catch(Exception e){
					if(previousMatchResult.hasValue(arg[1])){
						al.add(previousMatchResult.getValueOfVariable(arg[1]));
						Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
						previousMatchResult.addToOutput(shape);
						return previousMatchResult;
					}
					else{
						al.add(null);
						Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
						previousMatchResult.addToOutput(shape);
						return previousMatchResult;
					}
				}
			}
			if(arg[1].equals("1")){
				ArrayList<DimValue> al = new ArrayList<DimValue>(2);
				if(previousMatchResult.hasValue(arg[0])){
					al.add(previousMatchResult.getValueOfVariable(arg[0]));
					al.add(new DimValue(1, null));
					Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
					previousMatchResult.addToOutput(shape);
					return previousMatchResult;
				}
				else{
					al.add(null);
					al.add(new DimValue(1, null));
					Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
					previousMatchResult.addToOutput(shape);
					return previousMatchResult;
				}
			}
			else{
				//FIXME deal with the [m,k] or [m,k,j,..] kinds of output
				if (Debug) System.out.println("inside output vertcat expression!");
				ArrayList<DimValue> al = new ArrayList<DimValue>(arg.length);
				/**
				 * sometime, for loop can not be executed, so you can't put the return inside the for loop.
				 */
				for(String i : arg){
					if(previousMatchResult.hasValue(i)){
						al.add(previousMatchResult.getValueOfVariable(i));
					}
					else{
						al.add(new DimValue());
					}
				}
				if (Debug) System.out.println(al);
				Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
				previousMatchResult.addToOutput(shape);
				return previousMatchResult;	
			}
		}
	}
	
	public String toString(){
		if (vl==null) return "[]";
		return "["+vl.toString()+"]";
	}
}
