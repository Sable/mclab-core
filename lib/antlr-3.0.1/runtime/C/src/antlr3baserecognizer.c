/** \file
 * Contains the base functions that all recognizers start out with.
 * Any function can be overridden by a lexer/parser/tree parser or by the
 * ANTLR3 programmer.
 * 
 */
#include    <antlr3baserecognizer.h>

#ifdef	WIN32
#pragma warning( disable : 4100 )
#endif

/* Interface functions -stanadard implemenations cover parser and treeparser
 * almost completely but are overriden by parser or tree paresr as needed. Lexer overrides
 * most of these functions.
 */
static void			beginResync		    (pANTLR3_BASE_RECOGNIZER recognizer);
static pANTLR3_BITSET		computeErrorRecoverySet	    (pANTLR3_BASE_RECOGNIZER recognizer);
static void			endResync		    (pANTLR3_BASE_RECOGNIZER recognizer);
static ANTLR3_BOOLEAN		match			    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow);
static void			matchAny		    (pANTLR3_BASE_RECOGNIZER recognizer);
static void			mismatch		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow);
static void			reportError		    (pANTLR3_BASE_RECOGNIZER recognizer);
static pANTLR3_BITSET		computeCSRuleFollow	    (pANTLR3_BASE_RECOGNIZER recognizer);
static pANTLR3_BITSET		combineFollows		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_BOOLEAN exact);
static void			displayRecognitionError	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_UINT8 * tokenNames);
static void			recover			    (pANTLR3_BASE_RECOGNIZER recognizer);
static void			recoverFromMismatchedToken  (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow);
static void			recoverFromMismatchedSet    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET follow);
static ANTLR3_BOOLEAN		recoverFromMismatchedElement(pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET follow);
static void			consumeUntil		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 tokenType);
static void			consumeUntilSet		    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET set);
static pANTLR3_STACK		getRuleInvocationStack	    (pANTLR3_BASE_RECOGNIZER recognizer);
static pANTLR3_STACK		getRuleInvocationStackNamed (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_UINT8 name);
static pANTLR3_HASH_TABLE	toStrings		    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_HASH_TABLE);
static ANTLR3_UINT64		getRuleMemoization	    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex, ANTLR3_UINT64 ruleParseStart);
static ANTLR3_BOOLEAN		alreadyParsedRule	    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex);
static void			memoize			    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex, ANTLR3_UINT64 ruleParseStart);
static ANTLR3_BOOLEAN		synpred			    (pANTLR3_BASE_RECOGNIZER recognizer, void * ctx, void (*predicate)(void * ctx));
static void			reset			    (pANTLR3_BASE_RECOGNIZER recognizer);
static void			freeBR			    (pANTLR3_BASE_RECOGNIZER recognizer);

