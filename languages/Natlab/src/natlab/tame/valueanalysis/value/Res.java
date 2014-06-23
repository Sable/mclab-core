package natlab.tame.valueanalysis.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import natlab.tame.valueanalysis.ValueSet;
import natlab.toolkits.analysis.Mergable;

/**
 * combining multiple Values into a list, as used by Results
 * This is a ordered collection of ValueSets
 * 
 */

public class Res<V extends Value<V>> extends ArrayList<ValueSet<V>> 
         implements Mergable<Res<V>> {
    boolean isViable = true;
    
    public static <V extends Value<V>> Res<V> newInstance(V aValue){
        Res<V> result = new Res<V>();
        result.add(ValueSet.newInstance(aValue));
        return result;
    }

    public static <V extends Value<V>> Res<V> newInstance(ValueSet<V> aValueSet){
        Res<V> result = new Res<V>();
        result.add(aValueSet);
        return result;
    }

    public static <V extends Value<V>> Res<V> newInstance(){
        return new Res<V>();
    }
    
    public static <V extends Value<V>> Res<V> newInstance(boolean isViable){
        Res<V> result =  new Res<V>();
        result.isViable = isViable;
        return result;
    }
    
    
    /**
     * copy
     */
    public static <V extends Value<V>> Res<V> newInstance(Res<V> other){
        Res<V> result =  new Res<V>();
        result.isViable = other.isViable;
        result.addAll(other);
        return result;
    }
    
    
    /**
     * returns a non-viable result set, that is, an error result
     */
    public static <V extends Value<V>> Res<V> newErrorResult(String message){
        Res<V> result =  new Res<V>();
        result.isViable = false;
        return result;
    }
    
    
    private Res() {
    }

    private Res(int capacity) {
        super(capacity);
    }

    
    /**
     * returns a Res object, which is the result of merging all the given
     * res objects. If there is none, returns a nonViable result
     */
    public static <V extends Value<V>> Res<V> newInstance(Collection<Res<V>> valueSets){
        if (valueSets.size() == 0){
            return newInstance(false);
        } else {
            Iterator<Res<V>> i = valueSets.iterator();
            Res<V> result = i.next();
            while(i.hasNext()){
            	result = result.merge(i.next());
            }
            return result;
        }
    }

    @Override
    public Res<V> merge(Res<V> o) {
        if (!isViable || !o.isViable){
            //TODO merge errors
            return isViable?this:o;
        }
        if (this.size() != o.size()){
            throw new UnsupportedOperationException("mreging results with different number of results");
        } else {
            Res<V> result = new Res<V>(this.size());
            for (int i = 0; i < this.size(); i++){
                result.add(this.get(i).merge(o.get(i)));
            }
            return result;
        }
    }
    
    /**
     * returns true if this result is not viable
     */
    public boolean isViable(){
        return isViable;
    }
    
    @Override
    public String toString() {
        if (!isViable) return "[not viable]";
        return super.toString();
    }
    
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Res<?>){
            Res<?> oRes = (Res<?>)arg0;
            return (isViable==oRes.isViable) &&
                ((isViable)?super.equals(arg0):true);
        } else {
            return false;
        }
    }
}





