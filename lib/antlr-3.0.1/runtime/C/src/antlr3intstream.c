/** \file
 * Implementation of superclass elements of an ANTLR3 int stream.
 * The only methods required are an allocator and a destructor.
 */
#include    <antlr3intstream.h>

static	void	freeStream    (pANTLR3_INT_STREAM stream);

ANTLR3_API pANTLR3_INT_STREAM
antlr3IntStreamNew()
{
    pANTLR3_INT_STREAM	stream;

    /* Allocate memory
     */
    stream  = (pANTLR3_INT_STREAM) ANTLR3_MALLOC(sizeof(ANTLR3_INT_STREAM));

    if	(stream == NULL)
    {
	return	(pANTLR3_INT_STREAM) ANTLR3_FUNC_PTR(ANTLR3_ERR_NOMEM);
    }

    stream->free    =  freeStream;

    return stream;
}

static	void	
freeStream    (pANTLR3_INT_STREAM stream)
{
    ANTLR3_FREE(stream);
}