ANTLR3_API pANTLR3_BASE_RECOGNIZER
antlr3BaseRecognizerNew(ANTLR3_UINT32 type, ANTLR3_UINT32 sizeHint)
{
    pANTLR3_BASE_RECOGNIZER recognizer;

    /* Allocate memory for the structure
     */
    recognizer	    = (pANTLR3_BASE_RECOGNIZER) ANTLR3_MALLOC((size_t)sizeof(ANTLR3_BASE_RECOGNIZER));

    if	(recognizer == NULL)
    {
	/* Allocation failed
	 */
	return	(pANTLR3_BASE_RECOGNIZER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Install the BR API
     */
    recognizer->alreadyParsedRule	    =  alreadyParsedRule;
    recognizer->beginResync		    =  beginResync;
    recognizer->combineFollows		    =  combineFollows;
    recognizer->computeCSRuleFollow	    =  computeCSRuleFollow;
    recognizer->computeErrorRecoverySet	    =  computeErrorRecoverySet;
    recognizer->consumeUntil		    =  consumeUntil;
    recognizer->consumeUntilSet		    =  consumeUntilSet;
    recognizer->displayRecognitionError	    =  displayRecognitionError;
    recognizer->endResync		    =  endResync;
    recognizer->exConstruct		    =  antlr3MTExceptionNew;
    recognizer->getRuleInvocationStack	    =  getRuleInvocationStack;
    recognizer->getRuleInvocationStackNamed =  getRuleInvocationStackNamed;
    recognizer->getRuleMemoization	    =  getRuleMemoization;
    recognizer->match			    =  match;
    recognizer->matchAny		    =  matchAny;
    recognizer->memoize			    =  memoize;
    recognizer->mismatch		    =  mismatch;
    recognizer->recover			    =  recover;
    recognizer->recoverFromMismatchedElement=  recoverFromMismatchedElement;
    recognizer->recoverFromMismatchedSet    =  recoverFromMismatchedSet;
    recognizer->recoverFromMismatchedToken  =  recoverFromMismatchedToken;
    recognizer->reportError		    =  reportError;
    recognizer->reset			    =  reset;

    recognizer->synpred			    =  synpred;
    recognizer->toStrings		    =  toStrings;

    recognizer->free			    =  freeBR;

    /* Initialize variables
     */
    recognizer->type		= type;
    recognizer->errorRecovery	= ANTLR3_FALSE;
    recognizer->lastErrorIndex	= -1;
    recognizer->failed		= ANTLR3_FALSE;
    recognizer->errorCount	= 0;
    recognizer->backtracking	= 0;
    recognizer->following	= NULL;
    recognizer->_fsp		= -1;
    recognizer->ruleMemo	= NULL;
    recognizer->tokenNames	= NULL;
    recognizer->sizeHint	= sizeHint;

    return  recognizer;
}
static void	
freeBR	    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    pANTLR3_EXCEPTION thisE;

    if	(recognizer->ruleMemo != NULL)
    {
	recognizer->ruleMemo->free(recognizer->ruleMemo);
    }

    thisE = recognizer->exception;
    if	(thisE != NULL)
    {
	thisE->freeEx(thisE);
    }

    ANTLR3_FREE(recognizer);
}

/**
 * \brief
 * Creates a new Mismatched Token Exception and inserts in the recognizer
 * exception stack.
 * 
 * \param recognizer
 * Context pointer for this recognizer
 * 
 */
ANTLR3_API	void
antlr3MTExceptionNew(pANTLR3_BASE_RECOGNIZER recognizer)
{
    /* Create a basic recognition exception structure
     */
    antlr3RecognitionExceptionNew(recognizer);

    /* Now update it to indicate this is a Mismatched token exception
     */
    recognizer->exception->name		= ANTLR3_MISMATCHED_EX_NAME;
    recognizer->exception->type		= ANTLR3_MISMATCHED_TOKEN_EXCEPTION;

    return;
}

ANTLR3_API	void
antlr3RecognitionExceptionNew(pANTLR3_BASE_RECOGNIZER recognizer)
{
	pANTLR3_EXCEPTION		    ex;
	pANTLR3_LEXER		    lexer;
	pANTLR3_PARSER		    parser;
	pANTLR3_TREE_PARSER		    tparser;

	pANTLR3_INPUT_STREAM	    ins;
	pANTLR3_INT_STREAM		    is;
	pANTLR3_COMMON_TOKEN_STREAM	    cts;
	pANTLR3_TREE_NODE_STREAM	    tns;

	ins	    = NULL;
	cts	    = NULL;
	tns	    = NULL;
	is	    = NULL;
	lexer   = NULL;
	parser  = NULL;
	tparser = NULL;

	switch	(recognizer->type)
	{
	case	ANTLR3_TYPE_LEXER:

		lexer	= (pANTLR3_LEXER) (recognizer->super);
		ins	= lexer->input;
		is	= ins->istream;

		break;

	case	ANTLR3_TYPE_PARSER:

		parser  = (pANTLR3_PARSER) (recognizer->super);
		cts	= (pANTLR3_COMMON_TOKEN_STREAM)(parser->tstream->super);
		is	= parser->tstream->istream;

		break;

	case	ANTLR3_TYPE_TREE_PARSER:

		tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
		tns	= tparser->ctnstream->tnstream;
		is	= tns->istream;

		break;

	default:

		fprintf(stderr, "Base recognizer function antlr3RecognitionExceptionNew called by unknown parser type - provide override for this function\n");
		return;

		break;
	}

	/* Create a basic exception structure
	 */
	ex = antlr3ExceptionNew(ANTLR3_RECOGNITION_EXCEPTION,
		(void *)ANTLR3_RECOGNITION_EX_NAME,
		NULL,
		ANTLR3_FALSE);

	/* Rest of information depends on the base type of the 
	 * input stream.
	 */
	switch  (is->type & ANTLR3_INPUT_MASK)
	{
	case    ANTLR3_CHARSTREAM:

		ex->c			= is->_LA		    	(is, 1);					/* Current input character			*/
		ex->line		= ins->getLine			(ins);						/* Line number comes from stream		*/
		ex->charPositionInLine	= ins->getCharPositionInLine	(ins);	    /* Line offset also comes from the stream   */
		ex->index		= is->index			(is);
		ex->streamName		= ins->fileName;
		ex->message		= "Unexpected character";
		break;

	case    ANTLR3_TOKENSTREAM:

		ex->token		= cts->tstream->_LT						(cts->tstream, 1);	    /* Current input token			    */
		ex->line		= ((pANTLR3_COMMON_TOKEN)(ex->token))->getLine			(ex->token);
		ex->charPositionInLine	= ((pANTLR3_COMMON_TOKEN)(ex->token))->getCharPositionInLine	(ex->token);
		ex->index		= cts->tstream->istream->index					(cts->tstream->istream);
		if	(((pANTLR3_COMMON_TOKEN)(ex->token))->type == ANTLR3_TOKEN_EOF)
		{
			ex->streamName		= NULL;
		}
		else
		{
			ex->streamName		= ((pANTLR3_COMMON_TOKEN)(ex->token))->input->fileName;
		}
		ex->message		= "Unexpected token";
		break;

	case    ANTLR3_COMMONTREENODE:

		ex->token		= tns->_LT						    (tns, 1);	    /* Current input tree node			    */
		ex->line		= ((pANTLR3_BASE_TREE)(ex->token))->getLine		    (ex->token);
		ex->charPositionInLine	= ((pANTLR3_BASE_TREE)(ex->token))->getCharPositionInLine   (ex->token);
		ex->index		= tns->istream->index					    (tns->istream);

		// Are you ready for this? Deep breath now...
		//
		{
			pANTLR3_COMMON_TREE tnode;

			tnode		= ((pANTLR3_COMMON_TREE)(((pANTLR3_BASE_TREE)(ex->token))->super));

			if	(tnode->token    == NULL)
			{
				ex->streamName = ((pANTLR3_BASE_TREE)(ex->token))->strFactory->newStr(((pANTLR3_BASE_TREE)(ex->token))->strFactory, (pANTLR3_UINT8)"-unknown source-");
			}
			else
			{
				if	(tnode->token->input == NULL)
				{
					ex->streamName		= tnode->token->text->factory->newStr8(tnode->token->text->factory, (pANTLR3_UINT8)"-Imaginary-");
				}
				else
				{
					ex->streamName		= tnode->token->input->fileName;
				}
			}
			ex->message		= "Unexpected node";
		}
		break;
	}

	ex->input		    = is;
	ex->nextException	    = recognizer->exception;	/* So we don't leak the memory */
	recognizer->exception   = ex;
	recognizer->error	    = ANTLR3_TRUE;	    /* Exception is outstanding	*/

	return;
}


/** Match current input symbol against ttype.  Upon error, do one token
 *  insertion or deletion if possible.  You can override to not recover
 *  here and bail out of the current production to the normal error
 *  exception catch (at the end of the method) by just throwing
 *  MismatchedTokenException upon input._LA(1)!=ttype.
 */
static ANTLR3_BOOLEAN
match(	pANTLR3_BASE_RECOGNIZER recognizer,
		ANTLR3_UINT32 ttype, pANTLR3_BITSET follow)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function 'match' called by unknown parser type - provide override for this function\n");
	return ANTLR3_FALSE;

	break;
    }

    if	(is->_LA(is, 1) == ttype)
    {
	/* The token was the one we were told to expect
	 */
	is->consume(is);				/* Consume that token from the stream	    */
	recognizer->errorRecovery   = ANTLR3_FALSE;	/* Not in error recovery now (if we were)   */
	recognizer->failed	    = ANTLR3_FALSE;	/* The match was a success		    */
	return ANTLR3_TRUE;				/* We are done				    */
    }

    /* We did not find the expected token type, if we are backtracking then
     * we just set the failed flag and return.
     */
    if	(recognizer->backtracking > 0)
    {
	/* Backtracking is going on
	 */
	recognizer->failed  = ANTLR3_TRUE;
	return ANTLR3_FALSE;
    }

    /* We did not find the expected token and there is no backtracking
     * going on, so we mismatch, which creates an exception in the recognizer exception
     * stack.
     */
    recognizer->mismatch(recognizer, ttype, follow);

    return ANTLR3_FALSE;
}

