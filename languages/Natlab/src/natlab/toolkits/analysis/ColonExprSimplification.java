package natlab.toolkits.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import ast.ASTNode;
import ast.AssignStmt;
import ast.ColonExpr;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.List;
import ast.MTimesExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Program;
import ast.RangeExpr;
import ast.Stmt;
import natlab.CompilationProblem;
import natlab.FPNumericLiteralValue;
import natlab.Parse;
import natlab.toolkits.filehandling.GenericFile;
import nodecases.natlab.NatlabAbstractNodeCaseHandler;

public class ColonExprSimplification extends NatlabAbstractNodeCaseHandler {
	private Stmt currStmt = null;
	private final String tempName = "dim_temp";
	private int tempNum = 1;
	HashSet<ColonExpr> colonExprSet = new HashSet<>();

	@Override
	public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode node) {
		// TODO Auto-generated method stub
		for (int i = 0; i < node.getNumChild(); i++) {
			node.getChild(i).analyze(this);
		}
	}

	@Override
	public void caseColonExpr(ColonExpr node) {
		if (colonExprSet.contains(node)) {
			return;
		}
		if (!(node.getParent().getParent() instanceof ParameterizedExpr)) {
			throw new UnsupportedOperationException(
					"Parent of parent of ColonExpr is assumed to be a ParamaterizedExpr");
		}
		ParameterizedExpr arrayExpr = (ParameterizedExpr) node.getParent()
				.getParent();
		String arrayName = arrayExpr.getVarName();
		String colonTemp = genTempVar();
		List<Expr> args = arrayExpr.getArgList();
		int colonIndex = args.getIndexOfChild(node);
		ASTNode parentNode = currStmt.getParent();
		if (args.getIndexOfChild(node) < (args.getNumChild() - 1)) {

			@SuppressWarnings("rawtypes")
			AssignStmt stmt = genAssignStmt(node, colonIndex, arrayName,
					colonTemp);
			parentNode.insertChild(stmt, parentNode.getIndexOfChild(currStmt));
			RangeExpr rangeExpr = genRangeExpr(colonTemp);
			args.setChild(rangeExpr, colonIndex);
		} else {
			FPLiteralExpr lowerExpr = genConstExpr(colonIndex + 1);
			ast.List<Expr> ndimList = new ast.List<Expr>();
			ndimList.add(genNameExpr(arrayName));

			AssignStmt tempInit = genAssignStmt(genNameExpr(colonTemp),
					genConstExpr(1));
			parentNode.insertChild(tempInit,
					parentNode.getIndexOfChild(currStmt));
			ParameterizedExpr nDimCall = genParameterizedExpr(
					genNameExpr("ndims"), ndimList);
			RangeExpr forRange = genRangeExpr(lowerExpr, nDimCall);
			String tempIter = genTempVar();
			AssignStmt forHeader = genAssignStmt(genNameExpr(tempIter),
					forRange);
			ast.List<Stmt> forBody = new ast.List<>();
			ast.List<Expr> sizeArgs = new ast.List<>();
			sizeArgs.add(genNameExpr(arrayName));
			sizeArgs.add(genNameExpr(tempIter));
			MTimesExpr multExpr = new MTimesExpr(genNameExpr(colonTemp),
					genParameterizedExpr(genNameExpr("size"), sizeArgs));
			forBody.add(genAssignStmt(genNameExpr(colonTemp), multExpr));
			ForStmt forStmt = new ForStmt(forHeader, forBody);
			parentNode.insertChild(forStmt,
					parentNode.getIndexOfChild(currStmt));
			RangeExpr rangeExpr = genRangeExpr(colonTemp);
			args.setChild(rangeExpr, colonIndex);
		}

		colonExprSet.add(node);
		// caseExpr(node);
	}

	public AssignStmt genAssignStmt(Expr lhs, Expr rhs) {
		return new AssignStmt(lhs, rhs);
	}

	public void caseStmt(Stmt node) {
		currStmt = node;
		caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
	}

	public MTimesExpr genMultExpr(Expr lhs, Expr rhs) {
		return new MTimesExpr(lhs, rhs);
	}

	public ParameterizedExpr genParameterizedExpr(NameExpr arrayName,
			ast.List<Expr> args) {
		return new ParameterizedExpr(arrayName, args);
	}

	public RangeExpr genRangeExpr(Expr lower, Expr upper) {
		RangeExpr rangeExpr = new RangeExpr();
		rangeExpr.setLower(lower);
		rangeExpr.setUpper(upper);
		return rangeExpr;
	}

	public FPLiteralExpr genConstExpr(int val) {
		return new FPLiteralExpr(new FPNumericLiteralValue(
				Integer.toString(val)));
	}

	public NameExpr genNameExpr(String var) {
		return new NameExpr(new Name(var));
	}

	public String genTempVar() {
		return tempName + tempNum++;
	}

	public RangeExpr genRangeExpr(String colonTemp) {
		RangeExpr rangeExpr = new RangeExpr();
		Expr lower = new FPLiteralExpr(new FPNumericLiteralValue(
				Integer.toString(1)));
		rangeExpr.setLower(lower);
		rangeExpr.setUpper(new NameExpr(new Name(colonTemp)));
		return rangeExpr;
	}

	public AssignStmt genAssignStmt(ColonExpr node, int colonIndex,
			String arrayName, String colonTemp) {

		NameExpr lhsExpr = new NameExpr(new Name(colonTemp));
		ast.List<Expr> argList = new ast.List<>();
		argList.add(new NameExpr(new Name(arrayName)));
		argList.add(genConstExpr(colonIndex + 1));
		ParameterizedExpr rhsExpr = new ParameterizedExpr((Expr) (new NameExpr(
				new Name("size"))), argList);
		AssignStmt stmt = new AssignStmt(lhsExpr, rhsExpr);
		return stmt;
	}

	public static void analyze(Program program) {
		ColonExprSimplification colonAnalysis = new ColonExprSimplification();
		program.analyze(colonAnalysis);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileDir = "mcpi/";// "capr/";
		String fileName = "mcpi_p.m";
		String fileIn = fileDir + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		Program program = Parse.parseMatlabFile(gFile,
				new ArrayList<CompilationProblem>());
		//ColonExprSimplification.analyze(program);
		System.out.println(program.getPrettyPrinted());
	}

}
