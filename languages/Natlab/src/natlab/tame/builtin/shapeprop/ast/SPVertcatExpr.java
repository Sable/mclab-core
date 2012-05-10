package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.Value;

public class SPVertcatExpr extends SPAbstractVectorExpr
{
	SPVertExprArglist vl;
	public SPVertcatExpr(SPVertExprArglist vl)
	{
		this.vl = vl;
		//System.out.println("[]");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		//System.out.println(isPatternSide);
		if(isPatternSide==true){
			System.out.println("just get into SPVertcatExpr, setIsInsideVertcat is true!");
			previousMatchResult.setIsInsideVertcat(true);
			previousMatchResult.setNumInVertcat(0);//reset it
			ShapePropMatch match = vl.match(isPatternSide, previousMatchResult, argValues);
			match.setIsInsideVertcat(false);
			match.comsumeArg();
			return match;
		}
		else{
			//a vertcat in output side, it should return a shape, and now, I think it should return an ArrayList<Integer>, then call newShapeFromIntegers...
			String[] arg = vl.toString().split(",");
			if(arg[0].equals("1")){
				ArrayList<Integer> al = new ArrayList<Integer>(2);
				al.add(1);
				al.add(previousMatchResult.getValueOfVariable(arg[1]));
				Shape<?> shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>()).newShapeFromIntegers(al);
				previousMatchResult.addToOutput("vertcat", shape);
				return previousMatchResult;
			}
			else{
				//FIXME deal with the [m,k] kinds of output
				System.out.println("inside output vertcat expression!");
				ArrayList<Integer> al = new ArrayList<Integer>(arg.length);
				for(String i : arg){
					al.add(previousMatchResult.getValueOfVariable(i));
				}
				System.out.println(al);
				Shape<?> shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>()).newShapeFromIntegers(al);
				previousMatchResult.addToOutput("vertcat", shape);
				return previousMatchResult;				
			}
		}
	}
	
	public String toString()
	{
		return "["+vl.toString()+"]";
	}
}
