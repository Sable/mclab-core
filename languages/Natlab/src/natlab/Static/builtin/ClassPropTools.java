package natlab.Static.builtin;

import java.util.*;

import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.constant.*;
import natlab.Static.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.Static.valueanalysis.value.*;

/**
 * tools for building class propagation information for builtins.
 * This works together with the class propagation tag of the builtins.csv,
 * and implements the java backend for the tiny class propagation langauge
 * @author ant6n
 */


public class ClassPropTools {
    static final boolean DEBUG = false;
        
    /**
     * given a Matlab Class Propagation description and a set of argument values,
     * returns a list of sets of return matlab classes.
     * returns null if the combination of argument classes is illegal.
     */
    public static LinkedList<HashSet<ClassReference>> matchByValues(MC tree,
            List<? extends Value<?>> argValues){
        ArrayList<ClassReference> argClasses = new ArrayList<ClassReference>(argValues.size());
        for (Value<?> v : argValues){
            argClasses.add(v.getMatlabClass());
        }
        return match(tree,argClasses,argValues);
    }
    
    /**
     * given a Matlab Class Propagation description and a set of argument classes,
     * returns a list of sets of return matlab classes
     * returns null if the combination of argument classes is illegal.
     */
    public static LinkedList<HashSet<ClassReference>> match(MC tree,
            List<ClassReference> argClasses){
        return match(tree,argClasses,null);
    }
    /**
     * given a Matlab Class Propagation description and a set of argument classes,
     * and corresponding (optional) argument values,
     * returns a list of sets of return matlab classes
     * returns null if the combination of argument classes is illegal.
     */

    public static LinkedList<HashSet<ClassReference>> match(MC tree,
            List<ClassReference> argClasses,List<? extends Value<?>> argValues){
        MatchResult match = tree.match(true, new MatchResult(), argClasses, argValues);
        //System.out.println(match.getAllResults());
        if (match == null || match.isError || match.numMatched != argClasses.size()) return null;
        return match.getAllResults();
    }
    
    
    /**
     * match result object is the result of matching operation, it stores how many
     * elements were matched, what elements were emitted (results). It operates
     * as a singly linked list or tree, for union results
     */
    private static class MatchResult{
        int numMatched; //number of matched arguments
        MatchResult p1,p2; //parents
        ClassReference emittedClass;
        int numEmittedResults; //number of emmited results
        boolean isError = false; //if any MatchResult is an error, then overall we'll have an error
        /**
         * default constructor
         */
        MatchResult(){}
        /**
         * constructor referring to parent, copying numMatched/numEmmited, error
         */
        MatchResult(MatchResult parent){
            this.numMatched = parent.numMatched;
            this.p1 = parent;
            this.numEmittedResults = parent.numEmittedResults;
            this.isError = parent.isError;
        }
        /**
         * constructor referring to parent, copying numMatched,error and adding extra result
         */
        MatchResult(MatchResult parent,ClassReference emittedClass){
            this.numMatched = parent.numMatched;
            this.p1 = parent;
            this.emittedClass = emittedClass;
            this.numEmittedResults = parent.numEmittedResults+1;
            this.isError = parent.isError;
        }
        
        
        /**
         * returns the union of this and another MatchResult, but
         * only if the argIndex and number of result elements match
         */
        MatchResult union(MatchResult other){
            if (numMatched == other.numMatched && numEmittedResults == other.numEmittedResults){
                MatchResult result = new MatchResult(this);
                result.p2 = other;
                result.isError = this.isError || other.isError;
                return result;
            } else {
                throw new UnsupportedOperationException(
                        "class propagation resulted in inconsistent result classes");
            }
        }
        /**
         * returns a MatchResult which advances argIndex by one, and refers back to this
         */
        MatchResult next(){
            MatchResult result = new MatchResult(this);
            result.numMatched = this.numMatched+1;
            return result;
        }
        
        /**
         * returns a match result which adds the given result, and refers back to this
         */
        MatchResult emit(ClassReference classRef){
            return new MatchResult(this,classRef);
        }

        /**
         * returns a match result which adds an error to the given result,
         * and refers back tho this
         */
        MatchResult error(){
           MatchResult m = new MatchResult(this); 
           m.isError = true;
           return m;
        }
        
