package natlab.tame.valueanalysis;

import java.util.ArrayList;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.IfBlock;
import ast.Name;

//TODO - maybe this could be abstracted into a tir class, in natlab.tir.tools?

public class ValueAnalysisPrinter extends TIRAbstractNodeCaseHandler {
	public static final int COMMENTINDENT = 36;
	IntraproceduralValueAnalysis<?> analysis;
	private StringBuffer buf;
	
	private ValueAnalysisPrinter(IntraproceduralValueAnalysis<?> analysis) {
		this.buf = new StringBuffer();
		this.analysis = analysis;
		((TIRNode)analysis.getTree()).tirAnalyze(this);
	}
	
	
	public static String prettyPrint(
			IntraproceduralValueAnalysis<?> analysis){
		return new ValueAnalysisPrinter(analysis).buf.toString();
	}
	
	
	@Override
	public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode node) {
		buf.append(node.getPrettyPrintedLessComments());
		//buf.append("~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}	
	
	/** cases containing other statements ****************************/
	public void caseTIRFunction(TIRFunction node){
		String indent = node.getIndent();
		buf.append("% args: "+analysis.getArgMap().toString().replace("\n","\n%       ")+"\n");
		buf.append(indent + "function ");
		buf.append(" [");
		boolean first = true;
		for(Name param : node.getOutputParams()) {
			if(!first) {
				buf.append(", ");
			}
			buf.append(param.getPrettyPrinted());
			
			first = false;
		}
		buf.append("] = ");
		buf.append(node.getName());
		
				
		buf.append("(");
		first = true;
		for(Name param : node.getInputParams()) {
			if(!first) {
				buf.append(", ");
			}
			buf.append(param.getPrettyPrinted());
			
			first = false;
		}
		buf.append(")");
		buf.append('\n');
		for(ast.HelpComment comment : node.getHelpComments()) {
			buf.append(comment.getPrettyPrinted());
			buf.append('\n');
		}
		printStatements(node.getStmts());
		for(ast.Function func : node.getNestedFunctions()) {
			((TIRNode)func).tirAnalyze(this);
			buf.append('\n');
		}
		buf.append(indent + "end\n");
		buf.append("% results: "+analysis.getResult().toString().replace("\n","\n%          "));
    }
	

	@Override
	public void caseTIRWhileStmt(TIRWhileStmt node) {
        String indent = node.getIndent();
        buf.append(indent + "while ");
        buf.append(node.getExpr().getPrettyPrinted());
        buf.append('\n');
        printStatements(node.getStmts());
        buf.append(indent + "end");
	}
	
	@Override
	public void caseTIRForStmt(TIRForStmt node) {
        String indent = node.getIndent();
        buf.append(indent + "for ");
        String assignStmt = node.getAssignStmt().getPrettyPrinted();
        buf.append( assignStmt.trim() );   // remove all tabs
        buf.append('\n');
        printStatements(node.getStmtList());
        buf.append(indent + "end");
	}
	
	@Override
	public void caseTIRIfStmt(TIRIfStmt node) {
        String indent = node.getIndent();
        boolean first = true;
        for(IfBlock block : node.getIfBlocks()) {
            if(!first) {
                buf.append(indent + "elseif " ); // TIRFUNCTIONS have no else, actually...
            }else {
            	buf.append(indent + "if " );
            }
            buf.append(block.getCondition().getPrettyPrinted());
            buf.append('\n');
            printStatements(block.getStmtList());
            first = false;
        }
        if(node.hasElseBlock()) {
        	buf.append(indent + "else\n");
            printStatements(node.getElseStatements());
        }
        buf.append(indent + "end");
	}
	
	private void printStatements(ast.List<ast.Stmt> stmts){
		for(ast.Stmt stmt : stmts) {
			int length = buf.length();
			((TIRNode)stmt).tirAnalyze(this);
			if (buf.length() > length) buf.append('\n');
		}
	}
	
	
	
	/*** value analysis printer cases ********************************/
	@Override
	public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
		int length = buf.length();
		buf.append(node.getPrettyPrintedLessComments()+" ");
		for(int i = (buf.length()-length-node.getIndent().length());i<COMMENTINDENT;i++){
			buf.append(' ');
		}
		ArrayList<String> vars = new ArrayList<String>();
		if (node instanceof TIRAbstractAssignToVarStmt){
			vars.add(((TIRAbstractAssignToVarStmt)node).getTargetName().getID());
		} else if (node instanceof TIRAbstractAssignToListStmt){ 
			for(ast.Name name : ((TIRAbstractAssignToListStmt)node).getTargets().asNameList()){
				vars.add(name.getID());				
			}
		} else if (node instanceof TIRArraySetStmt){
			vars.add(((TIRArraySetStmt)node).getArrayName().getID());
		} else if (node instanceof TIRCellArraySetStmt){
			vars.add(((TIRCellArraySetStmt)node).getCellArrayName().getID());
		} else if (node instanceof TIRDotSetStmt){
			vars.add(((TIRDotSetStmt)node).getDotName().getID());
		};
		printVars(analysis.getOutFlowSets().get(node), vars);
	}
	
	private void printVars(ValueFlowMap<?> flow, ArrayList<String> vars){
		String prefix = "% ";
		if (!flow.isViable()){
			buf.append("% non-viable"); return;
		}
		for (String name : vars){
			buf.append(prefix+name+"="+flow.get(name));
			prefix = ", ";
		}
	}
	
	
	
}
