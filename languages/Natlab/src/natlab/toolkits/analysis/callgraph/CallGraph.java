package natlab.toolkits.analysis.callgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import natlab.toolkits.analysis.handlepropagation.MayMustTreeSet;
import ast.ASTNode;

public class CallGraph
{

    public HashMap<ASTNode, TreeSet<CallSiteLabel>> programLabelMap;
    public HashMap<ASTNode, CallSiteLabel> labelMap;
    public HashMap<CallSiteLabel, MayMustTreeSet<ASTNode>> targetMap;
    protected Map<CallSiteLabel, ASTNode> labelProgramMap;

    public CallGraph( HashMap<ASTNode, TreeSet<CallSiteLabel>> programLabelMap,
                      HashMap<ASTNode, CallSiteLabel> labelMap,
                      HashMap<CallSiteLabel, MayMustTreeSet<ASTNode>> targetMap,
                      Map<CallSiteLabel, ASTNode> labelProgramMap )
    {
        this.programLabelMap = programLabelMap;
        this.labelMap = labelMap;
        this.targetMap = targetMap;
        this.labelProgramMap = labelProgramMap;
    }

    /**
     * Gets all the call site labels for a given callable. A callable
     * is either a function or a script.
     */
    public TreeSet<CallSiteLabel> getCallSiteLabelsForCallables( ASTNode n )
    {
        return programLabelMap.get(n);
    }
    /**
     * Gets call site label for a given call node, either a
     * parametrized expression or a name expression.
     */
    public CallSiteLabel getCallSiteLabel( ASTNode n )
    {
        return labelMap.get( n );
    }

    /**
     * Gets call targets by the call site label.
     */
    public MayMustTreeSet<ASTNode> getTargets( CallSiteLabel label )
    {
        return targetMap.get( label );
    }

    /**
     * Gets call targets for a given call node. Such nodes can be
     * parameterized expressions or name expressions. This is
     * equivalent to calling getTargets( getCallSiteLabel( site ))
     * when the given site is a call site. 
     */
    public MayMustTreeSet<ASTNode> getTargets(ASTNode site)
    {
        CallSiteLabel label = labelMap.get(site);
        if( label == null )
            return null;
        else
            return targetMap.get( label );
    }

    /**
     * Gets the Callable that contains the given call site label.
     */
    public ASTNode getCallableByLabel( CallSiteLabel label )
    {
        return labelProgramMap.get( label );
    }
    /**
     * Checks to see if the given node might represent a call site.
     */
    public boolean isPossibleCallSite(ASTNode site)
    {
        MayMustTreeSet targets = getTargets( site );
        return targets != null;
    }

    /**
     * Checks to see if the given node is definitely a call site.
     */
    public boolean isDefiniteCallSite(ASTNode site)
    {
        MayMustTreeSet targets = getTargets( site );
        return targets != null && targets.isMust();
    }
}