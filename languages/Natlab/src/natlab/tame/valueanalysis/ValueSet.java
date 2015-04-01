package natlab.tame.valueanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.Value;
import natlab.toolkits.analysis.Mergable;

/**
 * This represents a set of values that a variable can take on.
 * It is actually a map from ClassReference -> AbstractValue.
 * Objects of this class are immutable.
 * Note that this class uses factory method (newInstance) rather than constructors.
 * 
 * @author ant6n
 * 
 * Note about implementation: this will only actually use a map if there is more
 * than element stored by this set. If there is one, a singleton value is used.
 * This should be ok since this class is immutable.
 */
public class ValueSet<V extends Value<V>> implements 
  Iterable<V>, Mergable<ValueSet<V>>{
    LinkedHashMap<ClassReference, V> map = null;
    V singleton = null; //used if this value set only stores one value
    int size = 0; //number of elements in this
    public static boolean DEBUG = false;
    
    //Xu add this method for code generation
    public V getSingleton(){
    	return singleton;
    }
    
    private ValueSet(){}
    public  static <V extends Value<V>> ValueSet<V> newInstance(){
        return new ValueSet<V>();
    }
    
    private ValueSet(Map<ClassReference, V> aMap){
        if (aMap.size() == 0) return;            
        if (aMap.size() == 1){
            size = 1;
            singleton = aMap.values().iterator().next();
        } else {
            map = new LinkedHashMap<ClassReference, V>();
            map.putAll(aMap);
            size = aMap.size();
        }
    }
    public static <V extends Value<V>> ValueSet<V> newInstance(Map<ClassReference, V> aMap){
        return new ValueSet<V>(aMap);
    }
    
    //this is private, because it's meaningless for immutable object
    private ValueSet(ValueSet<V> other){
        if (other.size == 1){
            singleton = other.singleton;
            size = 1;
        }
        if (other.size > 1){
            map = new LinkedHashMap<ClassReference, V>();
            map.putAll(other.map);
            size = other.size;
        }
    }
    
    private ValueSet(V value){
        singleton = value;
        size = 1;
    }
    public static <V extends Value<V>> ValueSet<V> newInstance(V value){
        if (value == null) throw new NullPointerException();
        return new ValueSet<V>(value);
    }
   
    
    /**
     * factory method for collections - the given values need not to have distinct
     * matlab classes. Values with equal matlab classes will get merged together.
     */
    public static <V extends Value<V>> ValueSet<V> newInstance(Collection<V> values){
        return new ValueSet<V>(values.iterator());
    }
    
    /**
     * constructor from a set of not necessairly distinct Values (i.e. they may have
     * different types)
     */
    private ValueSet(Collection<V> values){
        this(values.iterator());
    }
    /**
     * constructor form an iterator of not necessarily distinct values (i.e. they may
     * have different types)
     */
    @SuppressWarnings("unchecked")
	private ValueSet(Iterator<V> iterator){
        if (!iterator.hasNext()) return;
        singleton = iterator.next();
        if (!iterator.hasNext()){
            size = 1;
        } else {
            map = new LinkedHashMap<ClassReference, V>();
            map.put(singleton.getMatlabClass(), singleton);
            while (iterator.hasNext()){
                V next = iterator.next();
                //merge if necessary
                if (map.containsKey(next.getMatlabClass())){
                    map.put(next.getMatlabClass(), 
                            (V)map.get(next.getMatlabClass()).merge(next));
                } else {
                    map.put(next.getMatlabClass(),next);
                }
            }
            singleton = null;
            size = map.size();
            //check if we actually ended up having a singleton
            if (size == 1){
                singleton = map.values().iterator().next();
                map = null;
            }
            
        }
    }
    
    
    /**
     * returns a new ValueSet with all elements of this, plus the given
     * value merged into the set
     * @return
     */
    @SuppressWarnings("unchecked")
	public ValueSet<V> add(V v){
        if (v == null) throw new NullPointerException();
        if (size == 0) return new ValueSet<V>(v); //case previous value set empty
        //check whether we add more elements
        ValueSet<V> result;
        if (this.hasMatlabClass(v.getMatlabClass())){
            //we need to merge the element
            result = new ValueSet<V>(this);
            if (size == 1){
                result.singleton = (V)result.singleton.merge(v);                
            } else {
            	result.map.put(v.getMatlabClass(), 
            			(V)result.map.get(v.getMatlabClass()).merge(v));
            }
        } else {
            //we add one more value to the set - it's gonna be a map
            if (size == 1){
                result = new ValueSet<V>();
                result.map = new LinkedHashMap<ClassReference, V>();
                result.map.put(singleton.getMatlabClass(),singleton);                
            } else {
                result = new ValueSet<V>(this);
            }
            result.map.put(v.getMatlabClass(), v);
            result.size = map.size();
        }
        return result;
    }
    
    @Override
    public Iterator<V> iterator() {
        if (size > 1){
            return map.values().iterator();
        } else {
            return values().iterator();
        }
    }
    
    public Collection<V> values(){
        if (size > 1){
            return map.values();
        } else {
            List<V> list = new ArrayList<V>(size);
            if (size == 1) list.add(singleton);
            return list;
        }
    }
    public V get(ClassReference aClass){
        if (size > 1){
            return map.get(aClass);
        } else {
            if (size == 1 && singleton.getMatlabClass().equals(aClass)) return singleton;
            return null;
        }
    }
    public Collection<ClassReference> getMatlabClasses(){
        if (size > 1){
            return map.keySet();
        } else {
            Collection<ClassReference> list = new ArrayList<ClassReference>(size);
            if (size == 1) list.add(singleton.getMatlabClass());
            return list;
        }
    }
    public int size(){
        return size;
    }
    /**
     * there exists a value with the given class in this map
     * @return
     */
    public boolean hasMatlabClass(ClassReference aClass){
        if (size == 1) return singleton.equals(aClass);
        if (size == 0) return false;
        return map.containsKey(aClass);        
    }
    
    
    @SuppressWarnings("unchecked")
	@Override
    public ValueSet<V> merge(ValueSet<V> other) {
        if (DEBUG) System.out.println("merge: "+this+", "+other);
        ValueSet<V> result;
        //clone the bigger set
        if (size() > other.size()){
            result = new ValueSet<V>(this);
        } else {
            result = new ValueSet<V>(other);
            other = this;
        }
        if (other.size == 0) return result;
        //System.out.println(result+" "+other);
        //merge elements from the smaller set
        if (result.size == 1){
            if (other.size == 1){
                if (result.singleton.getMatlabClass().equals(other.singleton.getMatlabClass())){
                    result.singleton = (V)result.singleton.merge(other.singleton);
                } else {
                    result.map = new LinkedHashMap<ClassReference, V>(3); //|result| = |size| = 1
                    result.map.put(result.singleton.getMatlabClass(), result.singleton);
                    result.singleton = null;
                    V v = other.singleton;
                    result.map.put(v.getMatlabClass(), v);
                    result.size = 2;
                }
            }
        } else { // |result| > 1
            if (other.size == 1){
                V v = other.singleton;
                ClassReference key = v.getMatlabClass();
                if (result.hasMatlabClass(key)){ //exist in both sets - merge
                    result.map.put(key,(V)result.map.get(key).merge(v));
                } else { //put new element
                    result.map.put(key, v);
                }
            } else {
                for (V v : other){
                    ClassReference key = v.getMatlabClass();
                    if (result.hasMatlabClass(key)){ //exist in both sets - merge
                        result.map.put(key,(V)other.map.get(key).merge(v));
                    } else { //put new element
                        result.map.put(key, v);
                    }
                }
            }
            result.size = result.map.size();
        }
        return result;
    }

    /**
     * given a list of valueSets, returns the cross product of the sets as a
     * List of List of Values. That is, it returns all combinations of picking a value
     * from each set of the list.
     * For example (int,double)x(double)x(char,double)
     * returns
     * (int,double,char)
     * (int,double,double)
     * (double,double,char)
     * (doble,double,double)
     * 
     * @param list
     * @return
     */
    public static  <V extends Value<V>> List<LinkedList<V>> cross(
            ValueSet<V>... sets){
        return cross(Arrays.asList(sets));
    }
    public static  <V extends Value<V>> List<LinkedList<V>> cross(
            List<ValueSet<V>> list){
        return cross(list.iterator());
    }
    @SuppressWarnings("unchecked")
    public static <V extends Value<V>> List<LinkedList<V>> cross(
            Iterator<ValueSet<V>> i){
        if (!i.hasNext()){
            LinkedList<LinkedList<V>> result = new LinkedList<LinkedList<V>>();
            result.add(new LinkedList<V>());
            return result;
        }
        //get set, (others)
        ValueSet<V> set = i.next();
        List<LinkedList<V>> rec = cross(i);
        //compute set x others
        if (rec.size() == 1 && rec.get(0).size() == 0){
            //base case just return the set of elements
            LinkedList<LinkedList<V>> result = new LinkedList<LinkedList<V>>();
            for (V v : set){
                result.add(new LinkedList<V>());
                result.peekLast().add(v);
            }
            return result;
        } else {
            //TODO add case for there being only one value - no copy needed
            LinkedList<LinkedList<V>> result = new LinkedList<LinkedList<V>>();            
            //recursive case 
            for (LinkedList<V> currentList : rec){
                for (V v : set){
                    LinkedList<V> newList = ((LinkedList<V>)currentList.clone());
                    newList.addFirst(v);
                    result.add(newList);
                }
            }
            return result;
        }
    }
    
    
    @Override
    public String toString() {
        if (size == 0) return "[]";
        if (size == 1) return singleton.toString();
        return map.values().toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ValueSet<?>)) return false;
        ValueSet<?> other = (ValueSet<?>)obj;
        if (size == 0) return other.size == 0;
        if (size == 1) return singleton.equals(other.singleton);
        return map.equals(other.map);
    }
    
    @Override
    public int hashCode() {
        if (size == 0) return 0;
        if (size == 1) return singleton.hashCode();
        return map.hashCode();
    }
    
    public boolean contains(ClassReference aClass){
        if (size == 0) return false;
        if (size == 1){
            ClassReference classRef = singleton.getMatlabClass();
            return (classRef == null)?(classRef == aClass):classRef.equals(aClass);
        }
        return map.containsKey(aClass);
    }
    public boolean contains(String aClassName){
        if (size == 0) return false;
        if (size == 1) return singleton.getMatlabClass().getName().equals(aClassName);
        for (ClassReference k : map.keySet()){
            if (k.getName().equals(aClassName)) return true;
        }
        return false;
    }
    
    /**
     * returns a new value set, calling toFunctionArgument on all values
     */
    public ValueSet<V> toFunctionArgument(boolean recursive){
        if (size() == 0) return this;
        ValueSet<V> result = new ValueSet<V>(this);
        if (result.size == 1){
            result.singleton = (V)result.singleton.toFunctionArgument(recursive);
        } else {
            for (ClassReference aClass : result.map.keySet()){
                result.map.put(aClass,(V)result.map.get(aClass).toFunctionArgument(recursive));
            }
        }
        return result;
    }

}


