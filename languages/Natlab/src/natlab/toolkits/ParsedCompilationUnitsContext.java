package natlab.toolkits;

import java.util.List;

import natlab.toolkits.path.FolderHandler;
import ast.ASTNode;
import ast.CompilationUnits;

public class ParsedCompilationUnitsContext extends Context {

	public final CompilationUnits cu;

	public ParsedCompilationUnitsContext(ASTNode<?> curFunction, FolderHandler pwd,
			FolderHandler fwd, List<FolderHandler> path) {
		super(curFunction, pwd, fwd, path);
		cu = null;
	}
	
	public ParsedCompilationUnitsContext(ASTNode<?> curFunction, FolderHandler pwd,
			FolderHandler fwd, List<FolderHandler> path, CompilationUnits cu) {
		super(curFunction, pwd, fwd, path);
		this.cu = cu;
	}	
}
