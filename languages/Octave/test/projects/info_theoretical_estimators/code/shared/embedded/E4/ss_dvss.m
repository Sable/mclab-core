function [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvss(theta,din,i)
% SS_DVSS  - Computes the partial derivatives of the matrices of the SS
% representation with respect to the i-th parameter in THETA.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvss(theta, din, i)
% This function only works with native SS models.
%
% 5/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if nargin < 3, e4error(3); end

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if type ~= 7, e4error(14); end
vdiag = din(H_D+1);
q = din(H_D+2);
v = din(H_D+3);

ptr2 = H_D+4;

if i < 1 | i > np, e4error(2); end

q1 = max(q,1);
v1 = max(v,1);

dim = [n n n m m m q1 q1 v1; n r q1 n r v1 q1 v1 v1]';

dPhi = zeros(dim(1,:));
dGam = zeros(dim(2,:));
dE   = zeros(dim(3,:));
dH   = zeros(dim(4,:));
dD   = zeros(dim(5,:));
dC   = zeros(dim(6,:));
dQ   = zeros(dim(7,:));
dS   = zeros(dim(8,:));
dR   = zeros(dim(9,:));

dimptr = zeros(9,1);
ptrptr = zeros(9,1);
if isinnov(1), nmatrix = 7; else, nmatrix = 9; end
for j=1:nmatrix
    dimptr(j) = din(ptr2);
    ptrptr(j) = ptr2;
    ptr2 = ptr2+din(ptr2)+1;
end
dimptr = cumsum(dimptr);

vs = find(dimptr >= i);
ptr = vs(1);

if ptr > 1
   pos = din(ptrptr(ptr)+i-dimptr(ptr-1));
else
   pos = din(ptrptr(ptr)+i);
end

nc = fix((pos-1)/dim(ptr,1)) + 1;
nr = pos-dim(ptr,1)*(nc-1);

if ptr == 1
   dPhi(nr,nc) = 1;
elseif ptr == 2
   dGam(nr,nc) = 1;
elseif ptr == 3
   dE(nr,nc) = 1;
elseif ptr == 4
   dH(nr,nc) = 1;
elseif ptr == 5
   dD(nr,nc) = 1;
elseif ptr == 6
   dC(nr,nc) = 1;
elseif ptr == 8
   dS(nr,nc) = 1;
elseif ptr == 7
   dQ(nr,nc) = 1;
   
   if E4OPTION(5) == 1 
     Q = vecss(theta(dimptr(6)+1:dimptr(7),1), din(ptrptr(7)+1:ptrptr(7)+din(ptrptr(7))), q, q, 0);
     dQ = Q * dQ' + dQ * Q';
   else
      dQ(nc,nr) = 1;
   end

   if v & dimptr(9) == dimptr(8,1)
      dS = dQ; dR = dQ;
   end
%
else
   dR(nr,nc) = 1;

   if E4OPTION(5) == 1
      R = vecss(theta(dimptr(8)+1:dimptr(9),1), din(ptrptr(9)+1:ptrptr(9)+din(ptrptr(9))), v, v, 0);
      dR = R * dR' + dR * R';
   else
      dR(nc,nr) = 1;
   end
end