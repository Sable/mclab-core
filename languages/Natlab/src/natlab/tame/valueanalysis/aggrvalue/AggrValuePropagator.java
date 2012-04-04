package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.value.ValuePropagator;

abstract public class AggrValuePropagator<D extends MatrixValue<D>> extends ValuePropagator<AggrValue<D>>{
	protected AggrValueFactory<D> factory;
	
	
	public AggrValuePropagator(AggrValueFactory<D> factory) {
		super(factory);
		this.factory = factory;
	}

}
