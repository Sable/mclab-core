package natlab.toolkits.analysis;

import natlab.ast.*;
import java.util.*;

/**
 * Abstract implementation of a structural analysis. 
 */
public abstract class AbstractStructuralAnalysis<A> extends AbstractNodeCaseHandler implements StructuralAnalysis<A>
{

    protected A currentOutSet;
    protected A currentInSet;
    protected Map<ASTNode,A> outFlowSets, inFlowSets;
    private AnalysisHelperVisitor helper;

    public AbstractStructuralAnalysis(ASTNode tree){
        super( tree );

        //setup maps
        outFlowSets = new HashMap<ASTNode, A>();
        inFlowSets = new HashMap<ASTNode, A>();

        //setup helper visitor
        helper = new AnalysisHelperVisitor( this, tree );

    }

    public Map<ASTNode, A> getOutFlowSets(){
        return outFlowSets;
    }

    public Map<ASTNode, A> getInFlowSets(){
        return inFlowSets;
    }

    public void caseRangeForStmt(ForStmt node)
    {
        caseForStmt(node);
    }



    public void analyze()
    {
        //initialize currentOutSet
        currentOutSet = newInitialFlow();
        analyze( tree );
    }
    protected void analyze( ASTNode node )
    {
        node.analyze( helper );
    }
    public abstract void merge(A in1, A in2, A out);
    public abstract void copy(A source, A dest);

    public A getCurrentOutSet()
    {
        return currentOutSet;
    }
    public void setCurrentOutSet( A outSet )
    {
        currentOutSet = outSet;
    }
    public A getCurrentInSet()
    {
        return currentInSet;
    }
    public void setCurrentInSet( A inSet )
    {
        currentInSet = inSet;
    }


    public abstract void caseLoopVarAsInit( AssignStmt loopVar );
    //process the loop variable in the initialization part of a for loop
    //eg  for i=start:step:stop
    //treat as i = start
    public abstract void caseLoopVarAsUpdate( AssignStmt loopVar );
    //prosess in the update 
    //treat as i=i+step
    public abstract void caseLoopVarAsCondition( AssignStmt loopVar );
    //process as checking the condition
    //treat as i<=stop or i*sign(step)<=stop*sign(step)

    public abstract void caseCondition( Expr condExpr );
    public abstract A newInitialFlow();
}