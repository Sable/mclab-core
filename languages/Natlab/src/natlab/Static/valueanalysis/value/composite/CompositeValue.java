package natlab.Static.valueanalysis.value.composite;

import natlab.Static.valueanalysis.value.*;

public abstract class CompositeValue<D extends MatrixValue<D>> implements Value<D> {
    ValueFactory<D> factory;
    public CompositeValue(ValueFactory<D> factory){
        this.factory = factory;
    }
}