/**
 * \brief
 * Consumes the next token whatever it is and resets the recognizer state
 * so that it is not in error.
 * 
 * \param recognizer
 * Recognizer context pointer
 */
static void
matchAny(pANTLR3_BASE_RECOGNIZER recognizer)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function 'matchAny' called by unknown parser type - provide override for this function\n");
	return;

	break;
    }
    recognizer->errorRecovery	    = ANTLR3_FALSE;
    recognizer->failed		    = ANTLR3_FALSE;
    is->consume(is);

    return;
}

/**
 * \remark Mismatch only works for parsers and must be overridden for anything else.
 */
static	void
mismatch(pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    /* Install a mismatched token exception in the exception stack
     */
    antlr3MTExceptionNew(recognizer);
    recognizer->exception->expecting    = ttype;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function 'mismatch' called by unknown parser type - provide override for this function\n");
	return;

	break;
    }



    /* Enter error recovery mode
     */
    recognizer->recoverFromMismatchedToken(recognizer, ttype, follow);

    return;

}
static void			
reportError		    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    if	(recognizer->errorRecovery == ANTLR3_TRUE)
    {
	/* In error recovery so don't display another error while doing so
	 */
	return;
    }

    /* Signal we are in error recovery now
     */
    recognizer->errorRecovery = ANTLR3_TRUE;

    recognizer->displayRecognitionError(recognizer, recognizer->tokenNames);
}

static void			
beginResync		    (pANTLR3_BASE_RECOGNIZER recognizer)
{
}

static void			
endResync		    (pANTLR3_BASE_RECOGNIZER recognizer)
{
}

/**
 * Documentation below is from the Java implementation.
 *
 * Compute the error recovery set for the current rule.  During
 *  rule invocation, the parser pushes the set of tokens that can
 *  follow that rule reference on the stack; this amounts to
 *  computing FIRST of what follows the rule reference in the
 *  enclosing rule. This local follow set only includes tokens
 *  from within the rule; i.e., the FIRST computation done by
 *  ANTLR stops at the end of a rule.
 *
 *  EXAMPLE
 *
 *  When you find a "no viable alt exception", the input is not
 *  consistent with any of the alternatives for rule r.  The best
 *  thing to do is to consume tokens until you see something that
 *  can legally follow a call to r *or* any rule that called r.
 *  You don't want the exact set of viable next tokens because the
 *  input might just be missing a token--you might consume the
 *  rest of the input looking for one of the missing tokens.
 *
 *  Consider grammar:
 *
 *  a : '[' b ']'
 *    | '(' b ')'
 *    ;
 *  b : c '^' INT ;
 *  c : ID
 *    | INT
 *    ;
 *
 *  At each rule invocation, the set of tokens that could follow
 *  that rule is pushed on a stack.  Here are the various "local"
 *  follow sets:
 *
 *  FOLLOW(b1_in_a) = FIRST(']') = ']'
 *  FOLLOW(b2_in_a) = FIRST(')') = ')'
 *  FOLLOW(c_in_b) = FIRST('^') = '^'
 *
 *  Upon erroneous input "[]", the call chain is
 *
 *  a -> b -> c
 *
 *  and, hence, the follow context stack is:
 *
 *  depth  local follow set     after call to rule
 *    0         <EOF>                    a (from main())
 *    1          ']'                     b
 *    3          '^'                     c
 *
 *  Notice that ')' is not included, because b would have to have
 *  been called from a different context in rule a for ')' to be
 *  included.
 *
 *  For error recovery, we cannot consider FOLLOW(c)
 *  (context-sensitive or otherwise).  We need the combined set of
 *  all context-sensitive FOLLOW sets--the set of all tokens that
 *  could follow any reference in the call chain.  We need to
 *  resync to one of those tokens.  Note that FOLLOW(c)='^' and if
 *  we resync'd to that token, we'd consume until EOF.  We need to
 *  sync to context-sensitive FOLLOWs for a, b, and c: {']','^'}.
 *  In this case, for input "[]", LA(1) is in this set so we would
 *  not consume anything and after printing an error rule c would
 *  return normally.  It would not find the required '^' though.
 *  At this point, it gets a mismatched token error and throws an
 *  exception (since LA(1) is not in the viable following token
 *  set).  The rule exception handler tries to recover, but finds
 *  the same recovery set and doesn't consume anything.  Rule b
 *  exits normally returning to rule a.  Now it finds the ']' (and
 *  with the successful match exits errorRecovery mode).
 *
 *  So, you cna see that the parser walks up call chain looking
 *  for the token that was a member of the recovery set.
 *
 *  Errors are not generated in errorRecovery mode.
 *
 *  ANTLR's error recovery mechanism is based upon original ideas:
 *
 *  "Algorithms + Data Structures = Programs" by Niklaus Wirth
 *
 *  and
 *
 *  "A note on error recovery in recursive descent parsers":
 *  http://portal.acm.org/citation.cfm?id=947902.947905
 *
 *  Later, Josef Grosch had some good ideas:
 *
 *  "Efficient and Comfortable Error Recovery in Recursive Descent
 *  Parsers":
 *  ftp://www.cocolab.com/products/cocktail/doca4.ps/ell.ps.zip
 *
 *  Like Grosch I implemented local FOLLOW sets that are combined
 *  at run-time upon error to avoid overhead during parsing.
 */
