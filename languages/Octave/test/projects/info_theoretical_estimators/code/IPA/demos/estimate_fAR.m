function [E] = estimate_fAR(x,fAR)
%function [E] = estimate_fAR(x,fAR)
%Estimates fAR model of order fAR.L (=:L). Method: estimate the "u_t = [x_{t-1},...,x_{t-L}] -> v_t = x_t" mapping nonparametrically.
%
%INPUT:
%   x: x(:,t) is the t^th observation from the fAR model.
%   fAR: fAR estimator,
%      fAR.method: method used for fAR identification. Possibilities: 'recursiveNW'.
%      fAR.L: fAR order, see also 'recursive_Nadaraya_Watson_estimator.m'.
%OUTPUT:
%   E: estimated innovation, E(:,t) at time t.

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

%initialization:
    L = fAR.L;

switch fAR.method
    case 'recursiveNW'
        U = concatenation(x(:,1:end-1),L);
        V = x(:,L+1:end);
        E = V - recursive_Nadaraya_Watson_estimator(U,V,fAR);
    otherwise
        error('fAR fit method=?');
end