package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.components.rangeValue.*;
import natlab.tame.valueanalysis.components.isComplex.*;
import natlab.tame.valueanalysis.value.*;

/**
 * represents a MatrixValue that is instantiable. It stores a constant, on top
 * of the matlab class
 */
public class BasicMatrixValue extends MatrixValue<BasicMatrixValue> implements
		HasConstant, 
		HasShape<AggrValue<BasicMatrixValue>>, 
		HasRangeValue<AggrValue<BasicMatrixValue>>, 
		HasisComplexInfo<AggrValue<BasicMatrixValue>> {
	
	static boolean Debug = false;
	//MatrixValue has only one protected filed, PrimitiveClassReference classRef.
	protected Constant constant;
	protected Shape<AggrValue<BasicMatrixValue>> shape;
	// with the reference to this range value, we can assign new range value to this basic matrix value.
	protected RangeValue<AggrValue<BasicMatrixValue>> rangeValue;
	// TODO -- also need complex
	protected isComplexInfo<AggrValue<BasicMatrixValue>> complex;
	static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator =
			ShapePropagator.getInstance();

	/**
	 * Construct a BasicMatrixValue based on a Constant.
	 * Whenever we need to construct a BasicMatrixValue based on a Constant,
	 * we need to call corresponding factory method in BasicMatrixValueFactory,
	 * which is newMatrixValue.
	 * TODO, actually, we should rename newMatrixValue to newMatrixValueFromConstant.
	 */
	public BasicMatrixValue(String name, Constant constant) {
		super(name, constant.getMatlabClass());
		this.constant = constant;
		this.shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>())
				.newShapeFromIntegers(constant.getShape());
		//TODO, this line may cause infinite loop.
		if (constant instanceof DoubleConstant) {
			this.rangeValue = (new RangeValueFactory<AggrValue<BasicMatrixValue>>(factory))
					.newRangeValueFromDouble(((DoubleConstant)constant).getValue());			
		}
		this.complex = new isComplexInfoFactory<AggrValue<BasicMatrixValue>>()
				.newisComplexInfoFromStr("REAL");
	}

	/**
	 * Construct a BasicMatrixValue based on a PrimitiveClassReference and a Shape,
	 * for the situation, a variable which doesn't have a constant value.
	 * Whenever we need to construct a BasicMatrixValue based on a 
	 * PrimitiveClassReference and a Shape,
	 * we need to call corresponding factory method in BasicMatrixValueFactory,
	 * which is newMatrixValueFromClassAndShape.
	 */
	public BasicMatrixValue(
			String name,
			PrimitiveClassReference aClass,
			Shape<AggrValue<BasicMatrixValue>> shape,
			RangeValue<AggrValue<BasicMatrixValue>> rangeValue, 
			isComplexInfo<AggrValue<BasicMatrixValue>> complex) {
		super(name, aClass);
		this.shape = shape;
		this.rangeValue = rangeValue;
		this.complex = complex;
	}

	/**
	 * Construct a BasicMatrixValue based on user input Shape information.
	 * Whenever we need to construct a BasicMatrixValue based on a user input 
	 * Shape information, we need to call corresponding factory method in 
	 * BasicMatrixValueFactory, which is newMatrixValueFromInputShape.
	 */
	public BasicMatrixValue(String name
			, PrimitiveClassReference aClass
			, String shapeInfo
			, String complexInfo) {
		super(name, aClass);
		this.shape = new ShapeFactory<AggrValue<BasicMatrixValue>>()
				.newShapeFromInputString(shapeInfo);
		// TODO pass complexInfo
		this.complex = new isComplexInfoFactory<AggrValue<BasicMatrixValue>>()
				.newisComplexInfoFromStr(complexInfo);
	}
	
	public boolean hasSymbolic() {
		return symbolic != null;
	}
	
	public String getSymbolic() {
		return symbolic;
	}
	
	public boolean hasMatlabClass() {
		return classRef != null;
	}

	/**
	 * returns true if the represented data is a constant
	 */
	public boolean hasConstant() {
		return constant != null;
	}

	@Override
	/**
	 * Override the getConstant method in HasConstant Interface, which 
	 * returns the constant represented by this data, or null if it is not constant
	 */
	public Constant getConstant() {
		return constant;
	}
	
	public boolean hasShape() {
		return shape != null;
	}

	/**
	 * Always has shape information? No, if shape propagation fails or not match, 
	 * there will be no shape info from result.
	 */
	public Shape<AggrValue<BasicMatrixValue>> getShape() {
		return shape;
	}
	
	public boolean hasRangeValue() {
		return rangeValue != null;
	}

	public RangeValue<AggrValue<BasicMatrixValue>> getRangeValue() {
		return rangeValue;
	}
	
	public boolean hasisComplexInfo() {
		return complex != null;
	}

	@Override
	public isComplexInfo<AggrValue<BasicMatrixValue>> getisComplexInfo() {
		return complex;
	}
	
	/**
	 * what's this used for?
	 */
	public void setConstantNull() {
		constant = null;
	}

	@Override
	/**
	 * Override the merge method in super class MatrixValue.
	 */
	public BasicMatrixValue merge(AggrValue<BasicMatrixValue> other) {
		if (Debug) System.out.println("inside BasicMatrixValue merge!");
		if (!(other instanceof BasicMatrixValue))
			throw new UnsupportedOperationException(
					"can only merge a Basic Matrix Value with another Basic Matrix Value");
		/**
		 * TODO, currently, we cannot merge two matrix value with different class 
		 * information. i.e. 
		 * 		if
		 *			c='a';
		 *		else
		 *			c=12;
		 *		end
		 * what we have at the merge point is a c=[(double,12.0,[1, 1]), (char,a,[1, 1])]
		 */
		else if (!other.getMatlabClass().equals(this.getMatlabClass()))
			throw new UnsupportedOperationException(
					"only Values with the same class can be merged, trying to merge :"
							+ this + ", " + other + " has failed");
		else if (this.constant!=null&&((BasicMatrixValue)other).getConstant()!=null) {
			Constant result = this.constant.merge(((BasicMatrixValue)other).getConstant());
			if (result!=null) return factory.newMatrixValue(this.getSymbolic(), result);
		}
		BasicMatrixValue newMatrix = factory.newMatrixValueFromClassShapeRange(
				getSymbolic()
				, getMatlabClass()
				, null
				, null
				, null);
		if (hasShape()) {
			newMatrix.shape = shape.merge(((BasicMatrixValue)other).getShape());
		}
		if (hasRangeValue()) {
			newMatrix.rangeValue = rangeValue.merge(((BasicMatrixValue)other).getRangeValue());
		}
		if (hasisComplexInfo()) {
			if (((BasicMatrixValue)other).getisComplexInfo() != null) {
				newMatrix.complex = complex.merge(((BasicMatrixValue)other).getisComplexInfo());
			}
		}
		return newMatrix;
	}

	/**
	 * this method is very important, since it will be used in loop statements 
	 * fix point check. And this equals method is an override method, the input 
	 * argument must be Object class, not BasicMatrixValue or something else. 
	 * Because we use generic (not too few), it's not easy to decide the class 
	 * statically every time, so, we have to use override methods.
	 * 
	 * The BasicMatrixValue class is kind of wrapper of all component values, 
	 * like mclass, shape and rangeValue, so only all component values equals, 
	 * the BasicMatrixValue will return true.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		else if (obj instanceof BasicMatrixValue) {
			if (Debug) System.out.println("inside check whether BasicMatrixValue equals!");
			BasicMatrixValue o = (BasicMatrixValue)obj;
			if (hasConstant()) {
				return constant.equals(o.getConstant());
			}
			else {
				boolean shapeResult, rangeResult, complexResult;
				// test shape
				if (shape == null) {
					return o.getShape() == null;
				}
				else {
					shapeResult = shape.equals(o.getShape());
				}
				// test range value
				if (rangeValue == null) {
					rangeResult = o.getRangeValue() == null;
				}
				else {
					rangeResult = rangeValue.equals(o.getRangeValue());
				}
				// test complex
				if (complex == null) {
					complexResult = o.getisComplexInfo() == null;
				}
				else {
					complexResult = complex.equals(o.getisComplexInfo());
				}
				return shapeResult && rangeResult && complexResult;
			}
		}
		else {
			return false;		
		}
	}

	@Override
	/**
	 * although there should always be mclass and shape info for each variable 
	 * after value propagation, since we need them to declare variable in back end, 
	 * actually, any component info can be null, since mclass, shape or rangeValue 
	 * propagation may not match for some reason.
	 * we should always be careful about null value!
	 */
	public String toString() {
		return "(" + (hasSymbolic()? (symbolic + ",") : "")
				+ (hasMatlabClass()? classRef : ",[mclass propagation fails]") 
				+ (hasConstant()? ("," + constant) : "") 
				+ (hasShape()? ("," + shape) : ",[shape propagation fails]")
				+ (hasRangeValue()? ("," + rangeValue) : "")
				+ (hasisComplexInfo()? ("," + complex) : "") + ")";
	}

	@Override
	public ValueSet<AggrValue<BasicMatrixValue>> arraySubsref(
			Args<AggrValue<BasicMatrixValue>> indizes) {
		return ValueSet.<AggrValue<BasicMatrixValue>> newInstance(
				factory.newMatrixValueFromClassShapeRange(
						getSymbolic()
						, getMatlabClass()
						, shapePropagator.arraySubsref(shape, indizes)
						, null
						, complex)
						);
	}

	@Override
	public AggrValue<BasicMatrixValue> arraySubsasgn(
			Args<AggrValue<BasicMatrixValue>> indizes,
			AggrValue<BasicMatrixValue> value) {
		return factory.newMatrixValueFromClassShapeRange(
				getSymbolic()
				, getMatlabClass()
				, shapePropagator.arraySubsasgn(shape, indizes, value)
				, null
				, complex);
	}

	@Override
	public Res<AggrValue<BasicMatrixValue>> cellSubsref(
			Args<AggrValue<BasicMatrixValue>> indizes) {
		throw new UnsupportedOperationException("cannot curly-braces-access a matrix!");
	}

	@Override
	public AggrValue<BasicMatrixValue> cellSubsasgn(
			Args<AggrValue<BasicMatrixValue>> indizes,
			Args<AggrValue<BasicMatrixValue>> values) {
		throw new UnsupportedOperationException("cannot curly-braces-access a matrix!");
	}

	@Override
	public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
		throw new UnsupportedOperationException("cannot dot-access a matrix!");
	}

	@Override
	public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
			AggrValue<BasicMatrixValue> value) {
		throw new UnsupportedOperationException("cannot dot-access a matrix");
	}

	@Override
	public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
		return this;
		// throw new UnsupportedOperationException(); //TODO
	}
}
