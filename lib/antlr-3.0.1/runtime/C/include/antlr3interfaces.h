/** \file
 * Declarations for all the antlr3 C runtime intrfaces/classes. This
 * allows the structures that define th einterfaces to contain pointers to
 * each other without trying to sort out the cyclic inter-dependancies that
 * would otherwise result.
 */
#ifndef	_ANTLR3_INTERFACES_H
#define	_ANTLR3_INTERFACES_H

typedef	struct ANTLR3_INT_STREAM_struct		    *pANTLR3_INT_STREAM;

typedef struct ANTLR3_BASE_RECOGNIZER_struct	    *pANTLR3_BASE_RECOGNIZER;

typedef struct ANTLR3_BITSET_struct		    *pANTLR3_BITSET;

typedef struct ANTLR3_TOKEN_FACTORY_struct	    *pANTLR3_TOKEN_FACTORY;
typedef struct ANTLR3_COMMON_TOKEN_struct	    *pANTLR3_COMMON_TOKEN;

typedef struct ANTLR3_EXCEPTION_struct		    *pANTLR3_EXCEPTION;

typedef struct ANTLR3_HASH_BUCKET_struct	    *pANTLR3_HASH_BUCKET;
typedef struct ANTLR3_HASH_ENTRY_struct		    *pANTLR3_HASH_ENTRY;
typedef struct ANTLR3_HASH_ENUM_struct		    *pANTLR3_HASH_ENUM;
typedef struct ANTLR3_HASH_TABLE_struct		    *pANTLR3_HASH_TABLE;

typedef struct ANTLR3_LIST_struct		    *pANTLR3_LIST;
typedef struct ANTLR3_VECTOR_FACTORY_struct	    *pANTLR3_VECTOR_FACTORY;
typedef struct ANTLR3_VECTOR_struct		    *pANTLR3_VECTOR;
typedef struct ANTLR3_STACK_struct		    *pANTLR3_STACK;

typedef struct ANTLR3_INPUT_STREAM_struct	    *pANTLR3_INPUT_STREAM;
typedef struct ANTLR3_LEX_STATE_struct		    *pANTLR3_LEX_STATE;

typedef struct ANTLR3_STRING_FACTORY_struct	    *pANTLR3_STRING_FACTORY;
typedef struct ANTLR3_STRING_struct		    *pANTLR3_STRING;

typedef struct ANTLR3_TOKEN_SOURCE_struct	    *pANTLR3_TOKEN_SOURCE;
typedef	struct ANTLR3_TOKEN_STREAM_struct	    *pANTLR3_TOKEN_STREAM;
typedef	struct ANTLR3_COMMON_TOKEN_STREAM_struct    *pANTLR3_COMMON_TOKEN_STREAM;

typedef struct ANTLR3_CYCLIC_DFA_struct		    *pANTLR3_CYCLIC_DFA;

typedef	struct ANTLR3_LEXER_struct		    *pANTLR3_LEXER;
typedef struct ANTLR3_PARSER_struct		    *pANTLR3_PARSER;

typedef	struct ANTLR3_BASE_TREE_struct		    *pANTLR3_BASE_TREE;
typedef struct ANTLR3_COMMON_TREE_struct	    *pANTLR3_COMMON_TREE;
typedef	struct ANTLR3_ARBORETUM_struct		    *pANTLR3_ARBORETUM;
typedef	struct ANTLR3_PARSE_TREE_struct		    *pANTLR3_PARSE_TREE;

typedef struct ANTLR3_TREE_NODE_STREAM_struct	    *pANTLR3_TREE_NODE_STREAM;
typedef	struct ANTLR3_COMMON_TREE_NODE_STREAM_struct
						    *pANTLR3_COMMON_TREE_NODE_STREAM;
typedef struct ANTLR3_TREE_WALK_STATE_struct	    *pANTLR3_TREE_WALK_STATE;

typedef struct ANTLR3_BASE_TREE_ADAPTOR_struct	    *pANTLR3_BASE_TREE_ADAPTOR;
typedef	struct ANTLR3_COMMON_TREE_ADAPTOR_struct    *pANTLR3_COMMON_TREE_ADAPTOR;

typedef struct ANTLR3_TREE_PARSER_struct	    *pANTLR3_TREE_PARSER;

typedef struct ANTLR3_INT_TRIE_struct		    *pANTLR3_INT_TRIE;

typedef struct ANTLR3_REWRITE_RULE_ELEMENT_STREAM_struct
						    *pANTLR3_REWRITE_RULE_ELEMENT_STREAM;
typedef	struct ANTLR3_REWRITE_RULE_ELEMENT_STREAM_struct
						    *pANTLR3_REWRITE_RULE_TOKEN_STREAM;
typedef	struct ANTLR3_REWRITE_RULE_ELEMENT_STREAM_struct
						    *pANTLR3_REWRITE_RULE_SUBTREE_STREAM;

#endif
