function [I] = IHoeffding_estimation(Y,ds,co)
%function [I] = IHoeffding_estimation(Y,ds,co)
%Estimates mutual information (I) using the multivariate version of Hoeffding's Phi. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE: 
%   Sandra Gaiser, Martin Ruppert, Friedrich Schmid. A multivariate version of Hoeffding's Phi-Square. Journal of Multivariate Analysis. 101 (2010) 2571-2586.

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
    if ~one_dimensional_problem(ds)
        error('The subspaces must be one-dimensional for this estimator.');
    end

[d,num_of_samples] = size(Y);
U = copula_transformation(Y);

%term1:
   term1 = Hoeffding_term1(U);
%term2:
   if co.small_sample_adjustment
       term2 = -2 * sum(prod(1-U.^2-(1-U)/num_of_samples,1)) / (num_of_samples * 2^d);
   else
       term2 = -2 * sum(prod(1-U.^2,1)) / (num_of_samples * 2^d);
   end
%term3:    
   if co.small_sample_adjustment
       term3 = ( (num_of_samples-1) * (2*num_of_samples-1) / (3*2*num_of_samples^2) )^d;
   else
       term3 = 1/3^d;
   end
%I:
   I = term1 + term2 + term3;        
    
if co.mult%multiplicative constant, if needed
    if co.small_sample_adjustment
        t1 = sum((1 - [1:num_of_samples-1]/num_of_samples).^d .* (2*[1:num_of_samples-1]-1)) / num_of_samples^2;
        t2 = -2 * sum( ( (num_of_samples * (num_of_samples-1) - [1:num_of_samples] .* ([1:num_of_samples]-1)) / (2*num_of_samples^2) ).^d ) / num_of_samples;
        t3 = term3;
        inv_hd = t1 + t2 + t3;%1/h(d,n)
    else
        inv_hd = 2/((d+1)*(d+2)) - factorial(d)/(2^d*prod([0:d]+1/2)) + 1/3^d;%1/h(d)
    end
    I = I / inv_hd;
end

I = sqrt(abs(I));

