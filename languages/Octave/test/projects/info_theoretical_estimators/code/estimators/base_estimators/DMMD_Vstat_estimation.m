function [D] = DMMD_Vstat_estimation(Y1,Y2,co)
%function [D] = DMMD_Vstat_estimation(Y1,Y2,co)
%Estimates divergence (D) of Y1 and Y2 using the MMD (maximum mean discrepancy) method, applying V-statistics. 
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Arthur Gretton, Karsten M. Borgwardt, Malte J. Rasch, Bernhard Scholkopf and Alexander Smola. A Kernel Two-Sample Test. Journal of Machine  Learning Research 13 (2012) 723-773.
%   Alain Berlinet and Christine Thomas-Agnan. Reproducing Kernel Hilbert Spaces in Probability and Statistics. Kluwer, 2004. (mean embedding)

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

[dY1,num_of_samplesY1] = size(Y1);
[dY2,num_of_samplesY2] = size(Y2);

%verification:
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    
%kY1Y1,kY2Y2,kY1Y2:    
    switch co.kernel
        case 'RBF' 
            %pairwise distances:
                kY1Y1 = sqdistance(Y1);
                kY2Y2 = sqdistance(Y2);
                kY1Y2 = sqdistance(Y1,Y2);
            %distance(i,j) ->  kernel(i,j):
                kY1Y1 = exp(-kY1Y1/(2*co.sigma^2));
                kY2Y2 = exp(-kY2Y2/(2*co.sigma^2));
                kY1Y2 = exp(-kY1Y2/(2*co.sigma^2));
        case 'linear'
            kY1Y1 = Y1.' * Y1;
            kY2Y2 = Y2.' * Y2;
            kY1Y2 = Y1.' * Y2;
        otherwise
            error('Kernel=?');
    end

term1 = sum(sum(kY1Y1)) / (num_of_samplesY1^2);
term2 = sum(sum(kY2Y2)) / (num_of_samplesY2^2);
term3 = -2 * sum(sum(kY1Y2)) / (num_of_samplesY1*num_of_samplesY2);

D = sqrt(abs(term1+term2+term3)); %abs(): to avoid 'sqrt(negative)' values
