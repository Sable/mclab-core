package natlab.refactoring;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowSensitiveAnalysis;
import natlab.toolkits.analysis.varorfun.VFFlowset;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FolderHandler;
import natlab.toolkits.path.FunctionReference;
import natlab.utils.AbstractNodeFunction;
import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CompilationUnits;
import ast.ExprStmt;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MScriptInliner {	
	private CompilationUnits cu;
	Map<String, Script> scripts = Maps.newHashMap();
	
	public MScriptInliner(CompilationUnits cu) {
		this.cu = cu;
		for (int i = 0; i < cu.getChild(0).getNumChild(); i++) {
			if (cu.getChild(0).getChild(i) instanceof Script) {
				Script s = (Script) cu.getChild(0).getChild(i);
				scripts.put(FolderHandler.getFileName(s.getFile()), s);
			}
		}
	};

	public LinkedList<LinkedList<Exception>> inlineAll(){
		LinkedList<LinkedList<Exception>>  res = Lists.newLinkedList();
		for (Function f: NodeFinder.of(Function.class).findIn(cu)) {
			if (! (f.getParent().getParent() instanceof Function))
				res.addAll(inlineAllScripts(f));
		}
		return res;
	}

	private Set<Stmt> findScripts(Function f) {
		final Set<Stmt> scriptCalls = Sets.newLinkedHashSet();
		NodeFinder.of(ExprStmt.class).applyIn(f, new AbstractNodeFunction<ExprStmt>() {
		  @Override public void apply(ExprStmt n) {
	      if (n.getNumChild() == 1 && n.getChild(0) instanceof NameExpr) {
          NameExpr child = (NameExpr) n.getChild(0);
          if (scripts.containsKey(child.getName().getID())) {
            scriptCalls.add((Stmt) n);
          }
        }
		  }
		});
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
	  FunctionList fl = NodeFinder.findParent(f, FunctionList.class);
		final Set<String> res = Sets.newLinkedHashSet();
		for (Function function : fl.getFunctions()) {
		  res.add(function.getName());
		}
		return res;
	}
	
	public LinkedList<LinkedList<Exception>> inlineAllScripts(Function f) {
		Set<Stmt> scriptCalls = findScripts(f);
		LinkedList<LinkedList<Exception>> res = Lists.newLinkedList();
		for (Stmt s: scriptCalls){
            res.add(inlineStmt(s));
		}
		return res;
	}

	public java.util.LinkedList<Exception> inlineStmt(Stmt s) {
        LinkedList<Exception> e = new LinkedList<Exception>();
        Function f = NodeFinder.findParent(s, Function.class);
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
			return e;
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
        return e;
	}
}