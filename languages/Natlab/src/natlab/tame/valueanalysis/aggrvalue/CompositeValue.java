package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.components.isComplex.HasisComplexInfo;
import natlab.tame.valueanalysis.components.shape.HasShape;


public abstract class CompositeValue<D extends MatrixValue<D>> extends AggrValue<D> implements HasShape<AggrValue<D>>, HasisComplexInfo<AggrValue<D>>{
    AggrValueFactory<D> factory;
    public CompositeValue(AggrValueFactory<D> factory){
        this.factory = factory;
    }
    
    
}
