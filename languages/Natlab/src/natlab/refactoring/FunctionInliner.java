package natlab.refactoring;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static natlab.refactoring.Exceptions.*;

import natlab.LookupFile;
import natlab.toolkits.Context;
import natlab.toolkits.ContextStack;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.*;
import analysis.*;
import analysis.natlab.*;
import natlab.toolkits.analysis.defassigned.DefinitelyAssignedAnalysis;
import natlab.toolkits.analysis.liveliness.LivelinessAnalysis;
import natlab.toolkits.analysis.test.CopyAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.path.FunctionReference.ReferenceType;
import natlab.toolkits.rewrite.simplification.RightSimplification;
import natlab.toolkits.utils.NodeFinder;
import ast.*;

public class FunctionInliner {
	private CompilationUnits cu;
	public int extra = 0;
	public int removed = 0;

	HashMap<String, Script> scripts = new HashMap<String, Script>();
	final ParsedCompilationUnitsContextStack context;
	public FunctionInliner(CompilationUnits cu) {
		context=new ParsedCompilationUnitsContextStack(new LinkedList<GenericFile>(), cu.getRootFolder(), cu);
		this.cu = cu;
	};
		
	
	private LinkedList<AssignStmt> functionCalls(Function f) {
		final LinkedList<AssignStmt> functionCalls = new LinkedList<AssignStmt>();
		for (AssignStmt stmt: NodeFinder.find(f.getStmtList(), AssignStmt.class)){
			if (stmt.getRHS() instanceof ParameterizedExpr) // we have a call
				functionCalls.add(stmt);
		}
		return functionCalls; 
	}
	
	public int countCalls(){
		int res = 0;
		int resolve_fail =0; 
		
		for (Function f: NodeFinder.find(cu, Function.class)){
			if (! (f.getParent().getParent() instanceof Function)){
				context.push(f);
				VFFlowInsensitiveAnalysis kind_analysis_caller = new VFFlowInsensitiveAnalysis(f);
				kind_analysis_caller.analyze();
				VFFlowset kind = kind_analysis_caller.getFlowSets().get(f);
				for (ParameterizedExpr p: NodeFinder.find(f.getStmts(), ParameterizedExpr.class)){
					if (p.getTarget() instanceof NameExpr){
						NameExpr ne = (NameExpr)p.getTarget();
						if (!kind.getKind(ne.getName().getID()).isVariable()){
							FunctionReference lookupreference = context.peek().resolve(ne.getName().getID());
							
							ASTNode lookupres = context.resolveFunctionReference(lookupreference);
							if (context.peek().getAllOverloads(ne.getName().getID()).size()<2 && lookupres != null && lookupres instanceof Function){
								res++;
							}
							else if (!LookupFile.builtinFunctions.containsKey(ne.getName().getID())){
								resolve_fail++;
//								System.out.print(" " + ne.getName().getID()+ " ,");
							}
						}
					}
				}
			}
		}
//		System.out.print("F: "+ resolve_fail+ " S:");
		return res;
	}

	
	
	public HashMap<AssignStmt, LinkedList<RefactorException>> inlineAll(){
		HashMap<AssignStmt, LinkedList<RefactorException>>  res = new HashMap<AssignStmt, LinkedList<RefactorException>>();
		for (Function f: NodeFinder.find(cu, Function.class)){
			if (! (f.getParent().getParent() instanceof Function))
				res.putAll(inlineAll(f));
		}
		return res;
	}
	
	public HashMap<AssignStmt, LinkedList<RefactorException>> inlineAll(Function f){
		VFFlowInsensitiveAnalysis kind = new VFFlowInsensitiveAnalysis(f);
		kind.analyze();
		RightSimplification rs = new RightSimplification(f, new VFPreorderAnalysis(f));
        rs.transform();
		int l=functionCalls(f).size();
		HashMap<AssignStmt, LinkedList<RefactorException>> errors = new HashMap<AssignStmt, LinkedList<RefactorException>>();
//		System.out.println("Orig: "+l);
		for (int i=0;i<l;i++){
			Function fb = f.fullCopy();
			AssignStmt s = functionCalls(fb).get(i);
			LinkedList<RefactorException> errors_tmp = inline(fb, s);
			errors.put(s, errors_tmp);
//			System.out.println("After round " + i + " " + functionCalls(f).size());
//			if (errors_tmp.isEmpty())
//				System.out.println(fb.getPrettyPrinted());
		}
		
		return errors;
	}

