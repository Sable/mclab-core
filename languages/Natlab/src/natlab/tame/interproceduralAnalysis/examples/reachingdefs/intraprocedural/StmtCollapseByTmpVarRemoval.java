package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

@SuppressWarnings("rawtypes")
public class StmtCollapseByTmpVarRemoval extends TIRAbstractNodeCaseHandler
{

    UDDUWeb fUDDUWeb;
    IRNodeToRawASTNodeTableBuilder fTameIRToRawASTNodeTableBuilder;
    private HashMap<ASTNode, ASTNode> fTIRToRawASTTable;
    
    public StmtCollapseByTmpVarRemoval(UDDUWeb udduWeb, IRNodeToRawASTNodeTableBuilder tameIRNodeToRawASTNodeTableBuilder)
    {
        fUDDUWeb = udduWeb;
        fTameIRToRawASTNodeTableBuilder = tameIRNodeToRawASTNodeTableBuilder;
        
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
        for (ASTNode node : fUDDUWeb.getVisitedStmtsLinkedList())
        {
            // TODO I should not have to make this call explicit!!!
            if (node instanceof TIRFunction)
            {
                caseTIRFunction((TIRFunction) node);
            }
            (node).analyze(this);  
        }
//        fUDDUWeb.getVisitedStmtsLinkedList().get(0).analyze(this);
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
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        Name conditionVariableName = node.getConditionVarName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            boolean isNodeAValidKeyInNodeToColorMap = nodeAndColor != null &&  nodeAndColor.containsKey(node);
            if (isNodeAValidKeyInNodeToColorMap && isTemporaryVariable(conditionVariableName))
            {
                replaceUsedTmpVarByDefinitionForIfStmt(conditionVariableName, node);
            }
        }
        caseIfStmt(node);
    }
    
    private void replaceUsedTmpVarByDefinitionForIfStmt
    (
            Name conditionVariableName,
            TIRIfStmt node
    )
    {
        
    }

    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        Name conditionVariableName = node.getCondition().getName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            boolean isNodeAValidKeyInNodeToColorMap = nodeAndColor != null &&  nodeAndColor.containsKey(node);
            if (isNodeAValidKeyInNodeToColorMap && isTemporaryVariable(conditionVariableName))
            {
                replaceUsedTmpVarByDefinitionForWhileStmt(conditionVariableName, node);
            }
        }
        caseWhileStmt(node);
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
        
        filterNonTemporaryVariables(usedVariablesNames);
        replaceUsedTmpVarsByDefinitionForTIRAbstractAssignStmt(usedVariablesNames, node);
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
        else 
        {
            throw new UnsupportedOperationException("unknown assignment to var statement " + node);
        }
        if(usedVariableName != null && isTemporaryVariable(usedVariableName))
        {
            replaceUsedTmpVarByDefinitionForTIRAbstractAssignToVarStmt(usedVariableName, node);
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
        
        filterNonTemporaryVariables(usedVariablesNames);
        replaceUsedTmpVarsByDefinitionForTIRAbstractAssignToListStmt(usedVariablesNames, node);
    }
    
    private void replaceUsedTmpVarsByDefinitionForTIRAbstractAssignStmt(TIRCommaSeparatedList usedVariablesNames, TIRAbstractAssignFromVarStmt node)
    {
        
    }
    
    private void replaceUsedTmpVarsByDefinitionForTIRAbstractAssignToListStmt(TIRCommaSeparatedList usedVariablesNames, TIRAbstractAssignToListStmt node)
    {
        
    }
    
    private void replaceUsedTmpVarByDefinitionForTIRAbstractAssignToVarStmt(Name usedVariableName, TIRAbstractAssignToVarStmt node)
    {
        
    }
    
    private void replaceUsedTmpVarByDefinitionForWhileStmt(Name conditionVariableName, TIRWhileStmt node)
    {
        
    }

    
    private void filterNonTemporaryVariables(TIRCommaSeparatedList usedVariablesNames)
    {
        int usedVariablesCount = usedVariablesNames.size();
        for (int i = 0; i < usedVariablesCount; i++)
        {
            NameExpr usedVariableName = usedVariablesNames.getNameExpresion(i);
            if (!isTemporaryVariable(usedVariableName))
            {
                usedVariablesNames.removeChild(i);
            }
        }
    }
    
    public void addTmpIndicesToUsedVariablesNames(TIRCommaSeparatedList indices, TIRCommaSeparatedList usedVariablesNames)
    {
        for (int i = 0; i < indices.size(); i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                usedVariablesNames.add(indexNameExpr);
            }
        }
    }
    
    private boolean isTemporaryVariable(NameExpr variableName)
    {
        return variableName.tmpVar;
    }
    
    private boolean isTemporaryVariable(Name variableName)
    {
        return variableName.tmpVar;
    }
    
}
