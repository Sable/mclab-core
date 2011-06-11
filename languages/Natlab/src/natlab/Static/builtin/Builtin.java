
package natlab.Static.builtin;

import java.util.HashMap;
import natlab.toolkits.path.BuiltinQuery;


public abstract class Builtin {
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
     */
    public static BuiltinQuery getBuiltinQuery() {
        return new BuiltinQuery(){
            public boolean isBuiltin(String functionname) 
              { return builtinMap.containsKey(functionname); }
        };
    }

    
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
        builtinMap.put("nargout",new Nargout());
        builtinMap.put("end",new End());
        builtinMap.put("cast",new Cast());
        builtinMap.put("isequalwithequalnans",new Isequalwithequalnans());
        builtinMap.put("isequal",new Isequal());
        builtinMap.put("subsasgn",new Subsasgn());
        builtinMap.put("subsref",new Subsref());
        builtinMap.put("structfun",new Structfun());
        builtinMap.put("eq",new Eq());
        builtinMap.put("ne",new Ne());
        builtinMap.put("lt",new Lt());
        builtinMap.put("le",new Le());
        builtinMap.put("ge",new Ge());
        builtinMap.put("gt",new Gt());
        builtinMap.put("and",new And());
        builtinMap.put("or",new Or());
        builtinMap.put("xor",new Xor());
        builtinMap.put("plus",new Plus());
        builtinMap.put("minus",new Minus());
        builtinMap.put("mtimes",new Mtimes());
        builtinMap.put("mpower",new Mpower());
        builtinMap.put("mldivide",new Mldivide());
        builtinMap.put("mrdivide",new Mrdivide());
        builtinMap.put("times",new Times());
        builtinMap.put("ldivide",new Ldivide());
        builtinMap.put("rdivide",new Rdivide());
        builtinMap.put("power",new Power());
        builtinMap.put("pow2",new Pow2());
        builtinMap.put("mod",new Mod());
        builtinMap.put("uplus",new Uplus());
        builtinMap.put("uminus",new Uminus());
        builtinMap.put("transpose",new Transpose());
        builtinMap.put("ctranspose",new Ctranspose());
        builtinMap.put("conj",new Conj());
        builtinMap.put("real",new Real());
        builtinMap.put("imag",new Imag());
        builtinMap.put("abs",new Abs());
        builtinMap.put("eps",new Eps());
        builtinMap.put("not",new Not());
        builtinMap.put("any",new Any());
        builtinMap.put("all",new All());
        builtinMap.put("isempty",new Isempty());
        builtinMap.put("sqrt",new Sqrt());
        builtinMap.put("erf",new Erf());
        builtinMap.put("erfinv",new Erfinv());
        builtinMap.put("erfc",new Erfc());
        builtinMap.put("erfcinv",new Erfcinv());
        builtinMap.put("gammainc",new Gammainc());
        builtinMap.put("betainc",new Betainc());
        builtinMap.put("exp",new Exp());
        builtinMap.put("log",new Log());
        builtinMap.put("sin",new Sin());
        builtinMap.put("cos",new Cos());
        builtinMap.put("tan",new Tan());
        builtinMap.put("cot",new Cot());
        builtinMap.put("sec",new Sec());
        builtinMap.put("csc",new Csc());
        builtinMap.put("sind",new Sind());
        builtinMap.put("cosd",new Cosd());
        builtinMap.put("tand",new Tand());
        builtinMap.put("cotd",new Cotd());
        builtinMap.put("secd",new Secd());
        builtinMap.put("cscd",new Cscd());
        builtinMap.put("sinh",new Sinh());
        builtinMap.put("cosh",new Cosh());
        builtinMap.put("tanh",new Tanh());
        builtinMap.put("coth",new Coth());
        builtinMap.put("sech",new Sech());
        builtinMap.put("csch",new Csch());
        builtinMap.put("asin",new Asin());
        builtinMap.put("acos",new Acos());
        builtinMap.put("atan",new Atan());
        builtinMap.put("atan2",new Atan2());
        builtinMap.put("acot",new Acot());
        builtinMap.put("asec",new Asec());
        builtinMap.put("acsc",new Acsc());
        builtinMap.put("asind",new Asind());
        builtinMap.put("acosd",new Acosd());
        builtinMap.put("atand",new Atand());
        builtinMap.put("acotd",new Acotd());
        builtinMap.put("asecd",new Asecd());
        builtinMap.put("acscd",new Acscd());
        builtinMap.put("asinh",new Asinh());
        builtinMap.put("acosh",new Acosh());
        builtinMap.put("atanh",new Atanh());
        builtinMap.put("acoth",new Acoth());
        builtinMap.put("asech",new Asech());
        builtinMap.put("acsch",new Acsch());
        builtinMap.put("fix",new Fix());
        builtinMap.put("round",new Round());
        builtinMap.put("floor",new Floor());
        builtinMap.put("ceil",new Ceil());
        builtinMap.put("eig",new Eig());
        builtinMap.put("norm",new Norm());
        builtinMap.put("rank",new Rank());
        builtinMap.put("dot",new Dot());
        builtinMap.put("cross",new Cross());
        builtinMap.put("schur",new Schur());
        builtinMap.put("ordschur",new Ordschur());
        builtinMap.put("lu",new Lu());
        builtinMap.put("linsolve",new Linsolve());
        builtinMap.put("ifftn",new Ifftn());
        builtinMap.put("fftn",new Fftn());
        builtinMap.put("rcond",new Rcond());
        builtinMap.put("expm",new Expm());
        builtinMap.put("sqrtm",new Sqrtm());
        builtinMap.put("logm",new Logm());
        builtinMap.put("bitand",new Bitand());
        builtinMap.put("bitor",new Bitor());
        builtinMap.put("bitxor",new Bitxor());
        builtinMap.put("bitcmp",new Bitcmp());
        builtinMap.put("bitget",new Bitget());
        builtinMap.put("bitshift",new Bitshift());
        builtinMap.put("double",new Double());
        builtinMap.put("single",new Single());
        builtinMap.put("char",new Char());
        builtinMap.put("logical",new Logical());
        builtinMap.put("int8",new Int8());
        builtinMap.put("int16",new Int16());
        builtinMap.put("int32",new Int32());
        builtinMap.put("int64",new Int64());
        builtinMap.put("uint8",new Uint8());
        builtinMap.put("uint16",new Uint16());
        builtinMap.put("uint32",new Uint32());
        builtinMap.put("uint64",new Uint64());
        builtinMap.put("cell",new Cell());
        builtinMap.put("struct",new Struct());
        builtinMap.put("sort",new Sort());
        builtinMap.put("unique",new Unique());
        builtinMap.put("ones",new Ones());
        builtinMap.put("zeros",new Zeros());
        builtinMap.put("diag",new Diag());
        builtinMap.put("eye",new Eye());
        builtinMap.put("reshape",new Reshape());
        builtinMap.put("squeeze",new Squeeze());
        builtinMap.put("find",new Find());
        builtinMap.put("mean",new Mean());
        builtinMap.put("min",new Min());
        builtinMap.put("max",new Max());
        builtinMap.put("numel",new Numel());
        builtinMap.put("length",new Length());
        builtinMap.put("size",new Size());
        builtinMap.put("sum",new Sum());
        builtinMap.put("prod",new Prod());
        builtinMap.put("isemtpy",new Isemtpy());
        builtinMap.put("isinteger",new Isinteger());
        builtinMap.put("regexptranslate",new Regexptranslate());
        builtinMap.put("regexp",new Regexp());
        builtinMap.put("regexpi",new Regexpi());
        builtinMap.put("superiorto",new Superiorto());
        builtinMap.put("exit",new Exit());
        builtinMap.put("quit",new Quit());
        builtinMap.put("clock",new Clock());
        builtinMap.put("tic",new Tic());
        builtinMap.put("toc",new Toc());
        builtinMap.put("error",new Error());
        builtinMap.put("warning",new Warning());
        builtinMap.put("echo",new Echo());
        builtinMap.put("diary",new Diary());
        builtinMap.put("rand",new Rand());
        builtinMap.put("randi",new Randi());
        builtinMap.put("fprintf",new Fprintf());
        builtinMap.put("sprintf",new Sprintf());
        builtinMap.put("load",new Load());
        builtinMap.put("disp",new Disp());
        builtinMap.put("conv",new Conv());
        builtinMap.put("toeplitz",new Toeplitz());
        builtinMap.put("dyaddown",new Dyaddown());
        builtinMap.put("flipud",new Flipud());
        builtinMap.put("linspace",new Linspace());
        builtinMap.put("imwrite",new Imwrite());
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
    public static class Nargout extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Nargout();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargout(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargout";
        }
    }
    public static class End extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new End();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEnd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "end";
        }
    }
    public static class Cast extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cast();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCast(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cast";
        }
    }
    public static class Isequalwithequalnans extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isequalwithequalnans();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsequalwithequalnans(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isequalwithequalnans";
        }
    }
    public static class Isequal extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isequal();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsequal(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isequal";
        }
    }
    public static class Subsasgn extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Subsasgn();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSubsasgn(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "subsasgn";
        }
    }
    public static class Subsref extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Subsref();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSubsref(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "subsref";
        }
    }
    public static class Structfun extends AbstractOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Structfun();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStructfun(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "structfun";
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
    public static class Le extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Le();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLe(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "le";
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
    public static class Gt extends AbstractRelationalOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Gt();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseGt(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "gt";
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
    public static class Minus extends AbstractMatrixOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Minus();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMinus(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "minus";
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
    public static class Power extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Power();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePower(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "power";
        }
    }
    public static class Pow2 extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Pow2();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePow2(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "pow2";
        }
    }
    public static class Mod extends AbstractArrayOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mod();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMod(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mod";
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
    public static class Eps extends AbstractNumericalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Eps();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEps(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eps";
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
    public static class Isempty extends AbstractLogicalUnaryOperator {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isempty();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsempty(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isempty";
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
    public static class Erf extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Erf();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseErf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "erf";
        }
    }
    public static class Erfinv extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Erfinv();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseErfinv(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "erfinv";
        }
    }
    public static class Erfc extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Erfc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseErfc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "erfc";
        }
    }
    public static class Erfcinv extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Erfcinv();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseErfcinv(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "erfcinv";
        }
    }
    public static class Gammainc extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Gammainc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseGammainc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "gammainc";
        }
    }
    public static class Betainc extends AbstractElementwiseMatrixOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Betainc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBetainc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "betainc";
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
    public static class Cot extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cot();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cot";
        }
    }
    public static class Sec extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sec();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSec(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sec";
        }
    }
    public static class Csc extends AbstractTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Csc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCsc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "csc";
        }
    }
    public static abstract class AbstractDecimalTrigonometricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalTrigonometricFunction(this,arg);
        }
    }
    public static class Sind extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sind();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sind";
        }
    }
    public static class Cosd extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cosd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCosd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cosd";
        }
    }
    public static class Tand extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tand();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTand(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tand";
        }
    }
    public static class Cotd extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cotd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCotd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cotd";
        }
    }
    public static class Secd extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Secd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSecd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "secd";
        }
    }
    public static class Cscd extends AbstractDecimalTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cscd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCscd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cscd";
        }
    }
    public static abstract class AbstractHyperbolicTrigonometricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicTrigonometricFunction(this,arg);
        }
    }
    public static class Sinh extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sinh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSinh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sinh";
        }
    }
    public static class Cosh extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cosh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCosh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cosh";
        }
    }
    public static class Tanh extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tanh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTanh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tanh";
        }
    }
    public static class Coth extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Coth();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCoth(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "coth";
        }
    }
    public static class Sech extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sech();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSech(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sech";
        }
    }
    public static class Csch extends AbstractHyperbolicTrigonometricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Csch();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCsch(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "csch";
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
    public static class Atan2 extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Atan2();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAtan2(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "atan2";
        }
    }
    public static class Acot extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acot();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acot";
        }
    }
    public static class Asec extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asec();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsec(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asec";
        }
    }
    public static class Acsc extends AbstractInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acsc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcsc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acsc";
        }
    }
    public static abstract class AbstractDecimalInverseTrigonmetricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalInverseTrigonmetricFunction(this,arg);
        }
    }
    public static class Asind extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asind();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asind";
        }
    }
    public static class Acosd extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acosd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcosd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acosd";
        }
    }
    public static class Atand extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Atand();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAtand(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "atand";
        }
    }
    public static class Acotd extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acotd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcotd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acotd";
        }
    }
    public static class Asecd extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asecd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsecd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asecd";
        }
    }
    public static class Acscd extends AbstractDecimalInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acscd();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcscd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acscd";
        }
    }
    public static abstract class AbstractHyperbolicInverseTrigonmetricFunction extends AbstractTranscendentalFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicInverseTrigonmetricFunction(this,arg);
        }
    }
    public static class Asinh extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asinh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsinh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asinh";
        }
    }
    public static class Acosh extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acosh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcosh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acosh";
        }
    }
    public static class Atanh extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Atanh();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAtanh(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "atanh";
        }
    }
    public static class Acoth extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acoth();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcoth(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acoth";
        }
    }
    public static class Asech extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Asech();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAsech(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "asech";
        }
    }
    public static class Acsch extends AbstractHyperbolicInverseTrigonmetricFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Acsch();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAcsch(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "acsch";
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
    public static class Dot extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Dot();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dot";
        }
    }
    public static class Cross extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cross();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCross(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cross";
        }
    }
    public static class Schur extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Schur();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSchur(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "schur";
        }
    }
    public static class Ordschur extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ordschur();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseOrdschur(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ordschur";
        }
    }
    public static class Lu extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Lu();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLu(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "lu";
        }
    }
    public static class Linsolve extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Linsolve();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLinsolve(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "linsolve";
        }
    }
    public static class Ifftn extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ifftn();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIfftn(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ifftn";
        }
    }
    public static class Fftn extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Fftn();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFftn(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fftn";
        }
    }
    public static class Rcond extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Rcond();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRcond(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rcond";
        }
    }
    public static class Expm extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Expm();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseExpm(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "expm";
        }
    }
    public static class Sqrtm extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sqrtm();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSqrtm(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sqrtm";
        }
    }
    public static class Logm extends AbstractMatrixComputation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Logm();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLogm(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "logm";
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
    public static abstract class AbstractConstructor extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstructor(this,arg);
        }
    }
    public static abstract class AbstractPrimitiveConstructor extends AbstractConstructor {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPrimitiveConstructor(this,arg);
        }
    }
    public static class Double extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Double();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDouble(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "double";
        }
    }
    public static class Single extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Single();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSingle(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "single";
        }
    }
    public static class Char extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Char();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseChar(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "char";
        }
    }
    public static class Logical extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Logical();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLogical(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "logical";
        }
    }
    public static class Int8 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Int8();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt8(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int8";
        }
    }
    public static class Int16 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Int16();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt16(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int16";
        }
    }
    public static class Int32 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Int32();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt32(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int32";
        }
    }
    public static class Int64 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Int64();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt64(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int64";
        }
    }
    public static class Uint8 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uint8();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint8(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint8";
        }
    }
    public static class Uint16 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uint16();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint16(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint16";
        }
    }
    public static class Uint32 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uint32();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint32(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint32";
        }
    }
    public static class Uint64 extends AbstractPrimitiveConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Uint64();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint64(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint64";
        }
    }
    public static abstract class AbstractCompoundConstructor extends AbstractConstructor {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractCompoundConstructor(this,arg);
        }
    }
    public static class Cell extends AbstractCompoundConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cell();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCell(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cell";
        }
    }
    public static class Struct extends AbstractCompoundConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Struct();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStruct(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "struct";
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
    public static class Unique extends AbstractArrayOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Unique();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUnique(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "unique";
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
    public static class Diag extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Diag();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDiag(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "diag";
        }
    }
    public static class Eye extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Eye();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEye(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eye";
        }
    }
    public static class Reshape extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Reshape();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseReshape(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "reshape";
        }
    }
    public static class Squeeze extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Squeeze();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSqueeze(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "squeeze";
        }
    }
    public static class Find extends AbstractArrayConstructor {
        //creates a new instance of this class
        protected Builtin create(){
            return new Find();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "find";
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
    public static class Max extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Max();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMax(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "max";
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
    public static class Length extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Length();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLength(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "length";
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
    public static class Isemtpy extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isemtpy();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsemtpy(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isemtpy";
        }
    }
    public static class Isinteger extends AbstractArrayQueryOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isinteger();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsinteger(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isinteger";
        }
    }
    public static abstract class AbstractRegexpOperation extends AbstractPureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRegexpOperation(this,arg);
        }
    }
    public static class Regexptranslate extends AbstractRegexpOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Regexptranslate();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRegexptranslate(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "regexptranslate";
        }
    }
    public static class Regexp extends AbstractRegexpOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Regexp();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRegexp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "regexp";
        }
    }
    public static class Regexpi extends AbstractRegexpOperation {
        //creates a new instance of this class
        protected Builtin create(){
            return new Regexpi();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRegexpi(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "regexpi";
        }
    }
    public static abstract class AbstractImpureFunction extends Builtin {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImpureFunction(this,arg);
        }
    }
    public static class Superiorto extends AbstractImpureFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Superiorto();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSuperiorto(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "superiorto";
        }
    }
    public static class Exit extends AbstractImpureFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Exit();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseExit(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "exit";
        }
    }
    public static class Quit extends AbstractImpureFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Quit();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseQuit(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "quit";
        }
    }
    public static abstract class AbstractBuiltin extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBuiltin(this,arg);
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
    public static abstract class AbstractReportFunction extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractReportFunction(this,arg);
        }
    }
    public static class Error extends AbstractReportFunction {
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
    public static class Warning extends AbstractReportFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Warning();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseWarning(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "warning";
        }
    }
    public static class Echo extends AbstractReportFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Echo();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEcho(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "echo";
        }
    }
    public static class Diary extends AbstractReportFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Diary();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDiary(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "diary";
        }
    }
    public static abstract class AbstractRandomFunction extends AbstractImpureFunction {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRandomFunction(this,arg);
        }
    }
    public static class Rand extends AbstractRandomFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Rand();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRand(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rand";
        }
    }
    public static class Randi extends AbstractRandomFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Randi();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRandi(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "randi";
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
    public static class Sprintf extends AbstractIoFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sprintf();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSprintf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sprintf";
        }
    }
    public static class Load extends AbstractIoFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Load();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLoad(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "load";
        }
    }
    public static class Disp extends AbstractIoFunction {
        //creates a new instance of this class
        protected Builtin create(){
            return new Disp();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDisp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "disp";
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
    public static class Linspace extends AbstractNotABuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new Linspace();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLinspace(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "linspace";
        }
    }
    public static class Imwrite extends AbstractNotABuiltin {
        //creates a new instance of this class
        protected Builtin create(){
            return new Imwrite();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImwrite(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "imwrite";
        }
    }
}