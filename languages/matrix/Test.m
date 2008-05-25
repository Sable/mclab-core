# current a program consists of a list of statements, which separated by ;
# except single line comment 
# Notes 
#  - Current grammar doesn't support 
#		- cell, structure, object, ...
#  		- list of lhs variables, i.g.  [a, b]=foo();
#  - Another way to run the test : java Main Test.m >log.txt

# following are some test cases
;
% Continuation/ Elipsis
a = ...
  b;
c =  a + ...   
  d;
% '/' left-div series operators
a = a \ b;
c =  a \    ... 
  d;
a = a .\ b;		
a \= b;
a .\= b;

# some operators
x = a**5;
y = b^6;

x ^= 6;
y **= 7;

a > b;
a < b;
c == d;
e <= f;
g >= k;
l != m;
n ~= q;

!a == b;
c != !d;
e ~= ~f;

a .+= b;
a ./= b;
c .**= d;
c .^= d;
	
a='string';
b="DOUBLUE QU";
a = 5;

# Matrix...
matrixA = [1, 2; 3, a=5; a, a+6];
matrixA = [1, 2, 3, a=5];

matrixC = 1:9;
matrixD = 1:a+7;
matrixC(1:5) = a+6;		

matrixX = [];
matrixY = [;];
matrixZ = [1,];
matrixA = [1, 2; 3, a=5; [a, a+6]];
Mb = matrixA;
Mc = matrixA(1);
Mc = matrixA(1,:);
Mc = matrixA();		% RHS, OK, matrix_access ==  method_invocation

matrixA(1, 1) = a;
matrixA(:) = [a, 9];
matrixA(1, :) = a;

%	 matrixA() = a;		% LHS, report error

# the precedence of this kind of statements need check more
a = a=5;

# Currently, the grammar doesn't support Left-Div series operators
% a\b
% c.\d
% e .\= f
