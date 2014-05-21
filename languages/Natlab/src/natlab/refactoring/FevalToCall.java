package natlab.refactoring;

import java.util.List;
import java.util.Map;

import natlab.refactoring.Exceptions.RefactorException;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.filehandling.GenericFile;
import natlab.utils.NodeFinder;
import ast.CompilationUnits;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class FevalToCall {
	ParsedCompilationUnitsContextStack context;
	public FevalToCall(CompilationUnits cu){
		context = new ParsedCompilationUnitsContextStack(Lists.<GenericFile>newLinkedList(),
		    cu.getRootFolder(), cu);
	}
	
	public Map<ParameterizedExpr, List<RefactorException>> replaceAll(){
	  final ImmutableMap.Builder<ParameterizedExpr, List<RefactorException>> builder =
	      ImmutableMap.builder();

		for (ast.Function f: NodeFinder.find(ast.Function.class, context.cu)){
			context.push(f);
			final VFFlowInsensitiveAnalysis kind = new VFFlowInsensitiveAnalysis(context.cu);
			kind.analyze();

			builder.putAll(NodeFinder.find(ParameterizedExpr.class, f)
			    .filter(new Predicate<ParameterizedExpr>() {
			      @Override public boolean apply(ParameterizedExpr node) {
			        return node.getTarget() instanceof NameExpr &&
			            ((NameExpr) node.getTarget()).getName().getID().equals("feval") &&
			            node.getArg(0) instanceof StringLiteralExpr;
			      }
			    })
			    .toMap(new Function<ParameterizedExpr, List<RefactorException>>() {
			      @Override public List<RefactorException> apply(ParameterizedExpr node) {
			        return replace(node, kind);
			      }
			    }));

			context.pop();
		}
		return builder.build();
	}
	
	public List<RefactorException> replace(ParameterizedExpr node, VFFlowInsensitiveAnalysis kind){
		List<RefactorException> errors = Lists.newLinkedList();
		String target = ((StringLiteralExpr)node.getArg(0)).getValue();
		Map<String, VFDatum> kinds = kind.getFlowSets().get(context.peek().curFunction);
		VFDatum targetKind = kinds.containsKey(target) ? kinds.get(target) : VFDatum.UNDEF;
		if (targetKind.isVariable())
			errors.add(new RenameRequired(new Name(target)));
        if (errors.isEmpty()) {
            node.setTarget(new NameExpr(new Name(target)));
            node.getArgList().removeChild(0);
        }
		return errors;
	}
}
