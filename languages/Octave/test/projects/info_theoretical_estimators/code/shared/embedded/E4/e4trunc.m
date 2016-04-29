function [t0,d0,l0] = e4trunc(t, d, l)

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

if nargin < 3, sl = 0; else, sl = size(l,1); end

kt = ones(size(t,1),1);
kd = ones(size(d,1),1);

if innov(2) > 0
   pv = H_D+innov(2);
   v = d(pv);
   d(pv) = 0;
   t0 = t(1:np-v,:);
   if sl, l0 = l(1:np-v,:); end
   d0 = d([1:pv pv+v+1:size(d,1)]);
   d0(1:H_D) = e4sthead(type, m, r, s, n, np-v, userflag, userf, innov, szpriv-v);
else % composite models
   n = size(d,1);
   ptr = H_D + szpriv(2) + 1;
   ptr2 = 1;
   t0 = [];
   d0 = d(1:H_D+szpriv(2));
   l0 = [];
   while ptr < n
         [H_D, type, m2, r2, s2, n2, np2, userflag, userf, innov2, szpriv] = e4gthead(d(ptr:n));
         if sl
            [t2,d2,l2] = e4trunc(t(ptr2:ptr2+np2-1,:), d(ptr:ptr+szpriv(1)+H_D-1), l(ptr2:ptr2+np2-1,:));
            l0 = [l0;l2];
         else
            [t2,d2] = e4trunc(t(ptr2:ptr2+np2-1,:), d(ptr:ptr+szpriv(1)+H_D-1));
         end
         t0 = [t0;t2];
         d0 = [d0;d2];
         ptr = ptr + H_D + szpriv(1);
         ptr2 = ptr2 + np2;
   end   
end 