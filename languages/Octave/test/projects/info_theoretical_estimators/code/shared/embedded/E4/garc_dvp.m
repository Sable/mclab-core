function [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = garc_dvp(theta, din, p)
% garc_dvp - Computes the partial derivatives of the system matrices in
% a model with GARCH structure in the error term along the direction p.
%    [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = ...
%                                         garc_dvp(theta, din, p)
%
% 5/4/97

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

if nargin < 3, e4error(13); end

mtipo=din(1,1);
if fix(mtipo/100) ~= 1, e4error(5); end

if size(theta,2) == 1
   k = (1:size(theta,1))';  
else
   k = find(theta(:,2) == 0);
end

if size(p,1) ~= size(k,1), e4error(11); end

if size(find(p~=0),1) == 0
   [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = garch_dv(theta, din, 1);
   dPhi = zeros(size(dPhi));
   dGam = zeros(size(dGam));
   dE   = zeros(size(dE));
   dH   = zeros(size(dH));
   dD   = zeros(size(dD));
   dC   = zeros(size(dC));
   dQ   = zeros(size(dQ));
   dPhig= zeros(size(dPhig));
   dGamg= zeros(size(dGamg));
   dEg  = zeros(size(dEg));
   dHg  = zeros(size(dHg));
   dDg  = zeros(size(dDg));
   return;
end

dPhi = 0; dGam = 0; dE = 0; dH = 0; dD = 0; dC = 0; dQ = 0;
dPhig = 0; dGamg = 0; dEg = 0; dHg = 0; dDg = 0;

for i=1:size(p,1)
    if p(i,1) ~= 0
       [dPhi1,dGam1,dE1,dH1,dD1,dC1,dQ1,dPhig1,dGamg1,dEg1,dHg1,dDg1] = garch_dv(theta, din, k(i));
       dPhi = dPhi1*p(i,1) + dPhi;
       dGam = dGam1*p(i,1) + dGam;
       dE   = dE1*p(i,1)   + dE;
       dH   = dH1*p(i,1)   + dH;
       dD   = dD1*p(i,1)   + dD;
       dC   = dC1*p(i,1)   + dC;
       dQ   = dQ1*p(i,1)   + dQ;
       dPhig= dPhig1*p(i,1)   + dPhig;
       dGamg= dGamg1*p(i,1) + dGamg;
       dEg  = dEg1*p(i,1)   + dEg;
       dHg  = dHg1*p(i,1)   + dHg;
       dDg  = dDg1*p(i,1)   + dDg;
    end
end