package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.builtin.Builtin;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.tame.valueanalysis.value.ValuePropagator;

/**
 * provides propagations for builtins called with composite mclasses like
 * struct, function_handle, or cell. Does not provide propagations
 * for MatrixValues, which should be provided by actual MatrixValue factories.
 * 
 * When implementing a new matrix value, the corresponding matrix value propagator
 * should not be used directory by the analysis - instead, it should use an 
 * AggrValue propagator containing a matrix value propagator.
 * 
 * It also means that when implementing new matrix values, this class may have
 * to be updated if the new abstraction affects the way cells, structs and function
 * handles behave.
 */
final public class AggrValuePropagator<D extends MatrixValue<D>> extends ValuePropagator<AggrValue<D>>{
	private MatrixPropagator<D> matrixValuePropagator;
	
	/**
	 * produce an aggrValuePropagator, given a propagator for the matrix values
	 */
	public AggrValuePropagator(
			MatrixPropagator<D> matrixValuePropagator) {
		super(matrixValuePropagator.getFactory());
		this.matrixValuePropagator = matrixValuePropagator;
	}


	@Override
	//XU add this to support...
	public Res<AggrValue<D>> caseBuiltin(Builtin builtin, Args<AggrValue<D>> args) {
		//TODO - check whether values are only matrizes
		return matrixValuePropagator.call(builtin, args);
	}
}


