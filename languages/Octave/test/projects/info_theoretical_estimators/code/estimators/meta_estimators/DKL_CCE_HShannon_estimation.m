function [D] = DKL_CCE_HShannon_estimation(Y1,Y2,co)
%function [D] = DKL_CCE_HShannon_estimation(Y1,Y2,co)
%Estimates the Kullback-Leibler divergence (D) using the relation: D(f_1,f_2) = CE(f_1,f_2) - H(f_1). Here D denotes the Kullback-Leibler divergence, CE stands for cross-entropy and H is the Shannon differential entropy. 
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the cross-entropy and Shannon entropy estimators can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.

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
    if size(Y1,1)~=size(Y2,1)
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

CE = C_estimation(Y1,Y2,co.CE_member_co);
H = H_estimation(Y1,co.H_member_co);
D =  CE - H;
