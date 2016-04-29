function [D] = DMMD_online_estimation(Y1,Y2,co)
%function [D] = DMMD_online_estimation(Y1,Y2,co)
%Estimates divergence (D) of Y1 and Y2 using the MMD (maximum mean discrepancy) method, online. 
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] must be equal; otherwise their minimum is taken.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Arthur Gretton, Karsten M. Borgwardt, Malte J. Rasch, Bernhard Scholkopf and Alexander Smola. A Kernel Two-Sample Test. Journal of Machine  Learning Research 13 (2012) 723-773. See Lemma 14.
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

%verification:
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);
    %size(Y1) must be equal to size(Y2):
        if num_of_samplesY1~=num_of_samplesY2
            warning('There must be equal number of samples in Y1 and Y2 for this estimator. Minimum of the sample numbers has been taken.');
        end
        if dY1~=dY2
            error('The dimension of the samples in Y1 and Y2 must be equal.');
        end
    num_of_samples = min(num_of_samplesY1,num_of_samplesY2);        
    %Number of samples must be even:
        if ~all_even(num_of_samples)
            warning('The number of samples must be even, the last sample is discarded.');
            num_of_samples = num_of_samples - 1;
        end
        
%initialization: 
    odd_indices = [1:2:num_of_samples];
    even_indices = [2:2:num_of_samples];
    
%Y1i,Y1j,Y2i,Y2j:
    Y1i = Y1(:,odd_indices);
    Y1j = Y1(:,even_indices);
    Y2i = Y2(:,odd_indices);
    Y2j = Y2(:,even_indices);
    
switch co.kernel
    case 'RBF'
        D = (K_RBF(Y1i,Y1j,co) + K_RBF(Y2i,Y2j,co) - K_RBF(Y1i,Y2j,co) - K_RBF(Y1j,Y2i,co)) / (num_of_samples/2);
    case 'linear'
        D = (K_linear(Y1i,Y1j) + K_linear(Y2i,Y2j) - K_linear(Y1i,Y2j) - K_linear(Y1j,Y2i)) / (num_of_samples/2);
    otherwise
        error('Kernel=?');
end

%-----------------------------
function [s] = K_RBF(U,V,co)
%Computes \sum_i kernel(U(:,i),V(:,i)), RBF (Gaussian) kernel is used with std=co.sigma

s = sum( exp(-sum((U-V).^2,1)/(2*co.sigma^2)) );

%--------
function [s] = K_linear(U,V)
%Computes \sum_i kernel(U(:,i),V(:,i)) in case of a linear kernel

s = sum(dot(U,V));
