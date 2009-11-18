package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

public abstract class AbstractPreorderAnalysis<A extends FlowSet> 
    extends AbstractNodeCaseHandler 
    implements Analysis<A>
{
    public static boolean DEBUG=false;

    protected Map<ASTNode, A> flowSets;
    protected A currentSet;
    protected ASTNode tree;
    protected boolean analyzed = false;

    public A getCurrentSet()
    {
        return currentSet;
    }
    public AbstractPreorderAnalysis(){
        super();
    }
    public AbstractPreorderAnalysis(ASTNode tree){
        this.tree = tree;
        flowSets = new HashMap<ASTNode, A>();
    }

    public Map<ASTNode, A> getFlowSets()
    {
        return flowSets;
    }

    public void setTree( ASTNode t )
    {
        tree = t;
    }
    public ASTNode getTree()
    {
        return tree;
    }

    /**
     * Returns a boolean signifying whether or not the analysis has
     * been performed on a given AST. This does not take into account
     * changes to the AST.
     *
     * @return Whether or not the analysis has been performed.
     */
    public boolean isAnalyzed()
    {
        return analyzed;
    }

    /**
     * Perform the analysis on the entire AST. Also has the efffect of
     * setting the is analyzed status to true.
     * Note, as of this time the analyzed status has no effect on
     * whether or not the analysis is performed. This results in the
     * analysis being able to be run multiple times on the same
     * AST. This could be desirable if the AST has been modified in
     * some way. If you wish to prevent the analysis from being
     * performed you should check the is analyzed status with the
     * isAnalyzed method. The is analyzed method does not however take
     * into account any changes made to the AST since the status was
     * changed. 
     *
     * @see isAnalyzed()
     */
    public void analyze()
    {
        //initialize set
        currentSet = newInitialFlow();
        tree.analyze( this );

        analyzed = true;
    }

    public abstract A newInitialFlow();

    public abstract void caseCondition( Expr condExpr );

    public void caseASTNode(ASTNode node)
    {
        if(DEBUG)
            System.err.println("in caseASTNode for node type " + node.getClass() );
        //visit each child node in forward order
        for( int i = 0; i<node.getNumChild(); i++ ){
            if( node.getChild(i) != null )
                node.getChild(i).analyze( this );
        }
    }
    
}
