package natlab.tame.valueanalysis.advancedMatrix;

import natlab.tame.classes.reference.PrimitiveClassReference; //class    component
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValue;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant; //constant component
import natlab.tame.valueanalysis.components.isComplex.HasisComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.rangeValue.HasRangeValue;
import natlab.tame.valueanalysis.components.rangeValue.RangeValue;
import natlab.tame.valueanalysis.components.shape.HasShape; //shape    component
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.components.shape.ShapePropagator;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;

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
	
	static ShapePropagator<AggrValue<AdvancedMatrixValue>> shapePropagator =
			ShapePropagator.getInstance();

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
	
	public boolean hasConstant() {
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
//
//		if (!(other instanceof AdvancedMatrixValue))
//			throw new UnsupportedOperationException(
//					"can only merge a Matrix Value with another Matrix Value");
//		if (!other.getMatlabClass().equals(classRef))
//			throw new UnsupportedOperationException(
//					"only Values with the same class can be merged, trying to merge :"
//							+ this + ", " + other);
//		System.out.println("this constant is ~" + constant + " " + other);
//
//		if (constant == null) {
//
//			if (((AdvancedMatrixValue) other).constant == null) {
//
//				if (iscomplex == null) {
//					return this;
//				}
//				if (iscomplex.equals(((AdvancedMatrixValue) other)
//						.getisComplexInfo()) != true) {
//					
//					return new AdvancedMatrixValue(this.symbolic, new AdvancedMatrixValue(this.symbolic, 
//							this.classRef),this.shape.merge(((AdvancedMatrixValue) other)
//								.getShape()),
//							this.iscomplex.merge(((AdvancedMatrixValue) other)
//									.getisComplexInfo()));
//				}
//				else{
//					return new AdvancedMatrixValue(this.symbolic, new AdvancedMatrixValue(this.symbolic, 
//							this.classRef),this.shape.merge(((AdvancedMatrixValue) other)
//								.getShape()), this.iscomplex.merge(((AdvancedMatrixValue) other)
//										.getisComplexInfo()));
//				}
//				
//			}
//
//			return this;
//
//		}
//		if (constant.equals(((AdvancedMatrixValue) other).constant)) {
//
//			return this;
//		}
//		AdvancedMatrixValue newMatrix = new AdvancedMatrixValue(this.symbolic, 
//				new AdvancedMatrixValue(this.symbolic, this.classRef),this.shape.merge(((AdvancedMatrixValue) other)
//						.getShape()),
//				this.iscomplex.merge(((AdvancedMatrixValue) other)
//						.getisComplexInfo()));
//
//		System.out.println(newMatrix);
//
//		return newMatrix;
		if (Debug) System.out.println("inside BasicMatrixValue merge!");
		if (!(other instanceof AdvancedMatrixValue))
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
		else if (this.constant!=null&&((AdvancedMatrixValue)other).getConstant()!=null) {
			Constant result = this.constant.merge(((AdvancedMatrixValue)other).getConstant());
			if (result!=null) return factory.newMatrixValue(this.getSymbolic(), result);
		}
		AdvancedMatrixValue newMatrix = factory.newMatrixValueFromClassShapeRange(
				getSymbolic()
				, getMatlabClass()
				, null
				, null
				, null);
		if (hasShape()) {
			newMatrix.shape = shape.merge(((AdvancedMatrixValue)other).getShape());
		}
		if (hasRangeValue()) {
			newMatrix.rangeValue = rangeValue.merge(((AdvancedMatrixValue)other).getRangeValue());
		}
		if (hasisComplexInfo()) {
			if (((AdvancedMatrixValue)other).getisComplexInfo() != null) {
				newMatrix.iscomplex = iscomplex.merge(((AdvancedMatrixValue)other).getisComplexInfo());
			}
		}
		return newMatrix;
		
		
	}

	private boolean hasShape() {
		// TODO Auto-generated method stub
		return shape !=null;
	}
	
	public boolean hasRangeValue() {
		return rangeValue != null;
	}
	
	public boolean hasisComplexInfo() {
		return iscomplex != null;
	}

	@Override
	public boolean equals(Object obj) {
//		if (obj == null)
//			return false;
//		if (!(obj instanceof AdvancedMatrixValue))
//			return false;
//		AdvancedMatrixValue m = (AdvancedMatrixValue) obj;
//		if (isConstant())
//			return constant.equals(m.constant);
//		if (Debug)
//			System.out.println(m.getMatlabClass());
//
//		if ((shape == null)
//				&& (((HasShape) ((AdvancedMatrixValue) obj)).getShape() == null)) {
//			return (classRef.equals(m.getMatlabClass())) && true;
//		}
//		boolean bs = (classRef.equals(m.getMatlabClass()) && shape
//				.equals(((HasShape) ((AdvancedMatrixValue) obj)).getShape()));
//
//		// TODO NOW - complex part
//
//		if ((iscomplex == null)
//				&& (((HasisComplexInfo) ((AdvancedMatrixValue) obj))
//						.getisComplexInfo() == null)) {
//			return (classRef.equals(m.getMatlabClass())) && true;
//		}
//		return ((classRef.equals(m.getMatlabClass()) && iscomplex
//				.equals(((HasisComplexInfo) ((AdvancedMatrixValue) obj))
//						.getisComplexInfo())) && bs);
		
		if (obj == null) {
			return false;
		}
		else if (obj instanceof AdvancedMatrixValue) {
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
				if (iscomplex == null) {
					complexResult = o.getisComplexInfo() == null;
				}
				else {
					complexResult = iscomplex.equals(o.getisComplexInfo());
				}
				return shapeResult && rangeResult && complexResult;
			}
		}
		else {
			return false;		
		}
		
		
	}

	@Override
	public String toString() {
		return "(" + (hasSymbolic()? (symbolic + ",") : "")
				+ (hasMatlabClass()? classRef : ",[mclass propagation fails]") 
				+ (hasConstant()? ("," + constant) : "") 
				+ (hasShape()? ("," + shape) : ",[shape propagation fails]")
				+ (hasRangeValue()? ("," + rangeValue) : "")
				+ (hasisComplexInfo()? ("," + iscomplex) : "") + ")";
	}

	private boolean hasMatlabClass() {
		// TODO Auto-generated method stub
		return classRef != null;
		}

	private boolean hasSymbolic() {
		// TODO Auto-generated method stub
		return symbolic != null;
	}

	public static final AdvancedMatrixValueFactory FACTORY = new AdvancedMatrixValueFactory();
	
//	static ShapePropagator<AggrValue<AdvancedMatrixValue>> shapePropagator = ShapePropagator
//			.getInstance();

	@Override
	public ValueSet<AggrValue<AdvancedMatrixValue>> arraySubsref(
			Args<AggrValue<AdvancedMatrixValue>> indizes) {
		return ValueSet.<AggrValue<AdvancedMatrixValue>> newInstance(
				factory.newMatrixValueFromClassShapeRange(
						getSymbolic()
						, getMatlabClass()
						, shapePropagator.arraySubsref(shape, indizes)
						, null
						, iscomplex)
						);
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
		return factory.newMatrixValueFromClassShapeRange(
				getSymbolic()
				, getMatlabClass()
				, shapePropagator.arraySubsasgn(shape, indizes, value)
				, null
				, iscomplex);
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