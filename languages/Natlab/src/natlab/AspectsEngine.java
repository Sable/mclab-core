package natlab;

import natlab.ast.*;
import natlab.ast.List;

import java.util.*;

public class AspectsEngine {

	static LinkedList<action> actionsList = new LinkedList<action>();
	static LinkedList<pattern> patternsList = new LinkedList<pattern>();

	static final public String AFTER = "after";
	static final public String BEFORE = "before";
	static final public String AROUND = "around";

	static final public String SET = "set";
	static final public String GET = "get";
	static final public String CALL = "call";

	static final public String ARGS = "args";
	static final public String DIMS = "dims";
	static final public String NEWVAL = "newVal";

	static public int correspondingCount = -1;
	public static int getCorrespondingCount() { return correspondingCount; }
	private static String generateCorrespondingFunctionName(String func){
		correspondingCount += 1;
		return "_$" + func + "$" + correspondingCount;
	}

	public static void generateCorrespondingFunction(Expr pe) {
		ASTNode node = pe;
		while(node != null && !(node instanceof Function)) //TODO: for script?
			node = node.getParent();

		if(node != null) {
			Function prog = (Function)node;
			String funName = generateCorrespondingFunctionName(prog.getName());

			natlab.ast.List<Name> output = new natlab.ast.List<Name>();
			output.add(new Name("retValue"));

			natlab.ast.List<Stmt> as = new natlab.ast.List<Stmt>();
			as.add(new AssignStmt(new NameExpr(new Name("retValue")), (Expr) pe.copy()));
			Function fun = new Function(output, funName, new natlab.ast.List<Name>(), new natlab.ast.List<HelpComment>(), as, new natlab.ast.List<Function>());

			prog.addNestedFunction(fun);
			ParameterizedExpr call = new ParameterizedExpr(new NameExpr(new Name(funName)), new natlab.ast.List<Expr>());

			int ind = pe.getParent().getIndexOfChild(pe);
			pe.getParent().setChild(call, ind);
		}
	}

	private static String generateActionName(String aspect, String action){
		return aspect + "_" + action;
	}

	public static void fetchAspectInfo(Program prog)
	{ 	
		Aspect aspect = (Aspect) prog;

		for(Patterns patterns : aspect.getPatterns())
			for(Pattern pattern : patterns.getPatterns())
			{
				String name = pattern.getName();
				PatternDesignator pd = (PatternDesignator) pattern.getPD();
				String type = pd.getName();

				String variable = pd.getArgs().getChild(pd.getArgs().getNumChild()-1).getID();
				variable = variable.substring(variable.lastIndexOf('.')+1);

				String target = variable;
				String dims = "0";
				boolean more = false;

				if(variable.contains("$")) {
					target = variable.substring(0, variable.lastIndexOf('$'));
					dims = variable.substring(variable.lastIndexOf('$')+1);

					if(dims.contains("+")) {
						more = true;
						dims = dims.substring(0, dims.lastIndexOf('+'));
					}
				}

				patternsList.add(new pattern(name, type, target, dims, more));
			}

		for(Actions lst : aspect.getActions())
			for(AspectAction action : lst.getAspectActions())
			{
				String name = action.getName();
				String type = action.getType();
				String pattern = action.getPattern();
				String modifiedName = generateActionName(aspect.getName(), name);

				pattern pat = null;
				for(int i = 0; i<patternsList.size(); i++) {
					pattern tmp = patternsList.get(i);
					if(tmp.getName().compareTo(pattern) == 0) {
						pat = tmp;
						break;
					}
				}

				Function fun = new Function(new natlab.ast.List<Name>(), modifiedName, action.getSelectors(), new natlab.ast.List<HelpComment>() , action.getStmts(), action.getNestedFunctions());
				actionsList.add(new action(modifiedName, type, pat, fun));
			}
	}

	public static FunctionList convertToFunctionList(Program prog)
	{ 	
		Aspect aspect = (Aspect) prog;
		FunctionList funcList = new FunctionList();

		for(Actions actions : aspect.getActions())
			for(AspectAction action : actions.getAspectActions())
			{
				String name = action.getName();
				String type = action.getType();

				String modifiedName = generateActionName(aspect.getName(), name);

				if(type.compareTo(AROUND) != 0)
					funcList.addFunction(new Function(new natlab.ast.List<Name>(), modifiedName, action.getSelectors(), new natlab.ast.List<HelpComment>(), action.getStmts(), action.getNestedFunctions()));
			}

		for(Methods methods : aspect.getMethods())
			for(Function function : methods.getFunctions())
			{
				funcList.addFunction(function);
			}

		return funcList;
	}

