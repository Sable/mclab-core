package natlab.toolkits.analysis.varorfun;

/**
 * Abstract implementation of the VFDatum interface. Provides some
 * basic functionality.
 */

public abstract class AbstractVFDatum implements VFDatum
{

    protected Value value = Value.BOT;

    /**
     * a protected constructor to set the value of a new datum.
     */
    public AbstractVFDatum( ){
        return;
    }
    protected AbstractVFDatum( Value v ){
        value = v;
    }
    
    public abstract void makeVariable();
    public abstract void makeAssignedVariable();
    public abstract void makeFunction();

    public Value getValue()
    {
        return value;
    }

    public boolean isTop()
    {
        return Value.TOP.equals( value );
    }

    public boolean isBottom()
    {
        return Value.BOT.equals( value );
    }

    public abstract boolean isVariable();
    public boolean isExactlyVariable()
    {
        return Value.VAR.equals( value );
    }

    public abstract boolean isAssignedVariable();
    public boolean isExactlyAssignedVariable()
    {
        return Value.AVAR.equals( value );
    }

    public abstract boolean isFunction();
    public boolean isExactlyFunction()
    {
        return Value.FUN.equals( value );
    }

    public void reset()
    {
        value = Value.BOT;
    }
    public abstract VFDatum merge(VFDatum that);

    /**
     * Merge two datums and return the result.
     *
     * @param in1  The first datum
     * @param in2  The second datum
     *
     * @return The merge of in1 and in2
     */
    public static VFDatum merge(VFDatum in1, VFDatum in2)
    {
        /* 
           try merging 2 into 1, if it fails try 1 into 2.
           The second attempt will succeed where the first failed if 1
           is a subtype of 2.
        */
        try{
            return in1.merge( in2 );
        }catch( UnsupportedOperationException e ){}

        return in2.merge( in1 );
    }
}