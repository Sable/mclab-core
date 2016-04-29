function [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvc(theta, din, i)
% ss_dvn   - Computes the partial derivatives of the matrices of a components SS model
% with respect to the i-th parameter in theta.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvn(theta, din, i)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% i     > index of the parameter in the denominator of the partial derivative.
%
% 30/4/98

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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if fix(type/10) ~= 3, e4error(14); end

if userflag
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvown(theta,din,i, userf(2,:));
   return;
end

n = max(n,1);

dPhi = zeros(n);
if r, dGam = zeros(n,r); dD = zeros(m,r); else, dGam = []; dD = []; end
dH = zeros(m, n);

ptr = H_D+szpriv(2)+1;
ptr2 = 1;
sd = size(din,1);
if isinnov(1)
   q = m; v = m;
else
   q = isinnov(2); v = isinnov(3);
end

q = max(q,1);
v = max(v,1);

dE = zeros(n,q); dC = zeros(m,v); dQ = zeros(q); dR = zeros(v); dS = zeros(q,v);  
nc1 = 1;
rc1 = 1;
qc1 = 1;
vc1 = 1;

while ptr < sd
      [H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:sd));
      if isinnov(1)
         v = m; if n, q = m; else, q = 0; end
      else
         v = isinnov(3); if n, q = isinnov(2); else q = 0; end
      end 

      nc2 = nc1+n-1;
      rc2 = rc1+r-1;
      qc2 = qc1+q-1;
      vc2 = vc1+v-1;

      if i >= ptr2 & i < ptr2+np
         [dPhi1, dGam1, dE1, dH1, dD1, dC1, dQ1, dS1, dR1] = ss_dv(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), i-ptr2+1);

         if n
            dPhi(nc1:nc2,nc1:nc2) = dPhi1;
            if r, dGam(nc1:nc2,rc1:rc2) = dGam1; end
            dH(:,nc1:nc2) = dH1;
            if q
               dE(nc1:nc2,qc1:qc2) = dE1;
               dQ(qc1:qc2,qc1:qc2) = dQ1;
               if v, dS(qc1:qc2,vc1:vc2) = dS1; end
            end
         end
         
         if v
            dR(vc1:vc2,vc1:vc2) = dR1;
            dC(:,vc1:vc2) = dC1;
         end
         if r, dD(:,rc1:rc2) = dD1; end
      end
      nc1 = nc2+1;
      rc1 = rc2+1;
      qc1 = qc2+1;
      vc1 = vc2+1;
      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np;
end
