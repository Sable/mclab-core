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
     * Make value a variable. If value is BOT or AVAR then change to
     * VAR. If the value is FUN then change to TOP. Otherwise do
     * nothing.
     */
    public void makeVariable()
    {
        if( Value.FUN.equals( value ) )
            value = Value.TOP;

        else if( Value.BOT.equals( value ) || Value.AVAR.equals( value ) || Value.LDVAR.equals(value) )
            value = Value.VAR;
    }

    /**
     * Make value an assigned variable. If value is TOP or AVAR, then
     * do nothing. If value is FUN then make value TOP. Otherwise make
     * value AVAR.
     */
    public void makeAssignedVariable()
    {
        if( Value.BOT.equals( value ) || Value.VAR.equals( value ) || Value.LDVAR.equals(value) )
            value = Value.AVAR;
        else if( Value.FUN.equals( value ) )
            value = Value.TOP;
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
        if( Value.BOT.equals( value ) || Value.LDVAR.equals( value) )
            value = Value.FUN;
        else if( Value.VAR.equals( value ) || Value.AVAR.equals( value ) )
            value = Value.TOP;
    }
   
    /**
     * Make value a load variable. If value is BOT, assign LDVAR to value. 
     * Otherwise do nothing.
     */
    public void makeLDVar()
    {
        if( Value.BOT.equals( value ) )
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
        //not equal at this point
        else if( Value.TOP.equals( value ) || Value.TOP.equals( ov ) )
            return new FunctionVFDatum( Value.TOP );
        //not equal and neither are TOP
        else if( Value.BOT.equals( value ) )
            return new FunctionVFDatum( ov );
        else if( Value.BOT.equals( ov ) )
            return new FunctionVFDatum( value );
        //not equal, neither are TOP or BOT
	else if( Value.LDVAR.equals( value ) && (Value.FUN.equals( ov )  || 
					       Value.VAR.equals( ov )  || 
					       Value.AVAR.equals( ov ) ) )
	    return new FunctionVFDatum( ov );
	else if( Value.LDVAR.equals( ov ) && (Value.FUN.equals( value )  || 
					    Value.VAR.equals( value )  || 
					    Value.AVAR.equals( value ) ) )
	    return new FunctionVFDatum( value );
        else if( Value.FUN.equals( value ) || Value.FUN.equals( ov ) )
            return new FunctionVFDatum( Value.TOP );
        //not equal and each is either AVAR or VAR, so one must be VAR
        else
            return new FunctionVFDatum( Value.VAR );
    }

    public static FunctionVFDatum merge(FunctionVFDatum in1, FunctionVFDatum in2)
    {
        //This is probably a bad way of doing this. Shouldn't refer
        //explicitly to AbstractVFDatum?
        return (FunctionVFDatum)(AbstractVFDatum.merge(in1,in2));
    }
}