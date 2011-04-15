package natlab.Static.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);
    
    //pure functions have no side effects and always return the same value
    public Ret caseAbstractPureFunction(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    
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
    //spcieal operators ... not sure what to do with them
    public Ret caseColon(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseHorzcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseVertcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseNargin(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    
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
    public Ret caseLdivide(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    public Ret caseRdivide(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    
    //*** unary operators ***************************************************
    public Ret caseAbstractUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }

    public Ret caseAbstractNumericalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseUplus(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseUminus(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseTranspose(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseCtranspose(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseConj(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseReal(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseImag(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseAbs(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }

    public Ret caseAbstractLogicalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    
    //*** matrix operations ************************************************
    public Ret caseAbstractMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractElementwiseMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseSqrt(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }

    public Ret caseAbstractTranscendentalFunction(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
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

    public Ret caseAbstractRoundingOperation(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseFix(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseRound(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseFloor(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseCeil(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    
    //matrix computation algorithms
    public Ret caseAbstractMatrixComputation(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseEig(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseNorm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseRank(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    
    //bit operators
    public Ret caseAbstractBitOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseBitand(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitxor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitcmp(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitget(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitshift(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    
    //*** Arrat operations **************************************************
    public Ret caseAbstractArrayOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseSort(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }

    public Ret caseAbstractArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseOnes(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseZeros(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }

    public Ret caseAbstractArrayQueryOperation(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseMean(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseMin(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseNumel(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseSize(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseSum(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseProd(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    
    //*** opposite of pure functions *****************************************
    public Ret caseAbstractImpureFunction(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }

    public Ret caseAbstractTimeFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseClock(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }
    public Ret caseTic(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }
    public Ret caseToc(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }

    public Ret caseAbstractReportFuncgion(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseError(Builtin builtin,Arg arg){ return caseAbstractReportFuncgion(builtin,arg); }

    public Ret caseAbstractIoFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseFprintf(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    
    //*** library funcitons that are not builtins!! TODO **********************
    public Ret caseAbstractNotABuiltin(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    public Ret caseConv(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseToeplitz(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseDyaddown(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseFlipud(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
}