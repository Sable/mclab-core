package natlab.tame.valueanalysis.value;

import java.util.*;

import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.constant.*;
import natlab.toolkits.analysis.Mergable;

/**
 * represents a shape. it is represented using matrix values
 * @param <D>
 */


public class Shape<D extends MatrixValue<D>> implements Mergable<Shape<D>>{
	AggrValueFactory<D> factory;
    
    private Shape(AggrValueFactory<D> factory) {
        this.factory = factory;
    }
    
    
    public static <D extends MatrixValue<D>> Shape<D> scalar(AggrValueFactory<D> factory){
        return new Shape<D>(factory);
    }
    
    /**
     * returns a shape with the given dimensions.
     * The given constants should be scalar.
     */
    public static <D extends MatrixValue<D>> Shape<D> newInstance(
            ValueFactory<AggrValue<D>> factory,Collection<Constant> dims){   
        throw new UnsupportedOperationException();//return new Shape<D>(factory);
    }

    
    /**
     * returns a 0x0 shape
     */
    public static <D extends MatrixValue<D>> Shape<D> empty(AggrValueFactory<D> factory){
        return new Shape<D>(factory);
    }
    
    /**
     * returns a shape which would be the result when accessing an array with the given
     * indizes (i.e., the shape [max(a1),max(a2),max(a3),...,max(an)])
     */
    public static <D extends MatrixValue<D>> Shape<D> fromIndizes(AggrValueFactory<D> factory, Args<AggrValue<D>> indizes){
        return new Shape<D>(factory); //TODO
    }
    
    /**
     * returns true if this shape is scalar or may be scalar
     * returns false if this shape is known to be non-scalar
     */
    public boolean maybeScalar(){
        return true;
    }

    /**
     * returns the single linear index that refers to the same element as
     * the given elements.
     */
    public D getLinearIndex(Args<? extends AggrValue<D>> indizes){
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public Shape<D> merge(Shape<D> o) {
        return new Shape<D>(factory);
    }
    
    
    /**
     * returns a shape that is the result of growing this to the given shape.
     * This is different than a merge, for example
     * [2x3] merge [3x2] = [2 or 3 x 2 or 3]
     * whereas
     * [2x3] grow [3x2] = [3x3]
     */
    public Shape<D> grow(Shape<D> o){
        return new Shape<D>(factory);
    }


    /**
     * returns true if the shape is known
     * @return
     */
    public boolean isConstant() {
        return false;
    }    
}
