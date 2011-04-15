
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
        builtinMap.put("colon",new Colon());
        builtinMap.put("horzcat",new Horzcat());
        builtinMap.put("vertcat",new Vertcat());
        builtinMap.put("nargin",new Nargin());
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
        builtinMap.put("ldivide",new Ldivide());
        builtinMap.put("rdivide",new Rdivide());
        builtinMap.put("uplus",new Uplus());
        builtinMap.put("uminus",new Uminus());
        builtinMap.put("transpose",new Transpose());
        builtinMap.put("ctranspose",new Ctranspose());
        builtinMap.put("conj",new Conj());
        builtinMap.put("real",new Real());
        builtinMap.put("imag",new Imag());
        builtinMap.put("abs",new Abs());
        builtinMap.put("not",new Not());
        builtinMap.put("any",new Any());
        builtinMap.put("all",new All());
        builtinMap.put("sqrt",new Sqrt());
        builtinMap.put("exp",new Exp());
        builtinMap.put("log",new Log());
        builtinMap.put("sin",new Sin());
        builtinMap.put("cos",new Cos());
        builtinMap.put("tan",new Tan());
        builtinMap.put("asin",new Asin());
        builtinMap.put("acos",new Acos());
        builtinMap.put("atan",new Atan());
        builtinMap.put("fix",new Fix());
        builtinMap.put("round",new Round());
        builtinMap.put("floor",new Floor());
        builtinMap.put("ceil",new Ceil());
        builtinMap.put("eig",new Eig());
        builtinMap.put("norm",new Norm());
        builtinMap.put("rank",new Rank());
        builtinMap.put("bitand",new Bitand());
        builtinMap.put("bitor",new Bitor());
        builtinMap.put("bitxor",new Bitxor());
        builtinMap.put("bitcmp",new Bitcmp());
        builtinMap.put("bitget",new Bitget());
        builtinMap.put("bitshift",new Bitshift());
        builtinMap.put("sort",new Sort());
        builtinMap.put("ones",new Ones());
        builtinMap.put("zeros",new Zeros());
        builtinMap.put("mean",new Mean());
        builtinMap.put("min",new Min());
        builtinMap.put("numel",new Numel());
        builtinMap.put("size",new Size());
        builtinMap.put("sum",new Sum());
        builtinMap.put("prod",new Prod());
        builtinMap.put("clock",new Clock());
        builtinMap.put("tic",new Tic());
        builtinMap.put("toc",new Toc());
        builtinMap.put("error",new Error());
        builtinMap.put("fprintf",new Fprintf());
        builtinMap.put("conv",new Conv());
        builtinMap.put("toeplitz",new Toeplitz());
        builtinMap.put("dyaddown",new Dyaddown());
        builtinMap.put("flipud",new Flipud());
    }    
    
    //the actual Builtin Classes:
    
    public static abstract class AbstractPureFunction extends Builtin {
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
    public static class Colon extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Colon();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseColon(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "colon";
        }
    }
    public static class Horzcat extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Horzcat();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseHorzcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "horzcat";
        }
    }
    public static class Vertcat extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Vertcat();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseVertcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "vertcat";
        }
    }
    public static class Nargin extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Nargin();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargin";
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
    public static class Ldivide extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ldivide();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLdivide(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ldivide";
        }
    }
    public static class Rdivide extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Rdivide();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRdivide(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rdivide";
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
    public static class Real extends AbstractNumericalUnaryOperator {
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
    public static class Imag extends AbstractNumericalUnaryOperator {
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
    public static class Abs extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Abs();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbs(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "abs";
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
    public static abstract class AbstractElementwiseMatrixOperation extends AbstractMatrixOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementwiseMatrixOperation(this,arg);
        }
    }
    public static class Sqrt extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sqrt();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSqrt(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sqrt";
        }
    }
    public static abstract class AbstractTranscendentalFunction extends AbstractElementwiseMatrixOperation {
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
    public static abstract class AbstractRoundingOperation extends AbstractElementwiseMatrixOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRoundingOperation(this,arg);
        }
    }
    public static class Fix extends AbstractRoundingOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Fix();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFix(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fix";
        }
    }
    public static class Round extends AbstractRoundingOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Round();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRound(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "round";
        }
    }
    public static class Floor extends AbstractRoundingOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Floor();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFloor(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "floor";
        }
    }
    public static class Ceil extends AbstractRoundingOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ceil();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCeil(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ceil";
        }
    }
    public static abstract class AbstractMatrixComputation extends AbstractMatrixOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixComputation(this,arg);
        }
    }
    public static class Eig extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Eig();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEig(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eig";
        }
    }
    public static class Norm extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Norm();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNorm(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "norm";
        }
    }
    public static class Rank extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Rank();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRank(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rank";
        }
    }
    public static abstract class AbstractBitOperation extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBitOperation(this,arg);
        }
    }
    public static class Bitand extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitand();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitand(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitand";
        }
    }
    public static class Bitor extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitor();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitor(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitor";
        }
    }
    public static class Bitxor extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitxor();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitxor(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitxor";
        }
    }
    public static class Bitcmp extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitcmp();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitcmp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitcmp";
        }
    }
    public static class Bitget extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitget();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitget(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitget";
        }
    }
    public static class Bitshift extends AbstractBitOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitshift();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitshift(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitshift";
        }
    }
    public static abstract class AbstractArrayOperation extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayOperation(this,arg);
        }
    }
    public static class Sort extends AbstractArrayOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sort();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSort(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sort";
        }
    }
    public static abstract class AbstractArrayConstructor extends AbstractArrayOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayConstructor(this,arg);
        }
    }
    public static class Ones extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ones();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseOnes(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ones";
        }
    }
    public static class Zeros extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Zeros();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseZeros(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "zeros";
        }
    }
    public static abstract class AbstractArrayQueryOperation extends AbstractArrayOperation {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayQueryOperation(this,arg);
        }
    }
    public static class Mean extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mean();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMean(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mean";
        }
    }
    public static class Min extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Min();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "min";
        }
    }
    public static class Numel extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Numel();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNumel(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "numel";
        }
    }
    public static class Size extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Size();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSize(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "size";
        }
    }
    public static class Sum extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sum();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSum(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sum";
        }
    }
    public static class Prod extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Prod();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseProd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "prod";
        }
    }
    public static abstract class AbstractImpureFunction extends Builtin {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImpureFunction(this,arg);
        }
    }
    public static abstract class AbstractTimeFunction extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractTimeFunction(this,arg);
        }
    }
    public static class Clock extends AbstractTimeFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Clock();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseClock(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "clock";
        }
    }
    public static class Tic extends AbstractTimeFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tic();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTic(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tic";
        }
    }
    public static class Toc extends AbstractTimeFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Toc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseToc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "toc";
        }
    }
    public static abstract class AbstractReportFuncgion extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractReportFuncgion(this,arg);
        }
    }
    public static class Error extends AbstractReportFuncgion {
        //creates a new instance of this class
        protected Builtin create(){
            return new Error();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseError(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "error";
        }
    }
    public static abstract class AbstractIoFunction extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractIoFunction(this,arg);
        }
    }
    public static class Fprintf extends AbstractIoFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Fprintf();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFprintf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fprintf";
        }
    }
    public static abstract class AbstractNotABuiltin extends Builtin {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNotABuiltin(this,arg);
        }
    }
    public static class Conv extends AbstractNotABuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new Conv();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseConv(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "conv";
        }
    }
    public static class Toeplitz extends AbstractNotABuiltin {
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
    public static class Dyaddown extends AbstractNotABuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new Dyaddown();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDyaddown(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dyaddown";
        }
    }
    public static class Flipud extends AbstractNotABuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new Flipud();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFlipud(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "flipud";
        }
    }
}