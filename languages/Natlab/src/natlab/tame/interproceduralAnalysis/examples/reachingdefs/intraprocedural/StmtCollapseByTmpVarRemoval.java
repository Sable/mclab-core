package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.rewrite.TempFactory;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.Name;
import ast.NameExpr;
import ast.WhileStmt;

@SuppressWarnings("rawtypes")
public class StmtCollapseByTmpVarRemoval extends TIRAbstractNodeCaseHandler
{

    UDDUWeb fUDDUWeb;
    TIRNodeToRawASTNodeTableBuilder fTameIRToRawASTNodeTableBuilder;
    private HashMap<TIRNode, ASTNode> fTIRToRawASTTable;
    private HashMap<Expr, Name> fExprToTempVarName;
    
    public StmtCollapseByTmpVarRemoval(UDDUWeb udduWeb, TIRNodeToRawASTNodeTableBuilder tameIRNodeToRawASTNodeTableBuilder)
    {
        fUDDUWeb = udduWeb;
        fTameIRToRawASTNodeTableBuilder = tameIRNodeToRawASTNodeTableBuilder;
        fExprToTempVarName = new HashMap<Expr, Name>();
        
        initializeUDDUWeb();
        initializeASTTable();
    }
    
    public void initializeUDDUWeb()
    {
        fUDDUWeb.constructUDDUWeb();
    }
    
    public void initializeASTTable()
    {
        fTameIRToRawASTNodeTableBuilder.build();
        fTIRToRawASTTable = fTameIRToRawASTNodeTableBuilder.getIRToRawASTTable();
    }
    
