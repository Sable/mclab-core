#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "SimpleCLexer.h"
#import "SimpleCParser.h"
#import "SimpleCTP.h"
#import "stdio.h"
#include <unistd.h>

int main(int argc, const char * argv[]) {
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    if (argc < 2) {
        NSLog(@"provide the input file, please");
        return 1;
    }
	
	NSString *string = [NSString stringWithContentsOfFile:[NSString stringWithCString:argv[1]]];
	NSLog(@"input is : %@", string);

	ANTLRStringStream *stream = [[ANTLRStringStream alloc] initWithStringNoCopy:string];
	SimpleCLexer *lexer = [[SimpleCLexer alloc] initWithCharStream:stream];
	
//    id<ANTLRToken> currentToken;
//    while ((currentToken = [lexer nextToken]) && [currentToken type] != ANTLRTokenTypeEOF) {
//        NSLog(@"%@", currentToken);
//    }
	
	ANTLRCommonTokenStream *tokenStream = [[ANTLRCommonTokenStream alloc] initWithTokenSource:lexer];
	SimpleCParser *parser = [[SimpleCParser alloc] initWithTokenStream:tokenStream];
	ANTLRCommonTree *program_tree = [[parser program] tree];

	NSLog(@"tree: %@", [program_tree treeDescription]);
	ANTLRCommonTreeNodeStream *treeStream = [[ANTLRCommonTreeNodeStream alloc] initWithTree:program_tree];
	SimpleCTP *walker = [[SimpleCTP alloc] initWithTreeNodeStream:treeStream];
	[walker program];

	[lexer release];
	[stream release];
	[tokenStream release];
	[parser release];
	[treeStream release];
	[walker release];

	[pool release];

    // use this for ObjectAlloc
    while(1) sleep(60);
	return 0;
}