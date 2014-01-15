package natlab.refactoring;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.refactoring.Exceptions.IDConflictException;
import natlab.refactoring.Exceptions.NameResolutionChangeException;
import natlab.refactoring.Exceptions.RefactorException;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.refactoring.Exceptions.TargetNotAFunction;
import natlab.refactoring.Exceptions.TargetNotFoundException;
import natlab.refactoring.Exceptions.TooManyInputParams;
import natlab.refactoring.Exceptions.TooManyOutputParams;
import natlab.toolkits.ContextStack;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.core.CopyAnalysis;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.rewrite.simplification.RightSimplification;
import natlab.utils.AstPredicates;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CompilationUnits;
import ast.Expr;
import ast.Function;
import ast.List;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FunctionInliner {
	private CompilationUnits cu;
	public int extra = 0;
	public int removed = 0;

	HashMap<String, Script> scripts = Maps.newHashMap();
	final ParsedCompilationUnitsContextStack context;
	public FunctionInliner(CompilationUnits cu) {
		context=new ParsedCompilationUnitsContextStack(Lists.<GenericFile>newLinkedList(),
		    cu.getRootFolder(), cu);
		this.cu = cu;
	};
	
	private LinkedList<AssignStmt> functionCalls(Function f) {
	  return Lists.newLinkedList(NodeFinder.find(AssignStmt.class, f.getStmtList())
	      .filter(new Predicate<AssignStmt>() {
	        @Override public boolean apply(AssignStmt stmt) {
	          return stmt.getRHS() instanceof ParameterizedExpr;
	        }
	      }));
	}
	
	private Iterable<Function> nonNestedFunctions(ASTNode<?> tree) {
	  return NodeFinder.find(Function.class, tree).filter(
	      Predicates.not(AstPredicates.nestedFunction()));
	}
	
	public int countCalls(){
		int res = 0;
		
		for (Function f : nonNestedFunctions(cu)) {
		    context.push(f);
		    VFFlowInsensitiveAnalysis kind_analysis_caller = new VFFlowInsensitiveAnalysis(f);
		    kind_analysis_caller.analyze();
		    Map<String, VFDatum> kind = kind_analysis_caller.getFlowSets().get(f);
		    for (ParameterizedExpr p: NodeFinder.find(ParameterizedExpr.class, f.getStmts())){
		      if (p.getTarget() instanceof NameExpr){
		        String name = ((NameExpr)p.getTarget()).getName().getID();
		        VFDatum nameKind = kind.containsKey(name) ? kind.get(name) : VFDatum.UNDEF;
		        if (nameKind.isVariable()) {
		          FunctionReference lookupreference = context.peek().resolve(name);

		          ASTNode lookup = context.resolveFunctionReference(lookupreference);
		          if (context.peek().getAllOverloads(name).size()<2 && lookup instanceof Function){
		            res++;
		          }
		        }
		      }
		    }
		}
		return res;
	}

	public HashMap<AssignStmt, LinkedList<RefactorException>> inlineAll(){
		HashMap<AssignStmt, LinkedList<RefactorException>>  res = Maps.newHashMap();
		for (Function f: nonNestedFunctions(cu)) {
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
		HashMap<AssignStmt, LinkedList<RefactorException>> errors = Maps.newHashMap();
		for (int i=0;i<l;i++){
			AssignStmt s = functionCalls(f).get(i);
            errors.put(s, inline(s));
		}
		return errors;
	}

	private Collection<NameExpr> getUses(AssignStmt s, Collection<NameExpr> all, ReachingDefs defAnalysis){
		LinkedList<NameExpr> uses = Lists.newLinkedList();
		for (NameExpr n: all){
			Stmt aUse= NodeFinder.findParent(Stmt.class, n);
			Map<String, Set<Def>> output= defAnalysis.getOutFlowSets().get(aUse);
			if (output == null){
                continue;
			}
 			Set<Def> defs = defAnalysis.getOutFlowSets().get(aUse).get(n.getName().getID());
			if (aUse != s && defs!=null && defs.contains(s))
				uses.add(n);
		}
		return uses;
	}

	private boolean removeExtra(Function f, AssignStmt s){
		String left = ((NameExpr)s.getLHS()).getName().getID();
		String right = ((NameExpr)s.getRHS()).getName().getID();
		LivenessAnalysis l = new LivenessAnalysis(f);
		ReachingDefs defs = new ReachingDefs(f);
		CopyAnalysis copies = new CopyAnalysis(f);
		java.util.List<NameExpr> exprs = 
		        Lists.newArrayList(NodeFinder.find(NameExpr.class, f.getStmts()));
		if (exprs == null)
			throw new RuntimeException("exprs is null");
		l.analyze();
		defs.analyze(); 
		copies.analyze();
		Collection<NameExpr> uses = getUses(s, exprs, defs);
		for (NameExpr use: uses){
			Stmt useStmt = NodeFinder.findParent(Stmt.class, use);
            if (! copies.getOutFlowSets().get(useStmt).containsKey(left)) 
                return false;
			AssignStmt copy = copies.getOutFlowSets().get(useStmt).get(left);
			if (copy != s  || copy == null)
				return false;
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
	
	public LinkedList<RefactorException> inline(AssignStmt s){
		LinkedList<RefactorException> errors = Lists.newLinkedList();
        Function f = NodeFinder.findParent(Function.class, s);
		context.push(f);
		VFFlowInsensitiveAnalysis kind_analysis_caller = 
            new VFFlowInsensitiveAnalysis(f, 
                                          context.peek().getFunctionOrScriptQuery());
		kind_analysis_caller.analyze();
		Map<String, VFDatum> kind_caller_results = kind_analysis_caller.getFlowSets().get(f);
		ParameterizedExpr rhs = (ParameterizedExpr) s.getRHS();
		if (!(rhs.getChild(0) instanceof NameExpr)){
			errors.add(new TargetNotFoundException(rhs.getTarget().getNameExpressions().iterator().next().getName()));
			return errors;
		}
		NameExpr ne = (NameExpr) rhs.getChild(0);
		VFDatum neKind = kind_caller_results.containsKey(ne.getName().getID()) ?
		    kind_caller_results.get(ne.getName().getID()) : VFDatum.UNDEF;
		if (!neKind.isFunction()){
			errors.add(new TargetNotAFunction(ne.getName(),f , kind_caller_results.get(ne.getName().getID()).toString()));
			return errors;
		}
		
		FunctionReference lookupreference = context.peek().resolve(ne.getName().getID());
		ASTNode lookupres = context.resolveFunctionReference(lookupreference);
		Function target = null;
		if (context.peek().getAllOverloads(ne.getName().getID()).size()<2 && lookupres instanceof Function)
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
		
		
		ContextStack calleeContext = new ContextStack(Lists.<GenericFile>newLinkedList(),
		    lookupreference.path.getParent());
		calleeContext.push(target);
		LinkedList<AssignStmt> toRemove = Lists.newLinkedList();
		VFFlowInsensitiveAnalysis kind_analysis_callee = new VFFlowInsensitiveAnalysis(target);
		kind_analysis_callee.analyze();
		
		List list=(List) s.getParent();
		int k=0;
		for (;list.getChild(k)!=s;k++);
		    //just go forward in list
		list.removeChild(k);
		LinkedList<Stmt> nlist = Lists.newLinkedList();
		
		for (int i=0;i<rhs.getNumArg();i++){
			AssignStmt newStmt = new AssignStmt(new NameExpr(target.getInputParam(i)), rhs.getArg(i));
			toRemove.add(newStmt);
			nlist.add(newStmt);
		}
		for(int j=0;j<target.getStmtList().getNumChild();j++){
			nlist.add(target.getStmtList().getChild(j));
		}
		if (s.getLHS() instanceof MatrixExpr) {
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
		} else if (target.getNumOutputParam()>0) {
			AssignStmt newStmt = new AssignStmt((Expr)(s.getLHS().copy()), new NameExpr(target.getOutputParam(0)));
			toRemove.add(newStmt);
			nlist.add(newStmt);
		}
		for(int j=nlist.size()-1;j>=0;j--){
			list.insertChild(nlist.get(j),k);
		}
		java.util.List<NameExpr> symbols = 
		        Lists.newArrayList(NodeFinder.find(NameExpr.class, target.getStmtList()));
		VFFlowInsensitiveAnalysis kind_analysis_post = new VFFlowInsensitiveAnalysis(f);
		kind_analysis_post.analyze();
		for (NameExpr nameExpr: symbols){
			Name n = nameExpr.getName();
			String sym = n.getID();
			VFDatum kind_callee;
			if (kind_analysis_callee.getFlowSets().get(n)!=null) {
			  kind_callee = kind_analysis_callee.getFlowSets().get(n).get(sym);
			  if (kind_callee == null) {
			    kind_callee = VFDatum.UNDEF;
			  }
			}
			else{
	           kind_callee = kind_analysis_callee.getFlowSets().get(target).get(sym);
	           if (kind_callee == null) {
	             kind_callee = VFDatum.UNDEF;
	           }
			}
			VFDatum kind_caller=kind_caller_results.containsKey(sym) ?
			    kind_caller_results.get(sym) : VFDatum.UNDEF;
			VFDatum kind_post = kind_analysis_post.getFlowSets().get(f).get(sym);
			if (kind_post == null) {
			  kind_post = VFDatum.UNDEF;
			}
			
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
