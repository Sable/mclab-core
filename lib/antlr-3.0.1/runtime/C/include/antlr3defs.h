/** \file
 * Basic type and constant definitions for ANTLR3 Runtime.
 */
#ifndef	_ANTLR3DEFS_H
#define	_ANTLR3DEFS_H


/* Following are for genreaed code, they are not referenced internally!!!
 */
#if !defined(ANTLR3_HUGE) && !defined(ANTLR3_AVERAGE) && !defined(ANTLR3_SMALL)
#define	ANTLR3_AVERAGE
#endif

#ifdef	ANTLR3_HUGE
#ifndef	ANTLR3_SIZE_HINT
#define	ANTLR3_SIZE_HINT    2049
#endif
#ifndef	ANTLR3_LIST_SIZE_HINT
#define	ANTLR3_LIST_SIZE_HINT 127
#endif
#endif

#ifdef	ANTLR3_AVERAGE
#ifndef	ANTLR3_SIZE_HINT
#define	ANTLR3_SIZE_HINT    1025
#define	ANTLR3_LIST_SIZE_HINT 63
#endif
#endif

#ifdef	ANTLR3_SMALL
#ifndef	ANTLR3_SIZE_HINT
#define	ANTLR3_SIZE_HINT    211
#define	ANTLR3_LIST_SIZE_HINT 31
#endif
#endif

/* Common definitions come first
 */
#include    <antlr3errors.h>

#define	ANTLR3_ENCODING_LATIN1	0
#define ANTLR3_ENCODING_UCS2	1
#define	ANTLR3_ENCODING_UTF8	2
#define	ANTLR3_ENCODING_UTF32	3

/* Work out what operating system/compiler this is. We just do this once
 * here and use an internal symbol after this.
 */
#ifdef	_WIN32
#ifndef	ANTLR3_WINDOWS
#define	ANTLR3_WINDOWS
#define	ANTLR3_WIN32
#endif
#endif

#ifdef	_WIN64
#define	ANTLR3_WIN64
#define	ANTLR3_USE_64BIT
#endif

#ifdef	ANTLR3_WINDOWS 

#define	WINDOWS_LEAN_AND_MEAN

/* Allow VC 8 (vs2005) to use 'secure' versions of various functions such as sprintf
 */
#define	_CRT_SECURE_NO_DEPRECATE 

#include    <windows.h>
#include    <stdio.h>
#include    <sys/types.h>
#include    <sys/stat.h>
#include    <stdarg.h>

#define	ANTLR3_API  __declspec(dllexport)
#define	ANTLR3_CDECL __cdecl
#define ANTLR3_FASTCALL __fastcall

typedef	INT32	ANTLR3_CHAR,	*pANTLR3_CHAR;
typedef	UINT32	ANTLR3_UCHAR,	*pANTLR3_UCHAR;

typedef	INT8	ANTLR3_INT8,	*pANTLR3_INT8;
typedef	INT16	ANTLR3_INT16,	*pANTLR3_INT16;
typedef	INT32	ANTLR3_INT32,	*pANTLR3_INT32;
#ifdef	ANTLR3_USE_64BIT
typedef	INT64	ANTLR3_INT64,	*pANTLR3_INT64;
#else
typedef	INT32	ANTLR3_INT64,	*pANTLR3_INT64;
#endif
typedef	UINT8	ANTLR3_UINT8,	*pANTLR3_UINT8;
typedef	UINT16	ANTLR3_UINT16,	*pANTLR3_UINT16;
typedef	UINT32	ANTLR3_UINT32,	*pANTLR3_UINT32;
#ifdef	ANTLR3_USE_64BIT
typedef	UINT64	ANTLR3_UINT64,	*pANTLR3_UINT64;
#else
typedef	UINT32	ANTLR3_UINT64,	*pANTLR3_UINT64;
#endif
typedef UINT64  ANTLR3_BITWORD, *pANTLR3_BITWORD;

typedef	UINT32	ANTLR3_BOOLEAN, *pANTLR3_BOOLEAN;

#define	ANTLR3_INLINE	__inline

typedef FILE *	    ANTLR3_FDSC;
typedef	struct stat ANTLR3_FSTAT_STRUCT;

#define	ANTLR3_FUNC_PTR(ptr) (void *)((ANTLR3_UINT64)(ptr))
#define ANTLR3_UINT64_CAST(ptr)   (ANTLR3_UINT64)(ptr)
#define	ANTLR3_UINT32_CAST(ptr)	  (ANTLR3_UINT32)((ANTLR3_UINT64)(ptr))
#define	ANTLR3_UINT64_LIT(lit)	    lit##ULL

#ifdef	ANTLR3_WIN32
#endif

#ifdef	ANTLR3_WIN64
#endif

