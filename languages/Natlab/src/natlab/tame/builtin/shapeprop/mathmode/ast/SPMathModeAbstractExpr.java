package natlab.tame.builtin.shapeprop.mathmode.ast;

import beaver.Symbol;

public abstract class SPMathModeAbstractExpr extends SPMathModeNode {
    public abstract <V> V apply(AbstractMathModeVisitor<V> visitor);

    @Override
    public abstract String toString();
}
