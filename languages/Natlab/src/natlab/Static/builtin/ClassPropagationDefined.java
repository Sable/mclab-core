package natlab.Static.builtin;

/**
 * This interface is for builtins for which class propagation is defined.
 */
public interface ClassPropagationDefined {
    /**
     * returns the class propagaion info for the Builtin, with semantics that closely
     * resembles Matlab, with some of their irregularities smoothed out.
     * These are the semantics that should be used 
     */
    public ClassPropTools.MC getClassPropagationInfo();

    /**
     * returns the class propagaion info for the Builtin, with semantics that matches Matlab
     * as close as possible. This will replicate some of 'strange' Matlab behavior.
     * By default (i.e. usually), this returns the same as ClassPropagationInfo()
     */
    public ClassPropTools.MC getMatlabClassPropagationInfo();
}
