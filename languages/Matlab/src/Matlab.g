//TODO-AC: I really wanted most of this grammar to be whitespace insensitive,
//  but I couldn't find a way to use whitespace as an element separator

grammar Matlab;

//AC: keep the lexer and the parser together for now because that's the default Antlr layout

options {
    //modify the buffer in place since our changes are small
    output = template;
    rewrite = true;
}

@parser::header {
package matlab;
}

@parser::members {
/* Convert a RecognitionException into a TranslationException. */
private matlab.TranslationProblem makeProblem(RecognitionException e) {
    return makeProblem(getTokenNames(), e);
}

/* Convert a RecognitionException into a TranslationException. */
private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new matlab.TranslationProblem(e.line, e.charPositionInLine + 1, getErrorMessage(e, tokenNames));
}

public static String translate(String text, int baseLine, int baseCol, OffsetTracker offsetTracker, List<matlab.TranslationProblem> problems) {
    return translate(new ANTLRStringStream(text), baseLine, baseCol, offsetTracker, problems);
}

public static String translate(ANTLRStringStream in, int baseLine, int baseCol, OffsetTracker offsetTracker, List<matlab.TranslationProblem> problems) {
    in.setLine(baseLine);
    in.setCharPositionInLine(baseCol - 1); //since antlr columns are 0-based
    MatlabLexer lexer = new MatlabLexer(in);
    TokenRewriteStream tokens = new TokenRewriteStream(lexer);
    MatlabParser parser = new MatlabParser(tokens);
    parser.offsetTracker = offsetTracker;
    try {
        parser.program();
    } catch (RecognitionException e) {
        parser.problems.add(parser.makeProblem(e));
    }
    problems.addAll(lexer.getProblems());
    problems.addAll(parser.getProblems());
    return tokens.toString();
}

private OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));

private final List<matlab.TranslationProblem> problems = new ArrayList<matlab.TranslationProblem>();

public boolean hasProblem() {
    return !problems.isEmpty();
}

public List<matlab.TranslationProblem> getProblems() {
    return java.util.Collections.unmodifiableList(problems);
}

//AC: this is a hackish way to prevent messages from being printed to stderr
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    problems.add(makeProblem(tokenNames, e));
}

private static boolean isBinaryOperator(Token op) {
    switch(op.getType()) {
    case AT:
    case COLON:
    case DOT:
    case PLUS:
    case MINUS:
    case MTIMES:
    case ETIMES:
    case MDIV:
    case EDIV:
    case MLDIV:
    case ELDIV:
    case MPOW:
    case EPOW:
    case LE:
    case GE:
    case LT:
    case GT:
    case EQ:
    case NE:
    case AND:
    case OR:
    case SHORTAND:
    case SHORTOR:
        return true;
    default:
        return false;
    }
}

private static boolean isPrefixOperator(Token op) {
    switch(op.getType()) {
    case PLUS:
    case MINUS:
    case NOT:
    case AT:
        return true;
    default:
        return false;
    }
}

private static boolean isPostfixOperator(Token op) {
    switch(op.getType()) {
    case MTRANSPOSE:
    case ARRAYTRANSPOSE:
        return true;
    default:
        return false;
    }
}

private static boolean isLParen(Token op) {
    return op.getType() == LPAREN;
}

private static boolean isRParen(Token op) {
    return op.getType() == RPAREN;
}

/*
 * Key helper method: used to determine whether or not a FILLER token is meant
 * to be a column delimiter.
 */
private boolean isElementSeparator() {
    if(!(inCurly() || inSquare())) {
        return false;
    }
    Token prevToken = input.LT(-1);
    Token nextToken = input.LT(2); //2, not 1 because we haven't matched the FILLER yet
    switch(nextToken.getType()) {
    case PLUS:
    case MINUS:
        if(input.LA(3) != FILLER) {
            return true;
        }
        break;
    case AT:
        return true;
    }
    return !(isBinaryOperator(prevToken) || isBinaryOperator(nextToken) ||
             isPrefixOperator(prevToken) || isPostfixOperator(nextToken) ||
             isLParen(prevToken) || isRParen(nextToken));
}

//flag so that Antlr doesn't test all fillers with isCompoundStmtHeaderSeparator()
private boolean wantCompoundStmtHeaderSeparator = false;

private boolean isCompoundStmtHeaderSeparator() {
    if(!wantCompoundStmtHeaderSeparator || input.LA(1) != FILLER) {
        return false;
    }
    Token prevToken = input.LT(-1);
    Token nextToken = input.LT(2); //2, not 1 because we haven't matched the FILLER yet
    return !(isBinaryOperator(prevToken) || isBinaryOperator(nextToken) ||
             isPrefixOperator(prevToken) || isPostfixOperator(nextToken) ||
             isLParen(prevToken) || isRParen(nextToken));
}

