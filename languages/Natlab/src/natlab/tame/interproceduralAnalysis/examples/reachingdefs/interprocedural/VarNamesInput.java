package natlab.tame.interproceduralAnalysis.examples.reachingdefs.interprocedural;

import java.util.HashSet;

/**
 * This class is used as input for the interprocedural analysis nodes.
 * It contains the complete set of the variables defined in a function,
 * and an interprocedural analysis, which is used to get callsites.
 * @author Amine Sahibi
 */

import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.toolkits.analysis.HashSetFlowSet;

public class VarNamesInput extends HashSetFlowSet<VarNamesValue>
{
    private InterproceduralAnalysisNode<?, ?, ?> fNode;
    
    protected VarNamesInput(InterproceduralAnalysisNode<?, ?, ?> node)
    {
        super();
        fNode = node;
    }
    
    protected VarNamesInput(InterproceduralAnalysisNode<?, ?, ?> node, HashSetFlowSet<VarNamesValue> set)
    {
        super((HashSet<VarNamesValue>) set.getSet());
        fNode = node;
    }
    
    protected InterproceduralAnalysisNode<?, ?, ?> getNode()
    {
        return fNode;
    }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof VarNamesInput && ((VarNamesInput)o).getNode().equals(fNode) && super.equals(o));
    }
}
