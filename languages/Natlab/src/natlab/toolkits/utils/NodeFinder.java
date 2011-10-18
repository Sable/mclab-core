package natlab.toolkits.utils;

import java.util.*;

import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;

public class NodeFinder{
	
	public static <T extends ASTNode> List<T> find(ASTNode n, Class<T> type){
		final Class<T> t= type;
		final List<T> res = new LinkedList<T>();
		new AbstractNodeCaseHandler() {
			public void caseASTNode(ASTNode n) {
				if (t.isInstance(n)) {
					res.add((T) n);
				}
				for (int i = 0; i < n.getNumChild(); i++) {
					caseASTNode(n.getChild(i));
				}
			}
		}.caseASTNode(n);
		return res;
	}

	public static <T extends ASTNode> T findParent(ASTNode n, Class<T> type){
		while (n!=null && (!type.isInstance(n)) ){
			n = n.getParent();
		}
		return (T) n;
	}
	
	public static <T extends ASTNode> void apply(final ASTNode n, Class<T> type, final AbstractNodeFunction<T> func){
		final Class<T> t= type;
		final List<T> res = new LinkedList<T>();
		new AbstractNodeCaseHandler() {
			public void caseASTNode(ASTNode n) {
				if (t.isInstance(n)) 
					func.apply((T)n);
				for (int i = 0; i < n.getNumChild(); i++) {
					caseASTNode(n.getChild(i));
				}
			}
		}.caseASTNode(n);
	}
}