private final java.util.Stack<Integer> bracketStack = new java.util.Stack<Integer>();
private boolean inParens() { return !bracketStack.isEmpty() && bracketStack.peek() == LPAREN; }
private boolean inCurly() { return !bracketStack.isEmpty() && bracketStack.peek() == LCURLY; }
private boolean inSquare() { return !bracketStack.isEmpty() && bracketStack.peek() == LSQUARE; }

private final StringBuffer leadingComments = new StringBuffer();
private int leadingCommentsPos = -1;

private void appendToLeadingComments(String text, int pos) {
    if(leadingComments.length() == 0) {
        leadingCommentsPos = pos;
    }
    leadingComments.append(text);
}

private String insertDeletedComments() {
    if(leadingComments.length() == 0) {
        //no offset changes or advancement
        return "";
    }

    TextPosition missingNewlineAdjustment = new TextPosition(0, 0);

    Token prevTok = input.LT(-1);
    if(prevTok.getType() != LINE_TERMINATOR) {
        TextPosition chompedPrecedingEOFPos = LengthScanner.getLength(prevTok.getText());
        int line = -1;
        int col = chompedPrecedingEOFPos.getColumn() - 1;
        if(chompedPrecedingEOFPos.getLine() == 1) {
            col += input.LT(-1).getCharPositionInLine();
        }
        missingNewlineAdjustment = new TextPosition(line, col);
    }

    boolean leadingCommentsEndWithNewline = true;
    String leadingCommentsString = leadingComments.toString();
    String chompedLeadingComments = chomp(leadingCommentsString);
    if(chompedLeadingComments == null) {
        leadingCommentsEndWithNewline = false;
        chompedLeadingComments = leadingCommentsString;
    }
    TextPosition chompedLeadingCommentsEOFPos = LengthScanner.getLength(chompedLeadingComments);

    //introduce fudge factor
    offsetTracker.recordOffsetChange(-1 * missingNewlineAdjustment.getLine(), -1 * missingNewlineAdjustment.getColumn());

    if(leadingCommentsEndWithNewline) {
        offsetTracker.recordOffsetChange(-1 * (chompedLeadingCommentsEOFPos.getLine() + 1), Math.max(0, leadingCommentsPos));
        offsetTracker.advanceByTextSize(leadingCommentsString); //for text (NB: not chomped)

        offsetTracker.recordOffsetChange(-1, chompedLeadingCommentsEOFPos.getColumn() - 1);
        offsetTracker.advanceToNewLine(1, 1); //for inserted newline
        offsetTracker.recordOffsetChange(1, 0);
    } else {
        offsetTracker.recordOffsetChange(-1 * chompedLeadingCommentsEOFPos.getLine(), Math.max(0, leadingCommentsPos));
        offsetTracker.advanceByTextSize(leadingCommentsString); //for text (NB: not chomped)

        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1); //for inserted newline
        offsetTracker.recordOffsetChange(0, 0);
    }

    //cancel fudge factor
    offsetTracker.recordOffsetChange(missingNewlineAdjustment.getLine(), missingNewlineAdjustment.getColumn());

    String commentString = leadingCommentsString + "\n";
    leadingComments.setLength(0);
    return commentString;
}

private static String chomp(String original) {
    if(original.endsWith("\r\n")) {
        return original.substring(0, original.length() - 2);
    } else if(original.endsWith("\r") || original.endsWith("\n")) {
        return original.substring(0, original.length() - 1);
    } else {
        return null;
    }
}
}

@lexer::header {
package matlab;
}

@lexer::members {
private static boolean isPreTransposeChar(int ch) {
    switch(ch) {
    case ')':
    case '}':
    case ']':
    case '_': //at the end of an identifier
    case '\'': //at the end of a transpose
               //NB: if this was at the end of a string, it would have been an escape instead of a close quote
        return true;
    default:
        return Character.isLetterOrDigit(ch); //at the end of an identifier
    }
}

//TODO-AC: This content is duplicated from the parser...maybe it can be factored out
private final List<matlab.TranslationProblem> problems = new ArrayList<matlab.TranslationProblem>();

public boolean hasProblem() {
    return !problems.isEmpty();
}

public List<matlab.TranslationProblem> getProblems() {
    return java.util.Collections.unmodifiableList(problems);
}

//AC: this is a hackish way to prevent messages from being printed to stderr
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    problems.add(makeProblem(tokenNames, e));
}

private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new matlab.TranslationProblem(e.line, e.charPositionInLine + 1, "LEXER: " + getErrorMessage(e, tokenNames));
}

private boolean couldBeFieldName = false;
}

//start symbol
program :
     t_FILLER? EOF //empty
  |  t_FILLER? script script_ending EOF
  |  function_beginning function_list function_ending EOF
  |  function_beginning class_def function_ending EOF
  ;

script :
     (stmt t_FILLER?)+
  ;

script_ending :
     (t_COMMENT | t_BRACKET_COMMENT)?
  ;

stmt :
     stmt_body (stmt_separator | EOF)
  |  stmt_separator
  ;

stmt_separator :
     t_LINE_TERMINATOR
  |  t_COMMENT t_LINE_TERMINATOR
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR
  |  t_SEMICOLON
  |  t_COMMA
  ;

