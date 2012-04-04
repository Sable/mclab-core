package natlab.tame.valueanalysis.aggrvalue;


public abstract class CompositeValue<D extends MatrixValue<D>> extends AggrValue<D> {
    AggrValueFactory<D> factory;
    public CompositeValue(AggrValueFactory<D> factory){
        this.factory = factory;
    }
}
