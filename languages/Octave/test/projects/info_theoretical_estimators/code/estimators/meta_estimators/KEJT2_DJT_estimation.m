function [K] = KEJT2_DJT_estimation(Y1,Y2,co)
%function [K] = KEJT2_DJT_estimation(Y1,Y2,co)
%Estimates the exponentiated Jensen-Tsallis kernel-2 of two distributions from which we have samples (Y1 and Y2) using the relation: 
%K_EJT2(f_1,f_2) = exp[-u x D_JT(f_1,f_2)], where D_JT is the Jensen-Tsallis divergence, u>0.
%
%%Note:
%   1)We use the naming convention 'K<name>_estimation' to ease embedding new kernels on distributions.
%   2)This is a meta method: the Jensen-Tsallis divergence estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: estimator object of a kernel on distributions.
%
%REFERENCE: 
%   Andre F. T. Martins, Noah A. Smith, Eric P. Xing, Pedro M. Q. Aguiar, and Mario A. T. Figueiredo. Nonextensive information theoretical kernels on measures. Journal of Machine Learning Research, 10:935-975, 2009.

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if size(Y1,1)~=size(Y2,1)
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

K = exp(-co.u * D_estimation(Y1,Y2,co.member_co));