stmt_body :
     maybe_cmd
  |  (t_GLOBAL | t_PERSISTENT) (t_FILLER? name)+ t_FILLER?
  |  t_SHELL_COMMAND t_FILLER?
  |  t_TRY sep_stmt_list (t_CATCH sep_stmt_list)? t_END t_FILLER?
  |  t_SWITCH t_FILLER? expr {wantCompoundStmtHeaderSeparator = true;} compound_stmt_header_sep {wantCompoundStmtHeaderSeparator = false;} (t_CASE t_FILLER? expr sep_stmt_list)* (t_OTHERWISE sep_stmt_list)? t_END t_FILLER?
  |  t_IF t_FILLER? expr sep_stmt_list (t_ELSEIF t_FILLER? expr sep_stmt_list)* (t_ELSE sep_stmt_list)? t_END t_FILLER?
  |  t_BREAK t_FILLER?
  |  t_CONTINUE t_FILLER?
  |  t_RETURN t_FILLER?
  |  t_WHILE t_FILLER? expr sep_stmt_list t_END t_FILLER?
  |  t_FOR t_FILLER? (name t_FILLER? ASSIGN t_FILLER? expr | LPAREN t_FILLER? name t_FILLER? ASSIGN t_FILLER? expr t_FILLER? RPAREN) sep_stmt_list t_END t_FILLER?
  |  t_ANNOTATION t_FILLER?
  ;

maybe_cmd options { k=1; } : //k = 1 forces reliance on the syntactic predicate
     (not_cmd_lookahead)=> expr (t_FILLER? t_ASSIGN t_FILLER? expr)? t_FILLER?
  |  t_IDENTIFIER cmd_args
  ;

//TODO-AC: official doc suggests that this list is not complete but does not elaborate
//lookahead only
not_cmd_lookahead :
     ~IDENTIFIER
  |  IDENTIFIER ~FILLER
  |  IDENTIFIER FILLER LPAREN
  |  IDENTIFIER FILLER ASSIGN
  |  IDENTIFIER FILLER op FILLER after_op
  |  IDENTIFIER EOF
  ;

//lookahead only
op :
     PLUS
  |  MINUS
  |  MTIMES
  |  ETIMES
  |  MDIV
  |  EDIV
  |  MLDIV
  |  ELDIV
  |  MPOW
  |  EPOW
  //not MTRANSPOSE
  //not ARRAYTRANSPOSE
  |  LE
  |  GE
  |  LT
  |  GT
  |  EQ
  |  NE
  |  AND
  |  OR
  // not NOT
  |  SHORTAND
  |  SHORTOR
  |  AT //NB
  |  COLON
  // not DOT
  ;

//lookahead only
after_op :
     IDENTIFIER //includes PROPERTIES, METHODS, EVENTS
  |  NUMBER
  |  LPAREN
  |  LSQUARE
  |  LCURLY
  |  BREAK
  |  CASE
  |  CATCH
  |  CLASSDEF
  |  CONTINUE
  |  ELSE
  |  ELSEIF
  |  END
  |  FOR
  |  FUNCTION
  |  GLOBAL
  |  IF
  |  OTHERWISE
  |  PARFOR
  |  PERSISTENT
  |  RETURN
  |  SWITCH
  |  TRY
  |  WHILE
  |  PLUS
  |  MINUS
  |  NOT
  //not AT
  |  STRING //NB
  ;

cmd_args :
     cmd_args_helper -> template(formatted={CommandFormatter.format($text, $cmd_args_helper.start.getLine(), $cmd_args_helper.start.getCharPositionInLine() + 1, offsetTracker, problems)}) "<formatted>"
  ;

//NB: use actual tokens, not t_ non-terminals
cmd_args_helper :
     FILLER (~(LPAREN | ASSIGN | COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT) cmd_args_tail)?
  |  ~(FILLER | MTRANSPOSE | ARRAYTRANSPOSE | LCURLY | LPAREN | ASSIGN | COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT) cmd_args_tail
  ;

//NB: use actual tokens, not t_ non-terminals
cmd_args_tail :
     (~(COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT))*
  ;

compound_stmt_header_sep :
     stmt_separator t_FILLER?
  |  (FILLER stmt_separator)=> t_FILLER stmt_separator t_FILLER? //don't insert a comma if we don't have to
  |  {isCompoundStmtHeaderSeparator()}? { offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); } t_FILLER -> template(filler={$text}) ",<filler>" //insert comma
  ;

sep_stmt_list :
     {wantCompoundStmtHeaderSeparator = true;} compound_stmt_header_sep {wantCompoundStmtHeaderSeparator = false;} (stmt t_FILLER?)*
  ;

function_list :
     function (function_separator function)*
  ;

function :
     function_body
  ;

function_beginning :
     (((dt_FILLER | dt_LINE_TERMINATOR | dt_COMMENT | dt_BRACKET_COMMENT) { appendToLeadingComments($text, $start.getCharPositionInLine()); }) -> template() "")*
  ;

