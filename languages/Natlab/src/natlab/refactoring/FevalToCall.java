package natlab.refactoring;

import java.util.List;
import java.util.Map;

import natlab.refactoring.Exceptions.RefactorException;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.utils.AbstractNodeFunction;
import natlab.utils.NodeFinder;
import ast.CompilationUnits;
import ast.Function;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FevalToCall {
	ParsedCompilationUnitsContextStack context;
	public FevalToCall(CompilationUnits cu){
		context = new ParsedCompilationUnitsContextStack(Lists.<GenericFile>newLinkedList(),
		    cu.getRootFolder(), cu);
	}
	
	public Map<ParameterizedExpr, List<RefactorException>> replaceAll(){
		final Map<ParameterizedExpr, List<RefactorException>> map =  Maps.newHashMap();

		for (Function f: NodeFinder.find(context.cu, Function.class)){
			context.push(f);
			final VFFlowInsensitiveAnalysis kind = new VFFlowInsensitiveAnalysis(context.cu);
			kind.analyze();
			NodeFinder.apply(f, ParameterizedExpr.class, new AbstractNodeFunction<ParameterizedExpr>() {
			@Override public void apply(ParameterizedExpr node) {
				if (! (node.getTarget() instanceof NameExpr)) return ;
		        NameExpr target = (NameExpr)node.getTarget();
		        if (target.getName().getID().equals( "feval" ) ){
		            if (node.getArg(0) instanceof StringLiteralExpr)
		            	map.put(node, replace(node, kind));
		        }
			}});
			context.pop();	
		}
		return map;
	}
	
	public List<RefactorException> replace(ParameterizedExpr node, VFFlowInsensitiveAnalysis kind){
		List<RefactorException> errors = Lists.newLinkedList();
		String target = ((StringLiteralExpr)node.getArg(0)).getValue();
		if (kind.getFlowSets().get(context.peek().curFunction).getKind(target).isVariable())
			errors.add(new RenameRequired(new Name(target)));
        if (errors.isEmpty()) {
            node.setTarget(new NameExpr(new Name(target)));
            node.getArgList().removeChild(0);
        }
		return errors;
	}
}
