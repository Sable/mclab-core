function [dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = sc_dvown(theta,din,i, userf)
% sc_dvown - Builds a call to the function which name is specified in FOWN.
% This function should return the derivatives of the system matrices with
% respect to the i-th parameter.
%    [dPhi, dGam, dE, dHv, dDv, dCv, dQ, dS, dR, dHf, dDf, dCf] = ...
%                                             sc_dvown(theta, din, i, userf)
% Only for models with time-varying parameters.
%
% 4/4/97

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

if nargin < 3, e4error(3); end
if nargin < 4, e4error(13); end

userf = deblank(userf(:)');
if ~exist(userf), e4error(18,userf); end

[dPhi,dGam,dE,dHv,dDv,dCv,dQ,dS,dR,dHf,dDf,dCf] = eval([userf '(theta,din,i)']);
