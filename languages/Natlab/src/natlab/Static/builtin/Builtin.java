
package natlab.Static.builtin;

import java.util.HashMap;
import natlab.toolkits.path.BuiltinQuery;


public abstract class Builtin {
    private static HashMap<String, Builtin> builtinMap = new HashMap<String, Builtin>();
    public static void main(String[] args) {
        java.lang.System.out.println(getInstance("i"));
        Builtin b = builtinMap.get("i");
        java.lang.System.out.println(b);
        java.lang.System.out.println("number of builtins "+builtinMap.size());
        java.lang.System.out.println(builtinMap);
    }

    /**
     * returns the builtin from the given name (case sensitive)
     * if there is no builtin, returns null.
     */
    public static final Builtin getInstance(String name){
        if (builtinMap.containsKey(name)){
            return builtinMap.get(name);
        } else {
            return null;
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
        builtinMap.put("pi",Pi.getInstance());
        builtinMap.put("test",Test.getInstance());
        builtinMap.put("transpose",Transpose.getInstance());
        builtinMap.put("ctranspose",Ctranspose.getInstance());
        builtinMap.put("tril",Tril.getInstance());
        builtinMap.put("triu",Triu.getInstance());
        builtinMap.put("dimensionCollapsingAnyMatrixFunction",DimensionCollapsingAnyMatrixFunction.getInstance());
        builtinMap.put("fix",Fix.getInstance());
        builtinMap.put("round",Round.getInstance());
        builtinMap.put("floor",Floor.getInstance());
        builtinMap.put("ceil",Ceil.getInstance());
        builtinMap.put("matrixUnaryNumericMatrixFunction",MatrixUnaryNumericMatrixFunction.getInstance());
        builtinMap.put("plus",Plus.getInstance());
        builtinMap.put("minus",Minus.getInstance());
        builtinMap.put("times",Times.getInstance());
        builtinMap.put("ldivide",Ldivide.getInstance());
        builtinMap.put("rdivide",Rdivide.getInstance());
        builtinMap.put("mod",Mod.getInstance());
        builtinMap.put("rem",Rem.getInstance());
        builtinMap.put("mtimes",Mtimes.getInstance());
        builtinMap.put("mldivide",Mldivide.getInstance());
        builtinMap.put("mrdivide",Mrdivide.getInstance());
        builtinMap.put("min",Min.getInstance());
        builtinMap.put("max",Max.getInstance());
        builtinMap.put("sqrt",Sqrt.getInstance());
        builtinMap.put("realsqrt",Realsqrt.getInstance());
        builtinMap.put("erf",Erf.getInstance());
        builtinMap.put("erfinv",Erfinv.getInstance());
        builtinMap.put("erfc",Erfc.getInstance());
        builtinMap.put("erfcinv",Erfcinv.getInstance());
        builtinMap.put("gamma",Gamma.getInstance());
        builtinMap.put("gammainc",Gammainc.getInstance());
        builtinMap.put("betainc",Betainc.getInstance());
        builtinMap.put("gammaln",Gammaln.getInstance());
        builtinMap.put("exp",Exp.getInstance());
        builtinMap.put("log",Log.getInstance());
        builtinMap.put("log2",Log2.getInstance());
        builtinMap.put("log10",Log10.getInstance());
        builtinMap.put("sin",Sin.getInstance());
        builtinMap.put("cos",Cos.getInstance());
        builtinMap.put("tan",Tan.getInstance());
        builtinMap.put("cot",Cot.getInstance());
        builtinMap.put("sec",Sec.getInstance());
        builtinMap.put("csc",Csc.getInstance());
        builtinMap.put("sind",Sind.getInstance());
        builtinMap.put("cosd",Cosd.getInstance());
        builtinMap.put("tand",Tand.getInstance());
        builtinMap.put("cotd",Cotd.getInstance());
        builtinMap.put("secd",Secd.getInstance());
        builtinMap.put("cscd",Cscd.getInstance());
        builtinMap.put("sinh",Sinh.getInstance());
        builtinMap.put("cosh",Cosh.getInstance());
        builtinMap.put("tanh",Tanh.getInstance());
        builtinMap.put("coth",Coth.getInstance());
        builtinMap.put("sech",Sech.getInstance());
        builtinMap.put("csch",Csch.getInstance());
        builtinMap.put("asin",Asin.getInstance());
        builtinMap.put("acos",Acos.getInstance());
        builtinMap.put("atan",Atan.getInstance());
        builtinMap.put("atan2",Atan2.getInstance());
        builtinMap.put("acot",Acot.getInstance());
        builtinMap.put("asec",Asec.getInstance());
        builtinMap.put("acsc",Acsc.getInstance());
        builtinMap.put("asind",Asind.getInstance());
        builtinMap.put("acosd",Acosd.getInstance());
        builtinMap.put("atand",Atand.getInstance());
        builtinMap.put("acotd",Acotd.getInstance());
        builtinMap.put("asecd",Asecd.getInstance());
        builtinMap.put("acscd",Acscd.getInstance());
        builtinMap.put("asinh",Asinh.getInstance());
        builtinMap.put("acosh",Acosh.getInstance());
        builtinMap.put("atanh",Atanh.getInstance());
        builtinMap.put("acoth",Acoth.getInstance());
        builtinMap.put("asech",Asech.getInstance());
        builtinMap.put("acsch",Acsch.getInstance());
        builtinMap.put("logm",Logm.getInstance());
        builtinMap.put("sqrtm",Sqrtm.getInstance());
        builtinMap.put("expm",Expm.getInstance());
        builtinMap.put("hypot",Hypot.getInstance());
        builtinMap.put("cumsum",Cumsum.getInstance());
        builtinMap.put("eig",Eig.getInstance());
        builtinMap.put("norm",Norm.getInstance());
        builtinMap.put("cond",Cond.getInstance());
        builtinMap.put("rcond",Rcond.getInstance());
        builtinMap.put("det",Det.getInstance());
        builtinMap.put("linsolve",Linsolve.getInstance());
        builtinMap.put("schur",Schur.getInstance());
        builtinMap.put("ordschur",Ordschur.getInstance());
        builtinMap.put("lu",Lu.getInstance());
        builtinMap.put("chol",Chol.getInstance());
        builtinMap.put("svd",Svd.getInstance());
        builtinMap.put("qr",Qr.getInstance());
        builtinMap.put("bitMatrixFunction",BitMatrixFunction.getInstance());
        builtinMap.put("charMatrixFunction",CharMatrixFunction.getInstance());
        builtinMap.put("eq",Eq.getInstance());
        builtinMap.put("ne",Ne.getInstance());
        builtinMap.put("lt",Lt.getInstance());
        builtinMap.put("gt",Gt.getInstance());
        builtinMap.put("le",Le.getInstance());
        builtinMap.put("ge",Ge.getInstance());
        builtinMap.put("and",And.getInstance());
        builtinMap.put("or",Or.getInstance());
        builtinMap.put("xor",Xor.getInstance());
        builtinMap.put("constructor",Constructor.getInstance());
        builtinMap.put("nnz",Nnz.getInstance());
        builtinMap.put("not",Not.getInstance());
        builtinMap.put("any",Any.getInstance());
        builtinMap.put("all",All.getInstance());
        builtinMap.put("cellFunction",CellFunction.getInstance());
        builtinMap.put("structFunction",StructFunction.getInstance());
        builtinMap.put("objectFunction",ObjectFunction.getInstance());
        builtinMap.put("matrixOrCellOfStringsFunction",MatrixOrCellOfStringsFunction.getInstance());
        builtinMap.put("superiorto",Superiorto.getInstance());
        builtinMap.put("exit",Exit.getInstance());
        builtinMap.put("quit",Quit.getInstance());
        builtinMap.put("clock",Clock.getInstance());
        builtinMap.put("tic",Tic.getInstance());
        builtinMap.put("toc",Toc.getInstance());
        builtinMap.put("cputime",Cputime.getInstance());
        builtinMap.put("assert",Assert.getInstance());
        builtinMap.put("nargoutchk",Nargoutchk.getInstance());
        builtinMap.put("nargchk",Nargchk.getInstance());
        builtinMap.put("str2func",Str2func.getInstance());
        builtinMap.put("pause",Pause.getInstance());
        builtinMap.put("eval",Eval.getInstance());
        builtinMap.put("evalin",Evalin.getInstance());
        builtinMap.put("feval",Feval.getInstance());
        builtinMap.put("assignin",Assignin.getInstance());
        builtinMap.put("inputname",Inputname.getInstance());
        builtinMap.put("import",Import.getInstance());
        builtinMap.put("cd",Cd.getInstance());
        builtinMap.put("exist",Exist.getInstance());
        builtinMap.put("matlabroot",Matlabroot.getInstance());
        builtinMap.put("whos",Whos.getInstance());
        builtinMap.put("which",Which.getInstance());
        builtinMap.put("version",Version.getInstance());
        builtinMap.put("clear",Clear.getInstance());
        builtinMap.put("disp",Disp.getInstance());
        builtinMap.put("display",Display.getInstance());
        builtinMap.put("clc",Clc.getInstance());
        builtinMap.put("error",Error.getInstance());
        builtinMap.put("warning",Warning.getInstance());
        builtinMap.put("echo",Echo.getInstance());
        builtinMap.put("diary",Diary.getInstance());
        builtinMap.put("lastwarn",Lastwarn.getInstance());
        builtinMap.put("lasterror",Lasterror.getInstance());
        builtinMap.put("format",Format.getInstance());
        builtinMap.put("rand",Rand.getInstance());
        builtinMap.put("randi",Randi.getInstance());
        builtinMap.put("randn",Randn.getInstance());
        builtinMap.put("computer",Computer.getInstance());
        builtinMap.put("beep",Beep.getInstance());
        builtinMap.put("dir",Dir.getInstance());
        builtinMap.put("unix",Unix.getInstance());
        builtinMap.put("dos",Dos.getInstance());
        builtinMap.put("system",System.getInstance());
        builtinMap.put("load",Load.getInstance());
        builtinMap.put("save",Save.getInstance());
        builtinMap.put("input",Input.getInstance());
        builtinMap.put("textscan",Textscan.getInstance());
        builtinMap.put("sprintf",Sprintf.getInstance());
        builtinMap.put("sscanf",Sscanf.getInstance());
        builtinMap.put("fprintf",Fprintf.getInstance());
        builtinMap.put("ftell",Ftell.getInstance());
        builtinMap.put("ferror",Ferror.getInstance());
        builtinMap.put("fopen",Fopen.getInstance());
        builtinMap.put("fread",Fread.getInstance());
        builtinMap.put("frewind",Frewind.getInstance());
        builtinMap.put("fscanf",Fscanf.getInstance());
        builtinMap.put("fseek",Fseek.getInstance());
        builtinMap.put("fwrite",Fwrite.getInstance());
        builtinMap.put("fgetl",Fgetl.getInstance());
        builtinMap.put("fgets",Fgets.getInstance());
        builtinMap.put("fclose",Fclose.getInstance());
    }    
    
    //the actual Builtin Classes:
    
    public static abstract class AbstractPureFunction extends Builtin  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPureFunction(this,arg);
        }
        
    }
    public static abstract class AbstractMatrixFunction extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractAnyMatrixFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractConstant extends AbstractAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstant(this,arg);
        }
        
    }
    public static class Pi extends AbstractConstant  {
        //returns the singleton instance of this class
        private static Pi singleton = null;
        public static Pi getInstance(){
            if (singleton == null) singleton = new Pi();
            return singleton;
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
    public static abstract class AbstractStrictAnyMatrixFunction extends AbstractAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStrictAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryAnyMatrixFunction extends AbstractStrictAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractElementalUnaryAnyMatrixFunction extends AbstractUnaryAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static class Test extends AbstractElementalUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //returns the singleton instance of this class
        private static Test singleton = null;
        public static Test getInstance(){
            if (singleton == null) singleton = new Test();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTest(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "test";
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = getParentMatlabClassPropagationInfo();
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfTest(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractElementalUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static abstract class AbstractMatrixUnaryAnyMatrixFunction extends AbstractUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixUnaryAnyMatrixFunction(this,arg);
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCMap(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCBuiltin("double"));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfAbstractMatrixUnaryAnyMatrixFunction(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static class Transpose extends AbstractMatrixUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //returns the singleton instance of this class
        private static Transpose singleton = null;
        public static Transpose getInstance(){
            if (singleton == null) singleton = new Transpose();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTranspose(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "transpose";
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCMap(new ClassPropTools.MCChain(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCBuiltin("single")),new ClassPropTools.MCNum(1)),new ClassPropTools.MCChain(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCBuiltin("logical")));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfTranspose(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractMatrixUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static class Ctranspose extends AbstractMatrixUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //returns the singleton instance of this class
        private static Ctranspose singleton = null;
        public static Ctranspose getInstance(){
            if (singleton == null) singleton = new Ctranspose();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCtranspose(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ctranspose";
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCChain(getParentMatlabClassPropagationInfo(),new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCNone()));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfCtranspose(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractMatrixUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static class Tril extends AbstractMatrixUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //returns the singleton instance of this class
        private static Tril singleton = null;
        public static Tril getInstance(){
            if (singleton == null) singleton = new Tril();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTril(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tril";
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCCoerce(new ClassPropTools.MCMap(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("logical"),new ClassPropTools.MCBuiltin("char")),new ClassPropTools.MCBuiltin("double")),new ClassPropTools.MCMap(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCBuiltin("double")));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfTril(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractMatrixUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static class Triu extends AbstractMatrixUnaryAnyMatrixFunction implements ClassPropagationDefined {
        //returns the singleton instance of this class
        private static Triu singleton = null;
        public static Triu getInstance(){
            if (singleton == null) singleton = new Triu();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTriu(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "triu";
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("single"),new ClassPropTools.MCBuiltin("double")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64")))),new ClassPropTools.MCBuiltin("double"));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfTriu(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractMatrixUnaryAnyMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static abstract class AbstractFlexibleAnyMatrixFunction extends AbstractAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFlexibleAnyMatrixFunction(this,arg);
        }
        
    }
    public static class DimensionCollapsingAnyMatrixFunction extends AbstractFlexibleAnyMatrixFunction  {
        //returns the singleton instance of this class
        private static DimensionCollapsingAnyMatrixFunction singleton = null;
        public static DimensionCollapsingAnyMatrixFunction getInstance(){
            if (singleton == null) singleton = new DimensionCollapsingAnyMatrixFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDimensionCollapsingAnyMatrixFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dimensionCollapsingAnyMatrixFunction";
        }
        
    }
    public static abstract class AbstractNumericMatrixFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractStrictNumericMatrixFunction extends AbstractNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStrictNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryNumericMatrixFunction extends AbstractStrictNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractElementalUnaryNumericMatrixFunction extends AbstractUnaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractRoundingOperation extends AbstractElementalUnaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRoundingOperation(this,arg);
        }
        
    }
    public static class Fix extends AbstractRoundingOperation  {
        //returns the singleton instance of this class
        private static Fix singleton = null;
        public static Fix getInstance(){
            if (singleton == null) singleton = new Fix();
            return singleton;
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
    public static class Round extends AbstractRoundingOperation  {
        //returns the singleton instance of this class
        private static Round singleton = null;
        public static Round getInstance(){
            if (singleton == null) singleton = new Round();
            return singleton;
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
    public static class Floor extends AbstractRoundingOperation  {
        //returns the singleton instance of this class
        private static Floor singleton = null;
        public static Floor getInstance(){
            if (singleton == null) singleton = new Floor();
            return singleton;
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
    public static class Ceil extends AbstractRoundingOperation  {
        //returns the singleton instance of this class
        private static Ceil singleton = null;
        public static Ceil getInstance(){
            if (singleton == null) singleton = new Ceil();
            return singleton;
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
    public static class MatrixUnaryNumericMatrixFunction extends AbstractUnaryNumericMatrixFunction  {
        //returns the singleton instance of this class
        private static MatrixUnaryNumericMatrixFunction singleton = null;
        public static MatrixUnaryNumericMatrixFunction getInstance(){
            if (singleton == null) singleton = new MatrixUnaryNumericMatrixFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMatrixUnaryNumericMatrixFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "matrixUnaryNumericMatrixFunction";
        }
        
    }
    public static abstract class AbstractBinaryNumericMatrixFunction extends AbstractStrictNumericMatrixFunction implements ClassPropagationDefined {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryNumericMatrixFunction(this,arg);
        }
        
        //class prop info stuff:
        private final ClassPropTools.MC classPropInfo = new ClassPropTools.MCCoerce(new ClassPropTools.MCMap(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("logical"),new ClassPropTools.MCBuiltin("char")),new ClassPropTools.MCBuiltin("double")),new ClassPropTools.MCUnion(new ClassPropTools.MCMap(new ClassPropTools.MCChain(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("single"),new ClassPropTools.MCBuiltin("double")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64")))),new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCNum(0))),new ClassPropTools.MCNum(0)),new ClassPropTools.MCMap(new ClassPropTools.MCChain(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("double"),new ClassPropTools.MCNum(1)),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("single"),new ClassPropTools.MCBuiltin("double")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64")),new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCUnion(new ClassPropTools.MCBuiltin("int8"),new ClassPropTools.MCBuiltin("uint16")),new ClassPropTools.MCBuiltin("uint32")),new ClassPropTools.MCBuiltin("uint64"))))),new ClassPropTools.MCNum(1))));
        private ClassPropTools.MC parentClassPropInfo;

        //method that explicitly returns tree for this class - used by get parent class info
        public final ClassPropTools.MC getMatlabClassPropInfoOfAbstractBinaryNumericMatrixFunction(){
            return this.classPropInfo;
        }

        public ClassPropTools.MC getParentMatlabClassPropagationInfo(){
            if (parentClassPropInfo == null) try{
                //try to access the explicit tree method for the parent
                parentClassPropInfo = (ClassPropTools.MC)
                    getClass().getMethod("getMatlabClassPropInfoOfAbstractStrictNumericMatrixFunction").invoke(this);
            } catch (Exception e) {
                //the parent class does not implment the class prop defined interface - assign 'none'
                parentClassPropInfo = new ClassPropTools.MCNone();
            }
            return parentClassPropInfo;
        }
        
        public ClassPropTools.MC getMatlabClassPropagationInfo(){ return classPropInfo; }

    }
    public static abstract class AbstractElementalBinaryNumericMatrixFunction extends AbstractBinaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractElemnetalBinaryArithmeticFunction extends AbstractElementalBinaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElemnetalBinaryArithmeticFunction(this,arg);
        }
        
    }
    public static class Plus extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Plus singleton = null;
        public static Plus getInstance(){
            if (singleton == null) singleton = new Plus();
            return singleton;
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
    public static class Minus extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Minus singleton = null;
        public static Minus getInstance(){
            if (singleton == null) singleton = new Minus();
            return singleton;
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
    public static class Times extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Times singleton = null;
        public static Times getInstance(){
            if (singleton == null) singleton = new Times();
            return singleton;
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
    public static class Ldivide extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Ldivide singleton = null;
        public static Ldivide getInstance(){
            if (singleton == null) singleton = new Ldivide();
            return singleton;
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
    public static class Rdivide extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Rdivide singleton = null;
        public static Rdivide getInstance(){
            if (singleton == null) singleton = new Rdivide();
            return singleton;
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
    public static class Mod extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Mod singleton = null;
        public static Mod getInstance(){
            if (singleton == null) singleton = new Mod();
            return singleton;
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
    public static class Rem extends AbstractElemnetalBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Rem singleton = null;
        public static Rem getInstance(){
            if (singleton == null) singleton = new Rem();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRem(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rem";
        }
        
    }
    public static abstract class AbstractMatrixBinaryNumericMatrixFunction extends AbstractBinaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixBinaryNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractMatrixBinaryArithmeticFunction extends AbstractMatrixBinaryNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixBinaryArithmeticFunction(this,arg);
        }
        
    }
    public static class Mtimes extends AbstractMatrixBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Mtimes singleton = null;
        public static Mtimes getInstance(){
            if (singleton == null) singleton = new Mtimes();
            return singleton;
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
    public static class Mldivide extends AbstractMatrixBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Mldivide singleton = null;
        public static Mldivide getInstance(){
            if (singleton == null) singleton = new Mldivide();
            return singleton;
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
    public static class Mrdivide extends AbstractMatrixBinaryArithmeticFunction  {
        //returns the singleton instance of this class
        private static Mrdivide singleton = null;
        public static Mrdivide getInstance(){
            if (singleton == null) singleton = new Mrdivide();
            return singleton;
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
    public static abstract class AbstractFlexibleNumericMatrixFunction extends AbstractNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFlexibleNumericMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractDimensionCollapsingNumericMatrixFunction extends AbstractFlexibleNumericMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionCollapsingNumericMatrixFunction(this,arg);
        }
        
    }
    public static class Min extends AbstractDimensionCollapsingNumericMatrixFunction  {
        //returns the singleton instance of this class
        private static Min singleton = null;
        public static Min getInstance(){
            if (singleton == null) singleton = new Min();
            return singleton;
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
    public static class Max extends AbstractDimensionCollapsingNumericMatrixFunction  {
        //returns the singleton instance of this class
        private static Max singleton = null;
        public static Max getInstance(){
            if (singleton == null) singleton = new Max();
            return singleton;
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
    public static abstract class AbstractFloatMatrixFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractStrictFloatMatrixFunction extends AbstractFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStrictFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryFloatMatrixFunction extends AbstractStrictFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractElementalUnaryFloatMatrixFunction extends AbstractUnaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static class Sqrt extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Sqrt singleton = null;
        public static Sqrt getInstance(){
            if (singleton == null) singleton = new Sqrt();
            return singleton;
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
    public static class Realsqrt extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Realsqrt singleton = null;
        public static Realsqrt getInstance(){
            if (singleton == null) singleton = new Realsqrt();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRealsqrt(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "realsqrt";
        }
        
    }
    public static class Erf extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Erf singleton = null;
        public static Erf getInstance(){
            if (singleton == null) singleton = new Erf();
            return singleton;
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
    public static class Erfinv extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Erfinv singleton = null;
        public static Erfinv getInstance(){
            if (singleton == null) singleton = new Erfinv();
            return singleton;
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
    public static class Erfc extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Erfc singleton = null;
        public static Erfc getInstance(){
            if (singleton == null) singleton = new Erfc();
            return singleton;
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
    public static class Erfcinv extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Erfcinv singleton = null;
        public static Erfcinv getInstance(){
            if (singleton == null) singleton = new Erfcinv();
            return singleton;
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
    public static class Gamma extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Gamma singleton = null;
        public static Gamma getInstance(){
            if (singleton == null) singleton = new Gamma();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseGamma(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "gamma";
        }
        
    }
    public static class Gammainc extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Gammainc singleton = null;
        public static Gammainc getInstance(){
            if (singleton == null) singleton = new Gammainc();
            return singleton;
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
    public static class Betainc extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Betainc singleton = null;
        public static Betainc getInstance(){
            if (singleton == null) singleton = new Betainc();
            return singleton;
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
    public static class Gammaln extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Gammaln singleton = null;
        public static Gammaln getInstance(){
            if (singleton == null) singleton = new Gammaln();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseGammaln(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "gammaln";
        }
        
    }
    public static class Exp extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Exp singleton = null;
        public static Exp getInstance(){
            if (singleton == null) singleton = new Exp();
            return singleton;
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
    public static class Log extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Log singleton = null;
        public static Log getInstance(){
            if (singleton == null) singleton = new Log();
            return singleton;
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
    public static class Log2 extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Log2 singleton = null;
        public static Log2 getInstance(){
            if (singleton == null) singleton = new Log2();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLog2(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "log2";
        }
        
    }
    public static class Log10 extends AbstractElementalUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Log10 singleton = null;
        public static Log10 getInstance(){
            if (singleton == null) singleton = new Log10();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLog10(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "log10";
        }
        
    }
    public static abstract class AbstractForwardTrigonometricFunction extends AbstractElementalUnaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractForwardTrigonometricFunction(this,arg);
        }
        
    }
    public static abstract class AbstractTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sin extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Sin singleton = null;
        public static Sin getInstance(){
            if (singleton == null) singleton = new Sin();
            return singleton;
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
    public static class Cos extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cos singleton = null;
        public static Cos getInstance(){
            if (singleton == null) singleton = new Cos();
            return singleton;
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
    public static class Tan extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Tan singleton = null;
        public static Tan getInstance(){
            if (singleton == null) singleton = new Tan();
            return singleton;
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
    public static class Cot extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cot singleton = null;
        public static Cot getInstance(){
            if (singleton == null) singleton = new Cot();
            return singleton;
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
    public static class Sec extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Sec singleton = null;
        public static Sec getInstance(){
            if (singleton == null) singleton = new Sec();
            return singleton;
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
    public static class Csc extends AbstractTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Csc singleton = null;
        public static Csc getInstance(){
            if (singleton == null) singleton = new Csc();
            return singleton;
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
    public static abstract class AbstractDecimalTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sind extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Sind singleton = null;
        public static Sind getInstance(){
            if (singleton == null) singleton = new Sind();
            return singleton;
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
    public static class Cosd extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cosd singleton = null;
        public static Cosd getInstance(){
            if (singleton == null) singleton = new Cosd();
            return singleton;
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
    public static class Tand extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Tand singleton = null;
        public static Tand getInstance(){
            if (singleton == null) singleton = new Tand();
            return singleton;
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
    public static class Cotd extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cotd singleton = null;
        public static Cotd getInstance(){
            if (singleton == null) singleton = new Cotd();
            return singleton;
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
    public static class Secd extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Secd singleton = null;
        public static Secd getInstance(){
            if (singleton == null) singleton = new Secd();
            return singleton;
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
    public static class Cscd extends AbstractDecimalTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cscd singleton = null;
        public static Cscd getInstance(){
            if (singleton == null) singleton = new Cscd();
            return singleton;
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
    public static abstract class AbstractHyperbolicTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sinh extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Sinh singleton = null;
        public static Sinh getInstance(){
            if (singleton == null) singleton = new Sinh();
            return singleton;
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
    public static class Cosh extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Cosh singleton = null;
        public static Cosh getInstance(){
            if (singleton == null) singleton = new Cosh();
            return singleton;
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
    public static class Tanh extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Tanh singleton = null;
        public static Tanh getInstance(){
            if (singleton == null) singleton = new Tanh();
            return singleton;
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
    public static class Coth extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Coth singleton = null;
        public static Coth getInstance(){
            if (singleton == null) singleton = new Coth();
            return singleton;
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
    public static class Sech extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Sech singleton = null;
        public static Sech getInstance(){
            if (singleton == null) singleton = new Sech();
            return singleton;
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
    public static class Csch extends AbstractHyperbolicTrigonometricFunction  {
        //returns the singleton instance of this class
        private static Csch singleton = null;
        public static Csch getInstance(){
            if (singleton == null) singleton = new Csch();
            return singleton;
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
    public static abstract class AbstractInverseTrigonmetricFunction extends AbstractElementalUnaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static abstract class AbstractStandardInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStandardInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asin extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asin singleton = null;
        public static Asin getInstance(){
            if (singleton == null) singleton = new Asin();
            return singleton;
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
    public static class Acos extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acos singleton = null;
        public static Acos getInstance(){
            if (singleton == null) singleton = new Acos();
            return singleton;
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
    public static class Atan extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Atan singleton = null;
        public static Atan getInstance(){
            if (singleton == null) singleton = new Atan();
            return singleton;
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
    public static class Atan2 extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Atan2 singleton = null;
        public static Atan2 getInstance(){
            if (singleton == null) singleton = new Atan2();
            return singleton;
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
    public static class Acot extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acot singleton = null;
        public static Acot getInstance(){
            if (singleton == null) singleton = new Acot();
            return singleton;
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
    public static class Asec extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asec singleton = null;
        public static Asec getInstance(){
            if (singleton == null) singleton = new Asec();
            return singleton;
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
    public static class Acsc extends AbstractStandardInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acsc singleton = null;
        public static Acsc getInstance(){
            if (singleton == null) singleton = new Acsc();
            return singleton;
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
    public static abstract class AbstractDecimalInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asind extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asind singleton = null;
        public static Asind getInstance(){
            if (singleton == null) singleton = new Asind();
            return singleton;
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
    public static class Acosd extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acosd singleton = null;
        public static Acosd getInstance(){
            if (singleton == null) singleton = new Acosd();
            return singleton;
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
    public static class Atand extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Atand singleton = null;
        public static Atand getInstance(){
            if (singleton == null) singleton = new Atand();
            return singleton;
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
    public static class Acotd extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acotd singleton = null;
        public static Acotd getInstance(){
            if (singleton == null) singleton = new Acotd();
            return singleton;
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
    public static class Asecd extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asecd singleton = null;
        public static Asecd getInstance(){
            if (singleton == null) singleton = new Asecd();
            return singleton;
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
    public static class Acscd extends AbstractDecimalInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acscd singleton = null;
        public static Acscd getInstance(){
            if (singleton == null) singleton = new Acscd();
            return singleton;
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
    public static abstract class AbstractHyperbolicInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asinh extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asinh singleton = null;
        public static Asinh getInstance(){
            if (singleton == null) singleton = new Asinh();
            return singleton;
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
    public static class Acosh extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acosh singleton = null;
        public static Acosh getInstance(){
            if (singleton == null) singleton = new Acosh();
            return singleton;
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
    public static class Atanh extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Atanh singleton = null;
        public static Atanh getInstance(){
            if (singleton == null) singleton = new Atanh();
            return singleton;
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
    public static class Acoth extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acoth singleton = null;
        public static Acoth getInstance(){
            if (singleton == null) singleton = new Acoth();
            return singleton;
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
    public static class Asech extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Asech singleton = null;
        public static Asech getInstance(){
            if (singleton == null) singleton = new Asech();
            return singleton;
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
    public static class Acsch extends AbstractHyperbolicInverseTrigonmetricFunction  {
        //returns the singleton instance of this class
        private static Acsch singleton = null;
        public static Acsch getInstance(){
            if (singleton == null) singleton = new Acsch();
            return singleton;
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
    public static abstract class AbstractMatrixUnaryFloatMatrixFunction extends AbstractUnaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixUnaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractSquareMatrixUnaryFloatMatrixFunction extends AbstractMatrixUnaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractSquareMatrixUnaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static class Logm extends AbstractSquareMatrixUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Logm singleton = null;
        public static Logm getInstance(){
            if (singleton == null) singleton = new Logm();
            return singleton;
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
    public static class Sqrtm extends AbstractSquareMatrixUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Sqrtm singleton = null;
        public static Sqrtm getInstance(){
            if (singleton == null) singleton = new Sqrtm();
            return singleton;
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
    public static class Expm extends AbstractSquareMatrixUnaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Expm singleton = null;
        public static Expm getInstance(){
            if (singleton == null) singleton = new Expm();
            return singleton;
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
    public static abstract class AbstractBinaryFloatMatrixFunction extends AbstractStrictFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractMatrixBinaryFloatMatrixFunction extends AbstractBinaryFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixBinaryFloatMatrixFunction(this,arg);
        }
        
    }
    public static class Hypot extends AbstractMatrixBinaryFloatMatrixFunction  {
        //returns the singleton instance of this class
        private static Hypot singleton = null;
        public static Hypot getInstance(){
            if (singleton == null) singleton = new Hypot();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseHypot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "hypot";
        }
        
    }
    public static abstract class AbstractFlexibleFloatMatrixFunction extends AbstractFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFlexibleFloatMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractDimensionCollapsingFloaMatrixFunction extends AbstractFlexibleFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionCollapsingFloaMatrixFunction(this,arg);
        }
        
    }
    public static class Cumsum extends AbstractDimensionCollapsingFloaMatrixFunction  {
        //returns the singleton instance of this class
        private static Cumsum singleton = null;
        public static Cumsum getInstance(){
            if (singleton == null) singleton = new Cumsum();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCumsum(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cumsum";
        }
        
    }
    public static abstract class AbstractMatrixLibaryFunction extends AbstractFlexibleFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixLibaryFunction(this,arg);
        }
        
    }
    public static class Eig extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Eig singleton = null;
        public static Eig getInstance(){
            if (singleton == null) singleton = new Eig();
            return singleton;
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
    public static class Norm extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Norm singleton = null;
        public static Norm getInstance(){
            if (singleton == null) singleton = new Norm();
            return singleton;
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
    public static class Cond extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Cond singleton = null;
        public static Cond getInstance(){
            if (singleton == null) singleton = new Cond();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCond(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cond";
        }
        
    }
    public static class Rcond extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Rcond singleton = null;
        public static Rcond getInstance(){
            if (singleton == null) singleton = new Rcond();
            return singleton;
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
    public static class Det extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Det singleton = null;
        public static Det getInstance(){
            if (singleton == null) singleton = new Det();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDet(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "det";
        }
        
    }
    public static class Linsolve extends AbstractMatrixLibaryFunction  {
        //returns the singleton instance of this class
        private static Linsolve singleton = null;
        public static Linsolve getInstance(){
            if (singleton == null) singleton = new Linsolve();
            return singleton;
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
    public static abstract class AbstractFacotorizationFunction extends AbstractFlexibleFloatMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFacotorizationFunction(this,arg);
        }
        
    }
    public static class Schur extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Schur singleton = null;
        public static Schur getInstance(){
            if (singleton == null) singleton = new Schur();
            return singleton;
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
    public static class Ordschur extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Ordschur singleton = null;
        public static Ordschur getInstance(){
            if (singleton == null) singleton = new Ordschur();
            return singleton;
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
    public static class Lu extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Lu singleton = null;
        public static Lu getInstance(){
            if (singleton == null) singleton = new Lu();
            return singleton;
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
    public static class Chol extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Chol singleton = null;
        public static Chol getInstance(){
            if (singleton == null) singleton = new Chol();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseChol(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "chol";
        }
        
    }
    public static class Svd extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Svd singleton = null;
        public static Svd getInstance(){
            if (singleton == null) singleton = new Svd();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSvd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "svd";
        }
        
    }
    public static class Qr extends AbstractFacotorizationFunction  {
        //returns the singleton instance of this class
        private static Qr singleton = null;
        public static Qr getInstance(){
            if (singleton == null) singleton = new Qr();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseQr(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "qr";
        }
        
    }
    public static class BitMatrixFunction extends AbstractMatrixFunction  {
        //returns the singleton instance of this class
        private static BitMatrixFunction singleton = null;
        public static BitMatrixFunction getInstance(){
            if (singleton == null) singleton = new BitMatrixFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitMatrixFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitMatrixFunction";
        }
        
    }
    public static class CharMatrixFunction extends AbstractMatrixFunction  {
        //returns the singleton instance of this class
        private static CharMatrixFunction singleton = null;
        public static CharMatrixFunction getInstance(){
            if (singleton == null) singleton = new CharMatrixFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCharMatrixFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "charMatrixFunction";
        }
        
    }
    public static abstract class AbstractConversionFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConversionFunction(this,arg);
        }
        
    }
    public static abstract class AbstractConversionToLogicalFunction extends AbstractConversionFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConversionToLogicalFunction(this,arg);
        }
        
    }
    public static abstract class AbstractBinaryConversionToLogicalFunction extends AbstractConversionToLogicalFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryConversionToLogicalFunction(this,arg);
        }
        
    }
    public static abstract class AbstractElementalBinaryConversionToLogicalFunction extends AbstractBinaryConversionToLogicalFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryConversionToLogicalFunction(this,arg);
        }
        
    }
    public static abstract class AbstractRelationalOperator extends AbstractElementalBinaryConversionToLogicalFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRelationalOperator(this,arg);
        }
        
    }
    public static class Eq extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Eq singleton = null;
        public static Eq getInstance(){
            if (singleton == null) singleton = new Eq();
            return singleton;
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
    public static class Ne extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Ne singleton = null;
        public static Ne getInstance(){
            if (singleton == null) singleton = new Ne();
            return singleton;
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
    public static class Lt extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Lt singleton = null;
        public static Lt getInstance(){
            if (singleton == null) singleton = new Lt();
            return singleton;
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
    public static class Gt extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Gt singleton = null;
        public static Gt getInstance(){
            if (singleton == null) singleton = new Gt();
            return singleton;
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
    public static class Le extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Le singleton = null;
        public static Le getInstance(){
            if (singleton == null) singleton = new Le();
            return singleton;
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
    public static class Ge extends AbstractRelationalOperator  {
        //returns the singleton instance of this class
        private static Ge singleton = null;
        public static Ge getInstance(){
            if (singleton == null) singleton = new Ge();
            return singleton;
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
    public static abstract class AbstractLogicalOperator extends AbstractElementalBinaryConversionToLogicalFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalOperator(this,arg);
        }
        
    }
    public static class And extends AbstractLogicalOperator  {
        //returns the singleton instance of this class
        private static And singleton = null;
        public static And getInstance(){
            if (singleton == null) singleton = new And();
            return singleton;
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
    public static class Or extends AbstractLogicalOperator  {
        //returns the singleton instance of this class
        private static Or singleton = null;
        public static Or getInstance(){
            if (singleton == null) singleton = new Or();
            return singleton;
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
    public static class Xor extends AbstractLogicalOperator  {
        //returns the singleton instance of this class
        private static Xor singleton = null;
        public static Xor getInstance(){
            if (singleton == null) singleton = new Xor();
            return singleton;
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
    public static class Constructor extends AbstractConversionFunction  {
        //returns the singleton instance of this class
        private static Constructor singleton = null;
        public static Constructor getInstance(){
            if (singleton == null) singleton = new Constructor();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseConstructor(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "constructor";
        }
        
    }
    public static abstract class AbstractArrayQuery extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayQuery(this,arg);
        }
        
    }
    public static abstract class AbstractDoubleResultArrayQuery extends AbstractArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDoubleResultArrayQuery(this,arg);
        }
        
    }
    public static abstract class AbstractScalarDoubleResultArrayQuery extends AbstractDoubleResultArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarDoubleResultArrayQuery(this,arg);
        }
        
    }
    public static class Nnz extends AbstractScalarDoubleResultArrayQuery  {
        //returns the singleton instance of this class
        private static Nnz singleton = null;
        public static Nnz getInstance(){
            if (singleton == null) singleton = new Nnz();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNnz(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nnz";
        }
        
    }
    public static abstract class AbstractLogicalResultArrayQuery extends AbstractArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalResultArrayQuery(this,arg);
        }
        
    }
    public static abstract class AbstractScalarLogicalResultArrayQuery extends AbstractLogicalResultArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarLogicalResultArrayQuery(this,arg);
        }
        
    }
    public static class Not extends AbstractScalarLogicalResultArrayQuery  {
        //returns the singleton instance of this class
        private static Not singleton = null;
        public static Not getInstance(){
            if (singleton == null) singleton = new Not();
            return singleton;
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
    public static class Any extends AbstractScalarLogicalResultArrayQuery  {
        //returns the singleton instance of this class
        private static Any singleton = null;
        public static Any getInstance(){
            if (singleton == null) singleton = new Any();
            return singleton;
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
    public static class All extends AbstractScalarLogicalResultArrayQuery  {
        //returns the singleton instance of this class
        private static All singleton = null;
        public static All getInstance(){
            if (singleton == null) singleton = new All();
            return singleton;
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
    public static class CellFunction extends AbstractPureFunction  {
        //returns the singleton instance of this class
        private static CellFunction singleton = null;
        public static CellFunction getInstance(){
            if (singleton == null) singleton = new CellFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCellFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cellFunction";
        }
        
    }
    public static class StructFunction extends AbstractPureFunction  {
        //returns the singleton instance of this class
        private static StructFunction singleton = null;
        public static StructFunction getInstance(){
            if (singleton == null) singleton = new StructFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStructFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "structFunction";
        }
        
    }
    public static class ObjectFunction extends AbstractPureFunction  {
        //returns the singleton instance of this class
        private static ObjectFunction singleton = null;
        public static ObjectFunction getInstance(){
            if (singleton == null) singleton = new ObjectFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseObjectFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "objectFunction";
        }
        
    }
    public static abstract class AbstractVersatileFunction extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractVersatileFunction(this,arg);
        }
        
    }
    public static class MatrixOrCellOfStringsFunction extends AbstractVersatileFunction  {
        //returns the singleton instance of this class
        private static MatrixOrCellOfStringsFunction singleton = null;
        public static MatrixOrCellOfStringsFunction getInstance(){
            if (singleton == null) singleton = new MatrixOrCellOfStringsFunction();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMatrixOrCellOfStringsFunction(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "matrixOrCellOfStringsFunction";
        }
        
    }
    public static abstract class AbstractImpureFunction extends Builtin  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImpureFunction(this,arg);
        }
        
    }
    public static class Superiorto extends AbstractImpureFunction  {
        //returns the singleton instance of this class
        private static Superiorto singleton = null;
        public static Superiorto getInstance(){
            if (singleton == null) singleton = new Superiorto();
            return singleton;
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
    public static class Exit extends AbstractImpureFunction  {
        //returns the singleton instance of this class
        private static Exit singleton = null;
        public static Exit getInstance(){
            if (singleton == null) singleton = new Exit();
            return singleton;
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
    public static class Quit extends AbstractImpureFunction  {
        //returns the singleton instance of this class
        private static Quit singleton = null;
        public static Quit getInstance(){
            if (singleton == null) singleton = new Quit();
            return singleton;
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
    public static abstract class AbstractBuiltin extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBuiltin(this,arg);
        }
        
    }
    public static abstract class AbstractTimeFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractTimeFunction(this,arg);
        }
        
    }
    public static class Clock extends AbstractTimeFunction  {
        //returns the singleton instance of this class
        private static Clock singleton = null;
        public static Clock getInstance(){
            if (singleton == null) singleton = new Clock();
            return singleton;
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
    public static class Tic extends AbstractTimeFunction  {
        //returns the singleton instance of this class
        private static Tic singleton = null;
        public static Tic getInstance(){
            if (singleton == null) singleton = new Tic();
            return singleton;
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
    public static class Toc extends AbstractTimeFunction  {
        //returns the singleton instance of this class
        private static Toc singleton = null;
        public static Toc getInstance(){
            if (singleton == null) singleton = new Toc();
            return singleton;
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
    public static class Cputime extends AbstractTimeFunction  {
        //returns the singleton instance of this class
        private static Cputime singleton = null;
        public static Cputime getInstance(){
            if (singleton == null) singleton = new Cputime();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCputime(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cputime";
        }
        
    }
    public static abstract class AbstractMatlabSystemFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatlabSystemFunction(this,arg);
        }
        
    }
    public static class Assert extends AbstractMatlabSystemFunction  {
        //returns the singleton instance of this class
        private static Assert singleton = null;
        public static Assert getInstance(){
            if (singleton == null) singleton = new Assert();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAssert(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "assert";
        }
        
    }
    public static class Nargoutchk extends AbstractMatlabSystemFunction  {
        //returns the singleton instance of this class
        private static Nargoutchk singleton = null;
        public static Nargoutchk getInstance(){
            if (singleton == null) singleton = new Nargoutchk();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargoutchk(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargoutchk";
        }
        
    }
    public static class Nargchk extends AbstractMatlabSystemFunction  {
        //returns the singleton instance of this class
        private static Nargchk singleton = null;
        public static Nargchk getInstance(){
            if (singleton == null) singleton = new Nargchk();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargchk(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargchk";
        }
        
    }
    public static class Str2func extends AbstractMatlabSystemFunction  {
        //returns the singleton instance of this class
        private static Str2func singleton = null;
        public static Str2func getInstance(){
            if (singleton == null) singleton = new Str2func();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStr2func(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "str2func";
        }
        
    }
    public static class Pause extends AbstractMatlabSystemFunction  {
        //returns the singleton instance of this class
        private static Pause singleton = null;
        public static Pause getInstance(){
            if (singleton == null) singleton = new Pause();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePause(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "pause";
        }
        
    }
    public static abstract class AbstractDynamicMatlabFunction extends AbstractMatlabSystemFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDynamicMatlabFunction(this,arg);
        }
        
    }
    public static class Eval extends AbstractDynamicMatlabFunction  {
        //returns the singleton instance of this class
        private static Eval singleton = null;
        public static Eval getInstance(){
            if (singleton == null) singleton = new Eval();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEval(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eval";
        }
        
    }
    public static class Evalin extends AbstractDynamicMatlabFunction  {
        //returns the singleton instance of this class
        private static Evalin singleton = null;
        public static Evalin getInstance(){
            if (singleton == null) singleton = new Evalin();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEvalin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "evalin";
        }
        
    }
    public static class Feval extends AbstractDynamicMatlabFunction  {
        //returns the singleton instance of this class
        private static Feval singleton = null;
        public static Feval getInstance(){
            if (singleton == null) singleton = new Feval();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFeval(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "feval";
        }
        
    }
    public static class Assignin extends AbstractDynamicMatlabFunction  {
        //returns the singleton instance of this class
        private static Assignin singleton = null;
        public static Assignin getInstance(){
            if (singleton == null) singleton = new Assignin();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAssignin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "assignin";
        }
        
    }
    public static class Inputname extends AbstractDynamicMatlabFunction  {
        //returns the singleton instance of this class
        private static Inputname singleton = null;
        public static Inputname getInstance(){
            if (singleton == null) singleton = new Inputname();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInputname(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "inputname";
        }
        
    }
    public static abstract class AbstractMatlabEnvironmentFunction extends AbstractMatlabSystemFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatlabEnvironmentFunction(this,arg);
        }
        
    }
    public static class Import extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Import singleton = null;
        public static Import getInstance(){
            if (singleton == null) singleton = new Import();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImport(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "import";
        }
        
    }
    public static class Cd extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Cd singleton = null;
        public static Cd getInstance(){
            if (singleton == null) singleton = new Cd();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cd";
        }
        
    }
    public static class Exist extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Exist singleton = null;
        public static Exist getInstance(){
            if (singleton == null) singleton = new Exist();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseExist(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "exist";
        }
        
    }
    public static class Matlabroot extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Matlabroot singleton = null;
        public static Matlabroot getInstance(){
            if (singleton == null) singleton = new Matlabroot();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMatlabroot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "matlabroot";
        }
        
    }
    public static class Whos extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Whos singleton = null;
        public static Whos getInstance(){
            if (singleton == null) singleton = new Whos();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseWhos(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "whos";
        }
        
    }
    public static class Which extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Which singleton = null;
        public static Which getInstance(){
            if (singleton == null) singleton = new Which();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseWhich(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "which";
        }
        
    }
    public static class Version extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Version singleton = null;
        public static Version getInstance(){
            if (singleton == null) singleton = new Version();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseVersion(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "version";
        }
        
    }
    public static class Clear extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Clear singleton = null;
        public static Clear getInstance(){
            if (singleton == null) singleton = new Clear();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseClear(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "clear";
        }
        
    }
    public static abstract class AbstractReportFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractReportFunction(this,arg);
        }
        
    }
    public static class Disp extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Disp singleton = null;
        public static Disp getInstance(){
            if (singleton == null) singleton = new Disp();
            return singleton;
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
    public static class Display extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Display singleton = null;
        public static Display getInstance(){
            if (singleton == null) singleton = new Display();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDisplay(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "display";
        }
        
    }
    public static class Clc extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Clc singleton = null;
        public static Clc getInstance(){
            if (singleton == null) singleton = new Clc();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseClc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "clc";
        }
        
    }
    public static class Error extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Error singleton = null;
        public static Error getInstance(){
            if (singleton == null) singleton = new Error();
            return singleton;
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
    public static class Warning extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Warning singleton = null;
        public static Warning getInstance(){
            if (singleton == null) singleton = new Warning();
            return singleton;
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
    public static class Echo extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Echo singleton = null;
        public static Echo getInstance(){
            if (singleton == null) singleton = new Echo();
            return singleton;
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
    public static class Diary extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Diary singleton = null;
        public static Diary getInstance(){
            if (singleton == null) singleton = new Diary();
            return singleton;
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
    public static class Lastwarn extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Lastwarn singleton = null;
        public static Lastwarn getInstance(){
            if (singleton == null) singleton = new Lastwarn();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLastwarn(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "lastwarn";
        }
        
    }
    public static class Lasterror extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Lasterror singleton = null;
        public static Lasterror getInstance(){
            if (singleton == null) singleton = new Lasterror();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLasterror(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "lasterror";
        }
        
    }
    public static class Format extends AbstractReportFunction  {
        //returns the singleton instance of this class
        private static Format singleton = null;
        public static Format getInstance(){
            if (singleton == null) singleton = new Format();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFormat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "format";
        }
        
    }
    public static abstract class AbstractRandomFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRandomFunction(this,arg);
        }
        
    }
    public static class Rand extends AbstractRandomFunction  {
        //returns the singleton instance of this class
        private static Rand singleton = null;
        public static Rand getInstance(){
            if (singleton == null) singleton = new Rand();
            return singleton;
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
    public static class Randi extends AbstractRandomFunction  {
        //returns the singleton instance of this class
        private static Randi singleton = null;
        public static Randi getInstance(){
            if (singleton == null) singleton = new Randi();
            return singleton;
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
    public static class Randn extends AbstractRandomFunction  {
        //returns the singleton instance of this class
        private static Randn singleton = null;
        public static Randn getInstance(){
            if (singleton == null) singleton = new Randn();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRandn(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "randn";
        }
        
    }
    public static abstract class AbstractSystemFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractSystemFunction(this,arg);
        }
        
    }
    public static class Computer extends AbstractSystemFunction  {
        //returns the singleton instance of this class
        private static Computer singleton = null;
        public static Computer getInstance(){
            if (singleton == null) singleton = new Computer();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseComputer(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "computer";
        }
        
    }
    public static class Beep extends AbstractSystemFunction  {
        //returns the singleton instance of this class
        private static Beep singleton = null;
        public static Beep getInstance(){
            if (singleton == null) singleton = new Beep();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBeep(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "beep";
        }
        
    }
    public static class Dir extends AbstractSystemFunction  {
        //returns the singleton instance of this class
        private static Dir singleton = null;
        public static Dir getInstance(){
            if (singleton == null) singleton = new Dir();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDir(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dir";
        }
        
    }
    public static abstract class AbstractOperatingSystemCallFunction extends AbstractSystemFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractOperatingSystemCallFunction(this,arg);
        }
        
    }
    public static class Unix extends AbstractOperatingSystemCallFunction  {
        //returns the singleton instance of this class
        private static Unix singleton = null;
        public static Unix getInstance(){
            if (singleton == null) singleton = new Unix();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUnix(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "unix";
        }
        
    }
    public static class Dos extends AbstractOperatingSystemCallFunction  {
        //returns the singleton instance of this class
        private static Dos singleton = null;
        public static Dos getInstance(){
            if (singleton == null) singleton = new Dos();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDos(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dos";
        }
        
    }
    public static class System extends AbstractOperatingSystemCallFunction  {
        //returns the singleton instance of this class
        private static System singleton = null;
        public static System getInstance(){
            if (singleton == null) singleton = new System();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSystem(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "system";
        }
        
    }
    public static abstract class AbstractIoFunction extends AbstractSystemFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractIoFunction(this,arg);
        }
        
    }
    public static class Load extends AbstractIoFunction  {
        //returns the singleton instance of this class
        private static Load singleton = null;
        public static Load getInstance(){
            if (singleton == null) singleton = new Load();
            return singleton;
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
    public static class Save extends AbstractIoFunction  {
        //returns the singleton instance of this class
        private static Save singleton = null;
        public static Save getInstance(){
            if (singleton == null) singleton = new Save();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSave(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "save";
        }
        
    }
    public static class Input extends AbstractIoFunction  {
        //returns the singleton instance of this class
        private static Input singleton = null;
        public static Input getInstance(){
            if (singleton == null) singleton = new Input();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInput(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "input";
        }
        
    }
    public static class Textscan extends AbstractIoFunction  {
        //returns the singleton instance of this class
        private static Textscan singleton = null;
        public static Textscan getInstance(){
            if (singleton == null) singleton = new Textscan();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTextscan(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "textscan";
        }
        
    }
    public static abstract class AbstractPosixIoFunction extends AbstractIoFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPosixIoFunction(this,arg);
        }
        
    }
    public static class Sprintf extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Sprintf singleton = null;
        public static Sprintf getInstance(){
            if (singleton == null) singleton = new Sprintf();
            return singleton;
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
    public static class Sscanf extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Sscanf singleton = null;
        public static Sscanf getInstance(){
            if (singleton == null) singleton = new Sscanf();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSscanf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sscanf";
        }
        
    }
    public static class Fprintf extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fprintf singleton = null;
        public static Fprintf getInstance(){
            if (singleton == null) singleton = new Fprintf();
            return singleton;
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
    public static class Ftell extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Ftell singleton = null;
        public static Ftell getInstance(){
            if (singleton == null) singleton = new Ftell();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFtell(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ftell";
        }
        
    }
    public static class Ferror extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Ferror singleton = null;
        public static Ferror getInstance(){
            if (singleton == null) singleton = new Ferror();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFerror(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ferror";
        }
        
    }
    public static class Fopen extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fopen singleton = null;
        public static Fopen getInstance(){
            if (singleton == null) singleton = new Fopen();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFopen(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fopen";
        }
        
    }
    public static class Fread extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fread singleton = null;
        public static Fread getInstance(){
            if (singleton == null) singleton = new Fread();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFread(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fread";
        }
        
    }
    public static class Frewind extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Frewind singleton = null;
        public static Frewind getInstance(){
            if (singleton == null) singleton = new Frewind();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFrewind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "frewind";
        }
        
    }
    public static class Fscanf extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fscanf singleton = null;
        public static Fscanf getInstance(){
            if (singleton == null) singleton = new Fscanf();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFscanf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fscanf";
        }
        
    }
    public static class Fseek extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fseek singleton = null;
        public static Fseek getInstance(){
            if (singleton == null) singleton = new Fseek();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFseek(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fseek";
        }
        
    }
    public static class Fwrite extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fwrite singleton = null;
        public static Fwrite getInstance(){
            if (singleton == null) singleton = new Fwrite();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFwrite(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fwrite";
        }
        
    }
    public static class Fgetl extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fgetl singleton = null;
        public static Fgetl getInstance(){
            if (singleton == null) singleton = new Fgetl();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFgetl(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fgetl";
        }
        
    }
    public static class Fgets extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fgets singleton = null;
        public static Fgets getInstance(){
            if (singleton == null) singleton = new Fgets();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFgets(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fgets";
        }
        
    }
    public static class Fclose extends AbstractPosixIoFunction  {
        //returns the singleton instance of this class
        private static Fclose singleton = null;
        public static Fclose getInstance(){
            if (singleton == null) singleton = new Fclose();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFclose(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fclose";
        }
        
    }
}