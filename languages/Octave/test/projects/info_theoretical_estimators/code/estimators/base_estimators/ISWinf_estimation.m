function [I] = ISWinf_estimation(Y,ds,co)
%function [I] = ISWinf_estimation(Y,ds,co)
%Estimates mutual information (I) using Schweizer-Wolff's kappa. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE:
%  Sergey Kirshner and Barnabas Poczos. ICA and ISA Using Schweizer-Wolff Measure of Dependence. International Conference on Machine Learning (ICML), pages 464-471, 2008.
%  Edward F. Wolff. N-dimensional measures of dependence. Stochastica, 4:175-188, 1980.
%  B. Schweizer and Edward F. Wolff. On Nonparametric Measures of Dependence for Random Variables. The Annals of Statistics 9:879-885, 1981.

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

%initialization:
    bound = 1;
    num_of_samples = size(Y,2);

if (num_of_samples <= co.max_num_of_samples_without_appr) %directly
    r = rank_transformation(Y);
    I = SW_kappa(r,bound);
else %approximation on a sparse grid
    bin_size = ceil(num_of_samples/co.max_num_of_bins);
    num_of_samples = floor(num_of_samples/bin_size)*bin_size;    
    r = rank_transformation(Y(:,1:num_of_samples));
    I = SW_kappa(r,bound,bin_size);
end    

