function t = e4noise(t, d, B)
%
% Assigns estimates to noise parameters from B matrix estimate
%

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

[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(d);
if ~innov(1), return; end

if innov(2) > 0
   pv = H_D+innov(2);
   v = d(pv);
   if v
      B1 = B(1:m,1:m);
      t(np-v+1:np,1) = B1(d(pv+1:pv+v));
   end
else % composite models
   n = size(d,1);
   ptr = H_D + szpriv(2) + 1;
   ptr2 = 1;
   m = 0;
   while ptr < n
         [H_D, type, m2, r2, s2, n2, np2, userflag, userf, innov2, szpriv] = e4gthead(d(ptr:n));
         t(ptr2:ptr2+np2-1,:) = e4noise(t(ptr2:ptr2+np2-1,:), d(ptr:ptr+szpriv(1)+H_D-1),B(m+1:m+m2,m+1:m+m2));
         m = m + m2;
         ptr = ptr + H_D + szpriv(1);
         ptr2 = ptr2 + np2;
   end
end 