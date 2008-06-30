/** \file
 *
 * Base implementation of an antlr 3 lexer.
 *
 * An ANTLR3 lexer implements a base recongizer, a token source and
 * a lexer interface. It constructs a base recognizer with default
 * functions, then overrides any of these that are parser specific (usual
 * default implementation of base recognizer.
 */
#include    <antlr3lexer.h>

static void					mTokens						(pANTLR3_LEXER lexer);
static void					setCharStream				(pANTLR3_LEXER lexer,  pANTLR3_INPUT_STREAM input);
static void					pushCharStream				(pANTLR3_LEXER lexer,  pANTLR3_INPUT_STREAM input);
static void					popCharStream				(pANTLR3_LEXER lexer);

static void					emitNew						(pANTLR3_LEXER lexer,  pANTLR3_COMMON_TOKEN token);
static pANTLR3_COMMON_TOKEN emit						(pANTLR3_LEXER lexer);
static ANTLR3_BOOLEAN	    matchs						(pANTLR3_LEXER lexer, ANTLR3_UCHAR * string);
static ANTLR3_BOOLEAN	    matchc						(pANTLR3_LEXER lexer, ANTLR3_UCHAR c);
static ANTLR3_BOOLEAN	    matchs_ucase				(pANTLR3_LEXER lexer, ANTLR3_UCHAR * string);
static ANTLR3_BOOLEAN	    matchc_ucase				(pANTLR3_LEXER lexer, ANTLR3_UCHAR c);
static void					setUpperCompare				(pANTLR3_LEXER lexer, ANTLR3_BOOLEAN flag);
static ANTLR3_BOOLEAN	    matchRange					(pANTLR3_LEXER lexer, ANTLR3_UCHAR low, ANTLR3_UCHAR high);
static void					matchAny					(pANTLR3_LEXER lexer);
static void					recover						(pANTLR3_LEXER lexer);
static ANTLR3_UINT64	    getLine						(pANTLR3_LEXER lexer);
static ANTLR3_UINT64	    getCharIndex				(pANTLR3_LEXER lexer);
static ANTLR3_UINT32	    getCharPositionInLine		(pANTLR3_LEXER lexer);
static pANTLR3_STRING	    getText						(pANTLR3_LEXER lexer);
static pANTLR3_COMMON_TOKEN nextToken					(pANTLR3_TOKEN_SOURCE toksource);

static void					displayRecognitionError	    (pANTLR3_BASE_RECOGNIZER rec, pANTLR3_UINT8 * tokenNames);
static void					reportError					(pANTLR3_BASE_RECOGNIZER rec);

static void					reset						(pANTLR3_BASE_RECOGNIZER rec);

static void					freeLexer					(pANTLR3_LEXER lexer);


