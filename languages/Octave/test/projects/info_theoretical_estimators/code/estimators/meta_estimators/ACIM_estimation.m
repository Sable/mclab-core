function [A] = ACIM_estimation(Y,ds,co)
%function [A] = ACIM_estimation(Y,ds,co)
%Estimates the correntropy induced metric of Y1 and Y2 using the relation CIM(y^1,y^_2) = [k(0,0)-correntropy(y^1,y^2)]^{1/2}, where k is the applied (Gaussian) kernel. 
%
%We use the naming convention 'A<name>_estimation' to ease embedding new association measure estimator methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: association measure estimator object.
%
%Note:
%   1)We use the naming convention 'A<name>_estimation' to ease embedding new association estimator methods.
%   2)This is a meta method: the (Gaussian) correntropy estimator can be arbitrary.
%
%REFERENCE: 
%   Sohan Seth and Jose C. Principe. Compressed signal reconstruction using the correntropy induced metric. In IEEE International Conference on Acoustics, Speech and Signal Processing (ICASSP), pages 3845-3848, 2008.
%   Weifeng Liu, P.P. Pokharel, and Jose C. Principe. Correntropy: Properties and applications in non-Gaussian signal processing. IEEE Transactions on Signal Processing, 55:5286-5298, 2007.

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
    if ~one_dimensional_problem(ds) || length(ds)~=2
        error('There must be 2 pieces of one-dimensional subspaces (coordinates) for this estimator.');
    end

A0 = A_estimation(Y,ds,co.member_co); %correntropy
k0 = 1; %Assumption: k(u,u)=1. Example (Gaussian kernel): k(u,v)=e^{-(u-v)^2}, k(u,u)=1
A =  sqrt(k0-A0);

