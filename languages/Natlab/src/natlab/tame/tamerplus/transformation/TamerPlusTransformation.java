package natlab.tame.tamerplus.transformation;

import ast.ASTNode;

public interface TamerPlusTransformation
{
    public void transform(TransformationEngine engine);
    public ASTNode<?> getTransformedTree();
}
