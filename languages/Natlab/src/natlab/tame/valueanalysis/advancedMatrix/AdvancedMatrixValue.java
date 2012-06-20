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
import natlab.tame.valueanalysis.components.shape.*; //shape    component
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.*;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValue;
//import natlab.tame.valueanalysis.AdvancedMatrix.AdvancedMatrixValueFactory;

/**
 * represents a MatrixValue that is instantiable. It stores a constant, on top
 * of the matlab class
 */
public class AdvancedMatrixValue extends MatrixValue<AdvancedMatrixValue>
		implements HasConstant,
		HasisComplexInfo<AggrValue<AdvancedMatrixValue>>, HasShape<AggrValue<AdvancedMatrixValue>>
{
	static boolean Debug = false;
	
	static AdvancedMatrixValueFactory factory = new AdvancedMatrixValueFactory();
	Constant constant;
	
	Shape<AggrValue<AdvancedMatrixValue>> shape;
	isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex;

	
	
	public AdvancedMatrixValue(PrimitiveClassReference aClass) {
		super(aClass);
	}

	public AdvancedMatrixValue(AdvancedMatrixValue onlyClassInfo,
			Shape<AggrValue<AdvancedMatrixValue>> shape) {
		super(onlyClassInfo.classRef);
		this.shape = shape;
	}
	
	public AdvancedMatrixValue(AdvancedMatrixValue onlyClassInfo,
			Shape<AggrValue<AdvancedMatrixValue>> shape, isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex) {
		super(onlyClassInfo.classRef);
		this.shape = shape;
		this.iscomplex = iscomplex;

	}

	public AdvancedMatrixValue(AdvancedMatrixValue onlyClassInfo,
			isComplexInfo<AggrValue<AdvancedMatrixValue>> iscomplex) {
		super(onlyClassInfo.classRef);
		this.iscomplex = iscomplex;

	}

	

	/**
	 * how to deal with a constant
	 */
	public AdvancedMatrixValue(Constant constant) {
		super(constant.getMatlabClass());

		shape = (new ShapeFactory<AggrValue<AdvancedMatrixValue>>(factory))
				.newShapeFromIntegers(constant.getShape());
		iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory)).newisComplexInfoFromConst(constant.getisComplexInfo());// TODO
		this.constant = constant;
	}

	/**
	 * Use this constructor if mclass and constant value is provided by the user
	 */
	public AdvancedMatrixValue(PrimitiveClassReference aClass, Constant constant) {
		super(aClass);
		
		shape = (new ShapeFactory<AggrValue<AdvancedMatrixValue>>(factory))
				.newShapeFromIntegers(constant.getShape());
		iscomplex = (new isComplexInfoFactory<AggrValue<AdvancedMatrixValue>>(
				factory)).newisComplexInfoFromConst(constant.getisComplexInfo());// TODO
		this.constant = constant;
		
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
		System.out.println("this constant is ~" + constant+" "+other);
		
		if (constant == null) {

			if (((AdvancedMatrixValue) other).constant == null) {
			
				if (iscomplex == null) {
							return this;
				}
				if (iscomplex.equals(((AdvancedMatrixValue) other).getisComplexInfo()) != true) {
					return new AdvancedMatrixValue(new AdvancedMatrixValue(
							this.classRef),
							this.iscomplex.merge(((AdvancedMatrixValue) other)
									.getisComplexInfo()));
				}
			}
			
			return this;
			
			
		}
		if (constant.equals(((AdvancedMatrixValue) other).constant)) {
			
			return this;
		}
		AdvancedMatrixValue newMatrix = new AdvancedMatrixValue(
				new AdvancedMatrixValue(this.classRef),
				this.iscomplex.merge(((AdvancedMatrixValue) other).getisComplexInfo()));
		
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
		boolean bs = (classRef.equals(m.getMatlabClass()) && shape.equals(((HasShape) ((AdvancedMatrixValue) obj)).getShape()));
		
		//TODO NOW - complex part
		
		if ((iscomplex == null)
				&& (((HasisComplexInfo) ((AdvancedMatrixValue) obj)).getisComplexInfo() == null)){
			return (classRef.equals(m.getMatlabClass())) && true;
		}
		return ((classRef.equals(m.getMatlabClass()) && iscomplex
				.equals(((HasisComplexInfo) ((AdvancedMatrixValue) obj)).getisComplexInfo()))&&bs);
	}

	@Override
	public String toString() {
		return "(" + classRef + (isConstant() ? ("," + constant) : "") + ","
				+ shape +","
				+ "{"+iscomplex+"}" + ")";// XU added shape
	}

	public static final AdvancedMatrixValueFactory FACTORY = new AdvancedMatrixValueFactory();

	@Override
	public ValueSet<AggrValue<AdvancedMatrixValue>> arraySubsref(
			Args<AggrValue<AdvancedMatrixValue>> indizes) {
		throw new UnsupportedOperationException(); // TODO
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
			AggrValue<AdvancedMatrixValue> value) {// XU: we don't need to care
													// about value, but we
													// should care about index!
		for (AggrValue<AdvancedMatrixValue> index : indizes) {
			try {
				// deal with constant index
				if ((((AdvancedMatrixValue) index).getConstant()) != null) {
					double castDou = ((DoubleConstant) ((HasConstant) ((AdvancedMatrixValue) index))
							.getConstant()).getValue();
					int castInt = (int) castDou;
//					if (castInt > (this.getShape().getDimensions().get(1))) {
//						if (Debug)
//							System.out
//									.println("the array is going to be expanded, because the index boundary is larger than the array boundary!");
//						ArrayList<Integer> dim = new ArrayList<Integer>(2);
//						dim.add(1);
//						dim.add(castInt);
//						return new AdvancedMatrixValue(
//								new AdvancedMatrixValue(this.getMatlabClass()),
//								(new ShapeFactory<AggrValue<AdvancedMatrixValue>>(
//										factory)).newShapeFromIntegers(dim));
//					}
				}
				// deal with matrix index
//				if ((((AdvancedMatrixValue) index).getShape()) != null) {
//					if ((((AdvancedMatrixValue) index).getShape()
//							.getDimensions().get(1)) > (this.getShape()
//							.getDimensions().get(1))) {
//						if (Debug)
//							System.out
//									.println("the array is going to be expanded, because the index boundary is larger than the array boundary!");
//						return new AdvancedMatrixValue(
//								new AdvancedMatrixValue(this.getMatlabClass()),
//								(new ShapeFactory<AggrValue<AdvancedMatrixValue>>(
//										factory))
//										.newShapeFromIntegers(((HasShape) ((AdvancedMatrixValue) index))
//												.getShape().getDimensions()));
//					}
//				}
			} catch (Exception e) {
				return new AdvancedMatrixValue(new AdvancedMatrixValue(
						this.getMatlabClass()), this.getisComplexInfo());
			}

		}
		return new AdvancedMatrixValue(new AdvancedMatrixValue(
				this.getMatlabClass()), this.getisComplexInfo());// XU modified
															// @21:24,May 13th
	}

	@Override
	public AggrValue<AdvancedMatrixValue> toFunctionArgument(boolean recursive) {
		return this; //FIXME !!!!!
	}

	@Override
	public AggrValue<AdvancedMatrixValue> dotSubsasgn(String field,
			AggrValue<AdvancedMatrixValue> value) {
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public Shape<AggrValue<AdvancedMatrixValue>> getShape() {
		// TODO Auto-generated method stub
		return shape;
	}
}
