#include    <antlr3basetree.h>

#ifdef	WIN32
#pragma warning( disable : 4100 )
#endif

static void    *	getChild	(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i);
static ANTLR3_UINT64	getChildCount	(pANTLR3_BASE_TREE tree);
static ANTLR3_UINT32	getCharPositionInLine
					(pANTLR3_BASE_TREE tree);
static ANTLR3_UINT64	getLine		(pANTLR3_BASE_TREE tree);
static pANTLR3_BASE_TREE    
			getFirstChildWithType
					(pANTLR3_BASE_TREE tree, ANTLR3_UINT32 type);
static void		addChild	(pANTLR3_BASE_TREE tree, pANTLR3_BASE_TREE child);
static void		addChildren	(pANTLR3_BASE_TREE tree, pANTLR3_LIST kids);
static void		createChildrenList
					(pANTLR3_BASE_TREE tree);

static void		setChild	(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i, void * child);
static void    *	deleteChild	(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i);
static void    *	dupTree		(pANTLR3_BASE_TREE tree);
static pANTLR3_STRING	toStringTree	(pANTLR3_BASE_TREE tree);


ANTLR3_API pANTLR3_BASE_TREE
antlr3BaseTreeNew(pANTLR3_BASE_TREE  tree)
{
    /* api */
    tree->getChild	    =  getChild;
    tree->getChildCount	    =  getChildCount;
    tree->addChild	    =  (void (*)(pANTLR3_BASE_TREE, void *))(addChild);
    tree->addChildren	    =  addChildren;
    tree->setChild	    =  setChild;
    tree->deleteChild	    =  deleteChild;
    tree->dupTree	    =  dupTree;
    tree->toStringTree	    =  toStringTree;
    tree->createChildrenList=  createChildrenList;
    
    tree->getCharPositionInLine
			    =  getCharPositionInLine;
    tree->getLine	    =  getLine;

    tree->getFirstChildWithType
			    =  (void *(*)(pANTLR3_BASE_TREE, ANTLR3_UINT32))(getFirstChildWithType);
    tree->children	    = NULL;

    /* Rest must be filled in by caller.
     */
    return  tree;
}

static ANTLR3_UINT32	
getCharPositionInLine	(pANTLR3_BASE_TREE tree)
{
    return  0;
}

static ANTLR3_UINT64	
getLine	(pANTLR3_BASE_TREE tree)
{
    return  0;
}
static pANTLR3_BASE_TREE
getFirstChildWithType	(pANTLR3_BASE_TREE tree, ANTLR3_UINT32 type)
{
    ANTLR3_UINT64   i;
    ANTLR3_UINT64   cs;

    pANTLR3_BASE_TREE	t;
    if	(tree->children != NULL)
    {
	cs	= tree->children->size(tree->children);
	for	(i = 0; i < cs; i++)
	{
	    t = (pANTLR3_BASE_TREE) (tree->children->get(tree->children, i+1));
	    if  (tree->getType(t) == type)
	    {
		return  (pANTLR3_BASE_TREE)t;
	    }
	}
    }
    return  NULL;
}



static void    *
getChild		(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i)
{
    if	(      tree->children == NULL
	    || i >= tree->children->size(tree->children))
    {
	return NULL;
    }
    return  tree->children->get(tree->children, i+1);
}


static ANTLR3_UINT64
getChildCount	(pANTLR3_BASE_TREE tree)
{
    if	(tree->children == NULL)
    {
	return 0;
    }
    else
    {
	return	tree->children->size(tree->children);
    }
}

