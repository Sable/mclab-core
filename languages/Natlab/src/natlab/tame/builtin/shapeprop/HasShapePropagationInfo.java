package natlab.tame.builtin.shapeprop;

import natlab.tame.builtin.shapeprop.ast.SPNode;
import natlab.tame.valueanalysis.value.Value;

/**
 * This interface is for builtins for which shape propagation is defined.
 * This is analoguous to classprop.ClassPropagation Defined
 */
public interface HasShapePropagationInfo<V extends Value<V>> {
    /**
     * returns the shape propagation info for the Builtin.
     * FIXME - this should return a proper ast node, not an object
     */
    public SPNode<V> getShapePropagationInfo();
}
