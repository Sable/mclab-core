package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;

import sun.awt.image.ShortInterleavedRaster;
import natlab.tame.builtin.Builtin.Islogical;
import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AndExpr;
import ast.AssignStmt;
import ast.BinaryExpr;
import ast.Expr;
import ast.ForStmt;
import ast.IfBlock;
import ast.IfStmt;
import ast.Name;
import ast.NameExpr;
import ast.NotExpr;
import ast.ParameterizedExpr;
import ast.ShortCircuitAndExpr;
import ast.ShortCircuitOrExpr;

import com.google.common.collect.HashBiMap;

@SuppressWarnings("rawtypes")
public class TemporaryVariablesRemoval extends TIRAbstractNodeCaseHandler
		implements TamerPlusAnalysis {
	public static boolean DEBUG = false;
	private final Integer INVALID_INDEX = -1;
	UDDUWeb fUDDUWeb;
	TIRToMcSAFIRTableBuilder fTIRToMcSAFIRTableBuilder;
	private HashBiMap<TIRNode, ASTNode> fTIRToMcSAFIRTable;
	private HashMap<Expr, Name> fExprToTempVarName;
	private Set<String> fRemainingVariablesNames;
	private ReachingDefinitions reachingDef = null;
	private Map<ASTNode, ASTNode> ifStmtToShortCircuitExprMap = new HashMap<ASTNode, ASTNode>();
	private Set<ASTNode> shortCircuitIfSet = new HashSet<ASTNode>();

	public Map<ASTNode, ASTNode> getShortCircuitIfStmtSet() {
		return ifStmtToShortCircuitExprMap;
	}

	public void setShortCircuitIfStmtSet(
			Map<ASTNode, ASTNode> shortCircuitIfStmtSet) {
		this.ifStmtToShortCircuitExprMap = shortCircuitIfStmtSet;
	}

	private Map<VarAndColorContainer, Set<TIRNode>> colorToDefSetMap = new HashMap<VarAndColorContainer, Set<TIRNode>>();
	private Map<VarAndColorContainer, Expr> colorToShortCircuitMap = new HashMap<VarAndColorContainer, Expr>();

	public TemporaryVariablesRemoval(ASTNode<?> tree) {
		fExprToTempVarName = new HashMap<>();
	}

	@Override
	public void analyze(AnalysisEngine engine) {
		fUDDUWeb = engine.getUDDUWebAnalysis();

		fTIRToMcSAFIRTableBuilder = engine.getTIRToMcSAFTableBuilder();
		fTIRToMcSAFIRTable = fTIRToMcSAFIRTableBuilder.getTIRToMcSAFIRTable();

		fRemainingVariablesNames = engine.getDefinedVariablesAnalysis()
				.getDefinedVariablesFullSet();
		reachingDef = engine.getReachingDefinitionsAnalysis();
		getFunctionNode().tirAnalyze(this);

		if (DEBUG) {
			printTIRToMcSAFIRTable();
			printExprToTempVarNameTable();
			printRemainingVariablesNames();
		}
	}

	@Override
	public void caseASTNode(ASTNode node) {
		int nodeChildrenCount = node.getNumChild();
		for (int i = 0; i < nodeChildrenCount; i++) {
			if (node.getChild(i) instanceof TIRNode) {
				((TIRNode) node.getChild(i)).tirAnalyze(this);
			} else {
				node.getChild(i).analyze(this);
			}
		}
	}

	@Override
	public void caseTIRCallStmt(TIRCallStmt node) {
		TIRCommaSeparatedList usedVariablesNames = node.getArguments();
		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRIfStmt(TIRIfStmt node) {
		Name conditionVariableName = node.getConditionVarName();
		if (conditionVariableName != null) {
			Map<TIRNode, Integer> nodeAndColor = fUDDUWeb
					.getNodeAndColorForUse(conditionVariableName
							.getNodeString());
			boolean isNodeAValidKeyInNodeToColorMap = (nodeAndColor != null && nodeAndColor
					.containsKey(node));
			if (isNodeAValidKeyInNodeToColorMap
					&& isTemporaryVariable(conditionVariableName)) {
				replaceUsedTempVarByDefinition(conditionVariableName, node);
			}
		}

		caseIfStmt(node);
	}

	@Override
	public void caseTIRWhileStmt(TIRWhileStmt node) {
		Name conditionVariableName = node.getCondition().getName();
		if (conditionVariableName != null) {
			Map<TIRNode, Integer> nodeAndColor = fUDDUWeb
					.getNodeAndColorForUse(conditionVariableName
							.getNodeString());
			boolean isNodeAValidKeyInNodeToColorMap = nodeAndColor != null
					&& nodeAndColor.containsKey(node);
			if (isNodeAValidKeyInNodeToColorMap
					&& isTemporaryVariable(conditionVariableName)) {
				replaceUsedTempVarByDefinition(conditionVariableName, node);
			}
		}
		caseWhileStmt(node);
	}

	@Override
	public void caseTIRForStmt(TIRForStmt node) {
		TIRCommaSeparatedList usedVariablesNames = null;

		usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(
				node.getLowerName()));
		if (node.hasIncr()) {
			usedVariablesNames.add(new NameExpr(node.getIncName()));
		}
		usedVariablesNames.add(new NameExpr(node.getUpperName()));

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);

		caseForStmt(node);
	}

	@Override
	public void caseTIRDotSetStmt(TIRDotSetStmt node) {
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr(node.getValueName()));
		usedVariablesNames.add(new NameExpr(node.getDotName()));

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node) {
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr(node.getValueName()));
		usedVariablesNames.add(new NameExpr(node.getCellArrayName()));
		TIRCommaSeparatedList indices = node.getIndizes();
		addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRArraySetStmt(TIRArraySetStmt node) {
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr(node.getValueName()));
		usedVariablesNames.add(new NameExpr(node.getArrayName()));
		TIRCommaSeparatedList indices = node.getIndizes();
		addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRCopyStmt(TIRCopyStmt node) {
		TIRCopyStmt copySmtmt = (TIRCopyStmt) node;
		Name usedVariableName = copySmtmt.getSourceName();

		if (usedVariableName != null && isTemporaryVariable(usedVariableName)) {
			replaceUsedTempVarByDefinition(usedVariableName, node);
		}
	}

	@Override
	public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node) {
		Name cellArrayName = node.getCellArrayName();
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr((cellArrayName)));
		TIRCommaSeparatedList indices = node.getIndices();
		addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRDotGetStmt(TIRDotGetStmt node) {
		Name dotName = node.getDotName();
		Name fieldName = node.getFieldName();
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr(dotName));
		usedVariablesNames.add(new NameExpr(fieldName));

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	@Override
	public void caseTIRArrayGetStmt(TIRArrayGetStmt node) {
		Name arrayName = node.getArrayName();
		TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(
				new NameExpr((arrayName)));
		TIRCommaSeparatedList indices = node.getIndizes();
		addTmpIndicesToUsedVariablesNames(indices, usedVariablesNames);

		replaceUsedTempVarsByDefintions(usedVariablesNames, node);
	}

	private void replaceUsedTempVarsByDefintions(
			TIRCommaSeparatedList usedVariablesNames, TIRNode useNode) {
		if (usedVariablesNames == null) {
			return;
		}
		int usedVariablesCount = usedVariablesNames.size();
		for (int i = 0; i < usedVariablesCount; i++) {
			NameExpr variableNameExpr = usedVariablesNames.getNameExpresion(i);
			if (variableNameExpr == null) {
				continue;
			}
			Name variableName = variableNameExpr.getName();
			if (isTemporaryVariable(usedVariablesNames.getNameExpresion(i))) {
				replaceUsedTempVarByDefinition(variableName, useNode);
			}
		}
	}

	private void replaceUsedTempVarByDefinition(Name variable, TIRNode useNode) {
		Expr definition = getDefinitionForVariableAtNode(variable, useNode);
		Queue<ASTNode> nodeQueue = new LinkedList<>();
		Set<ASTNode> markedNodes = new HashSet<>();
		ASTNode currentNode = null;

		ASTNode parentNode = null;
		Integer childIndex = INVALID_INDEX;
		initializeNodeQueueAndMarkedNodes(fTIRToMcSAFIRTable.get(useNode),
				nodeQueue, markedNodes);
		while (!nodeQueue.isEmpty()) {
			currentNode = nodeQueue.remove();
			if (currentNode instanceof ForStmt) {
				currentNode = ((ForStmt) currentNode).getAssignStmt();
			}
			if (isSeekedNode(currentNode, variable)) {
				parentNode = currentNode.getParent();
				childIndex = getChildIndexForNode(parentNode, currentNode);
				boolean isChildIndexAndParentValid = (parentNode != null)
						&& (childIndex.intValue() != INVALID_INDEX.intValue());
				if (isChildIndexAndParentValid) {
					parentNode.setChild(definition, childIndex);
					if (isShortCircuit(definition)) {
						addToExprToTempVarMap(variable, definition);
					}
					fExprToTempVarName.put(definition, variable);
					updateRemainingVariablesNamesSet(variable.getID());
				}
				break;
			} else {
				addChildrenOfNodeToQueueAndMarkedSet(currentNode, nodeQueue,
						markedNodes);
			}
		}
	}

	private void initializeNodeQueueAndMarkedNodes(ASTNode replacementNode,
			Queue<ASTNode> nodeQueue, Set<ASTNode> markedNodes) {
		nodeQueue.add(replacementNode);
		markedNodes.add(replacementNode);
	}

	private void addChildrenOfNodeToQueueAndMarkedSet(ASTNode currentNode,
			Queue<ASTNode> nodeQueue, Set<ASTNode> markedNodes) {
		int childrenCount = currentNode.getNumChild();
		for (int i = 0; i < childrenCount; i++) {
			ASTNode currentChild = currentNode.getChild(i);
			if (!markedNodes.contains(currentChild)) {
				markedNodes.add(currentChild);
				nodeQueue.add(currentChild);
			}
		}
	}

	private int getChildIndexForNode(ASTNode parentNode, ASTNode seekedChild) {
		int seekedIndex = INVALID_INDEX;
		int childrenCount = parentNode.getNumChild();
		for (int i = 0; i < childrenCount; i++) {
			ASTNode currentChild = parentNode.getChild(i);
			if (seekedChild == currentChild) {
				seekedIndex = i;
				break;
			}
		}
		return seekedIndex;
	}

	private boolean isSeekedNode(ASTNode node, Name variable) {
		return (node instanceof Name || node instanceof NameExpr)
				&& node.getVarName().equals(variable.getID());
	}

	private boolean isShortCircuitAnd(ArrayList<TIRNode> defSet, TIRNode useNode) {
		for (TIRNode node : defSet) {
			if (isShortCircuitAnd(node, useNode)) {
				return true;
			}
		}
		return false;
	}

	private boolean isShortCircuitOr(ArrayList<TIRNode> defSet) {
		for (TIRNode node : defSet) {
			if (isShortCircuitOr(node)) {
				return true;
			}
		}
		return false;
	}

	private boolean isShortCircuitLogicalAnd(TIRNode node) {
		if (node instanceof TIRCallStmt
				&& (((TIRCallStmt) node).getRHS().getVarName().equals("and"))) {
			return true;
		}
		return false;
	}

	private boolean isShortCircuitLogicalAnd(ArrayList<TIRNode> defSet) {
		for (TIRNode node : defSet) {
			if (isShortCircuitLogicalAnd(node)) {
				return true;
			}
		}
		return false;
	}

	private Expr getLogicalAndNode(TIRAbstractAssignStmt lhs,
			TIRAbstractAssignStmt rhs) {
		if (isShortCircuitLogicalAnd(rhs)) {
			return ((AssignStmt) fTIRToMcSAFIRTable.get(rhs)).getRHS();
		} else if (isShortCircuitLogicalAnd(lhs)) {
			return ((AssignStmt) fTIRToMcSAFIRTable.get(lhs)).getRHS();
		}
		return null;
	}

	private boolean isShortCircuitAndInIfCond(TIRNode useNode, TIRNode node) {
		return isIfCond((ASTNode) useNode)
				&& (((TIRCallStmt) node).getRHS().getVarName().trim()
						.equals("true"));
	}

	private boolean isShortCircuitAnd(TIRNode node, TIRNode useNode) {
		if (node instanceof TIRCallStmt) {

			if ((((TIRCallStmt) node).getRHS().getVarName().trim()
					.equals("false"))) {
				return true;
			}
			if (isShortCircuitAndInIfCond(useNode, node)) {
				return true;
			}
			return false;

		}

		return false;
	}

	private boolean isIfCond(ASTNode node) {
		ASTNode parentNode = node;
		if (parentNode instanceof IfStmt) {
			return true;
		}
		return false;
	}

	private boolean isShortCircuitOr(TIRNode node) {
		if (node instanceof TIRCallStmt
				&& (((TIRCallStmt) node).getRHS().getVarName().equals("true"))) {
			return true;
		}
		return false;
	}

	private Expr getShortCircuitAndNode(IfStmt stmt, TIRAbstractAssignStmt lhs,
			TIRAbstractAssignStmt rhs, TIRNode useNode) {
		TIRNode rhsNode;
		TIRNode lhsNode = null;
		Expr rhsExpr = null, lhsExpr = null;
		if (isShortCircuitAnd(lhs, useNode)) {
			rhsNode = rhs;
			if (ifStmtToShortCircuitExprMap.containsKey(stmt)) {
				lhsExpr = (Expr) ifStmtToShortCircuitExprMap.get(stmt);
			} else {
				lhsNode = replaceBoolExpr(lhs);
				lhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(lhsNode))
						.getRHS();
			}
			rhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(rhsNode)).getRHS();

		} else if (isShortCircuitAnd(rhs, useNode)) {
			rhsNode = lhs;
			if (ifStmtToShortCircuitExprMap.containsKey(stmt)) {
				lhsExpr = (Expr) ifStmtToShortCircuitExprMap.get(stmt);
			} else {
				lhsNode = replaceBoolExpr(rhs);
				lhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(lhsNode))
						.getRHS();
			}
			rhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(rhsNode)).getRHS();

		} else {
			throw new UnsupportedOperationException(
					"Either one of the two statements have to be a call statement to false");
		}
		if (isIfCond((ASTNode) useNode)) {
			if (rhsExpr.getVarName().equals("not")) {
				rhsExpr = ((ParameterizedExpr) rhsExpr).getArg(0);
			}
		}
		return new ShortCircuitAndExpr(lhsExpr, rhsExpr);
	}

	private Expr getShortCircuitOrNode(IfStmt stmt, TIRAbstractAssignStmt lhs,
			TIRAbstractAssignStmt rhs) {
		TIRNode rhsNode;
		TIRNode lhsNode = null;
		Expr lhsExpr = null, rhsExpr = null;
		if (isShortCircuitOr(lhs)) {
			rhsNode = rhs;
			if (ifStmtToShortCircuitExprMap.containsKey(stmt)) {

				lhsExpr = (Expr) ifStmtToShortCircuitExprMap.get(stmt);

			} else {
				lhsNode = replaceBoolExpr(rhs);
				lhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(lhsNode))
						.getRHS();
			}
			rhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(rhsNode)).getRHS();
		} else if (isShortCircuitOr(rhs)) {
			rhsNode = lhs;
			if (ifStmtToShortCircuitExprMap.containsKey(stmt)) {
				lhsExpr = (Expr) ifStmtToShortCircuitExprMap.get(stmt);

			} else {
				lhsNode = replaceBoolExpr(rhs);
				lhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(lhsNode))
						.getRHS();
			}
			rhsExpr = ((AssignStmt) fTIRToMcSAFIRTable.get(rhsNode)).getRHS();
		} else {
			throw new UnsupportedOperationException(
					"Either one of the two statements have to be a call statement to false");
		}
		if (!(lhsExpr instanceof ShortCircuitAndExpr)) {
			if (lhsExpr instanceof ParameterizedExpr
					&& lhsExpr.getVarName().equals("not")) {
				lhsExpr = ((ParameterizedExpr) lhsExpr).getArg(0);
			} else {
				NotExpr notExpr = new NotExpr();
				notExpr.setOperand(lhsExpr);
				lhsExpr = notExpr;
			}
		}
		return new ShortCircuitOrExpr(lhsExpr, rhsExpr);

	}

	private Expr getShortCircuitNode(IfStmt stmt, TIRAbstractAssignStmt lhs,
			TIRAbstractAssignStmt rhs, TIRNode useNode) {
		ArrayList<TIRNode> defArrayList = new ArrayList<TIRNode>(2);
		defArrayList.add(lhs);
		defArrayList.add(rhs);
		if (isShortCircuitLogicalAnd(defArrayList)) {
			return getLogicalAndNode(lhs, rhs);
		} else if (isShortCircuitAnd(defArrayList, useNode)) {
			Expr expr = getShortCircuitAndNode(stmt, lhs, rhs, useNode);
			return expr;
		} else if (isShortCircuitOr(defArrayList)) {
			Expr expr = getShortCircuitOrNode(stmt, lhs, rhs);
			return expr;
		}
		return null;
	}

	private Expr getShortCircuitNode(ArrayList<TIRNode> defSet, TIRNode useNode) {
		ArrayList<TIRNode> commonIfNodes = getNodesWithCommonIf(defSet);
		IfStmt currIfStmt = (IfStmt) getIfNode(commonIfNodes.get(0));

		Expr expr = getShortCircuitNode(currIfStmt,
				(TIRAbstractAssignStmt) commonIfNodes.get(0),
				(TIRAbstractAssignStmt) commonIfNodes.get(1), useNode);
		shortCircuitIfSet.add(currIfStmt);
		try {
			ASTNode useStmt = getIfNode(useNode);
			if (useStmt != null) {
				ifStmtToShortCircuitExprMap.put(
						fTIRToMcSAFIRTable.get(useStmt), expr.clone());
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		defSet.removeAll(commonIfNodes);
		Map<IfStmt, TIRNode> ifStmtMap = getIfStmtMap(defSet);
		while (!ifStmtMap.isEmpty()) {
			IfStmt parentIf = getParent(ifStmtMap.keySet(), currIfStmt);
			if (parentIf == null) {
				throw new NullPointerException(
						"Short circuit definition is not contained in a if block");
			}
			TIRNode elseStmt = ifStmtMap.get(parentIf);
			if (ifStmtToShortCircuitExprMap.containsKey(parentIf)) {
				Expr shortExpr = (Expr) ifStmtToShortCircuitExprMap
						.get(parentIf);
				if (isShortCircuitAnd(elseStmt, useNode)) {
					expr = new ShortCircuitAndExpr(shortExpr, expr);

				} else if (isShortCircuitOr(elseStmt)) {
					Expr lhsExpr = shortExpr;

					// if (lhsExpr instanceof ParameterizedExpr
					// && lhsExpr.getVarName().equals("not")) {
					// lhsExpr = ((ParameterizedExpr) lhsExpr).getArg(0);
					// } else {
					// NotExpr notExpr = new NotExpr();
					// notExpr.setOperand(lhsExpr);
					// lhsExpr = notExpr;
					// }
					expr = new ShortCircuitOrExpr(lhsExpr, expr);
				} else if (isShortCircuitLogicalAnd(elseStmt)) {
					expr = ((AssignStmt) fTIRToMcSAFIRTable.get(elseStmt))
							.getRHS();
				}
			} else {
				TIRNode replacementNode;
				// TODO : Add check to see if statement is contained in the map.
				// Will it go here though
				replacementNode = replaceBoolExpr(elseStmt);
				if (isShortCircuitAnd(elseStmt, useNode)) {
					expr = new ShortCircuitAndExpr(
							((AssignStmt) fTIRToMcSAFIRTable.get(replacementNode))
									.getRHS(), expr);

				} else if (isShortCircuitOr(elseStmt)) {
					Expr lhsExpr = ((AssignStmt) fTIRToMcSAFIRTable
							.get(replacementNode)).getRHS();

					if (lhsExpr instanceof ParameterizedExpr
							&& lhsExpr.getVarName().equals("not")) {
						lhsExpr = ((ParameterizedExpr) lhsExpr).getArg(0);
					} else {
						NotExpr notExpr = new NotExpr();
						notExpr.setOperand(lhsExpr);
						lhsExpr = notExpr;
					}
					expr = new ShortCircuitOrExpr(lhsExpr, expr);
				} else if (isShortCircuitLogicalAnd(elseStmt)) {
					expr = ((AssignStmt) fTIRToMcSAFIRTable.get(elseStmt))
							.getRHS();
				}
			}
			ifStmtMap.remove(parentIf);
			try {
				ifStmtToShortCircuitExprMap.put(currIfStmt, expr.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shortCircuitIfSet.add(parentIf);
			try {
				ifStmtToShortCircuitExprMap.put(currIfStmt, expr.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currIfStmt = parentIf;
		}

		return expr;

	}

	public Set<ASTNode> getShortCircuitIfSet() {
		return shortCircuitIfSet;
	}

	public void setShortCircuitIfSet(Set<ASTNode> shortCircuitIfSet) {
		this.shortCircuitIfSet = shortCircuitIfSet;
	}

	private IfStmt getParent(Set<IfStmt> ifStmtSet, ASTNode child) {
		ASTNode actualChild = null;
		for (TIRNode node : fTIRToMcSAFIRTable.keySet()) {
			if (fTIRToMcSAFIRTable.get(node) == child) {
				actualChild = (ASTNode) node;
				break;
			}
		}
		for (IfStmt node : ifStmtSet) {
			if (isParent(node, actualChild)) {
				return node;
			}
		}
		return null;
	}

	private boolean isParent(IfStmt parent, ASTNode child) {
		for (ASTNode node : parent.getIfBlock(0).getStmtList()) {
			if (node == child) {
				return true;
			}
		}
		if (parent.hasElseBlock()) {
			for (ASTNode node : parent.getElseBlock()) {
				if (node == child) {
					return true;
				}
			}
		}
		return false;
	}

	private Map<IfStmt, TIRNode> getIfStmtMap(ArrayList<TIRNode> defSet) {
		Map<IfStmt, TIRNode> ifStmtMap = new HashMap<IfStmt, TIRNode>();
		for (TIRNode node : defSet) {
			ifStmtMap.put((IfStmt) getIfNode(node), node);
		}
		return ifStmtMap;
	}

	private boolean isShortCircuit(Expr expr) {
		return expr instanceof ShortCircuitAndExpr
				|| expr instanceof ShortCircuitOrExpr
				|| (expr instanceof ParameterizedExpr && expr.getVarName()
						.equals("and"));
	}

	private void addToExprToTempVarMap(Name variable, Expr expr) {
		if (isShortCircuit(expr)) {
			if (expr instanceof BinaryExpr) {
				if (!fExprToTempVarName.containsKey(((BinaryExpr) expr)
						.getRHS())) {
					fExprToTempVarName.put(((BinaryExpr) expr).getRHS(),
							variable);
					if (isShortCircuit(((BinaryExpr) expr).getRHS())) {
						addToExprToTempVarMap(variable,
								((BinaryExpr) expr).getRHS());
					}
				}
				if (!fExprToTempVarName.containsKey(((BinaryExpr) expr)
						.getLHS())) {
					fExprToTempVarName
							.containsKey(((BinaryExpr) expr).getLHS());
					if (isShortCircuit(((BinaryExpr) expr).getLHS())) {
						addToExprToTempVarMap(variable,
								((BinaryExpr) expr).getLHS());
					}
				}
			}
		}

	}

	private Expr getDefinitionForVariableAtNode(Name variable, TIRNode useNode) {
		Integer colorForUseNode = getColorForVariableInUseNode(variable,
				useNode);
		ArrayList<TIRNode> tirDefSet = getDefintionNode(variable,
				colorForUseNode);
		ArrayList<TIRNode> originalDefinitionNodeInTIRSet;
		if (tirDefSet.size() == 1 && !(isShortCircuit(tirDefSet))) {
			originalDefinitionNodeInTIRSet = tirDefSet;
			AssignStmt updatedDefinitionNodeInAST = (AssignStmt) fTIRToMcSAFIRTable
					.get(originalDefinitionNodeInTIRSet.get(0));
			Expr definitionExpr = updatedDefinitionNodeInAST.getRHS();
			return definitionExpr;
		} else if (isShortCircuit(tirDefSet)) {
			if (isShortCircuitAnd(tirDefSet, useNode)) {
				return getShortCircuitNode(tirDefSet, useNode);
			} else if (isShortCircuitOr(tirDefSet)) {
				// definitionExpr = new ShortCircuitOrExpr();
				return getShortCircuitNode(tirDefSet, useNode);
			} else if (isShortCircuitLogicalAnd(tirDefSet)) {
				// AssignStmt updatedDefinitionNodeInAST = (AssignStmt)
				// fTIRToMcSAFIRTable
				// .get(getLogicalAndNode(tirDefSet));
				// Expr andExpr = updatedDefinitionNodeInAST.getRHS();
				// return andExpr;
				return getShortCircuitNode(tirDefSet, useNode);
			} else {
				throw new UnsupportedOperationException(
						"Short circuit opertion can either be And or Or");
			}
			// AssignStmt updatedDefinitionNodeInAST = (AssignStmt)
			// fTIRToMcSAFIRTable
			// .get(originalDefinitionNodeInTIRSet.get(0));
			// definitionExpr.setLHS(updatedDefinitionNodeInAST.getRHS());
			// Expr rhsExpr = ((AssignStmt) fTIRToMcSAFIRTable
			// .get(originalDefinitionNodeInTIRSet.get(1))).getRHS();
			// if (definitionExpr instanceof ShortCircuitOrExpr) {
			// if (rhsExpr instanceof ParameterizedExpr
			// && rhsExpr.getVarName().equals("not")) {
			// rhsExpr = ((ParameterizedExpr) rhsExpr).getArg(0);
			// } else {
			// NotExpr tempExpr = new NotExpr();
			// tempExpr.setOperand(rhsExpr);
			// rhsExpr = tempExpr;
			// }
			// }
			// definitionExpr.setRHS(rhsExpr);
			// return definitionExpr;
		}
		return null;
	}

	private Integer getColorForVariableInUseNode(Name variable, TIRNode useNode) {
		String variableName = variable.getID();
		Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb
				.getNodeAndColorForUse(variableName);
		return nodeToColorMap.get(useNode);
	}

	private ArrayList<TIRNode> getDefintionNode(Name variable, Integer color) {
		String variableName = variable.getID();
		Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb
				.getNodeAndColorForDefinition(variableName);
		return findNodeWithColorInMap(color, nodeToColorMap);
	}

	private boolean isShortCircuit(ArrayList<TIRNode> defSet) {
		ASTNode sameifNode = null;
		boolean isSame = false;

		for (TIRNode xNode : defSet) {
			IfStmt ifNode = getParentIfStmt((ASTNode) xNode);
			if (ifNode == null) {
				return false;
			}

			if (sameifNode == null) {
				sameifNode = ifNode;
			} else if (ifNode == sameifNode) {
				isSame = true;
				// return false;
			}

		}
		// TODO: Not the best way to do it. Change.
		if (defSet.size() >= 2) {
			// shortCircuitIfStmtSet.p(sameifNode);
			return true;
		}
		if (isSame) {
			// shortCircuitIfStmtSet.add(sameifNode);
			return true;
		}
		return false;
	}

	private IfStmt getParentIfStmt(ASTNode node) {
		return NodeFinder.findParent(IfStmt.class, node);
	}

	private TIRNode replaceBoolExpr(TIRNode node) {
		IfStmt astNode = getParentIfStmt((ASTNode) node);
		if (astNode == null) {
			throw new NullPointerException("it is null");
		}
		TIRNode prev = null;
		for (TIRNode rnode : reachingDef.getVisitedStmtsOrderedList()) {
			if (fTIRToMcSAFIRTable.get(rnode) == astNode) {
				break;
			}
			prev = rnode;
		}
		if (prev == null) {
			throw new NullPointerException("will not work");
		}
		return prev;
	}

	private ASTNode getIfNode(TIRNode node) {
		if (node == null) {
			throw new NullPointerException("Node cannot be null");
		}
		return getParentIfStmt((ASTNode) node);
	}

	private ArrayList<TIRNode> getNodesWithCommonIf(ArrayList<TIRNode> defSet) {
		Map<ASTNode, TIRNode> ifNodeMap = new HashMap<ASTNode, TIRNode>();
		// for(TIRNode no)
		ArrayList<TIRNode> commonIfArrayList = new ArrayList<TIRNode>();
		for (TIRNode node : defSet) {
			ASTNode ifNode = getIfNode(node);
			if (ifNodeMap.containsKey(ifNode)) {
				commonIfArrayList.add(ifNodeMap.get(ifNode));
				commonIfArrayList.add(node);
			} else if (ifNode == null) {
				throw new NullPointerException("IfNode cannot be null");
			} else {
				ifNodeMap.put(ifNode, node);
			}
		}
		return commonIfArrayList;
	}

	private ArrayList<TIRNode> getShortCircuitArrayList(
			ArrayList<TIRNode> defSet) {
		ArrayList<TIRNode> shortCircuitArrayList = new ArrayList<TIRNode>();

		for (TIRNode origNode : defSet) {
			if (origNode instanceof TIRCallStmt
					&& (((TIRCallStmt) origNode).getRHS().getVarName()
							.equals("false") || (((TIRCallStmt) origNode)
							.getRHS().getVarName().equals("true")))) {
				shortCircuitArrayList.add(replaceBoolExpr(origNode));
			} else {
				shortCircuitArrayList.add(origNode);
			}
		}
		return shortCircuitArrayList;
	}

	private TIRNode getLogicalAndNode(ArrayList<TIRNode> defSet) {
		for (TIRNode origNode : defSet) {
			if (origNode instanceof TIRCallStmt
					&& (((TIRCallStmt) origNode).getRHS().getVarName()
							.equals("and"))) {
				return ((TIRCallStmt) origNode);
			}
		}
		return null;
	}

	private ArrayList<TIRNode> findNodeWithColorInMap(Integer color,
			Map<TIRNode, Integer> nodeToColorMap) {
		ArrayList<TIRNode> defSet = new ArrayList<TIRNode>();
		TIRNode seekedNode = null;
		for (TIRNode node : nodeToColorMap.keySet()) {
			if (nodeToColorMap.get(node).intValue() == color.intValue()) {
				seekedNode = node;
				defSet.add(node);
				// break;
			}
		}
		if (defSet.size() >= 2) {
			ASTNode ifNode = getIfNode(defSet.get(0));
		}
		return defSet;
	}

	private void addTmpIndicesToUsedVariablesNames(
			TIRCommaSeparatedList indices,
			TIRCommaSeparatedList usedVariablesNames) {
		int indicesCount = indices.size();
		for (int i = 0; i < indicesCount; i++) {
			NameExpr indexNameExpr = indices.getNameExpresion(i);
			if (indexNameExpr != null) {
				usedVariablesNames.add(indexNameExpr);
			}
		}
	}

	private boolean isTemporaryVariable(NameExpr variable) {
		return variable.getName().tmpVar;
	}

	private boolean isTemporaryVariable(Name variableName) {
		return variableName.tmpVar;
	}

	private TIRNode getFunctionNode() {
		return fUDDUWeb.getVisitedStmtsLinkedList().get(0);
	}

	private void updateRemainingVariablesNamesSet(String tmpVariableName) {
		if (fRemainingVariablesNames.contains(tmpVariableName)) {
			fRemainingVariablesNames.remove(tmpVariableName);
		}
		/*
		 * else { throw new NoSuchElementException("The variable: " +
		 * tmpVariableName + " is not defined."); }
		 */
	}

	/**
	 * Returns a bi-map of Tame IR to equivalent McSAF IR for analyzed tree and
	 * vice versa with temporary variables removed
	 * 
	 * @return bi-map, key: start TIRNode/ASTNode, value: equivalent
	 *         ASTNode/TIRNode
	 */
	public HashBiMap<TIRNode, ASTNode> getTIRToMcSAFIRTable() {
		return fTIRToMcSAFIRTable;
	}

	/**
	 * Returns a map of aggregated expression to original temporary variable
	 * 
	 * @return map - key: aggregated expression, value: replaced temporary
	 *         variable
	 */
	public HashMap<Expr, Name> getExprToTempVarTable() {
		return fExprToTempVarName;
	}

	/**
	 * Returns the set of remaining variables after expression aggregation
	 * 
	 * @return set of remaining variables
	 */
	public Set<String> getRemainingVariablesNames() {
		return fRemainingVariablesNames;
	}

	private void printTIRToMcSAFIRTable() {
		System.err
				.println("Tame IR to McSAFIR without temporaries table content:");
		for (TIRNode key : fTIRToMcSAFIRTable.keySet()) {
			ASTNode value = fTIRToMcSAFIRTable.get(key);
			printTableEntry(key, value);
		}
	}

	private void printExprToTempVarNameTable() {
		System.err
				.println("Expression to temporary variable name table content:");
		for (Entry<Expr, Name> entry : fExprToTempVarName.entrySet()) {
			System.out.println(entry.getKey().getPrettyPrinted() + " ---> "
					+ NodePrinter.printName(entry.getValue()));
		}
		System.out.println("\n");
	}

	private void printRemainingVariablesNames() {
		System.err.println("Remaining Variables Set:");
		for (String remainingVariable : fRemainingVariablesNames) {
			System.out.print(remainingVariable + ", ");
		}
		System.out.println();
	}

	private void printTableEntry(TIRNode key, ASTNode value) {
		System.out.println(NodePrinter.printNode(key) + " ---> "
				+ NodePrinter.printNode(value));
	}

}
