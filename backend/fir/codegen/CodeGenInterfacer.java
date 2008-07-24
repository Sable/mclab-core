package fir.codegen;
//little class handed to code generation of builtin functions to interface with codegen
/* can either insert fir object or fortran text
 * 
 */
import java.util.*;
import fir.ast.*;
import fir.table.*;

public class CodeGenInterfacer {
	public Uid getUid(String name){return null;};
	public Uid getUid(){return null;}
	
	public void addFunction(Function function){}
	public void addSubroutine(Subroutine subroutine){}
	public void addFunction(SignatureType signature,String text,Uid name){}
	public void addSubroutine(Signature signature,String text,Uid name){}
	public void addNewVariable(InternalVar var,Uid varName){} //?
	public void addNewModule(String moduleText,Uid moduleName){}
	
	boolean insertExpression; //if not expressionk, then statement
	public boolean haveToInsertExpression(){return insertExpression;}
	
	 //multiple inserts will simply overwrite everything
	public void insertExpression(Expr expression){};
	public void insertExpression(String text){};
	public void insertStmt(Stmt statment){};
	public void insertStmt(String text){};
	
	
	public Procedure getCurrentProcedure(){return null;};
	public Program getCurrentProgram(){return null;};
	HashMap<Object,Object> getMap(){return null;}; //a map for Builtins to dump data in, which is the same for all interfacer gernerated by one codeGen
}
