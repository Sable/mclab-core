package fir.ast;
import fir.type.Type;

public abstract class Expr implements ASTnode {
	public abstract Type getType();
}
