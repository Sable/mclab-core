package natlab.Static.builtin;

/**
 * This interface is for builtins for which the arity (number of args) is 
 * defined via the builtins.csv. One can read the mininum and maximum number
 * of arguments that the builtin may receive (i.e. for a constant, min=max=0).
 * 
 * if the function supports an unbounded number of args, then 
 *   max=INFINITE=Integer.MAX_VALUE
 */
public interface ArityDefined{
    public static final int INFINITE = Integer.MAX_VALUE;
    /**
     * returns the maximum number of allowable arguments, or
     * INFINITE if the the function can take arbitrary many arguments
     */
    public int getMaxNumOfArgs();
    /**
     * returns the minimum number of allowable arguments.
     */
    public int getMinNumOfArgs();

    /**
     * returns true if the builtin is variadic, i.e. max=min.
     */
    public boolean isVariadic();
}
