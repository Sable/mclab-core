package natlab.refactoring;

import java.util.*;
import natlab.toolkits.analysis.*;
import natlab.refactoring.Exceptions.*;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.analysis.liveliness.LivelinessAnalysis;
import natlab.toolkits.utils.NodeFinder;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Function;
import ast.Name;
import ast.AssignStmt;
import ast.GlobalStmt;
import ast.Stmt;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;
import natlab.toolkits.analysis.test.ReachingDefs;

public class ExtractFunction {
	ParsedCompilationUnitsContextStack context;
	public ExtractFunction(CompilationUnits cu){
		context = new ParsedCompilationUnitsContextStack(new LinkedList<GenericFile>(), cu.getRootFolder(), cu);
	}
	
    public List<RefactorException> extract(ast.List<Stmt> stmtList, int offset_start, int offset_end, Function f){
        Function orig = NodeFinder.findParent(stmtList, Function.class);
        ast.List<Stmt> newStmtList = new ast.List<Stmt>();
        VFFlowInsensitiveAnalysis kindAnalysis = new VFFlowInsensitiveAnalysis(orig);

        kindAnalysis.analyze();

        for (int i=offset_start; i<offset_end; i++)
            newStmtList.addChild(stmtList.getChild(i));

        f.setStmtList(newStmtList);

        
        ReachingDefs reachingAnalysisOrig = new ReachingDefs(orig);
        reachingAnalysisOrig.analyze();
        ReachingDefs reachingAnalysisNew = new ReachingDefs(f);
        reachingAnalysisNew.analyze();

        LivelinessAnalysis liveAnalysisOrig = new LivelinessAnalysis(orig);
        liveAnalysisOrig.analyze();

        LivelinessAnalysis liveAnalysisNew = new LivelinessAnalysis(f);
        liveAnalysisNew.analyze();

        Stmt startStmt = stmtList.getChild(offset_start), endStmt = stmtList.getChild(offset_end-1);

        Map<String, Set<AssignStmt>> reachingBefore = reachingAnalysisOrig.getOutFlowSets().get(startStmt).toMap();
        Map<String, Set<AssignStmt>> reachingAfter = reachingAnalysisNew.getOutFlowSets().get(f).toMap();
        Set<String> liveBefore = liveAnalysisNew.getInFlowSets().get(f).getSet();
        Set<String> liveAfter = liveAnalysisOrig.getOutFlowSets().get(endStmt).getSet();
        VFFlowset kinds = kindAnalysis.getFlowSets().get(orig);
        System.out.println(liveBefore);
        System.out.println(liveAfter);
        System.out.println(reachingBefore);
        System.out.println(reachingAfter);
        LinkedList<RefactorException> errors = new LinkedList<RefactorException>();
        Set<String> addedGlobals = new HashSet<String>();
        //inputs 
        Set<String> inputs = new HashSet<String>();
        for (String id: liveBefore){
            if (kinds.getKind(id).isVariable()){
                if (reachingBefore.get(id).contains(ReachingDefs.GLOBAL)){
                    if (!addedGlobals.contains(id))
                        newStmtList.insertChild(new GlobalStmt(new ast.List<Name>().add(new Name(id))),0);
                } else if ((!reachingBefore.get(id).contains(ReachingDefs.UNDEF)) && 
                    (reachingBefore.size()>0)){
                    f.addInputParam(new Name(id));
                    inputs.add(id);
                } else {
                    errors.add(new Exceptions.FunctionInputCanBeUndefined(new Name(id)));
                }
            }
        }

        for (String id: liveAfter){
            if (kinds.getKind(id).isVariable() && reachingAfter.containsKey(id)) {
                int reachings = reachingAfter.get(id).size();
                if (reachingAfter.get(id).contains(ReachingDefs.GLOBAL)) {
                    if (!addedGlobals.contains(id))
                        newStmtList.insertChild(new GlobalStmt(new ast.List<Name>().add(new Name(id))),0);
                } else if (reachingAfter.get(id).contains(ReachingDefs.UNDEF)) {
                    f.addOutputParam(new Name(id));
                    if (!inputs.contains(id)) 
                        f.addInputParam(new Name(id));
                    errors.add(new Exceptions.FunctionOutputCanBeUndefined(new Name(id)));
                } else {
                    f.addOutputParam(new Name(id));
                }
            }
        }
        return errors;
    }
}