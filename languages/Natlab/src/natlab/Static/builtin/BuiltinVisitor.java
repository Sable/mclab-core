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
    //special operators ... not sure what to do with them
    public Ret caseColon(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseHorzcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseVertcat(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseNargin(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseNargout(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseEnd(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseCast(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //other operators
    public Ret caseIsequalwithequalnans(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //same as isequal
    public Ret caseIsequal(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //recursive equal all
    public Ret caseSubsasgn(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }    
    //explicit indexingops
    public Ret caseSubsref(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    public Ret caseStructfun(Builtin builtin,Arg arg){ return caseAbstractOperator(builtin,arg); }
    
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
    public Ret caseAbstractMatrixOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret casePlus(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMinus(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMtimes(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMpower(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMldivide(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    public Ret caseMrdivide(Builtin builtin,Arg arg){ return caseAbstractMatrixOperator(builtin,arg); }
    
    //array operators
    public Ret caseAbstractArrayOperator(Builtin builtin,Arg arg){ return caseAbstractNumericalBinaryOperator(builtin,arg); }
    public Ret caseTimes(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    public Ret caseLdivide(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    public Ret caseRdivide(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    public Ret casePower(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    public Ret casePow2(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }    
    //scalbn
    public Ret caseMod(Builtin builtin,Arg arg){ return caseAbstractArrayOperator(builtin,arg); }
    
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
    public Ret caseEps(Builtin builtin,Arg arg){ return caseAbstractNumericalUnaryOperator(builtin,arg); }

    public Ret caseAbstractLogicalUnaryOperator(Builtin builtin,Arg arg){ return caseAbstractUnaryOperator(builtin,arg); }
    public Ret caseNot(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAny(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseAll(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    public Ret caseIsempty(Builtin builtin,Arg arg){ return caseAbstractLogicalUnaryOperator(builtin,arg); }
    
    //*** matrix operations ************************************************
    public Ret caseAbstractMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }

    public Ret caseAbstractElementwiseMatrixOperation(Builtin builtin,Arg arg){ return caseAbstractMatrixOperation(builtin,arg); }
    public Ret caseSqrt(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErf(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfinv(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseErfcinv(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseGammainc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseBetainc(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }

    public Ret caseAbstractTranscendentalFunction(Builtin builtin,Arg arg){ return caseAbstractElementwiseMatrixOperation(builtin,arg); }
    public Ret caseExp(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseLog(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    
    //TODO group trigs together
    public Ret caseAbstractTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseSin(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCos(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseTan(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCot(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseSec(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }
    public Ret caseCsc(Builtin builtin,Arg arg){ return caseAbstractTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractDecimalTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseSind(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCosd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseTand(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCotd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseSecd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }
    public Ret caseCscd(Builtin builtin,Arg arg){ return caseAbstractDecimalTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicTrigonometricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseSinh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCosh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseTanh(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCoth(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseSech(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }
    public Ret caseCsch(Builtin builtin,Arg arg){ return caseAbstractHyperbolicTrigonometricFunction(builtin,arg); }

    public Ret caseAbstractInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseAsin(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcos(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtan2(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcot(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsec(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcsc(Builtin builtin,Arg arg){ return caseAbstractInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractDecimalInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
    public Ret caseAsind(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcosd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAtand(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcotd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAsecd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }
    public Ret caseAcscd(Builtin builtin,Arg arg){ return caseAbstractDecimalInverseTrigonmetricFunction(builtin,arg); }

    public Ret caseAbstractHyperbolicInverseTrigonmetricFunction(Builtin builtin,Arg arg){ return caseAbstractTranscendentalFunction(builtin,arg); }
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
    public Ret caseEig(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseNorm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseRank(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseDot(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseCross(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseSchur(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //factorization
    public Ret caseOrdschur(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseLu(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //factorization
    public Ret caseLinsolve(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //linear solve
    public Ret caseIfftn(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //n dimension inverse discrete fourier transform
    public Ret caseFftn(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //n dimensional discrete fourier transform
    public Ret caseRcond(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }    
    //analytical matrix operators
    public Ret caseExpm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseSqrtm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    public Ret caseLogm(Builtin builtin,Arg arg){ return caseAbstractMatrixComputation(builtin,arg); }
    
    //bit operators
    public Ret caseAbstractBitOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseBitand(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitxor(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitcmp(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitget(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    public Ret caseBitshift(Builtin builtin,Arg arg){ return caseAbstractBitOperation(builtin,arg); }
    
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
    
    //*** Array operations **************************************************
    public Ret caseAbstractArrayOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseSort(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseUnique(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }

    public Ret caseAbstractArrayConstructor(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseOnes(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseZeros(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseDiag(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseEye(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }    
    //changing arrays
    public Ret caseReshape(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseSqueeze(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }
    public Ret caseFind(Builtin builtin,Arg arg){ return caseAbstractArrayConstructor(builtin,arg); }

    public Ret caseAbstractArrayQueryOperation(Builtin builtin,Arg arg){ return caseAbstractArrayOperation(builtin,arg); }
    public Ret caseMean(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseMin(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseMax(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseNumel(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseLength(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseSize(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseSum(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseProd(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }    
    //true/false queries - unary op?
    public Ret caseIsemtpy(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    public Ret caseIsinteger(Builtin builtin,Arg arg){ return caseAbstractArrayQueryOperation(builtin,arg); }
    
    //*** regulat expressions ************************************************
    public Ret caseAbstractRegexpOperation(Builtin builtin,Arg arg){ return caseAbstractPureFunction(builtin,arg); }
    public Ret caseRegexptranslate(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    public Ret caseRegexp(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    public Ret caseRegexpi(Builtin builtin,Arg arg){ return caseAbstractRegexpOperation(builtin,arg); }
    
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

    public Ret caseAbstractReportFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseError(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseWarning(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    public Ret caseEcho(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }    
    //should this be here? - this could be a strict lib function?
    public Ret caseDiary(Builtin builtin,Arg arg){ return caseAbstractReportFunction(builtin,arg); }
    
    //something logging related
    public Ret caseAbstractRandomFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseRand(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }
    public Ret caseRandi(Builtin builtin,Arg arg){ return caseAbstractRandomFunction(builtin,arg); }

    public Ret caseAbstractIoFunction(Builtin builtin,Arg arg){ return caseAbstractImpureFunction(builtin,arg); }
    public Ret caseFprintf(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseSprintf(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseLoad(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    public Ret caseDisp(Builtin builtin,Arg arg){ return caseAbstractIoFunction(builtin,arg); }
    
    //*** library funcitons that are not builtins!! TODO **********************
    public Ret caseAbstractNotABuiltin(Builtin builtin,Arg arg){ return caseBuiltin(builtin,arg); }
    public Ret caseConv(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseToeplitz(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseDyaddown(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseFlipud(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseLinspace(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
    public Ret caseImwrite(Builtin builtin,Arg arg){ return caseAbstractNotABuiltin(builtin,arg); }
}