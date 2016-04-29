% The following function computes the parametric correntropy coefficient between two vectors X and Y.
%
% Input:   Both X and Y should be COLUMN vectors of SAME length (nx1).
%				'kSize' is a scalar for the kernel size.
%				'param' is a 2x1 vector containing the parameter coeffues 'a' and 'b' in ORDER.
%
% Output: 'coeff' contains the parametric correntropy coefficient
%
% Default:  param = [a,b] = [1,0] and kSize = 1.
%
% Caution: a ~= 0.
%
% Comments: The code uses Incomplete Cholesky Decomposition.
%
% Author: Sohan Seth (sohan@cnel.ufl.edu)	Date: 11.03.2008

function coeff = correncoef(X,Y,kSize,param)

if nargin == 2
	kSize = 1;
	param = [1,0];
end

if nargin == 3
	param = [1,0];
end

n = size(X,1);
a = param(1); b = param(2);
twokSizeSquare = 2*kSize^2;

if a == 0;
	error('a must NOT be 0');
end

X = a*X+b;

G =  incompleteCholeskyMulti([X;Y],kSize);
OZ = [ones(n,1);zeros(n,1)]/n;
ZO = [zeros(n,1);ones(n,1)]/n;
corren = (1/n)*sum(exp(-(X - Y).^2/twokSizeSquare));
coeff = (corren - (ZO'*G)*(G'*OZ)) /sqrt((1 - (OZ'*G)*(G'*OZ))*(1 - (ZO'*G)*(G'*ZO)));

% ~ Done