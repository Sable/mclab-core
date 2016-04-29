function [I] = IKCCA_estimation(Y,ds,co)
%function [I] = IKCCA_estimation(Y,ds,co)
%Estimates mutual information (I) using the KCCA (kernel canonical correlation analysis) method. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE:
%   Zoltan Szabo, Barnabas Poczos, Andras Lorincz. Undercomplete Blind Subspace Deconvolution. Journal of Machine Learning Research 8(May):1063-1095, 2007. (multidimensional case, i.e., ds(j)>=1)
%   Francis Bach, Michael I. Jordan. Kernel Independent Component Analysis. Journal of Machine Learning Research, 3: 1-48, 2002. (one-dimensional case, i.e., ds(1)=ds(2)=...=ds(end)=1)

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

R = compute_matrixR_KCCA_KGV(Y,ds,co);

[temp,eig_min] = eigs(R,1,'SM');%compute the smallest eigenvalue of R
I = -log(eig_min) / 2;
