package natlab.tame.builtin.classprop;

import java.util.HashMap;

import natlab.tame.builtin.classprop.ast.CP;
import natlab.tame.builtin.classprop.ast.CPAny;
import natlab.tame.builtin.classprop.ast.CPBegin;
import natlab.tame.builtin.classprop.ast.CPBuiltin;
import natlab.tame.builtin.classprop.ast.CPChain;
import natlab.tame.builtin.classprop.ast.CPCoerce;
import natlab.tame.builtin.classprop.ast.CPEnd;
import natlab.tame.builtin.classprop.ast.CPError;
import natlab.tame.builtin.classprop.ast.CPList;
import natlab.tame.builtin.classprop.ast.CPNone;
import natlab.tame.builtin.classprop.ast.CPPackaged;
import natlab.tame.builtin.classprop.ast.CPScalar;
import natlab.tame.builtin.classprop.ast.CPTypeString;
import natlab.tame.builtin.classprop.ast.CPUnion;
import natlab.tame.builtin.classprop.ast.CPVar;

/**
 * provides a list of functions that are defined in the class propagation language.
 * Note that CP has no parent pointers, so we can add a subtree to a tree multiple times.
 * We use this instead of defining keywords in the lexer, because it's shorter.
 */
public class Functions {
	static HashMap<String,CP> constants = new HashMap<String, CP>();
	static HashMap<String,Function> functions = new HashMap<String, Function>();
	
	private static void add(String name, CP exp){
		constants.put(name,exp);
	}
	private static void add(String name, String exp){
		add(name,new CPPackaged(ClassPropTool.parse(exp),name));
	}
	private static void add(String name, Function f){
		functions.put(name,f);
	}

	
	static {
		//construct the functions/constants on static load of the class:
		// **** constants ****************************************************
		add("double", new CPBuiltin("double"));
		add("single", new CPBuiltin("single"));
		add("char",   new CPBuiltin("char"));
		add("logical",new CPBuiltin("logical"));
		add("uint8",  new CPBuiltin("uint8"));
		add("uint16", new CPBuiltin("uint16"));
		add("uint32", new CPBuiltin("uint32"));
		add("uint64", new CPBuiltin("uint64"));
		add("int8",   new CPBuiltin("int8"));
		add("int16",  new CPBuiltin("int16"));
		add("int32",  new CPBuiltin("int32"));
		add("int64",  new CPBuiltin("int64"));
		add("function_handle", new CPBuiltin("function_handle"));
		
		
		add("float", "single|double");
		add("uint", "uint8|uint16|uint32|uint64");
		add("sint", "int8|int16|int32|int64");
		add("int", "uint|sint");
		add("numeric", "float|int");
		add("matrix", "numeric|char|logical");

		
		add("none",  new CPNone());
		add("end",   new CPEnd());
		add("begin", new CPBegin());
		add("any",   new CPAny());
		add("error", new CPError());
		add("scalar",new CPScalar());
			

		
		// **** add functions ******************************************************
		add("coerce",
				new Function(){
					public CP getTree(CPList list){
						if (list.size() != 2) return null;
						return new CPCoerce(
								list.get(0),
								list.get(1));
				}});
		
		add("typeString",
			new Function(){
				public CP getTree(CPList list){
					if (list.size() != 1) return null;
					return new CPTypeString(
						list.get(0));
			}});
		
				
		
		//TODO - this should be a proper AST node
		add("*",
			new Function(){
				public CP getTree(CPList list){
					if (list.size() != 1) return null;
					CP a = list.get(0);
					return new CPPackaged( // 'a*' is represented as (a|none)(a|none)...(a|none) - FIXME 
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
						new CPChain (new CPUnion(a,new CPNone()),
									 new CPUnion(a,new CPNone()))))))))))),
						"("+a+")*");
			}});
		
		//TODO - this should be a proper AST node
		add("?",
			new Function(){
				public CP getTree(CPList list){
					if (list.size() != 1) return null;
					CP a = list.get(0);
					return new CPPackaged(new CPUnion (a, new CPNone()),"("+a+")?");
			}});

	}
	
	
	/**
	 * returns the associated constant/variable
	 */
	static CP get(String name){
		//compare with variables - they have to be unique objects, so aren't actually constant
		if (name.equals("parent"))return new CPVar("parent");                                             
		if (name.equals("natlab"))return new CPVar("natlab");
		if (name.equals("matlab"))return new CPVar("matlab");
		
		//check among constants
		if (!constants.containsKey(name)) 
			throw new UnsupportedOperationException("unknown constant "+name);
		return constants.get(name);
	}
	
	/**
	 * returns the associated function, given a name and a list of arguments
	 */
	static CP get(String name, CPList args){
		if (!functions.containsKey(name)) 
			throw new UnsupportedOperationException("unknown function "+name);
		CP result = functions.get(name).getTree(args);		
		if (result == null) 
			throw new UnsupportedOperationException("bad function call "+name+" with "+args);		
		return result;
	}
}

/**
 * interface used to represent functions in the language
 */
interface Function{
	public CP getTree(CPList args);
}


