function [I] = IL2_DL2_estimation(Y,ds,co)
%function [I] = IL2_DL2_estimation(Y,ds,co)
%Estimates L2 mutual information (I) based on L2 divergence. The estimation is carried out according to the relation: I(y^1,...,y^M) = D(f_y,\prod_{m=1}^M f_{y^m}).
%
%Note:
%   1)We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%   2)This is a meta method: the L2 divergence estimator can be arbitrary. 
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE:
%   Barnabas Poczos, Zoltan Szabo, Jeff Schneider: Nonparametric divergence estimators for Independent Subspace Analysis. European Signal Processing Conference (EUSIPCO), pages 1849-1853, 2011.

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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if sum(ds) ~= size(Y,1);
        error('The subspace dimensions are not compatible with Y.');
    end

[Y1,Y2] = div_sample_generation(Y,ds);
I = D_estimation(Y1,Y2,co.member_co);
