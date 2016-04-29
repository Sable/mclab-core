function [t,d,l] = comp2thd(t,d,l)
% comp2thd  - Converts a stacked model (see stackthd.m) in a components model
% in THD format.
%    [theta, din, label] = comp2thd(t,d,l)
% t,d,l   > stacked models (simple models) in THD representation.
% theta, din, label < THD representation of the components model.
%
% 18/9/97

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

if nargin < 2, e4error(3); end
if nargin < 3, sl = 0; else, sl = size(l,1); end

sd = size(d,1);
ptr = 1;
q = 0; v = 0;

while ptr < sd
      if ptr == 1
         [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(d(ptr:sd));
         innov2 = innov;
         if ptr + H_D + szpriv(1) <= sd, innov = [0 0 0]; end
         m2 = m; n2 = n;
      else
         [H_D, type, m2, r2, s2, n2, np2, userflag, userf, innov2, szpriv] = e4gthead(d(ptr:sd));
         if m2 ~= m, e4error(5); end
         r = r + r2;
         np = np + np2;
         n = n + n2;
         s = max(s,s2);
      end
      if innov2(1)
         v = v + m2; if n2, q = q + m2; end
      else
         v = v + innov2(3); if n2, q = q + innov2(2); end
      end 

      ptr = ptr + H_D + szpriv(1);
end
if ~innov(1), innov = [0 q v]; end
d0 = e4sthead(30, m, r, s, n, size(t,1), 0, [], innov, size(d,1));
d = [d0;d];