	private static String fetchTargetName(Expr exp) {
		String varName = "";

		if(exp instanceof NameExpr)
		{
			NameExpr pe = (NameExpr) exp;
			varName = pe.getName().getID();
		}
		else if(exp instanceof ParameterizedExpr)
		{
			ParameterizedExpr pe = (ParameterizedExpr) exp;
			Expr expr1 = pe.getTarget();
			if(expr1 instanceof NameExpr)
			{
				NameExpr pe1 = (NameExpr) expr1;
				varName = pe1.getName().getID();
			}
		}

		return varName;
	}

	private static int matchAndWeave(natlab.ast.List<Stmt> stmts, int s, String type, String target, AssignStmt context) {
		int count = 0;
		for(int j=0; j<actionsList.size(); j++)
		{
			action act = actionsList.get(j);
			pattern pat = act.getPattern();

			if(pat.getType().compareTo(type) == 0 && pat.getTarget().compareTo(target) == 0){
				Function fun = act.getFunction();
				NameExpr funcName = new NameExpr(new Name(act.getName()));
				natlab.ast.List<Expr> lstExpr = new natlab.ast.List<Expr>();

				for(Name param : fun.getInputParams()) {
					if(param.getID().compareTo(NEWVAL) == 0) {
						lstExpr.add(getNewVal(context.getRHS()));
					} else if(param.getID().compareTo(ARGS) == 0) {
						if(pat.getType().compareTo(CALL) == 0)
							lstExpr.add(getDims(context.getRHS()));
						else
							lstExpr.add(new CellArrayExpr());
					} else if(param.getID().compareTo(DIMS) == 0) {
						if(pat.getType().compareTo(SET) == 0)
							lstExpr.add(getDims(context.getLHS()));
						else if(pat.getType().compareTo(GET) == 0)
							lstExpr.add(getDims(context.getRHS()));
						else
							lstExpr.add(new CellArrayExpr());
					}
				}

				Stmt call = null;
				ExprStmt action = new ExprStmt(new ParameterizedExpr(funcName, lstExpr));

				if(!(pat.getDims().compareTo("0") == 0)) {
					BinaryExpr cond = null; 
					if(!pat.getDimsAndMore())
						cond = new EQExpr(new NameExpr(new Name("numel(size("+target+"))")), new NameExpr(new Name(pat.getDims())));
					else
						cond = new GEExpr(new NameExpr(new Name("numel(size("+target+"))")), new NameExpr(new Name(pat.getDims())));

					IfBlock ib = new IfBlock(cond, new natlab.ast.List<Stmt>().add(action));
					call = new IfStmt(new natlab.ast.List<IfBlock>().add(ib), new Opt<ElseBlock>());
				} else {
					call = action;
				}

				if(act.getType().compareTo(BEFORE) == 0) {
					stmts.insertChild(call, s);
				} else if(act.getType().compareTo(AFTER) == 0) {
					stmts.insertChild(call, s+1);
				} else if(act.getType().compareTo(AROUND) == 0) {
					//TODO
				}

				count += 1;
			}
		}

		return count;
	}

