function [D_L] = DL_DKL_estimation(Y1,Y2,co)
%function [D_L] = DL_DKL_estimation(Y1,Y2,co)
%Estimates the L divergence of Y1 and Y2 using the relation: D_L(f_1,f_2) = D(f_1,(f_1+f_2)/2) + D(f_2,(f_1+f_2)/2), where D denotes the Kullback-Leibler divergence.
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the Kullback-Leibler divergence estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.
%
%REFERENCE:
%  Jianhua Lin. Divergence measures based on the Shannon entropy. IEEE Transactions on Information Theory, 37:145-151, 1991.

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

%mixture of Y1 and Y2 with 1/2, 1/2 weights:
    w = [1/2;1/2];
    %samples to the mixture (second part of Y1 and Y2; =:Y1m, Y2m):
        num_of_samplesY1m = floor(num_of_samplesY1/2); %(max) number of samples to the mixture from Y1
        num_of_samplesY2m = floor(num_of_samplesY2/2); %(max) number of samples to the mixture from Y2
        Y1m = Y1(:,num_of_samplesY1m+1:end);
        Y2m = Y2(:,num_of_samplesY2m+1:end);
    mixtureY = mixture_distribution(Y1m,Y2m,w);

D_L =  D_estimation(Y1(:,1:num_of_samplesY1m),mixtureY,co.member_co) + D_estimation(Y2(:,1:num_of_samplesY2m),mixtureY,co.member_co);
