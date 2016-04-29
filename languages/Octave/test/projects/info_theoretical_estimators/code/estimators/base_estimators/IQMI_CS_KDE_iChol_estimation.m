function [I] = IQMI_CS_KDE_iChol_estimation(Y,ds,co)
%function [I] = IQMI_CS_KDE_iChol_estimation(Y,ds,co)
%Estimates the Cauchy-Schwartz quadratic mutual information (I) approximately, applying Gaussian KDE (kernel density estimation) and incomplete Cholesky decomposition. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE: 
%   Sohan Seth and Jose C. Principe. On speeding up computation in information theoretic learning. International Joint Conference on Neural Networks (IJCNN), pages 2883-2887, 2009.

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
    if length(ds)~=2 %M=2
        error('The number of components must be 2 for this estimator.');
    end

I = qmi_cs(Y(1:ds(1),:).',Y(ds(1)+1:ds(1)+ds(2),:).',co.sigma);

