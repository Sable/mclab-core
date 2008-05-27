# current a program consists of a list of statements, which separated by ;
# except single line comment 
# Notes 
#  - Current grammar doesn't support 
#		- cell, structure, object, ...
#  		- list of lhs variables, i.g.  [a, b]=foo();
#  - Another way to run the test : java Main Test.m >log.txt

# following are some test cases

% expressions
a = 1 & 5;
a = 1 && 5;
a = 1 & 0;
a = 1 && 0;

a = b;
a = a + b;
a = a - b*c;
a = a - b*c+d/7;
a = a **5* b;

% matrix
e =[];
e =[;];
% a =[1,	%comment can't handle
%	 2];
a =[1, 2];
a =[1, 2; 3 , 4];
% java Main Test0.m >log.txt
% Mini-set of grammar, including 
# comments, lhs expr needs to be: (expr)

;
x = 5;
c =  (5 ...   
  );
y = (z>=5);  
 a = (c >=(d!=5));
% Method Access
 foo(0);
 foo(1);
 foo(1, 5);
a = foo(1);
% Matrix access
a(1)=5;
a(:)=6;
a(2,:)=7;

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


# some operators
x = a**5;
y = b^6;


a= a > b;
a = a < b;
a = c == d;
a = e <= f;
a = g >= k;
a= l != m;
a = n ~= q;

a =!a == b;
a = c != !d;
a = e ~= ~f;


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
a = (a=5);

