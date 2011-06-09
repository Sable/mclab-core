package natlab.Static.classes.reference;

public enum PrimitiveClassReference implements BuiltinClassReference {
    LOGICAL,
    CHAR,
    SINGLE,
    DOUBLE,
    INT8,
    UINT8,
    INT16,
    UINT16,
    INT32,
    UINT32,
    INT64,
    UINT64;

    private String name = this.name().toLowerCase();
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }
}
