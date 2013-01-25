package natlab.tame.interproceduralAnalysis.examples.reachingdefs;

import java.util.HashSet;

import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.toolkits.analysis.HashSetFlowSet;

public class VarNamesInput extends HashSetFlowSet<VarNamesValue>
{
    private static final long serialVersionUID = 1L;
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
