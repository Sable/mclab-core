package fir.codegen;
/* just holds a string 's all */
public class CodeString implements CodeComponent {
	String text;
	public CodeString(String text){ text = new String(text); }
	public String getCodeString(){ return new String(text); }
}

