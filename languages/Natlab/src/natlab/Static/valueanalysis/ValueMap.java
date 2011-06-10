package natlab.Static.valueanalysis;

import java.util.HashMap;

import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.value.*;

/**
 * This represents the map from matlabclass to abstractvalue.
 * In a value analysis, the flowset is a map from variable name to a map of matlab class to abstract value.
 * This class represents that second class.
 * 
 * TODO: this should be made immutable, probably
 * @author adubra
 */

public class ValueMap<T extends AbstractValue> extends HashMap<ClassReference, T>{
    public static final long serialVersionUID = 0;

}
