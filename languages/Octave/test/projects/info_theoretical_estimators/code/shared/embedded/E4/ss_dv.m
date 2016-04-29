function [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dv(theta, din, i)
% ss_dv    - Computes the partial derivatives of the matrices of a SS model
% with respect to the i-th parameter in theta.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dv(theta, din, i)
% This function only works with non-composite SS models without exogenous
% variables dGam = [], dD = [].
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% i     > index of the parameter in the denominator of the partial derivative.
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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if userflag
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvown(theta,din,i, userf(2,:));
   return;
end

if type < 4 % simple model
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvs(theta,din,i);
   return;
end

if type == 4 % echelon
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvh(theta,din,i);
   return;
end

if type == 7
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvss(theta,din,i);
   return;
end

if fix(type/10) == 2 % nested
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvn(theta,din,i);
   return;
end

if fix(type/10) == 3 % components
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvc(theta,din,i);
   return;
end

e4error(14);