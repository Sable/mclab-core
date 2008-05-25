
This program implements part of MATLAB lexical structure and part of grammar 
that related to matrix. 

# Notes 
 - Current version just uses JFlex and Beaver. 
 	So it is easy for testing Lexer.

 - To build, please use: ant
 - To parse 'Test.m', please use: ant test
 		or : java Main Test.m >log.txt
 		
 - Current grammar doesn't support 
		- cell, structure, object, ...
  		- list of lhs variables, i.g.  [a, b]=foo();
 		
 - Grammar structure is changed for clarity.
   Old AST classes are removed, corresponding AST is under developing... 
   (JL 2008.05.25)
 