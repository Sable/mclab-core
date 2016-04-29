function [dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = sc_dv(theta, din, i)
% sc_dv    - Computes the partial derivatives of the system matrices in a
% time-varying parameters model with respect to the i-th parameter in theta.
%    [dPhi, dGam, dE, dHb, dDb, dCb, dQ, dS, dR, dHf, dDf, dCf] = ...
%                                                 sc_dv(theta, din, i)
%
% 3/4/97

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

if type ~= 200, e4error(14); end

if userflag
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = sc_dvown(theta,din,i, userf(2,:));
   return;
end

n = max(n,1);
q = max(isinnov(2),1);
v = isinnov(3);
dPhi = zeros(n);
dGam = zeros(n,r);
dE = zeros(n,q);
dQ = zeros(q);
dR = zeros(v);
dS = zeros(q,v);

[H_D, type, m, rv, s, nv, np, userflag, userf, isinnov, szpriv] = e4gthead(din(H_D+1:2*H_D));

if i <= np
   [dPhiv, dGamv, dEv, dHv, dDv, dCv, dQv, dSv, dRv] = ss_dv(theta(1:np,:), din(H_D+1:2*H_D+szpriv(1)),i);
   if nv
      dPhi(1:nv,1:nv) = dPhiv; dGam(1:nv,1:rv) = dGamv;
      dE(1:nv,1:size(dEv,2)) = dEv; dQ(1:size(dQv,1),1:size(dQv,1)) = dQv;
       dS(1:size(dSv,1),1:size(dSv,2)) = dSv;
   end
   dR(1:size(dRv,1),1:size(dRv,1)) = dRv;
else
   dHv = zeros(m,max(nv,1));
   dDv = zeros(m,rv);
   if isinnov(1)
      dCv = zeros(m);
   else
      dCv = zeros(m,isinnov(3));
   end
end

if size(din,1) == 2*H_D+szpriv(1)
   dHf = []; dDf = []; dCf = [];
else
   [H_D, type, mf, rf, s, nf, npf, userflag, userf, isinnov, szprivf] = e4gthead(din(2*H_D+szpriv(1)+1:3*H_D+szpriv(1)));
   if i > np
      [dPhif, dGamf, dEf, dHf, dDf, dCf, dQf, dSf, dRf] = ss_dv(theta(np+1:np+npf,:), din(2*H_D+szpriv(1)+1:size(din,1)),i-np);
      if nf
         dPhi(nv+1:n,nv+1:n) = dPhif; dGam(nv+1:n,rv+1:r) = dGamf;
         dE(nv+1:n,1:q-size(dEf,2)+1:q) = dEf; dQ(q-size(dQf,1)+1:q,q-size(dQf,1)+1:q) = dQf;
         dS(q-size(dSf,1)+1:q,v-size(dSf,2)+1:v) = dSf;
      else
         dHf = [];
      end
      dR(v-size(dRf,1)+1:v,v-size(dRf,1)+1:v) = dRf;
   else
      dHf = zeros(mf,nf);
      dDf = zeros(mf,rf);
      if isinnov(1)
         dCf = zeros(mf);
      else
         dCf = zeros(mf,isinnov(3));
      end
   end
end