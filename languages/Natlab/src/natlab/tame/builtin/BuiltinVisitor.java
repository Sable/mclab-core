package natlab.tame.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg,int num);
    
    //- create a way of just returning the dominant arg, where a list of dominant args is given
    public Ret caseAbstractRoot(Builtin builtin,Arg arg,int num){ return caseBuiltin(builtin,arg,num); }
    
    //pure functions have no side effects and always return the same value, depending only on arguments
    public Ret caseAbstractPureFunction(Builtin builtin,Arg arg,int num){ return caseAbstractRoot(builtin,arg,num); }
    
    //function operates on matrizes (numerical,char,logical)
    public Ret caseAbstractMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractPureFunction(builtin,arg,num); }
    
    //constant function - there are few of these, most 'constants' take optional shape args
    public Ret caseAbstractConstant(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }

    public Ret caseAbstractDoubleConstant(Builtin builtin,Arg arg,int num){ return caseAbstractConstant(builtin,arg,num); }
    public Ret casePi(Builtin builtin,Arg arg,int num){ return caseAbstractDoubleConstant(builtin,arg,num); }
    public Ret caseI(Builtin builtin,Arg arg,int num){ return caseAbstractDoubleConstant(builtin,arg,num); }
    public Ret caseJ(Builtin builtin,Arg arg,int num){ return caseAbstractDoubleConstant(builtin,arg,num); }
    
    //general function - operates on any matrix, sometimes with restrictions
    public Ret caseAbstractAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    
    //any matrix functions with fixed arity, where all arguments are numeric operands
    public Ret caseAbstractProperAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractAnyMatrixFunction(builtin,arg,num); }
    
    //unary function operating on a general matrix
    public Ret caseAbstractUnaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperAnyMatrixFunction(builtin,arg,num); }
    
    //elemental unary function operating on a general matrix
    public Ret caseElementalUnaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg,num); }
    
    //matrix-wise unary function operating on a general matrix
    public Ret caseArrayUnaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryAnyMatrixFunction(builtin,arg,num); }
    
    //binary function operating on a general matrix
    public Ret caseAbstractBinaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperAnyMatrixFunction(builtin,arg,num); }
    
    //elemental binary function operating on a general matrix
    public Ret caseElementalBinaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryAnyMatrixFunction(builtin,arg,num); }
    
    //matrix-wise unary function operating on a general matrix
    public Ret caseArrayBinaryAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryAnyMatrixFunction(builtin,arg,num); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractImproperAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractAnyMatrixFunction(builtin,arg,num); }

    public Ret caseAbstractDiagonalSensitive(Builtin builtin,Arg arg,int num){ return caseAbstractImproperAnyMatrixFunction(builtin,arg,num); }
    public Ret caseTril(Builtin builtin,Arg arg,int num){ return caseAbstractDiagonalSensitive(builtin,arg,num); }
    public Ret caseTriu(Builtin builtin,Arg arg,int num){ return caseAbstractDiagonalSensitive(builtin,arg,num); }
    public Ret caseDiag(Builtin builtin,Arg arg,int num){ return caseAbstractDiagonalSensitive(builtin,arg,num); }
    
    //functions of the form f(matrix,[dimension]), where matrix is any matrix
    public Ret caseAbstractDimensionSensitiveAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperAnyMatrixFunction(builtin,arg,num); }
    
    //functions that collapse the specified dimension
    public Ret caseDimensionCollapsingAnyMatrixFunction(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveAnyMatrixFunction(builtin,arg,num); }
    
    //- some actual Matlab functions don't actually coerce logicals and or chars!
    public Ret caseAbstractNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    
    //should this be called something related to operator?
    public Ret caseAbstractProperNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractNumericFunction(builtin,arg,num); }
    
    //unary numeric function
    public Ret caseAbstractUnaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperNumericFunction(builtin,arg,num); }

    public Ret caseAbstractElementalUnaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryNumericFunction(builtin,arg,num); }
    public Ret caseReal(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseImag(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseAbs(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseConj(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseSign(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }

    public Ret caseAbstractElementalUnaryArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseUplus(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryArithmetic(builtin,arg,num); }
    public Ret caseUminus(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryArithmetic(builtin,arg,num); }

    public Ret caseAbstractRoundingOperation(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryNumericFunction(builtin,arg,num); }
    public Ret caseFix(Builtin builtin,Arg arg,int num){ return caseAbstractRoundingOperation(builtin,arg,num); }
    public Ret caseRound(Builtin builtin,Arg arg,int num){ return caseAbstractRoundingOperation(builtin,arg,num); }
    public Ret caseFloor(Builtin builtin,Arg arg,int num){ return caseAbstractRoundingOperation(builtin,arg,num); }
    public Ret caseCeil(Builtin builtin,Arg arg,int num){ return caseAbstractRoundingOperation(builtin,arg,num); }

    public Ret caseAbstractArrayUnaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryNumericFunction(builtin,arg,num); }

    public Ret caseArrayUnaryArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractArrayUnaryNumericFunction(builtin,arg,num); }
    
    //binary numeric function - doesn't mix integers
    public Ret caseAbstractBinaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperNumericFunction(builtin,arg,num); }
    
    //elemental binary numeric matrix function
    public Ret caseAbstractElementalBinaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryNumericFunction(builtin,arg,num); }    
    //imporoper? - supports 1 arg
    public Ret caseComplex(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryNumericFunction(builtin,arg,num); }
    
    //elemewise binary arithmetic
    public Ret caseAbstractElementalBinaryArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryNumericFunction(builtin,arg,num); }
    public Ret casePlus(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryArithmetic(builtin,arg,num); }
    public Ret caseMinus(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryArithmetic(builtin,arg,num); }
    public Ret caseTimes(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryArithmetic(builtin,arg,num); }    
    //integers can only be combined with scalar doubles
    public Ret casePower(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryArithmetic(builtin,arg,num); }

    public Ret caseAbstractDividingElementalArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryArithmetic(builtin,arg,num); }    
    //the following do not allow both args to be double under certain circumstances (...)
    public Ret caseLdivide(Builtin builtin,Arg arg,int num){ return caseAbstractDividingElementalArithmetic(builtin,arg,num); }
    public Ret caseRdivide(Builtin builtin,Arg arg,int num){ return caseAbstractDividingElementalArithmetic(builtin,arg,num); }
    public Ret caseMod(Builtin builtin,Arg arg,int num){ return caseAbstractDividingElementalArithmetic(builtin,arg,num); }
    public Ret caseRem(Builtin builtin,Arg arg,int num){ return caseAbstractDividingElementalArithmetic(builtin,arg,num); }
    
    //array wise binary numeric matrix function
    public Ret caseAbstractArrayBinaryNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryNumericFunction(builtin,arg,num); }
    public Ret caseCross(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryNumericFunction(builtin,arg,num); }
    
    //binary matrix arithmetic - doesn't mix integer types
    public Ret caseAbstractArrayBinaryArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryNumericFunction(builtin,arg,num); }
    public Ret caseMtimes(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryArithmetic(builtin,arg,num); }    
    //integers can only be comibned with scalar doubles -- or not?
    public Ret caseMpower(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryArithmetic(builtin,arg,num); }

    public Ret caseAbstractDividingArrayArithmetic(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryArithmetic(builtin,arg,num); }
    public Ret caseMldivide(Builtin builtin,Arg arg,int num){ return caseAbstractDividingArrayArithmetic(builtin,arg,num); }
    public Ret caseMrdivide(Builtin builtin,Arg arg,int num){ return caseAbstractDividingArrayArithmetic(builtin,arg,num); }
    
    //arguments that either options or different possible operands.
    public Ret caseAbstractImproperNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractNumericFunction(builtin,arg,num); }
    
    //functions of the form f(matrix1,matrix2,...,matrixk,[dimension]), where matrix is any matrix
    public Ret caseAbstractDimensionSensitiveNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperNumericFunction(builtin,arg,num); }    
    //TODO dot results in floats - but has the same input constraints as numeric - different category? not a builtin?
    public Ret caseDot(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveNumericFunction(builtin,arg,num); }
    
    //functions that collapse the specified dimension
    public Ret caseAbstractDimensionCollapsingNumericFunction(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveNumericFunction(builtin,arg,num); }

    public Ret caseAbstractMinOrMax(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionCollapsingNumericFunction(builtin,arg,num); }
    public Ret caseMin(Builtin builtin,Arg arg,int num){ return caseAbstractMinOrMax(builtin,arg,num); }
    public Ret caseMax(Builtin builtin,Arg arg,int num){ return caseAbstractMinOrMax(builtin,arg,num); }    
    //median does not support logical matrizes
    public Ret caseMedian(Builtin builtin,Arg arg,int num){ return caseAbstractMinOrMax(builtin,arg,num); }
    
    //operatoes on floating point matrizes
    public Ret caseAbstractFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    
    //proper float functions have a fixed arity, and all operands are floats
    public Ret caseAbstractProperFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractFloatFunction(builtin,arg,num); }
    
    //unary functions operating on floating point matrizes
    public Ret caseAbstractUnaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperFloatFunction(builtin,arg,num); }
    
    //elemental unary functions operating on floating point matrizes
    public Ret caseAbstractElementalUnaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryFloatFunction(builtin,arg,num); }
    public Ret caseSqrt(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseRealsqrt(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseErf(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseErfinv(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseErfc(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseErfcinv(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseGamma(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseGammaln(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseExp(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseLog(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseLog2(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }
    public Ret caseLog10(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }

    public Ret caseAbstractForwardTrigonometricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }

    public Ret caseAbstractRadianTrigonometricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractForwardTrigonometricFunction(builtin,arg,num); }
    public Ret caseSin(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }
    public Ret caseCos(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }
    public Ret caseTan(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }
    public Ret caseCot(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }
    public Ret caseSec(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }
    public Ret caseCsc(Builtin builtin,Arg arg,int num){ return caseAbstractRadianTrigonometricFunction(builtin,arg,num); }

    public Ret caseAbstractDegreeTrigonometricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractForwardTrigonometricFunction(builtin,arg,num); }
    public Ret caseSind(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }
    public Ret caseCosd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }
    public Ret caseTand(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }
    public Ret caseCotd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }
    public Ret caseSecd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }
    public Ret caseCscd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeTrigonometricFunction(builtin,arg,num); }

    public Ret caseAbstractHyperbolicTrigonometricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractForwardTrigonometricFunction(builtin,arg,num); }
    public Ret caseSinh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }
    public Ret caseCosh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }
    public Ret caseTanh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }
    public Ret caseCoth(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }
    public Ret caseSech(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }
    public Ret caseCsch(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg,num); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryFloatFunction(builtin,arg,num); }

    public Ret caseAbstractRadianInverseTrigonmetricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsin(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcos(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAtan(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcot(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsec(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcsc(Builtin builtin,Arg arg,int num){ return caseAbstractRadianInverseTrigonmetricFunction(builtin,arg,num); }

    public Ret caseAbstractDegreeInverseTrigonmetricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsind(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcosd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAtand(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcotd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsecd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcscd(Builtin builtin,Arg arg,int num){ return caseAbstractDegreeInverseTrigonmetricFunction(builtin,arg,num); }

    public Ret caseAbstractHyperbolicInverseTrigonmetricFunction(Builtin builtin,Arg arg,int num){ return caseAbstractInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsinh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcosh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAtanh(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcoth(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAsech(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    public Ret caseAcsch(Builtin builtin,Arg arg,int num){ return caseAbstractHyperbolicInverseTrigonmetricFunction(builtin,arg,num); }
    
    //matrix-wise unary function operating on floating point matrizes
    public Ret caseAbstractArrayUnaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryFloatFunction(builtin,arg,num); }
    
    //unary function operating on square floating point matrix
    public Ret caseAbstractSquareArrayUnaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractArrayUnaryFloatFunction(builtin,arg,num); }
    public Ret caseLogm(Builtin builtin,Arg arg,int num){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg,num); }
    public Ret caseSqrtm(Builtin builtin,Arg arg,int num){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg,num); }
    public Ret caseExpm(Builtin builtin,Arg arg,int num){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg,num); }
    public Ret caseInv(Builtin builtin,Arg arg,int num){ return caseAbstractSquareArrayUnaryFloatFunction(builtin,arg,num); }
    
    //binary matrix function operaitng on float matrices
    public Ret caseAbstractBinaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperFloatFunction(builtin,arg,num); }
    
    //elemental binary functino operating on floats matrizes
    public Ret caseAbstractElementalBinaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryFloatFunction(builtin,arg,num); }
    public Ret caseAtan2(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryFloatFunction(builtin,arg,num); }
    
    //matrix-wise binary funciton operating on float matrices
    public Ret caseAbstractArrayBinaryFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryFloatFunction(builtin,arg,num); }
    public Ret caseHypot(Builtin builtin,Arg arg,int num){ return caseAbstractArrayBinaryFloatFunction(builtin,arg,num); }
    
    //options or different possible operands.
    public Ret caseAbstractImproperFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractFloatFunction(builtin,arg,num); }
    public Ret caseEps(Builtin builtin,Arg arg,int num){ return caseAbstractImproperFloatFunction(builtin,arg,num); }
    
    //functions of the form f(matrix,[dimension])
    public Ret caseAbstractDimensionSensitiveFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperFloatFunction(builtin,arg,num); }    
    //cumsum coerces logicals to double
    public Ret caseCumsum(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg,num); }
    public Ret caseCumprod(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg,num); }
    
    //numeric function that collapses a dimension (optional second arg tells which)
    public Ret caseAbstractDimensionCollapsingFloatFunction(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionSensitiveFloatFunction(builtin,arg,num); }
    public Ret caseMode(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg,num); }
    public Ret caseProd(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg,num); }    
    //TODO - sum takes a possible string parameter
    public Ret caseSum(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg,num); }
    public Ret caseMean(Builtin builtin,Arg arg,int num){ return caseAbstractDimensionCollapsingFloatFunction(builtin,arg,num); }
    
    //standard matrix math functions with optional arguments
    public Ret caseAbstractMatrixLibaryFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperFloatFunction(builtin,arg,num); }
    public Ret caseEig(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }
    public Ret caseNorm(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }    
    //query?
    public Ret caseRank(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }    
    //optional 2 args
    public Ret caseCond(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }    
    //square float -> salar double - should probably be proper
    public Ret caseDet(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }
    public Ret caseRcond(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }    
    //has 2 operands
    public Ret caseLinsolve(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixLibaryFunction(builtin,arg,num); }
    
    //factorization functions
    public Ret caseAbstractFacotorizationFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperFloatFunction(builtin,arg,num); }
    public Ret caseSchur(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }    
    //ordschur is not a factorization per se, it massages schur result
    public Ret caseOrdschur(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }
    public Ret caseLu(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }
    public Ret caseChol(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }
    public Ret caseSvd(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }
    public Ret caseQr(Builtin builtin,Arg arg,int num){ return caseAbstractFacotorizationFunction(builtin,arg,num); }
    
    //bit functions - usually operates on ints or logicals, some exceptions
    public Ret caseAbstractBitFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }

    public Ret caseAbstractProperBitFunction(Builtin builtin,Arg arg,int num){ return caseAbstractBitFunction(builtin,arg,num); }
    public Ret caseBitand(Builtin builtin,Arg arg,int num){ return caseAbstractProperBitFunction(builtin,arg,num); }
    public Ret caseBitor(Builtin builtin,Arg arg,int num){ return caseAbstractProperBitFunction(builtin,arg,num); }
    public Ret caseBitxor(Builtin builtin,Arg arg,int num){ return caseAbstractProperBitFunction(builtin,arg,num); }

    public Ret caseAbstractImproperBitFunciton(Builtin builtin,Arg arg,int num){ return caseAbstractBitFunction(builtin,arg,num); }
    public Ret caseBitcmp(Builtin builtin,Arg arg,int num){ return caseAbstractImproperBitFunciton(builtin,arg,num); }
    public Ret caseBitset(Builtin builtin,Arg arg,int num){ return caseAbstractImproperBitFunciton(builtin,arg,num); }
    public Ret caseBitget(Builtin builtin,Arg arg,int num){ return caseAbstractImproperBitFunciton(builtin,arg,num); }
    public Ret caseBitshift(Builtin builtin,Arg arg,int num){ return caseAbstractImproperBitFunciton(builtin,arg,num); }
    
    //TODO impure/pure unary for scalar?
    public Ret caseAbstractMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    
    //return doubles
    public Ret caseAbstractToDoubleMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixQuery(builtin,arg,num); }
    public Ret caseFind(Builtin builtin,Arg arg,int num){ return caseAbstractToDoubleMatrixQuery(builtin,arg,num); }

    public Ret caseAbstractUnaryToScalarDoubleMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractToDoubleMatrixQuery(builtin,arg,num); }

    public Ret caseAbstractToScalarDoubleMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryToScalarDoubleMatrixQuery(builtin,arg,num); }
    public Ret caseNnz(Builtin builtin,Arg arg,int num){ return caseAbstractToScalarDoubleMatrixQuery(builtin,arg,num); }
    
    //return logicals
    public Ret caseAbstractToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixQuery(builtin,arg,num); }
    
    //unary return logicals
    public Ret caseAbstractUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractToLogicalMatrixQuery(builtin,arg,num); }

    public Ret caseAbstractScalarUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseNot(Builtin builtin,Arg arg,int num){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseAny(Builtin builtin,Arg arg,int num){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseAll(Builtin builtin,Arg arg,int num){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseIsreal(Builtin builtin,Arg arg,int num){ return caseAbstractScalarUnaryToLogicalMatrixQuery(builtin,arg,num); }

    public Ret caseAbstractElementalUnaryToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseIsinf(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseIsfinite(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseIsnan(Builtin builtin,Arg arg,int num){ return caseAbstractElementalUnaryToLogicalMatrixQuery(builtin,arg,num); }
    
    //binary return logicals
    public Ret caseAbstractBinaryToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractToLogicalMatrixQuery(builtin,arg,num); }

    public Ret caseAbstractElementalBinaryToLogicalMatrixQuery(Builtin builtin,Arg arg,int num){ return caseAbstractBinaryToLogicalMatrixQuery(builtin,arg,num); }
    
    //relational operators
    public Ret caseAbstractRelationalOperator(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseEq(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    public Ret caseNe(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    public Ret caseLt(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    public Ret caseGt(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    public Ret caseLe(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    public Ret caseGe(Builtin builtin,Arg arg,int num){ return caseAbstractRelationalOperator(builtin,arg,num); }
    
    //logical operators
    public Ret caseAbstractLogicalOperator(Builtin builtin,Arg arg,int num){ return caseAbstractElementalBinaryToLogicalMatrixQuery(builtin,arg,num); }
    public Ret caseAnd(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalOperator(builtin,arg,num); }
    public Ret caseOr(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalOperator(builtin,arg,num); }
    public Ret caseXor(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalOperator(builtin,arg,num); }
    
    //functions that create matrix arrays from vectors or values describing the matrizes
    public Ret caseAbstractMatrixCreation(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    public Ret caseColon(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixCreation(builtin,arg,num); }
    
    //construct arrays via their dimensions
    public Ret caseAbstractByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixCreation(builtin,arg,num); }

    public Ret caseAbstractNumericalByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg,int num){ return caseAbstractByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseOnes(Builtin builtin,Arg arg,int num){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseZeros(Builtin builtin,Arg arg,int num){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg,num); }    
    //eye takes at most 2 dims
    public Ret caseEye(Builtin builtin,Arg arg,int num){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg,num); }

    public Ret caseAbstractFloatByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg,int num){ return caseAbstractNumericalByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseInf(Builtin builtin,Arg arg,int num){ return caseAbstractFloatByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseNan(Builtin builtin,Arg arg,int num){ return caseAbstractFloatByShapeAndTypeMatrixCreation(builtin,arg,num); }
    
    //there is no optional type argument
    public Ret caseAbstractLogicalByShapeAndTypeMatrixCreation(Builtin builtin,Arg arg,int num){ return caseAbstractByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseTrue(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalByShapeAndTypeMatrixCreation(builtin,arg,num); }
    public Ret caseFalse(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalByShapeAndTypeMatrixCreation(builtin,arg,num); }
    
    //matrix constructors
    public Ret caseAbstractMatrixConstructor(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixFunction(builtin,arg,num); }
    public Ret caseDouble(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseSingle(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseChar(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseLogical(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseInt8(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseInt16(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseInt32(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseInt64(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseUint8(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseUint16(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseUint32(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    public Ret caseUint64(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixConstructor(builtin,arg,num); }
    
    //function operates on cell arrays
    public Ret caseAbstractCellFunction(Builtin builtin,Arg arg,int num){ return caseAbstractPureFunction(builtin,arg,num); }
    public Ret caseCell(Builtin builtin,Arg arg,int num){ return caseAbstractCellFunction(builtin,arg,num); }

    public Ret caseAbstractCellCat(Builtin builtin,Arg arg,int num){ return caseAbstractCellFunction(builtin,arg,num); }
    public Ret caseCellhorzcat(Builtin builtin,Arg arg,int num){ return caseAbstractCellCat(builtin,arg,num); }
    public Ret caseCellvertcat(Builtin builtin,Arg arg,int num){ return caseAbstractCellCat(builtin,arg,num); }
    
    //function operates on structures
    public Ret caseAbstractStructFunction(Builtin builtin,Arg arg,int num){ return caseAbstractPureFunction(builtin,arg,num); }
    public Ret caseIsfield(Builtin builtin,Arg arg,int num){ return caseAbstractStructFunction(builtin,arg,num); }
    public Ret caseStruct(Builtin builtin,Arg arg,int num){ return caseAbstractStructFunction(builtin,arg,num); }    
    //function operates on objects
    public Ret caseObjectFunction(Builtin builtin,Arg arg,int num){ return caseAbstractPureFunction(builtin,arg,num); }
    
    //function operates on a mixtures of matrizes, cell arrays, structures and/or objects
    public Ret caseAbstractVersatileFunction(Builtin builtin,Arg arg,int num){ return caseAbstractPureFunction(builtin,arg,num); }
    
    //takes in a matrix or cells of strings
    public Ret caseAbstractMatrixOrCellOfCharFunction(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    public Ret caseSort(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg,num); }
    public Ret caseUnique(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg,num); }
    
    //TODO should it be called string function?
    public Ret caseAbstractCharFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatrixOrCellOfCharFunction(builtin,arg,num); }

    public Ret caseAbstractProperCharFunction(Builtin builtin,Arg arg,int num){ return caseAbstractCharFunction(builtin,arg,num); }

    public Ret caseAbstractUnaryProperCharFunction(Builtin builtin,Arg arg,int num){ return caseAbstractProperCharFunction(builtin,arg,num); }
    public Ret caseUpper(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryProperCharFunction(builtin,arg,num); }
    public Ret caseLower(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryProperCharFunction(builtin,arg,num); }
    public Ret caseDeblank(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryProperCharFunction(builtin,arg,num); }    
    //strip
    public Ret caseStrtrim(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryProperCharFunction(builtin,arg,num); }

    public Ret caseAbstractImproperCharFunction(Builtin builtin,Arg arg,int num){ return caseAbstractCharFunction(builtin,arg,num); }
    public Ret caseStrfind(Builtin builtin,Arg arg,int num){ return caseAbstractImproperCharFunction(builtin,arg,num); }
    public Ret caseFindstr(Builtin builtin,Arg arg,int num){ return caseAbstractImproperCharFunction(builtin,arg,num); }
    public Ret caseStrrep(Builtin builtin,Arg arg,int num){ return caseAbstractImproperCharFunction(builtin,arg,num); }
    
    //str[n]cmp[i]
    public Ret caseAbstractStringCompare(Builtin builtin,Arg arg,int num){ return caseAbstractImproperCharFunction(builtin,arg,num); }
    public Ret caseStrcmp(Builtin builtin,Arg arg,int num){ return caseAbstractStringCompare(builtin,arg,num); }
    public Ret caseStrcmpi(Builtin builtin,Arg arg,int num){ return caseAbstractStringCompare(builtin,arg,num); }
    public Ret caseStrncmpi(Builtin builtin,Arg arg,int num){ return caseAbstractStringCompare(builtin,arg,num); }
    public Ret caseStrncmp(Builtin builtin,Arg arg,int num){ return caseAbstractStringCompare(builtin,arg,num); }
    
    //regular expression functions
    public Ret caseAbstractRegexpFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImproperCharFunction(builtin,arg,num); }
    public Ret caseRegexptranslate(Builtin builtin,Arg arg,int num){ return caseAbstractRegexpFunction(builtin,arg,num); }
    public Ret caseRegexp(Builtin builtin,Arg arg,int num){ return caseAbstractRegexpFunction(builtin,arg,num); }
    public Ret caseRegexpi(Builtin builtin,Arg arg,int num){ return caseAbstractRegexpFunction(builtin,arg,num); }
    public Ret caseRegexprep(Builtin builtin,Arg arg,int num){ return caseAbstractRegexpFunction(builtin,arg,num); }
    
    //query functions that operate not just on matrizes (c.f. matrixQueryFunction)
    public Ret caseAbstractVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    public Ret caseClass(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileQuery(builtin,arg,num); }
    
    //return numericals
    public Ret caseAbstractDoubleResultVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileQuery(builtin,arg,num); }    
    //TODO fix this output?!
    public Ret caseSize(Builtin builtin,Arg arg,int num){ return caseAbstractDoubleResultVersatileQuery(builtin,arg,num); }
    
    //scalar numerical
    public Ret caseAbstractScalarDoubleResultVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractDoubleResultVersatileQuery(builtin,arg,num); }
    public Ret caseLength(Builtin builtin,Arg arg,int num){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg,num); }
    public Ret caseNdims(Builtin builtin,Arg arg,int num){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg,num); }
    public Ret caseNumel(Builtin builtin,Arg arg,int num){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg,num); }
    public Ret caseEnd(Builtin builtin,Arg arg,int num){ return caseAbstractScalarDoubleResultVersatileQuery(builtin,arg,num); }
    
    //return logicals
    public Ret caseAbstractLogicalResultVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileQuery(builtin,arg,num); }
    
    //return scalar logicals
    public Ret caseAbstractScalarLogicalResultVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractLogicalResultVersatileQuery(builtin,arg,num); }

    public Ret caseAbstractClassQuery(Builtin builtin,Arg arg,int num){ return caseAbstractScalarLogicalResultVersatileQuery(builtin,arg,num); }
    public Ret caseIsobject(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIsfloat(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIsinteger(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIslogical(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIsstruct(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIschar(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIscell(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }
    public Ret caseIsnumeric(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }    
    //TODO impure?
    public Ret caseIsa(Builtin builtin,Arg arg,int num){ return caseAbstractClassQuery(builtin,arg,num); }

    public Ret caseAbstractScalarLogicalShapeQuery(Builtin builtin,Arg arg,int num){ return caseAbstractScalarLogicalResultVersatileQuery(builtin,arg,num); }
    public Ret caseIsempty(Builtin builtin,Arg arg,int num){ return caseAbstractScalarLogicalShapeQuery(builtin,arg,num); }
    public Ret caseIsvector(Builtin builtin,Arg arg,int num){ return caseAbstractScalarLogicalShapeQuery(builtin,arg,num); }
    public Ret caseIsscalar(Builtin builtin,Arg arg,int num){ return caseAbstractScalarLogicalShapeQuery(builtin,arg,num); }

    public Ret caseAbstractMultiaryToScalarLogicalVersatileQuery(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileQuery(builtin,arg,num); }    
    //same as isequal
    public Ret caseIsequalwithequalnans(Builtin builtin,Arg arg,int num){ return caseAbstractMultiaryToScalarLogicalVersatileQuery(builtin,arg,num); }    
    //recursive equal all
    public Ret caseIsequal(Builtin builtin,Arg arg,int num){ return caseAbstractMultiaryToScalarLogicalVersatileQuery(builtin,arg,num); }

    public Ret caseAbstractVersatileConversion(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    
    //functions that change the shape
    public Ret caseAbstractShapeTransformation(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileConversion(builtin,arg,num); }
    public Ret caseReshape(Builtin builtin,Arg arg,int num){ return caseAbstractShapeTransformation(builtin,arg,num); }
    public Ret casePermute(Builtin builtin,Arg arg,int num){ return caseAbstractShapeTransformation(builtin,arg,num); }

    public Ret caseAbstractUnaryShapeTransformation(Builtin builtin,Arg arg,int num){ return caseAbstractShapeTransformation(builtin,arg,num); }
    public Ret caseSqueeze(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryShapeTransformation(builtin,arg,num); }
    public Ret caseTranspose(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryShapeTransformation(builtin,arg,num); }
    public Ret caseCtranspose(Builtin builtin,Arg arg,int num){ return caseAbstractUnaryShapeTransformation(builtin,arg,num); }
    
    //all these take multiple args
    public Ret caseAbstractConcatenation(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    public Ret caseHorzcat(Builtin builtin,Arg arg,int num){ return caseAbstractConcatenation(builtin,arg,num); }
    public Ret caseVertcat(Builtin builtin,Arg arg,int num){ return caseAbstractConcatenation(builtin,arg,num); }    
    //generalization of horz/vert cat
    public Ret caseCat(Builtin builtin,Arg arg,int num){ return caseAbstractConcatenation(builtin,arg,num); }

    public Ret caseAbstractIndexing(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    public Ret caseSubsasgn(Builtin builtin,Arg arg,int num){ return caseAbstractIndexing(builtin,arg,num); }
    public Ret caseSubsref(Builtin builtin,Arg arg,int num){ return caseAbstractIndexing(builtin,arg,num); }

    public Ret caseAbstractMapOperator(Builtin builtin,Arg arg,int num){ return caseAbstractVersatileFunction(builtin,arg,num); }
    public Ret caseStructfun(Builtin builtin,Arg arg,int num){ return caseAbstractMapOperator(builtin,arg,num); }
    public Ret caseArrayfun(Builtin builtin,Arg arg,int num){ return caseAbstractMapOperator(builtin,arg,num); }
    public Ret caseCellfun(Builtin builtin,Arg arg,int num){ return caseAbstractMapOperator(builtin,arg,num); }
    
    //*** opposite of pure functions *****************************************
    public Ret caseAbstractImpureFunction(Builtin builtin,Arg arg,int num){ return caseAbstractRoot(builtin,arg,num); }    
    //dunnno what to do with these
    public Ret caseSuperiorto(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    public Ret caseSuperiorfloat(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }    
    //set superior to relationship in oldclass constructor
    public Ret caseExit(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    public Ret caseQuit(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    
    //only really need one of quit/exit
    public Ret caseAbstractBuiltin(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    
    //calls builtin - proper
    public Ret caseAbstractTimeFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    public Ret caseClock(Builtin builtin,Arg arg,int num){ return caseAbstractTimeFunction(builtin,arg,num); }
    public Ret caseTic(Builtin builtin,Arg arg,int num){ return caseAbstractTimeFunction(builtin,arg,num); }
    public Ret caseToc(Builtin builtin,Arg arg,int num){ return caseAbstractTimeFunction(builtin,arg,num); }
    public Ret caseCputime(Builtin builtin,Arg arg,int num){ return caseAbstractTimeFunction(builtin,arg,num); }

    public Ret caseAbstractMatlabSystemFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    public Ret caseAssert(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret caseNargoutchk(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret caseNargchk(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret caseStr2func(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret casePause(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }

    public Ret caseAbstractDynamicMatlabFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret caseEval(Builtin builtin,Arg arg,int num){ return caseAbstractDynamicMatlabFunction(builtin,arg,num); }
    public Ret caseEvalin(Builtin builtin,Arg arg,int num){ return caseAbstractDynamicMatlabFunction(builtin,arg,num); }
    public Ret caseFeval(Builtin builtin,Arg arg,int num){ return caseAbstractDynamicMatlabFunction(builtin,arg,num); }
    public Ret caseAssignin(Builtin builtin,Arg arg,int num){ return caseAbstractDynamicMatlabFunction(builtin,arg,num); }
    public Ret caseInputname(Builtin builtin,Arg arg,int num){ return caseAbstractDynamicMatlabFunction(builtin,arg,num); }

    public Ret caseAbstractMatlabEnvironmentFunction(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabSystemFunction(builtin,arg,num); }
    public Ret caseImport(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseCd(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseExist(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseMatlabroot(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseWhos(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseWhich(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseVersion(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseClear(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseNargin(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseNargout(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }    
    //methods, fieldnames takes obj or string, allows extra arg '-full'
    public Ret caseMethods(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }
    public Ret caseFieldnames(Builtin builtin,Arg arg,int num){ return caseAbstractMatlabEnvironmentFunction(builtin,arg,num); }

    public Ret caseAbstractReportFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }    
    //errors should be seprated from warnings and displaying stuff?
    public Ret caseDisp(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseDisplay(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseClc(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }    
    //TODO rething error
    public Ret caseError(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseWarning(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseEcho(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }    
    //should this be here? - this could be a proper lib function?
    public Ret caseDiary(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }    
    //TODO review this:
    public Ret caseMessage(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseLastwarn(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseLasterror(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }
    public Ret caseFormat(Builtin builtin,Arg arg,int num){ return caseAbstractReportFunction(builtin,arg,num); }

    public Ret caseAbstractRandomFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }
    public Ret caseRand(Builtin builtin,Arg arg,int num){ return caseAbstractRandomFunction(builtin,arg,num); }
    public Ret caseRandn(Builtin builtin,Arg arg,int num){ return caseAbstractRandomFunction(builtin,arg,num); }
    public Ret caseRandi(Builtin builtin,Arg arg,int num){ return caseAbstractRandomFunction(builtin,arg,num); }

    public Ret caseAbstractSystemFunction(Builtin builtin,Arg arg,int num){ return caseAbstractImpureFunction(builtin,arg,num); }    
    //should there be a separated operating system category?
    public Ret caseComputer(Builtin builtin,Arg arg,int num){ return caseAbstractSystemFunction(builtin,arg,num); }
    public Ret caseBeep(Builtin builtin,Arg arg,int num){ return caseAbstractSystemFunction(builtin,arg,num); }
    public Ret caseDir(Builtin builtin,Arg arg,int num){ return caseAbstractSystemFunction(builtin,arg,num); }

    public Ret caseAbstractOperatingSystemCallFunction(Builtin builtin,Arg arg,int num){ return caseAbstractSystemFunction(builtin,arg,num); }
    public Ret caseUnix(Builtin builtin,Arg arg,int num){ return caseAbstractOperatingSystemCallFunction(builtin,arg,num); }
    public Ret caseDos(Builtin builtin,Arg arg,int num){ return caseAbstractOperatingSystemCallFunction(builtin,arg,num); }
    public Ret caseSystem(Builtin builtin,Arg arg,int num){ return caseAbstractOperatingSystemCallFunction(builtin,arg,num); }

    public Ret caseAbstractIoFunction(Builtin builtin,Arg arg,int num){ return caseAbstractSystemFunction(builtin,arg,num); }
    public Ret caseLoad(Builtin builtin,Arg arg,int num){ return caseAbstractIoFunction(builtin,arg,num); }
    public Ret caseSave(Builtin builtin,Arg arg,int num){ return caseAbstractIoFunction(builtin,arg,num); }
    public Ret caseInput(Builtin builtin,Arg arg,int num){ return caseAbstractIoFunction(builtin,arg,num); }
    public Ret caseTextscan(Builtin builtin,Arg arg,int num){ return caseAbstractIoFunction(builtin,arg,num); }

    public Ret caseAbstractPosixIoFunction(Builtin builtin,Arg arg,int num){ return caseAbstractIoFunction(builtin,arg,num); }
    public Ret caseSprintf(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseSscanf(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFprintf(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFtell(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFerror(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFopen(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFread(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFrewind(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFscanf(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFseek(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFwrite(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFgetl(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFgets(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    public Ret caseFclose(Builtin builtin,Arg arg,int num){ return caseAbstractPosixIoFunction(builtin,arg,num); }
    
    //*** library funcitons that are not builtins!! TODO **********************
    public Ret caseAbstractNotABuiltin(Builtin builtin,Arg arg,int num){ return caseAbstractRoot(builtin,arg,num); }    
    //linspace
    public Ret caseImwrite(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
    public Ret caseSparse(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
    public Ret caseRealmax(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
    public Ret caseHistc(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
    public Ret caseBlkdiag(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }    
    //at least variance should be a builtin, std = sqrt(var)
    public Ret caseVar(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
    public Ret caseStd(Builtin builtin,Arg arg,int num){ return caseAbstractNotABuiltin(builtin,arg,num); }
}