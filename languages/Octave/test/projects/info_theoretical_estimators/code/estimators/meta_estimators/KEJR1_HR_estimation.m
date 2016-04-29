function [K] = KEJR1_HR_estimation(Y1,Y2,co)
%function [K] = KEJR1_HR_estimation(Y1,Y2,co)
%Estimates the exponentiated Jensen-Renyi kernel-1 of two distributions from which we have samples (Y1 and Y2) using the relation: 
%K_EJR1(f_1,f_2) = exp[-u x H_R((y^1+y^2)/2)], where H_R is the Renyi entropy, (y^1+y^2)/2 is the mixture of y^1~f_1 and y^2~f_2 with 1/2-1/2 weights, u>0.
%
%%Note:
%   1)We use the naming convention 'K<name>_estimation' to ease embedding new kernels on distributions.
%   2)This is a meta method: the Renyi entropy estimator can be arbitrary.
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

%mixture:    
    w = [1/2,1/2];
    mixtureY = mixture_distribution(Y1,Y2,w);

K = exp(-co.u * H_estimation(mixtureY,co.member_co));
