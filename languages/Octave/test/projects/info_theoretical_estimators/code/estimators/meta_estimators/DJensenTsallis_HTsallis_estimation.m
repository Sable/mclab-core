function [D_JT] = DJensenTsallis_HTsallis_estimation(Y1,Y2,co)
%function [D_JT] = DJensenTsallis_HTsallis_estimation(Y1,Y2,co)
%Estimates the Jensen-Tsallis divergence of Y1 and Y2 using the relation: 
%D_JT(f_1,f_2) = D_{JT,alpha}(f_1,f_2) = H_{T,alpha}((y^1+y^2)/2) - [1/2*H_{T,alpha}(y^1) + 1/2*H_{T,alpha}(y^2)], where y^i has density f_i (i=1,2), (y^1+y^2)/2 is the mixture distribution of y^1 and y^2 with 1/2-1/2 weights, and H_{T,alpha} denotes the Tsallis entropy.
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the Tsallis entropy estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.
%
%REFERENCE:
%  J. Burbea and C.R. Rao. On the convexity of some divergence measures based on entropy functions. IEEE Transactions on Information Theory, 28:489-495, 1982.

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

w = [1/2,1/2];
mixtureY = mixture_distribution(Y1,Y2,w);
D_JT =  H_estimation(mixtureY,co.member_co) - (w(1)*H_estimation(Y1,co.member_co) + w(2)*H_estimation(Y2,co.member_co));
