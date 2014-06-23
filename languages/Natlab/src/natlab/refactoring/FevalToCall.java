package natlab.refactoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import natlab.refactoring.Exceptions.RefactorException;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.utils.NodeFinder;
import ast.CompilationUnits;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;

import com.google.common.collect.ImmutableMap;

public class FevalToCall {
	ParsedCompilationUnitsContextStack context;
	public FevalToCall(CompilationUnits cu){
		context = new ParsedCompilationUnitsContextStack(new LinkedList<>(),
		    cu.getRootFolder(), cu);
	}
	
	public Map<ParameterizedExpr, List<RefactorException>> replaceAll(){
	  final ImmutableMap.Builder<ParameterizedExpr, List<RefactorException>> builder =
	      ImmutableMap.builder();

	    NodeFinder.find(ast.Function.class, context.cu).forEach(f -> {
			context.push(f);
			final VFFlowInsensitiveAnalysis kind = new VFFlowInsensitiveAnalysis(context.cu);
			kind.analyze();

			builder.putAll(NodeFinder.find(ParameterizedExpr.class, f)
			    .filter(node -> node.getTarget() instanceof NameExpr)
			    .filter(node -> ((NameExpr) node.getTarget()).getName().getID().equals("feval"))
			    .filter(node -> node.getArg(0) instanceof StringLiteralExpr)
			    .collect(Collectors.toMap(Function.identity(), node -> replace(node, kind))));
			context.pop();
		});
		return builder.build();
	}
	
	public List<RefactorException> replace(ParameterizedExpr node, VFFlowInsensitiveAnalysis kind){
		List<RefactorException> errors = new LinkedList<>();
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
