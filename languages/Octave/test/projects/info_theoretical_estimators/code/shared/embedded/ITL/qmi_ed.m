% The following function computes the Euclidean distance quadratic mutual information between two vectors X and Y.
%
% Input:   Both X and Y should be COLUMN vectors of SAME length (nxd_x) and (nxd_y).
%				'kSize' is a scalar for the kernel size.
%
% Output: 'val' contains the Euclidean distance quadratic mutual information
%
% Default:  kSize = 1.
%
% Comments: The code uses Incomplete Cholesky Decomposition.
%
% Author: Sohan Seth (sohan@cnel.ufl.edu)	Date: 01.06.2009

function val = qmi_ed(X,Y,kernelSize)

if nargin == 2
    kernelSize = 1;
end

n = length(X);
Gxx = incompleteCholeskySigma(X,kernelSize);
Gyy = incompleteCholeskySigma(Y,kernelSize);
dx = size(Gxx,2); dy = size(Gyy,2);
A = Gxx' * Gyy; % dx x dy
B = ones(1,n) * Gxx; % 1 x dx
C = ones(1,n) * Gyy; % 1 x dy

val = ones(1,dx) * (A.^2) * ones(dy,1)/(n^2) + (C*C') * (B*B') / (n^4) - 2* (B * A * C') / (n^3);