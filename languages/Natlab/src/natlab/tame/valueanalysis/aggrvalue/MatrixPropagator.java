package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.value.ValuePropagator;

/**
 * In order to use aggr value together with a new matrix value abstraction to extend
 * the value analysis, one has to provide a new matrix propagator. This is a value
 * propagator where it is assumed that any incoming arguments are of type D, i.e.
 * they are matrices. This is enforced by the AggrValuePropagator.
 * 
 * Any analysis based on AggrValues uses an AggrValuePropagator. At its base case,
 * the MatrixPropagator is called to deal with builtins called with matrices.
 */


abstract public class MatrixPropagator<D extends MatrixValue<D>> extends ValuePropagator<AggrValue<D>> {
	protected AggrValueFactory<D> factory;
	
	public MatrixPropagator(AggrValueFactory<D> factory) {
		super(factory);
		this.factory = factory;
	}

}
