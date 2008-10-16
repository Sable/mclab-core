//A trivial lexer for determining the length of a string, as reported by JFlex
package matlab;

import java.io.*;

%%

//general header info
%public
%final
%class LengthScanner

%unicode
%function getEOFPosition
%type TextPosition

//track line and column
%line
%column

%{
public static TextPosition getLength(String text) {
    StringReader reader = new StringReader(text);
    LengthScanner scanner = new LengthScanner(reader);
    TextPosition eofPos = null;
    try {
        eofPos = scanner.getEOFPosition();
        reader.close();
    } catch(IOException e) {
        //can't happen since StringReader
        e.printStackTrace();
        throw new RuntimeException(e);
    }
    return eofPos;
}
%}

%%

. | \n {}
<<EOF>> { return new TextPosition(yyline + 1, yycolumn + 1); }