function [H] = HShannon_spacing_VKDE_estimation(Y,co)
%function [H] = HShannon_spacing_VKDE_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using the Vasicek's spacing method corrected with KDE (kernel density estimation) at the left and right sides.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: Havva Alizadeh Noughabi and Reza Alizadeh Noughabi. On the entropy estimators. Journal of Statistical Computatiion and Simulation, 83:784-792, 2013.

%Copyright (C) 2013 Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) Matlab/Octave toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0].

[d,num_of_samples] = size(Y);

%verification:
    if d~=1
        error('The samples must be one-dimensional for this estimator.');
    end

m = floor(sqrt(num_of_samples));%m/num_of_samples->0, m,num_of_samples->infty; m<num_of_samples/2
Y_sorted = sort(Y);
stdY = std(Y); %sample standard deviation
h = 1.06 * stdY * num_of_samples^(-1/5);

%s:
   sD = sqdistance(Y_sorted([1:m,end-m+1:end])/h,Y_sorted/h);%pairwise squared distances to KDE
   s13 = mean(exp(-sD/2),2) / (sqrt(2*pi)*h); %column vector => ".'" in "H=..."
   s2 = (2*m/num_of_samples) ./ (Y_sorted(2*m+1:end) - Y_sorted(1:end-2*m));
   
H = -mean(log([s13.',s2]));
   


  

