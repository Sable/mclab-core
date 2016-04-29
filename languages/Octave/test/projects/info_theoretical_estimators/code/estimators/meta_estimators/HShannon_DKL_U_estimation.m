function [H] = HShannon_DKL_U_estimation(Y,co)
%function [H] = HShannon_DKL_U_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using the relation H(Y) = -D(Y',U) + log(\prod_i(b_i-a_i)), where Y\in[a,b] = \times_{i=1}^d[a_i,b_i], D is the Kullback-Leibler divergence, Y' = linearly transformed version of Y to [0,1]^d, and U is the uniform distribution on [0,1]^d.
%
%Note:
%   1)We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%   2)This is a meta method: the Kullback-Leibler divergence estimator can be arbitrary.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.

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

%estimate the support of y (a,b):
    a = min(Y,[],2);
    b = max(Y,[],2);
    
%transform y to [0,1]^d:
    Y2 = bsxfun(@plus,bsxfun(@rdivide,Y,b-a),a./(a-b));
    
%generate samples from U[0,1]^d:
    U = rand(d,num_of_samples);    
    
H = -D_estimation(Y2,U,co.member_co) + log(prod(b-a)); 

