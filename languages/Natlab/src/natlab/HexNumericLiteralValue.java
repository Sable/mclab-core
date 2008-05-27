package natlab;

public class HexNumericLiteralValue extends IntNumericLiteralValue {
	public HexNumericLiteralValue(String text) {
		this(text, false);
	}

	public HexNumericLiteralValue(String text, boolean isImaginary) {
		super(text, parseHex(stripImaginary(text, isImaginary)), isImaginary);
	}

	private static Integer parseHex(String text) {
		if(!text.startsWith("0x")) {
			throw new NumberFormatException("Hex literals must begin with '0x': " + text);
		}
		return Integer.parseInt(text.substring(2), 16);
	}
}
