function [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvs(theta,din,i)
% ss_dvs   - Computes the partial derivatives of the matrices of the SS
% representation with respect to the i-th parameter in THETA.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvs(theta, din, i)
% This function only works with simple models.
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

[H_D, mtipo, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if np ~= size(theta,1), e4error(1); end

if mtipo < 0 | mtipo > 3, e4error(14); end

[F, dF, A, dA, V, dV, G, dG] = fr_dv(theta, din, i);

dQ = dV; dS = dQ; dR = dQ;
if (mtipo==0) | (n==0)
  dPhi = 0;  dE = zeros(1,m); dH = zeros(m,1); dC = dA;
  if r, dGam = zeros(1,r); dD = dG; else dGam = []; dD = []; end
  return;
end

F0 = F(1:m,1:m); dF0 = dF(1:m,1:m);
A0 = A(1:m,1:m); dA0 = dA(1:m,1:m);
k = n / m;
Fb = []; dFb = []; dAb = [];
for i=1:k
   Fb  = [ Fb; F(1:m,m*i+1:m*i+m)];
   dFb = [dFb; dF(1:m,m*i+1:m*i+m)];
   dAb = [dAb; dA(1:m,m*i+1:m*i+m)];
end
if r
  G0  = G(1:m,1:r);
  dG0 = dG(1:m,1:r);
  dGb = [];
  for i=1:k, dGb = [dGb; dG(1:m,r*i+1:r*i+r)]; end
end

dPhi = [ -dFb zeros(m*k,m*(k-1)) ];
dE   = [  dAb - dFb*A0 - Fb*dA0];
dH   = [zeros(m,m) zeros(m,m*(k-1))];
dC   = dA0;

if r
   if k, dGam = [ dGb - dFb*G0 - Fb*dG0]; end
   dD = dG0;
else    dGam = []; dD = []; end

