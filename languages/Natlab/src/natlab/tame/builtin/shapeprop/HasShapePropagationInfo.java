package natlab.tame.builtin.shapeprop;

/**
 * This interface is for builtins for which shape propagation is defined.
 * This is analoguous to classprop.ClassPropagationDefined
 */
public interface HasShapePropagationInfo {
    /**
     * returns the shape propagation info for the Builtin.
     * FIXME - this should return a proper ast node, not an object
     */
    public Object getShapePropagationInfo();
}
