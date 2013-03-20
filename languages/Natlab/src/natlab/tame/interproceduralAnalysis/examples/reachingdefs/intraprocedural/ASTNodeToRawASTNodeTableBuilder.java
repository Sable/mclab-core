package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.LinkedList;

import analysis.natlab.NatlabAbstractStructuralAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;

@SuppressWarnings("rawtypes")
public class ASTNodeToRawASTNodeTableBuilder extends NatlabAbstractStructuralAnalysis<Object>
{

    private LinkedList<ASTNode> fVisitedNodes;
    private HashMap<ASTNode, ASTNode> fIRToRawASTTable;
    
    public ASTNodeToRawASTNodeTableBuilder(ASTNode tree, LinkedList<ASTNode> visitedNodes, HashMap<ASTNode, ASTNode> IRToRawASTTable)
    {
        super(tree);
        fVisitedNodes = visitedNodes;
        fIRToRawASTTable = IRToRawASTTable;
    }
    
    public void analyze()
    {
        for (ASTNode node : fVisitedNodes)
        {
            // TODO caseLoopVar should be called without intervention
            if (node instanceof AssignStmt && node.getParent() instanceof ForStmt)
            {
                caseLoopVar((AssignStmt) node);
            }
            node.analyze(this);
        }
    }
    
    @Override
    public void caseASTNode(ASTNode node)
    {
        int nodeChildrenCount = node.getNumChild();
        for (int i = 0; i < nodeChildrenCount; i++)
        {
            node.getChild(i).analyze(this);
        }
    }
    
    @Override
    public void caseLoopVar(AssignStmt node)
    {
        
    }
    
    
    @Override
    public void merge(Object in1, Object in2, Object out) {}

    @Override
    public void copy(Object source, Object dest) {}

    @Override
    public Object newInitialFlow()
    {
        return null;
    }

}
