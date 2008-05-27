package natlab;

/**
 * An integer literal specified in base 10.
 */
public class DecIntNumericLiteralValue extends IntNumericLiteralValue {
	public DecIntNumericLiteralValue(String text) {
		this(text, false);
	}

	public DecIntNumericLiteralValue(String text, boolean isImaginary) {
		super(text, Integer.parseInt(stripImaginary(text, isImaginary)), isImaginary);
	}
}
