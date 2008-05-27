package natlab;

public abstract class NumericLiteralValue {
	private final String text;
	private final boolean isImaginary;

	public NumericLiteralValue(String text, boolean isImaginary) {
		this.text = text;
		this.isImaginary = isImaginary;
	}

	public String getText() {
		return text;
	}

	public boolean isImaginary() {
		return isImaginary;
	}

	public abstract Number getValue();

	public String toString() {
		return getValue() + (isImaginary ? "i" : "") + " as '" + text + "'";
	}

	protected static String stripImaginary(String text, boolean isImaginary) {
		return isImaginary ? text.substring(0, text.length() - 1) : text;
	}
}
