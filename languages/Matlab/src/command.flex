package matlab;

import matlab.CommandToken.Arg;
import matlab.CommandToken.EllipsisComment;

%%

//general header info
%public
%final
%class CommandScanner

//required for beaver compatibility
%unicode
%function nextToken
%type CommandToken
%yylexthrow CommandException

//for debugging - track line and column
%line
%column

%{
  //// Base position ///////////////////////////////////////////////////////////
  
  private int baseLine = 1;
  private int baseCol = 1;
  
  //NB: baseCol only lasts until the first line break
  public void setBasePosition(int baseLine, int baseCol) {
    this.baseLine = baseLine;
    this.baseCol = baseCol;
  }
  
  public int getBaseLine() {
    return baseLine;
  }
  
  public int getBaseCol() {
    return baseCol;
  }
  
  //// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with position information from JFlex
  private void error(String msg) throws CommandException {
    //correct to one-indexed
    throw new CommandException(yyline + baseLine, yycolumn + baseCol, msg);
  }
  
  //// Command-style call arguments ////////////////////////////////////////////
  
  private Arg arg = new Arg();
  
  //populate the line and start column fields of the Arg with values from JFlex
  private void markStart() {
    //correct to one-indexed
    arg.setLine(yyline + baseLine);
    arg.setStartCol(yycolumn + baseCol);
  }
  
  //populate the end column field of the Arg with values from JFlex
  private void markPotentialEnd() {
    arg.setEndCol((yycolumn + baseCol) + yylength() - 1);
  }
  
  private CommandToken getAndRestartArg() {
    Arg result = arg;
    arg = new Arg();
    return result;
  }
  
  private CommandToken handleNonArg(CommandToken nonArg) throws CommandException {
    if(arg.isQuoteCountOdd()) {
        error("Unterminated command-style call argument: '" + arg.getText() + "'");
        return null; //unreachable, but Java can't tell that error always throws an exception
    } else {
        if(!arg.isArgTextEmpty()) {
            yypushback(yylength()); //will rematch after we return the arg
            return getAndRestartArg();
        }
        return nonArg;
    }
  }
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

//NB: acceptable to conflict with ... - matlab just treats .... as a comment containing .
Ellipsis = \.\.\.
EscapedLineTerminator = {Ellipsis}.*{LineTerminator}

%%

{EscapedLineTerminator} {
    CommandToken tok = new EllipsisComment(yytext());
    tok.setLine(yyline + baseLine);
    tok.setStartCol(yycolumn + baseCol);
    tok.setEndCol(yycolumn + baseCol + yylength() - 1);
    baseCol = 1; //NB: reset base col after the first line break
    return handleNonArg(tok);
}

{OtherWhiteSpace}+ {
    if(arg.isQuoteCountOdd()) {
        arg.appendText(yytext());
        arg.appendArgText(yytext());
        markPotentialEnd();
    } else if(!arg.isArgTextEmpty()) {
        return getAndRestartArg();
    }
}

. {
    if(arg.getText().length() == 0) {
        markStart();
    }
    if(yytext().equals("'")) {
        arg.incrQuoteCount();
        if(arg.isPrevTextCharQuote() && arg.isQuoteCountOdd()) {
            arg.appendArgText("''");
        }
    } else {
        arg.appendArgText(yytext());
    }
    arg.appendText(yytext());
    markPotentialEnd();
}

<<EOF>> { return handleNonArg(null); }