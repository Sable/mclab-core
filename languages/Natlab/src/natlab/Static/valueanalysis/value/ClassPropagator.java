package natlab.Static.valueanalysis.value;

import java.util.List;

import natlab.Static.builtin.Builtin;
import natlab.Static.builtin.BuiltinVisitor;
import natlab.Static.classes.reference.*;

public class ClassPropagator extends BuiltinVisitor<List<ClassReference>,ClassReference>{

    @Override
    public ClassReference caseBuiltin(Builtin builtin, List<ClassReference> arg) {
        throw new UnsupportedOperationException(
                "ClassPropagator unimplemented builtin "+builtin.getName());
    }

}
