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

public class Res<D extends MatrixValue<D>> extends ArrayList<ValueSet<D>> 
         implements Mergable<Res<D>> {
    boolean isViable = true;
    
    public static <D extends MatrixValue<D>> Res<D> newInstance(Value<D> aValue){
        Res<D> result = new Res<D>();
        result.add(ValueSet.newInstance(aValue));
        return result;
    }

    public static <D extends MatrixValue<D>> Res<D> newInstance(ValueSet<D> aValueSet){
        Res<D> result = new Res<D>();
        result.add(aValueSet);
        return result;
    }

    public static <D extends MatrixValue<D>> Res<D> newInstance(){
        return new Res<D>();
    }
    
    public static <D extends MatrixValue<D>> Res<D> newInstance(boolean isViable){
        Res<D> result =  new Res<D>();
        result.isViable = isViable;
        return result;
    }
    
    
    /**
     * copy
     */
    public static <D extends MatrixValue<D>> Res<D> newInstance(Res<D> other){
        Res<D> result =  new Res<D>();
        result.isViable = other.isViable;
        result.addAll(other);
        return result;
    }
    
    
    /**
     * returns a non-viable result set, that is, an error result
     */
    public static <D extends MatrixValue<D>> Res<D> newErrorResult(String message){
        Res<D> result =  new Res<D>();
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
    public static <D extends MatrixValue<D>> Res<D> newInstance(Collection<Res<D>> valueSets){
        if (valueSets.size() == 0){
            return newInstance(false);
        } else {
            Iterator<Res<D>> i = valueSets.iterator();
            Res<D> result = i.next();
            while(i.hasNext()) result = result.merge(i.next());
            return result;
        }
    }

    @Override
    public Res<D> merge(Res<D> o) {
        if (!isViable || o.isViable){
            //TODO merge errors
            return isViable?this:o;
        }
        if (this.size() != o.size()){
            throw new UnsupportedOperationException("mreging results with different number of results");
        } else {
            Res<D> result = new Res<D>(this.size());
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