function_separator :
     {
        //first inserted newline
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1);

        TextPosition eofPos = LengthScanner.getLength(input.LT(-1).getText());

        //second inserted newline
        int startCol = eofPos.getLine() == 1 ? input.LT(-1).getCharPositionInLine() : 0;
        int lineLength = eofPos.getColumn() - 1 + startCol;
        offsetTracker.recordOffsetChange(-1, lineLength - 1);
        offsetTracker.advanceToNewLine(1, 1);

        //fudge factor to point following stuff back to location preceding inserted newlines
        offsetTracker.recordOffsetChange(-1, lineLength);
     }
     ((function_separator_blob { appendToLeadingComments($text, $start.getCharPositionInLine()); }) -> template() "")*
     -> template(gap={"\n\n"}) "<gap>"
  ;

function_separator_blob :
     dt_FILLER
  |  dt_LINE_TERMINATOR
  |  dt_COMMENT dt_LINE_TERMINATOR
  |  dt_BRACKET_COMMENT dt_LINE_TERMINATOR
  ;

function_ending :
     (t_FILLER? stmt_separator)* (t_COMMENT | t_BRACKET_COMMENT)?
  ;

function_body :
     t_FUNCTION (t_FILLER? output_params t_FILLER? t_ASSIGN)? t_FILLER? name t_FILLER? (input_params t_FILLER?)?
     (stmt_separator -> template(original={$text}, extra={insertDeletedComments()}) "<original><extra>")
     (t_FILLER? stmt_or_function)* t_FILLER?
     t_END
  ;

input_params :
     t_LPAREN t_FILLER? (input_param_list t_FILLER?)? t_RPAREN
  ;

input_param_list :
     name_or_tilde (t_FILLER? t_COMMA t_FILLER? name_or_tilde)*
  ;

output_params :
     t_LSQUARE output_param_list t_RSQUARE
  |  name
  ;

output_param_list :
     quiet_element_separator_list? (name_list quiet_element_separator_list?)?
  ;

name_list :
     name (element_separator_list name)*
  ;

stmt_or_function :
     stmt
  |  function_body
  ;

class_def :
     t_CLASSDEF (t_FILLER? attributes)? t_FILLER? t_IDENTIFIER (t_FILLER? t_LT t_FILLER? superclass_list)? fill_sep+ (t_FILLER? class_body)* t_FILLER? t_END
  ;

fill_sep :
     t_FILLER? stmt_separator
  ;

attributes :
     t_LPAREN t_FILLER? attribute (t_FILLER? t_COMMA t_FILLER? attribute)* t_FILLER? t_RPAREN
  ;

attribute :
     t_IDENTIFIER
  |  t_NOT t_FILLER? t_IDENTIFIER
  |  t_IDENTIFIER t_FILLER? ASSIGN t_FILLER? expr
  ;

superclass_list :
     t_IDENTIFIER (t_FILLER? t_AND t_FILLER? t_IDENTIFIER)*
  ;

class_body :
     properties_block fill_sep*
  |  methods_block fill_sep*
  |  events_block fill_sep*
  ;

properties_block :
     t_PROPERTIES (t_FILLER? attributes)? fill_sep+ (t_FILLER? properties_body)* t_FILLER? t_END
  ;

properties_body :
     t_IDENTIFIER (t_FILLER? t_ASSIGN t_FILLER? expr)? fill_sep+
  ;

methods_block :
     t_METHODS (t_FILLER? attributes)? fill_sep+ (t_FILLER? methods_body)* t_FILLER? t_END
  ;

methods_body :
     function
  |  function_signature
  |  property_access fill_sep*
  ;

events_block :
     t_EVENTS (t_FILLER? attributes)? fill_sep+ (t_FILLER? events_body)* t_FILLER? t_END
  ;

events_body :
     t_IDENTIFIER fill_sep+
  ;

function_signature :
     (output_params t_FILLER? t_ASSIGN t_FILLER?)? t_IDENTIFIER (t_FILLER? input_params)? fill_sep+
  ;

property_access :
     t_FUNCTION (t_FILLER? output_params t_FILLER? t_ASSIGN)? t_FILLER? t_IDENTIFIER t_FILLER? t_DOT t_FILLER? t_IDENTIFIER (t_FILLER? input_params)? stmt_separator (t_FILLER? stmt_or_function)* t_FILLER? t_END
  ;

//precedence from: http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
//all binary operators are left associative so no special handling is required
expr :
     short_or_expr
  |  t_AT t_FILLER? input_params t_FILLER? expr
  ;

short_or_expr :
     short_and_expr (t_FILLER? t_SHORTOR t_FILLER? short_and_expr)*
  ;

short_and_expr :
     or_expr (t_FILLER? t_SHORTAND t_FILLER? or_expr)*
  ;

or_expr :
     and_expr (t_FILLER? t_OR t_FILLER? and_expr)*
  ;

and_expr :
     comp_expr (t_FILLER? t_AND t_FILLER? comp_expr)*
  ;

