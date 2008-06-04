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
% Testing about switch
%================================
switch a
end

switch a
otherwise
end

switch a
case 1
otherwise
end

switch a
case 1
  a = a * 2 
otherwise
  a = a - 1
end

switch a
case 1
  a = a * 2 
case 2
  a = a / 2 
case 3
  a = a ^ 2 
otherwise
  a = a - 1
end


%================================
% Testing about try
%================================
try
  A = B/C
catch
  A = B
end

try
catch
end


if n < 0 % If n negative, display error message.
	disp('Input must be positive');
elseif rem(n,2) == 0 % If n positive and even, divide by 2.
	A = n/2;
else
	A = (n+1)/2; % If n positive and odd, increment and divide.
end

%================================
% Testing about the stmt_sep 
% if / else / elseif / 
%================================

n = 10; if n < 0; A=1; else
 A = (n+1)/2, end

%-------------------------------
n = 10;  
if  n < 0  % No stmt_sep alow just after if
   A = 11
elseif n > 2
   A = (n+1)/2 
else 
   A = (n+1) 
end
%-------------------------------
n = 10;  
if  n < 0   
   A = -n/2,
end
%-------------------------------
if  n < 0   
   A = -n/2,
else
   A = (n+1)/2, 
end
%-------------------------------
if  n < 0   
   A = -n/2,
elseif n > 2
   A = (n+1)/2, 
end
%-------------------------------
n = 10;  
if  n < 0   
   A = -n/2,
elseif n > 10
   A = (n+1)/2, 
elseif n > 6
   A = (n+1)/2+1, 
elseif n > 2
   A = (n)/2+1, 
end
%-------------------------------
n = 10;  
if  n < 0   
   A = -n/2,
elseif n > 10
   A = (n+1)/2, 
elseif n > 6
   A = (n+1)/2+1, 
elseif n > 2
   A = (n)/2+1, 
else
   A = n,
end
%-------------------------------

n = 10; if n < 0 else A = (n+1)/2; end

if  n < 0  % No stmt_sep alow just after if
else 
   A = (n+1)/2; 
end

%===========================================
% Error: cannot handle cases
% other stmt_sep, treated as Empty statement
n = 10; if n < 0 else ; A = (n+1)/2; end
n = 10; if n < 0; A=1; else A = (n+1)/2, end

% need stmt_sep after else!!
n = 10; if n < 0; A=1; else; A = (n+1)/2, end

n = 10; if n < 0; A=1; else
 A = (n+1)/2, end

% Tranditional form
n = 10; 
if n < 0
   A=1 
else 
   A = (n+1)/2
end

n = 10; if n < 0 A=1; else A = (n+1)/2; end

%================================
% Testing about the stmt_sep  after end
x=1; for j=1:5   x = x + j; end         % need all of stmt_sep ';'
x=1; for j=1:5   x = x + j, end, y=10   % need stmt_sep after end

[1, 18]  unexpected token IDENTIFIER
[1, 14]  try inserting LINE_TERMINATOR


% === CANNOT SUPPORT !!! ======
% do not need stmt_sep after
  FOR for_assign.a stmt_separator? stmt_list.l END

% support stmt_sep !!!
x=1; for j=1:5,   x = x + j, end, y=10
x=1; for j=1:5,
   x = x + j, end
x=1; for j=1:5
   x = x + j, end


{: return new ElseifStmt(new List<ElseifStmt>(), l); :}
/*
//-----------------------------------------
// Another way for IF--ELSE

if_statement = 
     IF if_stmt_list END
  |  IF if_stmt_list else_clause END
  ;

if_stmt_list = 
     expr.e stmt_separator stmt_list.l {: return new IfStmt(e, l); :}
  |  if_stmt_list elseif_clause
  ;

elseif_clause = 
     ELSEIF expr.e stmt_separator stmt_list.l {: return new IfStmt(e, l); :}
  ;

else_clause = 
     ELSE stmt_separator? stmt_list.l
  ;
*/

%================================
x = 0; n = 1;
% comment-for
for a=1:3;  % comment-assign
x = x+1; break;  n = n*2, % comment-stmt 
end

% comment-for
for a=1:3  % comment-assign
  x = x+1, 
  n = n*2, % comment-stmt 
  if n>10
    break;
  end 
end

x = 0; n = 1;
% comment-while
while ~feof(fid)  % comment-expr
x = x+1; continue;  n = n*2, % comment-stmt 
end

while ~feof(fid)  
   x = x+fid
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
  x = x+i;
	for j=1:5
	  x = x * j
	end
  n = n+x^2
end

for a=1:3  x = x+1;   n = n*2; end
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
