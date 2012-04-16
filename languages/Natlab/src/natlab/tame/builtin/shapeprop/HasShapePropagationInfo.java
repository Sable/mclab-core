package natlab.tame.builtin.shapeprop;

import natlab.tame.builtin.shapeprop.ast.SPNode;

/**
 * This interface is for builtins for which shape propagation is defined.
 * This is analoguous to classprop.ClassPropagation Defined
 */
public interface HasShapePropagationInfo {
    /**
     * returns the shape propagation info for the Builtin.
     * FIXME - this should return a proper ast node, not an object
     */
    public SPNode getShapePropagationInfo();
}