comp_expr :
     colon_expr (t_FILLER? (t_LT | t_GT | t_LE | t_GE | t_EQ | t_NE) t_FILLER? colon_expr)*
  ;

colon_expr :
     plus_expr (t_FILLER? t_COLON t_FILLER? plus_expr (t_FILLER? t_COLON t_FILLER? plus_expr)?)?
  ;

//TODO-AC: antlr periodically has trouble with this (because of unary plus?)
plus_expr :
     binary_expr (t_FILLER? (t_PLUS | t_MINUS) t_FILLER? binary_expr)*
  ;

binary_expr :
     prefix_expr (t_FILLER? (t_MTIMES | t_ETIMES | t_MDIV | t_EDIV | t_MLDIV | t_ELDIV) t_FILLER? prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  t_NOT t_FILLER? prefix_expr
  |  (t_PLUS | t_MINUS) t_FILLER? prefix_expr //NB: plus_expr breaks if this is written as two rules
  ;

pow_expr :
     postfix_expr (t_FILLER? (t_MPOW | t_EPOW) t_FILLER? prefix_postfix_expr)*
  ;

prefix_postfix_expr :
     postfix_expr
  |  (t_NOT | t_PLUS | t_MINUS) t_FILLER? prefix_postfix_expr
  ;

postfix_expr :
     primary_expr (t_FILLER? (t_ARRAYTRANSPOSE | t_MTRANSPOSE))*
  ;

primary_expr :
     literal
  |  t_LPAREN t_FILLER? expr t_FILLER? t_RPAREN
  |  matrix
  |  cell_array
  |  access
  |  t_AT t_FILLER? name
  ;

//TODO-AC: This is separate from expr because it allows t_COLON and t_END
//  This shouldn't be necessary - dynamic scopes should handle this.
//  See revision 709 for an implementation with dynamic scopes.
arg :
     short_or_arg
  |  t_AT t_FILLER? input_params t_FILLER? arg
  |  t_COLON
  ;

short_or_arg :
     short_and_arg (t_FILLER? t_SHORTOR t_FILLER? short_and_arg)*
  ;

short_and_arg :
     or_arg (t_FILLER? t_SHORTAND t_FILLER? or_arg)*
  ;

or_arg :
     and_arg (t_FILLER? t_OR t_FILLER? and_arg)*
  ;

and_arg :
     comp_arg (t_FILLER? t_AND t_FILLER? comp_arg)*
  ;

comp_arg :
     colon_arg (t_FILLER? (t_LT | t_GT | t_LE | t_GE | t_EQ | t_NE) t_FILLER? colon_arg)*
  ;

colon_arg :
     plus_arg (t_FILLER? t_COLON t_FILLER? plus_arg (t_FILLER? t_COLON t_FILLER? plus_arg)?)?
  ;

//TODO-AC: antlr periodically has trouble with this (because of unary plus?)
plus_arg :
     binary_arg (t_FILLER? (t_PLUS | t_MINUS) t_FILLER? binary_arg)*
  ;

binary_arg :
     prefix_arg (t_FILLER? (t_MTIMES | t_ETIMES | t_MDIV | t_EDIV | t_MLDIV | t_ELDIV) t_FILLER? prefix_arg)*
  ;

prefix_arg :
     pow_arg
  |  t_NOT t_FILLER? prefix_arg
  |  (t_PLUS | t_MINUS) t_FILLER? prefix_arg //NB: plus_arg may break if this is written as two rules
  ;

pow_arg :
     postfix_arg (t_FILLER? (t_MPOW | t_EPOW) t_FILLER? prefix_postfix_arg)*
  ;

prefix_postfix_arg :
     postfix_arg
  |  (t_NOT | t_PLUS | t_MINUS) t_FILLER? prefix_postfix_arg
  ;

postfix_arg :
     primary_arg (t_FILLER? (t_ARRAYTRANSPOSE | t_MTRANSPOSE))*
  ;

primary_arg :
     literal
  |  t_LPAREN t_FILLER? arg t_FILLER? t_RPAREN
  |  matrix
  |  cell_array
  |  access
  |  t_AT t_FILLER? name
  |  t_END
  ;

access :
     paren_access (t_FILLER? t_DOT t_FILLER? paren_access)*
  ;

paren_access :
     cell_access (t_FILLER? t_LPAREN t_FILLER? (arg_list t_FILLER?)? t_RPAREN)?
  ;

cell_access :
     name (t_FILLER? t_LCURLY t_FILLER? arg_list t_FILLER? t_RCURLY)*
  |  {!(inSquare() || inCurly())}? name t_FILLER? t_AT t_FILLER? name
  |  {inSquare() || inCurly()}? name t_AT name //TODO-AC: fix error message for name AT FILLER name case
  ;

arg_list :
     arg (t_FILLER? t_COMMA t_FILLER? arg)*
  ;

literal :
     t_NUMBER
  |  t_STRING
  ;

matrix :
     t_LSQUARE optional_row_list t_RSQUARE
  ;

cell_array :
     t_LCURLY optional_row_list t_RCURLY
  ;

optional_row_list :

  |  quiet_element_separator_list
  |  (quiet_element_separator_list? quiet_row_separator)+ quiet_element_separator_list?
  |  (quiet_element_separator_list? quiet_row_separator)* row_list (quiet_row_separator_list quiet_element_separator_list?)?
  ;

row_list :
     row (row_separator_list row)*
  ;

row :
     quiet_element_separator_list? element_list quiet_element_separator_list?
  ;

//non-empty
row_separator_list :
     row_separator (quiet_element_separator_list? quiet_row_separator)*
  ;

//possibly empty
quiet_row_separator_list :
     quiet_row_separator (quiet_element_separator_list? quiet_row_separator)*
  ;

//TODO-AC: allow t_SEMICOLON t_LINE_TERMINATOR
row_separator :
     t_LINE_TERMINATOR
  |  t_SEMICOLON
  |  t_COMMENT t_LINE_TERMINATOR
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR
  ;

quiet_row_separator : //match and delete row_separator
     dt_LINE_TERMINATOR -> template() ""
  |  dt_SEMICOLON -> template() ""
  |  t_COMMENT t_LINE_TERMINATOR //can't delete in this case
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR //can't delete in this case
  ;

element_list :
     element (element_separator_list element)*
  ;

element :
     expr_or_tilde
  ;

expr_or_tilde options { k=2; } :
     expr
  |  t_NOT
  ;

//non-empty
element_separator_list :
     t_FILLER? t_COMMA t_FILLER? //just echo
  |  {isElementSeparator()}? { offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); } t_FILLER -> template(filler={$text}) ",<filler>" //insert comma
  ;

