package natlab.toolkits.analysis.varorfun;

import java.util.*;
import natlab.toolkits.analysis.*;

/**
 * Special implementation of FlowSet for the varorfun analysis. This
 * implementation uses a HashTable to store the set detail. The set
 * consists of identifier names associated with VFDatum objects. It is
 * a set in terms of the names, rather than (name,datum) pairs
 *
 * @see VFDatum
 */

/*public class VFFlowset<D extends VFDatum> extends AbstractFlowSet<D>
{

    protected HashMap<String, D>
*/