        /**
         * returns all results as a linked list of sets of matlab classes
         * returns null if the match result is erroneous
         */
        LinkedList<HashSet<ClassReference>> getAllResults(){
            //check if matchresult is error
            if (isError) return null;
            LinkedList<HashSet<ClassReference>> results = new LinkedList<HashSet<ClassReference>>();
            Deque<MatchResult> deque = new LinkedList<MatchResult>();
            deque.add(this);
            int currentIndex = numEmittedResults;
            HashSet<ClassReference> currentSet = new HashSet<ClassReference>();
            while(deque.size() > 0){
                MatchResult current = deque.removeFirst();
                //start a new set
                if (currentIndex != current.numEmittedResults){
                    results.addFirst(currentSet);
                    currentSet = new HashSet<ClassReference>();
                    currentIndex = current.numEmittedResults;
                }
                //put info from this in set and deque
                if (current.emittedClass != null) currentSet.add(current.emittedClass);
                if (current.p1 != null) deque.addLast(current.p1);
                if (current.p2 != null) deque.addLast(current.p2);
            }
            return results;
        }
        
        @Override
        public String toString() {
            return "machresult-"+numMatched+"-"+getAllResults().toString();
        }
    }
        
    
    
    
    /**** class prop tree classes *************************************************/
    /**
     * matlab classes 
     */
    //represents a general matlab class in the class propagation language
    public static abstract class MC{
        /**
         * computes the match result for the tree, given the input classes, and
         * (optional) values. The input values can be used to get extra information
         * about the arguments, but may be null. The input values should not be
         * used to query the argument class, because arguments may be coerced to
         * other classes.
         * previousMatchResult gives information about the matches so far, and
         * the current index in the arguments, and should not be altered.
         * This method returns null if the match failed.
         */
        abstract public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues);
    }
    
    //<builtin class>
    public static class MCBuiltin extends MC{
        ClassReference classRef;
        public MCBuiltin(String name){
            //TODO - check in more than just primitive classes?
            this.classRef = PrimitiveClassReference.valueOf(name.toUpperCase());
        }
        public String toString() {return classRef.getName();}
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
            if (isLeft){ //isleft - try to match
                if (previousMatchResult.numMatched < inputClasses.size() &&
                    inputClasses.get(previousMatchResult.numMatched).equals(classRef))
                    return previousMatchResult.next();
                return null;
            } else { //isright - just emit the class
                return previousMatchResult.emit(classRef);
            }
        }
    }
    
    //class1 | class2
    public static  class MCUnion extends MC{
        MC class1,class2;
        public MCUnion(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return class1+"|"+class2;} 
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
            if (isLeft){ //isleft - try left and right, then return the longer succesfull match
                MatchResult match1 = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
                MatchResult match2 = class2.match(isLeft, previousMatchResult, inputClasses, inputValues);
                if (DEBUG){
                    System.out.println("|left: before: "+previousMatchResult+" expr:"+class1+" result: "+match1);
                    System.out.println("|right:before: "+previousMatchResult+" expr:"+class2+" result: "+match2);
                }
                if (match1 == null) return match2;
                if (match2 == null) return match1;
                if (match1.numMatched >= match2.numMatched) return match1;
                return match2;
            } else { //isright
                MatchResult match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
                return match.union(class2.match(isLeft, previousMatchResult, inputClasses, inputValues));
            }
        }
    }
    //class1 & class2
    public static class MCChain extends MC{
        MC class1,class2;
        public MCChain(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return "("+class1+")&("+class2+")";}        
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
            if (isLeft){ //isleft - try to match first, then try to match second using the result
                MatchResult match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
                if (match == null) return null;
                return class2.match(isLeft, match, inputClasses, inputValues);
            } else { //is right, emit first, then second
                MatchResult match = class1.match(isLeft, previousMatchResult, inputClasses, inputValues);
                return class2.match(isLeft, match, inputClasses, inputValues);
            }
        }
    }
    //class1 > class2
    public static class MCMap extends MC{
        MC class1,class2;
        public MCMap(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return "("+class1+">"+class2+")";}        
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
            //isleft/isright - try to match first, then try to match second using the result
            MatchResult match = class1.match(true, previousMatchResult, inputClasses, inputValues);
            if (match == null){
                if (isLeft) return null; //if left we didn't properly match
                return previousMatchResult; //if right we'll just skip this
            }
            return class2.match(false, match, inputClasses, inputValues);
        }
    }
    //<n> - matches or emits argument n, negative numbers match from the back
    public static class MCNum extends MC{
        int num;
        public MCNum(int num){this.num = num;}
        public String toString() { return num+""; }
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues){
            int index = num<0?(inputClasses.size()+num):num; //negative index?
            if (isLeft){ //isleft
                if (index < inputClasses.size() && index >= 0 &&
                    inputClasses.get(previousMatchResult.numEmittedResults).
                        equals(inputClasses.get(num))) return previousMatchResult.next();
                return null;
            } else { //isright
                //TODO should catch the index out of bounds exception?
                return previousMatchResult.emit(inputClasses.get(index));
            }
        }
    }
    //none - matches anything without advancing the numMatched, emits nothing
    public static class MCNone extends MC{
        public String toString() { return "none"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            return previousMatchResult;
        }
        
    }
    //begin - matches if the current read-index is 0, emits nothing
    public static class MCBegin extends MC{
        public String toString() { return "begin"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            return (previousMatchResult.numMatched == 0)?previousMatchResult:null;
        }
    }
    //end - matches if all input elements have been matched, emits nothing
    public static class MCEnd extends MC{
        public String toString() { return "end"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            return (previousMatchResult.numMatched == inputClasses.size())?previousMatchResult:null;
        }
    }
    //any - matches the next input, no matter what it is - throws error if used as rhs
    public static class MCAny extends MC{
        public String toString() { return "any"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            if (!isLeft) throw new UnsupportedOperationException("class propagation error: 'any' can not be used on the right hand side");
            return (previousMatchResult.numMatched < inputClasses.size())?
                    previousMatchResult.next():
                    null;
        }
    }
    //If there is another argument to consume, matches if it is
    //scalar, or if it's shape is unknown, without consuming the argument.
    //Can be used to check if the next argument is scalar.
    public static class MCScalar extends MC{
        public String toString() { return "scalar"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            if (!isLeft) throw new UnsupportedOperationException("class propagation error: 'any' can not be used on the right hand side");
            int i = previousMatchResult.numMatched; 
            if (i >= inputClasses.size())
                return null; //no more elements to match
            if (inputValues == null || inputValues.get(i) == null) return previousMatchResult;
            //find if the value is scalar nor not 
            Value<?> value = inputValues.get(i);
            if (value instanceof MatrixValue<?>){
                MatrixValue<?> matrix = (MatrixValue<?>)value;
                if (matrix.isConstant()){
                    return matrix.getConstant().isScalar()?previousMatchResult:null;
                }
            }
            if (value.hasShape() && !value.getShape().maybeScalar()){
                return null;
            }   
            //we couldn't find anyting out
            return previousMatchResult;        
        }
    }    
    //error - emits an error result - any MatchResult that includes an error object will result in an error overall
    public static class MCError extends MC{
        public String toString() { return "error"; }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses,
                List<? extends Value<?>> inputValues) {
            return previousMatchResult.error();
        }
    }
    
    
    
    
    //classOfString([builtin1,builtin2,builtin3,...])
    //matches the next input if it is a String, and assumes it is a String denoting
    //
    //TODO - error?
    //var(numeric|logical)&(classOfString(numeric)|)
    //public Static Class MCClassOfString

    
    //coerce(MC match and replace for all args, MC affeced expr)
    //the match and replace is run on all arguments separately, and have
    //to either not match or result in one single result
    //example: coerce(char|logical>double, numerical&numerical>double)
    public static class MCCoerce extends MC{
        MC tree;
        MC replaceTree;
        public MCCoerce(MC replaceTree,MC tree){
            this.tree = tree;
            this.replaceTree = replaceTree;
        }
        public String toString() {
            return "coerce("+replaceTree+","+tree+")";
        }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<? extends Value<?>> inputValues) {
            //try to match every arg, replacing if necessary
            ArrayList<ClassReference> newInputClasses = 
                new ArrayList<ClassReference>(inputClasses);
            System.out.println(newInputClasses);
            for(int i = 0; i < inputClasses.size(); i++){
                //do match for arg i
                MatchResult match = 
                    replaceTree.match(true, new MatchResult(), Collections.singletonList(inputClasses.get(i)), null);
                //check whether it returned a match - and whether the match result is valid
                if (match != null){
                    if (match.numEmittedResults != 1 || match.emittedClass == null){
                        throw new UnsupportedOperationException(
                                "argument coercion for builtin class propagation must result in one ouput "
                                +"for every argument, received "+match+" for "+this);
                    }
                    //override argument class with the singleton match result
                    newInputClasses.set(i, match.emittedClass);                    
                }
                System.out.println(newInputClasses);
            }
            return tree.match(isLeft, previousMatchResult, newInputClasses, inputValues);
        }
        
    }
    
    
    // if next is a string, consumes it (otherwise no match)
    // if the value of the string is known, check whether it's among the types emmitted by the
    // argument expression (which should emit 1 result). If it is, emite that type.
    // if it isn't, emit an error
    public static class MCTypeString extends MC{
        MC tree;
        HashSet<String> allowedTypes = new HashSet<String>();
        Map<String,ClassReference> types = new HashMap<String,ClassReference>();
        public MCTypeString(MC tree){
            this.tree = tree;
            //get all allowed types from tree
            LinkedList<HashSet<ClassReference>> treeResult = tree.match(
                    false, new MatchResult(), new LinkedList<ClassReference>(), new LinkedList<Value<?>>()).getAllResults();
            if (treeResult.size() != 1) throw new UnsupportedOperationException(
                    "typeString arguments neeed emit one result, got "+treeResult+" for "+this);
            for (ClassReference c : treeResult.get(0)){
                if (!(c instanceof PrimitiveClassReference)) throw new UnsupportedOperationException(
                    "typeString arguments neeed emit primitive types, got "+c+" for "+this);
                allowedTypes.add(c.toString());
                types.put(c.toString(), c);
            }
        }
        public String toString() {
            return "typeString("+tree+")";
        }
        public MatchResult match(boolean isLeft,
                MatchResult previousMatchResult,
                List<ClassReference> inputClasses,
                List<? extends Value<?>> inputValues) {
            int i = previousMatchResult.numMatched;
            if (previousMatchResult.numMatched < inputClasses.size() &&
                    inputClasses.get(i).equals(PrimitiveClassReference.CHAR)){
                //consume element
                MatchResult next = previousMatchResult.next();
                //if the next value is not known, just return whatever 
                if ((inputValues == null) || inputValues.get(i) == null
                        || !(inputValues.get(i) instanceof MatrixValue<?>) 
                        || !((MatrixValue<?>)(inputValues.get(i))).isConstant() ){
                    return tree.match(false, next, new LinkedList<ClassReference>(), new LinkedList<Value<?>>());
                }
                //else we have a value
                Constant constant = ((MatrixValue<?>)(inputValues.get(i))).getConstant();
                if (!(constant instanceof CharConstant)){
                    return next.error();
                }
                //check whether the value is in the list of results
                String value = ((CharConstant)(constant)).getValue();
                if (allowedTypes.contains(value)) {
                    return next.emit(types.get(value));
                } else {
                    return next.error();
                }
            } else {
                //next arg not a string
                return null;
            }
        }
    }
    
    
    /**
     * main for testing
     */
    public static void main(String[] args) {
        MCBuiltin dbl = new MCBuiltin("double");
        MCBuiltin chr = new MCBuiltin("char");
        MCBuiltin itg = new MCBuiltin("int16");
        MCBuiltin log = new MCBuiltin("logical");
        
        MC a = new MCMap(new MCUnion(dbl,chr),new MCChain(log,itg));
        System.out.println();
        System.out.println(a);
        printMatch(a,PrimitiveClassReference.DOUBLE);
        printMatch(a,PrimitiveClassReference.CHAR);
        printMatch(a,PrimitiveClassReference.CHAR,PrimitiveClassReference.DOUBLE);
        printMatch(a);
        
        MC b = new MCChain(new MCMap(dbl,dbl),new MCMap(itg,itg));
        System.out.println();
        System.out.println(b);
        printMatch(b,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.INT16);
        
        MC c = new MCUnion(new MCMap(new MCChain(new MCUnion(itg,dbl),dbl),chr),new MCMap(dbl,dbl));
        System.out.println();
        System.out.println(c);
        printMatch(c,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(c,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(c,PrimitiveClassReference.INT16,PrimitiveClassReference.DOUBLE);
        printMatch(c,PrimitiveClassReference.DOUBLE);

        MC c2 = new MCUnion(new MCMap(dbl,dbl),new MCMap(new MCChain(new MCUnion(itg,dbl),dbl),chr));
        System.out.println();
        System.out.println(c2);
        printMatch(c2,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(c2,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(c2,PrimitiveClassReference.INT16,PrimitiveClassReference.DOUBLE);
        printMatch(c2,PrimitiveClassReference.DOUBLE);
        
        MC d = new MCUnion(new MCMap(dbl,dbl),new MCMap(itg,itg));
        System.out.println();
        System.out.println(d);
        printMatch(d,PrimitiveClassReference.DOUBLE);
        printMatch(d,PrimitiveClassReference.INT16);

        MC e = new MCMap(new MCChain(dbl,dbl),itg);
        System.out.println();
        System.out.println(e);
        printMatch(e,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(e,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(e,PrimitiveClassReference.DOUBLE);
        
        
        MC f = (new MCChain(dbl,dbl));
        System.out.println();
        System.out.println(f);
        printMatch(f,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f,PrimitiveClassReference.DOUBLE);

        MC f2 = new MCMap(new MCChain(dbl,dbl),new MCNone());
        System.out.println();
        System.out.println(f2);
        printMatch(f2,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f2,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f2,PrimitiveClassReference.DOUBLE);

        MC f3 = new MCMap(new MCNone(), new MCChain(dbl,dbl));
        System.out.println();
        System.out.println(f3);
        printMatch(f3,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f3,PrimitiveClassReference.DOUBLE);
        printMatch(f3);

        MC f4 = new MCCoerce(
                    new MCMap(new MCUnion(chr,itg),dbl),
                    new MCMap(new MCChain(dbl,dbl),new MCChain(new MCNum(0),new MCNum(1))));
        System.out.println();
        System.out.println(f4);
        printMatch(f4,PrimitiveClassReference.DOUBLE,PrimitiveClassReference.DOUBLE);
        printMatch(f4,PrimitiveClassReference.CHAR,PrimitiveClassReference.INT16);
        printMatch(f4,PrimitiveClassReference.INT16,PrimitiveClassReference.CHAR);
        
        
        System.out.println("(coerce([logical,char],double,double>double): "+Builtin.Tril.getInstance().getMatlabClassPropagationInfo());
        
        MC f5 = new MCTypeString(new MCUnion(new MCBuiltin("double"),new MCBuiltin("logical")));
        System.out.println();
        System.out.println(f5);
        printMatch(f5,PrimitiveClassReference.DOUBLE);
        printMatch(f5,PrimitiveClassReference.CHAR);
        printMatchByValue(f5,new SimpleMatrixValueFactory().newMatrixValue("double"));
        printMatchByValue(f5,new SimpleMatrixValueFactory().newMatrixValue("int16"));
        
        
        MC f6 = new MCMap(new MCChain(new MCScalar(),new MCBuiltin("char")),new MCBuiltin("logical"));
        System.out.println();
        System.out.println(f6);
        printMatchByValue(f6,new SimpleMatrixValueFactory().newMatrixValue("aaaa"));
        printMatchByValue(f6,new SimpleMatrixValueFactory().newMatrixValue("a"));
        
    }
    /**
     * for testing
     */
    static void printMatch(MC tree,ClassReference ... args){
        System.out.print(Arrays.asList(args)+"->");
        System.out.println(match(tree,Arrays.<ClassReference>asList(args)));
    }
    static void printMatchByValue(MC tree,Value<?>... values){
        System.out.print(Arrays.asList(values)+"->");
        //create values
        LinkedList<ClassReference> types = new LinkedList<ClassReference>();
        for (Value<?> v: values){
            types.add(v.getMatlabClass());
        }
        System.out.println(match(tree,types,Arrays.asList(values)));
    }
}




