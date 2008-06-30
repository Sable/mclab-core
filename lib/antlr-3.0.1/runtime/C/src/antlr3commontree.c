/** \file
 * Implementation of ANTLR3 CommonTree, wihch you can use as a
 * starting point for your own tree. Thoguh it is easier just to tag things on
 * to the user pointer in the tree to be honest.
 */
#include    <antlr3commontree.h>


static pANTLR3_COMMON_TOKEN getToken			(pANTLR3_BASE_TREE tree);
static pANTLR3_BASE_TREE    dupNode			(pANTLR3_BASE_TREE tree);
static ANTLR3_BOOLEAN	    isNil			(pANTLR3_BASE_TREE tree);
static ANTLR3_UINT32	    getType			(pANTLR3_BASE_TREE tree);
static pANTLR3_STRING	    getText			(pANTLR3_BASE_TREE tree);
static ANTLR3_UINT64	    getLine			(pANTLR3_BASE_TREE tree);
static ANTLR3_UINT32	    getCharPositionInLine	(pANTLR3_BASE_TREE tree);
static pANTLR3_STRING	    toString			(pANTLR3_BASE_TREE tree);

static void		    freeTree			(pANTLR3_BASE_TREE tree);

/* Factory functions for the Arboretum */
static void		    newPool			(pANTLR3_ARBORETUM factory);
static pANTLR3_BASE_TREE    newPoolTree			(pANTLR3_ARBORETUM factory);
static pANTLR3_BASE_TREE    newFromTree			(pANTLR3_ARBORETUM factory, pANTLR3_COMMON_TREE tree);
static pANTLR3_BASE_TREE    newFromToken		(pANTLR3_ARBORETUM factory, pANTLR3_COMMON_TOKEN token);
static void		    factoryClose		(pANTLR3_ARBORETUM factory);

