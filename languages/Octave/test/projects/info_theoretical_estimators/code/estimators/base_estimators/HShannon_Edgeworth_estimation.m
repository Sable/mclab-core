function [H] = HShannon_Edgeworth_estimation(Y,co)
%function [H] = HShannon_Edgeworth_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using Edgeworth expansion.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Marc Van Hulle. Edgeworth approximation of multivariate differential entropy. Neural Computation, 17(9), 1903-1910, 2005.

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

[d,num_of_samples] = size(Y);

%normalize Y to have zero mean and unit std:
    Y = whiten_E0(Y);%E=0, this step does not change the Shannon entropy of the variable
    %std(Y(i,:))=1:
        s = sqrt(sum(Y.^2,2)/(num_of_samples-1));%= std(Y,[],2)
        Y = Y./repmat(s,1,num_of_samples);
        H_whiten = log(prod(s));%we will take this scaling into account via the entropy transformation rule [ H(Wz) = H(z)+log(|det(W)|) ] at the end
    
H_normal = log(det(cov(Y.')))/2 + d/2 * log(2*pi) + d/2;%Shannon entropy of a normal variable with cov(Y.') covariance.
[t1,t2,t3] = Edgeworth_t1_t2_t3(Y);

H = (H_normal - (t1+t2+t3) / 12) + H_whiten;
