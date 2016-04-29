function [H] = HShannon_kNN_k_estimation(Y,co)
%function [H] = HShannon_kNN_k_estimation(Y,co)
%Estimates the Shannon differential entropy (H) of Y using the kNN method (and neighbors S={k}).
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE:
%   M. N. Goria, Nikolai N. Leonenko, V. V. Mergel, and P. L. Novi Inverardi. A new class of random vector entropy estimators and its applications in testing statistical hypotheses. Journal of Nonparametric Statistics, 17: 277-297, 2005. (S={k})
%   Harshinder Singh, Neeraj Misra, Vladimir Hnizdo, Adam Fedorowicz and Eugene Demchuk. Nearest neighbor estimates of entropy. American Journal of Mathematical and Management Sciences, 23, 301-321, 2003. (S={k})
%   L. F. Kozachenko and Nikolai N. Leonenko. A statistical estimate for the entropy of a random vector. Problems of Information Transmission, 23:9-16, 1987. (S={1})

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

[d,num_of_samples] = size(Y);
squared_distances = kNN_squared_distances(Y,Y,co,1);

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%H estimation:
    V = volume_of_the_unit_ball(d); 
    H = log(num_of_samples-1) - psi(co.k) + log(V) + d / num_of_samples * sum(log(sqrt(squared_distances(co.k,:)))); %sqrt <= squared_distances,
 
