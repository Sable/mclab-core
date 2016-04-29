function [L, maxadd]  = choldc(H, maxoffl)
% choldc   - Computes the perturbed Cholesky decomposition LL'=H+D.
%    [L, maxadd]  = choldc(H, maxoffl)
% D is a diagonal non-negative matrix which is computed when it is
% necessary to grant that a) the elements in the diagonal of L are
% greater than a tolerance and b) that the elemnts in the lower triangle
% are less that maxoffl. L is the perturbed Cholesky factor and maxadd
% is the bigger element of D.
%
% 11/3/97

% Copyright (C) 1997 Jaime Terceiro
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

% Based on CHOLDECOMP. Dennis & Schnabel (1983), pp. 315

n    = size(H,1);
minl = sqrt(sqrt(eps))*maxoffl;
minl2 = sqrt(eps)*maxoffl;
if maxoffl == 0.0
   maxoffl = sqrt(max(abs(diag(H))));
   minl2   = sqrt(eps)*maxoffl;
end
maxadd = 0.0;
L = zeros(n,n);

if n > 1
   L(1,1) = H(1,1);
   minljj = 0.0;
   L(2:n,1) = H(1,2:n)';
   minljj = max(max(abs(L(2:n,1))), minljj);
   minljj = max(minljj/maxoffl, minl);
   if L(1,1) > minljj^2
      L(1,1) = sqrt(L(1,1));
   else
      if minljj < minl2
         minljj = minl2;
      end
      maxadd = max(maxadd, (minljj^2) - L(1,1));
      L(1,1) = minljj;
   end
   L(2:n,1) = L(2:n,1)/L(1,1);
end

for j=2:n-1
   L(j,j) = H(j,j) - sum(L(j,1:j-1).^2);
   minljj = 0.0;
   L(j+1:n,j) = H(j,j+1:n)' - L(j+1:n,1:j-1)*(L(j,1:j-1)');
   minljj = max(max(abs(L(j+1:n,j))), minljj);
   minljj = max(minljj/maxoffl, minl);
   if L(j,j) > minljj^2
      L(j,j) = sqrt(L(j,j));
   else
      if minljj < minl2
          minljj = minl2;
      end
      maxadd = max(maxadd, (minljj^2) - L(j,j));
      L(j,j) = minljj;
   end
   L(j+1:n,j) = L(j+1:n,j)/L(j,j);
end

L(n,n) = H(n,n) - sum(L(n,1:n-1).^2);
if L(n,n) > minl^2
   L(n,n) = sqrt(L(n,n));
else
   if minl < minl2
      minl = minl2;
   end
   maxadd = max(maxadd, (minl^2) - L(n,n));
   L(n,n) = minl;
end