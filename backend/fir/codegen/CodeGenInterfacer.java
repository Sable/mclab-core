package fir.codegen;
//little class handed to code generation of builtin functions to interface with codegen
/* can either insert fir object or fortran text
 * 
 */
import java.util.*;
import fir.ast.*;
import fir.table.*;
import fir.type.*;

public abstract class CodeGenInterfacer {
	public Uid getUid(String name){return null;};
	public Uid getUid(){return null;}
	
	public void addFunction(Function function){}
	//public void addSubroutine(Subroutine subroutine){} TODO
	public void addFunction(Signature signature,String text,Uid name){}
	public void addSubroutine(Signature signature,String text,Uid name){}
	//public void addNewInternalVariable(InternalVar var,Uid varName){} //? -- variables are added with the constructors
	public void addNewModule(String moduleText,Uid moduleName){}
			
	public Procedure getCurrentProcedure(){return null;};
	public Program getCurrentProgram(){return null;};
	public Table getCurrentTable(){return null;} //this returns
	HashMap<Object,Object> getMap(){return null;}; //a map for Builtins to dump data in, which is the same for all interfacer gernerated by one codeGen
	
	//constructors for special AST nodes, using CodeComponents
	public Expr CreateCodeComponentExpression(Type type,Vector<CodeComponent> code){return null;};
	public Expr CreateCodeComponentFunctionCall(Type type,String name,Vector<CodeComponent> arguments){return null;}
	public Stmt CreateCodeComponentStatement(Vector<CodeComponent> code){return null;}
	public Stmt CreateCodeComponentCall(String name,Vector<CodeComponent> arguments){return null;}
}

