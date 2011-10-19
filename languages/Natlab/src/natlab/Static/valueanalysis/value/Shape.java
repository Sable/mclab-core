package natlab.Static.valueanalysis.value;

public class Shape<D extends MatrixValue<D>> {
    
    public static <D extends MatrixValue<D>> Shape<D> scalar(){
        return new Shape<D>();
    }
    
    
    /**
     * returns true if this shape is scalar or may be scalar
     * returns false if this shape is known to be non-scalar
     */
    public boolean maybeScalar(){
        return true;
    }
    
}