/* Warnings that are over-zealous such as complaining about strdup, we
 * can turn off.
 */

/* Don't complain about "deprecated" functions such as strdup
 */
#pragma warning( disable : 4996 )

#else

/* Include configure generated header file
 */
#include	<antlr3config.h>

#include <stdio.h>

#if HAVE_SYS_TYPES_H
# include <sys/types.h>
#endif

#if HAVE_SYS_STAT_H
# include <sys/stat.h>
#endif

#if STDC_HEADERS
# include   <stdlib.h>
# include   <stddef.h>
# include   <stdarg.h>
#else
# if HAVE_STDLIB_H
#  include  <stdlib.h>
# endif
# if HAVE_STDARG_H
#  include  <stdarg.h>
# endif
#endif

#if HAVE_STRING_H
# if !STDC_HEADERS && HAVE_MEMORY_H
#  include <memory.h>
# endif
# include <string.h>
#endif

#if HAVE_STRINGS_H
# include <strings.h>
#endif

#if HAVE_INTTYPES_H
# include <inttypes.h>
#endif

#if HAVE_STDINT_H
# include <stdint.h>
#endif

#if HAVE_UNISTD_H
# include <unistd.h>
#endif

#ifdef	HAVE_MALLOC_H
# include    <malloc.h>
#else
# ifdef	HAVE_SYS_MALLOC_H
#  include    <sys/malloc.h>
# endif
#endif

#ifdef  HAVE_CTYPE_H
# include   <ctype.h>
#endif

/* Some platforms define a macro, index() in string.h. AIX is
 * one of these for instance. We must get rid of that definition
 * as we use ->index all over the place. defining macros like this in system header
 * files is a really bad idea, but I doubt that IBM will listen to me ;-)
 */
#ifdef	index
#undef	index
#endif

#define _stat   stat

#define ANTLR3_API
#define	ANTLR3_CDECL
#define ANTLR3_FASTCALL

/* Inherit type definitions for autoconf
 */
typedef int32_t		    ANTLR3_CHAR,    *pANTLR3_CHAR;
typedef uint32_t	    ANTLR3_UCHAR,   *pANTLR3_UCHAR;

typedef int8_t		    ANTLR3_INT8,    *pANTLR3_INT8;
typedef int16_t		    ANTLR3_INT16,   *pANTLR3_INT16;
typedef int32_t		    ANTLR3_INT32,   *pANTLR3_INT32;
#ifdef	ANTLR3_USE_64BIT
typedef int64_t		    ANTLR3_INT64,   *pANTLR3_INT64;
#else
typedef int32_t		    ANTLR3_INT64,   *pANTLR3_INT64;
#endif

typedef uint8_t	    	    ANTLR3_UINT8,   *pANTLR3_UINT8;
typedef uint16_t      	    ANTLR3_UINT16,  *pANTLR3_UINT16;
typedef uint32_t	    ANTLR3_UINT32,  *pANTLR3_UINT32;
#ifdef	ANTLR3_USE_64BIT
typedef uint64_t	    ANTLR3_UINT64,  *pANTLR3_UINT64;
#else
typedef uint32_t	    ANTLR3_UINT64,  *pANTLR3_UINT64;
#endif
typedef uint64_t	    ANTLR3_BITWORD, *pANTLR3_BITWORD;

typedef uint32_t	    ANTLR3_BOOLEAN, *pANTLR3_BOOLEAN;

#define ANTLR3_INLINE   inline
#define	ANTLR3_API

typedef FILE *	    ANTLR3_FDSC;
typedef	struct stat ANTLR3_FSTAT_STRUCT;

#ifdef	ANTLR3_64
#define	ANTLR3_FUNC_PTR(ptr) (void *)((ANTLR3_UINT64)(ptr))
#define ANTLR3_UINT64_CAST(ptr)   (ANTLR3_UINT64)(ptr))
#define	ANTLR3_UINT32_CAST(ptr)	  (ANTLR3_UINT32)((ANTLR3_UINT64)(ptr))
#else
#define	ANTLR3_FUNC_PTR(ptr) (void *)((ANTLR3_UINT32)(ptr))
#define ANTLR3_UINT64_CAST(ptr)   (ANTLR3_UINT64)((ANTLR3_UINT32)(ptr))
#define	ANTLR3_UINT32_CAST(ptr)	  (ANTLR3_UINT32)(ptr)
#endif
#define	ANTLR3_UINT64_LIT(lit)	    lit##ULL

#endif

/* Predeclare the typedefs for all the interfaces, then 
 * they can be inter-dependant and we will let the linker
 * sort it out for us.
 */
#include    <antlr3interfaces.h>

/* Prototypes
 */
