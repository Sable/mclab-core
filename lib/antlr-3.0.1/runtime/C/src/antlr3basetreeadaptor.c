/** \file
 * Contains the base functions that all tree adaptors start with.
 * this implementation can then be overridden by any higher implementation.
 * 
 */
#include    <antlr3basetreeadaptor.h>

#ifdef	WIN32
#pragma warning( disable : 4100 )
#endif

/* Interface functions
 */
static	pANTLR3_BASE_TREE	nil			(pANTLR3_BASE_TREE_ADAPTOR adaptor);
static	pANTLR3_BASE_TREE	dupTree			(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t);
static	void			addChild		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, pANTLR3_BASE_TREE child);
static	pANTLR3_BASE_TREE	becomeRoot		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE newRoot, pANTLR3_BASE_TREE oldRoot);
static	pANTLR3_BASE_TREE	rulePostProcessing	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE root);
static	void			addChildToken		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, pANTLR3_COMMON_TOKEN child);
static	pANTLR3_BASE_TREE	becomeRootToken		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_COMMON_TOKEN newRoot, pANTLR3_BASE_TREE oldRoot);
static	pANTLR3_BASE_TREE	createTypeToken		(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_COMMON_TOKEN fromToken);
static	pANTLR3_BASE_TREE	createTypeTokenText	(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_COMMON_TOKEN fromToken, pANTLR3_UINT8 text);
static	pANTLR3_BASE_TREE	createTypeText		(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_UINT8 text);
static	ANTLR3_UINT32		getType			(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t);
static	void			setType			(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, ANTLR3_UINT32 type);
static	pANTLR3_STRING		getText			(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t);
static	void			setText			(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_STRING t);
static	void			setText8		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 t);
static	pANTLR3_BASE_TREE	getChild		(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT64 i);
static	pANTLR3_UINT64		getChildCount		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t);
static	ANTLR3_UINT64		getUniqueID		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t);

/** Given a pointer to a base tree adaptor structure (which is usually embedded in the
 *  super class the implements the tree adaptor used in the parse), initialize its
 *  function pointers and so on.
 */
ANTLR3_API void
antlr3BaseTreeAdaptorInit(pANTLR3_BASE_TREE_ADAPTOR adaptor)
{
	/* Initialize the interface
	 */
	adaptor->nil			=  nil;
	adaptor->dupTree		=  dupTree;
	adaptor->addChild		=  addChild;
	adaptor->becomeRoot		=  becomeRoot;
	adaptor->rulePostProcessing	=  rulePostProcessing;
	adaptor->addChildToken		=  addChildToken;
	adaptor->becomeRootToken	=  becomeRootToken;
	adaptor->createTypeToken	=  createTypeToken;
	adaptor->createTypeTokenText	=  createTypeTokenText;
	adaptor->createTypeText		=  createTypeText;
	adaptor->getType		=  getType;
	adaptor->setType		=  setType;
	adaptor->getText		=  getText;
	adaptor->setText8		=  setText8;
	adaptor->setText		=  setText;
	adaptor->getChild		=  getChild;
	adaptor->getChildCount		=  getChildCount;
	adaptor->getUniqueID		=  getUniqueID;

	/* Remaining functions filled in by the caller.
	 */
	return;
}

/** Create and return a nil tree node (no token payload)
 */
static	pANTLR3_BASE_TREE	
nil	    (pANTLR3_BASE_TREE_ADAPTOR adaptor)
{
	return	adaptor->create(adaptor, NULL);
}

/** Return a duplicate of the entire tree (implementation provided by the 
 *  BASE_TREE interface.)
 */
static	pANTLR3_BASE_TREE	
   dupTree  (pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t)
{
	return	t->dupTree(t);
}

/** Add a child to the tree t.  If child is a flat tree (a list), make all
 *  in list children of t. Warning: if t has no children, but child does
 *  and child isNil then it is ok to move children to t via
 *  t.children = child.children; i.e., without copying the array.  This
 *  is for construction and I'm not sure it's completely general for
 *  a tree's addChild method to work this way.  Make sure you differentiate
 *  between your tree's addChild and this parser tree construction addChild
 *  if it's not ok to move children to t with a simple assignment.
 */
static	void	
   addChild (pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, pANTLR3_BASE_TREE child)
{
	t->addChild(t, child);
}

/** Use the adaptor implementation to add a child node with the supplied token
 */
static	void		
   addChildToken		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, pANTLR3_COMMON_TOKEN child)
{
	adaptor->addChild(adaptor, t, adaptor->create(adaptor, child));
}

/** If oldRoot is a nil root, just copy or move the children to newRoot.
 *  If not a nil root, make oldRoot a child of newRoot.
 *
 *    old=^(nil a b c), new=r yields ^(r a b c)
 *    old=^(a b c), new=r yields ^(r ^(a b c))
 *
 *  If newRoot is a nil-rooted single child tree, use the single
 *  child as the new root node.
 *
 *    old=^(nil a b c), new=^(nil r) yields ^(r a b c)
 *    old=^(a b c), new=^(nil r) yields ^(r ^(a b c))
 *
 *  If oldRoot was null, it's ok, just return newRoot (even if isNil).
 *
 *    old=null, new=r yields r
 *    old=null, new=^(nil r) yields ^(nil r)
 *
 *  Return newRoot.  Throw an exception if newRoot is not a
 *  simple node or nil root with a single child node--it must be a root
 *  node.  If newRoot is ^(nil x) return x as newRoot.
 *
 *  Be advised that it's ok for newRoot to point at oldRoot's
 *  children; i.e., you don't have to copy the list.  We are
 *  constructing these nodes so we should have this control for
 *  efficiency.
 */
