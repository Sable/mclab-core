/** \file
 * Implementation of token/tree streams that are used by the
 * tree re-write rules to manipulate the tokens and trees produced
 * by rules that are subject to rewrite directives.
 */

#include    <antlr3rewritestreams.h>

/* Static support function forward declarations for the stream types.
 */
static    void			reset		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream); 
static	  void			add		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * el, void (ANTLR3_CDECL *freePtr)(void *));
static    void *		next		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static    void *		_next		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static	  void *		dupTok		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * el);
static	  void *		dupTree		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * el);
static	  pANTLR3_BASE_TREE	toTreeTree	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * element);
static	  pANTLR3_BASE_TREE	toTreeToken	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * element);
static    ANTLR3_BOOLEAN	hasNext		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static    pANTLR3_BASE_TREE	nextNode	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static    ANTLR3_UINT32		size		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static    pANTLR3_STRING	getDescription	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);
static	  void			freeRS		(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream);


static void
freeRS	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
    if (stream->freeElements == ANTLR3_TRUE && stream->elements != NULL)
    {
	stream->elements->free(stream->elements);
    }
    ANTLR3_FREE(stream);
}

/* Functions for creating streams
 */
static  pANTLR3_REWRITE_RULE_ELEMENT_STREAM 
antlr3RewriteRuleElementStreamNewAE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description)
{
    pANTLR3_REWRITE_RULE_ELEMENT_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= (pANTLR3_REWRITE_RULE_ELEMENT_STREAM) ANTLR3_MALLOC((size_t)(sizeof(ANTLR3_REWRITE_RULE_ELEMENT_STREAM)));

    if	(stream == NULL)
    {
	return	(pANTLR3_REWRITE_RULE_ELEMENT_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Populate the generic interface */

    stream->reset	    = reset;
    stream->add		    = add;
    stream->next	    = next;
    stream->_next	    = _next;
    stream->hasNext	    = hasNext;
    stream->size	    = size;
    stream->getDescription  = getDescription;
    stream->free	    = freeRS;

    /* Install the description
     */
    stream->elementDescription	= adaptor->strFactory->newStr8(adaptor->strFactory, description);

    /* Install the adaptor
     */
    stream->adaptor		= adaptor;

    return stream;
}

static pANTLR3_REWRITE_RULE_ELEMENT_STREAM 
antlr3RewriteRuleElementStreamNewAEE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, void * oneElement)
{
    pANTLR3_REWRITE_RULE_ELEMENT_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAE(adaptor, description);

    if (stream == (pANTLR3_REWRITE_RULE_ELEMENT_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Stream seems good so we need to add the supplied element
     */
    stream->add(stream, oneElement, NULL);
    return stream;
}

static pANTLR3_REWRITE_RULE_ELEMENT_STREAM 
antlr3RewriteRuleElementStreamNewAEV(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, pANTLR3_VECTOR vector)
{
    pANTLR3_REWRITE_RULE_ELEMENT_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAE(adaptor, description);

    if (stream == (pANTLR3_REWRITE_RULE_ELEMENT_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Stream seems good so we need to install the vector we were
     * given. We assume that someone else is going to free the
     * vector.
     */
    stream->elements	= vector;

    return stream;
}

/* Token rewrite stream ... */

ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
antlr3RewriteRuleTokenStreamNewAE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description)
{
    pANTLR3_REWRITE_RULE_TOKEN_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAE(adaptor, description);

    if (stream == (pANTLR3_REWRITE_RULE_TOKEN_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Install the token based overrides
     */
    stream->dup	    = dupTok;
    stream->toTree  = toTreeToken;

    /* No nextNode implementation for a token rewrite stream */
    return stream;
}

ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
antlr3RewriteRuleTokenStreamNewAEE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, void * oneElement)
{
    pANTLR3_REWRITE_RULE_TOKEN_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAEE(adaptor, description, oneElement);

    /* Install the token based overrides
     */
    stream->dup	    = dupTok;
    stream->toTree  = toTreeToken;

    /* No nextNode implementation for a token rewrite stream */
    return stream;
}

ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
antlr3RewriteRuleTokenStreamNewAEV(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, pANTLR3_VECTOR vector)
{
    pANTLR3_REWRITE_RULE_TOKEN_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAEV(adaptor, description, vector);

    /* Install the token based overrides
     */
    stream->dup	    = dupTok;
    stream->toTree  = toTreeToken;

    /* No nextNode implementation for a token rewrite stream */
    return stream;
}

/* Subtree rewrite stream */

ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
antlr3RewriteRuleSubtreeStreamNewAE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description)
{
    pANTLR3_REWRITE_RULE_SUBTREE_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAE(adaptor, description);

    if (stream == (pANTLR3_REWRITE_RULE_SUBTREE_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Install the token based overrides
     */
    stream->dup		= dupTree;
    stream->toTree	= toTreeTree;
    stream->nextNode	= nextNode;

    return stream;

}
ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
antlr3RewriteRuleSubtreeStreamNewAEE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, void * oneElement)
{
    pANTLR3_REWRITE_RULE_SUBTREE_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAEE(adaptor, description, oneElement);

    if (stream == (pANTLR3_REWRITE_RULE_SUBTREE_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Install the token based overrides
     */
    stream->dup		= dupTree;
    stream->toTree	= toTreeTree;
    stream->nextNode	= nextNode;

    return stream;
}

ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
antlr3RewriteRuleSubtreeStreamNewAEV(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, pANTLR3_VECTOR vector)
{
    pANTLR3_REWRITE_RULE_SUBTREE_STREAM	stream;

    /* First job is to create the memory we need.
     */
    stream	= antlr3RewriteRuleElementStreamNewAEV(adaptor, description, vector);

    if (stream == (pANTLR3_REWRITE_RULE_SUBTREE_STREAM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return stream;
    }

    /* Install the token based overrides
     */
    stream->dup		= dupTree;
    stream->toTree	= toTreeTree;
    stream->nextNode	= nextNode;

    return stream;
}
/* Static support functions */

/* Reset the condition of this stream so that it appears we have
 * not consumed any of its elements.  Elements themselves are untouched.
 */
static void		
reset    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
    stream->cursor = 0;
}

/* Add a new pANTLR3_BASE_TREE to this stream
 */
static void		
add	    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * el, void (ANTLR3_CDECL *freePtr)(void *))
{
    if (el== NULL)
    {
	return;
    }
    if (stream->elements != NULL)
    {
	/* We have already started with a vector, which means we already have >1
	 * entries in the stream. So we can just add this new element to the existing
	 * collection. 
	 */
	stream->elements->add(stream->elements, el, freePtr);
	return;
    }
    if (stream->singleElement == NULL)
    {
	stream->singleElement = el;
	return;
    }

    /* If we got here then we ahd only the one element so far
     * and we must now create a vector to hold a collection of them
     */
    stream->elements = antlr3VectorNew(0);  /* We will let the vector figure things out as it goes */
    stream->elements->add(stream->elements, stream->singleElement, NULL);
    stream->elements->add(stream->elements, el, NULL);
    stream->singleElement = NULL;

    return;
}

/* Return the next element in the stream.  If out of elements, throw
 * an exception unless size()==1.  If size is 1, then return elements[0].
 */
static void *	
next	    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
    ANTLR3_UINT32   s;

    s = stream->size(stream);
    if (stream->cursor >= s && s == 1)
    {
	pANTLR3_BASE_TREE el;

	el = stream->_next(stream);

	return	stream->dup(stream, el);
    }

    return stream->_next(stream);
}

/** Do the work of getting the next element, making sure that it's
 *  a tree node or subtree.  Deal with the optimization of single-
 *  element list versus list of size > 1.  Throw an exception (or something similar)
 *  if the stream is empty or we're out of elements and size>1.
 *  You can override in a subclass if necessary.
 */
static void *
_next    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
	ANTLR3_UINT32	s;
	pANTLR3_BASE_TREE	t;

	s = stream->size(stream);

	if (s == 0)
	{
		// This means that the stream is empty
		//
		return (pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(NULL);	// Caller must cope with this
	}

	// Traversed all the available elements already?
	//
	if (stream->cursor >= s)
	{
		if (s == 1)
		{
			// Special case when size is single element, it will just dup a lot
			//
			return stream->singleElement;
		}

		// OUt of elements and the size is not 1, so we cannot assume
		// that we just duplicate the entry n times (such as ID ent+ -> ^(ID ent)+)
		// THis means we ran out of elements earlier than was expected.
		//
		return (pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(NULL);	// Caller must cope with this
	}

	// Elements available either for duping or just available
	//
	if (stream->singleElement != NULL)
	{
		stream->cursor++;   // Cursor advances even for single element as this tells us to dup()
		return stream->toTree(stream, stream->singleElement);
	}

	// More than just a single element so we extract it from the 
	// vector.
	//
	t = stream->toTree(stream, stream->elements->get(stream->elements, stream->cursor+1));  // TODO: Why not just cursor++ ?
	stream->cursor++;
	return t;
}

/* When constructing trees, sometimes we need to dup a token or AST
 * subtree.  Dup'ing a token means just creating another AST node
 * around it.  For trees, you must call the adaptor.dupTree().
 */
static void *	
dupTok	    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * el)
{
    return stream->adaptor->create(stream->adaptor, (pANTLR3_COMMON_TOKEN)el);
}

/* When constructing trees, sometimes we need to dup a token or AST
 * subtree.  Dup'ing a token means just creating another AST node
 * around it.  For trees, you must call the adaptor.dupTree().
 */
static void *	
dupTree	    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * element)
{
    return stream->adaptor->dupNode(stream->adaptor, (pANTLR3_BASE_TREE)element);
}

/* Ensure stream emits trees; tokens must be converted to AST nodes.
 * AST nodes can be passed through unmolested.
 */
static pANTLR3_BASE_TREE	
toTreeToken   (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * element)
{
    return stream->adaptor->create(stream->adaptor, (pANTLR3_COMMON_TOKEN) element);
}

/* Ensure stream emits trees; tokens must be converted to AST nodes.
 * AST nodes can be passed through unmolested.
 */
#ifdef WIN32
#pragma warning(push)
#pragma warning(disable : 4100)
#endif

static pANTLR3_BASE_TREE	
toTreeTree   (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream, void * element)
{
    return (pANTLR3_BASE_TREE)element;
}

#ifdef WIN32
#pragma warning(pop)
#endif

/* Returns ANTLR3_TRUE if there is a next element available
 */
static ANTLR3_BOOLEAN	
hasNext  (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
    if (	  (stream->singleElement != NULL && stream->cursor < 1)
			|| stream->elements != NULL && stream->cursor < stream->elements->size(stream->elements))
    {
		return ANTLR3_TRUE;
    }
    else
    {
		return ANTLR3_FALSE;
    }
}

/* Treat next element as a single node even if it's a subtree.
 * This is used instead of next() when the result has to be a
 * tree root node.  Also prevents us from duplicating recently-added
 * children; e.g., ^(type ID)+ adds ID to type and then 2nd iteration
 * must dup the type node, but ID has been added.
 *
 * Referencing to a rule result twice is ok; dup entire tree as
 * we can't be adding trees; e.g., expr expr. 
 */
static pANTLR3_BASE_TREE	
nextNode (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{

	ANTLR3_UINT32	s;
	pANTLR3_BASE_TREE	el = stream->_next(stream);

	s = stream->size(stream);
	if (stream->cursor > s && s == 1)
	{
		// We are out of elements and the size is 1, which means we just 
		// dup the node that we have
		//
		return	stream->adaptor->dupNode(stream->adaptor, el);
	}

	// We were not out of nodes, so the one we received is the one to return
	//
	return  el;
}

/* Number of elements available in the stream
*/
static ANTLR3_UINT32	
size	    (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
	ANTLR3_UINT32   n = 0;

	/* Should be a count of one if singleElement is set. I copied this
	 * logic from the java implementation, which I suspect is just guarding
	 * against someone setting singleElement and forgetting to NULL it out
	 */
	if (stream->singleElement != NULL)
	{
		n = 1;
	}

	if (stream->elements != NULL)
	{
		return (ANTLR3_UINT32)(stream->elements->count);
	}

	return n;
}

/* Returns the description string if there is one available (check for NULL).
 */
static pANTLR3_STRING	
getDescription  (pANTLR3_REWRITE_RULE_ELEMENT_STREAM stream)
{
    if (stream->elementDescription == NULL)
    {
	stream->elementDescription = stream->adaptor->strFactory->newPtr8(stream->adaptor->strFactory, (pANTLR3_UINT8)"<unknown source>", 14);
    }

    return  stream->elementDescription;
}
