package natlab.tame.valueanalysis.value;

import java.util.List;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.constant.Constant;

/**
 * this is a special value that represents the colon ':' used in indexing operations.
 * This should only be used to pass colon into subsref. No operation should ever return
 * it.
 * @author ant6n
 * @param <D>
 */
public class ColonValue<D extends MatrixValue<D>> extends SpecialValue<D> {

    @Override
    public Constant getConstant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassReference getMatlabClass() {
        return null;
    }

    @Override
    public Shape<D> getShape() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasShape() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValueSet<D> arraySubsref(Args<D> indizes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ValueSet<D> dotSubsref(String field) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Value<D> arraySubsasgn(Args<D> indizes,Value<D> value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Value<D> dotSubsasgn(String field, Value<D> value) {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public Res<D> cellSubsref(Args<D> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Value<D> cellSubsasgn(Args<D> indizes, Args<D> values) {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public Value<D> merge(Value<D> o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value<D> toFunctionArgument(boolean recursive) {
        throw new UnsupportedOperationException();
    }


}
