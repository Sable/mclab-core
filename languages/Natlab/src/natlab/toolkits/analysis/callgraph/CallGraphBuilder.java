package natlab.toolkits.analysis.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import natlab.CompilationProblem;
import natlab.toolkits.analysis.handlepropagation.HandlePropagationAnalysis;
import natlab.toolkits.analysis.handlepropagation.MayMustTreeSet;
import natlab.toolkits.analysis.handlepropagation.handlevalues.AnonymousHandleValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.NamedHandleValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.Value;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import analysis.AbstractDepthFirstAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.Function;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

/**
 *
 */
public class CallGraphBuilder
{

    protected ASTNode tree;

    protected CompilationProblem error = null;

    //maps callables(functions, scripts) to the set of call site
    //labels contained within. 
    protected HashMap<ASTNode, TreeSet<CallSiteLabel>> programLabelMap = new HashMap<ASTNode, TreeSet<CallSiteLabel>>();
    //Maps a call site node to a call site label object. Call site
    //nodes can be parametrized expressions or name expressions.
    protected HashMap<ASTNode, CallSiteLabel> labelMap = new HashMap<ASTNode, CallSiteLabel>();
    //Maps a call site label to the set of targets
    protected HashMap<CallSiteLabel, MayMustTreeSet<ASTNode>> targetMap = new HashMap<CallSiteLabel, MayMustTreeSet<ASTNode>>();
    //Maps a callable name to the callable node with that name.
    protected HashMap<String, ASTNode> programNameMap;
    //Maps a callable node to it's name
    protected HashMap<ASTNode, String> nameOfProgMap = new HashMap<ASTNode, String>();
    //Maps call site labels to the callable nodes they belong to
    protected TreeMap<CallSiteLabel, ASTNode> labelProgramMap = new TreeMap<CallSiteLabel, ASTNode>();

    protected CallGraph graph;

    protected LinkedList<ASTNode> workList = new LinkedList<ASTNode>();

    protected VFPreorderAnalysis nameResolver;
    protected HandlePropagationAnalysis handleResolver;

    public CallGraphBuilder( ASTNode tree, HashMap<String, ASTNode> programNameMap )
    {
        this.tree = tree;
        this.programNameMap = programNameMap;

        for( Map.Entry<String,ASTNode> e : programNameMap.entrySet() )
            nameOfProgMap.put( e.getValue(), e.getKey());

        nameResolver = new VFPreorderAnalysis(tree);
        nameResolver.analyze();
        handleResolver = new HandlePropagationAnalysis(tree);
        handleResolver.analyze();
    }
    public CallGraphBuilder( ASTNode tree, HashMap<String, ASTNode> programNameMap, 
                             VFPreorderAnalysis nameResolver )
    {
        this.tree = tree;
        this.programNameMap = programNameMap;
        this.nameResolver = nameResolver;
        if( !tree.equals(nameResolver.getTree()) )
            error = new CompilationProblem("Given tree inconsistent with given VFPreorder's tree");
        if( !nameResolver.isAnalyzed() )
            nameResolver.analyze();
        handleResolver = new HandlePropagationAnalysis(tree);
        handleResolver.analyze();
    }
    public CallGraphBuilder( ASTNode tree, HashMap<String, ASTNode> programNameMap, 
                             HandlePropagationAnalysis handleResolver)
    {
        this.tree = tree;
        this.programNameMap = programNameMap;
        nameResolver = new VFPreorderAnalysis(tree);
        nameResolver.analyze();
        this.handleResolver = handleResolver;
        if( !tree.equals( handleResolver.getTree() ) )
            error = new CompilationProblem("Given tree inconsistent with given HandlePropagationAnalysis's tree");
        if( !handleResolver.isAnalyzed() )
            handleResolver.analyze();
    }
    public CallGraphBuilder( ASTNode tree, HashMap<String, ASTNode> programNameMap,
                             VFPreorderAnalysis nameResolver, 
                             HandlePropagationAnalysis handleResolver)
    {
        this.tree = tree;
        this.programNameMap = programNameMap;
        this.nameResolver = nameResolver;
        if( !tree.equals(nameResolver.getTree()) )
            error = new CompilationProblem("Given tree inconsistent with given VFPreorderAnalysis's tree");
        if( !nameResolver.isAnalyzed() )
            nameResolver.analyze();
        this.handleResolver = handleResolver;
        if( !tree.equals( handleResolver.getTree() ) )
            error = new CompilationProblem("Given tree inconsistent with given HandlePropagationAnalysis's tree");
        if( !handleResolver.isAnalyzed() )
            handleResolver.analyze();
    }

