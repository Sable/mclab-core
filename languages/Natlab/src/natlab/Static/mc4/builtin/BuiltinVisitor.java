package natlab.Static.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
    abstract public Ret caseAbstractRoot(Builtin builtin,Arg arg);
    
    //pure functions have no side effects, always return the same value
    //if the arguments are the same
    abstract public Ret caseAbstractPureFunction(Builtin builtin,Arg arg);
    
    //constants
    abstract public Ret caseAbstractConstant(Builtin builtin,Arg arg);
    abstract public Ret caseAbstractNumericalConstant(Builtin builtin,Arg arg);
    abstract public Ret caseI(Builtin builtin,Arg arg);
    abstract public Ret caseJ(Builtin builtin,Arg arg);
    abstract public Ret casePi(Builtin builtin,Arg arg);

    
    abstract public Ret caseAbstractLogicalConstant(Builtin builtin,Arg arg);    
    abstract public Ret caseTrue(Builtin builtin,Arg arg);
    abstract public Ret caseFalse(Builtin builtin,Arg arg);
    
    
    //matlab operators
    abstract public Ret caseAbstractOperator(Builtin builtin,Arg arg);
    //binary ops
    abstract public Ret caseAbstractBinaryOperator(Builtin builtin,Arg arg);

    
    //relational ops
    abstract public Ret caseAbstractRelationalOperators(Builtin builtin, Arg arg);
    abstract public Ret caseEq(Builtin builtin,Arg arg);
    abstract public Ret caseNe(Builtin builtin,Arg arg);
    abstract public Ret caseLt(Builtin builtin,Arg arg);
    abstract public Ret caseGe(Builtin builtin,Arg arg);
    
    //logical operators - note there's no short circuit ops
    abstract public Ret caseAbstractLogicalOperator(Builtin builtin, Arg arg);
    abstract public Ret caseAbstractBinaryLogicalOperator(Builtin builtin, Arg arg);
    abstract public Ret caseAnd(Builtin builtin, Arg arg);
    abstract public Ret caseOr(Builtin builtin, Arg arg);
    abstract public Ret caseXor(Builtin builtin, Arg arg);

    abstract public Ret caseAbstractUnaryLogicalOperator(Builtin builtin, Arg arg);
    abstract public Ret caseNot(Builtin builtin, Arg arg);
    abstract public Ret caseAny(Builtin builtin, Arg arg);
    abstract public Ret caseAll(Builtin builtin, Arg arg);
    
    
    
    //numerical binary oprators
    abstract public Ret caseAbstractNumericalBinaryOperator(Builtin builtin,Arg arg);
    //matrix operators
    abstract public Ret caseAbstractMatrixOperator(Builtin builtin,Arg arg);
    abstract public Ret casePlus(Builtin builtin,Arg arg);
    abstract public Ret caseMtimes(Builtin builtin,Arg arg);
    abstract public Ret caseMpower(Builtin builtin,Arg arg);
    abstract public Ret caseMldivide(Builtin builtin,Arg arg);
    abstract public Ret caseMrdivide(Builtin builtin,Arg arg);
    //array operators
    abstract public Ret caseAbstractArrayOperator(Builtin builtin,Arg arg);
    abstract public Ret caseTimes(Builtin builtin,Arg arg);
    
    //unary operators
    abstract public Ret caseAbstractUnaryOperator(Builtin builtin,Arg arg);
    
    abstract public Ret caseAbstractNumericalUnaryOperator(Builtin builtin,Arg arg);
    abstract public Ret caseUplus(Builtin builtin,Arg arg);
    abstract public Ret caseUminus(Builtin builtin,Arg arg);
    abstract public Ret caseTranspose(Builtin builtin,Arg arg);
    abstract public Ret caseCtranspose(Builtin builtin,Arg arg);
    abstract public Ret caseConj(Builtin builtin,Arg arg);
    
    abstract public Ret caseAbstractLogicalUnaryOperator(Builtin builtin,Arg arg);
    
    
    //Matrix Operations
    abstract public Ret caseMatrixOperations(Builtin builtin,Arg arg);
    abstract public Ret caseTranscendentalFunction(Builtin builtin,Arg arg);
    abstract public Ret caseExp(Builtin builtin,Arg arg);
    abstract public Ret caseLog(Builtin builtin,Arg arg);
    
    abstract public Ret caseTrigonometricFunction(Builtin builtin,Arg arg);
    abstract public Ret caseSin(Builtin builtin,Arg arg);
    abstract public Ret caseCos(Builtin builtin,Arg arg);
    abstract public Ret caseTan(Builtin builtin,Arg arg);

    abstract public Ret caseInverseTrigonmetricFunction(Builtin builtin,Arg arg);
    abstract public Ret caseAsin(Builtin builtin,Arg arg);
    abstract public Ret caseAcos(Builtin builtin,Arg arg);
    abstract public Ret caseAtan(Builtin builtin,Arg arg);
    
    
    abstract public Ret caseReal(Builtin builtin,Arg arg);
    abstract public Ret caseImag(Builtin builtin,Arg arg);
    
    
    //should this be a builtin?
    abstract public Ret caseToeplitz(Builtin builtin,Arg arg);
    
    
    //opposite of pure functions:
    abstract public Ret caseAbstractImpureFunction(Builtin builtin,Arg arg);
    
    
}
