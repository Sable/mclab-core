function M = vecss(t, d, nr, nc, sym)
% vecss    - Constructs a nrxnc matrix from a sparse vector where din is
% a vector with the indices of the theta parameters of M to be estimated.
% sym shows whether it is a symmetric matrix or not.
%    M = vecss(theta, din, nr, nc, sym)
%
% 7/3/97

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

if nargin < 5, sym = 0; end

t = t(:);
M = zeros(nr, nc);

if size(t,1)
   M(d) = t;
   if sym, M = M + tril(M,-1)'; end
end