    /**
     * Builds the call graph and make it available for
     * getCallGraph().
     */
    public void run()
    {
        System.out.println("running");
        workList.clear();
        workList.addAll( programNameMap.values() );
        GraphNodeBuilder nodeBuilder;
        while( !workList.isEmpty() ){
            nodeBuilder = new GraphNodeBuilder( workList.remove(0) );
            nodeBuilder.analyze();
        }
        graph = new CallGraph( programLabelMap, labelMap, targetMap, labelProgramMap );
    }
    
    /**
     * Gives the call graph generated by running the builder.
     */
    public CallGraph getCallGraph()
    {
        return graph;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        boolean first = true;
        s.append("{");
        for( Map.Entry<ASTNode,TreeSet<CallSiteLabel>> e : programLabelMap.entrySet() ){
            if( first )
                first = false;
            else
                s.append(", ");
            s.append( nameOfProgMap.get( e.getKey() ) );
            s.append("=");
            s.append( e.getValue() );
        }
        s.append("}");

        s.append("\n{");
        first = true;
        for( Map.Entry<CallSiteLabel,MayMustTreeSet<ASTNode>> e : targetMap.entrySet() ){
            if( first )
                first=false;
            else
                s.append(", ");
            s.append( e.getKey() );
            s.append("=");
            s.append( e.getValue().isMust()?"Must:":"May:");
            s.append("[");
            first = true;
            for( ASTNode n : e.getValue() ){
                if(first)
                    first=false;
                else
                    s.append(", ");
                s.append( nameOfProgMap.get( n ) );
            }
            first = false;
            s.append("]");
        }
        s.append("}");
        return s.toString();

    }
    /**
     * Generates a graph diagram specification for graphviz dot.
     */
    public String toDot( boolean deadEnds)
    {
        StringBuffer s = new StringBuffer();
        StringBuffer nodeDefs = new StringBuffer();
        StringBuffer edgeDefs = new StringBuffer();

        int deadentcounter = 0;
        s.append("digraph G{\n");
        s.append("  node [shape=record];\n");
        s.append("  rankdir=LR;\n");
        for( Map.Entry<ASTNode,TreeSet<CallSiteLabel>> e : programLabelMap.entrySet() ){
            String nname = nameOfProgMap.get( e.getKey() );
            nodeDefs.append("  "+nname + " [label=\"" + nname + " | { | {");
            boolean first = true;
            for( CallSiteLabel label : e.getValue() ){
                MayMustTreeSet<ASTNode> targetSet = targetMap.get( label );
                if( deadEnds || !targetSet.isEmpty() ){
                    if( first )
                        first = false;
                    else
                        nodeDefs.append(" | ");
                    nodeDefs.append("<"+label+"> "+label);
                }

                /*if(deadEnds && targetSet.isEmpty()){
                    edgeDefs.append("  "+nname + ":"+label+" -> x"+deadentcounter +" [arrowhead=obox];\n");
                    edgeDefs.append("  x"+deadentcounter+" [style=invis, constraint=false];\n");
                    deadentcounter++;
                    }*/
                for( ASTNode target : targetSet ){
                    edgeDefs.append("  "+nname+":"+label+" -> "+nameOfProgMap.get(target));
                    if( targetSet.isMay() )
                        edgeDefs.append("[color=red];\n");
                    else
                        edgeDefs.append(";\n");
                }
            }
            nodeDefs.append("}}\"];\n");
        }
        s.append(nodeDefs);
        s.append(edgeDefs);
        s.append("}\n");
        return s.toString();
    }
    /**
     * Builds the graph for a given script or function. It will
     * populate the maps and build up the worklist.
     */
    private class GraphNodeBuilder extends AbstractDepthFirstAnalysis<Set<String>>
    {
        GraphNodeBuilder( ASTNode tree )
        {
            super(tree);
        }

