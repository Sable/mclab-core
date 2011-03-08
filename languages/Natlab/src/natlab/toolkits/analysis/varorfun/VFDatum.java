package natlab.toolkits.analysis.varorfun;

/**
 * Interface for the datums used by var or function analysis.
 */

public interface VFDatum
{

    public enum Value {TOP, VAR, AVAR, PREFIX, FUN, LDVAR, BOT};

    /**
     * Make the value a variable
     */
    public void makeVariable();

    /**
     * Make the value an assigned variable.
     */
    public void makeAssignedVariable();

    /**
     * Make the value an assigned variable.
     */
    public void makeTop();

    /**
     * Make the value a function
     */
    public void makeFunction();

    /**
     * Query the value of this datum
     *
     * @return value of this datum
     */
    public Value getValue();
    
    /**
     * Check if the datum value is unknown or top.
     *
     * @return true if value is unknown or Top
     */
    public boolean isTop();

    /**
     * Check if you have no information about the datum.
     *
     * @return true if value is BOT, you have no information. 
     */
    public boolean isBottom();

    /**
     * Check if datum is a variable. Note this may return true even if
     * getValue does not return VAR.
     *
     * @return true if value can be considered a Variable
     */
    public boolean isVariable();

    /**
     * Check if datum is exactly a variable. This will be true if and
     * only if getValue return VAR.
     *
     * @return true if and only if the value is exactly a variable
     */
    public boolean isExactlyVariable();

    /**
     * Check if datum is an assigned variable. Note this may return
     * true even if getValue does not return AVAR.
     *
     * @return true if value can be considered an assigned variable
     */
    public boolean isAssignedVariable();

    /**
     * Check if datum is exactly an assigned variable. This will be
     * true if and only if getValue return AVAR.
     *
     * @return true if and only if the value is exactly an assigned
     * variable
     */
    public boolean isExactlyAssignedVariable();

    /**
     * Check if datum is a function. Note this may return
     * true even if getValue does not return FUN.
     *
     * @return true if value can be considered an function
     */
    public boolean isFunction();

    /**
     * Check if datum is exactly a function. This will be
     * true if and only if getValue return FUN.
     *
     * @return true if and only if the value is exactly a function
     */
    public boolean isExactlyFunction();


    /**
     * Reset to the initial value.
     */
    public void reset();

    /**
     * Merge one datum with this and return the result.
     *
     * @param that  The datum to merge with
     *
     * @return The resulting merged datum
     */
    public VFDatum merge(VFDatum that);

    /**
     * Clone the datum.
     */
    public VFDatum clone();
}