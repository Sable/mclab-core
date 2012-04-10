package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.value.*;

abstract public class MatrixPropagator<D extends MatrixValue<D>> extends ValuePropagator<AggrValue<D>> {
	protected AggrValueFactory<D> factory;
	
	public MatrixPropagator(AggrValueFactory<D> factory) {
		super(factory);
		this.factory = factory;
	}

}
