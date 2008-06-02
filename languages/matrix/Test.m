% current a program consists of a list of statements, which separated by ;
% except single line comment 
% Notes 
%  - Current grammar doesn't support 
%		- cell, structure, object, ...
%  		- list of lhs variables, i.g.  [a, b]=foo();
%  - Run it : 
%     - ant test
%     - java Main Test0.m >log.txt

% following are some test cases

%================================
% ERROR : No annoteted comments
%================================

x = 0; n = 1;
% comment-for
for a=1:3;  % comment-assign
x = x+1; break;  n = n*2, % comment-stmt 
end

x = 0; n = 1;
% comment-while
while ~feof(fid)  % comment-expr
x = x+1; continue;  n = n*2, % comment-stmt 
end



x = 0; n = 1;
% comment-for
for a=1:3;  % comment-assign
x = x+1;   n = n*2, % comment-stmt 
end

x = 0; n = 1;
% comment-while
while ~feof(fid)  % comment-expr
x = x+1;   n = n*2, % comment-stmt 
end

% Accept statement_separator =  SEMICOLON? LINE_TERMINATOR
x=0;n=1;y=1:10;b=1:3;
for i=b
  x = x+1;
	for j=1:5
	  x = x + y(j)
	end
  n = n*2;
end

for a=1:3;  x = x+1;   n = n*2; end

for j = (1 : 5)
	x = (x + y(j))
end

%b=1:3
% a = 1,2,3
% b = 2;d,f
% c = 3; d=4, a = a - b*c^5+d/7
%matrixA = [1, 2; 3, a~=5; a, a+6;];


x=0;n=1;

for (a = 1 : 3)
x = (x + 1)
n = (n * 2)
end

b = 1 : 3 ;
for (i = b);
x = (x + 1);
n = (n * 2);
end

a = b
x = 5;

x = @foo;

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

% java Main Test0.m >log.txt
% Mini-set of grammar, including 
% comments, lhs expr needs to be: (expr)

;
x = 5;
% c =  (5 ...   
%   );
y = (z>=5);  
 a = (c >=(d!=5));
 
% Method Access
 foo();
 foo(a);
 foo(7, 5, a+b);
a = foo(x<=y);


% matrix
e =[];
e =[;];

m = [1 2
3 4
]

e =[1, 2,
    3, 4]


% ERROR 
a = M'+M'

matrixA = [1, 2; 3, a-5; a, a+6]
% matrixA = [N.', ~~true; M', a---3; ++2,  N.'.']
matrixA = [N.', ~~true; N.'.', M'; a---3, ++2]

a = [1]
  
  % hello comment
a = [1,;2;]
matrixA = [1, 2; 3, a-5; a, a+6;];


a =[1, 2];
b =[1, 2,;];
c =[1, 2,; 3, 4,;];

a = [1,;2;]
matrixA = [1, 2; 3, a-5; a, a+6;];

% Matrix access
a(1)=5;
a(:)=6;
a(2,:)=7;

;
% Continuation/ Elipsis
% a = ...
%  b;
#c =  a + ...   
%  d;
% '/' left-div series operators
a = a \ b;
% c =  a \    ... 
%   d;
a = a .\ b;		


% some operators
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

% Matrix...
matrixA = [1, 2; 3, a=5; a, a+6];
matrixA = [1, 2, 3, a=5];

matrixX = [];
matrixY = [;];

matrixZ = [1,];
matrixA = [1, 2; 3, a=5; [a, a+6]];
Mb = matrixA;
Mc = matrixA(1);
Mc = matrixA(1,:);
% Mc = matrixA();		% RHS, OK, matrix_access ==  method_invocation
matrixA(1, 1) = a;
matrixA(:) = [a, 9];
matrixA(1, :) = a;
matrixC(1:5) = a+6;		

matrixC = 1:1:9;
matrixC = 1:1:a+7;
matrixC = 3:9;
matrixD = 1:a+7;