    public void analyze()
    {
        getFunctionNode().tirAnalyze(this);
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
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       TIRCommaSeparatedList usedVariablesNames = node.getArguments();
       replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        Name conditionVariableName = node.getConditionVarName();
        if (conditionVariableName != null)
        {
            HashMap<TIRNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            boolean isNodeAValidKeyInNodeToColorMap = nodeAndColor != null &&  nodeAndColor.containsKey(node);
            if (isNodeAValidKeyInNodeToColorMap && isTemporaryVariable(conditionVariableName))
            {
                replaceUsedTempVarByDefinition(conditionVariableName, node);
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
            HashMap<TIRNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            boolean isNodeAValidKeyInNodeToColorMap = nodeAndColor != null &&  nodeAndColor.containsKey(node);
            if (isNodeAValidKeyInNodeToColorMap && isTemporaryVariable(conditionVariableName))
            {
                replaceUsedTempVarByDefinition(conditionVariableName, node);
            }
        }
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames = null;
        
        usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getLowerName()));
        if (node.hasIncr())
        {
            usedVariablesNames.add(new NameExpr(node.getIncName()));
        }
        usedVariablesNames.add(new NameExpr(node.getUpperName()));

        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
        caseForStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames = null;
        
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            if (node instanceof TIRArraySetStmt)
            {
                TIRArraySetStmt arraySetStmt = (TIRArraySetStmt) node;
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(arraySetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(arraySetStmt.getArrayName()));
                TIRCommaSeparatedList indices = arraySetStmt.getIndizes();
                addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                TIRCellArraySetStmt cellArraySetStmt = (TIRCellArraySetStmt) node;
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(cellArraySetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(cellArraySetStmt.getCellArrayName()));
                TIRCommaSeparatedList indices = cellArraySetStmt.getIndizes();
                addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
            }
            else if (node instanceof TIRDotSetStmt)
            {
                TIRDotSetStmt dotSetStmt = (TIRDotSetStmt) node;
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotSetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(dotSetStmt.getDotName()));
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement " + node);
        }
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }

    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node)
    {
        Name usedVariableName = null;
        if (node instanceof TIRCopyStmt)
        {
            TIRCopyStmt copySmtmt = (TIRCopyStmt) node;
            usedVariableName = copySmtmt.getSourceName();
        }
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment to var statement " + node);
        }
        if(usedVariableName != null && isTemporaryVariable(usedVariableName))
        {
            replaceUsedTempVarByDefinition(usedVariableName, node);
        }
    }

    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames = null;
        
        if (node instanceof TIRArrayGetStmt)
        {
            TIRArrayGetStmt arrayGetStmt = (TIRArrayGetStmt) node;
            Name arrayName = arrayGetStmt.getArrayName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((arrayName)));
            TIRCommaSeparatedList indices = arrayGetStmt.getIndizes();
            addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        }
        else if (node instanceof TIRDotGetStmt)
        {
            TIRDotGetStmt dotGetStmt = (TIRDotGetStmt) node;
            Name dotName = dotGetStmt.getDotName();
            Name fieldName = dotGetStmt.getFieldName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotName));
            usedVariablesNames.add(new NameExpr(fieldName));
        }
        else if (node instanceof TIRCellArrayGetStmt)
        {
            TIRCellArrayGetStmt cellArrayGetStmt = (TIRCellArrayGetStmt) node;
            Name cellArrayName = cellArrayGetStmt.getCellArrayName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((cellArrayName)));
            TIRCommaSeparatedList indices = cellArrayGetStmt.getIndices();
            addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        }
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }

    private void replaceUsedTempVarsByDefintions(TIRCommaSeparatedList usedVariablesNames, TIRNode useNode)
    {
        if (usedVariablesNames == null)
        {
            return;
        }
        int usedVariablesCount = usedVariablesNames.size();
        for (int i = 0; i < usedVariablesCount; i++)
        {
            NameExpr variableNameExpr = usedVariablesNames.getNameExpresion(i);
            if (variableNameExpr == null)
            {
                continue;
            }
            Name variableName = variableNameExpr.getName();
            if (isTemporaryVariable(usedVariablesNames.getNameExpresion(i)))
            {
                replaceUsedTempVarByDefinition(variableName, useNode);
            }
        }
    }
    
    private void replaceUsedTempVarByDefinition(Name variable, TIRNode useNode)
    {
        Expr definition = getDefinitionForVariableAtNode(variable, useNode);
        
        Queue<ASTNode> nodeQueue = new LinkedList<ASTNode>();
        Set<ASTNode> markedNodes = new HashSet<ASTNode>();
        ASTNode currentNode = null;
        
        ASTNode parentNode = null;
        Integer childIndex = -1;
        
        ASTNode replacementNode = fTIRToRawASTTable.get(useNode);
        
        nodeQueue.add(replacementNode);
        markedNodes.add(replacementNode);
        while (!nodeQueue.isEmpty())
        {
            currentNode = nodeQueue.remove();
            if (currentNode instanceof ForStmt)
            {
                currentNode = ((ForStmt) currentNode).getAssignStmt();
            }
            if (isSeekedNode(currentNode, variable))
            {
                parentNode = currentNode.getParent();
                childIndex = getChildIndexForNode(parentNode, currentNode);
                boolean isChildIndexAndParentValid = (parentNode != null) && (childIndex != -1);
                if (isChildIndexAndParentValid)
                {
                    parentNode.setChild(definition, childIndex);
                    fExprToTempVarName.put(definition, variable);
                    // TODO put this in a function
//                    System.out.println(definition.getStructureString() + " ---> " + variable.getID());
                }
            }
            else
            {
                ASTNode currentNodeAsASTNode = currentNode;
                int childrenCount = currentNodeAsASTNode.getNumChild();
                for (int i = 0; i < childrenCount; i++)
                {
                    ASTNode currentChild = currentNodeAsASTNode.getChild(i);
                    if (!markedNodes.contains(currentChild))
                    {
                        markedNodes.add(currentChild);
                        nodeQueue.add(currentChild);
                    }
                }
            }
        }
    }
    
    
    private int getChildIndexForNode(ASTNode parentNode, ASTNode seekedChild)
    {
        int seekedIndex = -1;
        int childrenCount = parentNode.getNumChild();
        for (int i = 0; i < childrenCount; i++)
        {
            ASTNode currentChild = parentNode.getChild(i);
            if (seekedChild == currentChild)
            {
                seekedIndex = i;
                break;
            }
        }
        return seekedIndex;
    }
    
    private boolean isSeekedNode(ASTNode node, Name variable)
    {
        if (node instanceof Name)
        {
            String variableName = variable.getID();
            String variableNameOfNode = ((Name) node).getID();
            return variableName.equals(variableNameOfNode);
        }
        else if (node instanceof NameExpr)
        {
            String variableName = variable.getID();
            String variableNameOfNode = ((NameExpr) node).getName().getID();
            return variableName.equals(variableNameOfNode);
        }
        else
        {
            return false;
        }
    }
    
    private Expr getDefinitionForVariableAtNode(Name variable, TIRNode useNode)
    {
        Integer colorForUseNode = getColorForVariableInUseNode(variable, useNode);
        TIRNode originalDefinitionNodeInTIR = getDefintionNode(variable, colorForUseNode);
        AssignStmt updatedDefinitionNodeInAST = (AssignStmt) fTIRToRawASTTable.get(originalDefinitionNodeInTIR);
        Expr definitionExpr = updatedDefinitionNodeInAST.getRHS();
        return definitionExpr;
    }
    
    private Integer getColorForVariableInUseNode(Name variable, TIRNode useNode)
    {
        String variableName = variable.getID();
        HashMap<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForUse(variableName);
        return nodeToColorMap.get(useNode);
    }
    
    private TIRNode getDefintionNode(Name variable, Integer color)
    {
        String variableName = variable.getID();
        HashMap<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForDefinition(variableName);
        return findNodeWithColorInMap(color, nodeToColorMap);
    }
    
    private TIRNode findNodeWithColorInMap(Integer color, HashMap<TIRNode, Integer> nodeToColorMap)
    {
        TIRNode seekedNode = null;
        for (TIRNode node : nodeToColorMap.keySet())
        {
            if (nodeToColorMap.get(node) == color)
            {
                seekedNode = node;
                break;
            }
        }
        return seekedNode;
    }
    
    private void addTmpIndicesToUsedVariablesNames(TIRCommaSeparatedList indices, TIRCommaSeparatedList usedVariablesNames)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                usedVariablesNames.add(indexNameExpr);
            }
        }
    }
    
    private boolean isTemporaryVariable(NameExpr variable)
    {
        String temporaryVariablesPrefix = TempFactory.getPrefix();
        String variableName = variable.getName().getID();
        return variableName.startsWith(temporaryVariablesPrefix);
    }
    
    private boolean isTemporaryVariable(Name variableName)
    {
        String temporaryVariablesPrefix = TempFactory.getPrefix();
        return variableName.getID().startsWith(temporaryVariablesPrefix);
    }
    
    private TIRNode getFunctionNode()
    {
        return fUDDUWeb.getVisitedStmtsLinkedList().get(0);
    }
    
    public HashMap<TIRNode, ASTNode> getTIRToRawASTTable()
    {
        return fTIRToRawASTTable;
    }
    
    public void printTable()
    {
        for (TIRNode key : fTIRToRawASTTable.keySet())
        {
            ASTNode value = fTIRToRawASTTable.get(key);
            printTableEntry(key, value);
        }
    }
    
    private String printTIRNode(TIRNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRIfStmt) return ((TIRIfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRWhileStmt) return ((TIRWhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRForStmt) return ((TIRForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    private String printASTNode(ASTNode node)
    {
        if (node instanceof AssignStmt) return ((AssignStmt) node).getStructureString();
        else if (node instanceof Function) return ((Function) node).getStructureString().split("\n")[0];
        else if (node instanceof IfStmt) return ((IfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof WhileStmt) return ((WhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof ForStmt) return ((ForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    private void printTableEntry(TIRNode key, ASTNode value)
    {
       System.out.println(printTIRNode(key) + " ---> " + printASTNode(value));
    }
    
}
