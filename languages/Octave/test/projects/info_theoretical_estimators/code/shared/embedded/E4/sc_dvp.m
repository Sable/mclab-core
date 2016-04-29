function [dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = sc_dvp(theta, din, p)
% sc_dvp   - Computes the partial derivatives of the SS matrices in
% a time-varying parameters model along the direction of p.
%    [dPhi, dGam, dE, dHb, dDb, dCb, dQ, dS, dR, dHf, dDf, dCf] = ...
%                                                 sc_dvp(theta, din, p)
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
if mtipo >= 1000 | mtipo < 200, e4error(5); end

if size(theta,2) == 1
   k = (1:size(theta,1))';
else
   k = find(theta(:,2) == 0);
end

if size(p,1) ~= size(k,1), e4error(11); end

if size(find(p~=0),1) == 0
   [dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = sc_dv(theta, din, 1);
   dPhi = zeros(size(dPhi));
   dGam = zeros(size(dGam));
   dE   = zeros(size(dE));
   dHv  = zeros(size(dHv));
   dDv  = zeros(size(dDv));
   dCv  = zeros(size(dCv));
   dQ   = zeros(size(dQ));
   dS   = zeros(size(dS));
   dR   = zeros(size(dR));
   dHf  = zeros(size(dHf));
   dDf  = zeros(size(dDf));
   dCf  = zeros(size(dCf));
   return;
end

dPhi = 0; dGam = 0; dE = 0; dHv = 0; dDv = 0; dCv = 0; dQ = 0; dS = 0; dR = 0;
dHf = 0; dDf = 0; dCf = 0;

for i=1:size(p,1)
    if p(i,1) ~= 0
       [dPhi1,dGam1,dE1,dHv1,dDv1,dCv1,dQ1,dS1,dR1,dHf1,dDf1,dCf1] = sc_dv(theta, din, k(i));
       dPhi = dPhi1*p(i,1) + dPhi;
       dGam = dGam1*p(i,1) + dGam;
       dE   = dE1*p(i,1)   + dE;
       dHv  = dHv1*p(i,1)  + dHv;
       dDv  = dDv1*p(i,1)  + dDv;
       dCv  = dCv1*p(i,1)  + dCv;
       dQ   = dQ1*p(i,1)   + dQ;
       dS   = dS1*p(i,1)   + dS;
       dR   = dR1*p(i,1)   + dR;
       dHf  = dHf1*p(i,1)  + dHf;
       dDf  = dDf1*p(i,1)  + dDf;
       dCf  = dCf1*p(i,1)  + dCf;
    end
end