	private Collection<NameExpr> getUses(AssignStmt s, Collection<NameExpr> all, ReachingDefs defAnalysis){
		LinkedList<NameExpr> uses = new LinkedList<NameExpr>();
		for (NameExpr n: all){
			Stmt aUse= NodeFinder.findParent(n, Stmt.class);
			HashMapFlowMap<String, Set<AssignStmt>> output= defAnalysis.getOutFlowSets().get(aUse);
			if (output == null){
				System.out.println("null: "+ NodeFinder.findParent(s, Function.class).getName() + " "+ s.getPrettyPrinted() );
				continue;
			}
			Set<AssignStmt> defs = defAnalysis.getOutFlowSets().get(aUse).get(n);
			if (aUse != s && defs!=null && defs.contains(s))
				uses.add(n);
		}
		return uses;
	}

	private boolean removeExtra(Function f, AssignStmt s){
		String left = ((NameExpr)s.getLHS()).getName().getID();
		String right = ((NameExpr)s.getRHS()).getName().getID();
		System.out.print(" Running analysis l");
		LivelinessAnalysis l = new LivelinessAnalysis(f);
		ReachingDefs defs = new ReachingDefs(f);
		CopyAnalysis copies = new CopyAnalysis(f);
		java.util.List<NameExpr> exprs = NodeFinder.find(f.getStmts(), NameExpr.class);
		if (exprs == null)
			throw new RuntimeException("exprs is null");
		l.analyze();
		System.out.print(" Running analysis def");
		defs.analyze(); 
		System.out.print(" Running analysis copies");
		copies.analyze();
		System.out.println(" done");
		Collection<NameExpr> uses = getUses(s, exprs, defs);
		for (NameExpr use: uses){
			Stmt useStmt = NodeFinder.findParent(use, Stmt.class);
			AssignStmt copy = copies.getOutFlowSets().get(useStmt).get(left);
			if (copy != s)
				return false;
//			if (defs.getOutFlowSets())
		}
		List<Stmt> parent=(List<Stmt>) s.getParent();
		int k=0;
		for(;parent.getChild(k)!=s;k++);
		
		parent.removeChild(k);
		for(NameExpr use: uses){
			use.getName().setID(right);
		}
		return true;
	}
	
