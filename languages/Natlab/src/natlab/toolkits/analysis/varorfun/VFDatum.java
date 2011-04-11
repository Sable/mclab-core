package natlab.toolkits.analysis.varorfun;
/**
 * Datums used by var or function analysis.
 */

public enum VFDatum
{
    TOP, VAR, PREFIX, FUN, LDVAR, BOT, WAR;
    public VFDatum merge(VFDatum ov){
        if( this == ov )
            return this;

        if( BOT == this ) 
            return ov;

	if (BOT == ov)
            return this;

	if (LDVAR == this) 
	    return ov;

	if (LDVAR == ov)
	    return this;
	if (this==WAR || ov == WAR )
		return WAR;

	return TOP;
    }
    public boolean isFunction(){return this==FUN;}
    public boolean isVariable(){return this==VAR;}
    public boolean isID(){return this==BOT || this==LDVAR;}
}