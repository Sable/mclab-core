package natlab.tame.valueanalysis.advancedMatrix;

import java.util.*;

import natlab.tame.classes.reference.*; //class    component
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*; //constant component
import natlab.tame.valueanalysis.components.isComplex.HasisComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.rangeValue.HasRangeValue;
import natlab.tame.valueanalysis.components.rangeValue.RangeValue;
import natlab.tame.valueanalysis.components.shape.*; //shape    component
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.*;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValue;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

/**
 * represents a MatrixValue that is instantiable. It stores a constant, on top
 * of the matlab class
 */
public class AdvancedMatrixValue extends MatrixValue<AdvancedMatrixValue>
		implements HasConstant,
		HasisComplexInfo<AggrValue<AdvancedMatrixValue>>,
		HasShape<AggrValue<AdvancedMatrixValue>>,
		HasRangeValue<AggrValue<AdvancedMatrixValue>>{
	static boolean Debug = false;

	static AdvancedMatrixValueFactory factory = new AdvancedMatrixValueFactory();
	Constant constant;

	Shape<AggrValue<AdvancedMatrixValue>> shape;
	isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex;
	RangeValue<AggrValue<AdvancedMatrixValue>> rangeValue;

	public AdvancedMatrixValue(String symbolic, PrimitiveClassReference aClass) {
		super(symbolic, aClass);
	}

	public AdvancedMatrixValue(String symbolic, PrimitiveClassReference aClass, String isComplex) {
		super(symbolic, aClass);
		this.iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory)).newisComplexInfoFromConst(isComplex);
	}

	/**
	 * XU add this method to support initial input shape info. svcn
	 * 
	 * @25th,Jul,2012
	 * 
	 * @param aClass
	 * @param shapeInfo
	 * @param isComplex
	 */
	public AdvancedMatrixValue(String symbolic, 
			PrimitiveClassReference aClass,
			String shapeInfo, 
			String isComplex) {
		super(symbolic, aClass);
		this.iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory)).newisComplexInfoFromConst(isComplex);
		this.shape = (new ShapeFactory<AggrValue<AdvancedMatrixValue>>())
				.newShapeFromInputString(shapeInfo);
		}

	public AdvancedMatrixValue(String symbolic, AdvancedMatrixValue onlyClassInfo,
			Shape<AggrValue<AdvancedMatrixValue>> shape) {
		super(symbolic, onlyClassInfo.classRef);
		this.shape = shape;
	}

	public AdvancedMatrixValue(String symbolic, AdvancedMatrixValue onlyClassInfo,
			Shape<AggrValue<AdvancedMatrixValue>> shape,
			isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex) {
		super(symbolic, onlyClassInfo.classRef);
		this.shape = shape;
		this.iscomplex = iscomplex;

	}

	public AdvancedMatrixValue(String symbolic, AdvancedMatrixValue onlyClassInfo,
			isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex) {
		super(symbolic, onlyClassInfo.classRef);
		this.iscomplex = iscomplex;

	}

	/**
	 * how to deal with a constant
	 */
	public AdvancedMatrixValue(String symbolic, Constant constant) {
		super(symbolic, constant.getMatlabClass());

		shape = (new ShapeFactory<AggrValue<AdvancedMatrixValue>>())
				.newShapeFromIntegers(constant.getShape());
		iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory))
				.newisComplexInfoFromConst(constant.getisComplexInfo());// TODO
		this.constant = constant;
	}

	/**
	 * Use this constructor if mclass and constant value is provided by the user
	 */
	public AdvancedMatrixValue(String symbolic, PrimitiveClassReference aClass, Constant constant) {
		super(symbolic, aClass);

		shape = (new ShapeFactory<AggrValue<AdvancedMatrixValue>>())
				.newShapeFromIntegers(constant.getShape());
		iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory))
				.newisComplexInfoFromConst(constant.getisComplexInfo());// TODO
		this.constant = constant;

	}

	public AdvancedMatrixValue(String symbolic, PrimitiveClassReference matlabClass,
			Shape<AggrValue<AdvancedMatrixValue>> shape,
			RangeValue range, 
			isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex) {
		super(symbolic, matlabClass);
		this.shape = shape;
		this.iscomplex = iscomplex;
		this.rangeValue = range;
		
			}

	

	/**
	 * returns true if the represented data is a constant
	 */
	public boolean isConstant() {
		return constant != null;
	}

	@Override
	/**
	 * returns the constant represented by this data, or null if it is not constant
	 */
	public Constant getConstant() {
		return constant;
	}

	public isComplexInfo<AggrValue<AdvancedMatrixValue>> getisComplexInfo() {
		return iscomplex;
	}

	public void setConstantNull() {
		this.constant = null;
	}

	@Override
	public AdvancedMatrixValue merge(AggrValue<AdvancedMatrixValue> other) {

		if (!(other instanceof AdvancedMatrixValue))
			throw new UnsupportedOperationException(
					"can only merge a Matrix Value with another Matrix Value");
		if (!other.getMatlabClass().equals(classRef))
			throw new UnsupportedOperationException(
					"only Values with the same class can be merged, trying to merge :"
							+ this + ", " + other);
		System.out.println("this constant is ~" + constant + " " + other);

		if (constant == null) {

			if (((AdvancedMatrixValue) other).constant == null) {

				if (iscomplex == null) {
					return this;
				}
				if (iscomplex.equals(((AdvancedMatrixValue) other)
						.getisComplexInfo()) != true) {
					return new AdvancedMatrixValue(this.symbolic, new AdvancedMatrixValue(this.symbolic, 
							this.classRef),this.shape.merge(((AdvancedMatrixValue) other)
									.getShape()),
							this.iscomplex.merge(((AdvancedMatrixValue) other)
									.getisComplexInfo()));
				}
			}

			return this;

		}
		if (constant.equals(((AdvancedMatrixValue) other).constant)) {

			return this;
		}
		AdvancedMatrixValue newMatrix = new AdvancedMatrixValue(this.symbolic, 
				new AdvancedMatrixValue(this.symbolic, this.classRef),this.shape.merge(((AdvancedMatrixValue) other)
						.getShape()),
				this.iscomplex.merge(((AdvancedMatrixValue) other)
						.getisComplexInfo()));

		System.out.println(newMatrix);

		return newMatrix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof AdvancedMatrixValue))
			return false;
		AdvancedMatrixValue m = (AdvancedMatrixValue) obj;
		if (isConstant())
			return constant.equals(m.constant);
		if (Debug)
			System.out.println(m.getMatlabClass());

		if ((shape == null)
				&& (((HasShape) ((AdvancedMatrixValue) obj)).getShape() == null)) {
			return (classRef.equals(m.getMatlabClass())) && true;
		}
		boolean bs = (classRef.equals(m.getMatlabClass()) && shape
				.equals(((HasShape) ((AdvancedMatrixValue) obj)).getShape()));

		// TODO NOW - complex part

		if ((iscomplex == null)
				&& (((HasisComplexInfo) ((AdvancedMatrixValue) obj))
						.getisComplexInfo() == null)) {
			return (classRef.equals(m.getMatlabClass())) && true;
		}
		return ((classRef.equals(m.getMatlabClass()) && iscomplex
				.equals(((HasisComplexInfo) ((AdvancedMatrixValue) obj))
						.getisComplexInfo())) && bs);
	}

	@Override
	public String toString() {
		return "(" + classRef + (isConstant() ? ("," + constant) : "") + ","
				+ shape + "," + "{" + iscomplex + "}" + ")";// XU added shape
	}

	public static final AdvancedMatrixValueFactory FACTORY = new AdvancedMatrixValueFactory();
	
	static ShapePropagator<AggrValue<AdvancedMatrixValue>> shapePropagator = ShapePropagator
			.getInstance();

	@Override
	public ValueSet<AggrValue<AdvancedMatrixValue>> arraySubsref(
			Args<AggrValue<AdvancedMatrixValue>> indizes) {
		if(null == indizes)
		{System.out.println("indizes is null");}
		return ValueSet
				.<AggrValue<AdvancedMatrixValue>> newInstance(new AdvancedMatrixValue(null,
						this.getMatlabClass(), shapePropagator.arraySubsref(
								this.getShape(), indizes), null, this.iscomplex));
		// TODO
		//ADD A ISCOMPLEX ANALYSIS TO ISCOMPLEXINFOPROPOGATOR
	}

	@Override
	public ValueSet<AggrValue<AdvancedMatrixValue>> dotSubsref(String field) {
		throw new UnsupportedOperationException("cannot dot-access a matrix");
		// return
		// ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
	}

	@Override
	public Res<AggrValue<AdvancedMatrixValue>> cellSubsref(
			Args<AggrValue<AdvancedMatrixValue>> indizes) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public AggrValue<AdvancedMatrixValue> cellSubsasgn(
			Args<AggrValue<AdvancedMatrixValue>> indizes,
			Args<AggrValue<AdvancedMatrixValue>> values) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public AggrValue<AdvancedMatrixValue> arraySubsasgn(
			Args<AggrValue<AdvancedMatrixValue>> indizes,
			AggrValue<AdvancedMatrixValue> value) {
		return new AdvancedMatrixValue(null, this.getMatlabClass(),
				shapePropagator.arraySubsasgn(this.getShape(), indizes, value), null, this.iscomplex);
	}

	@Override
	public AggrValue<AdvancedMatrixValue> toFunctionArgument(boolean recursive) {
		return this; // FIXME !!!!!
	}

	@Override
	public AggrValue<AdvancedMatrixValue> dotSubsasgn(String field,
			AggrValue<AdvancedMatrixValue> value) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public Shape<AggrValue<AdvancedMatrixValue>> getShape() {
		return this.shape;
	}

	@Override
	public RangeValue<AggrValue<AdvancedMatrixValue>> getRangeValue() {
		// TODO Auto-generated method stub
		return this.rangeValue;
	}
}