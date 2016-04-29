function [D] = DCS_KDE_iChol_estimation(Y1,Y2,co)
%function [D] = DCS_KDE_iChol_estimation(Y1,Y2,co)
%Estimates the Cauchy-Schwartz divergence using Gaussian KDE (kernel density estimation) and incomplete Cholesky decomposition. 
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] must be equal; otherwise their minimum is taken. 
%  co: divergence estimator object.
%
%REFERENCE: 
%   Sohan Seth and Jose C. Principe. On speeding up computation in information theoretic learning. In International Joint Conference on Neural Networks (IJCNN), pages 2883-2887, 2009.

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
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);

    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

    if num_of_samplesY1~=num_of_samplesY2
        warning('There must be equal number of samples in Y1 and Y2 for this estimator. Minimum of the sample numbers has been taken.');
        num_of_samples = min(num_of_samplesY1,num_of_samplesY2);
        Y1 = Y1(:,1:num_of_samples);
        Y2 = Y2(:,1:num_of_samples);
    end

D = d_cs(Y1.',Y2.',co.sigma);
