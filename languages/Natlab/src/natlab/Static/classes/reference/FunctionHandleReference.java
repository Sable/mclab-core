package natlab.Static.classes.reference;

public class FunctionHandleReference implements BuiltinClassReference {

    @Override
    public String getName() {
        return "function_handle";
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

}
