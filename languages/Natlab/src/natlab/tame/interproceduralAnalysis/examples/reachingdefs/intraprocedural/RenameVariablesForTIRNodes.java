package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.ArrayList;
import java.util.HashMap;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.Name;
import ast.NameExpr;

/**
 * Rename variables using the UDDU web
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class RenameVariablesForTIRNodes extends TIRAbstractNodeCaseHandler
{
    // Member variables
    UDDUWeb fUDDUWeb;
    RenameVariablesForASTNodes fRenameVariablesForASTNodes;
    
    // Constructor
    public RenameVariablesForTIRNodes(UDDUWeb udduWeb)
    {
        fUDDUWeb = udduWeb;
        fUDDUWeb.constructUDDUWeb();
        fRenameVariablesForASTNodes = new RenameVariablesForASTNodes(null, fUDDUWeb);
        fRenameVariablesForASTNodes.analyze();
    }
    
    /**
     * Analyze only the visited statements
     */
    public void analyze()
    {
        for (ASTNode node : fUDDUWeb.getVisitedStmtsLinkedList())
        {
            (node).analyze(this);  
        }
    }
    
    @Override
    public void caseASTNode(ASTNode node) 
    {
        int nodeChildrenCount = node.getNumChild();
        for (int i = 0; i < nodeChildrenCount; i++)
        {
            if (node.getChild(i) instanceof TIRNode)
            {
                ((TIRNode) node.getChild(i)).tirAnalyze(this);
            }
            else 
            {
                node.getChild(i).analyze(this);
            }
            
        }
    }

    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        java.util.List<Name> inputParams = new ArrayList<Name>();

        for (Name inputParam : node.getInputParamList())
        {
            inputParams.add(inputParam);
        }
        TIRCommaSeparatedList definedVariablesNames = TIRCommaSeparatedList.createFromNames(inputParams);
        renameDefinedVariables(definedVariablesNames, node);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        Name conditionVariableName = node.getConditionVarName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            if (nodeAndColor != null &&  nodeAndColor.containsKey(node))
            {
                renameVariable(conditionVariableName, nodeAndColor.get(node));
            }
        }
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        Name conditionVariableName = node.getCondition().getName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            if (nodeAndColor != null &&  nodeAndColor.containsKey(node))
            {
                renameVariable(conditionVariableName, nodeAndColor.get(node));
            }
        }
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       // Handle the left hand side 
       TIRCommaSeparatedList definedVariablesNames = node.getTargets();
       if (definedVariablesNames != null) renameDefinedVariables(definedVariablesNames, node);
       
       // Handle the right hand side
       TIRCommaSeparatedList usedVariablesNames = node.getArguments();
       if (usedVariablesNames != null) renameUsedVariables(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = null;
        TIRCommaSeparatedList usedVariablesNames = null;
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            Name target;
            if (node instanceof TIRArraySetStmt)
            {
                target = ((TIRArraySetStmt)node).getArrayName();
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                target = ((TIRCellArraySetStmt)node).getCellArrayName();
            }
            else if (node instanceof TIRDotSetStmt)
            {
                target = ((TIRDotSetStmt)node).getDotName();
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
            definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(target));
        }
        else if (node instanceof TIRAbstractAssignToListStmt)
        {
            definedVariablesNames = ((TIRAbstractAssignToListStmt)node).getTargets();
        } 
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            if (node instanceof TIRCopyStmt)
            {
                TIRCopyStmt copySmtmt = (TIRCopyStmt) node;
                Name usedVarName = copySmtmt.getSourceName();
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(usedVarName));
            }
            definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement " + node);
        }
        if (definedVariablesNames != null) renameDefinedVariables(definedVariablesNames, node);
        if (usedVariablesNames != null) renameUsedVariables(usedVariablesNames, node);
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

}
