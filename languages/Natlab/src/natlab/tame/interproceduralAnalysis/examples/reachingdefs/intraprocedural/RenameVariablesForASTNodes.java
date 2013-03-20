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
    UDDUWeb fUDDUWeb;
    
    public RenameVariablesForASTNodes(ASTNode tree, UDDUWeb udduWeb)
    {
        super(tree);
        fUDDUWeb = udduWeb;
    }

    public void analyze()
    {
        for (ASTNode node : fUDDUWeb.getVisitedStmtsLinkedList())
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

        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }

    public void renameUsedVariablesForNode(TIRCommaSeparatedList usedVariablesNames, ASTNode parentNode)
    {
        if (usedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < usedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = usedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariableWithColor(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    public void renameDefinedVariablesForNode(TIRCommaSeparatedList definedVariablesNames, ASTNode parentNode)
    {
        if (definedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < definedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = definedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForDefinition(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariableWithColor(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    public void renameVariableWithColor(Name variableName, Integer color)
    {
        String updatedVariableName = variableName.getNodeString() + "#" + color;
        variableName.setID(updatedVariableName);
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
