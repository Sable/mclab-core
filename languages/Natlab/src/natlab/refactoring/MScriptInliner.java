package natlab.refactoring;

import ast.*;
import ast.List;
import natlab.*;
import java.util.*;

import natlab.refactoring.Exceptions.RefactorException;
import natlab.toolkits.Context;
import natlab.toolkits.ContextStack;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.defassigned.DefinitelyAssignedAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.analysis.*;
import nodecases.*;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FolderHandler;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.path.FunctionReference.ReferenceType;
import natlab.toolkits.utils.NodeFinder;
import static natlab.refactoring.Exceptions.*;

public class MScriptInliner {	
	private CompilationUnits cu;
	HashMap<String, Script> scripts = new HashMap<String, Script>();
	
	public MScriptInliner(CompilationUnits cu) {
		this.cu = cu;
		for (int i = 0; i < cu.getChild(0).getNumChild(); i++) {
			if (cu.getChild(0).getChild(i) instanceof Script) {
				Script s = (Script) cu.getChild(0).getChild(i);
				scripts.put(FolderHandler.getFileName(s.getFile()), s);

			}
		}

		this.cu = cu;
	};
	

	public LinkedList<LinkedList<Exception>> inlineAll(){
		LinkedList<LinkedList<Exception>>  res = new LinkedList<LinkedList<Exception>>();
		for (Function f: NodeFinder.find(cu, Function.class)){
			if (! (f.getParent().getParent() instanceof Function))
				res.addAll(inlineAllScripts(f));
		}
		return res;
	}

	private Set<Stmt> findScripts(Function f) {
		final Set<Stmt> scriptCalls = new LinkedHashSet<Stmt>();
		AbstractNodeCaseHandler n = new AbstractNodeCaseHandler() {
			public void caseASTNode(ASTNode n) {
				if (n instanceof ExprStmt) {
					if (n.getNumChild() == 1
							&& n.getChild(0) instanceof NameExpr) {
						NameExpr child = (NameExpr) n.getChild(0);
						if (scripts.containsKey(child.getName().getID())) {
							scriptCalls.add((Stmt) n);
						}
					}
				}
				for (int i = 0; i < n.getNumChild(); i++) {
					caseASTNode(n.getChild(i));
				}
			}
		};
		n.caseASTNode(f);
		return scriptCalls; 
	}
	
	private Set<String> findNested(Function f) {
		if (f.getParent().getParent() instanceof Function)
			f = (Function) f.getParent().getParent();
		final Set<String> res = new LinkedHashSet();
		AbstractNodeCaseHandler n = new AbstractNodeCaseHandler() {
			public void caseASTNode(ASTNode n) {
				if (n instanceof Function) {
					res.add(((Function)n).getName());
				}
				for (int i = 0; i < n.getNumChild(); i++) {
					caseASTNode(n.getChild(i));
				}
			}
		};
		n.caseASTNode(f);
		return res; 
	}
	
	private Set<Name> findNameExpr(ASTNode f) {
		final Set<Name> res = new LinkedHashSet();
		AbstractNodeCaseHandler n = new AbstractNodeCaseHandler() {
			public void caseASTNode(ASTNode n) {
				if (n instanceof NameExpr) {
					res.add(((NameExpr)n).getName());
				}
				if (n instanceof FunctionHandleExpr) {
					res.add(((FunctionHandleExpr)n).getName());
				}
				for (int i = 0; i < n.getNumChild(); i++) {
					caseASTNode(n.getChild(i));
				}
			}
		};
		n.caseASTNode(f);
		return res; 
	}
	
	
	private Set<String> findSiblings(Function f) {
		ASTNode p = f.getParent();
		while (! (p instanceof FunctionList))
			p=p.getParent();
		FunctionList fl = (FunctionList)p;
		final Set<String> res = new LinkedHashSet();
		for (int i=0; i<fl.getFunctionList().getNumChild();i++){
			res.add(fl.getFunction(i).getName());
		}
		return res; 
	}
	
