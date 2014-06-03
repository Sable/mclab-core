package natlab.refactoring;

import ast.Name;

public class Exceptions {
	public static abstract class RefactorException extends RuntimeException{
		public ast.Name sym;
		public ast.Function f;
		public String message;
		public String toString(){
			return this.getClass().getName()+ ":" +  sym.getID();
		}
	}; 
	
	public static class TargetNotFoundException extends RefactorException{
		public TargetNotFoundException(Name name) {
			sym=name;
		}
	};
	
	public static class TargetNotAScript extends RefactorException{
		public TargetNotAScript(Name name, ast.Function f, String m) {
			message = m;
			this.f = f;
			sym=name;
		}
	};
	
	public static class TargetNotAFunction extends RefactorException{
		public TargetNotAFunction(Name name, ast.Function f, String m) {
			message = m;
			this.f = f;
			sym=name;
		}
	};

	public static class TooManyInputParams extends RefactorException{
		public TooManyInputParams(Name name, ast.Function f, String m) {
			message = m;
			this.f = f;
			sym=name;
		}
	};

	public static class TooManyOutputParams extends RefactorException{
		public TooManyOutputParams(Name name, ast.Function f, String m) {
			message = m;
			this.f = f;
			sym=name;
		}
	};

	public static class RenameRequired extends RefactorException{
		public RenameRequired(Name name) {
			sym=name;
		}
	};
	
	public static class IDConflictException extends RefactorException{
		public IDConflictException(Name name) {
			sym=name;
		}
	};

	public static class NameResolutionChangeException extends RefactorException{
		public NameResolutionChangeException(Name name) {
			sym=name;
		}
	};
	
	public static class UnassignedVariableException extends RefactorException{
		public UnassignedVariableException(Name name) {
			sym=name;
		}
	};
	
	public static class WarnIDToFunException extends RefactorException{
		public WarnIDToFunException(Name name) {
			sym=name;
		}
	};
	
	public static class ScriptCalledFromScript extends RefactorException{
		public ScriptCalledFromScript(Name name) {
			sym=name;
		}
	};
	
	public static class ScriptInputsNotMatching extends RefactorException{
		public ScriptInputsNotMatching (Name name) {
			sym=name;
		}
	};
	

	
	public static class NoCallsToScript extends RefactorException{
		public NoCallsToScript (Name name) {
			sym=name;
		}
	};
	

	public static class ScriptOutputsNotMatching extends RefactorException{
		public ScriptOutputsNotMatching (Name name) {
			sym=name;
		}
	};

	public static class KindConflict extends RefactorException{
		public KindConflict (Name name) {
			sym=name;
		}
	};

	public static class FunctionInputCanBeUndefined extends RefactorException{
		public FunctionInputCanBeUndefined (Name name) {
			sym=name;
		}
	};

	public static class FunctionOutputCanBeUndefined extends RefactorException{
		public FunctionOutputCanBeUndefined (Name name) {
			sym=name;
		}
	};
	
	public static class NameConflict extends RefactorException{
	  public NameConflict (Name name) {
	    sym=name;
	  }
	};
}
