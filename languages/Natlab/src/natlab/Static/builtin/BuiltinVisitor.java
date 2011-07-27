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
    
    //matlab operators
    public Ret caseAbstractOperator(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }    
    //special operators ... not sure what to do with them
    public Ret caseColon(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseHorzcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseVertcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseCat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseNargin(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseNargout(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseMfilename(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseEnd(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //other operators
    public Ret caseIsequalwithequalnans(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //same as isequal
    public Ret caseIsequal(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //recursive equal all
    public Ret caseSubsasgn(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //explicit indexingops
    public Ret caseSubsref(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseHistc(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }

    public Ret caseAbstractMapOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseStructfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    public Ret caseArrayfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    public Ret caseCellfun(Builtin builtin,Arg arg){ return caseAbstractMapOperator(builtin,arg); }
    
    //*** binary ops *************************************************
    public Ret caseAbstractBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    
    //relational ops
    public Ret caseAbstractRelationalOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    public Ret caseEq(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseNe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseLe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGe(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    public Ret caseGt(Builtin builtin,Arg arg){ return caseAbstractRelationalOperator(builtin,arg); }
    
    //logical operators - note there's no short circuit ops
    public Ret caseAbstractBinaryLogicalOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    public Ret caseAnd(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    public Ret caseOr(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    public Ret caseXor(Builtin builtin,Arg arg){ return caseAbstractBinaryLogicalOperator(builtin,arg); }
    
    //numerical binary oprators
    public Ret caseAbstractNumericalBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractBinaryOperator(builtin,arg); }
    
    //matrix operators
    public Ret caseAbstractMatrixBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret casePlus(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    public Ret caseMinus(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    public Ret caseMtimes(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    public Ret caseMpower(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    public Ret caseMldivide(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    public Ret caseMrdivide(Builtin builtin,Arg arg){ return caseAbstractMatrixBinaryOperator(builtin,arg); }
    
    //array operators
    public Ret caseAbstractElementwiseBinaryOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret caseTimes(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    public Ret caseLdivide(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    public Ret caseRdivide(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    public Ret casePower(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    public Ret casePow2(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }    
    //scalbn
    public Ret caseMod(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    public Ret caseRem(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }    
    //only works with single
    public Ret caseHypot(Builtin builtin,Arg arg){ return caseAbstractElementwiseBinaryOperator(builtin,arg); }
    
    //*** unary operators ***************************************************
    public Ret caseAbstractUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }

    public Ret caseAbstractNumericalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }

    public Ret caseAbstractElementwiseUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseUplus(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }
    public Ret caseUminus(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }
    public Ret caseConj(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }
    public Ret caseReal(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }
    public Ret caseImag(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }
    public Ret caseAbs(Builtin builtin,Arg arg){ return caseAbstractElementwiseUnaryOperator(builtin,arg); }

    public Ret caseAbstractMatrixUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }
    public Ret caseTranspose(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryOperator(builtin,arg); }
    public Ret caseCtranspose(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryOperator(builtin,arg); }    
    //analytical matrix operators
    public Ret caseExpm(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryOperator(builtin,arg); }
    public Ret caseSqrtm(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryOperator(builtin,arg); }
    public Ret caseLogm(Builtin builtin,Arg arg){ return caseAbstractMatrixUnaryOperator(builtin,arg); }

    public Ret caseAbstractLogicalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    
    //*** matrix operations ************************************************
    public Ret caseAbstractMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractElementwiseMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseSqrt(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseRealsqrt(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErf(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfinv(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfcinv(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseGamma(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseGammainc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseBetainc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseGammaln(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseExp(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseLog(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseLog2(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseLog10(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }

    public Ret caseAbstractForwardTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }

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

    public Ret caseAbstractHyperbolicTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseSinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseTanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseSech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }

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

    public Ret caseAbstractRoundingOperation(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseFix(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseRound(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseFloor(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    public Ret caseCeil(Builtin builtin,Arg arg){ return caseAbstractRoundingOperation(builtin,arg); }
    
    //matrix computation algorithms
    public Ret caseAbstractMatrixComputation(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseInv(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseEig(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseNorm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseRank(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseDet(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseDot(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseCross(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //factorization
    public Ret caseLinsolve(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //n dimensional discrete fourier transform
    public Ret caseRcond(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //condition number estimate
    public Ret caseTril(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseTriu(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseEps(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }

    public Ret caseAbstractFourierFunction(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseIfftn(Builtin builtin,Arg arg){ return caseAbstractFourierFunction(builtin,arg); }
    public Ret caseFftn(Builtin builtin,Arg arg){ return caseAbstractFourierFunction(builtin,arg); }
    public Ret caseFft(Builtin builtin,Arg arg){ return caseAbstractFourierFunction(builtin,arg); }

    public Ret caseAbstractFactorization(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseSchur(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }    
    //factorization
    public Ret caseOrdschur(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }
    public Ret caseLu(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }
    public Ret caseChol(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }
    public Ret caseQr(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }
    public Ret caseSvd(Builtin builtin,Arg arg){ return caseAbstractFactorization(builtin,arg); }
    
    //bit operators
    public Ret caseAbstractBitOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseBitand(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitxor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitcmp(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitget(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitshift(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitset(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    
    //*** string operations **************************************************
    public Ret caseAbstractStringOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseStrncmpi(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseStrcmp(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseStrcmpi(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseStrtrim(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseStrfind(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseFindstr(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseStrrep(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseUpper(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseLower(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseDeblank(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    
    //regulat expressions
    public Ret caseAbstractRegexpOperation(Builtin builtin,Arg arg){ return caseAbstractStringOperation(builtin,arg); }
    public Ret caseRegexptranslate(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    public Ret caseRegexp(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    public Ret caseRegexpi(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    public Ret caseTegexprep(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    
    //*** Constructors *****************************************************
    public Ret caseAbstractConstructor(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractPrimitiveConstructor(Builtin builtin,Arg arg){ return caseAbstractConstructor(builtin,arg); }
    public Ret caseDouble(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseSingle(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseChar(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseLogical(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseInt8(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseInt16(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseInt32(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseInt64(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseUint8(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseUint16(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseUint32(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }
    public Ret caseUint64(Builtin builtin,Arg arg){ return caseAbstractPrimitiveConstructor(builtin,arg); }

    public Ret caseAbstractCompoundConstructor(Builtin builtin,Arg arg){ return caseAbstractConstructor(builtin,arg); }
    public Ret caseCell(Builtin builtin,Arg arg){ return caseAbstractCompoundConstructor(builtin,arg); }
    public Ret caseStruct(Builtin builtin,Arg arg){ return caseAbstractCompoundConstructor(builtin,arg); }

    public Ret caseAbstractConversionFunction(Builtin builtin,Arg arg){ return caseAbstractConstructor(builtin,arg); }
    public Ret caseCell2struct(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }
    public Ret caseStruct2cell(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }
    public Ret caseTypecast(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }
    public Ret caseCast(Builtin builtin,Arg arg){ return caseAbstractConversionFunction(builtin,arg); }
    
    //*** struct operations ************************************************
    public Ret caseAbstractStructOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseIsfield(Builtin builtin,Arg arg){ return caseAbstractStructOperation(builtin,arg); }
    
    //*** class operations **************************************************
    public Ret caseAbstractClassOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseClass(Builtin builtin,Arg arg){ return caseAbstractClassOperation(builtin,arg); }

    public Ret caseAbstractClassQueryOperation(Builtin builtin,Arg arg){ return caseAbstractClassOperation(builtin,arg); }
    public Ret caseMethodnames(Builtin builtin,Arg arg){ return caseAbstractClassQueryOperation(builtin,arg); }
    public Ret caseFieldnames(Builtin builtin,Arg arg){ return caseAbstractClassQueryOperation(builtin,arg); }

    public Ret caseAbstractLogicalClassQueryOperation(Builtin builtin,Arg arg){ return caseAbstractClassQueryOperation(builtin,arg); }
    public Ret caseIsempty(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIsobject(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIsfloat(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIsinteger(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIslogical(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIsstruct(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIschar(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIscell(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    public Ret caseIsa(Builtin builtin,Arg arg){ return caseAbstractLogicalClassQueryOperation(builtin,arg); }
    
    //*** Array operations **************************************************
    public Ret caseAbstractArrayOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseSort(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseUnique(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseFind(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }

    public Ret caseAbstractArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseDiag(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }    
    //changing arrays
    public Ret caseReshape(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret casePermute(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseSqueeze(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseComplex(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }

    public Ret caseAbstractDimensionCollapsingOperation(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseProd(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingOperation(builtin,arg); }
    public Ret caseSum(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingOperation(builtin,arg); }
    public Ret caseMean(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingOperation(builtin,arg); }
    public Ret caseMin(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingOperation(builtin,arg); }
    public Ret caseMax(Builtin builtin,Arg arg){ return caseAbstractDimensionCollapsingOperation(builtin,arg); }

    public Ret caseAbstractShapeArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    
    //construct arrays via their dimensions
    public Ret caseAbstractNumericalShapeAndTypeArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractShapeArrayConstructor(builtin,arg); }
    public Ret caseOnes(Builtin builtin,Arg arg){ return caseAbstractNumericalShapeAndTypeArrayConstructor(builtin,arg); }
    public Ret caseZeros(Builtin builtin,Arg arg){ return caseAbstractNumericalShapeAndTypeArrayConstructor(builtin,arg); }    
    //eye takes at most 2 dims
    public Ret caseEye(Builtin builtin,Arg arg){ return caseAbstractNumericalShapeAndTypeArrayConstructor(builtin,arg); }

    public Ret caseAbstractFloatShapeAndTypeArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractNumericalShapeAndTypeArrayConstructor(builtin,arg); }
    public Ret caseInf(Builtin builtin,Arg arg){ return caseAbstractFloatShapeAndTypeArrayConstructor(builtin,arg); }
    public Ret caseNan(Builtin builtin,Arg arg){ return caseAbstractFloatShapeAndTypeArrayConstructor(builtin,arg); }

    public Ret caseAbstractLogicalShapeArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractShapeArrayConstructor(builtin,arg); }
    public Ret caseTrue(Builtin builtin,Arg arg){ return caseAbstractLogicalShapeArrayConstructor(builtin,arg); }
    public Ret caseFalse(Builtin builtin,Arg arg){ return caseAbstractLogicalShapeArrayConstructor(builtin,arg); }

    public Ret caseAbstractArrayQuery(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseSize(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    public Ret caseNonzeros(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    public Ret caseCumprod(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    public Ret caseCumsum(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }
    public Ret caseSign(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }

    public Ret caseAbstractScalarResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractArrayQuery(builtin,arg); }

    public Ret caseAbstractNumericalScalarResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractScalarResultArrayQuery(builtin,arg); }
    public Ret caseLength(Builtin builtin,Arg arg){ return caseAbstractNumericalScalarResultArrayQuery(builtin,arg); }
    public Ret caseNdims(Builtin builtin,Arg arg){ return caseAbstractNumericalScalarResultArrayQuery(builtin,arg); }
    public Ret caseNumel(Builtin builtin,Arg arg){ return caseAbstractNumericalScalarResultArrayQuery(builtin,arg); }
    public Ret caseNnz(Builtin builtin,Arg arg){ return caseAbstractNumericalScalarResultArrayQuery(builtin,arg); }

    public Ret caseAbstractLogicalScalarResultArrayQuery(Builtin builtin,Arg arg){ return caseAbstractScalarResultArrayQuery(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsemtpy(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsnan(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsinf(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsfinite(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsvector(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsscalar(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsreal(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    public Ret caseIsnumeric(Builtin builtin,Arg arg){ return caseAbstractLogicalScalarResultArrayQuery(builtin,arg); }
    
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