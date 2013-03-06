package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;

import natlab.tame.tir.TIRCommaSeparatedList;
import analysis.natlab.NatlabAbstractStructuralAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;
import ast.Name;
import ast.NameExpr;
import ast.RangeExpr;

@SuppressWarnings("rawtypes")
public class RenameVariablesForASTNodes extends NatlabAbstractStructuralAnalysis<Object>
{
    // Member variables
    UDDUWeb fUDDUWeb;
    
    public RenameVariablesForASTNodes(ASTNode tree, UDDUWeb udduWeb)
    {
        super(tree);
        fUDDUWeb = udduWeb;
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
    
    public void analyze()
    {
        for (ASTNode node : fUDDUWeb.getVisitedStmtsLinkedList())
        {
            // TODO caseLoopVar should be called with intervention
            if (node instanceof AssignStmt && node.getParent() instanceof ForStmt)
            {
                caseLoopVar((AssignStmt) node);
            }
            node.analyze(this);
        }
    }
    
    @Override
    public void caseLoopVar(AssignStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = null;
        TIRCommaSeparatedList usedVariablesNames = null;
        definedVariablesNames = new TIRCommaSeparatedList((NameExpr) node.getLHS());

        RangeExpr range = (RangeExpr) node.getRHS();
        usedVariablesNames = new TIRCommaSeparatedList((NameExpr) range.getLower());
        if (range.hasIncr())
        {
            usedVariablesNames.add((NameExpr) range.getIncr());
        }
        usedVariablesNames.add((NameExpr) range.getUpper());

        renameDefinedVariables(definedVariablesNames, node);
        renameUsedVariables(usedVariablesNames, node);
    }

    /**
     * Renames used variables names for a particular node using the UDDU web
     * @param usedVariablesNames
     * @param parentNode
     */
    public void renameUsedVariables(TIRCommaSeparatedList usedVariablesNames, ASTNode parentNode)
    {
        for (int i = 0; i < usedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = usedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariable(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    /**
     * Renames defined variables names for a particular node using the UDDU web
     * @param definedVariablesNames
     * @param parentNode
     */
    public void renameDefinedVariables(TIRCommaSeparatedList definedVariablesNames, ASTNode parentNode)
    {
        for (int i = 0; i < definedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = definedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForDefinition(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariable(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    /**
     * Rename a variable using the UDDUWeb variable colors
     * @param variableName
     * @param color
     */
    public void renameVariable(Name variableName, Integer color)
    {
        String updatedVariableName = variableName.getNodeString() + "_" + color;
        variableName.setID(updatedVariableName);
    }
    
    @Override
    public void merge(Object in1, Object in2, Object out)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void copy(Object source, Object dest)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object newInitialFlow()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
