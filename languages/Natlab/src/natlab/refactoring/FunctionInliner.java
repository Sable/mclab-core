package natlab.refactoring;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CompilationUnits;
import ast.Expr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

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
	
	private List<AssignStmt> functionCalls(ast.Function f) {
	  return NodeFinder.find(AssignStmt.class, f.getStmts())
	      .filter(stmt -> stmt.getRHS() instanceof ParameterizedExpr)
	      .collect(Collectors.toList());
	}

	public int countCalls() {
	  return NodeFinder.find(ast.Function.class, cu)
	      .filter(f -> !(f.getParent().getParent() instanceof ast.Function))
	      .collect(Collectors.summingInt(f -> {
	        context.push(f);
	        VFFlowInsensitiveAnalysis kind_analysis_caller = new VFFlowInsensitiveAnalysis(f);
	        kind_analysis_caller.analyze();
	        Map<String, VFDatum> kind = kind_analysis_caller.getFlowSets().get(f);
	        int count = (int) NodeFinder.find(ParameterizedExpr.class, f.getStmts())
	            .filter(p -> p.getTarget() instanceof NameExpr)
	            .map(p -> ((NameExpr) p.getTarget()).getName().getID())
	            .filter(name -> context.peek().getAllOverloads(name).size() < 2)
	            .filter(name -> kind.getOrDefault(name, VFDatum.UNDEF).isVariable())
	            .map(name -> context.resolveFunctionReference(context.peek().resolve(name)))
	            .filter(lookup -> lookup instanceof ast.Function)
	            .count();
	        context.pop();
	        return count;
	      }));
	}

	public Map<AssignStmt, LinkedList<RefactorException>> inlineAll(){
	  return NodeFinder.find(ast.Function.class, cu)
	      .filter(f -> !(f.getParent().getParent() instanceof ast.Function))
	      .map(this::inlineAll)
	      .reduce(new HashMap<>(), (m1, m2) -> { m1.putAll(m2); return m1; });
	}
	
	public Map<AssignStmt, LinkedList<RefactorException>> inlineAll(ast.Function f){
		VFFlowInsensitiveAnalysis kind = new VFFlowInsensitiveAnalysis(f);
		kind.analyze();
		RightSimplification rs = new RightSimplification(f, new VFPreorderAnalysis(f));
        rs.transform();
        
        return functionCalls(f).stream()
            .collect(Collectors.toMap(Function.identity(), this::inline));
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

	private boolean removeExtra(ast.Function f, AssignStmt s){
		String left = ((NameExpr)s.getLHS()).getName().getID();
		String right = ((NameExpr)s.getRHS()).getName().getID();
		LivenessAnalysis l = new LivenessAnalysis(f);
		ReachingDefs defs = new ReachingDefs(f);
		CopyAnalysis copies = new CopyAnalysis(f);
		List<NameExpr> exprs = NodeFinder.find(NameExpr.class, f.getStmts())
		    .collect(Collectors.toList());
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
		ast.List<Stmt> parent= (ast.List<Stmt>) s.getParent();
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
        ast.Function f = NodeFinder.findParent(ast.Function.class, s);
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
		ast.Function target = null;
		if (context.peek().getAllOverloads(ne.getName().getID()).size()<2 && lookupres instanceof ast.Function)
			target = (ast.Function)lookupres;
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
		
		ast.List list=(ast.List) s.getParent();
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
		List<NameExpr> symbols = NodeFinder.find(NameExpr.class, target.getStmts())
		    .collect(Collectors.toList());
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