void	    
addChild (pANTLR3_BASE_TREE tree, pANTLR3_BASE_TREE child)
{
    ANTLR3_UINT64   n;
    ANTLR3_UINT64   i;

    if	(child == NULL)
    {
	return;
    }

    if	(child->isNil(child) == ANTLR3_TRUE)
    {
	if  (child->children != NULL && child->children == tree->children)
	{
	    /* TODO: Change to exception rather than fprintf
	     */
	    fprintf(stderr, "ANTLR3: An attempt was made to add a child list to itself!\n");
	    return;
	}

	/* Add all of the children's children to this list
	 */
	if  (child->children != NULL)
	{
	    if	(tree->children == NULL)
	    {
		tree->children = antlr3VectorNew(32);
	    }
	    /* Need to copy the children 
	     */
	    n = child->children->size(child->children);

	    for (i = 0; i<n; i++)
	    {
		void	* entry;
		entry	= child->children->get(child->children, i + 1);

		/* ANTLR3 lists can be sparse, unlike Array Lists
		 */
		if  (entry != NULL)
		{
		    tree->children->add(tree->children, entry, (void (ANTLR3_CDECL *)(void *))child->free);
		}
	    }
	}
    }
    else
    {
	/* Tree we are adding is not empty and might have children to copy
	 */
	if  (tree->children == NULL)
	{
	    /* No children in the tree we are adding to, so create a new list on
	     * the fly to hold them.
	     */
	    tree->createChildrenList((void *)tree);
	}
	tree->children->add(tree->children, child, (void (ANTLR3_CDECL *)(void *))child->free);
    }
}

/** Add all elements of the supplied list as children of this node
 */
static void
addChildren	(pANTLR3_BASE_TREE tree, pANTLR3_LIST kids)
{
   ANTLR3_UINT64    i;
   ANTLR3_UINT64    s;

   s = kids->size(kids);
   for	(i = 0; i<s; i++)
   {
       tree->addChild(tree, (pANTLR3_BASE_TREE)(kids->get(kids, i+1)));
   }
}

/* Can override in a child 'class' to do something different
 */
static void
createChildrenList  (pANTLR3_BASE_TREE tree)
{
    tree->children = antlr3VectorNew(32);
}

static    void
setChild	(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i, void * child)
{
    if	(tree->children == NULL)
    {
	tree->createChildrenList(tree);
    }
    tree->children->remove(tree->children, i + 1);	/* remove any existing node at that position */
    tree->children->put(tree->children, i+1, child, NULL, ANTLR3_FALSE);
}

static void    *
deleteChild	(pANTLR3_BASE_TREE tree, ANTLR3_UINT64 i)
{
    if	( tree->children == NULL)
    {
	return	NULL;
    }
    /* NB: this makes the list sparse (with a gap at i)
     * unlike Java so be careful to cater for this elsewhere
     */
    return  tree->children->remove(tree->children, i+1);
}

static void    *
dupTree		(pANTLR3_BASE_TREE tree)
{
    pANTLR3_BASE_TREE	newTree;
    ANTLR3_UINT64	i;
    ANTLR3_UINT64	s;

    newTree = tree->dupNode	    (tree);
    s	    = tree->children->size  (tree->children);

    if	(tree->children != NULL)
    {
	for	(i = 0; i < s; i++)
	{
	    pANTLR3_BASE_TREE    t;
	    pANTLR3_BASE_TREE    newNode;

	    t   = (pANTLR3_BASE_TREE) tree->children->get(tree->children, i+1);
    	
	    if  (t!= NULL)
	    {
		newNode	    = t->dupNode(t);
		newTree->addChild(newTree, newNode);
	    }
	}
    }

    return newTree;
}

static pANTLR3_STRING
toStringTree	(pANTLR3_BASE_TREE tree)
{
    pANTLR3_STRING  string;
    ANTLR3_UINT64   i;
    ANTLR3_UINT64   n;
    pANTLR3_BASE_TREE   t;

    if	(tree->children == NULL || tree->children->size(tree->children) == 0)
    {
	return	tree->toString(tree);
    }

    /* Need a new string with nothing at all in it.
     */
    string	= tree->strFactory->newRaw(tree->strFactory);

    if	(tree->isNil(tree) == ANTLR3_FALSE)
    {
	string->append8	(string, "(");
	string->appendS	(string, tree->toString(tree));
	string->append8	(string, " ");
    }
    if	(tree->children != NULL)
    {
	n = tree->children->size(tree->children);

	for	(i = 0; i < n; i++)
	{   
	    t   = (pANTLR3_BASE_TREE) tree->children->get(tree->children, i + 1);
    	
	    if  (i > 0)
	    {
		string->append8(string, " ");
	    }
	    string->appendS(string, t->toStringTree(t));
	}
    }
    if	(tree->isNil(tree) == ANTLR3_FALSE)
    {
	string->append8(string,")");
    }

    return  string;
}
