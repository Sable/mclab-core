package mclint.patterns.rewrite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mclint.patterns.Match;
import mclint.patterns.MatchHandler;
import mclint.util.AstUtil;
import mclint.util.Parsing;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.NameExpr;
import ast.Script;

/**
 * Given a pattern with metavariables %x, represents the tree given by replacing the metavariables
 * with placeholder regular variables (with mangled names) and parsing. Given a Match, 
 * it'll replace the placeholders with bound subtrees.
 * @author isbadawi
 */
public class TreeWithPlaceholders implements MatchHandler {
  private ASTNode<?> tree;
  
  private static String placeholder(String meta) {
    return String.format("INTERNAL_BINDING_%c", meta.charAt(1));
  }

  private static boolean isPlaceholder(NameExpr node) {
    return node.getName().getID().startsWith("INTERNAL_BINDING_");
  }

  private static char getMeta(NameExpr node) {
    String name = node.getName().getID();
    return name.charAt(name.length() - 1);
  }

  private void replacePlaceholder(NameExpr node, ASTNode<?> binding) {
    ASTNode<?> parent = node.getParent();
    if (binding instanceof ast.List && parent instanceof ast.List) {
      AstUtil.replaceWithContents(node, (ast.List<?>) binding);
    } else {
      AstUtil.replace(node, binding);
    }
  }

  @Override
  public void handle(final Match match) {
    NodeFinder.find(NameExpr.class, tree)
        .filter(TreeWithPlaceholders::isPlaceholder)
        .forEach(node -> replacePlaceholder(node, match.getBoundNode(getMeta(node))));
  }

  public ASTNode<?> getTree() {
    return tree;
  }

  public static TreeWithPlaceholders fromPattern(String pattern) {
    Matcher m = Pattern.compile("%[a-zA-Z]").matcher(pattern);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, placeholder(m.group()));
    }
    m.appendTail(sb);
    ASTNode<?> tree = ((Script) Parsing.string(sb.toString())).getStmtList().getChild(0);
    return new TreeWithPlaceholders(tree);
  }

  private TreeWithPlaceholders(ASTNode<?> tree) {
    this.tree = tree;
  }
}
