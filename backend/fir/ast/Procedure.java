package fir.ast;

import fir.table.*;
import fir.type.Signature;

/* either a function or subroutine */

public class Procedure implements ASTnode {
	Signature signature;
	Table table;
	Stmt body;
}
