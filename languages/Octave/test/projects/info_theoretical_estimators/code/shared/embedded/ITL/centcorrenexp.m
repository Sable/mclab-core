% The following function computes the parametric centered correntropy
%               between two vectors X and Y using double exponential kernel
%
% Input:   X and Y are real valued COLUMN vectors of SAME length
%				kSize is a scalar for the kernel size, i.e. K(x,y) = exp(-|x-y|/kSize),
%				param is a [2x1] vector containing the coefficients a and b in ORDER.
%
% Output: corren contains the parametric centered correntropy
%
% Default:  param = [a,b] = [1,0] and kSize = 1.
%
% Comments: The code uses cipexp
%
% Author: Sohan Seth (sohan@cnel.ufl.edu)	Date: 15.07.2009

function corren = centcorrenexp(X,Y,kSize,param)

if nargin == 2
	kSize = 1;
	param = [1,0];
end

if nargin == 3
	param = [1,0];
end

n = size(X,1);
a = param(1); b = param(2);

X = a*X+b;

corren = (1/n)*sum(exp(-abs(X - Y)/kSize)) - cipexp(X,Y,kSize);