//non-empty
quiet_element_separator_list :
     t_FILLER? quiet_element_separator_comma t_FILLER?
  |  {isElementSeparator()}? t_FILLER
  ;

quiet_element_separator_comma :
     dt_COMMA -> template() "" //delete comma
  ;

name :
     t_IDENTIFIER
  ;

name_or_tilde :
     t_IDENTIFIER
  |  t_NOT
  ;

//// Terminal Rules ////////////////////////////////////////////////////////////

//NB: always call these instead of the real tokens in order to keep the offsetTracker position up to date

//TODO-AC: is it possible to attach these actions to the actual lexer rules?  I don't think the lexer and
//  the parser are kept in sync (buffering, backtracking, etc)

t_BREAK : BREAK { offsetTracker.advanceInLine(5); };
t_CASE : CASE { offsetTracker.advanceInLine(4); };
t_CATCH : CATCH { offsetTracker.advanceInLine(5); };
t_CLASSDEF : CLASSDEF { offsetTracker.advanceInLine(8); };
t_CONTINUE : CONTINUE { offsetTracker.advanceInLine(8); };
t_ELSE : ELSE { offsetTracker.advanceInLine(4); };
t_ELSEIF : ELSEIF { offsetTracker.advanceInLine(6); };
t_END : END { offsetTracker.advanceInLine(3); };
t_FOR : FOR { offsetTracker.advanceInLine(3); };
t_FUNCTION : FUNCTION { offsetTracker.advanceInLine(8); };
t_GLOBAL : GLOBAL { offsetTracker.advanceInLine(6); };
t_IF : IF { offsetTracker.advanceInLine(2); };
t_OTHERWISE : OTHERWISE { offsetTracker.advanceInLine(9); };
t_PARFOR : PARFOR { offsetTracker.advanceInLine(6); };
t_PERSISTENT : PERSISTENT { offsetTracker.advanceInLine(10); };
t_RETURN : RETURN { offsetTracker.advanceInLine(6); };
t_SWITCH : SWITCH { offsetTracker.advanceInLine(6); };
t_TRY : TRY { offsetTracker.advanceInLine(3); };
t_WHILE : WHILE { offsetTracker.advanceInLine(5); };

//TODO-AC: technically there should also be a condition that we're in a class and the preceding non-filler wasn't DOT
//class-specific keywords (IDENTIFIER + predicate)
t_EVENTS : { ((CommonTokenStream) input).LT(1).getText().equals("events") }? IDENTIFIER { offsetTracker.advanceInLine(6); };
t_METHODS : { ((CommonTokenStream) input).LT(1).getText().equals("methods") }? IDENTIFIER { offsetTracker.advanceInLine(7); };
t_PROPERTIES : { ((CommonTokenStream) input).LT(1).getText().equals("properties") }? IDENTIFIER { offsetTracker.advanceInLine(10); };

