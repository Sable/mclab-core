// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //


package natlab.Static.builtin;

import java.util.HashMap;
import natlab.toolkits.path.BuiltinQuery;


public abstract class Builtin {
    private static HashMap<String, Builtin> builtinMap = new HashMap<String, Builtin>();
    public static void main(String[] args) {
        java.lang.System.out.println(create("i"));
        Builtin b = builtinMap.get("i");
        java.lang.System.out.println(b+"  "+b.create());
        java.lang.System.out.println("number of builtins "+builtinMap.size());
        java.lang.System.out.println(builtinMap);
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
        builtinMap.put("colon",new Colon());
        builtinMap.put("horzcat",new Horzcat());
        builtinMap.put("vertcat",new Vertcat());
        builtinMap.put("cat",new Cat());
        builtinMap.put("nargin",new Nargin());
        builtinMap.put("nargout",new Nargout());
        builtinMap.put("mfilename",new Mfilename());
        builtinMap.put("end",new End());
        builtinMap.put("isequalwithequalnans",new Isequalwithequalnans());
        builtinMap.put("isequal",new Isequal());
        builtinMap.put("subsasgn",new Subsasgn());
        builtinMap.put("subsref",new Subsref());
        builtinMap.put("histc",new Histc());
        builtinMap.put("structfun",new Structfun());
        builtinMap.put("arrayfun",new Arrayfun());
        builtinMap.put("cellfun",new Cellfun());
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
        builtinMap.put("rem",new Rem());
        builtinMap.put("hypot",new Hypot());
        builtinMap.put("uplus",new Uplus());
        builtinMap.put("uminus",new Uminus());
        builtinMap.put("conj",new Conj());
        builtinMap.put("real",new Real());
        builtinMap.put("imag",new Imag());
        builtinMap.put("abs",new Abs());
        builtinMap.put("transpose",new Transpose());
        builtinMap.put("ctranspose",new Ctranspose());
        builtinMap.put("expm",new Expm());
        builtinMap.put("sqrtm",new Sqrtm());
        builtinMap.put("logm",new Logm());
        builtinMap.put("not",new Not());
        builtinMap.put("sqrt",new Sqrt());
        builtinMap.put("realsqrt",new Realsqrt());
        builtinMap.put("erf",new Erf());
        builtinMap.put("erfinv",new Erfinv());
        builtinMap.put("erfc",new Erfc());
        builtinMap.put("erfcinv",new Erfcinv());
        builtinMap.put("gamma",new Gamma());
        builtinMap.put("gammainc",new Gammainc());
        builtinMap.put("betainc",new Betainc());
        builtinMap.put("gammaln",new Gammaln());
        builtinMap.put("exp",new Exp());
        builtinMap.put("log",new Log());
        builtinMap.put("log2",new Log2());
        builtinMap.put("log10",new Log10());
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
        builtinMap.put("inv",new Inv());
        builtinMap.put("eig",new Eig());
        builtinMap.put("norm",new Norm());
        builtinMap.put("rank",new Rank());
        builtinMap.put("det",new Det());
        builtinMap.put("dot",new Dot());
        builtinMap.put("cross",new Cross());
        builtinMap.put("linsolve",new Linsolve());
        builtinMap.put("rcond",new Rcond());
        builtinMap.put("tril",new Tril());
        builtinMap.put("triu",new Triu());
        builtinMap.put("eps",new Eps());
        builtinMap.put("ifftn",new Ifftn());
        builtinMap.put("fftn",new Fftn());
        builtinMap.put("fft",new Fft());
        builtinMap.put("schur",new Schur());
        builtinMap.put("ordschur",new Ordschur());
        builtinMap.put("lu",new Lu());
        builtinMap.put("chol",new Chol());
        builtinMap.put("qr",new Qr());
        builtinMap.put("svd",new Svd());
        builtinMap.put("bitand",new Bitand());
        builtinMap.put("bitor",new Bitor());
        builtinMap.put("bitxor",new Bitxor());
        builtinMap.put("bitcmp",new Bitcmp());
        builtinMap.put("bitget",new Bitget());
        builtinMap.put("bitshift",new Bitshift());
        builtinMap.put("bitset",new Bitset());
        builtinMap.put("strncmpi",new Strncmpi());
        builtinMap.put("strcmp",new Strcmp());
        builtinMap.put("strcmpi",new Strcmpi());
        builtinMap.put("strtrim",new Strtrim());
        builtinMap.put("strfind",new Strfind());
        builtinMap.put("findstr",new Findstr());
        builtinMap.put("strrep",new Strrep());
        builtinMap.put("upper",new Upper());
        builtinMap.put("lower",new Lower());
        builtinMap.put("deblank",new Deblank());
        builtinMap.put("regexptranslate",new Regexptranslate());
        builtinMap.put("regexp",new Regexp());
        builtinMap.put("regexpi",new Regexpi());
        builtinMap.put("tegexprep",new Tegexprep());
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
        builtinMap.put("cell2struct",new Cell2struct());
        builtinMap.put("struct2cell",new Struct2cell());
        builtinMap.put("typecast",new Typecast());
        builtinMap.put("cast",new Cast());
        builtinMap.put("isfield",new Isfield());
        builtinMap.put("class",new Class());
        builtinMap.put("methodnames",new Methodnames());
        builtinMap.put("fieldnames",new Fieldnames());
        builtinMap.put("isempty",new Isempty());
        builtinMap.put("isobject",new Isobject());
        builtinMap.put("isfloat",new Isfloat());
        builtinMap.put("isinteger",new Isinteger());
        builtinMap.put("islogical",new Islogical());
        builtinMap.put("isstruct",new Isstruct());
        builtinMap.put("ischar",new Ischar());
        builtinMap.put("iscell",new Iscell());
        builtinMap.put("isa",new Isa());
        builtinMap.put("sort",new Sort());
        builtinMap.put("unique",new Unique());
        builtinMap.put("find",new Find());
        builtinMap.put("diag",new Diag());
        builtinMap.put("reshape",new Reshape());
        builtinMap.put("permute",new Permute());
        builtinMap.put("squeeze",new Squeeze());
        builtinMap.put("complex",new Complex());
        builtinMap.put("prod",new Prod());
        builtinMap.put("sum",new Sum());
        builtinMap.put("mean",new Mean());
        builtinMap.put("min",new Min());
        builtinMap.put("max",new Max());
        builtinMap.put("ones",new Ones());
        builtinMap.put("zeros",new Zeros());
        builtinMap.put("eye",new Eye());
        builtinMap.put("inf",new Inf());
        builtinMap.put("nan",new Nan());
        builtinMap.put("true",new True());
        builtinMap.put("false",new False());
        builtinMap.put("size",new Size());
        builtinMap.put("nonzeros",new Nonzeros());
        builtinMap.put("cumprod",new Cumprod());
        builtinMap.put("cumsum",new Cumsum());
        builtinMap.put("sign",new Sign());
        builtinMap.put("length",new Length());
        builtinMap.put("ndims",new Ndims());
        builtinMap.put("numel",new Numel());
        builtinMap.put("nnz",new Nnz());
        builtinMap.put("any",new Any());
        builtinMap.put("all",new All());
        builtinMap.put("isemtpy",new Isemtpy());
        builtinMap.put("isnan",new Isnan());
        builtinMap.put("isinf",new Isinf());
        builtinMap.put("isfinite",new Isfinite());
        builtinMap.put("isvector",new Isvector());
        builtinMap.put("isscalar",new Isscalar());
        builtinMap.put("isreal",new Isreal());
        builtinMap.put("isnumeric",new Isnumeric());
        builtinMap.put("superiorto",new Superiorto());
        builtinMap.put("exit",new Exit());
        builtinMap.put("quit",new Quit());
        builtinMap.put("clock",new Clock());
        builtinMap.put("tic",new Tic());
        builtinMap.put("toc",new Toc());
        builtinMap.put("cputime",new Cputime());
        builtinMap.put("assert",new Assert());
        builtinMap.put("nargoutchk",new Nargoutchk());
        builtinMap.put("nargchk",new Nargchk());
        builtinMap.put("str2func",new Str2func());
        builtinMap.put("pause",new Pause());
        builtinMap.put("eval",new Eval());
        builtinMap.put("evalin",new Evalin());
        builtinMap.put("feval",new Feval());
        builtinMap.put("assignin",new Assignin());
        builtinMap.put("inputname",new Inputname());
        builtinMap.put("import",new Import());
        builtinMap.put("cd",new Cd());
        builtinMap.put("exist",new Exist());
        builtinMap.put("matlabroot",new Matlabroot());
        builtinMap.put("whos",new Whos());
        builtinMap.put("which",new Which());
        builtinMap.put("version",new Version());
        builtinMap.put("clear",new Clear());
        builtinMap.put("disp",new Disp());
        builtinMap.put("display",new Display());
        builtinMap.put("clc",new Clc());
        builtinMap.put("error",new Error());
        builtinMap.put("warning",new Warning());
        builtinMap.put("echo",new Echo());
        builtinMap.put("diary",new Diary());
        builtinMap.put("lastwarn",new Lastwarn());
        builtinMap.put("lasterror",new Lasterror());
        builtinMap.put("format",new Format());
        builtinMap.put("rand",new Rand());
        builtinMap.put("randi",new Randi());
        builtinMap.put("randn",new Randn());
        builtinMap.put("computer",new Computer());
        builtinMap.put("beep",new Beep());
        builtinMap.put("dir",new Dir());
        builtinMap.put("unix",new Unix());
        builtinMap.put("dos",new Dos());
        builtinMap.put("system",new System());
        builtinMap.put("load",new Load());
        builtinMap.put("save",new Save());
        builtinMap.put("input",new Input());
        builtinMap.put("textscan",new Textscan());
        builtinMap.put("sprintf",new Sprintf());
        builtinMap.put("sscanf",new Sscanf());
        builtinMap.put("fprintf",new Fprintf());
        builtinMap.put("ftell",new Ftell());
        builtinMap.put("ferror",new Ferror());
        builtinMap.put("fopen",new Fopen());
        builtinMap.put("fread",new Fread());
        builtinMap.put("frewind",new Frewind());
        builtinMap.put("fscanf",new Fscanf());
        builtinMap.put("fseek",new Fseek());
        builtinMap.put("fwrite",new Fwrite());
        builtinMap.put("fgetl",new Fgetl());
        builtinMap.put("fgets",new Fgets());
        builtinMap.put("fclose",new Fclose());
    }    
    
