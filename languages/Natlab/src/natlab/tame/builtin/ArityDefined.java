package natlab.tame.builtin;

/**
 * This interface is for builtins for which the arity (number of args) is 
 * defined via the builtins.csv. One can read the mininum and maximum number
 * of arguments that the builtin may receive (i.e. for a constant, min=max=0).
 * 
 * This exists mostly to illustration purposes, showing how to add attributes 
 * to the builtins.csv, how to chnage the processTags.py to recognize the attribute,
 * and how to get that into the java code.
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