static pANTLR3_BITSET		
computeErrorRecoverySet	    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    return   recognizer->combineFollows(recognizer, ANTLR3_FALSE);
}

/** Compute the context-sensitive FOLLOW set for current rule.
 *  This is the set of token types that can follow a specific rule
 *  reference given a specific call chain.  You get the set of
 *  viable tokens that can possibly come next (look ahead depth 1)
 *  given the current call chain.  Contrast this with the
 *  definition of plain FOLLOW for rule r:
 *
 *   FOLLOW(r)={x | S=>*alpha r beta in G and x in FIRST(beta)}
 *
 *  where x in T* and alpha, beta in V*; T is set of terminals and
 *  V is the set of terminals and non terminals.  In other words,
 *  FOLLOW(r) is the set of all tokens that can possibly follow
 *  references to r in *any* sentential form (context).  At
 *  runtime, however, we know precisely which context applies as
 *  we have the call chain.  We may compute the exact (rather
 *  than covering superset) set of following tokens.
 *
 *  For example, consider grammar:
 *
 *  stat : ID '=' expr ';'      // FOLLOW(stat)=={EOF}
 *       | "return" expr '.'
 *       ;
 *  expr : atom ('+' atom)* ;   // FOLLOW(expr)=={';','.',')'}
 *  atom : INT                  // FOLLOW(atom)=={'+',')',';','.'}
 *       | '(' expr ')'
 *       ;
 *
 *  The FOLLOW sets are all inclusive whereas context-sensitive
 *  FOLLOW sets are precisely what could follow a rule reference.
 *  For input input "i=(3);", here is the derivation:
 *
 *  stat => ID '=' expr ';'
 *       => ID '=' atom ('+' atom)* ';'
 *       => ID '=' '(' expr ')' ('+' atom)* ';'
 *       => ID '=' '(' atom ')' ('+' atom)* ';'
 *       => ID '=' '(' INT ')' ('+' atom)* ';'
 *       => ID '=' '(' INT ')' ';'
 *
 *  At the "3" token, you'd have a call chain of
 *
 *    stat -> expr -> atom -> expr -> atom
 *
 *  What can follow that specific nested ref to atom?  Exactly ')'
 *  as you can see by looking at the derivation of this specific
 *  input.  Contrast this with the FOLLOW(atom)={'+',')',';','.'}.
 *
 *  You want the exact viable token set when recovering from a
 *  token mismatch.  Upon token mismatch, if LA(1) is member of
 *  the viable next token set, then you know there is most likely
 *  a missing token in the input stream.  "Insert" one by just not
 *  throwing an exception.
 */
static pANTLR3_BITSET		
computeCSRuleFollow	    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    return   recognizer->combineFollows(recognizer, ANTLR3_FALSE);
}

static pANTLR3_BITSET		
combineFollows		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_BOOLEAN exact)
{
    pANTLR3_BITSET	followSet;
    pANTLR3_BITSET	localFollowSet;
    ANTLR3_UINT64	top;
    ANTLR3_UINT64	i;

    top	= recognizer->following->size(recognizer->following);

    followSet	    = antlr3BitsetNew(0);

    for (i = top; i>0; i--)
    {
	localFollowSet = (pANTLR3_BITSET) recognizer->following->get(recognizer->following, i);

	if  (localFollowSet != NULL)
	{
	    followSet->orInPlace(followSet, localFollowSet);
	}

	if	(      exact == ANTLR3_TRUE
		    && localFollowSet->isMember(localFollowSet, ANTLR3_EOR_TOKEN_TYPE) == ANTLR3_FALSE
		)
	{
	    break;
	}
    }

    followSet->remove(followSet, ANTLR3_EOR_TOKEN_TYPE);

    return  followSet;
}

static void			
displayRecognitionError	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_UINT8 * tokenNames)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;
    pANTLR3_STRING	    ttext;
    pANTLR3_STRING	    ftext;
    pANTLR3_EXCEPTION	    ex;
    pANTLR3_COMMON_TOKEN    theToken;
    pANTLR3_BASE_TREE	    theBaseTree;
    pANTLR3_COMMON_TREE	    theCommonTree;
    
    // Retrieve some info for easy reading.
    //
    ex	    =		recognizer->exception;
    ttext   =		NULL;

    /* Indicate this recognizer had an error while processing.
     */
    recognizer->errorCount++;

    // See if there is a 'filename' we can use
    //
    if	(ex->streamName == NULL)
    {
		if	(((pANTLR3_COMMON_TOKEN)(ex->token))->type == ANTLR3_TOKEN_EOF)
		{
			fprintf(stderr, "-end of input-(");
		}
		else
		{
			fprintf(stderr, "-unknown source-(");
		}
    }
    else
    {
	ftext = ex->streamName->to8(ex->streamName);
	fprintf(stderr, "%s(", ftext->chars);
    }
    
    // Next comes the line number
    //
#ifdef ANTLR3_WIN64
    // shnazzle fraazzle Dick Dastardly
    //
    fprintf(stderr, "%I64d) ", recognizer->exception->line);
#else
#ifdef ANTLR3_USE_64BIT
    fprintf(stderr, "%lld) ", recognizer->exception->line);
#else
    fprintf(stderr, "%d) ", recognizer->exception->line);