    //the actual Builtin Classes:
    
    public static abstract class AbstractPureFunction extends Builtin  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPureFunction(this,arg);
        }
        
    }
    public static abstract class AbstractConstant extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstant(this,arg);
        }
        
    }
    public static abstract class AbstractNumericalConstant extends AbstractConstant implements ArityDefined {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalConstant(this,arg);
        }
        
        public int getMaxNumOfArgs(){ return 0; }
        public int getMinNumOfArgs(){ return 0; }
        public boolean isVariadic(){ return 0==0; }

    }
    public static class I extends AbstractNumericalConstant  {
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
    public static class J extends AbstractNumericalConstant  {
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
    public static class Pi extends AbstractNumericalConstant  {
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
    public static abstract class AbstractOperator extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractOperator(this,arg);
        }
        
    }
    public static class Colon extends AbstractOperator  {
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
    public static class Horzcat extends AbstractOperator  {
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
    public static class Vertcat extends AbstractOperator  {
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
    public static class Cat extends AbstractOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cat();
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
    public static class Nargin extends AbstractOperator  {
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
    public static class Nargout extends AbstractOperator  {
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
    public static class Mfilename extends AbstractOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Mfilename();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMfilename(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "mfilename";
        }
        
    }
    public static class End extends AbstractOperator  {
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
    public static class Isequalwithequalnans extends AbstractOperator  {
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
    public static class Isequal extends AbstractOperator  {
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
    public static class Subsasgn extends AbstractOperator  {
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
    public static class Subsref extends AbstractOperator  {
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
    public static class Histc extends AbstractOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Histc();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseHistc(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "histc";
        }
        
    }
    public static abstract class AbstractMapOperator extends AbstractOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMapOperator(this,arg);
        }
        
    }
    public static class Structfun extends AbstractMapOperator  {
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
    public static class Arrayfun extends AbstractMapOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Arrayfun();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Cellfun();
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
    public static abstract class AbstractBinaryOperator extends AbstractOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryOperator(this,arg);
        }
        
    }
    public static abstract class AbstractRelationalOperator extends AbstractBinaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRelationalOperator(this,arg);
        }
        
    }
    public static class Eq extends AbstractRelationalOperator  {
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
    public static class Ne extends AbstractRelationalOperator  {
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
    public static class Lt extends AbstractRelationalOperator  {
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
    public static class Le extends AbstractRelationalOperator  {
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
    public static class Ge extends AbstractRelationalOperator  {
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
    public static class Gt extends AbstractRelationalOperator  {
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
    public static abstract class AbstractBinaryLogicalOperator extends AbstractBinaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBinaryLogicalOperator(this,arg);
        }
        
    }
    public static class And extends AbstractBinaryLogicalOperator  {
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
    public static class Or extends AbstractBinaryLogicalOperator  {
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
    public static class Xor extends AbstractBinaryLogicalOperator  {
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
    public static abstract class AbstractNumericalBinaryOperator extends AbstractBinaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalBinaryOperator(this,arg);
        }
        
    }
    public static abstract class AbstractMatrixBinaryOperator extends AbstractNumericalBinaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixBinaryOperator(this,arg);
        }
        
    }
    public static class Plus extends AbstractMatrixBinaryOperator  {
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
    public static class Minus extends AbstractMatrixBinaryOperator  {
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
    public static class Mtimes extends AbstractMatrixBinaryOperator  {
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
    public static class Mpower extends AbstractMatrixBinaryOperator  {
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
    public static class Mldivide extends AbstractMatrixBinaryOperator  {
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
    public static class Mrdivide extends AbstractMatrixBinaryOperator  {
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
    public static abstract class AbstractElementwiseBinaryOperator extends AbstractNumericalBinaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementwiseBinaryOperator(this,arg);
        }
        
    }
    public static class Times extends AbstractElementwiseBinaryOperator  {
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
    public static class Ldivide extends AbstractElementwiseBinaryOperator  {
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
    public static class Rdivide extends AbstractElementwiseBinaryOperator  {
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
    public static class Power extends AbstractElementwiseBinaryOperator  {
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
    public static class Pow2 extends AbstractElementwiseBinaryOperator  {
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
    public static class Mod extends AbstractElementwiseBinaryOperator  {
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
    public static class Rem extends AbstractElementwiseBinaryOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Rem();
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
    public static class Hypot extends AbstractElementwiseBinaryOperator  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Hypot();
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
    public static abstract class AbstractUnaryOperator extends AbstractOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractUnaryOperator(this,arg);
        }
        
    }
    public static abstract class AbstractNumericalUnaryOperator extends AbstractUnaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalUnaryOperator(this,arg);
        }
        
    }
    public static abstract class AbstractElementwiseUnaryOperator extends AbstractNumericalUnaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementwiseUnaryOperator(this,arg);
        }
        
    }
    public static class Uplus extends AbstractElementwiseUnaryOperator  {
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
    public static class Uminus extends AbstractElementwiseUnaryOperator  {
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
    public static class Conj extends AbstractElementwiseUnaryOperator  {
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
    public static class Real extends AbstractElementwiseUnaryOperator  {
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
    public static class Imag extends AbstractElementwiseUnaryOperator  {
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
    public static class Abs extends AbstractElementwiseUnaryOperator  {
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
    public static abstract class AbstractMatrixUnaryOperator extends AbstractNumericalUnaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixUnaryOperator(this,arg);
        }
        
    }
    public static class Transpose extends AbstractMatrixUnaryOperator  {
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
    public static class Ctranspose extends AbstractMatrixUnaryOperator  {
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
    public static class Expm extends AbstractMatrixUnaryOperator  {
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
    public static class Sqrtm extends AbstractMatrixUnaryOperator  {
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
    public static class Logm extends AbstractMatrixUnaryOperator  {
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
    public static abstract class AbstractLogicalUnaryOperator extends AbstractUnaryOperator  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalUnaryOperator(this,arg);
        }
        
    }
    public static class Not extends AbstractLogicalUnaryOperator  {
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
    public static abstract class AbstractMatrixOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixOperation(this,arg);
        }
        
    }
    public static abstract class AbstractElementwiseMatrixOperation extends AbstractMatrixOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractElementwiseMatrixOperation(this,arg);
        }
        
    }
    public static class Sqrt extends AbstractElementwiseMatrixOperation  {
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
    public static class Realsqrt extends AbstractElementwiseMatrixOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Realsqrt();
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
    public static class Erf extends AbstractElementwiseMatrixOperation  {
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
    public static class Erfinv extends AbstractElementwiseMatrixOperation  {
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
    public static class Erfc extends AbstractElementwiseMatrixOperation  {
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
    public static class Erfcinv extends AbstractElementwiseMatrixOperation  {
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
    public static class Gamma extends AbstractElementwiseMatrixOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Gamma();
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
    public static class Gammainc extends AbstractElementwiseMatrixOperation  {
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
    public static class Betainc extends AbstractElementwiseMatrixOperation  {
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
    public static class Gammaln extends AbstractElementwiseMatrixOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Gammaln();
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
    public static class Exp extends AbstractElementwiseMatrixOperation  {
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
    public static class Log extends AbstractElementwiseMatrixOperation  {
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
    public static class Log2 extends AbstractElementwiseMatrixOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Log2();
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
    public static class Log10 extends AbstractElementwiseMatrixOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Log10();
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
    public static abstract class AbstractForwardTrigonometricFunction extends AbstractElementwiseMatrixOperation  {
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
    public static class Cos extends AbstractTrigonometricFunction  {
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
    public static class Tan extends AbstractTrigonometricFunction  {
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
    public static class Cot extends AbstractTrigonometricFunction  {
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
    public static class Sec extends AbstractTrigonometricFunction  {
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
    public static class Csc extends AbstractTrigonometricFunction  {
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
    public static abstract class AbstractDecimalTrigonometricFunction extends AbstractForwardTrigonometricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sind extends AbstractDecimalTrigonometricFunction  {
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
    public static class Cosd extends AbstractDecimalTrigonometricFunction  {
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
    public static class Tand extends AbstractDecimalTrigonometricFunction  {
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
    public static class Cotd extends AbstractDecimalTrigonometricFunction  {
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
    public static class Secd extends AbstractDecimalTrigonometricFunction  {
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
    public static class Cscd extends AbstractDecimalTrigonometricFunction  {
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
    public static abstract class AbstractHyperbolicTrigonometricFunction extends AbstractElementwiseMatrixOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicTrigonometricFunction(this,arg);
        }
        
    }
    public static class Sinh extends AbstractHyperbolicTrigonometricFunction  {
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
    public static class Cosh extends AbstractHyperbolicTrigonometricFunction  {
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
    public static class Tanh extends AbstractHyperbolicTrigonometricFunction  {
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
    public static class Coth extends AbstractHyperbolicTrigonometricFunction  {
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
    public static class Sech extends AbstractHyperbolicTrigonometricFunction  {
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
    public static class Csch extends AbstractHyperbolicTrigonometricFunction  {
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
    public static abstract class AbstractInverseTrigonmetricFunction extends AbstractElementwiseMatrixOperation  {
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
    public static class Acos extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static class Atan extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static class Atan2 extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static class Acot extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static class Asec extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static class Acsc extends AbstractStandardInverseTrigonmetricFunction  {
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
    public static abstract class AbstractDecimalInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDecimalInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asind extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static class Acosd extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static class Atand extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static class Acotd extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static class Asecd extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static class Acscd extends AbstractDecimalInverseTrigonmetricFunction  {
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
    public static abstract class AbstractHyperbolicInverseTrigonmetricFunction extends AbstractInverseTrigonmetricFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractHyperbolicInverseTrigonmetricFunction(this,arg);
        }
        
    }
    public static class Asinh extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static class Acosh extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static class Atanh extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static class Acoth extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static class Asech extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static class Acsch extends AbstractHyperbolicInverseTrigonmetricFunction  {
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
    public static abstract class AbstractRoundingOperation extends AbstractElementwiseMatrixOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRoundingOperation(this,arg);
        }
        
    }
    public static class Fix extends AbstractRoundingOperation  {
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
    public static class Round extends AbstractRoundingOperation  {
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
    public static class Floor extends AbstractRoundingOperation  {
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
    public static class Ceil extends AbstractRoundingOperation  {
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
    public static abstract class AbstractMatrixComputation extends AbstractMatrixOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractMatrixComputation(this,arg);
        }
        
    }
    public static class Inv extends AbstractMatrixComputation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Inv();
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
    public static class Eig extends AbstractMatrixComputation  {
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
    public static class Norm extends AbstractMatrixComputation  {
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
    public static class Rank extends AbstractMatrixComputation  {
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
    public static class Det extends AbstractMatrixComputation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Det();
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
    public static class Dot extends AbstractMatrixComputation  {
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
    public static class Cross extends AbstractMatrixComputation  {
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
    public static class Linsolve extends AbstractMatrixComputation  {
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
    public static class Rcond extends AbstractMatrixComputation  {
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
    public static class Tril extends AbstractMatrixComputation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tril();
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
    public static class Triu extends AbstractMatrixComputation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Triu();
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
    public static class Eps extends AbstractMatrixComputation  {
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
    public static abstract class AbstractFourierFunction extends AbstractMatrixComputation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFourierFunction(this,arg);
        }
        
    }
    public static class Ifftn extends AbstractFourierFunction  {
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
    public static class Fftn extends AbstractFourierFunction  {
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
    public static class Fft extends AbstractFourierFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Fft();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseFft(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "fft";
        }
        
    }
    public static abstract class AbstractFactorization extends AbstractMatrixComputation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFactorization(this,arg);
        }
        
    }
    public static class Schur extends AbstractFactorization  {
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
    public static class Ordschur extends AbstractFactorization  {
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
    public static class Lu extends AbstractFactorization  {
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
    public static class Chol extends AbstractFactorization  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Chol();
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
    public static class Qr extends AbstractFactorization  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Qr();
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
    public static class Svd extends AbstractFactorization  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Svd();
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
    public static abstract class AbstractBitOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractBitOperation(this,arg);
        }
        
    }
    public static class Bitand extends AbstractBitOperation  {
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
    public static class Bitor extends AbstractBitOperation  {
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
    public static class Bitxor extends AbstractBitOperation  {
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
    public static class Bitcmp extends AbstractBitOperation  {
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
    public static class Bitget extends AbstractBitOperation  {
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
    public static class Bitshift extends AbstractBitOperation  {
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
    public static class Bitset extends AbstractBitOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Bitset();
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
    public static abstract class AbstractStringOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStringOperation(this,arg);
        }
        
    }
    public static class Strncmpi extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strncmpi();
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
    public static class Strcmp extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strcmp();
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
    public static class Strcmpi extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strcmpi();
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
    public static class Strtrim extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strtrim();
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
    public static class Strfind extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strfind();
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
    public static class Findstr extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Findstr();
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
    public static class Strrep extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Strrep();
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
    public static class Upper extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Upper();
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
    public static class Lower extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Lower();
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
    public static class Deblank extends AbstractStringOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Deblank();
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
    public static abstract class AbstractRegexpOperation extends AbstractStringOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractRegexpOperation(this,arg);
        }
        
    }
    public static class Regexptranslate extends AbstractRegexpOperation  {
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
    public static class Regexp extends AbstractRegexpOperation  {
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
    public static class Regexpi extends AbstractRegexpOperation  {
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
    public static class Tegexprep extends AbstractRegexpOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Tegexprep();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTegexprep(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "tegexprep";
        }
        
    }
    public static abstract class AbstractConstructor extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConstructor(this,arg);
        }
        
    }
    public static abstract class AbstractPrimitiveConstructor extends AbstractConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractPrimitiveConstructor(this,arg);
        }
        
    }
    public static class Double extends AbstractPrimitiveConstructor  {
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
    public static class Single extends AbstractPrimitiveConstructor  {
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
    public static class Char extends AbstractPrimitiveConstructor  {
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
    public static class Logical extends AbstractPrimitiveConstructor  {
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
    public static class Int8 extends AbstractPrimitiveConstructor  {
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
    public static class Int16 extends AbstractPrimitiveConstructor  {
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
    public static class Int32 extends AbstractPrimitiveConstructor  {
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
    public static class Int64 extends AbstractPrimitiveConstructor  {
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
    public static class Uint8 extends AbstractPrimitiveConstructor  {
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
    public static class Uint16 extends AbstractPrimitiveConstructor  {
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
    public static class Uint32 extends AbstractPrimitiveConstructor  {
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
    public static class Uint64 extends AbstractPrimitiveConstructor  {
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
    public static abstract class AbstractCompoundConstructor extends AbstractConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractCompoundConstructor(this,arg);
        }
        
    }
    public static class Cell extends AbstractCompoundConstructor  {
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
    public static class Struct extends AbstractCompoundConstructor  {
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
    public static abstract class AbstractConversionFunction extends AbstractConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractConversionFunction(this,arg);
        }
        
    }
    public static class Cell2struct extends AbstractConversionFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cell2struct();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseCell2struct(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "cell2struct";
        }
        
    }
    public static class Struct2cell extends AbstractConversionFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Struct2cell();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseStruct2cell(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "struct2cell";
        }
        
    }
    public static class Typecast extends AbstractConversionFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Typecast();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseTypecast(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "typecast";
        }
        
    }
    public static class Cast extends AbstractConversionFunction  {
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
    public static abstract class AbstractStructOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractStructOperation(this,arg);
        }
        
    }
    public static class Isfield extends AbstractStructOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isfield();
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
    public static abstract class AbstractClassOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractClassOperation(this,arg);
        }
        
    }
    public static class Class extends AbstractClassOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Class();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseClass(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "class";
        }
        
    }
    public static abstract class AbstractClassQueryOperation extends AbstractClassOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractClassQueryOperation(this,arg);
        }
        
    }
    public static class Methodnames extends AbstractClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Methodnames();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseMethodnames(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "methodnames";
        }
        
    }
    public static class Fieldnames extends AbstractClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Fieldnames();
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
    public static abstract class AbstractLogicalClassQueryOperation extends AbstractClassQueryOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalClassQueryOperation(this,arg);
        }
        
    }
    public static class Isempty extends AbstractLogicalClassQueryOperation  {
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
    public static class Isobject extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isobject();
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
    public static class Isfloat extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isfloat();
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
    public static class Isinteger extends AbstractLogicalClassQueryOperation  {
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
    public static class Islogical extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Islogical();
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
    public static class Isstruct extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isstruct();
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
    public static class Ischar extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ischar();
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
    public static class Iscell extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Iscell();
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
    public static class Isa extends AbstractLogicalClassQueryOperation  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isa();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseIsa(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "isa";
        }
        
    }
    public static abstract class AbstractArrayOperation extends AbstractPureFunction  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayOperation(this,arg);
        }
        
    }
    public static class Sort extends AbstractArrayOperation  {
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
    public static class Unique extends AbstractArrayOperation  {
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
    public static class Find extends AbstractArrayOperation  {
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
    public static abstract class AbstractArrayConstructor extends AbstractArrayOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayConstructor(this,arg);
        }
        
    }
    public static class Diag extends AbstractArrayConstructor  {
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
    public static class Reshape extends AbstractArrayConstructor  {
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
    public static class Permute extends AbstractArrayConstructor  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Permute();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.casePermute(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "permute";
        }
        
    }
    public static class Squeeze extends AbstractArrayConstructor  {
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
    public static class Complex extends AbstractArrayConstructor  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Complex();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseComplex(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "complex";
        }
        
    }
    public static abstract class AbstractDimensionCollapsingOperation extends AbstractArrayOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractDimensionCollapsingOperation(this,arg);
        }
        
    }
    public static class Prod extends AbstractDimensionCollapsingOperation  {
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
    public static class Sum extends AbstractDimensionCollapsingOperation  {
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
    public static class Mean extends AbstractDimensionCollapsingOperation  {
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
    public static class Min extends AbstractDimensionCollapsingOperation  {
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
    public static class Max extends AbstractDimensionCollapsingOperation  {
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
    public static abstract class AbstractShapeArrayConstructor extends AbstractArrayConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractShapeArrayConstructor(this,arg);
        }
        
    }
    public static abstract class AbstractNumericalShapeAndTypeArrayConstructor extends AbstractShapeArrayConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalShapeAndTypeArrayConstructor(this,arg);
        }
        
    }
    public static class Ones extends AbstractNumericalShapeAndTypeArrayConstructor  {
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
    public static class Zeros extends AbstractNumericalShapeAndTypeArrayConstructor  {
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
    public static class Eye extends AbstractNumericalShapeAndTypeArrayConstructor  {
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
    public static abstract class AbstractFloatShapeAndTypeArrayConstructor extends AbstractNumericalShapeAndTypeArrayConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractFloatShapeAndTypeArrayConstructor(this,arg);
        }
        
    }
    public static class Inf extends AbstractFloatShapeAndTypeArrayConstructor  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Inf();
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
    public static class Nan extends AbstractFloatShapeAndTypeArrayConstructor  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Nan();
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
    public static abstract class AbstractLogicalShapeArrayConstructor extends AbstractShapeArrayConstructor  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalShapeArrayConstructor(this,arg);
        }
        
    }
    public static class True extends AbstractLogicalShapeArrayConstructor  {
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
    public static class False extends AbstractLogicalShapeArrayConstructor  {
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
    public static abstract class AbstractArrayQuery extends AbstractArrayOperation  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractArrayQuery(this,arg);
        }
        
    }
    public static class Size extends AbstractArrayQuery  {
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
    public static class Nonzeros extends AbstractArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Nonzeros();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseNonzeros(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "nonzeros";
        }
        
    }
    public static class Cumprod extends AbstractArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cumprod();
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
    public static class Cumsum extends AbstractArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cumsum();
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
    public static class Sign extends AbstractArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sign();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseSign(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "sign";
        }
        
    }
    public static abstract class AbstractScalarResultArrayQuery extends AbstractArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractScalarResultArrayQuery(this,arg);
        }
        
    }
    public static abstract class AbstractNumericalScalarResultArrayQuery extends AbstractScalarResultArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractNumericalScalarResultArrayQuery(this,arg);
        }
        
    }
    public static class Length extends AbstractNumericalScalarResultArrayQuery  {
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
    public static class Ndims extends AbstractNumericalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ndims();
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
    public static class Numel extends AbstractNumericalScalarResultArrayQuery  {
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
    public static class Nnz extends AbstractNumericalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Nnz();
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
    public static abstract class AbstractLogicalScalarResultArrayQuery extends AbstractScalarResultArrayQuery  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractLogicalScalarResultArrayQuery(this,arg);
        }
        
    }
    public static class Any extends AbstractLogicalScalarResultArrayQuery  {
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
    public static class All extends AbstractLogicalScalarResultArrayQuery  {
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
    public static class Isemtpy extends AbstractLogicalScalarResultArrayQuery  {
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
    public static class Isnan extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isnan();
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
    public static class Isinf extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isinf();
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
    public static class Isfinite extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isfinite();
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
    public static class Isvector extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isvector();
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
    public static class Isscalar extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isscalar();
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
    public static class Isreal extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isreal();
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
    public static class Isnumeric extends AbstractLogicalScalarResultArrayQuery  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Isnumeric();
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
    public static abstract class AbstractImpureFunction extends Builtin  {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.caseAbstractImpureFunction(this,arg);
        }
        
    }
    public static class Superiorto extends AbstractImpureFunction  {
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
    public static class Exit extends AbstractImpureFunction  {
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
    public static class Quit extends AbstractImpureFunction  {
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
    public static class Tic extends AbstractTimeFunction  {
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
    public static class Toc extends AbstractTimeFunction  {
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
    public static class Cputime extends AbstractTimeFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Cputime();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Assert();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Nargoutchk();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Nargchk();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Str2func();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Pause();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Eval();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Evalin();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Feval();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Assignin();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Inputname();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Import();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Cd();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Exist();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Matlabroot();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Whos();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Which();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Version();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Clear();
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
    public static class Display extends AbstractReportFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Display();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Clc();
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
    public static class Warning extends AbstractReportFunction  {
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
    public static class Echo extends AbstractReportFunction  {
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
    public static class Diary extends AbstractReportFunction  {
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
    public static class Lastwarn extends AbstractReportFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Lastwarn();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Lasterror();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Format();
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
    public static class Randi extends AbstractRandomFunction  {
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
    public static class Randn extends AbstractRandomFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Randn();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Computer();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Beep();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Dir();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Unix();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Dos();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new System();
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
    public static class Save extends AbstractIoFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Save();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Input();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Textscan();
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
    public static class Sscanf extends AbstractPosixIoFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Sscanf();
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
    public static class Ftell extends AbstractPosixIoFunction  {
        //creates a new instance of this class
        protected Builtin create(){
            return new Ftell();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Ferror();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fopen();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fread();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Frewind();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fscanf();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fseek();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fwrite();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fgetl();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fgets();
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
        //creates a new instance of this class
        protected Builtin create(){
            return new Fclose();
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