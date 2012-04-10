package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

/**
 * represents a shape. it is represented using an array of values.
 * TODO - what to do about top?
 */


public class Shape<V extends Value<V>> implements Mergable<Shape<V>>{
	private ValueFactory<V> factory;
    //FIXME -- actually put dimensions
	
    protected Shape(ValueFactory<V> factory,List<V> dimensions) {
        this.factory = factory;
        //FIXME -- actually put dimensions
    }
    
    
    
    
    /**
     * returns true if this shape is scalar or may be scalar
     * returns false if this shape is known to be non-scalar
     */
    public boolean maybeScalar(){
        return true;//TODO
    }
    
    /**
     * returns true if this shape is known to be scalar.
     * returns false if this shape may or may not be scalar.
     */
    public boolean isSclar(){
    	return false;//TODO
    }

    /**
     * returns the single linear index that refers to the same element as
     * the given elements.
     */
    public V getLinearIndex(Args<V> indizes){
        throw new UnsupportedOperationException(); //TODO
    }
    
    
    /**
     * returns the first Non-Singleton dimension.
     * This may be known (i.e. a constant), or not.
     */
    public V getFirstNonSingletonDimension(){
    	throw new UnsupportedOperationException(); //TODO
    }
    
    
    @Override
    public Shape<V> merge(Shape<V> o) {
        return this; //FIXME
    }
    
    
    /**
     * returns a shape that is the result of growing this to the given shape.
     * This is different than a merge, for example
     * [2x3] merge [3x2] = [2 or 3 x 2 or 3]
     * whereas
     * [2x3] grow [3x2] = [3x3]
     */
    public Shape<V> grow(Shape<V> o){
        return new Shape<V>(factory, null); //TODO
    }
    
    /**
     * returns a shape that is the result of taking a matlab value with
     * a shape of this, and then index-assigning it with the given indizes.
     * i.e. if matlab variable A has the shape represented by this object,
     * and indizes i,j are represented by the arguments indizes, then
     * the Matlab assignment
     * A(i,j) = ...
     * will result in a shape for A that is the result of this method.
     */
    public Shape<V> growByIndices(Args<V> indizes){
    	return this; //FIXME -- do something here. For now, assume matrizes don't grow.
    }

    /**
     * returns true if the shape is known
     * @return
     */
    public boolean isConstant() {
        return false; //TODO
    }    
}
