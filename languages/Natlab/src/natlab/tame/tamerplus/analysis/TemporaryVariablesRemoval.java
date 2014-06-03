package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
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
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.Name;
import ast.NameExpr;

import com.google.common.collect.HashBiMap;

@SuppressWarnings("rawtypes")
public class TemporaryVariablesRemoval extends TIRAbstractNodeCaseHandler implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;
    
    private final Integer INVALID_INDEX = -1;

    UDDUWeb fUDDUWeb;
    TIRToMcSAFIRTableBuilder fTIRToMcSAFIRTableBuilder;
    private HashBiMap<TIRNode, ASTNode> fTIRToMcSAFIRTable;
    private HashMap<Expr, Name> fExprToTempVarName;
    private Set<String> fRemainingVariablesNames;
    
    public TemporaryVariablesRemoval(ASTNode<?> tree)
    {
        fExprToTempVarName = new HashMap<>();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUDDUWeb = engine.getUDDUWebAnalysis();
        
        fTIRToMcSAFIRTableBuilder = engine.getTIRToMcSAFTableBuilder();
        fTIRToMcSAFIRTable = fTIRToMcSAFIRTableBuilder.getTIRToMcSAFIRTable();
        
        fRemainingVariablesNames = engine.getDefinedVariablesAnalysis().getDefinedVariablesFullSet();
        
        getFunctionNode().tirAnalyze(this);
        
        if (DEBUG) 
        {
            printTIRToMcSAFIRTable();
            printExprToTempVarNameTable();
            printRemainingVariablesNames();
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
            Map<TIRNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
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
            Map<TIRNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
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
        
        if (usedVariableName != null && isTemporaryVariable(usedVariableName))
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
        Queue<ASTNode> nodeQueue = new LinkedList<>();
        Set<ASTNode> markedNodes = new HashSet<>();
        ASTNode currentNode = null;
        
        ASTNode parentNode = null;
        Integer childIndex = INVALID_INDEX;
        
        initializeNodeQueueAndMarkedNodes(fTIRToMcSAFIRTable.get(useNode), nodeQueue, markedNodes);
        
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
                
                boolean isChildIndexAndParentValid = (parentNode != null) && (childIndex.intValue() != INVALID_INDEX.intValue());
                if (isChildIndexAndParentValid)
                {
                    parentNode.setChild(definition, childIndex);
                    fExprToTempVarName.put(definition, variable);
                    updateRemainingVariablesNamesSet(variable.getID());
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
        AssignStmt updatedDefinitionNodeInAST = (AssignStmt) fTIRToMcSAFIRTable.get(originalDefinitionNodeInTIR);
        Expr definitionExpr = updatedDefinitionNodeInAST.getRHS();
        return definitionExpr;
    }
    
    private Integer getColorForVariableInUseNode(Name variable, TIRNode useNode)
    {
        String variableName = variable.getID();
        Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForUse(variableName);
        return nodeToColorMap.get(useNode);
    }
    
    private TIRNode getDefintionNode(Name variable, Integer color)
    {
        String variableName = variable.getID();
        Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForDefinition(variableName);
        return findNodeWithColorInMap(color, nodeToColorMap);
    }
    
    private TIRNode findNodeWithColorInMap(Integer color, Map<TIRNode, Integer> nodeToColorMap)
    {
        TIRNode seekedNode = null;
        for (TIRNode node : nodeToColorMap.keySet())
        {
            if (nodeToColorMap.get(node).intValue() == color.intValue())
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
        return variable.getName().tmpVar;
    }
    
    private boolean isTemporaryVariable(Name variableName)
    {
        return variableName.tmpVar;
    }
    
    private TIRNode getFunctionNode()
    {
        return fUDDUWeb.getVisitedStmtsLinkedList().get(0);
    }
    
    private void updateRemainingVariablesNamesSet(String tmpVariableName)
    {
        if (fRemainingVariablesNames.contains(tmpVariableName)) 
        {
            fRemainingVariablesNames.remove(tmpVariableName);
        }
        /*else
        {
            throw new NoSuchElementException("The variable: " + tmpVariableName + " is not defined.");
        }*/
    }
    
    /**
     * Returns a bi-map of Tame IR to equivalent McSAF IR for analyzed tree and vice versa with temporary variables removed
     * @return bi-map, key: start TIRNode/ASTNode, value: equivalent ASTNode/TIRNode
     */
    public HashBiMap<TIRNode, ASTNode> getTIRToMcSAFIRTable()
    {
        return fTIRToMcSAFIRTable;
    }
    
    /**
     * Returns a map of aggregated expression to original temporary variable
     * @return map - key: aggregated expression, value: replaced temporary variable
     */
    public HashMap<Expr, Name> getExprToTempVarTable()
    {
        return fExprToTempVarName;
    }
    
    /**
     * Returns the set of remaining variables after expression aggregation
     * @return set of remaining variables
     */
    public Set<String> getRemainingVariablesNames()
    {
        return fRemainingVariablesNames;
    }
    
    private void printTIRToMcSAFIRTable()
    {
        System.err.println("Tame IR to McSAFIR without temporaries table content:");
        for (TIRNode key : fTIRToMcSAFIRTable.keySet())
        {
            ASTNode value = fTIRToMcSAFIRTable.get(key);
            printTableEntry(key, value);
        }
    }
    
    private void printExprToTempVarNameTable()
    {
        System.err.println("Expression to temporary variable name table content:");
        for (Entry<Expr, Name> entry : fExprToTempVarName.entrySet())
        {
           System.out.println(entry.getKey().getPrettyPrinted() + " ---> " + NodePrinter.printName(entry.getValue()));
        }
        System.out.println("\n");
    }
    
    private void printRemainingVariablesNames()
    {
        System.err.println("Remaining Variables Set:");
        for (String remainingVariable : fRemainingVariablesNames)
        {
            System.out.print(remainingVariable + ", ");
        }
        System.out.println();
    }
    
    private void printTableEntry(TIRNode key, ASTNode value)
    {
        System.out.println(NodePrinter.printNode(key) + " ---> " + NodePrinter.printNode(value));
    }
    
}
