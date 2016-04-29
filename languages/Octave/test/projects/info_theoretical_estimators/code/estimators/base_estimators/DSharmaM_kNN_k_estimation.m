function [D] = DSharmaM_kNN_k_estimation(Y1,Y2,co)
%function [D] = DSharmaM_kNN_k_estimation(Y1,Y2,co)
%Estimates the Sharma-Mittal divergence (D) of Y1 and Y2 using maximum likelihood estimation (MLE) + analytical formula corresponding in the chosen exponential family. 
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Barnabas Poczos, Zoltan Szabo, Jeff Schneider. Nonparametric divergence estimators for Independent Subspace Analysis. European Signal Processing Conference (EUSIPCO), pages 1849-1853, 2011.
%   Barnabas Poczos, Jeff Schneider. On the Estimation of alpha-Divergences. International conference on Artificial Intelligence and Statistics (AISTATS), pages 609-617, 2011.
%   Marco Massi. A step beyond Tsallis and Renyi entropies. Physics Letters A, 338:217-224, 2005. (Sharma-Mittal divergence definition)

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
    dY1 = size(Y1,1);
    dY2 = size(Y2,1);
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
 
C = estimate_Dtemp1(Y1,Y2,co);
D = (C^((1-co.beta)/(1-co.alpha)) - 1) / (co.beta-1);
