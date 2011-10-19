package natlab.Static.valueanalysis;

import java.util.*;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.valueanalysis.value.*;
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
 * TODO - should the singleton be implemented by making the map a general map,
 *        and using Collecitons.singletonMap?
 */
public class ValueSet<D extends MatrixValue<D>> implements 
  Iterable<Value<D>>, Mergable<ValueSet<D>>{
    LinkedHashMap<ClassReference, Value<D>> map = null;
    Value<D> singleton = null; //used if this value set only stores one value
    int size = 0; //number of elements in this
    
    private ValueSet(){}
    public  static <D extends MatrixValue<D>> ValueSet<D> newInstance(){
        return new ValueSet<D>();
    }
    
    private ValueSet(Map<ClassReference, D> aMap){
        if (aMap.size() == 0) return;            
        if (aMap.size() == 1){
            size = 1;
            singleton = aMap.values().iterator().next();
        } else {
            map = new LinkedHashMap<ClassReference, Value<D>>();
            map.putAll(aMap);
            size = aMap.size();
        }
    }
    public static <D extends MatrixValue<D>> ValueSet<D> newInstance(Map<ClassReference, D> aMap){
        return new ValueSet<D>(aMap);
    }
    
    //this is private, because it's meaningless for immutable object
    private ValueSet(ValueSet<D> other){
        if (other.size == 1){
            singleton = other.singleton;
            size = 1;
        }
        if (other.size > 1){
            map = new LinkedHashMap<ClassReference, Value<D>>();
            map.putAll(other.map);
            size = other.size;
        }
    }
    
    private ValueSet(Value<D> value){
        singleton = value;
        size = 1;
    }
    public static <D extends MatrixValue<D>> ValueSet<D> newInstance(Value<D> value){
        if (value == null) throw new NullPointerException();
        return new ValueSet<D>(value);
    }
   
    
    
    /**
     * constructor from a set of distinct Values (i.e. they are assumed to have
     * different types)
     */
    private ValueSet(Collection<Value<D>> values){
        
    }
    /**
     * constructor form an iterator of distinct values (i.e. they are assumed to
     * have different types)
     */
    private ValueSet(Iterator<Value<D>> iterator){
        if (!iterator.hasNext()) return;
        singleton = iterator.next();
        if (!iterator.hasNext()){
            size = 1;
        } else {
            map = new LinkedHashMap<ClassReference, Value<D>>();
            map.put(singleton.getMatlabClass(), singleton);
            while (iterator.hasNext()){
                Value<D> next = iterator.next();
                map.put(next.getMatlabClass(),next);
            }
            singleton = null;
            size = map.size();
        }
    }
    
    
    /**
     * returns a new ValueSet with all elements of this, plus the given
     * value merged into the set
     * @return
     */
    public ValueSet<D> add(Value<D> v){
        if (v == null) throw new NullPointerException();
        if (size == 0) return new ValueSet<D>(v); //case previous value set empty
        //check whether we add more elements
        ValueSet<D> result;
        if (this.hasMatlabClass(v.getMatlabClass())){
            //we need to merge the element
            result = new ValueSet<D>(this);
            if (size == 1){
                result.singleton = result.singleton.merge(v);                
            } else {
                result.map.put(v.getMatlabClass(), 
                        result.map.get(v.getMatlabClass()).merge(v));
            }
        } else {
            //we add one more value to the set - it's gonna be a map
            if (size == 1){
                result = new ValueSet<D>();
                result.map = new LinkedHashMap<ClassReference, Value<D>>();
                result.map.put(singleton.getMatlabClass(),singleton);                
            } else {
                result = new ValueSet<D>(this);
            }
            result.map.put(v.getMatlabClass(), v);
            result.size = map.size();
        }
        return result;
    }
    
    @Override
    public Iterator<Value<D>> iterator() {
        if (size > 1){
            return map.values().iterator();
        } else {
            return values().iterator();
        }
    }
    
    public Collection<Value<D>> values(){
        if (size > 1){
            return map.values();
        } else {
            List<Value<D>> list = new ArrayList<Value<D>>(size);
            if (size == 1) list.add(singleton);
            return list;
        }
    }
    public Value<D> get(ClassReference aClass){
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
    
    
    @Override
    public ValueSet<D> merge(ValueSet<D> other) {
        System.out.println(this+" "+other);
        ValueSet<D> result;
        //clone the bigger set
        if (size() > other.size()){
            result = new ValueSet<D>(this);
        } else {
            result = new ValueSet<D>(other);
            other = this;
        }
        if (other.size == 0) return result;
        System.out.println(result+" "+other);
        //merge elements from the smaller set
        if (result.size == 1){
            if (other.size == 1){
                if (result.singleton.getMatlabClass().equals(other.singleton.getMatlabClass())){
                    result.singleton = result.singleton.merge(other.singleton);
                } else {
                    result.map = new LinkedHashMap<ClassReference, Value<D>>(3); //|result| = |size| = 1
                    result.map.put(result.singleton.getMatlabClass(), result.singleton);
                    result.singleton = null;
                    Value<D> v = other.singleton;
                    result.map.put(v.getMatlabClass(), v);
                    result.size = 2;
                }
            }
        } else { // |result| > 1
            if (other.size == 1){
                Value<D> v = other.singleton;
                ClassReference key = v.getMatlabClass();
                if (result.hasMatlabClass(key)){ //exist in both sets - merge
                    result.map.put(key,other.map.get(key).merge(v));
                } else { //put new element
                    result.map.put(key, v);
                }
            } else {
                for (Value<D> v : other){
                    ClassReference key = v.getMatlabClass();
                    if (result.hasMatlabClass(key)){ //exist in both sets - merge
                        result.map.put(key,other.map.get(key).merge(v));
                    } else { //put new element
                        result.map.put(key, v);
                    }
                }
            }
            result.size = map.size();
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
    public static  <D extends MatrixValue<D>> List<LinkedList<Value<D>>> cross(
            ValueSet<D>... sets){
        return cross(Arrays.asList(sets));
    }
    public static  <D extends MatrixValue<D>> List<LinkedList<Value<D>>> cross(
            List<ValueSet<D>> list){
        return cross(list.iterator());
    }
    @SuppressWarnings("unchecked")
    public static <D extends MatrixValue<D>> List<LinkedList<Value<D>>> cross(
            Iterator<ValueSet<D>> i){
        if (!i.hasNext()) return null;
        //get set, (others)
        ValueSet<D> set = i.next();
        List<LinkedList<Value<D>>> rec = cross(i);
        //compute set x others
        if (rec == null){
            //base case just return the set of elements
            LinkedList<LinkedList<Value<D>>> result = new LinkedList<LinkedList<Value<D>>>();
            for (Value v : set){
                result.add(new LinkedList<Value<D>>());
                result.peekLast().add(v);
            }
            return result;
        } else {
            LinkedList<LinkedList<Value<D>>> result = new LinkedList<LinkedList<Value<D>>>();            
            //recursive case 
            for (LinkedList<Value<D>> currentList : rec){
                for (Value v : set){
                    LinkedList<Value<D>> newList = ((LinkedList<Value<D>>)currentList.clone());
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
}
