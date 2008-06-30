/** \file
 *  Implementation of the tree parser and overrides for the base recognizer
 */

#include    <antlr3treeparser.h>

/* BASE Recognizer overrides
 */
static void				mismatch	    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow);

/* Tree parser API
 */
static void			displayRecognitionError	    (pANTLR3_BASE_RECOGNIZER rec, pANTLR3_UINT8 * tokenNames);
static void			recover			    (pANTLR3_BASE_RECOGNIZER rec, pANTLR3_INT_STREAM input);
static void			setTreeNodeStream	    (pANTLR3_TREE_PARSER parser, pANTLR3_COMMON_TREE_NODE_STREAM input);
static pANTLR3_COMMON_TREE_NODE_STREAM	
				getTreeNodeStream	    (pANTLR3_TREE_PARSER parser);
static void			freeParser		    (pANTLR3_TREE_PARSER parser);    
    
ANTLR3_API pANTLR3_TREE_PARSER
antlr3TreeParserNewStream(ANTLR3_UINT32 sizeHint, pANTLR3_COMMON_TREE_NODE_STREAM ctnstream)
{
    pANTLR3_TREE_PARSER	    parser;

    /** Allocate tree parser memory
    */
    parser  =(pANTLR3_TREE_PARSER) ANTLR3_MALLOC(sizeof(ANTLR3_TREE_PARSER));

    if	(parser == NULL)
    {
	return	(pANTLR3_TREE_PARSER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Create and install a base recognizer which does most of the work for us
     */
    parser->rec =  antlr3BaseRecognizerNew(ANTLR3_TYPE_PARSER, sizeHint);

    if	(parser->rec == (pANTLR3_BASE_RECOGNIZER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	parser->free(parser);
	return	(pANTLR3_TREE_PARSER) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }
    
    /* Ensure we can track back to the tree parser super structure
     * from the base recognizer structure
     */
    parser->rec->super	= parser;
    parser->rec->type	= ANTLR3_TYPE_TREE_PARSER;

    /* Install our base recognizer overrides
     */
    parser->rec->mismatch	=  mismatch;
    parser->rec->exConstruct	=  antlr3MTNExceptionNew;

    /* Install tree parser API
     */
    parser->getTreeNodeStream	=  getTreeNodeStream;
    parser->setTreeNodeStream	=  setTreeNodeStream;
    parser->free		=  freeParser;

    /* Install the tree node stream
     */
    parser->setTreeNodeStream(parser, ctnstream);

    return  parser;
}

/**
 * \brief
 * Creates a new Mismatched Tree Nde Exception and inserts in the recognizer
 * exception stack.
 * 
 * \param recognizer
 * Context pointer for this recognizer
 * 
 */
ANTLR3_API	void
antlr3MTNExceptionNew(pANTLR3_BASE_RECOGNIZER recognizer)
{
    /* Create a basic recognition exception strucuture
     */
    antlr3RecognitionExceptionNew(recognizer);

    /* Now update it to indicate this is a Mismatched token exception
     */
    recognizer->exception->name		= ANTLR3_MISMATCHED_TREE_NODE_NAME;
    recognizer->exception->type		= ANTLR3_MISMATCHED_TREE_NODE_EXCEPTION;

    return;
}


static void
freeParser	(pANTLR3_TREE_PARSER parser)
{
    if	(parser->rec != NULL)
    {
	    if	(parser->rec->following != NULL)
	    {
		parser->rec->following->free(parser->rec->following);
		parser->rec->following = NULL;
	    }
	    parser->rec->free(parser->rec);
	    parser->rec	= NULL;
    }

    ANTLR3_FREE(parser);
}

/** Set the input stream and reset the parser
 */
static void
setTreeNodeStream	(pANTLR3_TREE_PARSER parser, pANTLR3_COMMON_TREE_NODE_STREAM input)
{
    parser->ctnstream = input;
    parser->rec->reset		(parser->rec);
    parser->ctnstream->reset	(parser->ctnstream);
}

/** Return a pointer to the input stream
 */
static pANTLR3_COMMON_TREE_NODE_STREAM
getTreeNodeStream	(pANTLR3_TREE_PARSER parser)
{
    return  parser->ctnstream;
}


/** Override for standard base recognizer mismatch function
 *  as we have DOWN/UP nodes in the stream that have no line info,
 *  plus we want to alter the exception type.
 */
static void
mismatch	    (pANTLR3_BASE_RECOGNIZER recognizer, ANTLR3_UINT32 ttype, pANTLR3_BITSET follow)
{
    recognizer->exConstruct(recognizer);
    recognizer->recoverFromMismatchedToken(recognizer, ttype, follow);
}
