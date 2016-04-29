function [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvh(theta,din,di)
% ss_dvh   - Computes the partial derivatives of the matrices of the SS
% representation of a VARMAX echelon model, with respect to the i-th parameter in THETA.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvh(theta, din, i)
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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if type ~= 4, e4error(14); end

kronecker = din(H_D+1:H_D+m);
din2 = din(H_D+m+1:size(din,1));
[F, dF, A, dA, V, dV, G, dG] = str_dv(theta, din2, di);

km = (size(F,2)-m);
k = km/m;

F0 = F(1:m,1:m)+eye(m); Ab = zeros(km,m); Fb = zeros(km,m);
dF0 = dF(1:m,1:m); dAb = zeros(km,m); dFb = zeros(km,m);
iF0 = inv(F0);
diF0= -iF0*dF0*iF0;

for i=1:k
    Fb((i-1)*m+1:i*m,:) = F(1:m,m*i+1:m*i+m);
    dFb((i-1)*m+1:i*m,:) = dF(1:m,m*i+1:m*i+m);
    dAb((i-1)*m+1:i*m,:) = dA(1:m,m*i+1:m*i+m);
end
if r
  G0  = G(1:m,1:r); dG0 = dG(1:m,1:r); dGb = zeros(km,r);
  for i=1:k, dGb((i-1)*m+1:i*m,:) = dG(1:m,r*i+1:r*i+r); end
end

dH = [diF0 zeros(m,km-m)];
dC   = zeros(m);

if n==0
   dPhi = zeros(size(H));  dE = dPhi;
   if r, dGam = zeros(m,r); dD = diF0*G0 + iF0*dG0; 
   else   dGam = []; dD = []; end
else
  dPhi = [ -(dFb*iF0+Fb*diF0) zeros(km,m*(k-1))]; 
  dE   = [ dAb - dFb];
  if r,  D = iF0 * G0; dD = diF0*G0 + iF0*dG0; dGam = [ dGb - dFb*D - Fb*dD];
  else   dGam = []; dD = []; end
end

idx = ~zeros(km,1);

for i=1:m
    idx(m*kronecker(i)+i:m:km) = zeros(k-kronecker(i),1);
end

dH = dH(:,idx);
dE = dE(idx,:);
if r, dGam = dGam(idx,:); end
dPhi = dPhi(idx,idx);

dQ = dV; dS = dQ; dR = dQ;