function [FR, FS, AR, AS, V, G] = thd2arm2(theta, din, fromgrad)
% thd2arm2 - Converts a THD model to VARMAX notation.
%    [FR, FS, AR, AS, V, G] = thd2arm2(theta, din)
% For the model:
%   FR(B)FS(B)y(t) = G(B)u(t) + AR(B)AS(B)e(t)
% this function returns the matrices:
%   FR = [FR1 FR2 ... FRp], AR = [AR1 AR2 ... ARq],
%   FS = [FS1 FS2 ... FSP], AS = [AS1 AS2 ... ASQ],
%   G = [G0 G1 G2 ... Gg], V = V(e(t))
% The procedure also checks that V is positive definite.
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

global E4OPTION

if nargin < 3, fromgrad = 0; end

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if type > 2, e4error(14); end
vdiag = din(H_D+1);
p = din(H_D+2); P = din(H_D+3); q = din(H_D+4); Q = din(H_D+5); g = din(H_D+6);
if type == 2
   p = p+1; P = P+1; q = q+1; Q = Q+1;
end

ptr1 = 1;
ptr2 = H_D+7;

% FR

if ~din(ptr2,1) % void
   if type == 2, FR = []; else, FR = zeros(m,p*m); end
else
   FR = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, p*m);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% FS

if ~din(ptr2,1) % void
   if type == 2, FS = []; else, FS = zeros(m,P*m); end
else
   FS = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, P*m);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% AR

if ~din(ptr2,1) % void
   if type == 2, AR = []; else, AR = zeros(m,q*m); end
else
   AR = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, q*m);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% AS

if ~din(ptr2,1) % void
   if type == 2, AS = []; else, AS = zeros(m,Q*m); end
else
   AS = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, Q*m);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% G

if ~(r & din(ptr2,1)) % void
   G = zeros(m,(g+1)*r);
else
   G = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, (g+1)*r);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% V

if E4OPTION(5) == 1,sym = 0; else, sym = 1; end

if ~din(ptr2,1) % void
   V = [];
else
   V = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), m, m, sym);
end

% Force V positive definite
if ~fromgrad
   if ~E4OPTION(5)
      if min(eig(V)) <= 0
         Vu = cholp(V);
         V  = Vu'*Vu;
      end
   else
      V = V*V';  
   end
end
