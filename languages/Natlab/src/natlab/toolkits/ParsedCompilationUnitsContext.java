package natlab.toolkits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FolderHandler;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.path.FunctionReference.ReferenceType;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Function;
import ast.FunctionList;
import ast.Program;
import ast.Script;

public class ParsedCompilationUnitsContext extends Context {

	public final CompilationUnits cu;

	public ParsedCompilationUnitsContext(ASTNode curFunction, FolderHandler pwd,
			FolderHandler fwd, List<FolderHandler> path) {
		super(curFunction, pwd, fwd, path);
		cu = null;
	}
	
	public ParsedCompilationUnitsContext(ASTNode curFunction, FolderHandler pwd,
			FolderHandler fwd, List<FolderHandler> path, CompilationUnits cu) {
		super(curFunction, pwd, fwd, path);
		this.cu = cu;
	}	
}
