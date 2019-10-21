package natlab.tame.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);
    
    //- create a way of just returning the dominant arg, where a list of dominant args is given
    public Ret caseAbstractRoot(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    
    //pure functions have no side effects and always return the same value, depending only on arguments
    public Ret caseAbstractPureFunction(Builtin builtin,Arg arg){ return caseAbstractRoot(builtin,arg); }
    
    //function operates on matrizes (numerical,char,logical)
    public Ret caseAbstractMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //constant function - there are few of these, most 'constants' take optional shape args
    public Ret caseAbstractConstant(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }

    public Ret caseAbstractDoubleConstant(Builtin builtin,Arg arg){ return caseAbstractConstant(builtin,arg); }
    public Ret casePi(Builtin builtin,Arg arg){ return caseAbstractDoubleConstant(builtin,arg); }
    public Ret caseI(Builtin builtin,Arg arg){ return caseAbstractDoubleConstant(builtin,arg); }
    public Ret caseJ(Builtin builtin,Arg arg){ return caseAbstractDoubleConstant(builtin,arg); }
    
    //general function - operates on any matrix, sometimes with restrictions
    public Ret caseAbstractAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //any matrix functions with fixed arity, where all arguments are numeric operands
    public Ret caseAbstractProperAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractAnyMatrixFunction(builtin,arg); }
    
    //unary function operating on a general matrix
    public Ret caseAbstractUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractProperAnyMatrixFunction(builtin,arg); }
    
    //elemental unary function operating on a general matrix
    public Ret caseElementalUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg); }
    
    //matrix-wise unary function operating on a general matrix
    public Ret caseArrayUnaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg); }
    
    //binary function operating on a general matrix
    public Ret caseAbstractBinaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractProperAnyMatrixFunction(builtin,arg); }
    
    //elemental binary function operating on a general matrix
    public Ret caseElementalBinaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryAnyMatrixFunction(builtin,arg); }
    
    //matrix-wise unary function operating on a general matrix
    public Ret caseArrayBinaryAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryAnyMatrixFunction(builtin,arg); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractImproperAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractAnyMatrixFunction(builtin,arg); }

    public Ret caseAbstractDiagonalSensitive(Builtin builtin,Arg arg){ return caseAbstractImproperAnyMatrixFunction(builtin,arg); }
    public Ret caseTril(Builtin builtin,Arg arg){ return caseAbstractDiagonalSensitive(builtin,arg); }
    public Ret caseTriu(Builtin builtin,Arg arg){ return caseAbstractDiagonalSensitive(builtin,arg); }
    public Ret caseDiag(Builtin builtin,Arg arg){ return caseAbstractDiagonalSensitive(builtin,arg); }
    
    //functions of the form f(matrix,[dimension]), where matrix is any matrix
    public Ret caseAbstractDimensionSensitiveAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractImproperAnyMatrixFunction(builtin,arg); }
    
    //functions that collapse the specified dimension
    public Ret caseDimensionCollapsingAnyMatrixFunction(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveAnyMatrixFunction(builtin,arg); }
    
    //- some actual Matlab functions don't actually coerce logicals and or chars!
    public Ret caseAbstractNumericFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //should this be called something related to operator?
    public Ret caseAbstractProperNumericFunction(Builtin builtin,Arg arg){ return caseAbstractNumericFunction(builtin,arg); }
    
    //unary numeric function
    public Ret caseAbstractUnaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractProperNumericFunction(builtin,arg); }

    public Ret caseAbstractElementalUnaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryNumericFunction(builtin,arg); }
    public Ret caseReal(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseImag(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseAbs(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseConj(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseSign(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }

    public Ret caseAbstractElementalUnaryArithmetic(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseUplus(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryArithmetic(builtin,arg); }
    public Ret caseUminus(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryArithmetic(builtin,arg); }

    public Ret caseAbstractRoundingOperation(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryNumericFunction(builtin,arg); }
    public Ret caseFix(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseRound(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseFloor(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseCeil(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }

    public Ret caseAbstractArrayUnaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryNumericFunction(builtin,arg); }

    public Ret caseArrayUnaryArithmetic(Builtin builtin,Arg arg){ return caseAbstractArrayUnaryNumericFunction(builtin,arg); }
    
    //binary numeric function - doesn't mix integers
    public Ret caseAbstractBinaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractProperNumericFunction(builtin,arg); }
    
    //elemental binary numeric matrix function
    public Ret caseAbstractElementalBinaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryNumericFunction(builtin,arg); }    
    //imporoper? - supports 1 arg
    public Ret caseComplex(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryNumericFunction(builtin,arg); }
    
    //elemewise binary arithmetic
    public Ret caseAbstractElementalBinaryArithmetic(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryNumericFunction(builtin,arg); }
    public Ret casePlus(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryArithmetic(builtin,arg); }
    public Ret caseMinus(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryArithmetic(builtin,arg); }
    public Ret caseTimes(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryArithmetic(builtin,arg); }    
    //integers can only be combined with scalar doubles
    public Ret casePower(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryArithmetic(builtin,arg); }

    public Ret caseAbstractDividingElementalArithmetic(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryArithmetic(builtin,arg); }    
    //the following do not allow both args to be double under certain circumstances (...)
    public Ret caseLdivide(Builtin builtin,Arg arg){ return caseAbstractDividingElementalArithmetic(builtin,arg); }
    public Ret caseRdivide(Builtin builtin,Arg arg){ return caseAbstractDividingElementalArithmetic(builtin,arg); }
    public Ret caseMod(Builtin builtin,Arg arg){ return caseAbstractDividingElementalArithmetic(builtin,arg); }
    public Ret caseRem(Builtin builtin,Arg arg){ return caseAbstractDividingElementalArithmetic(builtin,arg); }
    
    //array wise binary numeric matrix function
    public Ret caseAbstractArrayBinaryNumericFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryNumericFunction(builtin,arg); }    
    //TODO@Vineet confirm if the output has to be real ?
    public Ret caseCross(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryNumericFunction(builtin,arg); }
    
    //binary matrix arithmetic - doesn't mix integer types
    public Ret caseAbstractArrayBinaryArithmetic(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryNumericFunction(builtin,arg); }
    public Ret caseMtimes(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryArithmetic(builtin,arg); }    
    //integers can only be comibned with scalar doubles -- or not?
    public Ret caseMpower(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryArithmetic(builtin,arg); }

    public Ret caseAbstractDividingArrayArithmetic(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryArithmetic(builtin,arg); }
    public Ret caseMldivide(Builtin builtin,Arg arg){ return caseAbstractDividingArrayArithmetic(builtin,arg); }
    public Ret caseMrdivide(Builtin builtin,Arg arg){ return caseAbstractDividingArrayArithmetic(builtin,arg); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractImproperNumericFunction(Builtin builtin,Arg arg){ return caseAbstractNumericFunction(builtin,arg); }
    
    //functions of the form f(matrix1,matrix2,...,matrixk,[dimension]), where matrix is any matrix
    public Ret caseAbstractDimensionSensitiveNumericFunction(Builtin builtin,Arg arg){ return caseAbstractImproperNumericFunction(builtin,arg); }    
    //TODO dot results in floats - but has the same input constraints as numeric - different category? not a builtin?
    public Ret caseDot(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveNumericFunction(builtin,arg); }
    
    //functions that collapse the specified dimension
    public Ret caseAbstractDimensionCollapsingNumericFunction(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveNumericFunction(builtin,arg); }

    public Ret caseAbstractMinOrMax(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingNumericFunction(builtin,arg); }
    public Ret caseMin(Builtin builtin,Arg arg){ return caseAbstractMinOrMax(builtin,arg); }
    public Ret caseMax(Builtin builtin,Arg arg){ return caseAbstractMinOrMax(builtin,arg); }    
    //median does not support logical matrizes
    public Ret caseMedian(Builtin builtin,Arg arg){ return caseAbstractMinOrMax(builtin,arg); }
    
    //operatoes on floating point matrizes
    public Ret caseAbstractFloatFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //proper float functions have a fixed arity, and all operands are floats
    public Ret caseAbstractProperFloatFunction(Builtin builtin,Arg arg){ return caseAbstractFloatFunction(builtin,arg); }
    
    //unary functions operating on floating point matrizes
    public Ret caseAbstractUnaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractProperFloatFunction(builtin,arg); }
    
    //elemental unary functions operating on floating point matrizes
    public Ret caseAbstractElementalUnaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryFloatFunction(builtin,arg); }
    public Ret caseSqrt(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseRealsqrt(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseErf(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseErfinv(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseErfc(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseErfcinv(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseGamma(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseGammaln(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseExp(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseLog(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseLog2(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }
    public Ret caseLog10(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }

    public Ret caseAbstractForwardTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }

    public Ret caseAbstractRadianTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSin(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }
    public Ret caseCos(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }
    public Ret caseTan(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }
    public Ret caseCot(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }
    public Ret caseSec(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }
    public Ret caseCsc(Builtin builtin,Arg arg){ return caseAbstractRadianTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractDegreeTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSind(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }
    public Ret caseCosd(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }
    public Ret caseTand(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }
    public Ret caseCotd(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }
    public Ret caseSecd(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }
    public Ret caseCscd(Builtin builtin,Arg arg){ return caseAbstractDegreeTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractForwardTrigonometricFunction(builtin,arg); }
    public Ret caseSinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseTanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseSech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryFloatFunction(builtin,arg); }

    public Ret caseAbstractRadianInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsin(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcos(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcot(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsec(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcsc(Builtin builtin,Arg arg){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractDegreeInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsind(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcosd(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtand(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcotd(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsecd(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcscd(Builtin builtin,Arg arg){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg); }
    
    //matrix-wise unary function operating on floating point matrizes
    public Ret caseAbstractArrayUnaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractUnaryFloatFunction(builtin,arg); }
    
    //unary function operating on square floating point matrix
    public Ret caseAbstractSquareArrayUnaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractArrayUnaryFloatFunction(builtin,arg); }
    public Ret caseLogm(Builtin builtin,Arg arg){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg); }
    public Ret caseSqrtm(Builtin builtin,Arg arg){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg); }
    public Ret caseExpm(Builtin builtin,Arg arg){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg); }
    public Ret caseInv(Builtin builtin,Arg arg){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg); }
    
    //binary matrix function operaitng on float matrices
    public Ret caseAbstractBinaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractProperFloatFunction(builtin,arg); }
    
    //elemental binary functino operating on floats matrizes
    public Ret caseAbstractElementalBinaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryFloatFunction(builtin,arg); }
    public Ret caseAtan2(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryFloatFunction(builtin,arg); }
    
    //matrix-wise binary funciton operating on float matrices
    public Ret caseAbstractArrayBinaryFloatFunction(Builtin builtin,Arg arg){ return caseAbstractBinaryFloatFunction(builtin,arg); }
    public Ret caseHypot(Builtin builtin,Arg arg){ return caseAbstractArrayBinaryFloatFunction(builtin,arg); }
    
    //options or different possible operands.
    public Ret caseAbstractImproperFloatFunction(Builtin builtin,Arg arg){ return caseAbstractFloatFunction(builtin,arg); }
    public Ret caseEps(Builtin builtin,Arg arg){ return caseAbstractImproperFloatFunction(builtin,arg); }
    
    //functions of the form f(matrix,[dimension])
    public Ret caseAbstractDimensionSensitiveFloatFunction(Builtin builtin,Arg arg){ return caseAbstractImproperFloatFunction(builtin,arg); }    
    //cumsum coerces logicals to double
    public Ret caseCumsum(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg); }
    public Ret caseCumprod(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg); }
    
    //numeric function that collapses a dimension (optional second arg tells which)
    public Ret caseAbstractDimensionCollapsingFloatFunction(Builtin builtin,Arg arg){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg); }
    public Ret caseMode(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg); }
    public Ret caseProd(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg); }    
    //TODO - sum takes a possible string parameter
    public Ret caseSum(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg); }
    public Ret caseMean(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg); }
    
    //standard matrix math functions with optional arguments
    public Ret caseAbstractMatrixLibaryFunction(Builtin builtin,Arg arg){ return caseAbstractImproperFloatFunction(builtin,arg); }
    public Ret caseEig(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseNorm(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }    
    //query?
    public Ret caseRank(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }    
    //optional 2 args
    public Ret caseCond(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }    
    //square float -> salar double - should probably be proper
    public Ret caseDet(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    public Ret caseRcond(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }    
    //has 2 operands
    public Ret caseLinsolve(Builtin builtin,Arg arg){ return caseAbstractMatrixLibaryFunction(builtin,arg); }
    
    //factorization functions
    public Ret caseAbstractFacotorizationFunction(Builtin builtin,Arg arg){ return caseAbstractImproperFloatFunction(builtin,arg); }
    public Ret caseSchur(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }    
    //ordschur is not a factorization per se, it massages schur result
    public Ret caseOrdschur(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseLu(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseChol(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseSvd(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    public Ret caseQr(Builtin builtin,Arg arg){ return caseAbstractFacotorizationFunction(builtin,arg); }
    
    //bit functions - usually operates on ints or logicals, some exceptions
    public Ret caseAbstractBitFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }

    public Ret caseAbstractProperBitFunction(Builtin builtin,Arg arg){ return caseAbstractBitFunction(builtin,arg); }
    public Ret caseBitand(Builtin builtin,Arg arg){ return caseAbstractProperBitFunction(builtin,arg); }
    public Ret caseBitor(Builtin builtin,Arg arg){ return caseAbstractProperBitFunction(builtin,arg); }
    public Ret caseBitxor(Builtin builtin,Arg arg){ return caseAbstractProperBitFunction(builtin,arg); }

    public Ret caseAbstractImproperBitFunciton(Builtin builtin,Arg arg){ return caseAbstractBitFunction(builtin,arg); }
    public Ret caseBitcmp(Builtin builtin,Arg arg){ return caseAbstractImproperBitFunciton(builtin,arg); }
    public Ret caseBitset(Builtin builtin,Arg arg){ return caseAbstractImproperBitFunciton(builtin,arg); }
    public Ret caseBitget(Builtin builtin,Arg arg){ return caseAbstractImproperBitFunciton(builtin,arg); }
    public Ret caseBitshift(Builtin builtin,Arg arg){ return caseAbstractImproperBitFunciton(builtin,arg); }
    
    //TODO impure/pure unary for scalar?
    public Ret caseAbstractMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    
    //return doubles
    public Ret caseAbstractToDoubleMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractMatrixQuery(builtin,arg); }
    public Ret caseFind(Builtin builtin,Arg arg){ return caseAbstractToDoubleMatrixQuery(builtin,arg); }

    public Ret caseAbstractUnaryToScalarDoubleMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractToDoubleMatrixQuery(builtin,arg); }

    public Ret caseAbstractToScalarDoubleMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractUnaryToScalarDoubleMatrixQuery(builtin,arg); }
    public Ret caseNnz(Builtin builtin,Arg arg){ return caseAbstractToScalarDoubleMatrixQuery(builtin,arg); }
    
    //return logicals
    public Ret caseAbstractToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractMatrixQuery(builtin,arg); }
    
    //unary return logicals
    public Ret caseAbstractUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractToLogicalMatrixQuery(builtin,arg); }

    public Ret caseAbstractScalarUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseIsreal(Builtin builtin,Arg arg){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg); }

    public Ret caseAbstractElementalUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseIsinf(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseIsfinite(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseIsnan(Builtin builtin,Arg arg){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg); }
    
    //binary return logicals
    public Ret caseAbstractBinaryToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractToLogicalMatrixQuery(builtin,arg); }

    public Ret caseAbstractElementalBinaryToLogicalMatrixQuery(Builtin builtin,Arg arg){ return caseAbstractBinaryToLogicalMatrixQuery(builtin,arg); }
    
    //relational operators
    public Ret caseAbstractRelationalOperator(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseEq(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseNe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    
    //logical operators
    public Ret caseAbstractLogicalOperator(Builtin builtin,Arg arg){ return caseAbstractElementalBinaryToLogicalMatrixQuery(builtin,arg); }
    public Ret caseAnd(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }
    public Ret caseOr(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }
    public Ret caseXor(Builtin builtin,Arg arg){ return caseAbstractLogicalOperator(builtin,arg); }
    
    //functions that create matrix arrays from vectors or values describing the matrizes
    public Ret caseAbstractMatrixCreation(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    public Ret caseColon(Builtin builtin,Arg arg){ return caseAbstractMatrixCreation(builtin,arg); }
    
    //construct arrays via their dimensions
    public Ret caseAbstractByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg){ return caseAbstractMatrixCreation(builtin,arg); }

    public Ret caseAbstractNumericalByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg){ return caseAbstractByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseOnes(Builtin builtin,Arg arg){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseZeros(Builtin builtin,Arg arg){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg); }    
    //Xu add this magic @4:15pm 6th Jan 2013
    public Ret caseMagic(Builtin builtin,Arg arg){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg); }    
    //eye takes at most 2 dims
    public Ret caseEye(Builtin builtin,Arg arg){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg); }

    public Ret caseAbstractFloatByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseInf(Builtin builtin,Arg arg){ return caseAbstractFloatByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseNan(Builtin builtin,Arg arg){ return caseAbstractFloatByShapeAndTypeMatrixCreation(builtin,arg); }
    
    //there is no optional type argument
    public Ret caseAbstractLogicalByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg){ return caseAbstractByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseTrue(Builtin builtin,Arg arg){ return caseAbstractLogicalByShapeAndTypeMatrixCreation(builtin,arg); }
    public Ret caseFalse(Builtin builtin,Arg arg){ return caseAbstractLogicalByShapeAndTypeMatrixCreation(builtin,arg); }
    
    //matrix constructors
    public Ret caseAbstractMatrixConstructor(Builtin builtin,Arg arg){ return caseAbstractMatrixFunction(builtin,arg); }
    public Ret caseDouble(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseSingle(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseChar(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseLogical(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseInt8(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseInt16(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseInt32(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseInt64(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseUint8(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseUint16(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseUint32(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    public Ret caseUint64(Builtin builtin,Arg arg){ return caseAbstractMatrixConstructor(builtin,arg); }
    
    //function operates on cell arrays
    public Ret caseAbstractCellFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseCell(Builtin builtin,Arg arg){ return caseAbstractCellFunction(builtin,arg); }

    public Ret caseAbstractCellCat(Builtin builtin,Arg arg){ return caseAbstractCellFunction(builtin,arg); }
    public Ret caseCellhorzcat(Builtin builtin,Arg arg){ return caseAbstractCellCat(builtin,arg); }
    public Ret caseCellvertcat(Builtin builtin,Arg arg){ return caseAbstractCellCat(builtin,arg); }
    
    //function operates on structures
    public Ret caseAbstractStructFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseIsfield(Builtin builtin,Arg arg){ return caseAbstractStructFunction(builtin,arg); }
    public Ret caseStruct(Builtin builtin,Arg arg){ return caseAbstractStructFunction(builtin,arg); }    
    //function operates on objects
    public Ret caseObjectFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //function operates on a mixtures of matrizes, cell arrays, structures and/or objects
    public Ret caseAbstractVersatileFunction(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    
    //takes in a matrix or cells of strings
    public Ret caseAbstractMatrixOrCellOfCharFunction(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    public Ret caseSort(Builtin builtin,Arg arg){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg); }
    public Ret caseUnique(Builtin builtin,Arg arg){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg); }
    
    //TODO should it be called string function?
    public Ret caseAbstractCharFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg); }

    public Ret caseAbstractProperCharFunction(Builtin builtin,Arg arg){ return caseAbstractCharFunction(builtin,arg); }

    public Ret caseAbstractUnaryProperCharFunction(Builtin builtin,Arg arg){ return caseAbstractProperCharFunction(builtin,arg); }
    public Ret caseUpper(Builtin builtin,Arg arg){ return caseAbstractUnaryProperCharFunction(builtin,arg); }
    public Ret caseLower(Builtin builtin,Arg arg){ return caseAbstractUnaryProperCharFunction(builtin,arg); }
    public Ret caseDeblank(Builtin builtin,Arg arg){ return caseAbstractUnaryProperCharFunction(builtin,arg); }    
    //strip
    public Ret caseStrtrim(Builtin builtin,Arg arg){ return caseAbstractUnaryProperCharFunction(builtin,arg); }

    public Ret caseAbstractImproperCharFunction(Builtin builtin,Arg arg){ return caseAbstractCharFunction(builtin,arg); }
    public Ret caseStrfind(Builtin builtin,Arg arg){ return caseAbstractImproperCharFunction(builtin,arg); }
    public Ret caseFindstr(Builtin builtin,Arg arg){ return caseAbstractImproperCharFunction(builtin,arg); }
    public Ret caseStrrep(Builtin builtin,Arg arg){ return caseAbstractImproperCharFunction(builtin,arg); }
    
    //str[n]cmp[i]
    public Ret caseAbstractStringCompare(Builtin builtin,Arg arg){ return caseAbstractImproperCharFunction(builtin,arg); }
    public Ret caseStrcmp(Builtin builtin,Arg arg){ return caseAbstractStringCompare(builtin,arg); }
    public Ret caseStrcmpi(Builtin builtin,Arg arg){ return caseAbstractStringCompare(builtin,arg); }
    public Ret caseStrncmpi(Builtin builtin,Arg arg){ return caseAbstractStringCompare(builtin,arg); }
    public Ret caseStrncmp(Builtin builtin,Arg arg){ return caseAbstractStringCompare(builtin,arg); }
    
    //regular expression functions
    public Ret caseAbstractRegexpFunction(Builtin builtin,Arg arg){ return caseAbstractImproperCharFunction(builtin,arg); }
    public Ret caseRegexptranslate(Builtin builtin,Arg arg){ return caseAbstractRegexpFunction(builtin,arg); }
    public Ret caseRegexp(Builtin builtin,Arg arg){ return caseAbstractRegexpFunction(builtin,arg); }
    public Ret caseRegexpi(Builtin builtin,Arg arg){ return caseAbstractRegexpFunction(builtin,arg); }
    public Ret caseRegexprep(Builtin builtin,Arg arg){ return caseAbstractRegexpFunction(builtin,arg); }
    
    //query functions that operate not just on matrizes (c.f. matrixQueryFunction)
    public Ret caseAbstractVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    public Ret caseClass(Builtin builtin,Arg arg){ return caseAbstractVersatileQuery(builtin,arg); }
    
    //return numericals
    public Ret caseAbstractDoubleResultVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractVersatileQuery(builtin,arg); }    
    //TODO fix this output?!
    public Ret caseSize(Builtin builtin,Arg arg){ return caseAbstractDoubleResultVersatileQuery(builtin,arg); }
    
    //scalar numerical
    public Ret caseAbstractScalarDoubleResultVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractDoubleResultVersatileQuery(builtin,arg); }
    public Ret caseLength(Builtin builtin,Arg arg){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg); }
    public Ret caseNdims(Builtin builtin,Arg arg){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg); }
    public Ret caseNumel(Builtin builtin,Arg arg){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg); }
    public Ret caseEnd(Builtin builtin,Arg arg){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg); }
    
    //return logicals
    public Ret caseAbstractLogicalResultVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractVersatileQuery(builtin,arg); }
    
    //return scalar logicals
    public Ret caseAbstractScalarLogicalResultVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractLogicalResultVersatileQuery(builtin,arg); }

    public Ret caseAbstractClassQuery(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalResultVersatileQuery(builtin,arg); }
    public Ret caseIsobject(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIsfloat(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIsinteger(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIslogical(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIsstruct(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIschar(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIscell(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }
    public Ret caseIsnumeric(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }    
    //TODO impure?
    public Ret caseIsa(Builtin builtin,Arg arg){ return caseAbstractClassQuery(builtin,arg); }

    public Ret caseAbstractScalarLogicalShapeQuery(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalResultVersatileQuery(builtin,arg); }
    public Ret caseIsempty(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }
    public Ret caseIsvector(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }
    public Ret caseIsscalar(Builtin builtin,Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }

    public Ret caseAbstractMultiaryToScalarLogicalVersatileQuery(Builtin builtin,Arg arg){ return caseAbstractVersatileQuery(builtin,arg); }    
    //same as isequal
    public Ret caseIsequalwithequalnans(Builtin builtin,Arg arg){ return caseAbstractMultiaryToScalarLogicalVersatileQuery(builtin,arg); }    
    //recursive equal all
    public Ret caseIsequal(Builtin builtin,Arg arg){ return caseAbstractMultiaryToScalarLogicalVersatileQuery(builtin,arg); }

    public Ret caseAbstractVersatileConversion(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    
    //functions that change the shape
    public Ret caseAbstractShapeTransformation(Builtin builtin,Arg arg){ return caseAbstractVersatileConversion(builtin,arg); }
    public Ret caseReshape(Builtin builtin,Arg arg){ return caseAbstractShapeTransformation(builtin,arg); }
    public Ret casePermute(Builtin builtin,Arg arg){ return caseAbstractShapeTransformation(builtin,arg); }

    public Ret caseAbstractUnaryShapeTransformation(Builtin builtin,Arg arg){ return caseAbstractShapeTransformation(builtin,arg); }
    public Ret caseSqueeze(Builtin builtin,Arg arg){ return caseAbstractUnaryShapeTransformation(builtin,arg); }
    public Ret caseTranspose(Builtin builtin,Arg arg){ return caseAbstractUnaryShapeTransformation(builtin,arg); }
    public Ret caseCtranspose(Builtin builtin,Arg arg){ return caseAbstractUnaryShapeTransformation(builtin,arg); }
    
    //all these take multiple args
    public Ret caseAbstractConcatenation(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    public Ret caseHorzcat(Builtin builtin,Arg arg){ return caseAbstractConcatenation(builtin,arg); }
    public Ret caseVertcat(Builtin builtin,Arg arg){ return caseAbstractConcatenation(builtin,arg); }    
    //generalization of horz/vert cat
    public Ret caseCat(Builtin builtin,Arg arg){ return caseAbstractConcatenation(builtin,arg); }

    public Ret caseAbstractIndexing(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    public Ret caseSubsasgn(Builtin builtin,Arg arg){ return caseAbstractIndexing(builtin,arg); }
    public Ret caseSubsref(Builtin builtin,Arg arg){ return caseAbstractIndexing(builtin,arg); }

    public Ret caseAbstractMapOperator(Builtin builtin,Arg arg){ return caseAbstractVersatileFunction(builtin,arg); }
    public Ret caseStructfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    public Ret caseArrayfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    public Ret caseCellfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    
    //*** opposite of pure functions *****************************************
    public Ret caseAbstractImpureFunction(Builtin builtin,Arg arg){ return caseAbstractRoot(builtin,arg); }    
    //dunnno what to do with these
    public Ret caseSuperiorto(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseSuperiorfloat(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }    
    //set superior to relationship in oldclass constructor
    public Ret caseExit(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseQuit(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    
    //only really need one of quit/exit
    public Ret caseAbstractBuiltin(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    
    //calls builtin - proper
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
    public Ret caseNargin(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseNargout(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }    
    //methods, fieldnames takes obj or string, allows extra arg '-full'
    public Ret caseMethods(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }
    public Ret caseFieldnames(Builtin builtin,Arg arg){ return caseAbstractMatlabEnvironmentFunction(builtin,arg); }

    public Ret caseAbstractReportFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }    
    //errors should be seprated from warnings and displaying stuff?
    public Ret caseDisp(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseDisplay(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseClc(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //TODO rething error
    public Ret caseError(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseWarning(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseEcho(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //should this be here? - this could be a proper lib function?
    public Ret caseDiary(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //TODO review this:
    public Ret caseMessage(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseLastwarn(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseLasterror(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseFormat(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }

    public Ret caseAbstractRandomFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseRand(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandn(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandi(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandperm(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }

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
    
    //*** library funcitons that are not builtins!! TODO **********************
    public Ret caseAbstractNotABuiltin(Builtin builtin,Arg arg){ return caseAbstractRoot(builtin,arg); }    
    //linspace
    public Ret caseImwrite(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseSparse(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseRealmax(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseHistc(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseBlkdiag(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }    
    //at least variance should be a builtin, std = sqrt(var)
    public Ret caseVar(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseStd(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public  Ret caseIscolumn(Builtin builtin, Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }
    public  Ret caseIsrow(Builtin builtin, Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }
    public  Ret caseIsmatrix(Builtin builtin, Arg arg){ return caseAbstractScalarLogicalShapeQuery(builtin,arg); }
}