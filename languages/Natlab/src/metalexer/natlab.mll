package natlab;
import static natlab.NatlabParser.Terminals.*;
%%
import beaver.Symbol;
import beaver.Scanner;
%%
%layout natlab

%option class "%class NatlabScanner"
%option function "%function nextToken"

%declare "CommentBuffer commentBuffer"
//methods are for external use only so don't declare
%{//// Comment queue ///////////////////////////////////////////////////////////
  
  //put comments in the buffer rather than returning them
  //NB: must be non-null before scanning starts
  private CommentBuffer commentBuffer = null;
  
  public void setCommentBuffer(CommentBuffer commentBuffer) {
      this.commentBuffer = commentBuffer;
  }
  
  public CommentBuffer getCommentBuffer() {
      return commentBuffer;
  }
%}

%declare "int storedLine"
%declare "int storedCol"
%{//// Position info for commas and semicolons /////////////////////////////////
  
  private int storedLine = -1;
  private int storedCol = -1;
%}

%component leading_ws
%component base
%component class
%component class_bracketed
%component field_name
%component comma_terminator
%component semicolon_terminator
%component string
%component transpose
%component bracket_comment
%component bracket_help_comment

%start base

%%
%%inherit helper_beaver

//// Leading whitespace ////////////////////////////////////////////////////////

%%embed
%name leading_ws
%host base
%guest leading_ws
%start <BOF>
%end END_LEADING_WS

//// Class /////////////////////////////////////////////////////////////////////

%%embed
%name class
%host base
%guest class
%start START_CLASS
%end END
%pair OPEN_END, END

%%embed
%name class_brackets
%host class
%guest class_bracketed
%start START_BRACKETED
%end [END_BRACKETED END_BRACKETED_START_TRANSPOSE]
%pair START_BRACKETED, END_BRACKETED
%pair START_BRACKETED, END_BRACKETED_START_TRANSPOSE

//// Field name ////////////////////////////////////////////////////////////////

%%embed
%name field_name
%host base, class
%guest field_name
%start START_FIELD_NAME
%end [END_FIELD_NAME END_FIELD_NAME_START_TRANSPOSE]

//// Transpose /////////////////////////////////////////////////////////////////

%%embed
%name transpose
%host base, class
%guest transpose
%start [START_TRANSPOSE END_BRACKETED_START_TRANSPOSE]
%end END_TRANSPOSE

%%embed
%name field_name_transpose
%host field_name
%guest transpose
%start END_FIELD_NAME_START_TRANSPOSE
%end END_TRANSPOSE

//// Strings ///////////////////////////////////////////////////////////////////

%%embed
%name string
%host base, class
%guest string
%start START_STRING
%end END_STRING

//// Terminators ///////////////////////////////////////////////////////////////

%%embed
%name comma_terminator
%host base, class, class_bracketed
%guest comma_terminator
%start START_COMMA_TERMINATOR
%end END_COMMA_TERMINATOR

%%embed
%name semicolon_terminator
%host base, class, class_bracketed
%guest semicolon_terminator
%start START_SEMICOLON_TERMINATOR
%end END_SEMICOLON_TERMINATOR

//// Comments //////////////////////////////////////////////////////////////////

%%embed
%name bracket_comment
%host bracket_comment, bracket_help_comment, base, class, class_bracketed, field_name, comma_terminator, semicolon_terminator
%guest bracket_comment
%start START_BRACKET_COMMENT
%end END_BRACKET_COMMENT

%%embed
%name bracket_help_comment
%host base, class, class_bracketed, field_name, comma_terminator, semicolon_terminator
%guest bracket_help_comment
%start START_BRACKET_HELP_COMMENT
%end END_BRACKET_COMMENT

%%embed
%name eof_error
%host bracket_comment, bracket_help_comment, class_bracketed, class, string
%guest base
%start EOF_ERROR
%end <ANY> //NB: never happens
