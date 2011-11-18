package mclab;

import java.util.HashMap;

public class Filters {
	public static Filters getInstance(String name){;
		return cache.get(name);
	}
	
	static {
		cache = new HashMap<String, Filters>();
		Filters f = new Filters("refactoring");
		f.add("feval", "//ParameterizedExpr/NameExpr[1]/Name[@nameId='feval']");
	    f.add("Function files","/CompilationUnits/FunctionList/Function");
//	    f.add("Script files","/CompilationUnits/Script");
	    f.add("Script files","/CompilationUnits/Script[count(.//*[@nameId='mex'])=0 and count(StmtList/*) >0 ]");
	    f.add("Number of functions","/CompilationUnits/FunctionList/Function");
//	    queries.put("calls to 'struct'","//ParameterizedExpr[NameExpr/Name/@nameId='struct']");
	    f.add("feval calls with a param arg", "//ParameterizedExpr[NameExpr[position()=1]/Name/@nameId='feval' and NameExpr[position()=2]/Name/@nameId=ancestor::Function/InputParamList/Name/@nameId]");
	    f.add("feval calls with a string literal arg", "//ParameterizedExpr[./*[position()=1 and name(.)='NameExpr' and ./Name/@nameId='feval'] and ./*[position()=2 and name(.)='StringLiteralExpr']]");
	    f.add("FunctionHandle","//FunctionHandleExpr");
	    f.add("Call To scripts","/CompilationUnits/FunctionList//ExprStmt[./ParameterizedExpr[count(*)=1]/NameExpr[Name/@nameId=/CompilationUnits/Script[count(StmtList/*)>0]/@name] | ./NameExpr[Name/@nameId=/CompilationUnits/Script[count(StmtList/*)>0]/@name]]");
	    f.add("Lines of code","//*[ends-with(name(.),'Stmt')]");
//	    f.add("samples","/CompilationUnits/FunctionList/Function[count(.//*[ends-with(name(.),'Stmt')])<15]//ReturnStmt");
	    
	    f.add("cd", "//ParameterizedExpr[NameExpr[position()=1]/Name/@nameId='cd']");
	    f.add("addpath", "//ParameterizedExpr[NameExpr[position()=1]/Name/@nameId='addpath']");
	    

		//queries.put("IDs","//NameExpr[@kind='BOT']");
		f = new Filters("loops");
		f.add("Calls inside loops", "//ForStmt[.//NameExpr[@kind='FUN']]|//WhileStmt[.//NameExpr[@kind='FUN']]|//ParForStmt[.//NameExpr[@kind='FUN']]");
		f.add("All loops", "//ForStmt|//WhileStmt|//ParForStmt");
		f = new Filters("kind");
		f.add("IDs", "//NameExpr[@kind='BOT']");
		f.add("Prefixes", "//NameExpr[@kind='PREFIX']");
		f.add("MayVar", "//NameExpr[@kind='LDVAR']");
		f.add("Warning", "//NameExpr[@kind='WAR']");
		f.add("Error", "//NameExpr[@kind='TOP']");
	}
	public final HashMap<String, String> queries = new HashMap<String, String>();
	public final String name;
	private void add(String name, String query){
		queries.put(name, query);
	}
	private static HashMap<String, Filters> cache; 
	private Filters(String name){
		this.name=name;
	//	queries.put("Project Name",
						//"((/CompilationUnits/FunctionList/@name|/CompilationUnits/Script/@name)[1])");
		cache.put(name, this);
	}
	
}
