
package natlab.toolkits;
/**
 * An implementation of ValueBox, used for ASTNode 
 * Change Log:
 *  - 2008.06 created by Jun Li
 *  	 
 */
import java.io.Serializable;

/** Reference implementation for ValueBox;  */
public class ASTValueBox implements ValueBox
{
	String value;

    public ASTValueBox(String value)
    {
    	this.value = value;
    }
    public void setValue(String value)
    {
        if(canContainValue(value))
            this.value = value;
        else
            throw new RuntimeException("Box " + this + " cannot contain value: " + value + "(" + value.getClass() + ")" );
    }

    public String getValue()
    {
        return value;
    }
    
    public String toString() { return "VB("+value+")"; }

	public boolean canContainValue(String value) {
		return true;
	}
}





