package natlab.toolkits;

import java.util.List;
import java.util.Map;

import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FolderHandler;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.path.FunctionReference.ReferenceType;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Function;
import ast.FunctionList;
import ast.Program;
import ast.Script;

import com.google.common.collect.Maps;

public class ParsedCompilationUnitsContextStack extends ContextStack{
	public CompilationUnits cu;
	Map<GenericFile, ASTNode<?>> pm = Maps.newHashMap();
	public ParsedCompilationUnitsContextStack(List<GenericFile> path,
			GenericFile pwd, CompilationUnits cu) {
		super(path, pwd);
		this.cu = cu;
		for (Program p: cu.getProgramList()){
			pm.put(p.getFile(), p);
		}
	}
	
	protected Context makeContext(ast.Script s, FolderHandler pwd, FolderHandler fwd, List<FolderHandler> path){
		return new ParsedCompilationUnitsContext(s, pwd, fwd, path, cu);
	}
	
	public ASTNode<?> resolveFunctionReference(FunctionReference r){
		if (r==null) return null;
		if (!pm.containsKey(r.path))
			return null;
		ASTNode<?> n = pm.get(r.path);
		if (r.referenceType==ReferenceType.UNKNOWN){
			if (n instanceof FunctionList)
				return ((FunctionList)n).getFunction(0);
			else if (n instanceof Script)
				return n;
			else {
				System.err.println(n + " " + ((FunctionList)n).getFunction(0).getName() + " " + r.name);
				throw new RuntimeException();
			}
		}
		else if(r.referenceType==ReferenceType.NESTED){
			if (n instanceof FunctionList
				&& ((FunctionList)n).getFunction(0).getNested().containsKey(r.name))
				return ((FunctionList)n).getFunction(0).getNested().get(r.name);
		}
		else if(r.referenceType==ReferenceType.SUBFUNCTION){
			if (n instanceof FunctionList){
				for (Function f: ((FunctionList)n).getFunctionList()){
					if (f.getName()==r.name)
						return f;
				}
			}
				return ((FunctionList)n).getFunction(0).getNested().get(r.name);
		}	
		return null;
	}
	
}
