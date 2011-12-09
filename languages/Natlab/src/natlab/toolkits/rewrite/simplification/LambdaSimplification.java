package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TempFunctionBuilderHelper;
import natlab.toolkits.rewrite.TransformedNode;

/**
 * This seeks through functions, and finds lambda expressions.
 * 
 *   @(x1,x2,....) exp1[w1,w2,...,wn, x1,x2,...]
 * 
 * where exp1 is some expression involving argument variables x_i,
 * and workspace variables w_j, will get replaced with
 * 
 *   @(x1,x2,...) someTempFunction(w1,w2,...,wn, x1,x2,...)
 * 
 * It will also create a new function someTempFunction, which will
 * be created in the current function list:
 * 
 *   function r = someTempFunction(w1,w2,...,wn, x1,x2,...)
 *     r = exp1[w1,w2,...,wn, x1,x2,...]
 *   end
 * 
 * 
 * Limitations
 * - the new function is created with one return argument. Therefore it is 
 *   assumed that all lambdas always return one result. This is a hard
 *   limitation that is inherent to this modification.
 * - if exp1 includes calls to functions that are not accessible to a sibling
 *   function, this will result in errors - TODO fix this
 * - this can only resolve lambdas within functions, not scripts (otherwise,
 *   where to put the generated function?) - TODO
 * 
 * @author ant6n
 */


public class LambdaSimplification extends AbstractSimplification{
    FunctionOrScriptQuery query;
    HashMap<String, Function> tempFunctions = new HashMap<String,Function>();
    
    public LambdaSimplification(ASTNode<?> tree, VFPreorderAnalysis kind) {
        super(tree, kind);
        this.query = kind.getQuery();
    }
    
    public Map<String,Function> getTempFunctions(){
        return new HashMap<String,Function>(tempFunctions);
    }
    
    
    @Override
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        return Collections.emptySet();
    }
    
    @Override
    public void caseLambdaExpr(LambdaExpr node) {
        //get all variables, params
        LinkedHashSet<String> params = new LinkedHashSet<String>(),variables = new LinkedHashSet<String>();
        for (Name name : node.getInputParamList()){
            params.add(name.getID());
        }
        for (NameExpr var : node.getNameExpressions()){
            if (!params.contains(var.getName().getID()) && isVar(var)){
                variables.add(var.getName().getID());
            }
        }
        //TODO deal with special case where there are no workspace vars, and lambda body is function call
        //TODO deal with functions unreachable from siblings (nested?)

        // generate the input list - i.e. variables, params
        ast.List<Name> inputList = new ast.List<Name>();
        ast.List<Expr> inputExprList = new ast.List<Expr>();
        for (String var : variables){
            inputList.add(new Name(var));
            inputExprList.add(new NameExpr(new Name(var)));
        }
        for (String param : params){
            inputList.add(new Name(param));
            inputExprList.add(new NameExpr(new Name(param)));
        }

        //generate the function
        Expr body = node.getBody();
        String functionName = TempFunctionBuilderHelper.getFreshFunctionName(
                node, query, "lambda_",tempFunctions.keySet());
        Function function = new Function();
        TempFactory ret = TempFactory.genFreshTempFactory();
        function.setName(functionName);
        function.setOutputParamList(new ast.List<Name>().add(ret.genName()));
        function.setStmtList(new ast.List<Stmt>().add(
                new AssignStmt(ret.genNameExpr(), body)));
        function.setInputParamList(inputList);
        tempFunctions.put(functionName,function);
        
        //transform body expr
        rewrite(body);
        
        //generate the transformed node - a call
        newNode = new TransformedNode(new LambdaExpr(node.getInputParamList(),
                new ParameterizedExpr(new NameExpr(new Name(functionName)),inputExprList)));
    }
    
    @Override
    public void caseFunction(Function node) {
        rewriteChildren(node);
        for (String name : tempFunctions.keySet()){
            System.out.println("\n"+tempFunctions.get(name).getPrettyPrinted());
        }
    }
    
    @Override
    public void caseFunctionList(FunctionList node) {
        //we'll rewrite the children to get the generated lambda functions
        rewriteChildren(node);

        //add to the function list
        for (String name : tempFunctions.keySet()){
            node.addChild(tempFunctions.get(name));            
        }
    }
    
}
