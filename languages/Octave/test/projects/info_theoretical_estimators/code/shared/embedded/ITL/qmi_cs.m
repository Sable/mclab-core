% The following function computes the Cauchy-Schwartz quadratic mutual information between two vectors X and Y.
%
% Input:   Both X and Y should be COLUMN vectors of SAME length (nxd_x) and (nxd_y).
%				'kSize' is a scalar for the kernel size.
%
% Output: 'val' contains the Cauchy-Schwartz quadratic mutual information
%
% Default:  kSize = 1.
%
% Comments: The code uses Incomplete Cholesky Decomposition.
%
% Author: Sohan Seth (sohan@cnel.ufl.edu)	Date: 01.06.2009

function val = qmi_cs(X,Y,kernelSize)

if nargin == 2
    kernelSize = 1;
end

n = length(X);
Gxx = incompleteCholeskyMulti(X,kernelSize);
Gyy = incompleteCholeskyMulti(Y,kernelSize);
dx = size(Gxx,2); dy = size(Gyy,2);
A = Gxx' * Gyy; % dx x dy
B = ones(1,n) * Gxx; % 1 x dx
C = ones(1,n) * Gyy; % 1 x dy

val = log(ones(1,dx) * (A.^2) * ones(dy,1) * (B * B') * (C * C') / (B * A * C')^2);