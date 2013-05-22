package natlab.tame.valueanalysis.value;

import java.util.List;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.shape.Shape;

/**
 * this is a special value that represents the colon ':' used in indexing operations.
 * This should only be used to pass colon into subsref. No operation should ever return
 * it.
 * @author ant6n
 * @param <V>
 */
public class ColonValue<V extends Value<V>> extends SpecialValue<V> {

	@Override
	public String getSymbolic() {
		return null;
	}
	
    @Override
    public ClassReference getMatlabClass() {
        return null;
    }

    @Override
    public ValueSet<V> arraySubsref(Args<V> indizes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ValueSet<V> dotSubsref(String field) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V arraySubsasgn(Args<V> indizes,V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V dotSubsasgn(String field, V value) {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public Res<V> cellSubsref(Args<V> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public V cellSubsasgn(Args<V> indizes, Args<V> values) {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public V merge(V o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V toFunctionArgument(boolean recursive) {
        throw new UnsupportedOperationException();
    }


}
