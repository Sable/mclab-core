function [t,d,l] = nest2thd(t,d,nestwhat,l)
% nest2thd  - Converts a stacked model (see stackthd.m) in a nested model
% in THD format.
%    [theta, din, label] = nest2thd(t,d,nestwhat, l)
% t,d,l   > stacked models (simple models) in THD representation.
% nestwhat > 0 : nest in errors,  1 : nest in inputs
% theta, din, label < THD representation of the nested model.
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
if nargin < 3, nestwhat = 0; end
if nargin < 4, sl = 0; else, sl = size(l,1); end

sd = size(d,1);
ptr = 1;
ptr2 = 1;
t2 = []; d2 = []; l2 = [];
v = 0; q = 0;

while ptr < sd
      if ptr == 1
         [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(d(ptr:sd));
         if type > 99, e4error(5); end
         if ~nestwhat & ~innov(1), e4error(5); end
         if nestwhat, innov(2) = 0; end
         innov2 = innov;
         np2 = np;
         m2 = m;
      else
         [H_D, type, m2, r2, s2, n2, np2, userflag, userf, innov2, szpriv] = e4gthead(d(ptr:sd));
         if type > 99, e4error(5); end
         if ~nestwhat
            if ~innov2(1) | m2 ~= m | r2 ~= r, e4error(5); end
            innov = innov2;
         else
            if m2 ~= r, e4error(5); end
            m = m + m2;
            r = r2;
            innov(1) = innov(1) & innov2(1);
            if innov2(1)
               v = v + m; q = q + m;
            else
               v = v + innov2(3); v = v + innov2(2);
            end 
            np = np + np2;
         end
         n = n + n2;
         s = max(s,s2);
      end
      if ~nestwhat
         if  ptr + H_D + szpriv(1) <= sd
             if sl
                [t0, d0,l0] = e4trunc(t(ptr2:ptr2+np2-1,:), d(ptr:ptr+H_D+szpriv(1)-1), l(ptr2:ptr2+np2-1,:)); 
             else
                [t0, d0] = e4trunc(t(ptr2:ptr2+np2-1,:), d(ptr:ptr+H_D+szpriv(1)-1)); 
             end
         else
             innov(2) = innov2(2) + size(d2,1) + H_D;
             t0 = t(ptr2:ptr2+np2-1,:);
             d0 = d(ptr:ptr+H_D+szpriv(1)-1);
             if sl
                l0 = l(ptr2:ptr2+np2-1,:); 
             end
         end
         t2 = [t2;t0];
         d2 = [d2;d0];
         if sl
            l2 = [l2;l0];
         end
      else
         if innov2(1)
            v = v + m2; if n, q = q + m2; end
         else
            v = v + innov2(3); if n, q = q + innov2(2); end
         end 
      end

      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np2;
end

if ~nestwhat, t = t2; d = d2; l = l2; elseif ~innov(1), innov(2:3) = [q v]; end
d0 = e4sthead(20+nestwhat, m, r, s, n, size(t,1), 0, [], innov, size(d,1));
d = [d0;d];