t_PLUS : PLUS { offsetTracker.advanceInLine(1); };
t_MINUS : MINUS { offsetTracker.advanceInLine(1); };
t_MTIMES : MTIMES { offsetTracker.advanceInLine(1); };
t_ETIMES : ETIMES { offsetTracker.advanceInLine(2); };
t_MDIV : MDIV { offsetTracker.advanceInLine(1); };
t_EDIV : EDIV { offsetTracker.advanceInLine(2); };
t_MLDIV : MLDIV { offsetTracker.advanceInLine(1); };
t_ELDIV : ELDIV { offsetTracker.advanceInLine(2); };
t_MPOW : MPOW { offsetTracker.advanceInLine(1); };
t_EPOW : EPOW { offsetTracker.advanceInLine(2); };
t_MTRANSPOSE : MTRANSPOSE { offsetTracker.advanceInLine(1); };
t_ARRAYTRANSPOSE : ARRAYTRANSPOSE { offsetTracker.advanceInLine(2); };
t_LE : LE { offsetTracker.advanceInLine(2); };
t_GE : GE { offsetTracker.advanceInLine(2); };
t_LT : LT { offsetTracker.advanceInLine(1); };
t_GT : GT { offsetTracker.advanceInLine(1); };
t_EQ : EQ { offsetTracker.advanceInLine(2); };
t_NE : NE { offsetTracker.advanceInLine(2); };
t_AND : AND { offsetTracker.advanceInLine(1); };
t_OR : OR { offsetTracker.advanceInLine(1); };
t_NOT : NOT { offsetTracker.advanceInLine(1); };
t_SHORTAND : SHORTAND { offsetTracker.advanceInLine(2); };
t_SHORTOR : SHORTOR { offsetTracker.advanceInLine(2); };
t_DOT : DOT { offsetTracker.advanceInLine(1); };
t_COMMA : COMMA { offsetTracker.advanceInLine(1); };
t_SEMICOLON : SEMICOLON { offsetTracker.advanceInLine(1); };
t_COLON : COLON { offsetTracker.advanceInLine(1); };
t_AT : AT { offsetTracker.advanceInLine(1); };

t_ASSIGN : ASSIGN { offsetTracker.advanceInLine(1); };

t_LPAREN : LPAREN { offsetTracker.advanceInLine(1); bracketStack.push(LPAREN); };
t_RPAREN : RPAREN { offsetTracker.advanceInLine(1); bracketStack.pop(); };
t_LCURLY : LCURLY { offsetTracker.advanceInLine(1); bracketStack.push(LCURLY); };
t_RCURLY : RCURLY { offsetTracker.advanceInLine(1); bracketStack.pop(); };
t_LSQUARE : LSQUARE { offsetTracker.advanceInLine(1); bracketStack.push(LSQUARE); };
t_RSQUARE : RSQUARE { offsetTracker.advanceInLine(1); bracketStack.pop(); };

t_IDENTIFIER : IDENTIFIER { offsetTracker.advanceByTextSize($text); };
t_NUMBER : NUMBER { offsetTracker.advanceByTextSize($text); };
t_STRING : STRING { offsetTracker.advanceByTextSize($text); };

t_ANNOTATION : ANNOTATION { offsetTracker.advanceByTextSize($text); };

t_BRACKET_COMMENT : BRACKET_COMMENT { offsetTracker.advanceByTextSize($text); };
t_COMMENT : COMMENT { offsetTracker.advanceByTextSize($text); } ->
    template(revised={$text.startsWith("\%\%") ? ("\% " + $text.substring(2)) : $text}) "<revised>";
    //NB: it's safe to do this here because the offsets don't change

t_SHELL_COMMAND : SHELL_COMMAND { offsetTracker.advanceByTextSize($text); };

t_FILLER : FILLER { offsetTracker.advanceByTextSize($text); };

t_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.advanceToNewLine(1, 1); };

//special "delete" versions of the t_TOKEN non-terminals

dt_COMMA : COMMA { offsetTracker.recordOffsetChange(0, 1); };
dt_SEMICOLON : SEMICOLON { offsetTracker.recordOffsetChange(0, 1); };
dt_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.recordOffsetChange($text, $LINE_TERMINATOR.pos + 1, false); };
dt_COMMENT : COMMENT { offsetTracker.recordOffsetChange($text, $COMMENT.pos + 1, false); } ->
    template(revised={$text.startsWith("\%\%") ? ("\% " + $text.substring(2)) : $text}) "<revised>";
    //NB: it's safe to do this here because the offsets don't change

dt_BRACKET_COMMENT : BRACKET_COMMENT { offsetTracker.recordOffsetChange($text, $BRACKET_COMMENT.pos + 1, false); };
dt_FILLER : FILLER { offsetTracker.recordOffsetChange($text, $FILLER.pos + 1, false); };

//// LEXER /////////////////////////////////////////////////////////////////////

//NB: only unconditional keywords - the rest will be treated as identifiers and handled in the parser

