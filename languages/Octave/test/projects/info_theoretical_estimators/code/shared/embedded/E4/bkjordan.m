function [P, Q, U1, U2, iR, iS]  = bkjordan(A, cut)
% bkjordan - Computes the decomposition A = [U1 U2]*diag(P,Q)*inv([U1 U2])
%    [P, Q, U1, U2, iR, iS]  = bkjordan(A, cut)
% P stores the eigenvalues of A greater or equal that cut, and Q the
% eigenvalues that do not meet this condition.
% inv([U1 U2]) = [iR; iS] iR*U1=I iS*U2=I iR*U2=0 iS*U1=0.
%
% 5/3/97

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

[T, U, iU, eigen]  = bkdiag(A);

k1 = abs(eigen) >= cut;
k2 = ~k1;
sk1 = sum(k1);
sk2 = sum(k2);

if sk1 & sk2
   P = T(k1,k1);
   U1 = U(:,k1);
   iR = iU(k1,:);
   Q = T(k2,k2);
   U2 = U(:,k2);
   iS = iU(k2,:);
elseif sk2
   P = [];
   U1= [];
   iR= [];
   Q = A;
   U2 = eye(size(A));
   iS = U2;
else
   Q = [];
   U2= [];
   iS= [];
   P = A;
   U1 = eye(size(A));
   iR = U1;
end