#define	ANTLR3_MALLOC(request)		    calloc  (1, (size_t)(request))
#define ANTLR3_REALLOC(current, request)    realloc ((void *)(current), (size_t)(request))
#define	ANTLR3_FREE(ptr)		    free    ((void *)(ptr))
#define	ANTLR3_FREE_FUNC		    free
#define	ANTLR3_STRDUP(instr)		    (pANTLR3_UINT8)(strdup  ((const char *)(instr)))
#define	ANTLR3_MEMMOVE(target, source, size)	memmove((void *)(target), (const void *)(source), (size_t)(size))
#define	ANTLR3_MEMSET(target, byte, size)	memset((void *)(target), (int)(byte), (size_t)(size))

ANTLR3_API pANTLR3_INT_TRIE	    antlr3IntTrieNew		    (ANTLR3_UINT32 depth);

ANTLR3_API pANTLR3_BITSET	    antlr3BitsetNew		    (ANTLR3_UINT32 numBits);
ANTLR3_API pANTLR3_BITSET	    antlr3BitsetOf		    (ANTLR3_INT32 bit, ...);
ANTLR3_API pANTLR3_BITSET	    antlr3BitsetList		    (pANTLR3_HASH_TABLE list);
ANTLR3_API pANTLR3_BITSET	    antlr3BitsetCopy		    (pANTLR3_UINT64 inSet, ANTLR3_UINT32 numElements);
ANTLR3_API pANTLR3_BITSET	    antlr3BitsetLoad		    (ANTLR3_UINT32 ec, pANTLR3_UINT64 bset);
ANTLR3_API void			    antlr3BitsetSetAPI		    (pANTLR3_BITSET bitset);


ANTLR3_API pANTLR3_BASE_RECOGNIZER  antlr3BaseRecognizerNew	    (ANTLR3_UINT32 type, ANTLR3_UINT32 sizeHint);
ANTLR3_API void			    antlr3RecognitionExceptionNew   (pANTLR3_BASE_RECOGNIZER recognizer);
ANTLR3_API void			    antlr3MTExceptionNew	    (pANTLR3_BASE_RECOGNIZER recognizer);
ANTLR3_API void			    antlr3MTNExceptionNew	    (pANTLR3_BASE_RECOGNIZER recognizer);
ANTLR3_API pANTLR3_HASH_TABLE	    antlr3HashTableNew		    (ANTLR3_UINT32 sizeHint);
ANTLR3_API ANTLR3_UINT32	    antlr3Hash			    (void * key, ANTLR3_UINT32 keylen);
ANTLR3_API pANTLR3_HASH_ENUM	    antlr3EnumNew		    (pANTLR3_HASH_TABLE table);
ANTLR3_API pANTLR3_LIST		    antlr3ListNew		    (ANTLR3_UINT32 sizeHint);
ANTLR3_API pANTLR3_VECTOR_FACTORY   antlr3VectorFactoryNew	    (ANTLR3_UINT32 sizeHint);
ANTLR3_API pANTLR3_VECTOR	    antlr3VectorNew		    (ANTLR3_UINT32 sizeHint);
ANTLR3_API pANTLR3_STACK	    antlr3StackNew		    (ANTLR3_UINT32 sizeHint);

ANTLR3_API ANTLR3_UCHAR		    antlr3c8toAntlrc		    (ANTLR3_INT8 inc);

ANTLR3_API pANTLR3_EXCEPTION	    antlr3ExceptionNew		    (ANTLR3_UINT32 exception, void * name, void * message, ANTLR3_BOOLEAN freeMessage);

ANTLR3_API pANTLR3_INPUT_STREAM	    antlr3AsciiFileStreamNew	    (pANTLR3_UINT8 fileName);

ANTLR3_API pANTLR3_INPUT_STREAM	    antlr3NewAsciiStringInPlaceStream   (pANTLR3_UINT8 inString, ANTLR3_UINT64 size, pANTLR3_UINT8 name);
ANTLR3_API pANTLR3_INPUT_STREAM	    antlr3NewUCS2StringInPlaceStream	(pANTLR3_UINT16 inString, ANTLR3_UINT64 size, pANTLR3_UINT16 name);
ANTLR3_API pANTLR3_INPUT_STREAM	    antlr3NewAsciiStringCopyStream	(pANTLR3_UINT8 inString, ANTLR3_UINT64 size, pANTLR3_UINT8 name);

ANTLR3_API pANTLR3_INT_STREAM	    antlr3IntStreamNew		    (void);

ANTLR3_API pANTLR3_STRING_FACTORY   antlr3StringFactoryNew	    (void);
ANTLR3_API pANTLR3_STRING_FACTORY   antlr3UCS2StringFactoryNew	    (void);

ANTLR3_API pANTLR3_COMMON_TOKEN	    antlr3CommonTokenNew	    (ANTLR3_UINT32 ttype);
ANTLR3_API pANTLR3_TOKEN_FACTORY    antlr3TokenFactoryNew	    (pANTLR3_INPUT_STREAM input);
ANTLR3_API void			    antlr3SetTokenAPI		    (pANTLR3_COMMON_TOKEN token);

