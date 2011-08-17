package natlab.Static.builtin;

import java.util.*;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.classes.reference.PrimitiveClassReference;
import natlab.Static.valueanalysis.value.Value;

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
            List<Value<?>> argValues){
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
            List<ClassReference> argClasses,List<Value<?>> argValues){
        MatchResult match = tree.match(true, new MatchResult(), argClasses, argValues);
        //System.out.println(match.getAllResults());
        if (match == null || match.numMatched != argClasses.size()) return null;
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
        
        /*** Match result class ***************************************************/
        /**
         * returns the union of this and another MatchResult, but
         * only if the argIndex and number of result elements match
         */
        MatchResult union(MatchResult other){
            if (numMatched == other.numMatched && numEmittedResults == other.numEmittedResults){
                MatchResult result = new MatchResult();
                result.numMatched = numMatched;
                result.p1 = this; result.p2 = other;
                result.numEmittedResults = numEmittedResults;
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
            MatchResult result = new MatchResult();
            result.numMatched = this.numMatched+1;
            result.p1 = this;
            result.numEmittedResults = numEmittedResults;
            return result;
        }
        
        /**
         * returns a match result which adds the given result, and refers back to this
         */
        MatchResult emit(ClassReference classRef){
            MatchResult result = new MatchResult();
            result.emittedClass = classRef;
            result.numMatched = this.numMatched;
            result.p1 = this;
            result.numEmittedResults = numEmittedResults+1;
            return result;
        }
        
        /**
         * returns all results as a linked list of sets of matlab classes
         */
        LinkedList<HashSet<ClassReference>> getAllResults(){
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
                List<ClassReference> inputClasses, List<Value<?>> inputValues);
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
                List<ClassReference> inputClasses, List<Value<?>> inputValues){
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
                List<ClassReference> inputClasses, List<Value<?>> inputValues){
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
                List<ClassReference> inputClasses, List<Value<?>> inputValues){
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
                List<ClassReference> inputClasses, List<Value<?>> inputValues){
            //isleft/isright - try to match first, then try to match second using the result
            MatchResult match = class1.match(true, previousMatchResult, inputClasses, inputValues);
            if (match == null){
                if (isLeft) return null; //if left we didn't properly match
                return previousMatchResult; //if right we'll just skip this
            }
            return class2.match(false, match, inputClasses, inputValues);
        }
    }
    //<n> - matches or emits argument n
    public static class MCNum extends MC{
        int num;
        public MCNum(int num){this.num = num;}
        public String toString() { return num+""; }
        public MatchResult match(boolean isLeft,MatchResult previousMatchResult,
                List<ClassReference> inputClasses, List<Value<?>> inputValues){
            if (isLeft){ //isleft
                if (num < inputClasses.size() &&
                    inputClasses.get(previousMatchResult.numEmittedResults).
                        equals(inputClasses.get(num))) return previousMatchResult.next();
                return null;
            } else { //isright
                return previousMatchResult.emit(inputClasses.get(num));
            }
        }
    }
    
    
    /**
     * main for testing
     */
    public static void main(String[] args) {
        MC dbl = new MCBuiltin("double");
        MC chr = new MCBuiltin("char");
        MC itg = new MCBuiltin("int16");
        MC log = new MCBuiltin("logical");
        
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
    
    }
    /**
     * for testing
     */
    static void printMatch(MC tree,ClassReference ... args){
        System.out.print(Arrays.asList(args)+"->");
        System.out.println(match(tree,Arrays.<ClassReference>asList(args)));
    }
}




