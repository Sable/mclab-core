package natlab.Static.classes.reference;
/**
 * A reference to a class.
 * Objects of this type are immutable.
 * 
 * These should store as much as possible about the class that is statically known,
 * i.e. known without looking at the actual file environment. These means that the
 * information provided by these references should also be independent of the
 * environment.
 * 
 * @author ant6n
 */
public abstract interface ClassReference {
    public abstract boolean isBuiltin();
    public abstract String getName();
}