static	pANTLR3_BASE_TREE	
   becomeRoot	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE newRootTree, pANTLR3_BASE_TREE oldRootTree)
{
	/* Protect against tree rewrites if we are in some sort of error
	 * state, but have tried to recover. In C we can end up with a null pointer
	 * for a tree that was not produced.
	 */
	if	(newRootTree == NULL)
	{
		return	oldRootTree;
	}

	/* root is just the new tree as is if there is no
	 * current root tree.
	 */
	if	(oldRootTree == NULL)
	{
		return	newRootTree;
	}

	/* Produce ^(nil real-node)
	 */
	if	(newRootTree->isNil(newRootTree))
	{
		if	(newRootTree->getChildCount(newRootTree) > 1)
		{
			/* TODO: Handle tree exceptions 
			 */
			fprintf(stderr, "More than one node as root! TODO: Create tree exception hndling\n");
			return newRootTree;
		}

		/* The new root is the first child
		 */
		newRootTree = newRootTree->getChild(newRootTree, 0);
	}

	/* Add old root into new root. addChild takes care of the case where oldRoot
	 * is a flat list (nill rooted tree). All children of oldroot are added to
	 * new root.
	 */
	newRootTree->addChild(newRootTree, oldRootTree);

	/* Always returns new root structure
	 */
	return	newRootTree;

}

/** Transform ^(nil x) to x 
 */
static	pANTLR3_BASE_TREE	
   rulePostProcessing	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE root)
{

	if (root != NULL && root->isNil(root) && root->getChildCount(root) == 1)
	{
		root = root->getChild(root, 0);
	}

	return root;
}
 
/** Use the adaptor interface to set a new tree node with the supplied token
 *  to the root of the tree.
 */
static	pANTLR3_BASE_TREE	
   becomeRootToken	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_COMMON_TOKEN newRoot, pANTLR3_BASE_TREE oldRoot)
{
	return	adaptor->becomeRoot(adaptor, adaptor->create(adaptor, newRoot), oldRoot);
}

/** Use the super class supplied create() method to create a new node
 *  from the supplied token.
 */
static	pANTLR3_BASE_TREE	
   createTypeToken	(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_COMMON_TOKEN fromToken)
{
	/* Create the new token
	 */
	fromToken = adaptor->createTokenFromToken(adaptor, fromToken);

	/* Set the type of the new token to that supplied
	 */
	fromToken->setType(fromToken, tokenType);

	/* Return a new node based upon this token
	 */
	return	adaptor->create(adaptor, fromToken);
}

static	pANTLR3_BASE_TREE	
   createTypeTokenText	(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_COMMON_TOKEN fromToken, pANTLR3_UINT8 text)
{
	/* Create the new token
	 */
	fromToken = adaptor->createTokenFromToken(adaptor, fromToken);

	/* Set the type of the new token to that supplied
	 */
	fromToken->setType(fromToken, tokenType);

	/* Set the text of the token accoridngly
	 */
	fromToken->setText8(fromToken, text);

	/* Return a new node based upon this token
	 */
	return	adaptor->create(adaptor, fromToken);
}

static	pANTLR3_BASE_TREE	
   createTypeText	(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT32 tokenType, pANTLR3_UINT8 text)
{
	pANTLR3_COMMON_TOKEN	fromToken;

	/* Create the new token
	 */
	fromToken = adaptor->createToken(adaptor, tokenType, text);

	/* Return a new node based upon this token
	 */
	return	adaptor->create(adaptor, fromToken);
}

/** Dummy implementation - will be supplied by super class
 */
static	ANTLR3_UINT32	
   getType		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t)
{
	return	0;
}

/** Dummy implementation - will be supplied by super class
 */
static	void		
   setType		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t, ANTLR3_UINT32 type)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement setType()\n");
}

/** Dummy implementation - will be supplied by super class
 */
static	pANTLR3_STRING	
   getText		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE t)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement getText()\n");
	return	NULL;
}

/** Dummy implementation - will be supplied by super class
 */
static	void		
   setText		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_STRING t)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement setText()\n");
}
/** Dummy implementation - will be supplied by super class
 */
static	void		
setText8		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 t)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement setText()\n");
}

static	pANTLR3_BASE_TREE	
   getChild		(pANTLR3_BASE_TREE_ADAPTOR adaptor, ANTLR3_UINT64 i)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement getChild()\n");
	return NULL;
}

static	pANTLR3_UINT64	
   getChildCount	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE tree)
{
	fprintf(stderr, "Internal error - implementor of superclass containoing ANTLR3_TREE_ADAPTOR did not implement getChildCount()\n");
	return NULL;
}

/** Returns a uniqueID for the node. Because this is the C implementation
 *  we can just use its address suitably converted/cast to an integer.
 */
static	ANTLR3_UINT64	
   getUniqueID		(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_BASE_TREE node)
{
	return	ANTLR3_UINT64_CAST(node);
}
