package natlab.Static.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);
    
    //the overal parent class
    public Ret caseAbstractBuiltin(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    
    //pure functions have no side effects and always return the same value
    public Ret caseAbstractPureFunction(Builtin builtin,Arg arg){ return caseAbstractBuiltin(builtin,arg); }
    
    //*** constants **************************************************
    public Ret caseAbstractConstant(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractNumericalConstant(Builtin builtin,Arg arg){ return caseAbstractConstant(builtin,arg); }
    public Ret caseI(Builtin builtin,Arg arg){ return caseAbstractNumericalConstant(builtin,arg); }
    public Ret caseJ(Builtin builtin,Arg arg){ return caseAbstractNumericalConstant(builtin,arg); }
    public Ret casePi(Builtin builtin,Arg arg){ return caseAbstractNumericalConstant(builtin,arg); }

    public Ret caseAbstractLogicalConstant(Builtin builtin,Arg arg){ return caseAbstractConstant(builtin,arg); }
    public Ret caseTrue(Builtin builtin,Arg arg){ return caseAbstractLogicalConstant(builtin,arg); }
    public Ret caseFalse(Builtin builtin,Arg arg){ return caseAbstractLogicalConstant(builtin,arg); }
    
    //matlab operators
    public Ret caseAbstractOperator(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //*** binary ops *************************************************
    public Ret caseAbstractBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    
    //relational ops
    public Ret caseAbstractRelationalOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    public Ret caseEq(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseNe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    
    //logical operators - note there's no short circuit ops
    public Ret caseAbstractBinaryLogicalOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    public Ret caseAnd(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    public Ret caseOr(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    public Ret caseXor(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    
    //numerical binary oprators
    public Ret caseAbstractNumericalBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    
    //matrix operators
    public Ret caseAbstractMatrixOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret casePlus(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMtimes(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMpower(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMldivide(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMrdivide(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    
    //array operators
    public Ret caseAbstractArrayOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret caseTimes(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    
    //*** unary operators ***************************************************
    public Ret caseAbstractUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }

    public Ret caseAbstractNumericalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseUplus(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseUminus(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseTranspose(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseCtranspose(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseConj(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }

    public Ret caseAbstractLogicalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    
    //*** matrix operations ************************************************
    public Ret caseAbstractMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractTranscendentalFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseExp(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseLog(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }

    public Ret caseAbstractTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseSin(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCos(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseTan(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseAsin(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcos(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseReal(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseImag(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }    
    //should this be a builtin?
    public Ret caseToeplitz(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }    
    //*** opposite of pure functions *****************************************
    public Ret caseImpureFunction(Builtin builtin,Arg arg){ return caseAbstractBuiltin(builtin,arg); }
}