#endif
#endif

    fprintf(stderr, " : error %d : %s", 
					    recognizer->exception->type,
		    (pANTLR3_UINT8)	   (recognizer->exception->message));
					    

    /* How we determine the next piece is dependent on which thing raised the
     * error.
     */
    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	// Prepare the knowledge we know we have
	//
	parser	    = (pANTLR3_PARSER) (recognizer->super);
	tparser	    = NULL;
	is	    = parser->tstream->istream;
	theToken    = (pANTLR3_COMMON_TOKEN)(recognizer->exception->token);
	ttext	    = theToken->toString(theToken);

	fprintf(stderr, ", at offset %d", recognizer->exception->charPositionInLine);
	if  (theToken != NULL)
	{
	    if (theToken->type == ANTLR3_TOKEN_EOF)
	    {
		fprintf(stderr, ", at <EOF>");
	    }
	    else
	    {
		fprintf(stderr, "\n    near %s\n    ", ttext->chars);
	    }
	}
	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser		= (pANTLR3_TREE_PARSER) (recognizer->super);
	parser		= NULL;
	is		= tparser->ctnstream->tnstream->istream;
	theBaseTree	= (pANTLR3_BASE_TREE)(recognizer->exception->token);
	ttext		= theBaseTree->toStringTree(theBaseTree);

	if  (theBaseTree != NULL)
	{
	    theCommonTree	= (pANTLR3_COMMON_TREE)	    theBaseTree->super;

	    if	(theCommonTree != NULL)
	    {
		theToken	= (pANTLR3_COMMON_TOKEN)    theCommonTree->getToken(theBaseTree);
	    }
	    fprintf(stderr, ", at offset %d", theBaseTree->getCharPositionInLine(theBaseTree));
	    fprintf(stderr, ", near %s", ttext->chars);
	}
	break;

    default:
	    
	fprintf(stderr, "Base recognizer function displayRecognitionError called by unknown parser type - provide override for this function\n");
	return;
	break;
    }

    // Although this function should generally be provided by the implementation, this one
    // should be as helpful as possible for grammar developers and serve as an example
    // of what you can do with each exception type. In general, when you make up your
    // 'real' handler, you should debug the routine with all possible errors you expect
    // which will then let you be as specific as possible about all circumstances.
    //
    // Note that in the general case, errors thrown by tree parsers indicate a problem
    // with the output of the parser or with the tree grammar itself. Th job of the parser
    // is to produce a perfect (in traversal terms) syntactically correct tree, so errors
    // at that stage should really be semantic errors that your own code determines and handles
    // in whatever way is appropriate.
    //
    switch  (ex->type)
    {
	case	ANTLR3_RECOGNITION_EXCEPTION:
	
	    // Indicates that the recognizer received a token
	    // in the input that was not predicted. This is the basic exception type 
	    // from which all others are derived. So we assume it was a syntax error.
	    // You may get this if there are not more tokens and more are needed
	    // to complete a parse for instance.
	    //
	    fprintf(stderr, " : syntax error...\n");    
	    break;

        case    ANTLR3_MISMATCHED_TOKEN_EXCEPTION:

	    // We were expecting to see one thing and got another. This is
	    // most common error, and here you can spend your efforts to
	    // derive more useful error messages based on the expected
	    // token set and the last token and so on. The error following
	    // bitmaps do a good job of reducing the set that we were looking
	    // for down to something small. Knowing what you are parsing may be
	    // able to allow you to be even more specific about an error.
	    //
	    if	(tokenNames == NULL)
	    {
		fprintf(stderr, " : syntax error...\n");
	    }
	    else
	    {
		fprintf(stderr, " : expected %s ...\n", tokenNames[ex->expecting]);
	    }
	    break;

	case	ANTLR3_NO_VIABLE_ALT_EXCEPTION:

	    // We could not pick any alt decision from the input given
	    // so god knows what happened - however when you examine your grammar,
	    // you should. It means that at the point where the current token occurred
	    // that the DFA indicates nowhere to go from here.
	    //
	    fprintf(stderr, " : cannot match to any predicted input...\n");
	    
	    break;

	case	ANTLR3_MISMATCHED_SET_EXCEPTION:

	    {
		ANTLR3_UINT32	  count;
		ANTLR3_UINT32	  bit;
		ANTLR3_UINT32	  size;
		ANTLR3_UINT32	  numbits;

		// This means we were able to deal with one of a set of
		// possible tokens at this point, but we did not see any
		// member of that set.
		//
		fprintf(stderr, " : unexpected input...\n  expected one of : ");
		
		// What tokens could we have accepted at this point in the
		// parse?
		//
		count   = 0;
		numbits = ex->expectingSet->numBits	(ex->expectingSet);
		size    = ex->expectingSet->size	(ex->expectingSet);

		if  (size > 0)
		{
		    // However many tokens we could have dealt with here, it is usually
		    // not useful to print ALL of the set here. I arbitrarily chose 8
		    // here, but you should do whatever makes sense for you of course.
		    // No token number 0, so look for bit 1 and on.
		    //
		    for	(bit = 1; bit < numbits && count < 8 && count < size; bit++)
		    {
			if  (tokenNames[bit])
			{
			    fprintf(stderr, "%%s", count > 0 ? ", " : "", tokenNames[bit]); 
			    count++;
			}
		    }
		    fprintf(stderr, "\n");
		}
		else
		{
		    fprintf(stderr, "Actually dude, we didn't seem to be expecting anything here, or at least\n");
		    fprintf(stderr, "I could not work out what I was expecting, like so many of us these days!\n");
		}
	    }
	    break;

	case	ANTLR3_EARLY_EXIT_EXCEPTION:

	    // We entered a loop requiring a number of token sequences
	    // but found a token that ended that sequence earlier than
	    // we should have done.
	    //
	    fprintf(stderr, " : missing elements...\n");
	    break;

	default:

	    // We don't handle any other exceptions here, but you can
	    // if you wish. If we get an exception that hits this point
	    // then we are just going to report what we know about the
	    // token.
	    //
	    fprintf(stderr, " : syntax not recognized...\n");
	    break;
    }

    // Here you have the token that was in error which if this is
    // the standard implementation will tell you the line and offset
    // and also record the address of the start of the line in the
    // input stream. You could therefore print the source line and so on.
    // Generally though, I would expect that your lexer/parser will keep
    // its own map of lines and source pointers or whatever as there
    // are a lot of specific things you need to know about the input
    // to do something like that.
    // Here is where you do it though :-).
    //
}

/** Recover from an error found on the input stream.  Mostly this is
 *  NoViableAlt exceptions, but could be a mismatched token that
 *  the match() routine could not recover from.
 */
