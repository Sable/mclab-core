package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValuePropagator extends
		MatrixPropagator<BasicMatrixValue> {
	
	static boolean Debug = false;
	static ConstantPropagator<AggrValue<BasicMatrixValue>> constantProp = ConstantPropagator
			.getInstance();
	static ClassPropagator<AggrValue<BasicMatrixValue>> classProp = ClassPropagator
			.getInstance();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapeProp = ShapePropagator
			.getInstance();
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
			return Res.<AggrValue<BasicMatrixValue>>newInstance(factory.newMatrixValue(cResult));
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

		// TODO deal with complex info propagation
		// TODO deal with range value propagation
		
		return matchResultToRes(matchClassResult, matchShapeResult);

	}

	private Res<AggrValue<BasicMatrixValue>> matchResultToRes(
			List<Set<ClassReference>> matchClassResult,
			List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult) {
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
		 */
		Res<AggrValue<BasicMatrixValue>> result = Res.newInstance();
		for (int counter=0; counter<matchShapeResult.size(); counter++) {
			HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = 
					new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
			Set<ClassReference> values = matchClassResult.get(counter);
			for (ClassReference classRef : values) {
				map.put(classRef, factory.newMatrixValueFromClassAndShape(
						(PrimitiveClassReference)classRef, matchShapeResult.get(counter)));
			}
			result.add(ValueSet.newInstance(map));
		}
		return result;
	}

	//TODO figure out shape propagation for following cases.
	@Override
    public Res<AggrValue<BasicMatrixValue>> caseAbstractConcatenation(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
        return Res.<AggrValue<BasicMatrixValue>>newInstance(
                factory.newMatrixValueFromClassAndShape(
                        (PrimitiveClassReference)getDominantCatArgClass(arg),null));
    }
    
    //TODO - move to aggr value prop
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCellhorzcat(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> elements) {
        ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues( 
                Args.newInstance(factory.newMatrixValue(1),factory.newMatrixValue(elements.size())));
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(factory, shape, values));
    }
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCellvertcat(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> elements) {
        ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues(
                Args.newInstance(factory.newMatrixValue(elements.size()),factory.newMatrixValue(1)));
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(factory, shape, values));
    }
    
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseCell(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
        return Res.<AggrValue<BasicMatrixValue>>newInstance(new CellValue<BasicMatrixValue>(
                factory, factory.getShapeFactory().newShapeFromValues(arg),ValueSet.<AggrValue<BasicMatrixValue>>newInstance()));
    }
}
