package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

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
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.rewrite.TempFactory;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.Name;
import ast.NameExpr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("rawtypes")
public class StmtCollapseByTmpVarRemoval extends TIRAbstractNodeCaseHandler implements TamerPlusAnalysis
{

    UDDUWeb fUDDUWeb;
    TIRNodeToRawASTNodeTableBuilder fTameIRToRawASTNodeTableBuilder;
    private HashMap<TIRNode, ASTNode> fTIRToRawASTTable;
    private HashMap<Expr, Name> fExprToTempVarName;
    private final Integer INVALID_INDEX = -1;
    
    public StmtCollapseByTmpVarRemoval(ASTNode<?> tree)
    {
        fExprToTempVarName = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUDDUWeb = engine.getUDDUWebAnalysis();
        
        fTameIRToRawASTNodeTableBuilder = engine.getTIRNodeToRawASTNodeTableBuilder();
        fTIRToRawASTTable = fTameIRToRawASTNodeTableBuilder.getIRToRawASTTable();
        
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
            boolean isNodeAValidKeyInNodeToColorMap = (nodeAndColor != null &&  nodeAndColor.containsKey(node));
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
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getDotName()));
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames  = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getCellArrayName()));
        TIRCommaSeparatedList indices = node.getIndizes();
        addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getArrayName()));
        TIRCommaSeparatedList indices = node.getIndizes();
        addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node)
    {
        TIRCopyStmt copySmtmt = (TIRCopyStmt) node;
        Name usedVariableName = copySmtmt.getSourceName();
        
        if(usedVariableName != null && isTemporaryVariable(usedVariableName))
        {
            replaceUsedTempVarByDefinition(usedVariableName, node);
        }
    }
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node)
    {
        Name cellArrayName = node.getCellArrayName();
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((cellArrayName)));
        TIRCommaSeparatedList indices = node.getIndices();
        addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node)
    {
        Name dotName = node.getDotName();
        Name fieldName = node.getFieldName();
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotName));
        usedVariablesNames.add(new NameExpr(fieldName));
        
        replaceUsedTempVarsByDefintions(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node)
    {
        Name arrayName = node.getArrayName();
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((arrayName)));
        TIRCommaSeparatedList indices = node.getIndizes();
        addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
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
        
        Queue<ASTNode> nodeQueue = Lists.newLinkedList();
        Set<ASTNode> markedNodes = Sets.newHashSet();
        ASTNode currentNode = null;
        
        ASTNode parentNode = null;
        Integer childIndex = INVALID_INDEX;
        
        initializeNodeQueueAndMarkedNodes(fTIRToRawASTTable.get(useNode), nodeQueue, markedNodes);
        
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
                
                boolean isChildIndexAndParentValid = (parentNode != null) && (childIndex != INVALID_INDEX);
                if (isChildIndexAndParentValid)
                {
                    parentNode.setChild(definition, childIndex);
                    fExprToTempVarName.put(definition, variable);
                }
            }
            else
            {
                addChildrenOfNodeToQueueAndMarkedSet(currentNode, nodeQueue, markedNodes);
            }
            
        }
    }
    
    private void initializeNodeQueueAndMarkedNodes(ASTNode replacementNode, Queue<ASTNode> nodeQueue, Set<ASTNode> markedNodes)
    {
        nodeQueue.add(replacementNode);
        markedNodes.add(replacementNode);
    }
    
    private void addChildrenOfNodeToQueueAndMarkedSet(ASTNode currentNode, Queue<ASTNode> nodeQueue, Set<ASTNode> markedNodes)
    {
        int childrenCount = currentNode.getNumChild();
        for (int i = 0; i < childrenCount; i++)
        {
            ASTNode currentChild = currentNode.getChild(i);
            if (!markedNodes.contains(currentChild))
            {
                markedNodes.add(currentChild);
                nodeQueue.add(currentChild);
            }
        }
    }
    
    private int getChildIndexForNode(ASTNode parentNode, ASTNode seekedChild)
    {
        int seekedIndex = INVALID_INDEX;
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
    
    private void printTableEntry(TIRNode key, ASTNode value)
    {
        System.out.println(NodePrinter.printNode(key) + " ---> " + NodePrinter.printASTNode(value));
    }
    
}
