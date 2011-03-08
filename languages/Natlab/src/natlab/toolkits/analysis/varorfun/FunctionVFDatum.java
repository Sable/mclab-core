package natlab.toolkits.analysis.varorfun;

/**
 * Concrete implementation of VFDatum for Function semantics.
 * The lattice of values for the values are as follows.
 * BOT &lt AVAR &lt VAR &lt TOP
 * BOT &lt FUN &lt TOP
 */

public class FunctionVFDatum extends AbstractVFDatum
{

    public FunctionVFDatum( )
    {
        super();
    }
    protected FunctionVFDatum( Value v){
        super(v);
    }

    public FunctionVFDatum clone()
    {
        return new FunctionVFDatum( value );
    }

    /**
     * Make value a variable. 
     */
    public void makeVariable()
    {
	value = Value.VAR;
    }

    /**
     * Make value an assigned variable.
     */
    public void makeAssignedVariable()
    {
	value = Value.AVAR;
    }

    /**
     * Make value an ID. 
     */
    public void makeBottom()
    {
	value = Value.BOT;
    }


    /**
     * Make a TOP. This represets a compile error.
     */
    public void makeTop()
    {
	value = Value.TOP;
    }


    /**
     * Make value a function. If value is BOT, assign FUN to value. If
     * value is VAR or AVAR, make value TOP. Otherwise do nothing.
     */
    public void makeFunction()
    {
	value = Value.FUN;
    }

    /**
     * Make value a prefix. 
     */
    public void makePrefix()
    {
	value = Value.PREFIX;
    }
   
    /**
     * Make value a load variable.
     */
    public void makeLDVar()
    {
	value = Value.LDVAR;
    }

    /**
     * Check if datum is considered a variable. It will be considered
     * a variable if it is either a VAR or an AVAR.
     *
     * @return true if VAR or AVAR
     */
    public boolean isVariable()
    {
        return isExactlyVariable() || isExactlyAssignedVariable();
    }

    /**
     * Check if datum is considered an assigned Variable. It is only
     * considered an assigned variable if it is exactly an assigned
     * variable, so value is AVAR
     *
     * @return true if AVAR
     */
    public boolean isAssignedVariable()
    {
        return isExactlyAssignedVariable();
    }

    /**
     * Check if datum is considered a function. It is only considered
     * a function if it is exactly a function, so value is FUN.
     *
     * @return true if FUN
     */
    public boolean isFunction()
    {
        return isExactlyFunction();
    }

    /**
     * Merge this datum with that datum. Make the value of the new
     * datum the max of the two values, ordering by the order
     * described in the class comments.
     *
     * @throws UnsupportedException when a non FunctionVFDatum is given
     */
    public FunctionVFDatum merge(VFDatum that)
    {
        if( !( that instanceof FunctionVFDatum) )
            throw new UnsupportedOperationException( "Trying to merge a FunctionVFDatum with a non FunctionVFDatum" );

        Value ov = that.getValue();
        if( value.equals( ov ) )
            return new FunctionVFDatum( value );

        if( Value.BOT.equals( value ) )
            return new FunctionVFDatum( ov );
	if( Value.BOT.equals( ov ) )
            return new FunctionVFDatum( value );

	if( Value.LDVAR.equals( value ) )
	    return new FunctionVFDatum( ov );

	if( Value.LDVAR.equals( ov ) )
	    return new FunctionVFDatum( value );

	return new FunctionVFDatum( Value.TOP );
    }

    public static FunctionVFDatum merge(FunctionVFDatum in1, FunctionVFDatum in2)
    {
        //This is probably a bad way of doing this. Shouldn't refer
        //explicitly to AbstractVFDatum?
        return (FunctionVFDatum)(AbstractVFDatum.merge(in1,in2));
    }
}