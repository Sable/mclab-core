package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import ast.ASTNode;

import com.google.common.base.Throwables;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class TransformationEngine
{
    private ASTNode<?> fTree;
    private ClassToInstanceMap<TamerPlusTransformation> fTransformationCache = MutableClassToInstanceMap.create();
    private AnalysisEngine fAnalysisEngine;
    
    public static TransformationEngine forAST(ASTNode<?> tree)
    {
        return new TransformationEngine(tree);
    }
    
    public ASTNode<?> getAST()
    {
        return fTree;
    }
    
    public AnalysisEngine getAnalysisEngine()
    {
        return fAnalysisEngine;
    }
    
    private <T extends TamerPlusTransformation> T construct(Class<T> clazz)
    {
        T transformation = null;
        try
        {
            transformation = clazz.getConstructor(ASTNode.class).newInstance(fTree);
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
        transformation.transform(this);
        return transformation;
    }
    
    private <T extends TamerPlusTransformation> T getOrCreate(Class<T> clazz)
    {
        if (!fTransformationCache.containsKey(clazz))
        {
            fTransformationCache.put(clazz, construct(clazz));
        }
        return fTransformationCache.getInstance(clazz);
    }
    
    public RenameVariablesForTIRNodes getVariableRenameTransformation()
    {
        return getOrCreate(RenameVariablesForTIRNodes.class);
    }
    
    public TIRToASTWithoutTemp getTIRToASTWithoutTemp()
    {
        return getOrCreate(TIRToASTWithoutTemp.class);
    }
    
    private TransformationEngine(ASTNode<?> tree)
    {
        fTree = tree;
        fAnalysisEngine = AnalysisEngine.forAST(fTree);
    }
}
