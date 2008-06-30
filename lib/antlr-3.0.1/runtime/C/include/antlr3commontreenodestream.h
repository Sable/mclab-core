/** \file
 * Definition of the ANTLR3 common tree adaptor.
 */

#ifndef	_ANTLR3_COMMON_TREE_NODE_STREAM__H
#define	_ANTLR3_COMMON_TREE_NODE_STREAM__H

#include    <antlr3defs.h>
#include    <antlr3commontreeadaptor.h>
#include    <antlr3commontree.h>
#include    <antlr3collections.h>
#include    <antlr3intstream.h>
#include    <antlr3string.h>

/** As tokens are cached in the stream for lookahead
 *  we start with a bufer of a certain size, defined here
 *  and increase the size if it overflows.
 */
#define	INITIAL_LOOKAHEAD_BUFFER_SIZE  5

typedef	struct ANTLR3_TREE_NODE_STREAM_struct
{
    /** Any interface that implements this interface (is a 
     *  super structure containing this structure), may store the pointer
     *  to itself here in the super pointer, which is not used by 
     *  the tree node stream. This will point to an implementation
     *  of ANTLR3_COMMON_TREE_NODE_STREAM in this case.
     */
    pANTLR3_COMMON_TREE_NODE_STREAM	ctns;

    /** All input streams implement the ANTLR3_INT_STREAM interface...
     */
    pANTLR3_INT_STREAM	    istream;

    pANTLR3_BASE_TREE	    (*_LT)			(struct ANTLR3_TREE_NODE_STREAM_struct * tns, ANTLR3_INT64 k);

    pANTLR3_BASE_TREE	    (*getTreeSource)		(struct ANTLR3_TREE_NODE_STREAM_struct * tns);

    pANTLR3_BASE_TREE_ADAPTOR
			    (*getTreeAdaptor)		(struct ANTLR3_TREE_NODE_STREAM_struct * tns);

    void		    (*setUniqueNavigationNodes)	(struct ANTLR3_TREE_NODE_STREAM_struct * tns, ANTLR3_BOOLEAN uniqueNavigationNodes);

    pANTLR3_STRING	    (*toString)			(struct ANTLR3_TREE_NODE_STREAM_struct * tns);

    pANTLR3_STRING	    (*toStringSS)		(struct ANTLR3_TREE_NODE_STREAM_struct * tns, pANTLR3_BASE_TREE start, pANTLR3_BASE_TREE stop);

    void		    (*toStringWork)		(struct ANTLR3_TREE_NODE_STREAM_struct * tns, pANTLR3_BASE_TREE start, pANTLR3_BASE_TREE stop, pANTLR3_STRING buf);

    void		    (*free)			(struct ANTLR3_TREE_NODE_STREAM_struct * tns);

}
    ANTLR3_TREE_NODE_STREAM;

