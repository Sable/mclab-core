package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.Value;

/*
 * matching expression ANY # is different from the matching expression uppercase,
 * because the same uppercase in shape equation should be equal, if not there will be error,
 * while, for ANY #, just used for matching a shape, and don't care whether or not it's equal to previous matched one.
 */
public class SPAny extends SPAbstractVectorExpr
{
	static boolean Debug = false;
	String s;
	public SPAny(String a){
		this.s = a;
		//System.out.println(a);
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		if (Debug) System.out.println("inside ANY!");
		if(isPatternSide==true){
			if(argValues.get(previousMatchResult.getNumMatched())!=null){
				//get indexing current Matrix Value from args
				//get shape info from current Matrix Value
				Shape<?> argumentShape = ((HasShape)argValues.get(previousMatchResult.getNumMatched())).getShape();
				HashMap<String, Integer> lowercase = new HashMap<String, Integer>();
				lowercase.put(s, null);
				HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();
				uppercase.put(s, argumentShape);
				ShapePropMatch match = new ShapePropMatch(previousMatchResult, lowercase, uppercase);
				match.comsumeArg();
				match.saveLatestMatchedUppercase(s);
				//System.out.println(match.getValueOfVariable(s));
				if (Debug) System.out.println("mathcing "+match.getLatestMatchedUppercase());
				return match;
			}
			return previousMatchResult;
		}
		else{
			if (Debug) System.out.println("inside output uppercase "+s);
			//default
			if(previousMatchResult.getShapeOfVariable(s)==null){
				if(previousMatchResult.getOutputVertcatExpr().size()==1){
					previousMatchResult.addToVertcatExpr(previousMatchResult.getOutputVertcatExpr().get(0));
					previousMatchResult.copyVertcatToOutput(s);
					return previousMatchResult;
				}
				else{
					previousMatchResult.copyVertcatToOutput(s);
					return previousMatchResult;
				}
			}
			else
				previousMatchResult.addToOutput(s, previousMatchResult.getShapeOfVariable(s));
				return previousMatchResult;
				
		}
	}
	
	public String toString(){
		return s.toString();
	}
}
