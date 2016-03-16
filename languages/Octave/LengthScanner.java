 import java.io.*;

 public final class LengthScanner{
    public int line;
    public int column;
    public StringReader in;
    public LengthScanner(StringReader in){
        this.in = in;
    }
    public static TextPosition getLength(String text) {
        StringReader reader = new StringReader(text);
        LengthScanner scanner = new LengthScanner(reader);
        TextPosition eofPos = null;
        //try {
            eofPos = scanner.getEOFPosition();
            reader.close();
        /*} catch(IOException e) {
            //can't happen since StringReader
            e.printStackTrace();
            throw new RuntimeException(e);
        }*/
        return eofPos;
    }
    public TextPosition getEOFPosition(){

        return new TextPosition(0, 0); //TODO VSD: FIX THIS
    }
}