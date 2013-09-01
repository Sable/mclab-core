package mclint.refactoring;

import java.util.List;

import mclint.MatlabProgram;
import mclint.transform.Transformer;
import natlab.refactoring.Exceptions.NameConflict;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.AstPredicates;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.Function;
import ast.FunctionList;
import ast.GlobalStmt;
import ast.Name;
import ast.Program;
import ast.Script;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

class RenameVariable extends Refactoring {
  private Name node;
  private String newName;
  private UseDefDefUseChain udduChain;
  private boolean renameGlobally;

  private List<Refactoring> globalRenames = Lists.newArrayList();

  public RenameVariable(RefactoringContext context, Name node, String newName,
      boolean renameGlobally) {
    super(context);
    this.node = node;
    this.newName = newName;
    this.udduChain = context.getMatlabProgram().analyze().getUseDefDefUseChain();
    this.renameGlobally = renameGlobally;
  }

  public RenameVariable(RefactoringContext context, Name node, String newName) {
    this(context, node, newName, true);
  }

  private boolean targetNameIsGlobal() {
    return Iterables.any(getAllDefsOfTargetName(), Predicates.instanceOf(GlobalStmt.class));
  }

  private void checkPreconditionsForFunctionOrScript(ASTNode<?> ast, MatlabProgram program) {
    Name decl = findGlobalNamed(node.getID(), ast);
    if (decl != null) {
      RefactoringContext newContext = RefactoringContext.create(program);
      Refactoring rename = new RenameVariable(newContext, decl, newName, false);
      if (!rename.checkPreconditions()) {
        addErrors(rename.getErrors());
      }
      globalRenames.add(rename);
    }
  }

  @Override
  public boolean checkPreconditions() {
    if (renameGlobally && targetNameIsGlobal()) {
      for (MatlabProgram program : context.getProject().getMatlabPrograms()) {
        Program ast = program.parse();
        if (ast instanceof Script) {
          checkPreconditionsForFunctionOrScript(ast, program);
        } else {
          for (Function f : ((FunctionList) ast).getFunctions()) {
            checkPreconditionsForFunctionOrScript(f, program);
          }
        }
      }
      return getErrors().isEmpty();
    }

    ASTNode<?> parent = getParentFunctionOrScript(node);
    Optional<Name> conflictingName = NodeFinder.find(Name.class, parent)
        .firstMatch(AstPredicates.named(newName));
    if (conflictingName.isPresent()) {
      addError(new NameConflict(conflictingName.get()));
      return false;
    }
    return true;
  }

  private static ASTNode<?> getParentFunctionOrScript(ASTNode<?> node) {
    ast.Function f = NodeFinder.findParent(ast.Function.class, node);
    if (f != null) {
      return f;
    }
    return NodeFinder.findParent(Script.class, node);
  }

  private Iterable<Def> getAllDefsOfTargetName() {
    ASTNode<?> parent = getParentFunctionOrScript(node);
    return NodeFinder.find(Def.class, parent).filter(new Predicate<Def>() {
      @Override public boolean apply(Def def) {
        return !udduChain.getDefinedNamesOf(node.getID(), def).isEmpty();
      }
    });
  }

  private static Name findGlobalNamed(final String name, ASTNode<?> tree) {
    return NodeFinder.find(Name.class, tree)
        .firstMatch(new Predicate<Name>() {
          @Override public boolean apply(Name node) {
            return node.getParent().getParent() instanceof GlobalStmt
                && node.getID().equals(name);
          }
        })
        .orNull();
  }

  @Override
  public void apply() {
    if (renameGlobally && !globalRenames.isEmpty()) {
      for (Refactoring rename : globalRenames) {
        rename.apply();
      }
      return;
    }

    Transformer transformer = context.getMatlabProgram().getBasicTransformer();
    for (Def def : getAllDefsOfTargetName()) {
      for (Name use : udduChain.getUsesOf(node.getID(), def)) {
        transformer.replace(use, new Name(newName)); 
      }
      for (Name name : udduChain.getDefinedNamesOf(node.getID(), def)) {
        transformer.replace(name, new Name(newName));
      }
    }
  }
}
