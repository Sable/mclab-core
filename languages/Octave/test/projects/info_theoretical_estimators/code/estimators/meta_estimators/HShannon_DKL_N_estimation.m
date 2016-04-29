function [H] = HShannon_DKL_N_estimation(Y,co)
%function [H] = HShannon_DKL_N_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using the relation H(Y) = H(G) - D(Y,G), where G is Gaussian [N(E(Y),cov(Y)] and D is the Kullback-Leibler divergence.
%
%Note:
%   1)We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%   2)This is a meta method: the Kullback-Leibler divergence estimator can be arbitrary.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Quing Wang, Sanjeev R. Kulkarni, and Sergio Verdu. Universal estimation of information measures for analog sources. Foundations And Trends In Communications And Information Theory, 5:265-353, 2009.

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

[d,num_of_samples] = size(Y); %dimension, number of samples

%estimate the mean and covariance of Y:
    m = mean(Y,2);
    C = cov(Y.');
    
%entropy of N(m,C):
    H_normal = 1/2 * log( (2*pi*exp(1))^d * det(C) );

%generate samples from N(m,C):
    R = chol(C); %R'*R=C
    Y_normal = R.' * randn(d,num_of_samples) + repmat(m,1,num_of_samples);    
    
H = H_normal - D_estimation(Y,Y_normal,co.member_co); 