        private Stmt currentStmt = null;
        private Function currentFunction = null;
        private ASTNode currentCallable = null;
        private boolean inFunction = false;
        
        public Set<String> newInitialFlow()
        {
            return new HashSet<>();
        }

        public void caseFunction( Function node )
        {
            inFunction = true;
            for( Function f : node.getNestedFunctions() ){
                workList.add(f);
                nameOfProgMap.put( f, f.getName() );
            }
            Function previousFunction = currentFunction;
            ASTNode previousCallable = currentCallable;
            currentFunction = node;
            currentCallable = node;
            node.getStmts().analyze(this);
            currentFunction = previousFunction;
            currentCallable = previousCallable;
        }
        public void caseScript( Script node )
        {
            inFunction = false;
            currentCallable = node;
            caseASTNode( node );
        }
        public void caseStmt( Stmt node )
        {
            Stmt previousStmt = currentStmt;
            currentStmt = node;
            caseASTNode( node );
            currentStmt = previousStmt;
        }
        public void caseNameExpr( NameExpr node )
        {
            if( !inLvalues ){
                String id = node.getName().getID();
                VFDatum datum =  nameResolver.getFlowSets().get( currentStmt ).get(id);
                if( datum == null ){
                    CallSiteLabel label = CallSiteLabel.makeFunctionLabel();
                    addToLabelMaps( label, node );
                    addTargetsToMaps( label, id, false );
                    //if( inFunction )
                    System.out.println("I found a possible function call to " + id + " maybe!" );
                }
                else if( datum.isFunction() ){
                    CallSiteLabel label = CallSiteLabel.makeFunctionLabel();
                    addToLabelMaps( label, node );
                    addTargetsToMaps( label, id, true );
                    System.out.println("I found a possible function call to " +id );
                }
            }
        }

        protected boolean inLvalues = false;
        public void caseAssignStmt( AssignStmt node )
        {
            Stmt previousStmt = currentStmt;
            currentStmt = node;
            inLvalues = true;
            node.getLHS().analyze(this);
            inLvalues = false;
            node.getRHS().analyze(this);
            currentStmt = previousStmt;
        }
        
        public void caseParameterizedExpr( ParameterizedExpr node )
        {
            if( inLvalues ){
                node.getTarget().analyze(this);
                inLvalues = false;
                node.getArgs().analyze(this);
                inLvalues = true;
            }
            else{
                if( node.getTarget() instanceof NameExpr ){
                    //This is the only case currently dealt with
                    String id = ((NameExpr)node.getTarget()).getName().getID();
                    VFDatum datum = nameResolver.getFlowSets().get( currentStmt ).get(id);
                    if( datum == null ){
                        CallSiteLabel label = CallSiteLabel.makeUnknownLabel();
                        addToLabelMaps( label, node );
                        addTargetsToMaps( label, id, false );
                        System.out.println("I found a possible function call to " + id + " maybe!" );
                    }
                    else if( datum.isFunction() ){
                        CallSiteLabel label = CallSiteLabel.makeFunctionLabel();
                        addToLabelMaps( label, node );
                        addTargetsToMaps( label, id, true );
                        System.out.println("I found a possible function call to " +id );
                    }
                    else if( datum.isVariable() && 
                             handleResolver.getInFlowSets().get( currentStmt ).containsKey( id ) ){

                        CallSiteLabel label = CallSiteLabel.makeHandleLabel();
                        addToLabelMaps( label, node );

                        TreeSet<Value> handleTargets;
                        handleTargets = handleResolver.getInFlowSets().get( currentStmt ).get( id );
                        addTargetsToMaps( label, handleTargets );
                    
                        System.out.println("I found a possible fn handle call from " + id );
                    }
                        
                }
            }
        }
        public void caseCellIndexExpr( CellIndexExpr node )
        {
            if( inLvalues ){
                node.getTarget().analyze(this);
                inLvalues = false;
                node.getArgs().analyze(this);
                inLvalues = true;
            }
            else
                caseLValueExpr(node);
        }
        public void caseDotExpr( DotExpr node )
        {
            if( inLvalues ){
                node.getTarget().analyze(this);
            }
            else
                caseLValueExpr(node);
        }
        public void caseMatrixExpr( MatrixExpr node )
        {
            if( inLvalues ){
                node.getRows().analyze(this);
            }
            else
                caseLValueExpr(node);
        }

