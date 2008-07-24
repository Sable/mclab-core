package fir.codegen;

import fir.ast.*;
import fir.builtin.*;
//code generator class!
//the constructor gernerates all code.
//afterwards the generated code etc can be retrieved

public class CodeGen {
	public String[] getRequiredLibraries(){return null;};
	public String[] getFiles(){return null;};
	public String[] getFilenames(){return null;};
	
	//generates code
	public CodeGen(Program program,BuiltinFinder builtinFinder,CodeGenOptions options){ 
	}
}
