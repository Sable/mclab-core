function [FR, FS, AR, AS, V, W, D] = thd2tf2(theta, din, fromgrad)
% thd2tf2 - Converts a THD model to TF notation.
%    [FR, FS, AR, AS, V, G] = thd2tf2(theta, din)
% For the model:
%   y(t) = [w1(B)/d1(B)]u1(t) + ... + [wr(B)/dr(B)]ur(t) + N(t)
%   FR(B)FS(B) N(t) = AR(B)AS(B) e(t)
% returns:
%   FR = [fr1 ... frp]         AR = [ar1 ... arq]
%   FS = [fs1 ... fsP]         AR = [as1 ... arQ]
%   W = [w1(B); ...; wr(B)]   D = [d1(B); ...; dr(B)]
%   V = V[e(t)]
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

p = din(H_D+1); P = din(H_D+2); q = din(H_D+3); Q = din(H_D+4); gw = din(H_D+5); gd = din(H_D+6);

ptr1 = 1;
ptr2 = H_D+7;

% FR

if ~din(ptr2,1) % void
   FR = zeros(1,p);
else
   FR = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), 1, p);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% FS

if ~din(ptr2,1) % void
   FS = zeros(1,P);
else
   FS = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), 1, P);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% AR

if ~din(ptr2,1) % void
   AR = zeros(1,q);
else
   AR = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), 1, q);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% AS

if ~din(ptr2,1) % void
   AS = zeros(1,Q);
else
   AS = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), 1, Q);
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% W

if ~din(ptr2,1) % void
   W = zeros(r,gw+1);
else
   W = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), gw+1, r)';
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% D

if ~din(ptr2,1) % void
   D = zeros(r,gd);
else
   D = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), gd, r)';
end
ptr1 = ptr1+din(ptr2);
ptr2 = ptr2+din(ptr2)+1;

% V

if E4OPTION(5) == 1,sym = 0; else, sym = 1; end

if ~din(ptr2,1) % void
   V = [];
else
   V = vecss(theta(ptr1:ptr1+din(ptr2)-1,1), din(ptr2+1:ptr2+din(ptr2)), 1, 1, 1);
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
