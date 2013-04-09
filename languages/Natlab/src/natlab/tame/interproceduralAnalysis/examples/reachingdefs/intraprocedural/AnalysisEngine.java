package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import ast.ASTNode;

import com.google.common.base.Throwables;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class AnalysisEngine
{
    private ASTNode<?> fTree;
    private ClassToInstanceMap<TamerPlusAnalysis> fAnalysisCache = MutableClassToInstanceMap.create();
    
    public static AnalysisEngine forAST(ASTNode<?> tree)
    {
        return new AnalysisEngine(tree);
    }
    
    public ASTNode<?> getAST()
    {
        return fTree;
    }
    
    private <T extends TamerPlusAnalysis> T construct(Class<T> clazz)
    {
        T analysis = null;
        try
        {
            analysis = clazz.getConstructor(ASTNode.class).newInstance(fTree);
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
        analysis.analyze(this);
        return analysis;
    }
    
    private <T extends TamerPlusAnalysis> T getOrCreate(Class<T> clazz)
    {
        if (!fAnalysisCache.containsKey(clazz))
        {
            fAnalysisCache.putInstance(clazz, construct(clazz));
        }
        return fAnalysisCache.getInstance(clazz);
    }
    
    public DefinedVariablesNameCollector getDefinedVariablesAnalysis()
    {
        return getOrCreate(DefinedVariablesNameCollector.class);
    }
    
    public UsedVariablesNameCollector getUsedVariablesAnalysis()
    {
        return getOrCreate(UsedVariablesNameCollector.class);
    }
    
    public ReachingDefinitions getReachingDefinitionsAnalysis()
    {
        return getOrCreate(ReachingDefinitions.class);
    }

    public DefiniteAssignmentAnalysis getDefiniteAssignmentAnalysis()
    {
        return getOrCreate(DefiniteAssignmentAnalysis.class);
    }
    
    public UDChain getUDChainAnalysis()
    {
        return getOrCreate(UDChain.class);
    }
    
    public DUChain getDUChainAnalysis()
    {
        return getOrCreate(DUChain.class);
    }
    
    public UDDUWeb getUDDUWebAnalysis()
    {
        return getOrCreate(UDDUWeb.class);
    }
    
    public RenameVariablesForTIRNodes getVariableRenameAnalysis()
    {
        return getOrCreate(RenameVariablesForTIRNodes.class);
    }
    
    public StmtCollapseByTmpVarRemoval getStmtCollapseByTmpVarRemovalAnalysis()
    {
        return getOrCreate(StmtCollapseByTmpVarRemoval.class);
    }
    
    public TIRToAST getTIRToASTAnalysis()
    {
        return getOrCreate(TIRToAST.class);
    }
    
    public void notifyTreeChanged() 
    {
        fAnalysisCache.clear();
    }
    
    private AnalysisEngine(ASTNode<?> tree)
    {
        fTree = tree;
    }
}
