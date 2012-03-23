package natlab.tame.valueanalysis.value;

import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.classes.reference.*;

public class ClassPropagator extends BuiltinVisitor<List<ClassReference>,ClassReference>{

    @Override
    public ClassReference caseBuiltin(Builtin builtin, List<ClassReference> arg) {
        throw new UnsupportedOperationException(
                "ClassPropagator unimplemented builtin "+builtin.getName());
    }

}
