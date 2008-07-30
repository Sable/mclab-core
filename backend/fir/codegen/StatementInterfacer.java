package fir.codegen;
import java.util.Vector;

import fir.ast.*;

/* this is a Code Interfacer that requires the insertion of a Statement */

public class StatementInterfacer {
	public boolean insertStatement(Expr expression){return false;}
	public boolean insertStatement(String text){return false;}
	public boolean insertStatement(Vector<CodeComponent> expression){return false;}
}
