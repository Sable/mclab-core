package natlab.Static.ir;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import ast.*;

public class IRStatementList extends List<Stmt> implements IRNode {
    public IRStatementList(List<Stmt> list){
        super();
        for (Stmt s : list){
            add(s);
        }
    }

    public List<Stmt> add(IRStmt stmt){
        return super.add((Stmt)stmt);
    }
    
    
    @Override
    public List<Stmt> add(Stmt node) {
        /*
        if (!(node instanceof IRStmt)){
            throw new UnsupportedOperationException("attempting to put non IR stmt "
                    +(node.getClass().getName())+" in a IRStatementList");
        }*/
        return super.add(node);
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRStatementList(this);
    }

}
