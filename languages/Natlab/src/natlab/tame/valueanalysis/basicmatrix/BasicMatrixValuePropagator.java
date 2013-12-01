package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.rangeValue.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.components.isComplex.*;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValuePropagator extends
		MatrixPropagator<BasicMatrixValue> {
	
	static boolean Debug = false;
	// component propagators
	static ConstantPropagator<AggrValue<BasicMatrixValue>> constantProp =
			ConstantPropagator.getInstance();
	static ClassPropagator<AggrValue<BasicMatrixValue>> classProp = 
			ClassPropagator.getInstance();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapeProp = 
			ShapePropagator.getInstance();
	static RangeValuePropagator<AggrValue<BasicMatrixValue>> rangeValueProp = 
			RangeValuePropagator.getInstance();
	static isComplexInfoPropagator<AggrValue<BasicMatrixValue>> iscomplexProp = 
			isComplexInfoPropagator.getInstance();
	
	static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
	
	public BasicMatrixValuePropagator() {
		super(new BasicMatrixValueFactory());
	}
	
	@Override
	public Res<AggrValue<BasicMatrixValue>> caseBuiltin(
			Builtin builtin,
			Args<AggrValue<BasicMatrixValue>> arg) {
		// do constants propagation first
		if (Debug) System.out.println("built-in:" + builtin + " fn's arguments are " + arg);
		Constant cResult = builtin.visit(constantProp, arg);
		if (Debug) System.out.println("constantProp result: " + cResult);
		if (cResult != null) {
			return Res.<AggrValue<BasicMatrixValue>>newInstance(factory.newMatrixValue(null, cResult));
		}

		// if the result is not a constant, just do mclass propagation
		List<Set<ClassReference>> matchClassResult = builtin.visit(classProp, arg);
		if (Debug) System.out.println("classProp result: " + matchClassResult);
		if (matchClassResult == null) { 
			// class propagation should throw exception
			return Res.newErrorResult(builtin.getName()
					+ " is not defined for arguments " + arg + "as class");
		}

		// if mclass propagation success, do shape propagation.
		List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult = builtin.visit(shapeProp, arg);
		if (Debug) System.out.println("shapeProp result: " + matchShapeResult);
		
		// range value propagation
		RangeValue<AggrValue<BasicMatrixValue>> rangeValueResult = builtin.visit(rangeValueProp, arg);
		
		// iscomplexInfo propagation
		List<isComplexInfo<AggrValue<BasicMatrixValue>>> iscomplexResult = builtin.visit(iscomplexProp, arg);
		
		return matchResultToRes(matchClassResult, matchShapeResult, rangeValueResult, iscomplexResult);

	}

	private Res<AggrValue<BasicMatrixValue>> matchResultToRes(
			List<Set<ClassReference>> matchClassResult,
			List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult,
			RangeValue<AggrValue<BasicMatrixValue>> rangeValueResult,
			List<isComplexInfo<AggrValue<BasicMatrixValue>>> iscomplexResult) {
		/**
		 * currently, class propagation equation doesn't take the number of output arguments 
		 * into consideration, i.e. for the built-in function size(), there can be one output 
		 * argument or more than one output argument, i.e.
		 * 		a=size(c);
		 * 		[a,b]=size(c);
		 * class propagation will return 15 doubles for all the situations as results, 
		 * while shape propagation equation will return corresponding number of results 
		 * based on the number of output arguments. That's the reason why I implement this 
		 * method like this.
		 * TODO maybe modify class propagation language later.
		 * TODO need to think about the rangeValue propagation on built-ins,
		 * currently, we only consider a very few subset of built-ins for rangeValue propagation,
		 * like +, -, * and /, and all of them won't return multiple results.
		 */
		Res<AggrValue<BasicMatrixValue>> result = Res.newInstance();
		if (matchShapeResult!=null) {
			for (int counter=0; counter<matchShapeResult.size(); counter++) {
				HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
				Set<ClassReference> values = matchClassResult.get(counter);
				for (ClassReference classRef : values) {
					map.put(classRef, factory.newMatrixValueFromClassShapeRange(
							null
							, (PrimitiveClassReference)classRef
							, matchShapeResult.get(counter)
							, rangeValueResult
							, iscomplexResult.get(0)
							));
				}
				result.add(ValueSet.newInstance(map));
			}
			return result;			
		}
		else {
			for (Set<ClassReference> values : matchClassResult) {
				HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
				for (ClassReference classRef : values) {
					map.put(classRef, factory.newMatrixValueFromClassShapeRange(
							null
							, (PrimitiveClassReference)classRef
							, null
							, rangeValueResult
							, iscomplexResult.get(0)
							));
				}
				result.add(ValueSet.newInstance(map));
			}
			return result;	
		}
	}

	//TODO figure out shape propagation for following cases.
	
	@Override
    public Res<AggrValue<BasicMatrixValue>> caseAbstractConcatenation(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
		if (Debug) System.out.println("inside BasicMatrixValuePropagator caseAbstractConcatenation.");
		// shape propagation
		List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult = builtin.visit(shapeProp, arg);
		if (matchShapeResult == null) 
			System.err.println("somehow, shape results from caseAbstractConcatenation are null");
		else if (Debug) 
			System.out.println("shape results for caseAbstractConcatenation are " + matchShapeResult);
		// iscomplex propagation
		List<isComplexInfo<AggrValue<BasicMatrixValue>>> iscomplexResult = builtin.visit(iscomplexProp, arg);
		
		Res<AggrValue<BasicMatrixValue>> result = Res.newInstance();
		if (matchShapeResult != null) {
			for (int counter = 0; counter < matchShapeResult.size(); counter++) {
				HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
				map.put((PrimitiveClassReference)getDominantCatArgClass(arg)
						, factory.newMatrixValueFromClassShapeRange(
						null
						, (PrimitiveClassReference)getDominantCatArgClass(arg)
						, matchShapeResult.get(counter)
						, null
						, iscomplexResult.get(0)
						));
				result.add(ValueSet.newInstance(map));
			}
	        return result;
		}
		else {
			HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = 
					new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
			map.put((PrimitiveClassReference)getDominantCatArgClass(arg)
					, factory.newMatrixValueFromClassShapeRange(
					null
					, (PrimitiveClassReference)getDominantCatArgClass(arg)
					, null
					, null
					, iscomplexResult.get(0)
					));
			result.add(ValueSet.newInstance(map));
			return result;
		}
    }
    
    //TODO - move to aggr value prop
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCellhorzcat(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> elements) {
        ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues( 
                Args.newInstance(factory.newMatrixValue(null, 1),factory.newMatrixValue(null, elements.size())));
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(factory, shape, values));
    }
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCellvertcat(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> elements) {
        ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues(
                Args.newInstance(factory.newMatrixValue(null, elements.size()),factory.newMatrixValue(null, 1)));
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(factory, shape, values));
    }
    
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCell(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(
                factory, factory.getShapeFactory().newShapeFromValues(arg),ValueSet.<AggrValue<BasicMatrixValue>>newInstance()));
    }
}