	public LinkedList<LinkedList<Exception>> inlineAllScripts(Function f) {
		
		
		Set<Stmt> scriptCalls = findScripts(f);
		if(!scriptCalls.isEmpty())
			System.out.println("FOOUNDDDDD "+ scriptCalls.size());
		LinkedList<LinkedList<Exception>> res= new LinkedList<LinkedList<Exception>>();
		for (Stmt s: scriptCalls){
			LinkedList<Exception> exceptionList = new LinkedList<Exception>();
			inlineStmt(f, s, exceptionList);
			res.add(exceptionList);
		}
		return res;
//		System.out.println("Result after inlining"+ f.getPrettyPrinted());
	}

	public void inlineStmt(Function f, Stmt s, java.util.List<Exception> e){
		ParsedCompilationUnitsContextStack context = new ParsedCompilationUnitsContextStack(new LinkedList<GenericFile>(), cu.getRootFolder(), cu); 
		context.push(f);
		VFFlowSensitiveAnalysis kind_analysis_f = new VFFlowSensitiveAnalysis(f);
		kind_analysis_f.analyze();
		ExprStmt callStmt = (ExprStmt) s;
		NameExpr ne = (NameExpr) s.getChild(0);
		FunctionReference lookupreference = context.peek().resolve(ne.getName().getID());
		ASTNode lookupres = context.resolveFunctionReference(lookupreference);
		
		Script target = null;
		if ( lookupres instanceof Script)
			target = (Script) lookupres;
		else{
			e.add(new Exceptions.TargetNotAScript(ne.getName(), f, "Nope:"+ lookupres));
			return;
		}
		VFFlowSensitiveAnalysis kind_analysis_s = new VFFlowSensitiveAnalysis(target);
		kind_analysis_s.analyze();
		ReachingDefs reachingDefs = new ReachingDefs(f);
		reachingDefs.analyze();
		HashMapFlowMap<String, Set<AssignStmt>> assigned=reachingDefs.getOutFlowSets().get(callStmt);
		if(assigned==null )
			System.out.println("NULL");
		Set<String> sibs= findSiblings(f);
		Set<String> nested= findNested(f);
		Set<Name> symbols_s = findNameExpr(target);
		List list=(List) callStmt.getParent();
		int i=0;
		for (;list.getChild(i)!=callStmt;i++);
		    //just go forward in list
		list.removeChild(i);
		for(int j=target.getStmtList().getNumChild()-1;j>=0;j--){
			list.insertChild(target.getStmtList().getChild(j), i);
		}
		VFFlowset kind_f_results = kind_analysis_f.getOutFlowSets().get(f);
		VFFlowSensitiveAnalysis kind_analysis_post = new VFFlowSensitiveAnalysis(f);
		kind_analysis_post.analyze();
		for (Name n: symbols_s){
			String sym = n.getID();
			VFDatum kind_s;
            kind_s=kind_analysis_s.getOutFlowSets().get(target).getKind(sym);
			VFDatum kind_f=kind_f_results.getKind(sym);
			VFDatum kind_post = kind_analysis_post.getOutFlowSets().get(f).getKind(sym);
			if (kind_s!=VFDatum.UNDEF && kind_f!=VFDatum.UNDEF && kind_s.merge(kind_f)==VFDatum.TOP)
				e.add(new Exceptions.RenameRequired(n));
			if (kind_s.isFunction() || kind_s.isID()){
				if (kind_f.isFunction()  || kind_f==VFDatum.UNDEF) {
					if (nested.contains(sym))
						e.add(new Exceptions.NameResolutionChangeException(n));
                }
            }
			if (kind_s.isID() && (kind_f.isVariable()))
				if ((!assigned.containsKey(sym)) || (assigned.get(sym).contains(ReachingDefs.UNDEF)))
					e.add(new Exceptions.UnassignedVariableException(n));	
			if (kind_s.isID() && kind_post.isFunction())
				e.add(new Exceptions.WarnIDToFunException(n));
		}
	}
}