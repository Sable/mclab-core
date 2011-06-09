package natlab.Static.classes.reference;

public enum BuiltinCompoundClassReference implements BuiltinClassReference {
    STRUCT,
    CELL;

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

}
