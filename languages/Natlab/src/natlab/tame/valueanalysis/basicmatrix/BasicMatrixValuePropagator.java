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
	ConstantPropagator<AggrValue<BasicMatrixValue>> constantProp = ConstantPropagator
			.getInstance();
	ClassPropagator<AggrValue<BasicMatrixValue>> classProp = ClassPropagator
			.getInstance();
	ShapePropagator<AggrValue<BasicMatrixValue>> shapeProp = ShapePropagator
			.getInstance();

	public BasicMatrixValuePropagator() {
		super(new BasicMatrixValueFactory());
	}

	/**
	 * base case
	 */
	@Override
	// XU add this function to support the number of output variables
	public Res<AggrValue<BasicMatrixValue>> caseBuiltin(Builtin builtin,
			Args<AggrValue<BasicMatrixValue>> arg) {
		// deal with constants
		if (Debug)
			System.out.println("built-in:" + builtin + " fn's arguments are "
					+ arg);
		Constant cResult = builtin.visit(constantProp, arg);
		if (cResult != null) {
			return Res
					.<AggrValue<BasicMatrixValue>> newInstance(new BasicMatrixValue(
							cResult));
		}

		// if the result is not a constant, just do mclass propagation
		List<Set<ClassReference>> matchClassResult = builtin.visit(classProp,
				arg);
		if (Debug)
			System.out.println("classProp results are " + matchClassResult);
		if (matchClassResult == null) { // class prop returned error
			return Res.newErrorResult(builtin.getName()
					+ " is not defined for arguments " + arg + "as class");
		}

		// deal with shape XU added
		List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult = builtin
				.visit(shapeProp, arg);
		if (Debug)
			System.out.println("shapeProp results are " + matchShapeResult);
		if (matchShapeResult == null) {
			if (Debug)
				System.out.println("shape results are empty");
		}

		// deal with complex

		// build results out of the result classes and shape XU modified, not
		// finished!!!
		return matchResultToRes(matchClassResult, matchShapeResult);

	}

	private Res<AggrValue<BasicMatrixValue>> matchResultToRes(
			List<Set<ClassReference>> matchClassResult,
			List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult) {
		// go through and fill in result
		Res<AggrValue<BasicMatrixValue>> result = Res.newInstance();
		for (Set<ClassReference> values : matchClassResult) {
			HashMap<ClassReference, AggrValue<BasicMatrixValue>> map = new HashMap<ClassReference, AggrValue<BasicMatrixValue>>();
			if (Debug)
				System.out.println(matchShapeResult.get(0));
			for (ClassReference classRef : values) {
				map.put(classRef,
						new BasicMatrixValue(
								(PrimitiveClassReference) classRef,
								matchShapeResult.get(0)));
				// FIXME
			}
			result.add(ValueSet.newInstance(map));
			if (Debug)
				System.out.println(result);
		}
		return result;
	}

	@Override
	public Res<AggrValue<BasicMatrixValue>> caseAbstractConcatenation(
			Builtin builtin, Args<AggrValue<BasicMatrixValue>> arg) {
		if (Debug)
			System.out
					.println("inside BasicMatrixValuePropagator caseAbstractConcatenation!");// XU
		// XU add this block
		List<Shape<AggrValue<BasicMatrixValue>>> matchShapeResult = builtin
				.visit(shapeProp, arg);
		if (Debug)
			System.out.println("shapeProp results are " + matchShapeResult);
		if (matchShapeResult == null) {
			if (Debug)
				System.out.println("shape results are empty");
		}
		// this block ends
		return Res
				.<AggrValue<BasicMatrixValue>> newInstance(new BasicMatrixValue(
						(PrimitiveClassReference) getDominantCatArgClass(arg),
						matchShapeResult.get(0)));// FIXME a little bit tricky
	}

	// TODO - move to aggr value prop. This comment is in Anton's
	// SimpleMatrixValuePropagator.java, do we need to do this later?
	@Override
	public Res<AggrValue<BasicMatrixValue>> caseCellhorzcat(Builtin builtin,
			Args<AggrValue<BasicMatrixValue>> elements) {
		ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet
				.newInstance(elements);
		Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory()
				.newShapeFromValues(
						Args.newInstance(factory.newMatrixValue(1),
								factory.newMatrixValue(elements.size())));
		return Res
				.<AggrValue<BasicMatrixValue>> newInstance(new CellValue<BasicMatrixValue>(
						this.factory, shape, values));
	}

	@Override
	public Res<AggrValue<BasicMatrixValue>> caseCellvertcat(Builtin builtin,
			Args<AggrValue<BasicMatrixValue>> elements) {
		ValueSet<AggrValue<BasicMatrixValue>> values = ValueSet
				.newInstance(elements);
		Shape<AggrValue<BasicMatrixValue>> shape = factory.getShapeFactory()
				.newShapeFromValues(
						Args.newInstance(
								factory.newMatrixValue(elements.size()),
								factory.newMatrixValue(1)));
		return Res
				.<AggrValue<BasicMatrixValue>> newInstance(new CellValue<BasicMatrixValue>(
						this.factory, shape, values));
	}

	@Override
	public Res<AggrValue<BasicMatrixValue>> caseCell(Builtin builtin,
			Args<AggrValue<BasicMatrixValue>> arg) {
		return Res
				.<AggrValue<BasicMatrixValue>> newInstance(new CellValue<BasicMatrixValue>(
						this.factory, factory.getShapeFactory()
								.newShapeFromValues(arg), ValueSet
								.<AggrValue<BasicMatrixValue>> newInstance()));
	}
}
