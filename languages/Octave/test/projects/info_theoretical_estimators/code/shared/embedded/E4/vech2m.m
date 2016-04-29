function X = vech2m(x,n,istriang)
% vech2m   - Builds matrix X from vector x, so that x=vech(X)
%    A = vech2m(v, n, istriang)
% v    > ([n(n+1)/2]x1) vector.
% n    > dimension of A.
% istriang > if istriang=0 (default) returns a symmetric matrix, 
%            if istriang=1 returns a (lower) triangular matrix.
% A    < (nxn) matrix.
%
% 28/1/97

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

if nargin < 3, istriang = 0; end

X = zeros(n);
X(~(~tril(ones(n)))) = x;

if ~istriang, X = X + tril(X,-1)'; end
