package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import ast.ASTNode;

public interface TamerPlusTransformation
{
    public void transform(TransformationEngine engine);
    public ASTNode<?> getTransformedTree();
}
