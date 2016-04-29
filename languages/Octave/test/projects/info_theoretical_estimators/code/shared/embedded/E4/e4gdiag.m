function [Q, Z, index] = e4gdiag(A, B, k, qtriang)
% gdiag - Internal function for the calculation of ARE. Gets the
% transformation matrices Q and Z in order to isolate principal vectors for
% the first k generalized eigenvalues of A and B.
%     [Q, Z, index] = e4gdiag(A, B, k, qtriang)
%
% A and B, complex (or real quasi) triangular schur forms, result of qz algorithm
% k number of first gen. eigenvalues (sorted in ascending order) to diagonalize
% qtriang, optional argument (default zero). If true, A and B are qasi
% triangular real schur forms and then some additional calculations have to
% be done in order to get de gen. eigenvalues.
% Q and Z are transformation matrices so that:
%    A2 = Q*A*Z and B2 = Q*B*Z
% where all the pairs i,j on the upper triangular part are zero if i and j
% corresponds to gen. eginvalues not belonging/belonging to the set of first k
% eigenvalues, respectively. In this way a sufficent "diagonalitation" is
% reached in order to isolate the first k principal vectors, instead of
% reordering all the matrices A and B as ordqz() does.
%
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2, or (at your option)
% any later version.
% 
% This program is distributed in the hope that it will be useful, but
% WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% General Public License for more details. 
% 
% You should have received a copy of the GNU General Public License
% along with this file.  If not, write to the Free Software Foundation,
% 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

if nargin < 4, qtriang = 0; end
tol = 100*eps;
n = size(A,1);
d = zeros(n,1);
if qtriang
   I2 = ones(2,1);
   alpha2 = diag(A,-1);
   alpha = diag(A).^2;
   beta = diag(B).^2;   
   ki = find(alpha2 ~= 0);
   for i = 1:size(ki,1)
       if abs(beta(ki(i))) > tol
          C = B(ki(i):ki(i)+1,ki(i):ki(i)+1) \ A(ki(i):ki(i)+1,ki(i):ki(i)+1);
          alpha(ki(i):ki(i)+1) = det(C)*I2;
          beta(ki(i):ki(i)+1) = I2;
       end
   end
else   
   beta = diag(B);
   alpha = diag(A);
end
kinf = abs(beta) > tol;
d(kinf) = alpha(kinf)./beta(kinf);
d(~kinf) = inf*ones(sum(~kinf),1);

[dor, index] = sort(abs(d));
idx = sort(index(k+1:n));
didx = diff(idx);
idx1 = find(didx > 1);
idx2 = [idx1; size(idx,1)];
idx1 = [1; idx1+1];
nidx = size(idx2,1);
idx3 = zeros(n,1);
idx3(index(1:k)) = ~zeros(k,1);
n3 = size(idx3,1);

Q = eye(n);
Z = Q;

if idx(size(idx,1)) >= n, nidx = nidx-1; end
    
for i = nidx:-1:1
    i0 = idx(idx1(i));
    i1 = idx(idx2(i));
    ki = find(idx3(i1+1:n3))+i1;
    
    [X,Y]  = gsylvest(A(i0:i1,i0:i1),B(i0:i1,i0:i1), A(ki,ki), B(ki,ki), -A(i0:i1,ki), -B(i0:i1,ki));

    Q(:,ki) = Q(:,ki) + Q(:,i0:i1)*Y;
    Z(i0:i1,:) = Z(i0:i1,:) + X*Z(ki,:);

    A(1:i0-1,ki) = A(1:i0-1,i0:i1)*X+A(1:i0-1,ki);
    B(1:i0-1,ki) = B(1:i0-1,i0:i1)*X+B(1:i0-1,ki);

%   Given that the transformed A and B matrices are not returned, we don't
%   need to apply the transformation
%    A(i0:i1,ki) = zeros(i1-i0+1,size(ki,1)); 
%    B(i0:i1,ki) = zeros(i1-i0+1,size(ki,1));
end       

index = sort(index(1:k));