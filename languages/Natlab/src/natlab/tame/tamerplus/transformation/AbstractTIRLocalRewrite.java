package natlab.tame.tamerplus.transformation;

import java.util.List;

import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
/**
 * @see AbstractLocalRewrite 
 *
 */
@SuppressWarnings("rawtypes")
public class AbstractTIRLocalRewrite extends TIRAbstractNodeCaseHandler
{

    protected ASTNode fOldTree;
    protected ASTNode fNewTree;
    protected TransformedNode fNewNode = null;
    protected List fMultiNodes = null;
    protected AbstractTIRLocalRewrite fCallback;
    
    public AbstractTIRLocalRewrite(ASTNode tree)
    {
        fOldTree = tree;
        fCallback = this;
    }
    
    public AbstractTIRLocalRewrite(ASTNode tree, AbstractTIRLocalRewrite callback)
    {
        fOldTree = tree;
        fCallback = callback;
    }
    
    protected void rewrite(ASTNode node)
    {
        fNewNode = null;
        if (node instanceof TIRNode)
        {
            ((TIRNode) node).tirAnalyze(this);
        }
        else
        {
            node.analyze(fCallback);
        }
    }
    
    protected void rewriteChildren(ASTNode node)
    {
        caseASTNode(node);
    }
    
    public void setTree(ASTNode tree)
    {
        fOldTree = tree;
    }
    
    public ASTNode transform()
    {
        rewrite(fOldTree);
        ASTNode returnNode;
        if (fNewNode == null)
        {
            returnNode = fOldTree;
        }
        else 
        {
            returnNode = fNewNode.getSingleNode();
        }
        return returnNode;
    }
    
    @Override
    public void caseASTNode(ASTNode node)
    {
        int childCount = node.getNumChild();
        for (int i = 0; i < childCount; i++)
        {
            rewrite(node.getChild(i));
            if (fNewNode != null)
            {
                if (fNewNode.isSingleNode())
                {
                    node.setChild(fNewNode.getSingleNode(), i);
                }
                else
                {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Generic transformation case received non single nodes from ");
                    msg.append("transforming a child node. This should only happen when the current ");
                    msg.append("case is for a list or otherwise expects this behavior.\n");
                    msg.append("node:\n");
                    msg.append(node.getPrettyPrinted());
                    msg.append("\nchild node transform:\n");
                    msg.append(fNewNode.toString());
                    throw new UnsupportedOperationException(msg.toString());
                }
            }
        }
        fNewNode = null;
    }
    
    public void caseList(ast.List node)
    {
        int childCount = node.getNumChild();
        for (int i = 0; i < childCount; i++)
        {
            ASTNode child = node.getChild(i);
            rewrite(child);
            if (fNewNode != null)
            {
                if (fNewNode.isSingleNode())
                {
                    node.setChild(fNewNode.getSingleNode(), i);
                }
                else if (fNewNode.isEmptyNode())
                {
                    node.removeChild(i);
                    i--;
                }
                else
                {
                    List<ASTNode> nodes = fNewNode.getMultipleNodes();
                    node.removeChild(i);
                    for (ASTNode newChild : nodes)
                    {
                        node.insertChild(newChild, i);
                        i++;
                    }
                    i--;
                }
            }
        }
        fNewNode = null;
    }

}