ANTLR3_API pANTLR3_ARBORETUM
antlr3ArboretumNew(pANTLR3_STRING_FACTORY strFactory)
{
    pANTLR3_ARBORETUM   factory;

    /* Allocate memory
     */
    factory	= (pANTLR3_ARBORETUM) ANTLR3_MALLOC((size_t)sizeof(ANTLR3_ARBORETUM));

    if	(factory == NULL)
    {
	return	(pANTLR3_ARBORETUM)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Install factory API
     */
    factory->newTree	    =  newPoolTree;
    factory->newFromTree    =  newFromTree;
    factory->newFromToken   =  newFromToken;
    factory->close	    =  factoryClose;

    /* Allocate the initial pool
     */
    factory->thisPool	= -1;
    factory->pools	= NULL;
    newPool(factory);

    /* Factory space is good, we now want to initialize our cheating token
     * which one it is initialized is the model for all tokens we manufacture
     */
    antlr3SetCTAPI(&factory->unTruc);

    /* Set some initial variables for future copying, including a string factory
     * that we can use later for converting trees to strings.
     */
    factory->unTruc.factoryMade		= ANTLR3_TRUE;
    factory->unTruc.baseTree.strFactory	= strFactory;
    
    return  factory;

}

static void
newPool(pANTLR3_ARBORETUM factory)
{
    /* Increment factory count
     */
    factory->thisPool++;

    /* Ensure we have enough pointers allocated
     */
    factory->pools = (pANTLR3_COMMON_TREE *)
		     ANTLR3_REALLOC(	(void *)factory->pools,	    /* Current pools pointer (starts at NULL)	*/
					(ANTLR3_UINT64)((factory->thisPool + 1) * sizeof(pANTLR3_COMMON_TREE *))	/* Memory for new pool pointers */
					);

    /* Allocate a new pool for the factory
     */
    factory->pools[factory->thisPool]	=
			    (pANTLR3_COMMON_TREE) 
				ANTLR3_MALLOC((size_t)(sizeof(ANTLR3_COMMON_TREE) * ANTLR3_FACTORY_POOL_SIZE));


    /* Reset the counters
     */
    factory->nextTree	= 0;
  
    /* Done
     */
    return;
}

static	pANTLR3_BASE_TREE    
newPoolTree	    (pANTLR3_ARBORETUM factory)
{
    pANTLR3_COMMON_TREE    tree;

    /* See if we need a new token pool before allocating a new
     * one
     */
    if	(factory->nextTree >= ANTLR3_FACTORY_POOL_SIZE)
    {
	/* We ran out of tokens in the current pool, so we need a new pool
	 */
	newPool(factory);
    }

    /* Assuming everything went well (we are trying for performance here so doing minimal
     * error checking - we might introduce a DEBUG flag set that turns on tracing and things
     * later, but I have typed this entire runtime in in 3 days so far :-(), <breath>, then
     * we can work out what the pointer is to the next token.
     */
    tree   = factory->pools[factory->thisPool] + factory->nextTree;
    factory->nextTree++;

    /* We have our token pointer now, so we can initialize it to the predefined model.
     */
    ANTLR3_MEMMOVE((void *)tree, (const void *)&(factory->unTruc), (ANTLR3_UINT64)sizeof(ANTLR3_COMMON_TREE));
    /* The super points to the common tree so we must override the one used by
     * by the pre-built tree as otherwise we will always poitn to the same initial
     * comomon tree and we might spend 3 hours trying to debug why - this woudl never
     * happen to me of course! :-(
     */
    tree->baseTree.super	= tree;

    /* And we are done
     */
    return  &(tree->baseTree);
}


static pANTLR3_BASE_TREE	    
newFromTree(pANTLR3_ARBORETUM factory, pANTLR3_COMMON_TREE tree)
{
    pANTLR3_BASE_TREE	newTree;

    newTree = factory->newTree(factory);

    if	(newTree == (pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return	(pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Pick up the payload we had in the supplied tree
     */
    ((pANTLR3_COMMON_TREE)(newTree->super))->token   = tree->token;
    newTree->u		    = tree->baseTree.u;	/* Copy any user pointer */

    return  newTree;
}

static pANTLR3_BASE_TREE	    
newFromToken(pANTLR3_ARBORETUM factory, pANTLR3_COMMON_TOKEN token)
{
    pANTLR3_BASE_TREE	newTree;

    newTree = factory->newTree(factory);

    if	(newTree == (pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return	(pANTLR3_BASE_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Pick up the payload we had in the supplied tree
     */
    ((pANTLR3_COMMON_TREE)(newTree->super))->token = token;

    return newTree;
}

static	void
factoryClose	    (pANTLR3_ARBORETUM factory)
{
    ANTLR3_INT32	    poolCount;

    /* We iterate the token pools one at a time
     */
    for	(poolCount = 0; poolCount <= factory->thisPool; poolCount++)
    {
	ANTLR3_UINT32	tree;

	/* We must free any child vectors or anything else that requires
	 * freeing by calling the free method on each member of the pool.
	 * The free method will not release itself as it is factoryMade. So
	 * we just release them en masse ici.
	 */
	for (tree=0; tree< ANTLR3_FACTORY_POOL_SIZE; tree++)
	{
	    pANTLR3_BASE_TREE thisTree;

	    thisTree    = &(factory->pools[poolCount][tree].baseTree);

	    if	(thisTree->free == NULL)
	    {
		break;	/* Found the last allocation in this pool */
	    }
	    thisTree->free(thisTree);
	}
    }

    /* We must free the pools after we have traversed all the entries in all the
     * the pools as we cannot guarantee that the trees in the pool will not be referenced as children
     * of other trees until all have been asked to free themselves.
     */
    for	(poolCount = 0; poolCount <= factory->thisPool; poolCount++)
    {
	/* We can now free this pool allocation
	 */
	ANTLR3_FREE(factory->pools[poolCount]);
	factory->pools[poolCount] = NULL;
    }

    /* All the pools are deallocated we can free the pointers to the pools
     * now.
     */
    ANTLR3_FREE(factory->pools);

    /* Finally, we can free the space for the factory itself
     */
    ANTLR3_FREE(factory);
}


ANTLR3_API void 
antlr3SetCTAPI(pANTLR3_COMMON_TREE tree)
{
    /* Init base tree
     */
    antlr3BaseTreeNew(&(tree->baseTree));

    /* We need a pointer to ourselves for 
     * the payload and few functions that we
     * provide.
     */
    tree->baseTree.super    =  tree;

    /* Common tree overrides */

    tree->baseTree.free	    =  freeTree;
    tree->baseTree.isNil    =  isNil;
    tree->baseTree.toString =  toString;
    tree->baseTree.dupNode  =  (void *(*)(pANTLR3_BASE_TREE))(dupNode);
    tree->baseTree.getLine  =  getLine;
    tree->baseTree.getCharPositionInLine
			    =  getCharPositionInLine;
    tree->baseTree.toString =  toString;
    tree->baseTree.getType  =  getType;
    tree->baseTree.getText  =  getText;

    tree->getToken	    =  getToken;

    tree->token	= NULL;	/* No token as yet */
    tree->startIndex	= 0;
    tree->stopIndex	= 0;

    return;
}

/* --------------------------------------
 * Non factory node constructors.
 */


ANTLR3_API pANTLR3_COMMON_TREE	    
antlr3CommonTreeNewFromTree(pANTLR3_COMMON_TREE tree)
{
    pANTLR3_COMMON_TREE	newTree;

    newTree = antlr3CommonTreeNew();

    if	(newTree == (pANTLR3_COMMON_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return	(pANTLR3_COMMON_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Pick up the payload we had in the supplied tree
     */
    newTree->token	    = tree->token;
    newTree->baseTree.u	    = tree->baseTree.u;	/* Copy any user pointer */

    return  newTree;
}

ANTLR3_API pANTLR3_COMMON_TREE	    
antlr3CommonTreeNewFromToken(pANTLR3_COMMON_TOKEN token)
{
    pANTLR3_COMMON_TREE	newTree;

    newTree = antlr3CommonTreeNew();

    if	(newTree == (pANTLR3_COMMON_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM))
    {
	return	(pANTLR3_COMMON_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    /* Pick up the payload we had in the supplied tree
     */
    newTree->token = token;

    return newTree;
}

ANTLR3_API pANTLR3_COMMON_TREE
antlr3CommonTreeNew()
{
    pANTLR3_COMMON_TREE	tree;

    tree    = ANTLR3_MALLOC(sizeof(ANTLR3_COMMON_TREE));

    if	(tree == NULL)
    {
	return (pANTLR3_COMMON_TREE)ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    antlr3SetCTAPI(tree);

    return tree;
}

static void
freeTree(pANTLR3_BASE_TREE tree)
{
    /* Call free on all the nodes.
     * We install all the nodes as base nodes with a pointer to a function that
     * knows how to free itself. A function that calls this function in fact. So if we just
     * delete the hash table, then this function will be called for all
     * child nodes, which will delete thier child nodes, and so on
     * recursively until they are all gone :-)
     */
    if	(tree->children != NULL)
    {
	tree->children->free(tree->children);
	tree->children = NULL;
    }
    
    if	(((pANTLR3_COMMON_TREE)(tree->super))->factoryMade == ANTLR3_FALSE)
    {
	/* Now we can free this structure memory, which contains the base tree
	 * structure also. Later I will expand this to call an public function to release
	 * the base node, so people overriding it will be able to use it more freely.
	 */
	ANTLR3_FREE(tree->super);
    }

    return;
}


static pANTLR3_COMMON_TOKEN 
getToken			(pANTLR3_BASE_TREE tree)
{
    /* The token is the payload of the common tree or other implementor
     * so it is stored within ourselves, which is the super pointer.
     */
    return  ((pANTLR3_COMMON_TREE)(tree->super))->token;
}

static pANTLR3_BASE_TREE    
dupNode			(pANTLR3_BASE_TREE tree)
{
    /* The node we are duplicating is in fact the common tree (that's why we are here)
     * so we use the me pointer to duplicate.
     */
    pANTLR3_COMMON_TREE	    theNew;
    
    theNew  = antlr3CommonTreeNewFromTree((pANTLR3_COMMON_TREE)(tree->super));

    /* The pointer we return is the base implementation of course
     */
    return &(theNew->baseTree);

}

static ANTLR3_BOOLEAN	    
isNil			(pANTLR3_BASE_TREE tree)
{
    /* This is a Nil tree if it has no payload (Token in our case)
     * This is C, and you should never return the result of a comparison
     * so we can't do the same as Java (no emails about this, I am correct and
     * you know it ;-)
     */
   if	(((pANTLR3_COMMON_TREE)(tree->super))->token == NULL)
   {
       return ANTLR3_TRUE;
   }
   else
   {
       return ANTLR3_FALSE;
   }
}

static ANTLR3_UINT32	    
getType			(pANTLR3_BASE_TREE tree)
{
    pANTLR3_COMMON_TREE    theTree;

    theTree = (pANTLR3_COMMON_TREE)(tree->super);

    if	(theTree->token == NULL)
    {
	return	0;
    }
    else
    {
	return	theTree->token->getType(theTree->token);
    }
}

static pANTLR3_STRING	    
getText			(pANTLR3_BASE_TREE tree)
{
    return	tree->toString(tree);
}

static ANTLR3_UINT64	    getLine			(pANTLR3_BASE_TREE tree)
{
    pANTLR3_COMMON_TREE	    cTree;
    pANTLR3_COMMON_TOKEN    token;

    cTree   = (pANTLR3_COMMON_TREE)(tree->super);

    token   = cTree->token;

    if	(token == NULL || token->getLine(token) == 0)
    {
	if  (tree->getChildCount(tree) > 0)
	{
	    pANTLR3_BASE_TREE	child;

	    child   = (pANTLR3_BASE_TREE)tree->getChild(tree, 0);
	    return child->getLine(child);
	}
	return 0;
    }
    return  token->getLine(token);
}

static ANTLR3_UINT32	    getCharPositionInLine	(pANTLR3_BASE_TREE tree)
{
    pANTLR3_COMMON_TOKEN    token;

    token   = ((pANTLR3_COMMON_TREE)(tree->super))->token;

    if	(token == NULL || token->getCharPositionInLine(token) == -1)
    {
	if  (tree->getChildCount(tree) > 0)
	{
	    pANTLR3_BASE_TREE	child;

	    child   = (pANTLR3_BASE_TREE)tree->getChild(tree, 0);

	    return child->getCharPositionInLine(child);
	}
	return 0;
    }
    return  token->getCharPositionInLine(token);
}

static pANTLR3_STRING	    toString			(pANTLR3_BASE_TREE tree)
{
	if  (tree->isNil(tree) == ANTLR3_TRUE)
	{
	    pANTLR3_STRING  nil;

	    nil	= tree->strFactory->newPtr(tree->strFactory, (pANTLR3_UINT8)"nil", 3);

	    return nil;
	}

	return	((pANTLR3_COMMON_TREE)(tree->super))->token->getText(((pANTLR3_COMMON_TREE)(tree->super))->token);
}