ANTLR3_API pANTLR3_LEXER	    antlr3LexerNewStream	    (ANTLR3_UINT32 sizeHint, pANTLR3_INPUT_STREAM input);
ANTLR3_API pANTLR3_LEXER	    antlr3LexerNew		    (ANTLR3_UINT32 sizeHint);
ANTLR3_API pANTLR3_PARSER	    antlr3ParserNewStream	    (ANTLR3_UINT32 sizeHint, pANTLR3_TOKEN_STREAM tstream);
ANTLR3_API pANTLR3_PARSER	    antlr3ParserNew		    (ANTLR3_UINT32 sizeHint);

ANTLR3_API pANTLR3_COMMON_TOKEN_STREAM	antlr3CommonTokenStreamSourceNew(ANTLR3_UINT32 hint, pANTLR3_TOKEN_SOURCE source);
ANTLR3_API pANTLR3_COMMON_TOKEN_STREAM	antlr3CommonTokenStreamNew(ANTLR3_UINT32 hint);

ANTLR3_API pANTLR3_BASE_TREE_ADAPTOR	    ANTLR3_TREE_ADAPTORNew  (pANTLR3_STRING_FACTORY strFactory);
ANTLR3_API pANTLR3_COMMON_TREE	    antlr3CommonTreeNew		    (void);
ANTLR3_API pANTLR3_COMMON_TREE	    antlr3CommonTreeNewFromTree	    (pANTLR3_COMMON_TREE tree);
ANTLR3_API pANTLR3_COMMON_TREE	    antlr3CommonTreeNewFromToken    (pANTLR3_COMMON_TOKEN tree);
ANTLR3_API pANTLR3_ARBORETUM	    antlr3ArboretumNew		    (pANTLR3_STRING_FACTORY factory);
ANTLR3_API void			    antlr3SetCTAPI		    (pANTLR3_COMMON_TREE tree);
ANTLR3_API pANTLR3_BASE_TREE	    antlr3BaseTreeNew		    (pANTLR3_BASE_TREE tree);

ANTLR3_API void			    antlr3BaseTreeAdaptorInit	    (pANTLR3_BASE_TREE_ADAPTOR adaptor);

ANTLR3_API pANTLR3_TREE_PARSER	    antlr3TreeParserNewStream	    (ANTLR3_UINT32 sizeHint, pANTLR3_COMMON_TREE_NODE_STREAM ctnstream);

ANTLR3_API ANTLR3_INT32		    antlr3dfaspecialTransition	    (void * ctx, pANTLR3_BASE_RECOGNIZER rec, pANTLR3_INT_STREAM is, pANTLR3_CYCLIC_DFA dfa, ANTLR3_INT32 s);
ANTLR3_API ANTLR3_INT32		    antlr3dfaspecialStateTransition (void * ctx, pANTLR3_BASE_RECOGNIZER rec, pANTLR3_INT_STREAM is, pANTLR3_CYCLIC_DFA dfa, ANTLR3_INT32 s);
ANTLR3_API ANTLR3_INT32		    antlr3dfapredict		    (void * ctx, pANTLR3_BASE_RECOGNIZER rec, pANTLR3_INT_STREAM is, pANTLR3_CYCLIC_DFA cdfa);

ANTLR3_API pANTLR3_COMMON_TREE_NODE_STREAM  antlr3CommonTreeNodeStreamNewTree(pANTLR3_BASE_TREE tree, ANTLR3_UINT32 hint);
ANTLR3_API pANTLR3_COMMON_TREE_NODE_STREAM  antlr3CommonTreeNodeStreamNew(pANTLR3_STRING_FACTORY strFactory, ANTLR3_UINT32 hint);

ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
	    antlr3RewriteRuleTokenStreamNewAE	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description);
ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
	    antlr3RewriteRuleTokenStreamNewAEE	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, void * oneElement);
ANTLR3_API pANTLR3_REWRITE_RULE_TOKEN_STREAM 
	    antlr3RewriteRuleTokenStreamNewAEV	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, pANTLR3_VECTOR vector);
ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
	    antlr3RewriteRuleSubtreeStreamNewAE	(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description);
ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
	    antlr3RewriteRuleSubtreeStreamNewAEE(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, void * oneElement);
ANTLR3_API pANTLR3_REWRITE_RULE_SUBTREE_STREAM 
	    antlr3RewriteRuleSubtreeStreamNewAEV(pANTLR3_BASE_TREE_ADAPTOR adaptor, pANTLR3_UINT8 description, pANTLR3_VECTOR vector);

#endif	/* _ANTLR3DEFS_H	*/
