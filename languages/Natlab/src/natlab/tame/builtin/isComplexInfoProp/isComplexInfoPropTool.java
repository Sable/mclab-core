package natlab.tame.builtin.isComplexInfoProp;

import java.io.StringReader;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.ast.ICCaselist;
import natlab.tame.builtin.isComplexInfoProp.ast.ICNode;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.value.Value;
//import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;

public class isComplexInfoPropTool {

	public static ICCaselist parse(String source) {
		// System.err.println("parsing: "+source);
		isComplexInfoPropParser parser = new isComplexInfoPropParser();
		isComplexInfoPropScanner input = new isComplexInfoPropScanner(
				new StringReader(source));
		// System.out.println(source);
		try {
			ICCaselist iclist = (ICCaselist) parser.parse(input);
			return iclist;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	public static List<isComplexInfo<AggrValue<AdvancedMatrixValue>>> matchByValues(
	public static List<isComplexInfo<?>> matchByValues(
			ICNode equation, List<? extends Value<?>> argValues) {
isComplexInfoPropMatch icMatch = equation.match(true, new isComplexInfoPropMatch(), argValues);
           return icMatch.getAllResults();
	}

//	public static void main(String[] args) throws IOException, Parser.Exception {
//
//		String s1, s2;
//
//		// s1 = parse("R* -> R").toString();
//		System.out.println("parse result : "
//				+ parse(" A,A -> NUMXARGS>0 ? X : R") + "\n");
//
//		 ICNode ic1 = parse("R,R ->R || A,A->NUMXARGS>0?X:A");
//		
//		 List<? extends Value<?>> arg1 = new ArrayList<Integer>(2);
//		 arg1.add(1);
//		 arg1.add(-1);
//		 // arg1.add(0);
//		isComplexInfoPropMatch icMatch1 = ic1.match(true, new isComplexInfoPropMatch(), arg1);
//		System.out.println("result :"+icMatch1.getOutputString());
//
//	}
}
