package natlab.tame.builtin.classprop;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import natlab.tame.classes.reference.ClassReference;


/**
 * match result object is the result of matching operation, it stores how many
 * elements were matched, what elements were emitted (results). It operates
 * as a singly linked list or tree, for union results.
 * It is used to store partial matches. It is part of how the matching algorithm works.
 */
public class ClassPropMatch{
    int numMatched; //number of matched arguments
    ClassPropMatch p1,p2; //parents (p2 is set if this is a union)
    ClassReference emittedClass;
    int numEmittedResults; //number of emmited results
    boolean isError = false; //if any ClassPropMatch is an error, then overall we'll have an error
    
    /**
     * default constructor
     */
    public ClassPropMatch(){}
    /**
     * constructor referring to parent, copying numMatched/numEmmited, error
     */
    public ClassPropMatch(ClassPropMatch parent){
        this.numMatched = parent.numMatched;
        this.p1 = parent;
        this.numEmittedResults = parent.numEmittedResults;
        this.isError = parent.isError;
    }
    /**
     * constructor referring to parent, copying numMatched,error and adding extra result
     */
    public ClassPropMatch(ClassPropMatch parent,ClassReference emittedClass){
        this.numMatched = parent.numMatched;
        this.p1 = parent;
        this.emittedClass = emittedClass;
        this.numEmittedResults = parent.numEmittedResults+1;
        this.isError = parent.isError;
    }
    
    
    /**
     * returns the union of this and another ClassPropMatch, but
     * only if the argIndex and number of result elements match
     */
    public ClassPropMatch union(ClassPropMatch other){
        if (numMatched == other.numMatched && numEmittedResults == other.numEmittedResults){
            ClassPropMatch result = new ClassPropMatch(this);
            result.p2 = other;
            result.isError = this.isError || other.isError;
            return result;
        } else {
            throw new UnsupportedOperationException(
                    "class propagation resulted in inconsistent result classes");
        }
    }
    /**
     * returns a ClassPropMatch which advances argIndex by one, and refers back to this
     */
    public ClassPropMatch next(){
        ClassPropMatch result = new ClassPropMatch(this);
        result.numMatched = this.numMatched+1;
        return result;
    }
    
    /**
     * returns a match result which adds the given result, and refers back to this
     */
    public ClassPropMatch emit(ClassReference classRef){
        return new ClassPropMatch(this,classRef);
    }

    /**
     * returns a match result which adds an error to the given result,
     * and refers back tho this
     */
    public ClassPropMatch error(){
       ClassPropMatch m = new ClassPropMatch(this); 
       m.isError = true;
       return m;
    }
    
    /**
     * returns all results as a linked list of sets of matlab classes
     * returns null if the match result is erroneous
     * every set in the list has at least one value.
     */
    public List<Set<ClassReference>> getAllResults(){
        //check if matchresult is error
        if (isError) return null;
        LinkedList<Set<ClassReference>> results = new LinkedList<Set<ClassReference>>();
        PriorityQueue<ClassPropMatch> pq = new PriorityQueue<ClassPropMatch>(10,new Comparator<ClassPropMatch>() {
            public int compare(ClassPropMatch o1, ClassPropMatch o2) {
                return o2.numEmittedResults - o1.numEmittedResults;
            }
        });
        pq.add(this);
        int currentIndex = numEmittedResults;
        Set<ClassReference> currentSet = new HashSet<ClassReference>();
        while(pq.size() > 0){
            ClassPropMatch current = pq.poll();
            //start a new set
            if (currentIndex != current.numEmittedResults){
                results.addFirst(currentSet);
                if (currentSet.size() == 0){
                	throw new UnsupportedOperationException(
                			"Class propagation resulted in an empty result. This should be impossible");
                }
                currentSet = new HashSet<ClassReference>();
                currentIndex = current.numEmittedResults;
            }
            //put info from this in set and deque
            if (current.emittedClass != null) currentSet.add(current.emittedClass);
            if (current.p1 != null) pq.offer(current.p1);
            if (current.p2 != null) pq.offer(current.p2);
        }
        return results;
    }
    
    public int getNumMatched(){
    	return numMatched;
    }
    public int getNumEmittedResults(){
    	return numEmittedResults;
    }
    public ClassReference getEmittedClass(){
    	return emittedClass;
    }
    
    @Override
    public String toString() {
        return "machresult-"+numMatched+"-"+getAllResults();
    }
}