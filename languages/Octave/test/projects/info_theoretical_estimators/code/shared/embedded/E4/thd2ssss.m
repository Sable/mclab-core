function [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssss(theta, din)
% thd2ssss  - Returns the SS matrices from a native SS model in THD format.
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssss(theta, din)
%
%  7/3/97

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

global E4OPTION

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if type ~= 7, e4error(14); end
vdiag = din(H_D+1);
q = din(H_D+2);
v = din(H_D+3);

ptr1 = 1;
ptr2 = H_D+4;

% PHI

if ~din(ptr2,1) % void
   Phi = zeros(n,n);
else
   Phi = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), n, n);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% GAM
	
Gam = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), n, r);
if r & ~size(Gam,1), Gam = zeros(n,r); end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% E

if ~din(ptr2) % void
   if ~q
      E = zeros(n,1);
   else
      E = eye(n);
   end
else
   E = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), n, q);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% H
H = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, n);
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% D
D = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, r);
if r & ~size(D,1), D = zeros(m,r); end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

if ~din(ptr2)  % void
   if ~v
      C = zeros(m,1);
   else
      C = eye(m);
   end
else
   C = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, v);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

if E4OPTION(5) == 1
   sym = 0;
else
   sym = 1;
end

if ~q
   Q = 0;          
else
   Q = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), q, q, sym);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

if isinnov(1) % => R = S = Q
   R = Q;

   if E4OPTION(5) == 1, S = Q*Q'; else, S = Q; end
else
%
   if ~din(ptr2) % vacía
      S = zeros(max(q,1),max(v,1));
   else
      S = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), q, v);
   end
   ptr1 = ptr1+din(ptr2);
   ptr2 = ptr2+din(ptr2)+1;

   if ~v
      R = 0;          
   else
      R = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), v, v, sym);
   end
%
end

if E4OPTION(5) == 1
   Q = Q*Q';
   R = R*R';
end
%