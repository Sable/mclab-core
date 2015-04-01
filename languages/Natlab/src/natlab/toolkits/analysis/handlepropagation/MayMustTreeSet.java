package natlab.toolkits.analysis.handlepropagation;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An implementation of TreeSet that adds a flag for may or must. An
 * example use is to keep track of a function handle must refer to one
 * target in the set or only may.
 */
public class MayMustTreeSet<E> extends TreeSet<E>
{

    protected boolean must = true;
    
    public MayMustTreeSet()
    {
        super();
    }
    public MayMustTreeSet( boolean must )
    {
        super();
        this.must = must;
    }
    public MayMustTreeSet( Collection<? extends E> c, boolean must )
    {
        super(c);
        this.must = must;
    }
    public MayMustTreeSet( Comparator<? super E> comparator)
    {
        super(comparator);
        this.must = must;
    }
    public MayMustTreeSet( SortedSet<E> s )
    {
        super( s );
        this.must = must;
    }

    public boolean isMay()
    {
        return !must;
    }
    public boolean isMust()
    {
        return must;
    }
    public void makeMay()
    {
        must = false;
    }
    public void makeMust()
    {
        must = true;
    }

    public MayMustTreeSet<E> clone()
    {
        return new MayMustTreeSet<E>(this, must);
    }

    public void addAll( MayMustTreeSet<E> o )
    {
        super.addAll(o);
        if( o.isMay() )
            makeMay();
    }
}