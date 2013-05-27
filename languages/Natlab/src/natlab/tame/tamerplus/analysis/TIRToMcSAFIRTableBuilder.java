package natlab.tame.tamerplus.analysis;


import java.util.LinkedList;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
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
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.List;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.Row;
import ast.Stmt;
import ast.WhileStmt;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;

@SuppressWarnings("rawtypes")
public class TIRToMcSAFIRTableBuilder extends TIRAbstractNodeCaseHandler implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;
    private LinkedList<TIRNode> fVisitedNodes;
    private HashBiMap<TIRNode, ASTNode> fTIRToMcSAFIRTable;
    
    // Case loop var will require a specialized builder and also expander!
    public TIRToMcSAFIRTableBuilder(ASTNode<?> tree) {}
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fVisitedNodes = engine.getReachingDefinitionsAnalysis().getVisitedStmtsOrderedList();
        initializeIRToRawASTTable();
        
        if (DEBUG) System.err.println("Tame IR to McSAF IR Table Builder");
        
        getFunctionNode().tirAnalyze(this);
    }
    
    private void initializeIRToRawASTTable()
    {
        fTIRToMcSAFIRTable = HashBiMap.create();
        for (TIRNode visitedNode : fVisitedNodes)
        {
            if (visitedNode instanceof Function)
            {
                fTIRToMcSAFIRTable.put(visitedNode, new Function());
            }
            else if (visitedNode instanceof Stmt)
            {
                fTIRToMcSAFIRTable.put(visitedNode, new Stmt() {});
            }
            else 
            {
                throw new UnsupportedOperationException("AST node with class " + visitedNode.getClass() + " is not supported!");
            }
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
        try
        {
            TIRFunction functionClone = (TIRFunction) node.clone();
            Function function = new Function();
            
            populateFunctionFieldsFromClone(function, functionClone);
            
            fTIRToMcSAFIRTable.put(node, function);
            
            if (DEBUG) printTableEntry(node, function);
            
            caseASTNode(node);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    private void populateFunctionFieldsFromClone(Function targetFunction, TIRFunction functionClone)
    {
        targetFunction.setComments(functionClone.getComments());
        targetFunction.setName(functionClone.getName());
        targetFunction.setInputParamList(functionClone.getInputParamList());
        targetFunction.setOutputParamList(functionClone.getOutputParamList());
        targetFunction.setNestedFunctionList(functionClone.getNestedFunctionList());
        targetFunction.setStmtList(functionClone.getStmts());
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        try
        {
            TIRForStmt forStmtClone = (TIRForStmt) node.clone();
            ForStmt forStmt = new ForStmt();
            
            forStmt.setAssignStmt(cloneAssignStmtOfTIRForStmt(forStmtClone));
            forStmt.setStmtList(forStmtClone.getStmtList().clone());
            
            fTIRToMcSAFIRTable.put(node, forStmt);
            
            if (DEBUG) printTableEntry(node, forStmt);
            
            caseForStmt(node);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        try
        {
            TIRIfStmt ifStmtClone = (TIRIfStmt) node.clone();
            IfStmt ifStmt = new IfStmt();
            
            ifStmt.setIfBlockList(ifStmtClone.getIfBlockList());
            ifStmt.setElseBlock(ifStmtClone.getElseBlock());
            
            fTIRToMcSAFIRTable.put(node, ifStmt);
            
            if (DEBUG) printTableEntry(node, ifStmt);
            
            caseIfStmt(node);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        try
        {
            TIRWhileStmt whileStmtClone = (TIRWhileStmt) node.clone();
            WhileStmt whileStmt = new WhileStmt();
            
            whileStmt.setExpr(whileStmtClone.getCondition());
            whileStmt.setStmtList(whileStmtClone.getStmtList());
            
            fTIRToMcSAFIRTable.put(node, whileStmt);
            
            if (DEBUG) printTableEntry(node, whileStmt);
            
            caseWhileStmt(node);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
        try
        {
            TIRCallStmt callStmtClone = (TIRCallStmt) node.clone();
            AssignStmt callStmt = new AssignStmt();
            
            ParameterizedExpr rhs = new ParameterizedExpr();
            rhs.setTarget(new NameExpr(callStmtClone.getFunctionName()));
            addIndicesToParametrizedExpr((TIRCommaSeparatedList) callStmtClone.getArguments(), rhs);
            MatrixExpr lhs = new MatrixExpr();
            addTargetsToMatrixExpr((TIRCommaSeparatedList) callStmtClone.getTargets(), lhs);
            callStmt.setLHS(lhs);
            callStmt.setRHS(rhs);
            
            fTIRToMcSAFIRTable.put(node, callStmt);
            
            if (DEBUG) printTableEntry(node, callStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRCopyStmt copyStmtClone = (TIRCopyStmt) node.clone();
            
            assignStmt.setLHS(copyStmtClone.getLHS());
            assignStmt.setRHS(copyStmtClone.getRHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        }  catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRAssignLiteralStmt assignLiteralStmtClone = (TIRAssignLiteralStmt) node.clone();
            
            assignStmt.setLHS(assignLiteralStmtClone.getLHS());
            assignStmt.setRHS(assignLiteralStmtClone.getRHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        }  catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRArraySetStmt arraySetStmtClone = (TIRArraySetStmt) node.clone();
            
            ParameterizedExpr lhs = new ParameterizedExpr();
            NameExpr arrayName = new NameExpr(arraySetStmtClone.getArrayName());
            lhs.setTarget(arrayName);
            addIndicesToParametrizedExpr(arraySetStmtClone.getIndizes(), lhs);
            assignStmt.setLHS(lhs);
            assignStmt.setRHS(arraySetStmtClone.getRHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRCellArraySetStmt cellArraySetStmtClone = (TIRCellArraySetStmt) node.clone();
            
            CellIndexExpr lhs = new CellIndexExpr();
            NameExpr cellArrayName = new NameExpr(cellArraySetStmtClone.getCellArrayName());
            lhs.setTarget(cellArrayName);
            addIndicesToCellIndexExpr(cellArraySetStmtClone.getIndizes(), lhs);
            assignStmt.setLHS(lhs);
            assignStmt.setRHS(cellArraySetStmtClone.getRHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRDotSetStmt dotSetStmtClone = (TIRDotSetStmt) node.clone();
            
            DotExpr lhs = new DotExpr();
            NameExpr dotName = new NameExpr(dotSetStmtClone.getDotName());
            lhs.setTarget(dotName);
            lhs.setField(dotSetStmtClone.getFieldName());
            assignStmt.setLHS(lhs);
            assignStmt.setRHS(dotSetStmtClone.getRHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRCellArrayGetStmt cellArrayGetStmtClone = (TIRCellArrayGetStmt) node.clone();
            
            CellIndexExpr rhs = new CellIndexExpr();
            NameExpr cellArrayName = new NameExpr(cellArrayGetStmtClone.getCellArrayName());
            rhs.setTarget(cellArrayName);
            addIndicesToCellIndexExpr(cellArrayGetStmtClone.getIndices(), rhs);
            assignStmt.setRHS(rhs);
            assignStmt.setLHS(cellArrayGetStmtClone.getLHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRDotGetStmt dotGetStmtClone = (TIRDotGetStmt) node.clone();
            
            DotExpr rhs = new DotExpr();
            NameExpr dotName = new NameExpr(dotGetStmtClone.getDotName());
            rhs.setTarget(dotName);
            rhs.setField(dotGetStmtClone.getFieldName());
            assignStmt.setRHS(rhs);
            assignStmt.setLHS(dotGetStmtClone.getLHS());
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            TIRArrayGetStmt arrayGetStmtClone = (TIRArrayGetStmt) node.clone();
            
            ParameterizedExpr rhs = new ParameterizedExpr();
            NameExpr arrayName = new NameExpr(arrayGetStmtClone.getArrayName());
            rhs.setTarget(arrayName);
            addIndicesToParametrizedExpr(arrayGetStmtClone.getIndizes(), rhs);
            assignStmt.setLHS(arrayGetStmtClone.getLHS());
            assignStmt.setRHS(rhs);
            
            fTIRToMcSAFIRTable.put(node, assignStmt);
            
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    public AssignStmt cloneAssignStmtOfTIRForStmt(TIRForStmt node)
    {

        Expr lhsOfAssignStmt = node.getAssignStmt().getLHS();
        
        RangeExpr forStmtRangeExpr = new RangeExpr();
        RangeExpr cloneOfTIRForStmtRangeExpr = (RangeExpr) node.getAssignStmt().getRHS();
        try
        {
            Expr low = cloneOfTIRForStmtRangeExpr.getLower().clone();
            Expr incr = cloneOfTIRForStmtRangeExpr.hasIncr() ? cloneOfTIRForStmtRangeExpr.getIncr().clone() : null;
            Expr up = cloneOfTIRForStmtRangeExpr.getUpper().clone();
            
            forStmtRangeExpr.setLower(low);
            if (cloneOfTIRForStmtRangeExpr.hasIncr()) forStmtRangeExpr.setIncr(incr);
            forStmtRangeExpr.setUpper(up);
        } catch (CloneNotSupportedException e)
        {
            throw Throwables.propagate(e);
        }
        return new AssignStmt(lhsOfAssignStmt, forStmtRangeExpr);
    }
    
    public void addIndicesToParametrizedExpr(TIRCommaSeparatedList indices, ParameterizedExpr parameterizedExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            try
            {
                parameterizedExpr.setArg(indices.getChild(i).clone(), i);
            } catch (CloneNotSupportedException e)
            {
                throw Throwables.propagate(e);
            }
        }
    }
    
    public void addIndicesToCellIndexExpr(TIRCommaSeparatedList indices, CellIndexExpr cellIndexExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            NameExpr indexNameExpr;
            try
            {
                indexNameExpr = indices.getNameExpresion(i).clone();
                if (indexNameExpr != null)
                {
                    cellIndexExpr.setArg(indexNameExpr, i);
                }
            } catch (CloneNotSupportedException e)
            {
                throw Throwables.propagate(e);
            }
        }
    }
    
    public void addTargetsToMatrixExpr(TIRCommaSeparatedList targets, MatrixExpr matrixExpr)
    {
        int targetsCount = targets.size();
        Row row = new Row();
        List<ASTNode> returnValues = new List<ASTNode>();
        for(int i = 0; i < targetsCount; i++)
        {
            NameExpr targetNameExpr;
            try
            {
                targetNameExpr = targets.getNameExpresion(i).clone();
                if (targetNameExpr != null)
                {
                    returnValues.add(targetNameExpr);
                }
            } catch (CloneNotSupportedException e)
            {
                throw Throwables.propagate(e);
            }
        }
       row.setChild(returnValues, 0);
       matrixExpr.setRow(row, 0);
    }
    
    public HashBiMap<TIRNode,  ASTNode> getTIRToMcSAFIRTable()
    {
        return fTIRToMcSAFIRTable;
    }
    
    private TIRNode getFunctionNode()
    {
        return fVisitedNodes.get(0);
    }
    
    private void printTableEntry(TIRNode key, ASTNode value)
    {
       System.out.println(NodePrinter.printNode(key) + " ---> " + NodePrinter.printNode(value));
    }
}
