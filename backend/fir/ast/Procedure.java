package fir.ast;

import fir.table.*;

/* either a function or subroutine */

public class Procedure implements ASTnode {
	Signature signature;
	Table table;
	Stmt body;
}