	public LinkedList<RefactorException> inline(Function f, AssignStmt s){
		LinkedList<RefactorException> errors = new LinkedList<RefactorException>();
		context.push(f);
		VFFlowInsensitiveAnalysis kind_analysis_caller = new VFFlowInsensitiveAnalysis(f);
		kind_analysis_caller.analyze();
		VFFlowset kind_caller_results = kind_analysis_caller.getFlowSets().get(f);
		
		
		ParameterizedExpr rhs = (ParameterizedExpr) s.getRHS();
		if (!(rhs.getChild(0) instanceof NameExpr)){
			errors.add(new TargetNotFoundException(rhs.getTarget().getNameExpressions().iterator().next().getName()));
			return errors;
		}
		NameExpr ne = (NameExpr) rhs.getChild(0);
		if (!kind_caller_results.getKind(ne.getName().getID()).isFunction()){
			errors.add(new TargetNotAFunction(ne.getName(),f , kind_caller_results.contains(ne.getName().getID()).toString()));
			return errors;
		}
		
		FunctionReference lookupreference = context.peek().resolve(ne.getName().getID());
		ASTNode lookupres = context.resolveFunctionReference(lookupreference);
		Function target = null;
		if (context.peek().getAllOverloads(ne.getName().getID()).size()<2 && lookupres != null && lookupres instanceof Function)
			target = (Function)lookupres;
		else{
			errors.add(new TargetNotFoundException(ne.getName()));
			return errors;
		}
		if (target.getNumInputParam()<rhs.getNumArg()){
            int inputs = target.getNumInputParam();
			RefactorException r = new TooManyInputParams(new Name(rhs.getArg(inputs).getPrettyPrinted()), null, 
                                                         target.getInputParam(inputs-1).getID()+
                                                         " "+(rhs.getNumArg()-inputs));
			errors.add(r);
			return errors;
		}
		
		
		ContextStack calleeContext = new ContextStack(new LinkedList(), lookupreference.path.getParent());
		calleeContext.push(target);
		LinkedList<AssignStmt> toRemove = new LinkedList<AssignStmt>();
		
		VFFlowInsensitiveAnalysis kind_analysis_callee = new VFFlowInsensitiveAnalysis(target);
		kind_analysis_callee.analyze();
		
		List list=(List) s.getParent();
		int k=0;
		for (;list.getChild(k)!=s;k++);
		    //just go forward in list
		list.removeChild(k);
		LinkedList<Stmt> nlist = new LinkedList<Stmt>();
		
		for (int i=0;i<rhs.getNumArg();i++){
			AssignStmt newStmt = new AssignStmt(new NameExpr(target.getInputParam(i)), rhs.getArg(i));
			toRemove.add(newStmt);
			nlist.add(newStmt);
		}
		for(int j=0;j<target.getStmtList().getNumChild();j++){
			nlist.add(target.getStmtList().getChild(j));
		}
		if (s.getLHS() instanceof MatrixExpr){
			MatrixExpr me = (MatrixExpr) s.getLHS();
			int provided = me.getRow(0).getNumElement(); 
			if (provided > target.getNumOutputParam()){
                if (target.getOutputParam(target.getNumOutputParam()-1).getID().equals("varargout")){
                    errors.add(new TooManyOutputParams(new Name(ne.getName().getID()), null, ""));
                    return errors;
                }
				RefactorException r = new RefactorException() {};
				r.f = null;
				r.message= "too many out params";
				r.sym = new Name(ne.getName().getID());
				errors.add(r);
				return errors;
			}
			for (int i=0; i< provided;i++){
				AssignStmt newStmt = new AssignStmt((Expr)(me.getRow(0).getElement(i).copy()), new NameExpr(target.getOutputParam(i)));
				toRemove.add(newStmt);
				nlist.add(newStmt);
			}
		}else if (target.getNumOutputParam()>0){
			AssignStmt newStmt = new AssignStmt((Expr)(s.getLHS().copy()), new NameExpr(target.getOutputParam(0)));
			toRemove.add(newStmt);
			nlist.add(newStmt);
		}
			
		Set<Stmt> nSet = new HashSet<Stmt>(nlist);
		for(int j=nlist.size()-1;j>=0;j--){
			list.insertChild(nlist.get(j),k);
		}
		java.util.List<NameExpr> symbols = NodeFinder.find(target.getStmtList(), NameExpr.class);
		VFFlowInsensitiveAnalysis kind_analysis_post = new VFFlowInsensitiveAnalysis(f);
		kind_analysis_post.analyze();
		for (NameExpr nameExpr: symbols){
			Name n = nameExpr.getName();
			String sym = n.getID();
			VFDatum kind_callee;
			if (kind_analysis_callee.getFlowSets().get(n)!=null)
				kind_callee=kind_analysis_callee.getFlowSets().get(n).getKind(sym);
			else{
				kind_callee=kind_analysis_callee.getFlowSets().get(target).getKind(sym);
			}
			VFDatum kind_caller=kind_caller_results.getKind(sym);
			VFDatum kind_post = kind_analysis_post.getFlowSets().get(f).getKind(sym);
			
			if (kind_callee!=VFDatum.UNDEF && kind_caller!=VFDatum.UNDEF && kind_callee.merge(kind_caller)==VFDatum.TOP){
				errors.add(new RenameRequired(new Name(n.getID())));
				continue;
			}
			
			if (kind_callee.isFunction()){
				if (kind_caller.isFunction() || kind_caller==VFDatum.UNDEF) {
					if (calleeContext.peek().resolve(sym)==null)
						System.out.print("");//"NULL: " + sym);
					else if (!calleeContext.peek().resolve(sym).equals(context.peek().resolve(sym))){
						Name newName = new Name(n.getID());
						errors.add(new NameResolutionChangeException(newName));
					    continue;
					}
				}
				else if (kind_caller.isID()){
					errors.add(new IDConflictException(new Name(n.getID())));
					continue;
				}
			}
			if (kind_callee.isVariable() && kind_caller.isVariable()){
				errors.add(new RenameRequired(new Name(n.getID())));
				continue;
			}
			
			if (kind_callee.isID() && kind_caller.isVariable()){
				errors.add(new RenameRequired(new Name(n.getID())));
				continue;
			}
			if (kind_caller.isID() && kind_callee.isVariable()){
				errors.add(new RenameRequired(new Name(n.getID())));
				continue;
			}
			
			if (kind_callee.isID() && kind_post.isFunction())
				errors.add(new IDConflictException(new Name(n.getID())));
		}
		if (errors.isEmpty()){
			extra+=toRemove.size();
			for (AssignStmt assign:toRemove){
				if ( (assign.getLHS() instanceof NameExpr ) && (assign.getRHS() instanceof NameExpr) &&  removeExtra(f, assign) )
					removed ++;
			}
		}
		context.pop();
		return errors;
	}
	
}
