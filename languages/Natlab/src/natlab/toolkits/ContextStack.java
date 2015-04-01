package natlab.toolkits;
import java.util.LinkedList;

import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FolderHandler;
import ast.Function;

public class ContextStack{
	protected java.util.Stack<Context> stack;
	protected java.util.List<FolderHandler> path;
	protected FolderHandler pwd;
	public ContextStack(java.util.List<GenericFile> path, GenericFile pwd){
	    stack = new java.util.Stack<Context>();
	    this.path=new LinkedList<FolderHandler>();
	    for (GenericFile p:path){
		this.path.add(FolderHandler.getFolderHandler(p));
	    }
	    this.pwd=FolderHandler.getFolderHandler(pwd);
	}

	public Context push(Function f){
	    Context c = new Context(f, pwd, null, path);
	    stack.push(c);
	    return c;
	}
	
	protected Context makeContext(ast.Script s, FolderHandler pwd, FolderHandler fwd, java.util.List<FolderHandler> path){
		return new Context(s, pwd, fwd, path);
	}
	
	public Context push(ast.Script s, GenericFile functionWorkingDirectory){
	    FolderHandler fwd = null;
	    if (functionWorkingDirectory != null)
		fwd = FolderHandler.getFolderHandler(functionWorkingDirectory);
	    Context c = makeContext(s, pwd, fwd, path);
	    stack.push(c);
	    return c;
	}
	public Context pop(){
	    return stack.pop();
	}
	public Context peek(){
	    return stack.peek();
	}
}
