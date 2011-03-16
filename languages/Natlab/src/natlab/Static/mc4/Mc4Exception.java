package natlab.Static.mc4;

/**
 * This is runtime except that gets thrown by the mc4 compiler.
 * Note that since this is a runtime exception, no try/catch is necessary.
 * 
 * @author ant6n
 *
 */


public class Mc4Exception extends RuntimeException {
	public Mc4Exception(String message){
		super("Mc4 Runtime Exception: "+message);		
	}
}
