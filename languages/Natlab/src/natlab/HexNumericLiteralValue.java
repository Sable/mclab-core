package natlab;

import java.math.BigInteger;

/**
 * An integer literal specified in base 16.
 */
public class HexNumericLiteralValue extends IntNumericLiteralValue {
	public HexNumericLiteralValue(String text) {
		this(text, false);
	}

	public HexNumericLiteralValue(String text, boolean isImaginary) {
		super(text, parseHex(stripImaginary(text, isImaginary)), isImaginary);
	}

	private static BigInteger parseHex(String text) {
		if(!text.startsWith("0x")) {
			throw new NumberFormatException("Hex literals must begin with '0x': " + text);
		}
		return new BigInteger(text.substring(2), 16);
	}
}
