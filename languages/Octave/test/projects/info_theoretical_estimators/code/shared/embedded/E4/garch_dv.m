function [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = garch_dv(theta, din, i)
% garch_dv - Computes the derivatives of the SS system matrices in a
% model with GARCH structure in the error term with respect to the i-th
% parameter in THETA.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dPhig, dGamg, dEg, dHg, dDg] = ...
%                                            garch_dv(theta, din, i)
%
% 30/4/97

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

if fix(type/100) ~= 1, e4error(14); end

if userflag
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = g_dvown(theta,din,i, userf(2,:));
   return;
end

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(H_D+1:2*H_D));
if i <= np
   [dPhi, dGam, dE, dH, dD, dC, dQ] = ss_dv(theta(1:np,:), din(H_D+1:2*H_D+szpriv(1)),i);
   [H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(2*H_D+szpriv(1)+1:size(din,1)));
   n = max(n,1);
   dPhig = zeros(n);
   dGamg = zeros(n,r);
   dEg = zeros(n,m);
   dHg = zeros(m,n);
   dDg = zeros(m,r);
else
   n = max(n,1);
   dPhi = zeros(n);
   dGam = zeros(n,r);
   dE = zeros(n,m);
   dH = zeros(m,n);
   dD = zeros(m,r);
   dC = zeros(m);
   dQ = zeros(m);
   [dPhig, dGamg, dEg, dHg, dDg] = ss_dv(theta(np+1:size(theta,1),:), din(2*H_D+szpriv(1)+1:size(din,1)),i-np);
end