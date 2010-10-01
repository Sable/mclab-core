/*package natlab.toolkits.analysis;

import natlab.ast.*;
import java.util.*;
import natlab.toolkits.scalar.*;

public class LiveVarAnalysis extends StructuralBackwardAnalysis<ASTNode, FlowSet>
{

    private FlowSet emptySet;
    public LiveVarAnalysis(ASTNode tree)
    {
        super(tree);

        emptySet = new ArraySparseSet();

        tree.analyze(this);
    }
}
*/