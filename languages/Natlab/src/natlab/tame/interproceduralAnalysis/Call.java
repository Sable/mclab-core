package natlab.tame.interproceduralAnalysis;

import natlab.toolkits.path.FunctionReference;

/**
 * an element of a call String immutable TODO - should there be a factory
 * method, to get rid of annoying generic constructor?
 */
public class Call<Arg> {
	private FunctionReference functionRef;
	private Arg argument;

	public Call(FunctionReference functionRef, Arg argument) {
		// this.isRecursive = isRecursive;
		this.functionRef = functionRef;
		this.argument = argument;
	}

	public Arg getArguments() {
		return argument;
	}

	public FunctionReference getFuncionReference() {
		return functionRef;
	}

	@Override
	public String toString() {
		return functionRef.getName() + "(" + (argument != null ? argument : "")
				+ ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Call<?>) {
			Call<?> aCall = (Call<?>) obj;
			return (aCall.functionRef.equals(functionRef))
					&& ((aCall.argument != null) ? aCall.argument
							.equals(argument) : aCall.argument == argument);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		
		return functionRef.hashCode()
				+ (argument == null ? 0 : argument.hashCode());
	}
}