BREAK: { !couldBeFieldName }?=> 'break';
CASE: { !couldBeFieldName }?=> 'case';
CATCH: { !couldBeFieldName }?=> 'catch';
CLASSDEF: { !couldBeFieldName }?=> 'classdef';
CONTINUE: { !couldBeFieldName }?=> 'continue';
ELSE: { !couldBeFieldName }?=> 'else';
ELSEIF: { !couldBeFieldName }?=> 'elseif';
END: { !couldBeFieldName }?=> 'end';
FOR: { !couldBeFieldName }?=> 'for';
FUNCTION: { !couldBeFieldName }?=> 'function';
GLOBAL: { !couldBeFieldName }?=> 'global';
IF: { !couldBeFieldName }?=> 'if';
OTHERWISE: { !couldBeFieldName }?=> 'otherwise';
PARFOR: { !couldBeFieldName }?=> 'parfor';
PERSISTENT: { !couldBeFieldName }?=> 'persistent';
RETURN: { !couldBeFieldName }?=> 'return';
SWITCH: { !couldBeFieldName }?=> 'switch';
TRY: { !couldBeFieldName }?=> 'try';
WHILE: { !couldBeFieldName }?=> 'while';

fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT : '0'..'9';
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)* { couldBeFieldName = false; };
fragment INT_NUMBER : DIGIT+;
fragment SCI_EXP : ('d' | 'D' | 'e' | 'E') ('+' | '-')? DIGIT+;
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+);
NUMBER : (INT_NUMBER | FP_NUMBER) SCI_EXP? ('i' | 'I' | 'j' | 'J')? { couldBeFieldName = false; };

PLUS : '+' { couldBeFieldName = false; };
MINUS : '-' { couldBeFieldName = false; };
MTIMES : '*' { couldBeFieldName = false; };
ETIMES : '.*' { couldBeFieldName = false; };
MDIV : '/' { couldBeFieldName = false; };
EDIV : './' { couldBeFieldName = false; };
MLDIV : '\\' { couldBeFieldName = false; };
ELDIV : '.\\' { couldBeFieldName = false; };
MPOW : '^' { couldBeFieldName = false; };
EPOW : '.^' { couldBeFieldName = false; };
MTRANSPOSE : {isPreTransposeChar(input.LA(-1))}?=> '\'' { couldBeFieldName = false; };
ARRAYTRANSPOSE : '.\'' { couldBeFieldName = false; };
LE : '<=' { couldBeFieldName = false; };
GE : '>=' { couldBeFieldName = false; };
LT : '<' { couldBeFieldName = false; };
GT : '>' { couldBeFieldName = false; };
EQ : '==' { couldBeFieldName = false; };
NE : '~=' { couldBeFieldName = false; };
AND : '&' { couldBeFieldName = false; };
OR : '|' { couldBeFieldName = false; };
NOT : '~' { couldBeFieldName = false; };
SHORTAND : '&&' { couldBeFieldName = false; };
SHORTOR : '||' { couldBeFieldName = false; };
DOT : '.' { couldBeFieldName = true; }; //NB: TRUE
COMMA : ',' { couldBeFieldName = false; };
SEMICOLON : ';' { couldBeFieldName = false; };
COLON : ':' { couldBeFieldName = false; };
AT : '@' { couldBeFieldName = false; };
LPAREN : '(' { couldBeFieldName = false; };
RPAREN : ')' { couldBeFieldName = false; };
LCURLY : '{' { couldBeFieldName = false; };
RCURLY : '}' { couldBeFieldName = false; };
LSQUARE : '[' { couldBeFieldName = false; };
RSQUARE : ']' { couldBeFieldName = false; };

ASSIGN : '=' { couldBeFieldName = false; };

//NB: matched AFTER transpose
fragment STRING_CHAR : ~('\'' | '\r' | '\n') | '\'\'';
STRING : {!isPreTransposeChar(input.LA(-1))}?=>'\'' STRING_CHAR* ('\'' | (LINE_TERMINATOR)=> { $type = MISC; }) { couldBeFieldName = false; };

fragment ANNOTATION_FILLER : ~'*' | '*' ~')';
ANNOTATION : '(*' ANNOTATION_FILLER* '*)' { couldBeFieldName = false; };

fragment BRACKET_COMMENT_FILLER : ~'%' | '%' ~('{' | '}');
BRACKET_COMMENT : '%{' BRACKET_COMMENT_FILLER* (BRACKET_COMMENT BRACKET_COMMENT_FILLER*)* '%}';

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : '%' | '%' ~('{' | '\r' | '\n') NOT_LINE_TERMINATOR*;

SHELL_COMMAND : '!' NOT_LINE_TERMINATOR* { couldBeFieldName = false; };

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n' { couldBeFieldName = false; };

fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
FILLER : ((('...')=> ELLIPSIS_COMMENT) | OTHER_WHITESPACE)+; //NB: putting the predicate on the fragment doesn't work

//TODO-AC: this is a huge hack to get around an antlr issue.
//  When the lexer sees lookahead '..', it attempts to match FILLER, regardless of whether or not there's a third '.'
//  If there isn't, it simply fails instead of falling back to match DOT.
//  This prevents that from happening.  It is safe to return a nonsense token because '..' is never a valid sequence in matlab.
//TODO-AC: getting this rule to emit two DOT tokens would be much more satisfactory.
DOUBLE_DOT : '..' { couldBeFieldName = false; };

MISC : . { couldBeFieldName = false; };