static void			
recover			    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    /* Used to compute the follow set of tokens
    */
    pANTLR3_BITSET	    followSet;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function recover called by unknown parser type - provide override for this function\n");
	return;

	break;
    }

    /* I know that all the indirection looks confusing, but you get used to it and it really isn't.
     * Don't be tempted to use macros like we do for the generated C code, you will never know
     * what is going on. The generated C code does this to hide implementation details not clarify them.
     */
    if	(recognizer->lastErrorIndex == is->index(is))
    {
	/* The last error was at the same token index point. This must be a case
	 * where LT(1) is in the recovery token set so nothing is
	 * consumed. Consume a single token so at least to prevent
	 * an infinite loop; this is a failsafe.
	 */
	is->consume(is);
    }

    /* Record error index position
     */
    recognizer->lastErrorIndex	 = is->index(is);
    
    /* Work out the follows set for error recovery
     */
    followSet	= recognizer->computeErrorRecoverySet(recognizer);

    /* Call resync hook (for debuggers and so on)
     */
    recognizer->beginResync(recognizer);

    /* Consume tokens until we have resynced to something in the follows set
     */
    recognizer->consumeUntilSet(recognizer, followSet);

    /* End resync hook 
     */
    recognizer->endResync(recognizer);

    /* Destoy the temporary bitset we produced.
     */
    followSet->free(followSet);

    /* Reset the in error bit so we don't re-report the exception
     */
    recognizer->error	= ANTLR3_FALSE;
    recognizer->failed  = ANTLR3_FALSE;
}


/** Attempt to recover from a single missing or extra token.
 *
 *  EXTRA TOKEN
 *
 *  LA(1) is not what we are looking for.  If LA(2) has the right token,
 *  however, then assume LA(1) is some extra spurious token.  Delete it
 *  and LA(2) as if we were doing a normal match(), which advances the
 *  input.
 *
 *  MISSING TOKEN
 *
 *  If current token is consistent with what could come after
 *  ttype then it is ok to "insert" the missing token, else throw
 *  exception For example, Input "i=(3;" is clearly missing the
 *  ')'.  When the parser returns from the nested call to expr, it
 *  will have call chain:
 *
 *    stat -> expr -> atom
 *
 *  and it will be trying to match the ')' at this point in the
 *  derivation:
 *
 *       => ID '=' '(' INT ')' ('+' atom)* ';'
 *                          ^
 *  match() will see that ';' doesn't match ')' and report a
 *  mismatched token error.  To recover, it sees that LA(1)==';'
 *  is in the set of tokens that can follow the ')' token
 *  reference in rule atom.  It can assume that you forgot the ')'.
 *
 * May need to come back and look at the exception stuff here, I am assuming 
 * that the exception that was passed in in the java implementation is
 * sorted in the recognizer exception stack. To 'throw' it we set the
 * error flag and rules can cascade back when this is set.
 */
static void			
recoverFromMismatchedToken  (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function recoverFromMismatchedToken called by unknown parser type - provide override for this function\n");
	return;

	break;
    }

    /* If the next token after the one we are looking at in the input stream
     * is what we are looking for then we remove the one we have discovered
     * from the stream by consuming it, then consume this next one along too as
     * if nothing had happened.
     */
    if	( is->_LA(is, 2) == ttype)
    {
	/* Print out the error
	 */
	recognizer->reportError(recognizer);

	/* Call resync hook (for debuggeres and so on)
	 */
	recognizer->beginResync(recognizer);

	/* "delete" the extra token
	 */
	is->consume(is);

	/* End resync hook 
	 */
	recognizer->endResync(recognizer);

	/* consume the token that the rule actually expected to get
	 */
	is->consume(is);

	recognizer->error  = ANTLR3_FALSE;	/* Exception is not outstanding any more */

    }

    /* The next token (after the one that is current, is not the one
     * that we were expecting, so the input is in more of an error state
     * than we hoped. 
     * If we are able to recover from the error using the follow set, then
     * we are hunky dory again and can move on, if we cannot, then we resort
     * to throwing the exception.
     */
    if	(recognizer->recoverFromMismatchedElement(recognizer, follow) == ANTLR3_FALSE)
    {
	recognizer->error	    = ANTLR3_TRUE;
	recognizer->failed	    = ANTLR3_TRUE;
	return;
    }
}

static void		
recoverFromMismatchedSet	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET follow)
{
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function recoverFromMismatchedSet called by unknown parser type - provide override for this function\n");
	return;

	break;
    }

    /* TODO - Single token deletion like in recoverFromMismatchedToken()
     */
    if	(recognizer->recoverFromMismatchedElement(recognizer, follow) == ANTLR3_FALSE)
    {
	recognizer->error	= ANTLR3_TRUE;
	recognizer->failed	= ANTLR3_TRUE;
	return;
    }
}

/** This code is factored out from mismatched token and mismatched set
 *  recovery.  It handles "single token insertion" error recovery for
 *  both.  No tokens are consumed to recover from insertions.  Return
 *  true if recovery was possible else return false.
 */