        /**
         * Adds target for a given set of handle targets to the
         * targetMap using a given label as the key.
        */ 
        private void addTargetsToMaps( CallSiteLabel label, TreeSet<Value> handleTargets )
        {
            ASTNode target = null;
            MayMustTreeSet<ASTNode> targetSet = new MayMustTreeSet<ASTNode>();
            System.out.println(handleTargets);
            for( Value ht : handleTargets ){
                if( ht instanceof NamedHandleValue ){
                    target = programNameMap.get( ((NamedHandleValue)ht).getName() );
                }
                else if( ht instanceof AnonymousHandleValue ){
                    target = ((AnonymousHandleValue)ht).getLambdaExpr();
                    nameOfProgMap.put( target, ((AnonymousHandleValue)ht).getNodeString() );
                }
                if( target==null )
                    targetSet.makeMay();
                targetSet.add( target );
            }
            //need some other way to do this
            //if( handleTargets.isMay())
            //    targetSet.makeMay();
            targetMap.put( label, targetSet );
        }
        /**
         * Adds the target for a given callable name to the targetMap
         * using a given label as the key.
         */
        private void addTargetsToMaps( CallSiteLabel label, String targetName, boolean mustBeCall )
        {
            ASTNode target;
            MayMustTreeSet<ASTNode> targetSet = new MayMustTreeSet<ASTNode>();
            if( inFunction ){
                target = currentFunction.lookupFunction( targetName );
                if( target == null )
                    target = programNameMap.get( targetName );
                if( target == null ){
                    target = new Name(targetName);
                    targetSet.add(target);
                    nameOfProgMap.put(target, targetName);

                    targetSet.makeMay();
                }
                else
                    targetSet.add( target );
            }
            else{
                target = programNameMap.get( targetName );
                if( target == null ){
                    target = new Name(targetName);
                    targetSet.add(target);
                    nameOfProgMap.put(target, targetName);
                    targetSet.makeMay();
                }
                else{
                    targetSet.add( target );
                    if( !mustBeCall )
                        targetSet.makeMay();
                }
            }
            targetMap.put(label, targetSet);
        }
        /**
         * Adds a given label for a given node to the appropriate
         * maps. This includes the labelMap and programLabelMap.
         */
        private void addToLabelMaps( CallSiteLabel label, ASTNode node ){
            labelMap.put(node, label);
            labelProgramMap.put(label, currentCallable);
            TreeSet<CallSiteLabel> labelSet;
            labelSet = programLabelMap.get(currentCallable);
                if(labelSet==null)
                    labelSet = new TreeSet<CallSiteLabel>();
            labelSet.add(label);
            programLabelMap.put(currentCallable,labelSet);
        }
        public void caseCondition( Expr condExpr )
        {
            caseASTNode( condExpr );
        }
    }
                    
}
