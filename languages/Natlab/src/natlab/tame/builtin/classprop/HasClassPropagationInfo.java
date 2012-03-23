package natlab.tame.builtin.classprop;

import natlab.tame.builtin.classprop.ast.CP;

/**
 * This interface is for builtins for which class propagation is defined.
 */
public interface HasClassPropagationInfo {
    /**
     * returns the class propagaion info for the Builtin, with semantics that closely
     * resembles Matlab, with some of their irregularities smoothed out.
     * These are the semantics that should be used 
     */
    public CP getClassPropagationInfo();

    /**
     * returns the class propagaion info for the Builtin, with semantics that matches Matlab
     * as close as possible. This will replicate some of 'strange' Matlab behavior.
     * By default (i.e. usually), this returns the same as ClassPropagationInfo()
     */
    public CP getMatlabClassPropagationInfo();

    
    public CP getClassPropagationInfo2();
}