static ANTLR3_BOOLEAN	
recoverFromMismatchedElement	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET follow)
{
    pANTLR3_BITSET	    viableToksFollowingRule;
    pANTLR3_BITSET	    newFollow;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function recover called by unknown parser type - provide override for this function\n");
	return ANTLR3_FALSE;

	break;
    }

    newFollow	= NULL;

    if	(follow == NULL)
    {
	/* The follow set is NULL, which means we don't know what can come 
	 * next, so we "hit and hope" by just signifying that we cannot
	 * recover, which will just cause the next token to be consumed,
	 * which might dig us out.
	 */
	return	ANTLR3_FALSE;
    }

    /* We have a bitmap for the follow set, hence we can compute 
     * what can follow this grammar element reference.
     */
    if	(follow->isMember(follow, ANTLR3_EOR_TOKEN_TYPE) == ANTLR3_TRUE)
    {
	/* First we need to know which of the available tokens are viable
	 * to follow this reference.
	 */
	viableToksFollowingRule	= recognizer->computeCSRuleFollow(recognizer);

	/* Knowing that, we can or in the follow set
	 */
	newFollow   = follow->or(follow, viableToksFollowingRule);
	
	/* Remove the EOR token, which we do not wish to compute with
	 */
	newFollow->remove(newFollow, ANTLR3_EOR_TOKEN_TYPE);
	viableToksFollowingRule->free(viableToksFollowingRule);
	/* We now have the computed set of what can follow the current token
	 */
	follow	= newFollow;
    }

    /* We can now see if the current token works with the set of tokens
     * that could follow the current grammar reference. If it looks like it
     * is consistent, then we can "insert" that token by not throwing
     * an exception and assuming that we saw it. 
     */
    if	( follow->isMember(follow, is->_LA(is, 1)) == ANTLR3_TRUE)
    {
	/* report the error, but don't cause any rules to abort and stuff
	 */
	recognizer->reportError(recognizer);
	if	(newFollow != NULL)
	{
		newFollow->free(newFollow);
	}
	recognizer->error			= ANTLR3_FALSE;
	recognizer->failed			= ANTLR3_FALSE;
	return ANTLR3_TRUE;	/* Success in recovery	*/
    }

    if	(newFollow != NULL)
    {
	newFollow->free(newFollow);
    }

    /* We could not find anything viable to do, so this is going to 
     * cause an exception.
     */
    return  ANTLR3_FALSE;
}

/** Eat tokens from the input stream until we get one of JUST the right type
 */
static void		
consumeUntil	(pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 tokenType)
{
    ANTLR3_UINT32	    ttype;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function 'consumeUntil' called by unknown parser type - provide override for this function\n");
	return;

	break;
    }

    /* What do have at the moment?
     */
    ttype	= is->_LA(is, 1);

    /* Start eating tokens until we get to the one we want.
     */
    while   (ttype != ANTLR3_TOKEN_EOF && ttype != tokenType)
    {
	is->consume(is);
	ttype	= is->_LA(is, 1);
    }
}

/** Eat tokens from the input stream until we find one that
 *  belongs to the supplied set.
 */
static void		
consumeUntilSet			    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_BITSET set)
{
    ANTLR3_UINT32	    ttype;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizer function 'consumeUntilSet' called by unknown parser type - provide override for this function\n");
	return;

	break;
    }

    /* What do have at the moment?
     */
    ttype	= is->_LA(is, 1);

    /* Start eating tokens until we get to one we want.
     */
    while   (ttype != ANTLR3_TOKEN_EOF && set->isMember(set, ttype) == ANTLR3_FALSE)
    {
	is->consume(is);
	ttype	= is->_LA(is, 1);
    }
}

/** Return the rule invocation stack (how we got here in the parse.
 *  In the java version Ter just asks the JVM for all the information
 *  but it C we don't get this information, so I am going to do nothing 
 *  right now, but when the generated code is there I will look to see how much 
 *  overhead is involved in pushing and popping this information on rule entry
 *  and exit. It is only good for error reporting and error recovery, though
 *  I don't see that we are using it in error recovery anyway as the context
 *  sensitive recovery just calls the normal recovery functions.
 *  TODO: Consult with Ter on this one as to usefulness, it is easy but do I need it?
 */
static pANTLR3_STACK	
getRuleInvocationStack		    (pANTLR3_BASE_RECOGNIZER recognizer)
{
    return NULL;
}

static pANTLR3_STACK	
getRuleInvocationStackNamed	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_UINT8 name)
{
    return NULL;
}

/** Convenience method for template rewrites - NYI.
 */
static pANTLR3_HASH_TABLE	
toStrings			    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_HASH_TABLE tokens)
{
    return NULL;
}

static	void ANTLR3_CDECL
freeList    (void * list)
{
    ((pANTLR3_LIST)list)->free(list);
}
static	void ANTLR3_CDECL
freeIntTrie    (void * trie)
{
    ((pANTLR3_INT_TRIE)trie)->free((pANTLR3_INT_TRIE)trie);
}


/** Pointer to a function to return whether the rule has parsed input starting at the supplied 
 *  start index before. If the rule has not parsed input starting from the supplied start index,
 *  then it will return ANTLR3_MEMO_RULE_UNKNOWN. If it has parsed from the suppled start point
 *  then it will return the point where it last stopped parsing after that start point.
 *
 * \remark
 * The rule memos are an ANTLR3_LIST of ANTLR3_LISTS, however if this becomes any kind of performance
 * issue (it probably won't, the hash tables are pretty quick) then we could make a special int only
 * version of the table.
 */
