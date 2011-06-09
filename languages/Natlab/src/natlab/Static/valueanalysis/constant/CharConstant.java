package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.reference.*;

/**
 * This is a char constant (String). This can only store char arrays which are strings,
 * i.e. a [1 n] array of char.
 * @author ant6n
 */
public class CharConstant extends Constant {
    String value;
    
    public CharConstant(String value){
       this.value = value;
    }

    @Override
    public PrimitiveClassReference getClassReference() {
        return PrimitiveClassReference.CHAR;
    }

    @Override
    public String getValue() {
        return value;
    }

}
