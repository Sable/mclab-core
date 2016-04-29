function [H] = HShannon_spacing_Vpconst_estimation(Y,co)
%function [H] = HShannon_spacing_Vpconst_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using Vasicek's spacing method with piecewise constant correction.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Nader Ebrahimi, Kurt Pflughoeft, and Ehsan S. Soofi. Two measures of sample entropy. Statistics and Probability Letters, 20:225-234, 1994.

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

%verification:
    if d~=1
        error('The samples must be one-dimensional for this estimator.');
    end
    
m = floor(sqrt(num_of_samples));%m/num_of_samples->0, m,num_of_samples->infty
Y_sorted = sort(Y);
Y_sorted = [repmat(Y_sorted(1),1,m),Y_sorted,repmat(Y_sorted(end),1,m)];
diffs = Y_sorted(2*m+1:num_of_samples+2*m) - Y_sorted(1:num_of_samples);
 c = [ones(1,m),2*ones(1,num_of_samples-2*m),ones(1,m)]; %piecewise constant correction
H = mean(log (num_of_samples / m * diffs./c));