static ANTLR3_UINT64	
getRuleMemoization		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex, ANTLR3_UINT64 ruleParseStart)
{
    /* The rule memos are an ANTLR3_LIST of ANTLR3_LIST.
     */
    pANTLR3_INT_TRIE	ruleList;
    ANTLR3_UINT64	stopIndex;
    pANTLR3_TRIE_ENTRY	entry;

    /* See if we have a list in the ruleMemos for this rule, and if not, then create one
     * as we will need it eventually if we are being asked for the memo here.
     */
    entry	= recognizer->ruleMemo->get(recognizer->ruleMemo, (ANTLR3_UINT64)ruleIndex);

    if	(entry == NULL)
    {
	/* Did not find it, so create a new one for it, with a bit depth based on the 
	 * size of the input stream. We need the bit depth to incorporate the number if
	 * bits required to represen the largest possible stop index in the input, which is the
	 * last character. An int stream is free to return the largest 64 bit offset if it has
	 * no idea of the size, but you should remember that this will cause the leftmost
	 * bit match algorithm to run to 63 bits, whcih will be the whole time spent in the trie ;-)
	 */
	ruleList    = antlr3IntTrieNew(63);	/* Depth is theoretically 64 bits, but probably not ;-)	*/

	if (ruleList != (pANTLR3_INT_TRIE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
	{
	    recognizer->ruleMemo->add(recognizer->ruleMemo, (ANTLR3_UINT64)ruleIndex, ANTLR3_HASH_TYPE_STR, 0, ANTLR3_FUNC_PTR(ruleList), freeIntTrie);
	}

	/* We cannot have a stopIndex in a trie we have just created of course
	 */
	return	MEMO_RULE_UNKNOWN;
    }

    ruleList	= (pANTLR3_INT_TRIE) (entry->data.ptr);

    /* See if there is a stop index associated with the supplied start index.
     */
    stopIndex	= 0;

    entry = ruleList->get(ruleList, ruleParseStart);
    if (entry != NULL)
    {
	stopIndex = entry->data.intVal;

    }

    if	(stopIndex == 0)
    {
	return MEMO_RULE_UNKNOWN;
    }

    return  stopIndex;
}

/** Has this rule already parsed input at the current index in the
 *  input stream?  Return ANTLR3_TRUE if we have and ANTLR3_FALSE
 *  if we have not.
 *
 *  This method has a side-effect: if we have seen this input for
 *  this rule and successfully parsed before, then seek ahead to
 *  1 past the stop token matched for this rule last time.
 */
static ANTLR3_BOOLEAN	
alreadyParsedRule		    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex)
{
    ANTLR3_UINT64	stopIndex;
    pANTLR3_LEXER	    lexer;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	lexer	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	lexer	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    case	ANTLR3_TYPE_LEXER:

	lexer	= (pANTLR3_LEXER)   (recognizer->super);
	parser	= NULL;
	tparser	= NULL;
	is	= lexer->input->istream;

    default:
	    
	fprintf(stderr, "Base recognizerfunction 'alreadyParsedRule' called by unknown paresr type - provide override for this function\n");
	return ANTLR3_FALSE;

	break;
    }

    /* See if we have a memo marker for this.
     */
    stopIndex	    = recognizer->getRuleMemoization(recognizer, ruleIndex, is->index(is));

    if	(stopIndex  == MEMO_RULE_UNKNOWN)
    {
	return ANTLR3_FALSE;
    }

    if	(stopIndex == MEMO_RULE_FAILED)
    {
	recognizer->failed = ANTLR3_TRUE;
    }
    else
    {
	is->seek(is, stopIndex+1);
    }

    /* If here then the rule was executed for this input already
     */
    return  ANTLR3_TRUE;
}

/** Record whether or not this rule parsed the input at this position
 *  successfully.
 */
static void		
memoize	(pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ruleIndex, ANTLR3_UINT64 ruleParseStart)
{
    /* The rule memos are an ANTLR3_LIST of ANTLR3_LIST.
     */
    pANTLR3_INT_TRIE	    ruleList;
    pANTLR3_TRIE_ENTRY	    entry;
    ANTLR3_UINT64	    stopIndex;
    pANTLR3_LEXER	    lexer;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    case	ANTLR3_TYPE_LEXER:

	lexer	= (pANTLR3_LEXER)   (recognizer->super);
	parser	= NULL;
	tparser	= NULL;
	is	= lexer->input->istream;

    default:
	    
	fprintf(stderr, "Base recognizerfunction consumeUntilSet called by unknown parser type - provide override for this function\n");
	return;

	break;
    }
    
    stopIndex	= recognizer->failed == ANTLR3_TRUE ? MEMO_RULE_FAILED : is->index(is) - 1;

    entry	= recognizer->ruleMemo->get(recognizer->ruleMemo, (ANTLR3_UINT64)ruleIndex);

    if	(entry != NULL)
    {
	ruleList = (pANTLR3_INT_TRIE)(entry->data.ptr);

	/* If we don't already have this entry, append it. The memoize trie does not
	 * accept duplicates so it won't add it if already there and we just ignore the
	 * return code as we don't care if it is there already.
	 */
	ruleList->add(ruleList, ruleParseStart, ANTLR3_HASH_TYPE_INT, stopIndex, NULL, NULL);
    }
}
/** A syntactic predicate.  Returns true/false depending on whether
 *  the specified grammar fragment matches the current input stream.
 *  This resets the failed instance var afterwards.
 */
static ANTLR3_BOOLEAN	
synpred	(pANTLR3_BASE_RECOGNIZER recognizer, void * ctx, void (*predicate)(void * ctx))
{
    ANTLR3_UINT64   start;
    pANTLR3_PARSER	    parser;
    pANTLR3_TREE_PARSER	    tparser;
    pANTLR3_INT_STREAM	    is;

    switch	(recognizer->type)
    {
    case	ANTLR3_TYPE_PARSER:

	parser  = (pANTLR3_PARSER) (recognizer->super);
	tparser	= NULL;
	is	= parser->tstream->istream;

	break;

    case	ANTLR3_TYPE_TREE_PARSER:

	tparser = (pANTLR3_TREE_PARSER) (recognizer->super);
	parser	= NULL;
	is	= tparser->ctnstream->tnstream->istream;

	break;

    default:
	    
	fprintf(stderr, "Base recognizerfunction 'synPred' called by unknown paresr type - provide override for this function\n");
	return ANTLR3_FALSE;

	break;
    }

    /* Begin backtracking so we can get back to where we started after trying out
     * the syntactic predicate.
     */
    start   = is->mark(is);
    recognizer->backtracking++;

    /* Try the syntactical predicate
     */
    predicate(ctx);

    /* Reset
     */
    is->rewind(is, start);
    recognizer->backtracking--;

    if	(recognizer->failed == ANTLR3_TRUE)
    {
	/* Predicate failed
	 */
	recognizer->failed = ANTLR3_FALSE;
	return	ANTLR3_FALSE;
    }
    else
    {
	/* Predicate was succesful
	 */
	recognizer->failed	= ANTLR3_FALSE;
	return	ANTLR3_TRUE;
    }
}

static void
reset(pANTLR3_BASE_RECOGNIZER recognizer)
{
    if	(recognizer->following != NULL)
    {
	recognizer->following->free(recognizer->following);
    }

    /* Install a new following set
     */
    recognizer->following   = antlr3StackNew(8);
}

#ifdef	WIN32
#pragma warning( default : 4100 )
#endif