typedef	struct ANTLR3_COMMON_TREE_NODE_STREAM_struct
{
    /** Any interface that implements this interface (is a 
     *  super structure containing this structure), may store the pointer
     *  to itself here in the super pointer, which is not used by 
     *  the common tree node stream.
     */
    void			* super;

    /** Pointer to the tree node stream interface
     */
    pANTLR3_TREE_NODE_STREAM	tnstream;

    /* String factory for use by anything that wishes to create strings
     * such as a tree representation or some copy of the text etc.
     */
    pANTLR3_STRING_FACTORY	stringFactory;

    /** Dummy tree node that indicates a descent into a child
     *  tree. Initialized by a call to create a new interface.
     */
    ANTLR3_COMMON_TREE		DOWN;

    /** Dummy tree node that indicates a descent up to a parent
     *  tree. Initialized by a call to create a new interface.
     */
    ANTLR3_COMMON_TREE		UP;

    /** Dummy tree node that indicates the termination point of the
     *  tree. Initialized by a call to create a new interface.
     */
    ANTLR3_COMMON_TREE		EOF_NODE;

    /** Dummy node that is returned if we need to indicate an invalid node
     *  for any reason.
     */
    ANTLR3_COMMON_TREE		INVALID_NODE;

    /** If set to ANTLR3_TRUE then the navigation nodes UP, DOWN are
     *  duplicated rather than reused within the tree.
     */
    ANTLR3_BOOLEAN		uniqueNavigationNodes;

    /** Which tree are we navigating ?
     */
    pANTLR3_BASE_TREE		root;

    /** Pointer to tree adaptor interface that maniplates/builds
     *  the tree.
     */
    pANTLR3_BASE_TREE_ADAPTOR	adaptor;

    /** As we walk down the nodes, we must track parent nodes so we know
     *  where to go after walking the last child of a node.  When visiting
     *  a child, push current node and current index (current index
     *  is first stored in the tree node structure to avoid two stacks.
     */
    pANTLR3_STACK		nodeStack;

    /** Track the last mark() call result value for use in rewind(). 
     */
    ANTLR3_UINT64		lastMarker;

    /** Which node are we currently visiting?
     */
    pANTLR3_BASE_TREE		currentNode;

    /** Which node did we last visit? Used for LT(-1)
     */
    pANTLR3_BASE_TREE		previousNode;

    /** Which child are we currently visiting?  If -1 we have not visited
     *  this node yet; next consume() request will set currentIndex to 0.
     */
    ANTLR3_INT64		currentChildIndex;

    /** What node index did we just consume?  i=0..n-1 for n node trees.
     *  IntStream.next is hence 1 + this value.  Size will be same.
     */
    ANTLR3_INT64		absoluteNodeIndex;

    /** Buffer tree node stream for use with LT(i).  This list grows
     *  to fit new lookahead depths, but consume() wraps like a circular
     *  buffer.
     */
    pANTLR3_BASE_TREE	      * lookAhead;

    /** NUmber of elements available in the lookahead buffer at any point in
     *  time. This is the current size of the array.
     */
    ANTLR3_UINT32		lookAheadLength;

    /** lookAhead[head] is the first symbol of lookahead, LT(1). 
     */
    ANTLR3_UINT32		head;

    /** Add new lookahead at lookahead[tail].  tail wraps around at the
     *  end of the lookahead buffer so tail could be less than head.
     */
    ANTLR3_UINT32		tail;

    /** Calls to mark() may be nested so we have to track a stack of
     *  them.  The marker is an index into this stack.  Index 0 is
     *  the first marker.  This is a List<TreeWalkState>
     */
    pANTLR3_VECTOR		markers;

    /* INTERFACE    */

    void		    (*fill)		(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns, ANTLR3_INT64 k);

    void		    (*addLookahead)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns, pANTLR3_BASE_TREE node);


    ANTLR3_BOOLEAN	    (*hasNext)		(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    pANTLR3_BASE_TREE	    (*next)		(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    pANTLR3_BASE_TREE	    (*handleRootnode)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    pANTLR3_BASE_TREE	    (*visitChild)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns, ANTLR3_UINT64 child);

    void		    (*addNavigationNode)(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns, ANTLR3_UINT32 ttype);

    pANTLR3_BASE_TREE	    (*newDownNode)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    pANTLR3_BASE_TREE	    (*newUpNode)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    void		    (*walkBackToMostRecentNodeWithUnvisitedChildren)

				    (struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    
    ANTLR3_BOOLEAN	    (*hasUniqueNavigationNodes)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    pANTLR3_STRING	    (*toNodesOnlyString)	(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    ANTLR3_UINT32	    (*getLookaheadSize)		(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    void		    (*reset)			(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

    void		    (*free)			(struct ANTLR3_COMMON_TREE_NODE_STREAM_struct * ctns);

}
    ANTLR3_COMMON_TREE_NODE_STREAM;

/** This structure is used to save the state information in the treenodestream
 *  when walking ahead with cyclic DFA or for syntactic predicates,
 *  we need to record the state of the tree node stream.  This
 *  class wraps up the current state of the CommonTreeNodeStream.
 *  Calling mark() will push another of these on the markers stack.
 */
typedef struct ANTLR3_TREE_WALK_STATE_struct
{
    ANTLR3_UINT64	      currentChildIndex;
    ANTLR3_UINT64	      absoluteNodeIndex;
    pANTLR3_BASE_TREE	      currentNode;
    pANTLR3_BASE_TREE	      previousNode;
    ANTLR3_UINT64	      nodeStackSize;
    pANTLR3_BASE_TREE	    * lookAhead;
    ANTLR3_UINT32	      lookAheadLength;
    ANTLR3_UINT32	      tail;
    ANTLR3_UINT32	      head;
}
    ANTLR3_TREE_WALK_STATE;

#endif
