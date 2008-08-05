package fir.ast;
import fir.type.Type;
//done4now
//TODO should an expression always be known completely? -- or at least have a fortran compatible type?
// --> then it could implement InternalValue
//could have expression also refer to variables for shape (different from variable)? maybe not...
//note that expressions are not fixed in their structure (and concept)
public abstract class Expr implements ASTnode {
	public abstract Type getType();
}
