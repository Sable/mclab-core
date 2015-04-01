package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.value.Value;

/**
 * a value class that acts as a superclass to the composite values and 
 * matrix values. It is generic in the actual matrix value implementaiton.
 * Using aggr values, it is possible to extend the value analysis by only
 * extending the abstraction for matrix values, the abstractions for 
 * composite values can be reused.
 *
 */

public abstract class AggrValue<D extends MatrixValue<D>> implements Value<AggrValue<D>> {
	
}


