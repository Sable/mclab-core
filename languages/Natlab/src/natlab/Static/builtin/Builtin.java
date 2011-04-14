
package natlab.Static.builtin;

import java.util.HashMap;
//import natlab.toolkits.path.BuiltinQuery;


public abstract class Builtin /*implements BuiltinQuery*/ {
    private static HashMap<String, Builtin> builtinMap = new HashMap<String, Builtin>();
    public static void main(String[] args) {
        System.out.println(create("i"));
        Builtin b = builtinMap.get("i");
        System.out.println(b+"  "+b.create());
        System.out.println("number of builtins "+builtinMap.size());
        System.out.println(builtinMap);
    }

    /**
     * creates a builtin from the given name (case sensitive)
     */
    public static final Builtin create(String name){
        if (builtinMap.containsKey(name)){
            return builtinMap.get(name).create();
        } else {
            throw new UnsupportedOperationException("cannot create nonexistent builtin "+name);
        }
    }
    
    /**
     * returns a builtin query object returning true for all builtings in this class hierarchy
     *
    public static BuiltinQuery getBuiltinQuery() {
        return new BuiltinQuery(){
            isBuiltin(String functionname) { return builtinMap.containsKey(functionname); }
        }
    } /* */

    
    /**
     * calls the BuiltinVisitor method associated with this Builtin, using the given argument,
     * and returns the value returned by the visitor.
     * (e.g. if this is a Builtin.Plus, calls visitor.casePlus)
     */
    public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
        return visitor.caseBuiltin(this,arg);
    }
    
    /**
     * allows the instantiation of Builtins - used to create more builtins using the builtinMap
     */
    //todo should all builtins of a given type be the same, so that this is not required?
    protected Builtin create(){return null;}

    /**
     * Returns the name of the builtin function that this object is referring to
     */
    public abstract String getName();
    
    public String toString() {
        return getName();
    }
    
    public int hashCode() {
        return getName().hashCode();
    }

    //static initializer fills in builtinMap
    static {
        builtinMap.put("i",new I());
        builtinMap.put("j",new J());
        builtinMap.put("pi",new Pi());
        builtinMap.put("true",new True());
        builtinMap.put("false",new False());
        builtinMap.put("eq",new Eq());
        builtinMap.put("ne",new Ne());
        builtinMap.put("lt",new Lt());
        builtinMap.put("ge",new Ge());
        builtinMap.put("and",new And());
        builtinMap.put("or",new Or());
        builtinMap.put("xor",new Xor());
        builtinMap.put("plus",new Plus());
        builtinMap.put("mtimes",new Mtimes());
        builtinMap.put("mpower",new Mpower());
        builtinMap.put("mldivide",new Mldivide());
        builtinMap.put("mrdivide",new Mrdivide());
        builtinMap.put("times",new Times());
        builtinMap.put("uplus",new Uplus());
        builtinMap.put("uminus",new Uminus());
        builtinMap.put("transpose",new Transpose());
        builtinMap.put("ctranspose",new Ctranspose());
        builtinMap.put("conj",new Conj());
        builtinMap.put("not",new Not());
        builtinMap.put("any",new Any());
        builtinMap.put("all",new All());
        builtinMap.put("exp",new Exp());
        builtinMap.put("log",new Log());
        builtinMap.put("sin",new Sin());
        builtinMap.put("cos",new Cos());
        builtinMap.put("tan",new Tan());
        builtinMap.put("asin",new Asin());
        builtinMap.put("acos",new Acos());
        builtinMap.put("atan",new Atan());
        builtinMap.put("real",new Real());
        builtinMap.put("imag",new Imag());
        builtinMap.put("toeplitz",new Toeplitz());
        builtinMap.put("impureFunction",new ImpureFunction());
    }    
    
    //the actual Builtin Classes:
    
    public static abstract class AbstractBuiltin extends Builtin {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBuiltin(this,arg);
        }
    }
    public static abstract class AbstractPureFunction extends AbstractBuiltin {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPureFunction(this,arg);
        }
    }
    public static abstract class AbstractConstant extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstant(this,arg);
        }
    }
    public static abstract class AbstractNumericalConstant extends AbstractConstant {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalConstant(this,arg);
        }
    }
    public static class I extends AbstractNumericalConstant {
        //creates a new instance of this class
        protected Builtin create(){
            return new I();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseI(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "i";
        }
    }
    public static class J extends AbstractNumericalConstant {
        //creates a new instance of this class
        protected Builtin create(){
            return new J();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseJ(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "j";
        }
    }
    public static class Pi extends AbstractNumericalConstant {
        //creates a new instance of this class
        protected Builtin create(){
            return new Pi();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePi(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "pi";
        }
    }
    public static abstract class AbstractLogicalConstant extends AbstractConstant {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalConstant(this,arg);
        }
    }
    public static class True extends AbstractLogicalConstant {
        //creates a new instance of this class
        protected Builtin create(){
            return new True();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTrue(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "true";
        }
    }
    public static class False extends AbstractLogicalConstant {
        //creates a new instance of this class
        protected Builtin create(){
            return new False();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFalse(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "false";
        }
    }
    public static abstract class AbstractOperator extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractOperator(this,arg);
        }
    }
    public static abstract class AbstractBinaryOperator extends AbstractOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryOperator(this,arg);
        }
    }
    public static abstract class AbstractRelationalOperator extends AbstractBinaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRelationalOperator(this,arg);
        }
    }
    public static class Eq extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Eq();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEq(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eq";
        }
    }
    public static class Ne extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ne();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNe(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ne";
        }
    }
    public static class Lt extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Lt();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLt(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "lt";
        }
    }
    public static class Ge extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ge();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseGe(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ge";
        }
    }
    public static abstract class AbstractBinaryLogicalOperator extends AbstractBinaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryLogicalOperator(this,arg);
        }
    }
    public static class And extends AbstractBinaryLogicalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new And();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAnd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "and";
        }
    }
    public static class Or extends AbstractBinaryLogicalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Or();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseOr(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "or";
        }
    }
    public static class Xor extends AbstractBinaryLogicalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Xor();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseXor(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "xor";
        }
    }
    public static abstract class AbstractNumericalBinaryOperator extends AbstractBinaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalBinaryOperator(this,arg);
        }
    }
    public static abstract class AbstractMatrixOperator extends AbstractNumericalBinaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixOperator(this,arg);
        }
    }
    public static class Plus extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Plus();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePlus(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "plus";
        }
    }
    public static class Mtimes extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mtimes();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMtimes(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mtimes";
        }
    }
    public static class Mpower extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mpower();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMpower(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mpower";
        }
    }
    public static class Mldivide extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mldivide();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMldivide(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mldivide";
        }
    }
    public static class Mrdivide extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mrdivide();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMrdivide(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mrdivide";
        }
    }
    public static abstract class AbstractArrayOperator extends AbstractNumericalBinaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayOperator(this,arg);
        }
    }
    public static class Times extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Times();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTimes(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "times";
        }
    }
    public static abstract class AbstractUnaryOperator extends AbstractOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryOperator(this,arg);
        }
    }
    public static abstract class AbstractNumericalUnaryOperator extends AbstractUnaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalUnaryOperator(this,arg);
        }
    }
    public static class Uplus extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uplus();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUplus(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uplus";
        }
    }
    public static class Uminus extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uminus();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUminus(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uminus";
        }
    }
    public static class Transpose extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Transpose();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTranspose(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "transpose";
        }
    }
    public static class Ctranspose extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ctranspose();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCtranspose(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ctranspose";
        }
    }
    public static class Conj extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Conj();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseConj(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "conj";
        }
    }
    public static abstract class AbstractLogicalUnaryOperator extends AbstractUnaryOperator {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalUnaryOperator(this,arg);
        }
    }
    public static class Not extends AbstractLogicalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Not();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "not";
        }
    }
    public static class Any extends AbstractLogicalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Any();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAny(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "any";
        }
    }
    public static class All extends AbstractLogicalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new All();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAll(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "all";
        }
    }
    public static abstract class AbstractMatrixOperation extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixOperation(this,arg);
        }
    }
    public static abstract class AbstractTranscendentalFunction extends AbstractMatrixOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractTranscendentalFunction(this,arg);
        }
    }
    public static class Exp extends AbstractTranscendentalFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Exp();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseExp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "exp";
        }
    }
    public static class Log extends AbstractTranscendentalFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Log();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLog(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "log";
        }
    }
    public static abstract class AbstractTrigonometricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractTrigonometricFunction(this,arg);
        }
    }
    public static class Sin extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sin();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sin";
        }
    }
    public static class Cos extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cos();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCos(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cos";
        }
    }
    public static class Tan extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tan();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTan(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tan";
        }
    }
    public static abstract class AbstractInverseTrigonmetricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractInverseTrigonmetricFunction(this,arg);
        }
    }
    public static class Asin extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asin();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asin";
        }
    }
    public static class Acos extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acos();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcos(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acos";
        }
    }
    public static class Atan extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Atan();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAtan(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "atan";
        }
    }
    public static class Real extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Real();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseReal(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "real";
        }
    }
    public static class Imag extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Imag();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImag(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "imag";
        }
    }
    public static class Toeplitz extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Toeplitz();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseToeplitz(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "toeplitz";
        }
    }
    public static class ImpureFunction extends AbstractBuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new ImpureFunction();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImpureFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "impureFunction";
        }
    }}