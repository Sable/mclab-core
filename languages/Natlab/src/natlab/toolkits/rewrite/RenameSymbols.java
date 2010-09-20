package natlab.toolkits.rewrite;

import java.util.*;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;


/**
 * This is a rewrite that will go through the AST and rename all Names,
 * according to a given Map. Any name expression in the tree whose name is a key
 * in the map will get transformed to name expression with the corresponding value
 * as a name.
 * 
 * @author ant6n
 */

public class RenameSymbols extends AbstractLocalRewrite {
	Map<String,String> map;
	
	/**
	 * Constructor taking the tree to be transformed, and the map
	 * 
	 * @param tree
	 * @param renameMap
	 */
	public RenameSymbols(ASTNode tree,Map<String, String> renameMap) {
		super(tree);
		map = renameMap;
	}
	

	//note that no rewriting of children is needed
	public void caseName(Name node) {
		if (map.containsKey(node.getID())){
			this.newNode = new TransformedNode(new Name(map.get(node.getID())));
		}
	}
	
}
