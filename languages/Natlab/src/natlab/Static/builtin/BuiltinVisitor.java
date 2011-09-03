package natlab.Static.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);
    
    //pure functions have no side effects and always return the same value
    public Ret caseAbstractPureFunction(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    
    //function operates on matrizes
    public Ret caseAbstractMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //general function - operates on any matrix, sometimes with restrictions
    public Ret caseAbstractAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //constant function
    public Ret caseAbstractConstant(Builtin builtin,Arg arg){ return caseAbstractAnyMatrixFunction(builtin,arg); }
    public Ret casePi(Builtin builtin,Arg arg){ return caseAbstractConstant(builtin,arg); }
    
    //any matrix functions with fixed arity, where all arguments are numeric operands
    public Ret caseAbstractStrictAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractAnyMatrixFunction(builtin,arg); }
    
    //unary function operating on a general matrix
    public Ret caseAbstractUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractStrictAnyMatrixFunction(builtin,arg); }    
    //elemental unary function operating on a general matrix
    public Ret caseElementalUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg); }
    
    //matrix-wise unary function operating on a general matrix
    public Ret caseAbstractMatrixUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg); }
    public Ret caseTranspose(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryAnyMatrixFunction(builtin,arg); }
    public Ret caseCtranspose(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryAnyMatrixFunction(builtin,arg); }    
    //TODO take optional 2nd parameter k:
    public Ret caseTril(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryAnyMatrixFunction(builtin,arg); }
    public Ret caseTriu(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryAnyMatrixFunction(builtin,arg); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractFlexibleAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractAnyMatrixFunction(builtin,arg); }    
    //numeric function that collapses a dimension (optional second arg tells which)
    public Ret caseDimensionCollapsingAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractFlexibleAnyMatrixFunction(builtin,arg); }
    
    //- some Matlab functions don't actually coerce logicals and or chars!
    public Ret caseAbstractNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //should this be called something related to operator?
    public Ret caseAbstractStrictNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractNumericMatrixFunction(builtin,arg); }
    
    //unary numeric matrix function
    public Ret caseAbstractUnaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractStrictNumericMatrixFunction(builtin,arg); }
    
    //elemental unary numeric matrix function
    public Ret caseAbstractElementalUnaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryNumericMatrixFunction(builtin,arg); }

    public Ret caseAbstractRoundingOperation(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericMatrixFunction(builtin,arg); }    
    //fix takes logicals, the others don't
    public Ret caseFix(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseRound(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseFloor(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseCeil(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }    
    //matrix-wise unary numeric matrix function
    public Ret caseMatrixUnaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryNumericMatrixFunction(builtin,arg); }
    
    //binary numeric matrix function
    public Ret caseAbstractBinaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractStrictNumericMatrixFunction(builtin,arg); }
    
    //elemental binary numeric matrix function
    public Ret caseAbstractElementalBinaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryNumericMatrixFunction(builtin,arg); }
    
    //binary elemental arithmetic - doesn't mix integer types
    public Ret caseAbstractElemnetalBinaryArithmeticFunction(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryNumericMatrixFunction(builtin,arg); }
    public Ret casePlus(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    public Ret caseMinus(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    public Ret caseTimes(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }    
    //the following do not allow both args to be double
    public Ret caseLdivide(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    public Ret caseRdivide(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    public Ret caseMod(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    public Ret caseRem(Builtin builtin,Arg arg){ return caseAbstractElemnetalBinaryArithmeticFunction(builtin,arg); }
    
    //matrix wise binary numeric matrix function
    public Ret caseAbstractMatrixBinaryNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryNumericMatrixFunction(builtin,arg); }
    
    //binary matrix arithmetic - doesn't mix integer types
    public Ret caseAbstractMatrixBinaryArithmeticFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryNumericMatrixFunction(builtin,arg); }
    public Ret caseMtimes(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryArithmeticFunction(builtin,arg); }    
    //the following do not allow both args to be double
    public Ret caseMldivide(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryArithmeticFunction(builtin,arg); }
    public Ret caseMrdivide(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryArithmeticFunction(builtin,arg); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractFlexibleNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractNumericMatrixFunction(builtin,arg); }
    
    //numeric function that collapses a dimension (optional second arg tells which)
    public Ret caseAbstractDimensionCollapsingNumericMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractFlexibleNumericMatrixFunction(builtin,arg); }
    public Ret caseMin(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingNumericMatrixFunction(builtin,arg); }
    public Ret caseMax(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingNumericMatrixFunction(builtin,arg); }
    
    //operatoes on floating point matrizes
    public Ret caseAbstractFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //strict float functions have a fixed arity, and all operands are floats
    public Ret caseAbstractStrictFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractFloatMatrixFunction(builtin,arg); }
    
    //unary functions operating on floating point matrizes
    public Ret caseAbstractUnaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractStrictFloatMatrixFunction(builtin,arg); }
    
    //elemental unary functions operating on floating point matrizes
    public Ret caseAbstractElementalUnaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseSqrt(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseRealsqrt(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseErf(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseErfinv(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseErfc(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseErfcinv(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseGamma(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseGammainc(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseBetainc(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseGammaln(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseExp(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseLog(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseLog2(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseLog10(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }

    public Ret caseAbstractForwardTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }

    public Ret caseAbstractTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSin(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCos(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseTan(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCot(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseSec(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCsc(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractDecimalTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSind(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCosd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseTand(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCotd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseSecd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCscd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseTanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseSech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatMatrixFunction(builtin,arg); }

    public Ret caseAbstractStandardInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsin(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcos(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan2(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcot(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsec(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcsc(Builtin builtin,Arg arg){ return caseAbstractStandardInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractDecimalInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsind(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcosd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtand(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcotd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsecd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcscd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    
    //matrix-wise unary function operating on floating point matrizes
    public Ret caseAbstractMatrixUnaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryFloatMatrixFunction(builtin,arg); }
    
    //unary function operating on square floating point matrix
    public Ret caseAbstractSquareMatrixUnaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseLogm(Builtin builtin,Arg arg){ return caseAbstractSquareMatrixUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseSqrtm(Builtin builtin,Arg arg){ return caseAbstractSquareMatrixUnaryFloatMatrixFunction(builtin,arg); }
    public Ret caseExpm(Builtin builtin,Arg arg){ return caseAbstractSquareMatrixUnaryFloatMatrixFunction(builtin,arg); }
    
    //binary matrix function operaitng on float matrices
    public Ret caseAbstractBinaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractStrictFloatMatrixFunction(builtin,arg); }
    
    //matrix-wise binary funciton operating on float matrices
    public Ret caseAbstractMatrixBinaryFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryFloatMatrixFunction(builtin,arg); }
    public Ret caseHypot(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryFloatMatrixFunction(builtin,arg); }
    
    //options or different possible operands.
    public Ret caseAbstractFlexibleFloatMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractFloatMatrixFunction(builtin,arg); }
    
    //numeric function that collapses a dimension (optional second arg tells which)
    public Ret caseAbstractDimensionCollapsingFloaMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractFlexibleFloatMatrixFunction(builtin,arg); }    
    //cumsum coerces logicals to double
    public Ret caseCumsum(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingFloaMatrixFunction(builtin,arg); }
    
    //standard matrix math functions with optional arguments
    public Ret caseAbstractMatrixLibaryFunction(Builtin builtin,Arg arg){ return caseAbstractFlexibleFloatMatrixFunction(builtin,arg); }
    public Ret caseEig(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseNorm(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseCond(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseRcond(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseDet(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }    
    //has 2 operands
    public Ret caseLinsolve(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    
    //factorization functions
    public Ret caseAbstractFacotorizationFunction(Builtin builtin,Arg arg){ return caseAbstractFlexibleFloatMatrixFunction(builtin,arg); }
    public Ret caseSchur(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }    
    //ordschur is not a factorization per se, it massages schur result
    public Ret caseOrdschur(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseLu(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseChol(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseSvd(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseQr(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }    
    //bit functions - usually operates on ints or logicals, some exceptions
    public Ret caseBitMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }    
    //char functions operate on strings - is this necessary or desired?
    public Ret caseCharMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //functions that convert values to different classes
    public Ret caseAbstractConversionFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //functions that convert values to logicals
    public Ret caseAbstractConversionToLogicalFunction(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }

    public Ret caseAbstractBinaryConversionToLogicalFunction(Builtin builtin,Arg arg){ return caseAbstractConversionToLogicalFunction(builtin,arg); }
    
    //elemental binary functions resulting in logicals
    public Ret caseAbstractElementalBinaryConversionToLogicalFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryConversionToLogicalFunction(builtin,arg); }
    
    //relational operators
    public Ret caseAbstractRelationalOperator(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryConversionToLogicalFunction(builtin,arg); }
    public Ret caseEq(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseNe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    
    //logical operators
    public Ret caseAbstractLogicalOperator(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryConversionToLogicalFunction(builtin,arg); }
    public Ret caseAnd(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }
    public Ret caseOr(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }
    public Ret caseXor(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }    
    //constructors
    public Ret caseConstructor(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }
    
    //functions that return scalar or vector values describing matrizes
    public Ret caseAbstractArrayQuery(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //return numericals
    public Ret caseAbstractDoubleResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    
    //scalar numerical
    public Ret caseAbstractScalarDoubleResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractDoubleResultArrayQuery(builtin,arg); }
    public Ret caseNnz(Builtin builtin,Arg arg){ return caseAbstractScalarDoubleResultArrayQuery(builtin,arg); }
    
    //return logicals
    public Ret caseAbstractLogicalResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    
    //return scalar doubles
    public Ret caseAbstractScalarLogicalResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractLogicalResultArrayQuery(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalResultArrayQuery(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalResultArrayQuery(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalResultArrayQuery(builtin,arg); }    
    //function operates on cell arrays
    public Ret caseCellFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }    
    //function operates on structures
    public Ret caseStructFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }    
    //function operates on objects
    public Ret caseObjectFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //function operates on a mixtures of matrizes, cell arrays, structures and/or objects
    public Ret caseAbstractVersatileFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }    
    //takes in a matrix or cells of strings
    public Ret caseMatrixOrCellOfStringsFunction(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    
    //*** opposite of pure functions *****************************************
    public Ret caseAbstractImpureFunction(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }    
    //dunnno what to do with these
    public Ret caseSuperiorto(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }    
    //set superior to relationship in oldclass constructor
    public Ret caseExit(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseQuit(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    
    //only really need one of quit/exit
    public Ret caseAbstractBuiltin(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    
    //calls builtin - strict
    public Ret caseAbstractTimeFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseClock(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }
    public Ret caseTic(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }
    public Ret caseToc(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }
    public Ret caseCputime(Builtin builtin,Arg arg){ return caseAbstractTimeFunction(builtin,arg); }

    public Ret caseAbstractMatlabSystemFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseAssert(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret caseNargoutchk(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret caseNargchk(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret caseStr2func(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret casePause(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }

    public Ret caseAbstractDynamicMatlabFunction(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret caseEval(Builtin builtin,Arg arg){ return caseAbstractDynamicMatlabFunction(builtin,arg); }
    public Ret caseEvalin(Builtin builtin,Arg arg){ return caseAbstractDynamicMatlabFunction(builtin,arg); }
    public Ret caseFeval(Builtin builtin,Arg arg){ return caseAbstractDynamicMatlabFunction(builtin,arg); }
    public Ret caseAssignin(Builtin builtin,Arg arg){ return caseAbstractDynamicMatlabFunction(builtin,arg); }
    public Ret caseInputname(Builtin builtin,Arg arg){ return caseAbstractDynamicMatlabFunction(builtin,arg); }

    public Ret caseAbstractMatlabEnvironmentFunction(Builtin builtin,Arg arg){ return caseAbstractMatlabSystemFunction(builtin,arg); }
    public Ret caseImport(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseCd(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseExist(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseMatlabroot(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseWhos(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseWhich(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseVersion(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseClear(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }

    public Ret caseAbstractReportFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }    
    //errors should be seprated from warnings and displaying stuff?
    public Ret caseDisp(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseDisplay(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseClc(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseError(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseWarning(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseEcho(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //should this be here? - this could be a strict lib function?
    public Ret caseDiary(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //something logging related
    public Ret caseLastwarn(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseLasterror(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseFormat(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }

    public Ret caseAbstractRandomFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseRand(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandi(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandn(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }

    public Ret caseAbstractSystemFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }    
    //should there be a separated operating system category?
    public Ret caseComputer(Builtin builtin,Arg arg){ return caseAbstractSystemFunction(builtin,arg); }
    public Ret caseBeep(Builtin builtin,Arg arg){ return caseAbstractSystemFunction(builtin,arg); }
    public Ret caseDir(Builtin builtin,Arg arg){ return caseAbstractSystemFunction(builtin,arg); }

    public Ret caseAbstractOperatingSystemCallFunction(Builtin builtin,Arg arg){ return caseAbstractSystemFunction(builtin,arg); }
    public Ret caseUnix(Builtin builtin,Arg arg){ return caseAbstractOperatingSystemCallFunction(builtin,arg); }
    public Ret caseDos(Builtin builtin,Arg arg){ return caseAbstractOperatingSystemCallFunction(builtin,arg); }
    public Ret caseSystem(Builtin builtin,Arg arg){ return caseAbstractOperatingSystemCallFunction(builtin,arg); }

    public Ret caseAbstractIoFunction(Builtin builtin,Arg arg){ return caseAbstractSystemFunction(builtin,arg); }
    public Ret caseLoad(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseSave(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseInput(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseTextscan(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }

    public Ret caseAbstractPosixIoFunction(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseSprintf(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseSscanf(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFprintf(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFtell(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFerror(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFopen(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFread(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFrewind(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFscanf(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFseek(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFwrite(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFgetl(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFgets(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
    public Ret caseFclose(Builtin builtin,Arg arg){ return caseAbstractPosixIoFunction(builtin,arg); }
}