ANTLR3_API pANTLR3_LEXER
antlr3LexerNew(ANTLR3_UINT32 sizeHint)
{
    pANTLR3_LEXER   lexer;
    pANTLR3_COMMON_TOKEN	eoft;

    /* Allocate memory
     */
    lexer   = (pANTLR3_LEXER) ANTLR3_MALLOC(sizeof(ANTLR3_LEXER));

    if	(lexer == NULL)
    {
	return	(pANTLR3_LEXER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Now we need to create the base recognizer
     */
    lexer->rec	    =  antlr3BaseRecognizerNew(ANTLR3_TYPE_LEXER, sizeHint);

    if	(lexer->rec == (pANTLR3_BASE_RECOGNIZER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	lexer->free(lexer);
	return	(pANTLR3_LEXER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }
    lexer->rec->super  =  lexer;

    lexer->rec->displayRecognitionError	    =  displayRecognitionError;
    lexer->rec->reportError		    =  reportError;
    lexer->rec->reset			    =  reset;

    /* Now install the token source interface
     */
    lexer->tokSource	= (pANTLR3_TOKEN_SOURCE)ANTLR3_MALLOC(sizeof(ANTLR3_TOKEN_SOURCE));

    if	(lexer->tokSource == (pANTLR3_TOKEN_SOURCE) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM)) 
    {
	lexer->rec->free(lexer->rec);
	lexer->free(lexer);

	return	(pANTLR3_LEXER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }
    lexer->tokSource->super    =  lexer;

    /* Install the default nextToken() method, which may be overridden
     * by generated code, or by anything else in fact.
     */
    lexer->tokSource->nextToken	    =  nextToken;
    lexer->tokSource->strFactory    = NULL;

    lexer->tokFactory				= NULL;

    /* Install the lexer API
     */
    lexer->setCharStream			=  setCharStream;
    lexer->mTokens					= (void (*)(void *))(mTokens);
    lexer->setCharStream			=  setCharStream;
    lexer->pushCharStream			=  pushCharStream;
    lexer->popCharStream			=  popCharStream;
    lexer->emit						=  emit;
    lexer->emitNew					=  emitNew;
    lexer->matchs					=  matchs;
    lexer->matchc					=  matchc;
    lexer->matchRange				=  matchRange;
    lexer->matchAny					=  matchAny;
    lexer->recover					=  recover;
    lexer->getLine					=  getLine;
    lexer->getCharIndex				=  getCharIndex;
    lexer->getCharPositionInLine    =  getCharPositionInLine;
    lexer->getText					=  getText;
    lexer->free						=  freeLexer;
    
    /* Initialise the eof token
     */
    eoft		= &(lexer->tokSource->eofToken);	/* Note interfaces allocated with calloc, everything is 0 */
    antlr3SetTokenAPI	  (eoft);
    eoft->setType	  (eoft, ANTLR3_TOKEN_EOF);
    eoft->factoryMade	= ANTLR3_FALSE;
    return  lexer;
}

static void
reset	(pANTLR3_BASE_RECOGNIZER rec)
{
    pANTLR3_LEXER   lexer;

    lexer   = rec->super;

    lexer->token			= NULL;
    lexer->type				= ANTLR3_TOKEN_INVALID;
    lexer->channel			= ANTLR3_TOKEN_DEFAULT_CHANNEL;
    lexer->tokenStartCharIndex		= -1;
    lexer->tokenStartCharPositionInLine = -1;
    lexer->tokenStartLine		= -1;

    lexer->text	    = NULL;

    if (lexer->input != NULL)
    {
	lexer->input->istream->seek(lexer->input->istream, 0);
    }
}

/**
 * \brief
 * Returns the next available token from the current input stream.
 * 
 * \param toksource
 * Points to the implementation of a token source. The lexer is 
 * addressed by the super structure pointer.
 * 
 * \returns
 * The next token in the current input stream or the EOF token
 * if there are no more tokens.
 * 
 * \remarks
 * Write remarks for nextToken here.
 * 
 * \see nextToken
 */
ANTLR3_INLINE static pANTLR3_COMMON_TOKEN
nextTokenStr	    (pANTLR3_TOKEN_SOURCE toksource)
{
    pANTLR3_LEXER   lexer;

    lexer   = (pANTLR3_LEXER)(toksource->super);

    /* Get rid of any previous token (token factory takes care of
     * any de-allocation when this token is finally used up.
     */
    lexer->token		    = NULL;
    lexer->rec->error		    = ANTLR3_FALSE;	    /* Start out without an exception	*/
    lexer->rec->failed		    = ANTLR3_FALSE;

    /* Record the start of the token in our input stream.
     */
    lexer->channel			= ANTLR3_TOKEN_DEFAULT_CHANNEL;
    lexer->tokenStartCharIndex		= lexer->input->istream->index(lexer->input->istream);  
    lexer->tokenStartCharPositionInLine	= lexer->input->getCharPositionInLine(lexer->input);
    lexer->tokenStartLine		= lexer->input->getLine(lexer->input);
    lexer->text				= NULL;

    /* Now call the matching rules and see if we can generate a new token
     */
    for	(;;)
    {
	if  (lexer->input->istream->_LA(lexer->input->istream, 1) == ANTLR3_CHARSTREAM_EOF)
	{
	    /* Reached the end of the current stream, nothing more to do if this is
	     * the last in the stack.
	     */
	    pANTLR3_COMMON_TOKEN    teof = &(toksource->eofToken);
    	
	    teof->setStartIndex (teof, lexer->getCharIndex(lexer));
	    teof->setStopIndex  (teof, lexer->getCharIndex(lexer));
	    teof->setLine	(teof, lexer->getLine(lexer));
	    teof->factoryMade = ANTLR3_TRUE;	// This isn't really manufactured but it stops things from tying to free it
	    return  teof;
	}

	lexer->token			= NULL;
	lexer->rec->error		= ANTLR3_FALSE;	    /* Start out without an exception	*/
	lexer->rec->failed		= ANTLR3_FALSE;

	/* Call the generated lexer, see if it can get a new token together.
	 */
	lexer->mTokens(lexer->ctx);

	if  (lexer->rec->error  == ANTLR3_TRUE)
	{
	    /* Recongition exception, report it and try to recover.
	     */
	    lexer->rec->failed	    = ANTLR3_TRUE;
	    lexer->rec->reportError(lexer->rec);
	    lexer->recover(lexer);
	}
	else
	{
	    if (lexer->token == NULL)
	    {
		emit(lexer);
	    }
	    // TODO: Deal with SKipped token type
	    //
	    return  lexer->token;
	}
    }
}

/**
 * \brief
 * Default implementation of the nextToken() call for a lexer.
 * 
 * \param toksource
 * Points to the implementation of a token source. The lexer is 
 * addressed by the super structure pointer.
 * 
 * \returns
 * The next token in the current input stream or the EOF token
 * if there are no more tokens in any input stream in the stack.
 * 
 * Write detailed description for nextToken here.
 * 
 * \remarks
 * Write remarks for nextToken here.
 * 
 * \see nextTokenStr
 */
static pANTLR3_COMMON_TOKEN
nextToken	    (pANTLR3_TOKEN_SOURCE toksource)
{
    pANTLR3_COMMON_TOKEN tok;

    // Find the next token in the current stream
    //
    tok = nextTokenStr(toksource);

    // If we got to the EOF token then switch to the previous
    // input stream if there were any and just return the
    // EOF if there are none.
    //
    if	(tok->type == ANTLR3_TOKEN_EOF)
    {
	pANTLR3_LEXER   lexer;

	lexer   = (pANTLR3_LEXER)(toksource->super);

	if  (lexer->streams != NULL && lexer->streams->size(lexer->streams) > 0)
	{
	    // We have another input stream in the stack so we
	    // need to revert to it.
	    //
	    lexer->popCharStream(lexer);
	    tok = nextTokenStr(toksource);
	}
    }

    // return whatever token we have, which may be EOF
    //
    return  tok;
}

ANTLR3_API pANTLR3_LEXER
antlr3LexerNewStream(ANTLR3_UINT32 sizeHint, pANTLR3_INPUT_STREAM input)
{
    pANTLR3_LEXER   lexer;

    /* Create a basic lexer first
     */
    lexer   = antlr3LexerNew(sizeHint);

    if	(lexer != (pANTLR3_LEXER)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM)) 
    {
	/* Install the input stream and reset the lexer
	 */
	setCharStream(lexer, input);
    }

    return  lexer;
}

static void mTokens	    (pANTLR3_LEXER lexer)
{
    if	(lexer)	    /* Fool compiler, avoid pragmas */
    {
	fprintf(stderr, "lexer->mTokens(): Error: No lexer rules were added to the lexer yet!\n");
    }
}

static void			
reportError		    (pANTLR3_BASE_RECOGNIZER rec)
{
    rec->displayRecognitionError(rec, rec->tokenNames);
}

#ifdef	WIN32
#pragma warning( disable : 4100 )
#endif

/** Default lexer error handler (works for 8 bit streams only!!!)
 */
static void			
displayRecognitionError	    (pANTLR3_BASE_RECOGNIZER recognizer, pANTLR3_UINT8 * tokenNames)
{
    pANTLR3_LEXER			lexer;
	pANTLR3_EXCEPTION	    ex;
	pANTLR3_STRING			ftext;

    lexer   = (pANTLR3_LEXER)(recognizer->super);
	ex		= lexer->rec->exception;

	// See if there is a 'filename' we can use
    //
    if	(ex->name == NULL)
    {
		fprintf(stderr, "-unknown source-(");
    }
    else
    {
		ftext = ex->streamName->to8(ex->streamName);
		fprintf(stderr, "%s(", ftext->chars);
    }

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

    fprintf(stderr, ": lexer error %d :\n\t%s at offset %d, ", 
						ex->type,
						(pANTLR3_UINT8)	   (ex->message),
					    ex->charPositionInLine+1
		    );
	{
		ANTLR3_INT32	width;

		width	= (ANTLR3_INT32)(ANTLR3_INT64)(((pANTLR3_UINT8)(lexer->input->data)+(lexer->input->size(lexer->input))) - (ANTLR3_INT32)(ex->index));

		if	(width >= 1)
		{			
			if	(isprint(ex->c))
			{
				fprintf(stderr, "near '%c' :\n", ex->c);
			}
			else
			{
				fprintf(stderr, "near char(%#02X) :\n", (ANTLR3_UINT8)(ex->c));
			}
			fprintf(stderr, "\t%.*s\n", width > 20 ? 20 : width ,((pANTLR3_UINT8)ex->index));
		}
		else
		{
			fprintf(stderr, "(end of input).\n\t This indicates a poorly specified lexer RULE\n\t or unterminated input element such as: \"STRING[\"]\n");
			fprintf(stderr, "\t The lexer was matching from line %d, offset %d, which\n\t ", 
								(ANTLR3_UINT32)(lexer->tokenStartLine),
								(ANTLR3_UINT32)(lexer->tokenStartCharPositionInLine)
								);
			width = (ANTLR3_INT32)(ANTLR3_INT64)(((pANTLR3_UINT8)(lexer->input->data)+(lexer->input->size(lexer->input))) - (ANTLR3_INT32)(lexer->tokenStartCharIndex));

			if	(width >= 1)
			{
				fprintf(stderr, "looks like this:\n\t\t%.*s\n", width > 20 ? 20 : width ,(pANTLR3_UINT8)(lexer->tokenStartCharIndex));
			}
			else
			{
				fprintf(stderr, "is also the end of the line, so you must check your lexer rules\n");
			}
		}
	}
}

static void setCharStream   (pANTLR3_LEXER lexer,  pANTLR3_INPUT_STREAM input)
{
    /* Install the input interface
     */
    lexer->input	= input;

    /* We may need a token factory for the lexer; we don't destroy any existing factory
     * until the lexer is destroyed, as people may still be using the tokens it produced.
     * TODO: Later I will provide a dup() method for a token so that it can extract itself
     * out of the factory. 
     */
    if	(lexer->tokFactory == NULL)
    {
	lexer->tokFactory	= antlr3TokenFactoryNew(input);
    }
    else
    {
	/* When the input stream is being changed on the fly, rather than
	 * at the start of a new lexer, then we must tell the tokenFactory
	 * which input stream to adorn the tokens with so that when they
	 * are asked to provide their original input strings they can
	 * do so from the correct text stream.
	 */
	lexer->tokFactory->setInputStream(lexer->tokFactory, input);
    }

    /* Propagate the string factory so that we preserve the encoding form from
     * the input stream.
     */
    if	(lexer->tokSource->strFactory == NULL)
    {
	lexer->tokSource->strFactory	= input->strFactory;
    }

    /* This is a lexer, install the appropriate exception creator
     */
    lexer->rec->exConstruct = antlr3RecognitionExceptionNew;

    /* Set the current token to nothing
     */
    lexer->token		= NULL;
    lexer->text			= NULL;
    lexer->tokenStartCharIndex	= -1;

    /* Copy the name of the char stream to the token source
     */
    lexer->tokSource->fileName = input->fileName;
}

/*!
 * \brief
 * Change to a new input stream, remembering the old one.
 * 
 * \param lexer
 * Pointer to the lexer instance to switch input streams for.
 * 
 * \param input
 * New input stream to install as the current one.
 * 
 * Switches the current character input stream to 
 * a new one, saving the old one, which we will revert to at the end of this 
 * new one.
 */
static void
pushCharStream  (pANTLR3_LEXER lexer,  pANTLR3_INPUT_STREAM input)
{
    // Do we need a new input stream stack?
    //
    if	(lexer->streams == NULL)
    {
	// This is the first call to stack a new
	// stream and so we must create the stack first.
	//
	lexer->streams = antlr3StackNew(0);

	if  (lexer->streams == ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
	{
	    // Could not do this, we just fail to push it.
	    // TODO: Consider if this is what we want to do, but then
	    //       any programmer can override this method to do somthing else.
	    return;
	}
    }

    // We have a stack, so we can save the current input stream
    // into it.
    //
    lexer->input->istream->mark(lexer->input->istream);
    lexer->streams->push(lexer->streams, lexer->input, NULL);

    // And now we can install this new one
    //
    lexer->setCharStream(lexer, input);
}

/*!
 * \brief
 * Stops using the current input stream and reverts to any prior
 * input stream on the stack.
 * 
 * \param lexer
 * Description of parameter lexer.
 * 
 * Pointer to a function that abandons the current input stream, whether it
 * is empty or not and reverts to the previous stacked input stream.
 *
 * \remark
 * The function fails silently if there are no prior input streams.
 */
static void
popCharStream   (pANTLR3_LEXER lexer)
{
    pANTLR3_INPUT_STREAM input;

    // If we do not have a stream stack or we are already at the
    // stack bottom, then do nothing.
    //
    if	(lexer->streams != NULL && lexer->streams->size(lexer->streams) > 0)
    {
	// We just leave the current stream to its fate, we do not close
	// it or anything as we do not know what the programmer intended
	// for it. This method can always be overridden of course.
	// So just find out what was currently saved on the stack and use
	// that now, then pop it from the stack.
	//
	input	= (pANTLR3_INPUT_STREAM)(lexer->streams->top);
	lexer->streams->pop(lexer->streams);

	// Now install the stream as the current one.
	//
	lexer->setCharStream(lexer, input);
	lexer->input->istream->rewindLast(lexer->input->istream);
    }
    return;
}

static void emitNew	    (pANTLR3_LEXER lexer,  pANTLR3_COMMON_TOKEN token)
{
    lexer->token    = token;	/* Voila!   */
}

static pANTLR3_COMMON_TOKEN
emit	    (pANTLR3_LEXER lexer)
{
    pANTLR3_COMMON_TOKEN	token;

    /* We could check pointers to token factories and so on, but
     * we are in code that we want to run as fast as possible
     * so we are not checking any errors. So make sure you have installed an input stream before
     * trying to emit a new token.
     */
    token   = lexer->tokFactory->newToken(lexer->tokFactory);

    /* Install the supplied information, and some other bits we already know
     * get added automatically, such as the input stream it is associated with
     * (though it can all be overridden of course)
     */
    token->type		    = lexer->type;
    token->channel	    = lexer->channel;
    token->start	    = lexer->tokenStartCharIndex;
    token->stop		    = lexer->getCharIndex(lexer) - 1;
    token->line		    = lexer->tokenStartLine;
    token->charPosition	= lexer->tokenStartCharPositionInLine;
    token->text		    = lexer->text;
    token->lineStart	= lexer->input->currentLine;
	token->user1		= lexer->user1;
	token->user2		= lexer->user2;
	token->user3		= lexer->user3;
	token->custom		= lexer->custom;

    lexer->token	    = token;

    return  token;
}

/**
 * Free the resources allocated by a lexer
 */
static void 
freeLexer    (pANTLR3_LEXER lexer)
{
	if	(lexer->streams != NULL)
	{
		lexer->streams->free(lexer->streams);
	}
	if	(lexer->tokFactory != NULL)
	{
		lexer->tokFactory->close(lexer->tokFactory);
		lexer->tokFactory = NULL;
	}
	if	(lexer->tokSource != NULL)
	{
		ANTLR3_FREE(lexer->tokSource);
		lexer->tokSource = NULL;
	}
	if	(lexer->rec != NULL)
	{
		lexer->rec->free(lexer->rec);
		lexer->rec = NULL;
	}
	ANTLR3_FREE(lexer);
}

/** Implementation of matchs for the lexer, overrides any
 *  base implementation in the base recognizer. 
 *
 *  \remark
 *  Note that the generated code lays down arrays of ints for constant
 *  strings so that they are int UTF32 form!
 */
static ANTLR3_BOOLEAN
matchs(pANTLR3_LEXER lexer, ANTLR3_UCHAR * string)
{
	while   (*string != ANTLR3_STRING_TERMINATOR)
	{
		if  (lexer->input->istream->_LA(lexer->input->istream, 1) != (*string))
		{
			if	(lexer->rec->backtracking > 0)
			{
				lexer->rec->failed = ANTLR3_TRUE;
				return ANTLR3_FALSE;
			}

			lexer->rec->exConstruct(lexer->rec);
			lexer->rec->failed	 = ANTLR3_TRUE;

			/* TODO: Implement exception creation more fully perhaps
			 */
			lexer->recover(lexer);
			return  ANTLR3_FALSE;
		}

		/* Matched correctly, do consume it
		 */
		lexer->input->istream->consume(lexer->input->istream);
		string++;

		/* Reset any failed indicator
		 */
		lexer->rec->failed = ANTLR3_FALSE;
	}


	return  ANTLR3_TRUE;
}

/** Implementation of matchc for the lexer, overrides any
 *  base implementation in the base recognizer. 
 *
 *  \remark
 *  Note that the generated code lays down arrays of ints for constant
 *  strings so that they are int UTF32 form!
 */
static ANTLR3_BOOLEAN
matchc(pANTLR3_LEXER lexer, ANTLR3_UCHAR c)
{
	if	(lexer->input->istream->_LA(lexer->input->istream, 1) == c)
	{
		/* Matched correctly, do consume it
		 */
		lexer->input->istream->consume(lexer->input->istream);

		/* Reset any failed indicator
		 */
		lexer->rec->failed = ANTLR3_FALSE;

		return	ANTLR3_TRUE;
	}

	/* Failed to match, exception and recovery time.
	 */
	if	(lexer->rec->backtracking > 0)
	{
		lexer->rec->failed  = ANTLR3_TRUE;
		return	ANTLR3_FALSE;
	}

	lexer->rec->exConstruct(lexer->rec);

	/* TODO: Implement exception creation more fully perhaps
	 */
	lexer->recover(lexer);

	return  ANTLR3_FALSE;
}

/** Implementation of match range for the lexer, overrides any
 *  base implementation in the base recognizer. 
 *
 *  \remark
 *  Note that the generated code lays down arrays of ints for constant
 *  strings so that they are int UTF32 form!
 */
static ANTLR3_BOOLEAN
matchRange(pANTLR3_LEXER lexer, ANTLR3_UCHAR low, ANTLR3_UCHAR high)
{
    ANTLR3_UCHAR    c;

    /* What is in the stream at the moment?
     */
    c	= lexer->input->istream->_LA(lexer->input->istream, 1);
    if	( c >= low && c <= high)
    {
	/* Matched correctly, consume it
	 */
	lexer->input->istream->consume(lexer->input->istream);

	/* Reset any failed indicator
	 */
	lexer->rec->failed = ANTLR3_FALSE;

	return	ANTLR3_TRUE;
    }
    
    /* Failed to match, execption and recovery time.
     */

    if	(lexer->rec->backtracking > 0)
    {
	lexer->rec->failed  = ANTLR3_TRUE;
	return	ANTLR3_FALSE;
    }

    lexer->rec->exConstruct(lexer->rec);

    /* TODO: Implement exception creation more fully
     */
    lexer->recover(lexer);

    return  ANTLR3_FALSE;
}

static void
matchAny	    (pANTLR3_LEXER lexer)
{
    lexer->input->istream->consume(lexer->input->istream);
}

static void
recover	    (pANTLR3_LEXER lexer)
{
    lexer->input->istream->consume(lexer->input->istream);
}

static ANTLR3_UINT64
getLine	    (pANTLR3_LEXER lexer)
{
    return  lexer->input->getLine(lexer->input);
}

static ANTLR3_UINT32
getCharPositionInLine	(pANTLR3_LEXER lexer)
{
    return  lexer->input->getCharPositionInLine(lexer->input);
}

static ANTLR3_UINT64	getCharIndex	    (pANTLR3_LEXER lexer)
{
    return lexer->input->istream->index(lexer->input->istream);
}

static pANTLR3_STRING
getText	    (pANTLR3_LEXER lexer)
{
    if (lexer->text)
    {
	return	lexer->text;

    }
    return  lexer->input->substr(
			    lexer->input, 
			    lexer->tokenStartCharIndex,
			    lexer->getCharIndex(lexer)-1);

}

