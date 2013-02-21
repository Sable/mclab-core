package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

/**
 * represents a MatrixValue that is instantiable. It stores a constant, on top
 * of the matlab class
 */
public class BasicMatrixValue extends MatrixValue<BasicMatrixValue> implements
		HasConstant, HasShape<AggrValue<BasicMatrixValue>> {
	
	static boolean Debug = false;
	//MatrixValue has only one protected filed, PrimitiveClassReference classRef.
	protected Constant constant;
	protected Shape<AggrValue<BasicMatrixValue>> shape;
	// TODO -- also need complex
	static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator = ShapePropagator
			.getInstance();

	/**
	 * Construct a BasicMatrixValue based on a Constant.
	 * Whenever we need to construct a BasicMatrixValue based on a Constant,
	 * we need to call corresponding factory method in BasicMatrixValueFactory,
	 * which is newMatrixValue.
	 * TODO, actually, we should rename newMatrixValue to newMatrixValueFromConstant.
	 */
	public BasicMatrixValue(Constant constant) {
		super(constant.getMatlabClass());
		this.constant = constant;
		this.shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory))
				.newShapeFromIntegers(constant.getShape());
		//TODO, this line may cause infinite loop.
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
			PrimitiveClassReference aClass,
			Shape<AggrValue<BasicMatrixValue>> shape) {
		super(aClass);
		this.constant = null;
		this.shape = shape;
	}

	/**
	 * Construct a BasicMatrixValue based on user input Shape information.
	 * Whenever we need to construct a BasicMatrixValue based on a user input 
	 * Shape informaiton, we need to call corresponding factory method in 
	 * BasicMatrixValueFactory, which is newMatrixValueFromInputShape.
	 */
	public BasicMatrixValue(PrimitiveClassReference aClass, String shapeInfo) {
		super(aClass);
		this.constant = null;
		this.shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)
				.newShapeFromInputString(shapeInfo));
	}

	/**
	 * returns true if the represented data is a constant
	 */
	public boolean isConstant() {
		return this.constant!=null;
	}

	@Override
	/**
	 * Override the getConstant method in HasConstant Interface, which 
	 * returns the constant represented by this data, or null if it is not constant
	 */
	public Constant getConstant() {
		return constant;
	}

	/**
	 * Always has shape information? Based on current three constructor,
	 * yes, BasicMatrixValue object always has shape infor.
	 */
	public Shape<AggrValue<BasicMatrixValue>> getShape() {
		return this.shape;
	}

	/**
	 * what's this used for?
	 */
	public void setConstantNull() {
		this.constant = null;
	}

	@Override
	/**
	 * Override the merge method in super class MatrixValue.
	 */
	public BasicMatrixValue merge(AggrValue<BasicMatrixValue> other) {
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
		if (!other.getMatlabClass().equals(this.getMatlabClass()))
			throw new UnsupportedOperationException(
					"only Values with the same class can be merged, trying to merge :"
							+ this + ", " + other + " has failed");
		if(this.getConstant()!=null&&((BasicMatrixValue)other).getConstant()!=null) {
			Constant result = this.getConstant().merge(((BasicMatrixValue)other).getConstant());
			if(result!=null) return factory.newMatrixValue(result);
		}
		BasicMatrixValue newMatrix = factory.newMatrixValueFromClassAndShape(this.getMatlabClass(),
				this.getShape().merge(((BasicMatrixValue)other).getShape()));
		return newMatrix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof BasicMatrixValue)) return false;
		BasicMatrixValue m = (BasicMatrixValue)obj;
		if (this.isConstant()) return this.getConstant().equals(m.getConstant());
		if ((this.getShape()==null)&&(((HasShape<AggrValue<BasicMatrixValue>>)m).getShape()==null)) {
			return this.getMatlabClass().equals(m.getMatlabClass());
		}
		return this.getMatlabClass().equals(m.getMatlabClass())
				&&this.getShape().equals(((HasShape<AggrValue<BasicMatrixValue>>)m).getShape());
	}

	@Override
	/**
	 * always has class infor and shape infor.
	 */
	public String toString() {
		return "(" + this.getMatlabClass() 
				+ (isConstant() ? ("," + this.getConstant()) : "") 
				+ "," + this.getShape() + ")";
	}

	@Override
	public ValueSet<AggrValue<BasicMatrixValue>> arraySubsref(
			Args<AggrValue<BasicMatrixValue>> indizes) {
		return ValueSet.<AggrValue<BasicMatrixValue>> newInstance(
				factory.newMatrixValueFromClassAndShape(this.getMatlabClass(), 
						shapePropagator.arraySubsref(this.getShape(), indizes)));
	}

	@Override
	public AggrValue<BasicMatrixValue> arraySubsasgn(
			Args<AggrValue<BasicMatrixValue>> indizes,
			AggrValue<BasicMatrixValue> value) {
		return factory.newMatrixValueFromClassAndShape(this.getMatlabClass(),
				shapePropagator.arraySubsasgn(this.getShape(), indizes, value));
	}

	@Override
	public Res<AggrValue<BasicMatrixValue>> cellSubsref(
			Args<AggrValue<BasicMatrixValue>> indizes) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public AggrValue<BasicMatrixValue> cellSubsasgn(
			Args<AggrValue<BasicMatrixValue>> indizes,
			Args<AggrValue<BasicMatrixValue>> values) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
		throw new UnsupportedOperationException("cannot dot-access a matrix!");
		// return
		// ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
	}

	@Override
	public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
			AggrValue<BasicMatrixValue> value) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
		return this;
		// throw new UnsupportedOperationException(); //TODO
	}
}