	public static void transformForStmt(natlab.ast.List<Stmt> stmts)
	{
		int stmtCount = stmts.getNumChild();

		for(int s=0; s<stmtCount; s++)
		{
			if(stmts.getChild(s) instanceof ForStmt) {
				ForStmt fs = (ForStmt) stmts.getChild(s);
				if( !fs.isAspectTransformed() ) {
					AssignStmt as_old = fs.getAssignStmt();
					Expr lhs = as_old.getLHS();
					String tmpAS = "_$" + "tmpAS$";
					String tmpFS = "_$" + "tmpFS$";
	
					if(lhs instanceof NameExpr){
						NameExpr ne = (NameExpr) lhs;
						tmpAS += ne.getName().getID();
						tmpFS += ne.getName().getID();
					}
	
					AssignStmt as_out = new AssignStmt();
					as_out.setRHS(as_old.getRHS());
					as_out.setLHS(new NameExpr(new Name(tmpAS)));
	
					AssignStmt as_for = new AssignStmt();
					as_for.setRHS(new RangeExpr(new IntLiteralExpr(new DecIntNumericLiteralValue("1")), new Opt<Expr>(), new ParameterizedExpr(new NameExpr(new Name("numel")), new natlab.ast.List<Expr>().add(as_out.getLHS()))));
					as_for.setLHS(new NameExpr(new Name(tmpFS)));
	
					AssignStmt as_in = new AssignStmt();
					as_in.setRHS(new ParameterizedExpr(new NameExpr(new Name(tmpAS)), new natlab.ast.List<Expr>().add(new NameExpr(new Name(tmpFS)))));
					as_in.setLHS(as_old.getLHS());
	
					natlab.ast.List<Stmt> lstFor = new natlab.ast.List<Stmt>();
					lstFor.add(as_in);
					for(Stmt stmt : fs.getStmts())
						lstFor.add(stmt);
	
					ForStmt fs_new = new ForStmt();
					fs_new.setAssignStmt(as_for);
					fs_new.setStmtList(lstFor);
					fs_new.setAspectTransformed(true);
					
					stmts.removeChild(s);
					stmts.insertChild(as_out, s);
					stmts.insertChild(fs_new, s+1);
					s++;
					stmtCount++;
					
					//lst.add(as_out);
					//lst.add(fs_new);
				}
			}
		}
	}

	public static void weaveStmts(natlab.ast.List<Stmt> stmts)
	{
		transformForStmt(stmts);

		int stmtCount = stmts.getNumChild();

		for(int s=0; s<stmtCount; s++)
		{
			Stmt stmt = stmts.getChild(s);
			if(stmt instanceof ExprStmt) {
				//TODO: ???
			} else if(stmt instanceof AssignStmt) {
				AssignStmt as = (AssignStmt) stmt;
				Expr lhs = as.getLHS();
				Expr rhs = as.getRHS();

				int count = 0;
				String varName = "";

				varName = fetchTargetName(lhs);
				count += matchAndWeave(stmts, s, SET, varName, as);

				varName = fetchTargetName(rhs);
				count += matchAndWeave(stmts, s, GET, varName, as);
				count += matchAndWeave(stmts, s, CALL, varName, as);

				s += count;
				stmtCount += count;
			} else {
				stmt.aspectsWeave();
			}
		}
	}

	private static CellArrayExpr getNewVal(Expr exp){
		CellArrayExpr nv = new CellArrayExpr();

		if(exp instanceof IntLiteralExpr || exp instanceof FPLiteralExpr || exp instanceof MatrixExpr) {
			nv.addRow(new Row(new natlab.ast.List<Expr>().add(exp)));
		}

		return nv;
	}

	private static CellArrayExpr getDims(Expr exp){
		CellArrayExpr dims = new CellArrayExpr();

		if(exp instanceof ParameterizedExpr) {
			ParameterizedExpr pe = (ParameterizedExpr) exp;
			dims.addRow(new Row(pe.getArgs()));
		}

		return dims;
	}
}

class pattern {
	private String name;
	private String type; //TODO: complicated patterns
	private String target;
	private String dims;
	private boolean dimsAndMore;

	public pattern(String nam, String typ, String tar, String dim, boolean mor) {
		name = nam;
		type = typ;
		target = tar;
		dims = dim;
		dimsAndMore = mor;
	}

	public void setName(String nam) { name = nam; }
	public void setType(String typ) { type = typ; }
	public void setTarget(String tar) { target = tar; }
	public void setDims(String dim) { dims = dim; }
	public void setDimsAndMore(boolean mor) { dimsAndMore = mor; }

	public String getName() { return name; }
	public String getType() { return type; }
	public String getTarget() { return target; }
	public String getDims() { return dims; }
	public boolean getDimsAndMore() { return dimsAndMore; }
}

class action {
	private String name;
	private String type;
	private pattern patt;
	private Function func;

	public action(String nam, String typ, pattern pat, Function fun) {
		name = nam;
		type = typ;
		patt = pat;
		func = fun;
	}

	public void setName(String nam) { name = nam; }
	public void setType(String typ) { type = typ; }
	public void setPattern(pattern pat) { patt = pat; }
	public void setFunction(Function fun) { func = fun; }

	public String getName() { return name; }
	public String getType() { return type; }
	public pattern getPattern() { return patt; }
	public Function getFunction() { return func; }
}