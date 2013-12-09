
package natlab.tame.builtin;

//decloare the required imports:
import java.util.*;
import natlab.toolkits.path.BuiltinQuery;
import natlab.tame.builtin.classprop.*;
import natlab.tame.builtin.classprop.ast.*;
import natlab.tame.builtin.shapeprop.*;
import natlab.tame.builtin.shapeprop.ast.*; //XU -- remove this!!!!! XU did
import natlab.tame.builtin.isComplexInfoProp.*;
import natlab.tame.builtin.isComplexInfoProp.ast.*;


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
     * returns the names of all defined Builtins as an unmodifiable set
     */
    public static Set<String> getAllBuiltinNames(){
      return Collections.unmodifiableSet(builtinMap.keySet());
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
        builtinMap.put("i",I.getInstance());
        builtinMap.put("j",J.getInstance());
        builtinMap.put("tril",Tril.getInstance());
        builtinMap.put("triu",Triu.getInstance());
        builtinMap.put("diag",Diag.getInstance());
        builtinMap.put("real",Real.getInstance());
        builtinMap.put("imag",Imag.getInstance());
        builtinMap.put("abs",Abs.getInstance());
        builtinMap.put("conj",Conj.getInstance());
        builtinMap.put("sign",Sign.getInstance());
        builtinMap.put("uplus",Uplus.getInstance());
        builtinMap.put("uminus",Uminus.getInstance());
        builtinMap.put("fix",Fix.getInstance());
        builtinMap.put("round",Round.getInstance());
        builtinMap.put("floor",Floor.getInstance());
        builtinMap.put("ceil",Ceil.getInstance());
        builtinMap.put("complex",Complex.getInstance());
        builtinMap.put("plus",Plus.getInstance());
        builtinMap.put("minus",Minus.getInstance());
        builtinMap.put("times",Times.getInstance());
        builtinMap.put("power",Power.getInstance());
        builtinMap.put("ldivide",Ldivide.getInstance());
        builtinMap.put("rdivide",Rdivide.getInstance());
        builtinMap.put("mod",Mod.getInstance());
        builtinMap.put("rem",Rem.getInstance());
        builtinMap.put("cross",Cross.getInstance());
        builtinMap.put("mtimes",Mtimes.getInstance());
        builtinMap.put("mpower",Mpower.getInstance());
        builtinMap.put("mldivide",Mldivide.getInstance());
        builtinMap.put("mrdivide",Mrdivide.getInstance());
        builtinMap.put("dot",Dot.getInstance());
        builtinMap.put("min",Min.getInstance());
        builtinMap.put("max",Max.getInstance());
        builtinMap.put("median",Median.getInstance());
        builtinMap.put("sqrt",Sqrt.getInstance());
        builtinMap.put("realsqrt",Realsqrt.getInstance());
        builtinMap.put("erf",Erf.getInstance());
        builtinMap.put("erfinv",Erfinv.getInstance());
        builtinMap.put("erfc",Erfc.getInstance());
        builtinMap.put("erfcinv",Erfcinv.getInstance());
        builtinMap.put("gamma",Gamma.getInstance());
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
        builtinMap.put("inv",Inv.getInstance());
        builtinMap.put("atan2",Atan2.getInstance());
        builtinMap.put("hypot",Hypot.getInstance());
        builtinMap.put("eps",Eps.getInstance());
        builtinMap.put("cumsum",Cumsum.getInstance());
        builtinMap.put("cumprod",Cumprod.getInstance());
        builtinMap.put("mode",Mode.getInstance());
        builtinMap.put("prod",Prod.getInstance());
        builtinMap.put("sum",Sum.getInstance());
        builtinMap.put("mean",Mean.getInstance());
        builtinMap.put("eig",Eig.getInstance());
        builtinMap.put("norm",Norm.getInstance());
        builtinMap.put("rank",Rank.getInstance());
        builtinMap.put("cond",Cond.getInstance());
        builtinMap.put("det",Det.getInstance());
        builtinMap.put("rcond",Rcond.getInstance());
        builtinMap.put("linsolve",Linsolve.getInstance());
        builtinMap.put("schur",Schur.getInstance());
        builtinMap.put("ordschur",Ordschur.getInstance());
        builtinMap.put("lu",Lu.getInstance());
        builtinMap.put("chol",Chol.getInstance());
        builtinMap.put("svd",Svd.getInstance());
        builtinMap.put("qr",Qr.getInstance());
        builtinMap.put("bitand",Bitand.getInstance());
        builtinMap.put("bitor",Bitor.getInstance());
        builtinMap.put("bitxor",Bitxor.getInstance());
        builtinMap.put("bitcmp",Bitcmp.getInstance());
        builtinMap.put("bitset",Bitset.getInstance());
        builtinMap.put("bitget",Bitget.getInstance());
        builtinMap.put("bitshift",Bitshift.getInstance());
        builtinMap.put("find",Find.getInstance());
        builtinMap.put("nnz",Nnz.getInstance());
        builtinMap.put("not",Not.getInstance());
        builtinMap.put("any",Any.getInstance());
        builtinMap.put("all",All.getInstance());
        builtinMap.put("isreal",Isreal.getInstance());
        builtinMap.put("isinf",Isinf.getInstance());
        builtinMap.put("isfinite",Isfinite.getInstance());
        builtinMap.put("isnan",Isnan.getInstance());
        builtinMap.put("eq",Eq.getInstance());
        builtinMap.put("ne",Ne.getInstance());
        builtinMap.put("lt",Lt.getInstance());
        builtinMap.put("gt",Gt.getInstance());
        builtinMap.put("le",Le.getInstance());
        builtinMap.put("ge",Ge.getInstance());
        builtinMap.put("and",And.getInstance());
        builtinMap.put("or",Or.getInstance());
        builtinMap.put("xor",Xor.getInstance());
        builtinMap.put("colon",Colon.getInstance());
        builtinMap.put("ones",Ones.getInstance());
        builtinMap.put("zeros",Zeros.getInstance());
        builtinMap.put("magic",Magic.getInstance());
        builtinMap.put("eye",Eye.getInstance());
        builtinMap.put("inf",Inf.getInstance());
        builtinMap.put("nan",Nan.getInstance());
        builtinMap.put("true",True.getInstance());
        builtinMap.put("false",False.getInstance());
        builtinMap.put("double",Double.getInstance());
        builtinMap.put("single",Single.getInstance());
        builtinMap.put("char",Char.getInstance());
        builtinMap.put("logical",Logical.getInstance());
        builtinMap.put("int8",Int8.getInstance());
        builtinMap.put("int16",Int16.getInstance());
        builtinMap.put("int32",Int32.getInstance());
        builtinMap.put("int64",Int64.getInstance());
        builtinMap.put("uint8",Uint8.getInstance());
        builtinMap.put("uint16",Uint16.getInstance());
        builtinMap.put("uint32",Uint32.getInstance());
        builtinMap.put("uint64",Uint64.getInstance());
        builtinMap.put("cell",Cell.getInstance());
        builtinMap.put("cellhorzcat",Cellhorzcat.getInstance());
        builtinMap.put("cellvertcat",Cellvertcat.getInstance());
        builtinMap.put("isfield",Isfield.getInstance());
        builtinMap.put("struct",Struct.getInstance());
        builtinMap.put("objectFunction",ObjectFunction.getInstance());
        builtinMap.put("sort",Sort.getInstance());
        builtinMap.put("unique",Unique.getInstance());
        builtinMap.put("upper",Upper.getInstance());
        builtinMap.put("lower",Lower.getInstance());
        builtinMap.put("deblank",Deblank.getInstance());
        builtinMap.put("strtrim",Strtrim.getInstance());
        builtinMap.put("strfind",Strfind.getInstance());
        builtinMap.put("findstr",Findstr.getInstance());
        builtinMap.put("strrep",Strrep.getInstance());
        builtinMap.put("strcmp",Strcmp.getInstance());
        builtinMap.put("strcmpi",Strcmpi.getInstance());
        builtinMap.put("strncmpi",Strncmpi.getInstance());
        builtinMap.put("strncmp",Strncmp.getInstance());
        builtinMap.put("regexptranslate",Regexptranslate.getInstance());
        builtinMap.put("regexp",Regexp.getInstance());
        builtinMap.put("regexpi",Regexpi.getInstance());
        builtinMap.put("regexprep",Regexprep.getInstance());
        builtinMap.put("class",Class.getInstance());
        builtinMap.put("size",Size.getInstance());
        builtinMap.put("length",Length.getInstance());
        builtinMap.put("ndims",Ndims.getInstance());
        builtinMap.put("numel",Numel.getInstance());
        builtinMap.put("end",End.getInstance());
        builtinMap.put("isobject",Isobject.getInstance());
        builtinMap.put("isfloat",Isfloat.getInstance());
        builtinMap.put("isinteger",Isinteger.getInstance());
        builtinMap.put("islogical",Islogical.getInstance());
        builtinMap.put("isstruct",Isstruct.getInstance());
        builtinMap.put("ischar",Ischar.getInstance());
        builtinMap.put("iscell",Iscell.getInstance());
        builtinMap.put("isnumeric",Isnumeric.getInstance());
        builtinMap.put("isa",Isa.getInstance());
        builtinMap.put("isempty",Isempty.getInstance());
        builtinMap.put("isvector",Isvector.getInstance());
        builtinMap.put("isscalar",Isscalar.getInstance());
        builtinMap.put("isequalwithequalnans",Isequalwithequalnans.getInstance());
        builtinMap.put("isequal",Isequal.getInstance());
        builtinMap.put("reshape",Reshape.getInstance());
        builtinMap.put("permute",Permute.getInstance());
        builtinMap.put("squeeze",Squeeze.getInstance());
        builtinMap.put("transpose",Transpose.getInstance());
        builtinMap.put("ctranspose",Ctranspose.getInstance());
        builtinMap.put("horzcat",Horzcat.getInstance());
        builtinMap.put("vertcat",Vertcat.getInstance());
        builtinMap.put("cat",Cat.getInstance());
        builtinMap.put("subsasgn",Subsasgn.getInstance());
        builtinMap.put("subsref",Subsref.getInstance());
        builtinMap.put("structfun",Structfun.getInstance());
        builtinMap.put("arrayfun",Arrayfun.getInstance());
        builtinMap.put("cellfun",Cellfun.getInstance());
        builtinMap.put("superiorto",Superiorto.getInstance());
        builtinMap.put("superiorfloat",Superiorfloat.getInstance());
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
        builtinMap.put("nargin",Nargin.getInstance());
        builtinMap.put("nargout",Nargout.getInstance());
        builtinMap.put("methods",Methods.getInstance());
        builtinMap.put("fieldnames",Fieldnames.getInstance());
        builtinMap.put("disp",Disp.getInstance());
        builtinMap.put("display",Display.getInstance());
        builtinMap.put("clc",Clc.getInstance());
        builtinMap.put("error",Error.getInstance());
        builtinMap.put("warning",Warning.getInstance());
        builtinMap.put("echo",Echo.getInstance());
        builtinMap.put("diary",Diary.getInstance());
        builtinMap.put("message",Message.getInstance());
        builtinMap.put("lastwarn",Lastwarn.getInstance());
        builtinMap.put("lasterror",Lasterror.getInstance());
        builtinMap.put("format",Format.getInstance());
        builtinMap.put("rand",Rand.getInstance());
        builtinMap.put("randn",Randn.getInstance());
        builtinMap.put("randi",Randi.getInstance());
        builtinMap.put("randperm",Randperm.getInstance());
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
        builtinMap.put("imwrite",Imwrite.getInstance());
        builtinMap.put("sparse",Sparse.getInstance());
        builtinMap.put("realmax",Realmax.getInstance());
        builtinMap.put("histc",Histc.getInstance());
        builtinMap.put("blkdiag",Blkdiag.getInstance());
        builtinMap.put("var",Var.getInstance());
        builtinMap.put("std",Std.getInstance());
    }    
    
    //the actual Builtin Classes:
    
    public static abstract class AbstractRoot extends Builtin  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRoot(this,arg);
        }
        
    }
    public static abstract class AbstractPureFunction extends AbstractRoot  {
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
    public static abstract class AbstractConstant extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstant(this,arg);
        }
        
    }
    public static abstract class AbstractDoubleConstant extends AbstractConstant implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDoubleConstant(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("none -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Pi extends AbstractDoubleConstant implements HasisComplexPropagationInfo {
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
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A*->NUMAARGS==0?R");
            }
            return isComplexPropInfo;
        }

    }
    public static class I extends AbstractDoubleConstant implements HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static I singleton = null;
        public static I getInstance(){
            if (singleton == null) singleton = new I();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseI(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "i";
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A*->NUMAARGS==0?X:");
            }
            return isComplexPropInfo;
        }

    }
    public static class J extends AbstractDoubleConstant implements HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static J singleton = null;
        public static J getInstance(){
            if (singleton == null) singleton = new J();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseJ(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "j";
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A*->NUMAARGS==0?X:");
            }
            return isComplexPropInfo;
        }

    }
    public static abstract class AbstractAnyMatrixFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractProperAnyMatrixFunction extends AbstractAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractProperAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryAnyMatrixFunction extends AbstractProperAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class ElementalUnaryAnyMatrixFunction extends AbstractUnaryAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseElementalUnaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class ArrayUnaryAnyMatrixFunction extends AbstractUnaryAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseArrayUnaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractBinaryAnyMatrixFunction extends AbstractProperAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class ElementalBinaryAnyMatrixFunction extends AbstractBinaryAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseElementalBinaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class ArrayBinaryAnyMatrixFunction extends AbstractBinaryAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseArrayBinaryAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractImproperAnyMatrixFunction extends AbstractAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImproperAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractDiagonalSensitive extends AbstractImproperAnyMatrixFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDiagonalSensitive(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M,$?->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix matrix? -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R,A? -> R || X,A? -> A");
            }
            return isComplexPropInfo;
        }

    }
    public static class Tril extends AbstractDiagonalSensitive  {
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
        
    }
    public static class Triu extends AbstractDiagonalSensitive  {
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
        
    }
    public static class Diag extends AbstractDiagonalSensitive implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Diag singleton = null;
        public static Diag getInstance(){
            if (singleton == null) singleton = new Diag();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDiag(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "diag";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[m,n],k=minimum(m,n)->[k,1]");
            }
            return shapePropInfo;
        }

    }
    public static abstract class AbstractDimensionSensitiveAnyMatrixFunction extends AbstractImproperAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionSensitiveAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class DimensionCollapsingAnyMatrixFunction extends AbstractDimensionSensitiveAnyMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDimensionCollapsingAnyMatrixFunction(this,arg);
        }
        
    }
    public static abstract class AbstractNumericFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericFunction(this,arg);
        }
        
    }
    public static abstract class AbstractProperNumericFunction extends AbstractNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractProperNumericFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryNumericFunction extends AbstractProperNumericFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryNumericFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("numeric -> 0, char|logical -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->R");
            }
            return isComplexPropInfo;
        }

    }
    public static abstract class AbstractElementalUnaryNumericFunction extends AbstractUnaryNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryNumericFunction(this,arg);
        }
        
    }
    public static class Real extends AbstractElementalUnaryNumericFunction  {
        //returns the singleton instance of this class
        private static Real singleton = null;
        public static Real getInstance(){
            if (singleton == null) singleton = new Real();
            return singleton;
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
    public static class Imag extends AbstractElementalUnaryNumericFunction implements HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Imag singleton = null;
        public static Imag getInstance(){
            if (singleton == null) singleton = new Imag();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImag(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "imag";
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->X");
            }
            return isComplexPropInfo;
        }

    }
    public static class Abs extends AbstractElementalUnaryNumericFunction  {
        //returns the singleton instance of this class
        private static Abs singleton = null;
        public static Abs getInstance(){
            if (singleton == null) singleton = new Abs();
            return singleton;
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
    public static class Conj extends AbstractElementalUnaryNumericFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Conj singleton = null;
        public static Conj getInstance(){
            if (singleton == null) singleton = new Conj();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseConj(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "conj";
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical->error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

    }
    public static class Sign extends AbstractElementalUnaryNumericFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Sign singleton = null;
        public static Sign getInstance(){
            if (singleton == null) singleton = new Sign();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSign(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sign";
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical -> error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

    }
    public static abstract class AbstractElementalUnaryArithmetic extends AbstractElementalUnaryNumericFunction implements HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryArithmetic(this,arg);
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->NUMXARGS>0?X:R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Uplus extends AbstractElementalUnaryArithmetic  {
        //returns the singleton instance of this class
        private static Uplus singleton = null;
        public static Uplus getInstance(){
            if (singleton == null) singleton = new Uplus();
            return singleton;
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
    public static class Uminus extends AbstractElementalUnaryArithmetic  {
        //returns the singleton instance of this class
        private static Uminus singleton = null;
        public static Uminus getInstance(){
            if (singleton == null) singleton = new Uminus();
            return singleton;
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
    public static abstract class AbstractRoundingOperation extends AbstractElementalUnaryNumericFunction implements HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRoundingOperation(this,arg);
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical -> error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->NUMXARGS>0?X:R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Fix extends AbstractRoundingOperation implements HasClassPropagationInfo {
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
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical->double, natlab");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
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
    public static abstract class AbstractArrayUnaryNumericFunction extends AbstractUnaryNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayUnaryNumericFunction(this,arg);
        }
        
    }
    public static abstract class ArrayUnaryArithmetic extends AbstractArrayUnaryNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseArrayUnaryArithmetic(this,arg);
        }
        
    }
    public static abstract class AbstractBinaryNumericFunction extends AbstractProperNumericFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryNumericFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M,M?->M");
            }
            return shapePropInfo;
        }

        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("(int logical)|(logical int)->error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(logical|char->double, (numeric double|0->0) | (double|1 numeric->1))");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractElementalBinaryNumericFunction extends AbstractBinaryNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryNumericFunction(this,arg);
        }
        
    }
    public static class Complex extends AbstractElementalBinaryNumericFunction implements HasClassPropagationInfo, HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Complex singleton = null;
        public static Complex getInstance(){
            if (singleton == null) singleton = new Complex();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseComplex(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "complex";
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("char|logical -> error, char|logical any->error, any char|logical->error, natlab");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R,R? -> X");
            }
            return isComplexPropInfo;
        }

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(logical|char->double, (numeric (double|0)? ->0) | (double|1 numeric->1))");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractElementalBinaryArithmetic extends AbstractElementalBinaryNumericFunction implements HasShapePropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryArithmetic(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R,R ->R || A,A->NUMXARGS>0?X:A");
            }
            return isComplexPropInfo;
        }

    }
    public static class Plus extends AbstractElementalBinaryArithmetic  {
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
    public static class Minus extends AbstractElementalBinaryArithmetic  {
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
    public static class Times extends AbstractElementalBinaryArithmetic  {
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
    public static class Power extends AbstractElementalBinaryArithmetic implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Power singleton = null;
        public static Power getInstance(){
            if (singleton == null) singleton = new Power();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePower(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "power";
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical 0->error, parent");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

    }
    public static abstract class AbstractDividingElementalArithmetic extends AbstractElementalBinaryArithmetic implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDividingElementalArithmetic(this,arg);
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical 0->error, parent");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R,R->R || X,R->X || R,X->X || X,X->X");
            }
            return isComplexPropInfo;
        }

    }
    public static class Ldivide extends AbstractDividingElementalArithmetic  {
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
    public static class Rdivide extends AbstractDividingElementalArithmetic  {
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
    public static class Mod extends AbstractDividingElementalArithmetic  {
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
    public static class Rem extends AbstractDividingElementalArithmetic  {
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
    public static abstract class AbstractArrayBinaryNumericFunction extends AbstractBinaryNumericFunction implements HasShapePropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayBinaryNumericFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M,M,atLeastOneDimNLT(3)->M||M,M,$,n=previousScalar(),k=previousShapeDim(n),latestMatchedLowercaseNLT(3)->M");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A,A,R?->R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Cross extends AbstractArrayBinaryNumericFunction  {
        //returns the singleton instance of this class
        private static Cross singleton = null;
        public static Cross getInstance(){
            if (singleton == null) singleton = new Cross();
            return singleton;
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
    public static abstract class AbstractArrayBinaryArithmetic extends AbstractArrayBinaryNumericFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayBinaryArithmetic(this,arg);
        }
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical 0->error, parent");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[m,n],[n,k]->[m,k]");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("X,X ->R || R,R ->R || A,A->NUMXARGS>0?X:A");
            }
            return isComplexPropInfo;
        }

    }
    public static class Mtimes extends AbstractArrayBinaryArithmetic implements HasShapePropagationInfo, HasClassPropagationInfo {
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
        
        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical 0->double, parent");
                matlabClassPropInfo.setVar("parent",super.getMatlabClassPropagationInfo());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M||[m,n],[n,k]->[m,k]");
            }
            return shapePropInfo;
        }

    }
    public static class Mpower extends AbstractArrayBinaryArithmetic implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Mpower singleton = null;
        public static Mpower getInstance(){
            if (singleton == null) singleton = new Mpower();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMpower(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mpower";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$,$|M->M||$|M,$->M");
            }
            return shapePropInfo;
        }

    }
    public static abstract class AbstractDividingArrayArithmetic extends AbstractArrayBinaryArithmetic implements HasShapePropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDividingArrayArithmetic(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

    }
    public static class Mldivide extends AbstractDividingArrayArithmetic  {
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
    public static class Mrdivide extends AbstractDividingArrayArithmetic  {
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
    public static abstract class AbstractImproperNumericFunction extends AbstractNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImproperNumericFunction(this,arg);
        }
        
    }
    public static abstract class AbstractDimensionSensitiveNumericFunction extends AbstractImproperNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionSensitiveNumericFunction(this,arg);
        }
        
    }
    public static class Dot extends AbstractDimensionSensitiveNumericFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Dot singleton = null;
        public static Dot getInstance(){
            if (singleton == null) singleton = new Dot();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDot(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "dot";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[1,n]|[n,1],[1,n]|[n,1]->$||M,M,M(1)=1->M");
            }
            return shapePropInfo;
        }

        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical any->error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(logical|char->double,(single float)|(float single)->single || numeric double|0->double || double|1 numeric->double) (numeric|logical)?");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractDimensionCollapsingNumericFunction extends AbstractDimensionSensitiveNumericFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionCollapsingNumericFunction(this,arg);
        }
        
    }
    public static abstract class AbstractMinOrMax extends AbstractDimensionCollapsingNumericFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMinOrMax(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[1,n]|[n,1]->$||M,M(1)=1->M||M,M->M||M,[],$,n=previousScalar(),M(n)=1->M");
            }
            return shapePropInfo;
        }

        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical logical->logical double, char|logical -> error, char|logical any->error, any char|logical->error, natlab");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix any (numeric|logical)->0 double, logical logical->logical double, coerce(logical|char->double, numeric (double|0)?->0 double || double|1 numeric->1 double), matrix->0 double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Min extends AbstractMinOrMax  {
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
    public static class Max extends AbstractMinOrMax  {
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
    public static class Median extends AbstractMinOrMax implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Median singleton = null;
        public static Median getInstance(){
            if (singleton == null) singleton = new Median();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMedian(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "median";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[1,n]|[n,1]->$||M,M(1)=1->M||M,M->M||M,$,n=previousScalar(),M(n)=1->M");
            }
            return shapePropInfo;
        }

    }
    public static abstract class AbstractFloatFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFloatFunction(this,arg);
        }
        
    }
    public static abstract class AbstractProperFloatFunction extends AbstractFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractProperFloatFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryFloatFunction extends AbstractProperFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryFloatFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float->0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->NUMXARGS>0?X:R");
            }
            return isComplexPropInfo;
        }

    }
    public static abstract class AbstractElementalUnaryFloatFunction extends AbstractUnaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryFloatFunction(this,arg);
        }
        
    }
    public static class Sqrt extends AbstractElementalUnaryFloatFunction  {
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
    public static class Realsqrt extends AbstractElementalUnaryFloatFunction  {
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
    public static class Erf extends AbstractElementalUnaryFloatFunction  {
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
    public static class Erfinv extends AbstractElementalUnaryFloatFunction  {
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
    public static class Erfc extends AbstractElementalUnaryFloatFunction  {
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
    public static class Erfcinv extends AbstractElementalUnaryFloatFunction  {
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
    public static class Gamma extends AbstractElementalUnaryFloatFunction  {
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
    public static class Gammaln extends AbstractElementalUnaryFloatFunction  {
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
    public static class Exp extends AbstractElementalUnaryFloatFunction  {
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
    public static class Log extends AbstractElementalUnaryFloatFunction  {
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
    public static class Log2 extends AbstractElementalUnaryFloatFunction  {
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
    public static class Log10 extends AbstractElementalUnaryFloatFunction  {
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
    public static abstract class AbstractForwardTrigonometricFunction extends AbstractElementalUnaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractForwardTrigonometricFunction(this,arg);
        }
        
    }
    public static abstract class AbstractRadianTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRadianTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sin extends AbstractRadianTrigonometricFunction  {
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
    public static class Cos extends AbstractRadianTrigonometricFunction  {
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
    public static class Tan extends AbstractRadianTrigonometricFunction  {
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
    public static class Cot extends AbstractRadianTrigonometricFunction  {
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
    public static class Sec extends AbstractRadianTrigonometricFunction  {
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
    public static class Csc extends AbstractRadianTrigonometricFunction  {
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
    public static abstract class AbstractDegreeTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDegreeTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sind extends AbstractDegreeTrigonometricFunction  {
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
    public static class Cosd extends AbstractDegreeTrigonometricFunction  {
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
    public static class Tand extends AbstractDegreeTrigonometricFunction  {
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
    public static class Cotd extends AbstractDegreeTrigonometricFunction  {
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
    public static class Secd extends AbstractDegreeTrigonometricFunction  {
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
    public static class Cscd extends AbstractDegreeTrigonometricFunction  {
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
    public static abstract class AbstractInverseTrigonmetricFunction extends AbstractElementalUnaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static abstract class AbstractRadianInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRadianInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asin extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static class Acos extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static class Atan extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static class Acot extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static class Asec extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static class Acsc extends AbstractRadianInverseTrigonmetricFunction  {
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
    public static abstract class AbstractDegreeInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDegreeInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asind extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static class Acosd extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static class Atand extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static class Acotd extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static class Asecd extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static class Acscd extends AbstractDegreeInverseTrigonmetricFunction  {
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
    public static abstract class AbstractArrayUnaryFloatFunction extends AbstractUnaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayUnaryFloatFunction(this,arg);
        }
        
    }
    public static abstract class AbstractSquareArrayUnaryFloatFunction extends AbstractArrayUnaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractSquareArrayUnaryFloatFunction(this,arg);
        }
        
    }
    public static class Logm extends AbstractSquareArrayUnaryFloatFunction  {
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
    public static class Sqrtm extends AbstractSquareArrayUnaryFloatFunction  {
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
    public static class Expm extends AbstractSquareArrayUnaryFloatFunction  {
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
    public static class Inv extends AbstractSquareArrayUnaryFloatFunction  {
        //returns the singleton instance of this class
        private static Inv singleton = null;
        public static Inv getInstance(){
            if (singleton == null) singleton = new Inv();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInv(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "inv";
        }
        
    }
    public static abstract class AbstractBinaryFloatFunction extends AbstractProperFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryFloatFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float double|0 -> 0, double|1 float -> 1");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractElementalBinaryFloatFunction extends AbstractBinaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryFloatFunction(this,arg);
        }
        
    }
    public static class Atan2 extends AbstractElementalBinaryFloatFunction  {
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
    public static abstract class AbstractArrayBinaryFloatFunction extends AbstractBinaryFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayBinaryFloatFunction(this,arg);
        }
        
    }
    public static class Hypot extends AbstractArrayBinaryFloatFunction  {
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
    public static abstract class AbstractImproperFloatFunction extends AbstractFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImproperFloatFunction(this,arg);
        }
        
    }
    public static class Eps extends AbstractImproperFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Eps singleton = null;
        public static Eps getInstance(){
            if (singleton == null) singleton = new Eps();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEps(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "eps";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M->M||'double'|'single'->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("none->double, float->0, typeString(float)");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractDimensionSensitiveFloatFunction extends AbstractImproperFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionSensitiveFloatFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M,$?->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float (numeric|logical)? -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Cumsum extends AbstractDimensionSensitiveFloatFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(logical->double, parent)");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Cumprod extends AbstractDimensionSensitiveFloatFunction  {
        //returns the singleton instance of this class
        private static Cumprod singleton = null;
        public static Cumprod getInstance(){
            if (singleton == null) singleton = new Cumprod();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCumprod(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cumprod";
        }
        
    }
    public static abstract class AbstractDimensionCollapsingFloatFunction extends AbstractDimensionSensitiveFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionCollapsingFloatFunction(this,arg);
        }
        
    }
    public static class Mode extends AbstractDimensionCollapsingFloatFunction implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Mode singleton = null;
        public static Mode getInstance(){
            if (singleton == null) singleton = new Mode();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMode(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mode";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$->$||M,M(1)=1->M||M,$,n=previousScalar(),M(n)=1->M");
            }
            return shapePropInfo;
        }

    }
    public static class Prod extends AbstractDimensionCollapsingFloatFunction implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Prod singleton = null;
        public static Prod getInstance(){
            if (singleton == null) singleton = new Prod();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseProd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "prod";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$->$||M,M(1)=1->M||M,$,n=previousScalar(),M(n)=1->M");
            }
            return shapePropInfo;
        }

    }
    public static class Sum extends AbstractDimensionCollapsingFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Sum singleton = null;
        public static Sum getInstance(){
            if (singleton == null) singleton = new Sum();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSum(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sum";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[1,n]|[n,1],('double'|'native')?->$||M,M(1)=1,('double'|'native')?->M||M,$,n=previousScalar(),M(n)=1,('double'|'native')?->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(int|char|logical->double,parent)");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("X,R? -> X || R,R? -> R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Mean extends AbstractDimensionCollapsingFloatFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Mean singleton = null;
        public static Mean getInstance(){
            if (singleton == null) singleton = new Mean();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMean(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mean";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[1,n]|[n,1]->$||M,M(1)=1->M||M,$,n=previousScalar(),M(n)=1->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(int|char|logical->double,parent)");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractMatrixLibaryFunction extends AbstractImproperFloatFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixLibaryFunction(this,arg);
        }
        
    }
    public static class Eig extends AbstractMatrixLibaryFunction implements HasShapePropagationInfo {
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
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[n,n]->[n,1]||[n,n],'nobalance'?->[n,n],[n,n]||[n,n],[n,n],('chol'|'qz')?->[n,1]||[n,n],[n,n],('chol'|'qz')?->[n,n],[n,n]");
            }
            return shapePropInfo;
        }

    }
    public static class Norm extends AbstractMatrixLibaryFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
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
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,($|'fro')?->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float (float|logical|char)?->0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Rank extends AbstractMatrixLibaryFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Rank singleton = null;
        public static Rank getInstance(){
            if (singleton == null) singleton = new Rank();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRank(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "rank";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M,$?->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float matrix?->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static abstract class AbstractFacotorizationFunction extends AbstractImproperFloatFunction  {
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
    public static class Qr extends AbstractFacotorizationFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float (float|logical)?->0 0 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractBitFunction extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBitFunction(this,arg);
        }
        
    }
    public static abstract class AbstractProperBitFunction extends AbstractBitFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractProperBitFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("uint double|0->0, double|1 uint->1, logical logical->logical, double|logical double|logical->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Bitand extends AbstractProperBitFunction  {
        //returns the singleton instance of this class
        private static Bitand singleton = null;
        public static Bitand getInstance(){
            if (singleton == null) singleton = new Bitand();
            return singleton;
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
    public static class Bitor extends AbstractProperBitFunction  {
        //returns the singleton instance of this class
        private static Bitor singleton = null;
        public static Bitor getInstance(){
            if (singleton == null) singleton = new Bitor();
            return singleton;
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
    public static class Bitxor extends AbstractProperBitFunction  {
        //returns the singleton instance of this class
        private static Bitxor singleton = null;
        public static Bitxor getInstance(){
            if (singleton == null) singleton = new Bitxor();
            return singleton;
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
    public static abstract class AbstractImproperBitFunciton extends AbstractBitFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImproperBitFunciton(this,arg);
        }
        
    }
    public static class Bitcmp extends AbstractImproperBitFunciton  {
        //returns the singleton instance of this class
        private static Bitcmp singleton = null;
        public static Bitcmp getInstance(){
            if (singleton == null) singleton = new Bitcmp();
            return singleton;
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
    public static class Bitset extends AbstractImproperBitFunciton  {
        //returns the singleton instance of this class
        private static Bitset singleton = null;
        public static Bitset getInstance(){
            if (singleton == null) singleton = new Bitset();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBitset(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "bitset";
        }
        
    }
    public static class Bitget extends AbstractImproperBitFunciton  {
        //returns the singleton instance of this class
        private static Bitget singleton = null;
        public static Bitget getInstance(){
            if (singleton == null) singleton = new Bitget();
            return singleton;
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
    public static class Bitshift extends AbstractImproperBitFunciton  {
        //returns the singleton instance of this class
        private static Bitshift singleton = null;
        public static Bitshift getInstance(){
            if (singleton == null) singleton = new Bitshift();
            return singleton;
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
    public static abstract class AbstractMatrixQuery extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixQuery(this,arg);
        }
        
    }
    public static abstract class AbstractToDoubleMatrixQuery extends AbstractMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractToDoubleMatrixQuery(this,arg);
        }
        
    }
    public static class Find extends AbstractToDoubleMatrixQuery implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Find singleton = null;
        public static Find getInstance(){
            if (singleton == null) singleton = new Find();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "find";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix (matrix char?)?->double double double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractUnaryToScalarDoubleMatrixQuery extends AbstractToDoubleMatrixQuery implements HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryToScalarDoubleMatrixQuery(this,arg);
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractToScalarDoubleMatrixQuery extends AbstractUnaryToScalarDoubleMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractToScalarDoubleMatrixQuery(this,arg);
        }
        
    }
    public static class Nnz extends AbstractToScalarDoubleMatrixQuery  {
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
    public static abstract class AbstractToLogicalMatrixQuery extends AbstractMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractToLogicalMatrixQuery(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryToLogicalMatrixQuery extends AbstractToLogicalMatrixQuery implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryToLogicalMatrixQuery(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix->logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractScalarUnaryToLogicalMatrixQuery extends AbstractUnaryToLogicalMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarUnaryToLogicalMatrixQuery(this,arg);
        }
        
    }
    public static class Not extends AbstractScalarUnaryToLogicalMatrixQuery  {
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
    public static class Any extends AbstractScalarUnaryToLogicalMatrixQuery  {
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
    public static class All extends AbstractScalarUnaryToLogicalMatrixQuery  {
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
    public static class Isreal extends AbstractScalarUnaryToLogicalMatrixQuery  {
        //returns the singleton instance of this class
        private static Isreal singleton = null;
        public static Isreal getInstance(){
            if (singleton == null) singleton = new Isreal();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsreal(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isreal";
        }
        
    }
    public static abstract class AbstractElementalUnaryToLogicalMatrixQuery extends AbstractUnaryToLogicalMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalUnaryToLogicalMatrixQuery(this,arg);
        }
        
    }
    public static class Isinf extends AbstractElementalUnaryToLogicalMatrixQuery  {
        //returns the singleton instance of this class
        private static Isinf singleton = null;
        public static Isinf getInstance(){
            if (singleton == null) singleton = new Isinf();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsinf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isinf";
        }
        
    }
    public static class Isfinite extends AbstractElementalUnaryToLogicalMatrixQuery  {
        //returns the singleton instance of this class
        private static Isfinite singleton = null;
        public static Isfinite getInstance(){
            if (singleton == null) singleton = new Isfinite();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsfinite(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isfinite";
        }
        
    }
    public static class Isnan extends AbstractElementalUnaryToLogicalMatrixQuery  {
        //returns the singleton instance of this class
        private static Isnan singleton = null;
        public static Isnan getInstance(){
            if (singleton == null) singleton = new Isnan();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsnan(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isnan";
        }
        
    }
    public static abstract class AbstractBinaryToLogicalMatrixQuery extends AbstractToLogicalMatrixQuery implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryToLogicalMatrixQuery(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M,$|M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("matrix matrix -> logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractElementalBinaryToLogicalMatrixQuery extends AbstractBinaryToLogicalMatrixQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementalBinaryToLogicalMatrixQuery(this,arg);
        }
        
    }
    public static abstract class AbstractRelationalOperator extends AbstractElementalBinaryToLogicalMatrixQuery implements HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRelationalOperator(this,arg);
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A,A->R");
            }
            return isComplexPropInfo;
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
    public static abstract class AbstractLogicalOperator extends AbstractElementalBinaryToLogicalMatrixQuery  {
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
    public static abstract class AbstractMatrixCreation extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixCreation(this,arg);
        }
        
    }
    public static class Colon extends AbstractMatrixCreation implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Colon singleton = null;
        public static Colon getInstance(){
            if (singleton == null) singleton = new Colon();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseColon(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "colon";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$,n=previousScalar(),$,m=previousScalar(),k=minus(m,n)->[1,k]||$,n=previousScalar(),$,i=previousScalar(),$,m=previousScalar(),k=minus(m,n),d=div(k,i)->[1,d]");
            }
            return shapePropInfo;
        }

        private CP matlabClassPropInfo = null;
        public CP getMatlabClassPropagationInfo(){
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){
                matlabClassPropInfo = ClassPropTool.parse("logical logical logical?->error, logical|double double|logical  (double|logical)? -> double, numeric double|0  (double|0)? -> 0, double|-1 (double|-1 numeric) | numeric -> -1, double|1 numeric double|1 -> 1, char (char|double char)|char -> char");
                matlabClassPropInfo.setVar("parent",new CPNone());
                matlabClassPropInfo.setVar("natlab",getClassPropagationInfo());
            }
            return matlabClassPropInfo;
        }

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(logical -> double, matlab)");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R* ->R");
            }
            return isComplexPropInfo;
        }

    }
    public static abstract class AbstractByShapeAndTypeMatrixCreation extends AbstractMatrixCreation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractByShapeAndTypeMatrixCreation(this,arg);
        }
        
    }
    public static abstract class AbstractNumericalByShapeAndTypeMatrixCreation extends AbstractByShapeAndTypeMatrixCreation implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalByShapeAndTypeMatrixCreation(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->$||($,n=previousScalar(),add())+->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("(numeric|logical)* typeString(numeric)|(none->double)");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A*->R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Ones extends AbstractNumericalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Ones singleton = null;
        public static Ones getInstance(){
            if (singleton == null) singleton = new Ones();
            return singleton;
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
    public static class Zeros extends AbstractNumericalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Zeros singleton = null;
        public static Zeros getInstance(){
            if (singleton == null) singleton = new Zeros();
            return singleton;
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
    public static class Magic extends AbstractNumericalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Magic singleton = null;
        public static Magic getInstance(){
            if (singleton == null) singleton = new Magic();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMagic(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "magic";
        }
        
    }
    public static class Eye extends AbstractNumericalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Eye singleton = null;
        public static Eye getInstance(){
            if (singleton == null) singleton = new Eye();
            return singleton;
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
    public static abstract class AbstractFloatByShapeAndTypeMatrixCreation extends AbstractNumericalByShapeAndTypeMatrixCreation implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFloatByShapeAndTypeMatrixCreation(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]|'double'|'single'->$||($,n=previousScalar(),add())+->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("(numeric|logical)* typeString(float)|(none->double)");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Inf extends AbstractFloatByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Inf singleton = null;
        public static Inf getInstance(){
            if (singleton == null) singleton = new Inf();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInf(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "inf";
        }
        
    }
    public static class Nan extends AbstractFloatByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static Nan singleton = null;
        public static Nan getInstance(){
            if (singleton == null) singleton = new Nan();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNan(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nan";
        }
        
    }
    public static abstract class AbstractLogicalByShapeAndTypeMatrixCreation extends AbstractByShapeAndTypeMatrixCreation implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalByShapeAndTypeMatrixCreation(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->$||($,n=previousScalar(),add())+->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("(numeric|logical)*->logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class True extends AbstractLogicalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static True singleton = null;
        public static True getInstance(){
            if (singleton == null) singleton = new True();
            return singleton;
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
    public static class False extends AbstractLogicalByShapeAndTypeMatrixCreation  {
        //returns the singleton instance of this class
        private static False singleton = null;
        public static False getInstance(){
            if (singleton == null) singleton = new False();
            return singleton;
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
    public static abstract class AbstractMatrixConstructor extends AbstractMatrixFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixConstructor(this,arg);
        }
        
    }
    public static class Double extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Double singleton = null;
        public static Double getInstance(){
            if (singleton == null) singleton = new Double();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDouble(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "double";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Single extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Single singleton = null;
        public static Single getInstance(){
            if (singleton == null) singleton = new Single();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSingle(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "single";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->single");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Char extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Char singleton = null;
        public static Char getInstance(){
            if (singleton == null) singleton = new Char();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseChar(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "char";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Logical extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Logical singleton = null;
        public static Logical getInstance(){
            if (singleton == null) singleton = new Logical();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLogical(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "logical";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Int8 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Int8 singleton = null;
        public static Int8 getInstance(){
            if (singleton == null) singleton = new Int8();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt8(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int8";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->int8");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Int16 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Int16 singleton = null;
        public static Int16 getInstance(){
            if (singleton == null) singleton = new Int16();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt16(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int16";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->int16");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Int32 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Int32 singleton = null;
        public static Int32 getInstance(){
            if (singleton == null) singleton = new Int32();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt32(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int32";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->int32");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Int64 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Int64 singleton = null;
        public static Int64 getInstance(){
            if (singleton == null) singleton = new Int64();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseInt64(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "int64";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->int64");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Uint8 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Uint8 singleton = null;
        public static Uint8 getInstance(){
            if (singleton == null) singleton = new Uint8();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint8(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint8";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->uint8");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Uint16 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Uint16 singleton = null;
        public static Uint16 getInstance(){
            if (singleton == null) singleton = new Uint16();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint16(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint16";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->uint16");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Uint32 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Uint32 singleton = null;
        public static Uint32 getInstance(){
            if (singleton == null) singleton = new Uint32();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint32(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint32";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->uint32");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Uint64 extends AbstractMatrixConstructor implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Uint64 singleton = null;
        public static Uint64 getInstance(){
            if (singleton == null) singleton = new Uint64();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUint64(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "uint64";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->uint64");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractCellFunction extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractCellFunction(this,arg);
        }
        
    }
    public static class Cell extends AbstractCellFunction  {
        //returns the singleton instance of this class
        private static Cell singleton = null;
        public static Cell getInstance(){
            if (singleton == null) singleton = new Cell();
            return singleton;
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
    public static abstract class AbstractCellCat extends AbstractCellFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractCellCat(this,arg);
        }
        
    }
    public static class Cellhorzcat extends AbstractCellCat  {
        //returns the singleton instance of this class
        private static Cellhorzcat singleton = null;
        public static Cellhorzcat getInstance(){
            if (singleton == null) singleton = new Cellhorzcat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCellhorzcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cellhorzcat";
        }
        
    }
    public static class Cellvertcat extends AbstractCellCat  {
        //returns the singleton instance of this class
        private static Cellvertcat singleton = null;
        public static Cellvertcat getInstance(){
            if (singleton == null) singleton = new Cellvertcat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCellvertcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cellvertcat";
        }
        
    }
    public static abstract class AbstractStructFunction extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStructFunction(this,arg);
        }
        
    }
    public static class Isfield extends AbstractStructFunction  {
        //returns the singleton instance of this class
        private static Isfield singleton = null;
        public static Isfield getInstance(){
            if (singleton == null) singleton = new Isfield();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsfield(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isfield";
        }
        
    }
    public static class Struct extends AbstractStructFunction  {
        //returns the singleton instance of this class
        private static Struct singleton = null;
        public static Struct getInstance(){
            if (singleton == null) singleton = new Struct();
            return singleton;
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
    public static abstract class AbstractMatrixOrCellOfCharFunction extends AbstractVersatileFunction implements HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixOrCellOfCharFunction(this,arg);
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Sort extends AbstractMatrixOrCellOfCharFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Sort singleton = null;
        public static Sort getInstance(){
            if (singleton == null) singleton = new Sort();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSort(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sort";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("M->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any numeric? char? -> 0 double");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Unique extends AbstractMatrixOrCellOfCharFunction  {
        //returns the singleton instance of this class
        private static Unique singleton = null;
        public static Unique getInstance(){
            if (singleton == null) singleton = new Unique();
            return singleton;
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
    public static abstract class AbstractCharFunction extends AbstractMatrixOrCellOfCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractCharFunction(this,arg);
        }
        
    }
    public static abstract class AbstractProperCharFunction extends AbstractCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractProperCharFunction(this,arg);
        }
        
    }
    public static abstract class AbstractUnaryProperCharFunction extends AbstractProperCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryProperCharFunction(this,arg);
        }
        
    }
    public static class Upper extends AbstractUnaryProperCharFunction  {
        //returns the singleton instance of this class
        private static Upper singleton = null;
        public static Upper getInstance(){
            if (singleton == null) singleton = new Upper();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseUpper(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "upper";
        }
        
    }
    public static class Lower extends AbstractUnaryProperCharFunction  {
        //returns the singleton instance of this class
        private static Lower singleton = null;
        public static Lower getInstance(){
            if (singleton == null) singleton = new Lower();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLower(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "lower";
        }
        
    }
    public static class Deblank extends AbstractUnaryProperCharFunction  {
        //returns the singleton instance of this class
        private static Deblank singleton = null;
        public static Deblank getInstance(){
            if (singleton == null) singleton = new Deblank();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseDeblank(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "deblank";
        }
        
    }
    public static class Strtrim extends AbstractUnaryProperCharFunction  {
        //returns the singleton instance of this class
        private static Strtrim singleton = null;
        public static Strtrim getInstance(){
            if (singleton == null) singleton = new Strtrim();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrtrim(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strtrim";
        }
        
    }
    public static abstract class AbstractImproperCharFunction extends AbstractCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImproperCharFunction(this,arg);
        }
        
    }
    public static class Strfind extends AbstractImproperCharFunction  {
        //returns the singleton instance of this class
        private static Strfind singleton = null;
        public static Strfind getInstance(){
            if (singleton == null) singleton = new Strfind();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrfind(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strfind";
        }
        
    }
    public static class Findstr extends AbstractImproperCharFunction  {
        //returns the singleton instance of this class
        private static Findstr singleton = null;
        public static Findstr getInstance(){
            if (singleton == null) singleton = new Findstr();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFindstr(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "findstr";
        }
        
    }
    public static class Strrep extends AbstractImproperCharFunction  {
        //returns the singleton instance of this class
        private static Strrep singleton = null;
        public static Strrep getInstance(){
            if (singleton == null) singleton = new Strrep();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrrep(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strrep";
        }
        
    }
    public static abstract class AbstractStringCompare extends AbstractImproperCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStringCompare(this,arg);
        }
        
    }
    public static class Strcmp extends AbstractStringCompare  {
        //returns the singleton instance of this class
        private static Strcmp singleton = null;
        public static Strcmp getInstance(){
            if (singleton == null) singleton = new Strcmp();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrcmp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strcmp";
        }
        
    }
    public static class Strcmpi extends AbstractStringCompare  {
        //returns the singleton instance of this class
        private static Strcmpi singleton = null;
        public static Strcmpi getInstance(){
            if (singleton == null) singleton = new Strcmpi();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrcmpi(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strcmpi";
        }
        
    }
    public static class Strncmpi extends AbstractStringCompare  {
        //returns the singleton instance of this class
        private static Strncmpi singleton = null;
        public static Strncmpi getInstance(){
            if (singleton == null) singleton = new Strncmpi();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrncmpi(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strncmpi";
        }
        
    }
    public static class Strncmp extends AbstractStringCompare  {
        //returns the singleton instance of this class
        private static Strncmp singleton = null;
        public static Strncmp getInstance(){
            if (singleton == null) singleton = new Strncmp();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStrncmp(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "strncmp";
        }
        
    }
    public static abstract class AbstractRegexpFunction extends AbstractImproperCharFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRegexpFunction(this,arg);
        }
        
    }
    public static class Regexptranslate extends AbstractRegexpFunction  {
        //returns the singleton instance of this class
        private static Regexptranslate singleton = null;
        public static Regexptranslate getInstance(){
            if (singleton == null) singleton = new Regexptranslate();
            return singleton;
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
    public static class Regexp extends AbstractRegexpFunction  {
        //returns the singleton instance of this class
        private static Regexp singleton = null;
        public static Regexp getInstance(){
            if (singleton == null) singleton = new Regexp();
            return singleton;
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
    public static class Regexpi extends AbstractRegexpFunction  {
        //returns the singleton instance of this class
        private static Regexpi singleton = null;
        public static Regexpi getInstance(){
            if (singleton == null) singleton = new Regexpi();
            return singleton;
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
    public static class Regexprep extends AbstractRegexpFunction  {
        //returns the singleton instance of this class
        private static Regexprep singleton = null;
        public static Regexprep getInstance(){
            if (singleton == null) singleton = new Regexprep();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRegexprep(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "regexprep";
        }
        
    }
    public static abstract class AbstractVersatileQuery extends AbstractVersatileFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractVersatileQuery(this,arg);
        }
        
    }
    public static class Class extends AbstractVersatileQuery implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Class singleton = null;
        public static Class getInstance(){
            if (singleton == null) singleton = new Class();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseClass(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "class";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any -> char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractDoubleResultVersatileQuery extends AbstractVersatileQuery implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDoubleResultVersatileQuery(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$|M->$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Size extends AbstractDoubleResultVersatileQuery implements HasShapePropagationInfo, HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Size singleton = null;
        public static Size getInstance(){
            if (singleton == null) singleton = new Size();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSize(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "size";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("numOutput(1),$|M,k=previousShapeDim()->[1,k]||numOutput(2),[m,n]->$,$");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any (numeric|logical)? -> double double double double double double double double double double double double double double double");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractScalarDoubleResultVersatileQuery extends AbstractDoubleResultVersatileQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarDoubleResultVersatileQuery(this,arg);
        }
        
    }
    public static class Length extends AbstractScalarDoubleResultVersatileQuery implements HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Length singleton = null;
        public static Length getInstance(){
            if (singleton == null) singleton = new Length();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseLength(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "length";
        }
        
        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A -> R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Ndims extends AbstractScalarDoubleResultVersatileQuery  {
        //returns the singleton instance of this class
        private static Ndims singleton = null;
        public static Ndims getInstance(){
            if (singleton == null) singleton = new Ndims();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNdims(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ndims";
        }
        
    }
    public static class Numel extends AbstractScalarDoubleResultVersatileQuery implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Numel singleton = null;
        public static Numel getInstance(){
            if (singleton == null) singleton = new Numel();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNumel(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "numel";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any matrix* -> double");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class End extends AbstractScalarDoubleResultVersatileQuery implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static End singleton = null;
        public static End getInstance(){
            if (singleton == null) singleton = new End();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseEnd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "end";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any numeric numeric -> double");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractLogicalResultVersatileQuery extends AbstractVersatileQuery implements HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalResultVersatileQuery(this,arg);
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any->logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractScalarLogicalResultVersatileQuery extends AbstractLogicalResultVersatileQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarLogicalResultVersatileQuery(this,arg);
        }
        
    }
    public static abstract class AbstractClassQuery extends AbstractScalarLogicalResultVersatileQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractClassQuery(this,arg);
        }
        
    }
    public static class Isobject extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Isobject singleton = null;
        public static Isobject getInstance(){
            if (singleton == null) singleton = new Isobject();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsobject(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isobject";
        }
        
    }
    public static class Isfloat extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Isfloat singleton = null;
        public static Isfloat getInstance(){
            if (singleton == null) singleton = new Isfloat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsfloat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isfloat";
        }
        
    }
    public static class Isinteger extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Isinteger singleton = null;
        public static Isinteger getInstance(){
            if (singleton == null) singleton = new Isinteger();
            return singleton;
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
    public static class Islogical extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Islogical singleton = null;
        public static Islogical getInstance(){
            if (singleton == null) singleton = new Islogical();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIslogical(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "islogical";
        }
        
    }
    public static class Isstruct extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Isstruct singleton = null;
        public static Isstruct getInstance(){
            if (singleton == null) singleton = new Isstruct();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsstruct(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isstruct";
        }
        
    }
    public static class Ischar extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Ischar singleton = null;
        public static Ischar getInstance(){
            if (singleton == null) singleton = new Ischar();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIschar(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "ischar";
        }
        
    }
    public static class Iscell extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Iscell singleton = null;
        public static Iscell getInstance(){
            if (singleton == null) singleton = new Iscell();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIscell(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "iscell";
        }
        
    }
    public static class Isnumeric extends AbstractClassQuery  {
        //returns the singleton instance of this class
        private static Isnumeric singleton = null;
        public static Isnumeric getInstance(){
            if (singleton == null) singleton = new Isnumeric();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsnumeric(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isnumeric";
        }
        
    }
    public static class Isa extends AbstractClassQuery implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Isa singleton = null;
        public static Isa getInstance(){
            if (singleton == null) singleton = new Isa();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsa(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isa";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any char -> logical");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractScalarLogicalShapeQuery extends AbstractScalarLogicalResultVersatileQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarLogicalShapeQuery(this,arg);
        }
        
    }
    public static class Isempty extends AbstractScalarLogicalShapeQuery  {
        //returns the singleton instance of this class
        private static Isempty singleton = null;
        public static Isempty getInstance(){
            if (singleton == null) singleton = new Isempty();
            return singleton;
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
    public static class Isvector extends AbstractScalarLogicalShapeQuery  {
        //returns the singleton instance of this class
        private static Isvector singleton = null;
        public static Isvector getInstance(){
            if (singleton == null) singleton = new Isvector();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsvector(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isvector";
        }
        
    }
    public static class Isscalar extends AbstractScalarLogicalShapeQuery  {
        //returns the singleton instance of this class
        private static Isscalar singleton = null;
        public static Isscalar getInstance(){
            if (singleton == null) singleton = new Isscalar();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsscalar(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isscalar";
        }
        
    }
    public static abstract class AbstractMultiaryToScalarLogicalVersatileQuery extends AbstractVersatileQuery implements HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMultiaryToScalarLogicalVersatileQuery(this,arg);
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any any any*->logical");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Isequalwithequalnans extends AbstractMultiaryToScalarLogicalVersatileQuery  {
        //returns the singleton instance of this class
        private static Isequalwithequalnans singleton = null;
        public static Isequalwithequalnans getInstance(){
            if (singleton == null) singleton = new Isequalwithequalnans();
            return singleton;
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
    public static class Isequal extends AbstractMultiaryToScalarLogicalVersatileQuery  {
        //returns the singleton instance of this class
        private static Isequal singleton = null;
        public static Isequal getInstance(){
            if (singleton == null) singleton = new Isequal();
            return singleton;
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
    public static abstract class AbstractVersatileConversion extends AbstractVersatileFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractVersatileConversion(this,arg);
        }
        
    }
    public static abstract class AbstractShapeTransformation extends AbstractVersatileConversion  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractShapeTransformation(this,arg);
        }
        
    }
    public static class Reshape extends AbstractShapeTransformation implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Reshape singleton = null;
        public static Reshape getInstance(){
            if (singleton == null) singleton = new Reshape();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseReshape(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "reshape";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any matrix matrix* -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Permute extends AbstractShapeTransformation implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Permute singleton = null;
        public static Permute getInstance(){
            if (singleton == null) singleton = new Permute();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePermute(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "permute";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any double -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static abstract class AbstractUnaryShapeTransformation extends AbstractShapeTransformation implements HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryShapeTransformation(this,arg);
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Squeeze extends AbstractUnaryShapeTransformation  {
        //returns the singleton instance of this class
        private static Squeeze singleton = null;
        public static Squeeze getInstance(){
            if (singleton == null) singleton = new Squeeze();
            return singleton;
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
    public static class Transpose extends AbstractUnaryShapeTransformation implements HasShapePropagationInfo, HasisComplexPropagationInfo {
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
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[m,n]->[n,m]");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("R -> R || X->X || A->A ");
            }
            return isComplexPropInfo;
        }

    }
    public static class Ctranspose extends AbstractUnaryShapeTransformation  {
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
        
    }
    public static abstract class AbstractConcatenation extends AbstractVersatileFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConcatenation(this,arg);
        }
        
    }
    public static class Horzcat extends AbstractConcatenation implements HasShapePropagationInfo, HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Horzcat singleton = null;
        public static Horzcat getInstance(){
            if (singleton == null) singleton = new Horzcat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseHorzcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "horzcat";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->[0,0]||M,n=previousShapeDim(2),K=copy(M),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=add(k))*,K(2)=n->K||$,n=previousShapeDim(2),K=copy($),K(2)=0,(#,k=previousShapeDim(2),N=copy(#),N(2)=0,isequal(K,N),n=add(k))*,K(2)=n->K");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A,A+ -> NUMXARGS>0?X:R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Vertcat extends AbstractConcatenation implements HasShapePropagationInfo, HasisComplexPropagationInfo {
        //returns the singleton instance of this class
        private static Vertcat singleton = null;
        public static Vertcat getInstance(){
            if (singleton == null) singleton = new Vertcat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseVertcat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "vertcat";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->[0,0]||M,n=previousShapeDim(1),K=copy(M),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=add(k))*,K(1)=n->K||$,n=previousShapeDim(1),K=copy($),K(1)=0,(#,k=previousShapeDim(1),N=copy(#),N(1)=0,isequal(K,N),n=add(k))*,K(1)=n->K");
            }
            return shapePropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A,A+ -> NUMXARGS>0?X:R");
            }
            return isComplexPropInfo;
        }

    }
    public static class Cat extends AbstractConcatenation  {
        //returns the singleton instance of this class
        private static Cat singleton = null;
        public static Cat getInstance(){
            if (singleton == null) singleton = new Cat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cat";
        }
        
    }
    public static abstract class AbstractIndexing extends AbstractVersatileFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractIndexing(this,arg);
        }
        
    }
    public static class Subsasgn extends AbstractIndexing  {
        //returns the singleton instance of this class
        private static Subsasgn singleton = null;
        public static Subsasgn getInstance(){
            if (singleton == null) singleton = new Subsasgn();
            return singleton;
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
    public static class Subsref extends AbstractIndexing  {
        //returns the singleton instance of this class
        private static Subsref singleton = null;
        public static Subsref getInstance(){
            if (singleton == null) singleton = new Subsref();
            return singleton;
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
    public static abstract class AbstractMapOperator extends AbstractVersatileFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMapOperator(this,arg);
        }
        
    }
    public static class Structfun extends AbstractMapOperator  {
        //returns the singleton instance of this class
        private static Structfun singleton = null;
        public static Structfun getInstance(){
            if (singleton == null) singleton = new Structfun();
            return singleton;
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
    public static class Arrayfun extends AbstractMapOperator  {
        //returns the singleton instance of this class
        private static Arrayfun singleton = null;
        public static Arrayfun getInstance(){
            if (singleton == null) singleton = new Arrayfun();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseArrayfun(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "arrayfun";
        }
        
    }
    public static class Cellfun extends AbstractMapOperator  {
        //returns the singleton instance of this class
        private static Cellfun singleton = null;
        public static Cellfun getInstance(){
            if (singleton == null) singleton = new Cellfun();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCellfun(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cellfun";
        }
        
    }
    public static abstract class AbstractImpureFunction extends AbstractRoot  {
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
    public static class Superiorfloat extends AbstractImpureFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Superiorfloat singleton = null;
        public static Superiorfloat getInstance(){
            if (singleton == null) singleton = new Superiorfloat();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSuperiorfloat(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "superiorfloat";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("float* -> char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static class Clock extends AbstractTimeFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
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
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->[1,6]");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("none -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static class Nargin extends AbstractMatlabEnvironmentFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Nargin singleton = null;
        public static Nargin getInstance(){
            if (singleton == null) singleton = new Nargin();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargin(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargin";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("none|char|function_handle->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Nargout extends AbstractMatlabEnvironmentFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Nargout singleton = null;
        public static Nargout getInstance(){
            if (singleton == null) singleton = new Nargout();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNargout(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nargout";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("none|char|function_handle->double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Methods extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Methods singleton = null;
        public static Methods getInstance(){
            if (singleton == null) singleton = new Methods();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMethods(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "methods";
        }
        
    }
    public static class Fieldnames extends AbstractMatlabEnvironmentFunction  {
        //returns the singleton instance of this class
        private static Fieldnames singleton = null;
        public static Fieldnames getInstance(){
            if (singleton == null) singleton = new Fieldnames();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFieldnames(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fieldnames";
        }
        
    }
    public static abstract class AbstractReportFunction extends AbstractImpureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractReportFunction(this,arg);
        }
        
    }
    public static class Disp extends AbstractReportFunction implements HasShapePropagationInfo, HasClassPropagationInfo, HasisComplexPropagationInfo {
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
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("#->[]");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any -> none");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

        private ICNode isComplexPropInfo = null;
        public ICNode getisComplexPropagationInfo(){
            //set isComplexPropInfo if not defined
            if (isComplexPropInfo == null){
                isComplexPropInfo = isComplexInfoPropTool.parse("A->X");
            }
            return isComplexPropInfo;
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
    public static class Error extends AbstractReportFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("char? char? any*->error");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Warning extends AbstractReportFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("any any* -> char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static class Message extends AbstractReportFunction implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Message singleton = null;
        public static Message getInstance(){
            if (singleton == null) singleton = new Message();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMessage(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "message";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("char any* -> char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static abstract class AbstractRandomFunction extends AbstractImpureFunction implements HasShapePropagationInfo, HasClassPropagationInfo {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRandomFunction(this,arg);
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("[]->$||($,n=previousScalar(),add())+->M");
            }
            return shapePropInfo;
        }

        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("(numeric|logical)* typeString(float)|(none->double)");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static class Randi extends AbstractRandomFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("numeric|logical (numeric|logical)* typeString(numeric)|(none->double)");
                classPropInfo.setVar("parent",super.getClassPropagationInfo());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Randperm extends AbstractRandomFunction implements HasShapePropagationInfo {
        //returns the singleton instance of this class
        private static Randperm singleton = null;
        public static Randperm getInstance(){
            if (singleton == null) singleton = new Randperm();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRandperm(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "randperm";
        }
        
        private SPNode shapePropInfo = null;
        public SPNode getShapePropagationInfo(){
            //set shapePropInfo if not defined
            if (shapePropInfo == null){
                shapePropInfo = ShapePropTool.parse("$,n=previousScalar()->[1,n]");
            }
            return shapePropInfo;
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
    public static class Sprintf extends AbstractPosixIoFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("char any* -> char");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static class Fprintf extends AbstractPosixIoFunction implements HasClassPropagationInfo {
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
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("double? char any* -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
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
    public static abstract class AbstractNotABuiltin extends AbstractRoot  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNotABuiltin(this,arg);
        }
        
    }
    public static class Imwrite extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Imwrite singleton = null;
        public static Imwrite getInstance(){
            if (singleton == null) singleton = new Imwrite();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseImwrite(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "imwrite";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("numeric numeric? char matrix*");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Sparse extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Sparse singleton = null;
        public static Sparse getInstance(){
            if (singleton == null) singleton = new Sparse();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSparse(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sparse";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("(double|char|logical) (double|char|logical)* -> 0");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Realmax extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Realmax singleton = null;
        public static Realmax getInstance(){
            if (singleton == null) singleton = new Realmax();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseRealmax(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "realmax";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("typeString(float)|(none->double)");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Histc extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Histc singleton = null;
        public static Histc getInstance(){
            if (singleton == null) singleton = new Histc();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseHistc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "histc";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("numeric numeric numeric? -> double double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Blkdiag extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Blkdiag singleton = null;
        public static Blkdiag getInstance(){
            if (singleton == null) singleton = new Blkdiag();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseBlkdiag(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "blkdiag";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("numeric* -> double");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Var extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Var singleton = null;
        public static Var getInstance(){
            if (singleton == null) singleton = new Var();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseVar(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "var";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(char|logical -> double, float ->0) numeric? numeric?");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
    public static class Std extends AbstractNotABuiltin implements HasClassPropagationInfo {
        //returns the singleton instance of this class
        private static Std singleton = null;
        public static Std getInstance(){
            if (singleton == null) singleton = new Std();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStd(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "std";
        }
        
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}

        private CP classPropInfo = null;
        public CP getClassPropagationInfo(){
            //set classPropInfo if not defined
            if (classPropInfo == null){
                classPropInfo = ClassPropTool.parse("coerce(char|logical -> double, float ->0) numeric? numeric?");
                classPropInfo.setVar("parent",new CPNone());
                classPropInfo.setVar("matlab",getMatlabClassPropagationInfo());
            }
            return classPropInfo;
        }

    }
}