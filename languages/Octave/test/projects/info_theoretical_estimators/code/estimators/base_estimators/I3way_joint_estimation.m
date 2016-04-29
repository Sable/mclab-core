function [I] = I3way_joint_estimation(Y,ds,co)
%function [I] = I3way_joint_estimation(Y,ds,co)
%Estimates mutual information (I) using the embedding of the 'joint - product of the marginals'. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE:
%   Dino Sejdinovic, Arthur Gretton, and Wicher Bergsma. A kernel test for three-variable interactions. In Advances in Neural Information Processing Systems (NIPS), pages 1124–1132, 2013. (Lancaster three-variable interaction based dependency index).
%   Henry Oliver Lancaster. The Chi-squared Distribution. John Wiley and Sons Inc, 1969. (Lancaster interaction)

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

%dimension, number of samples:
    [dY,num_of_samples] = size(Y);

%verification:
    if sum(ds) ~= dY;
        error('The subspace dimensions are not compatible with Y.');
    end
    if length(ds) ~= 3
        error('There must be 3 subspaces for this estimator.');
    end
    
%Gram matrices (K1,K2,K3):
    %K1:
        switch co.kernel1
            case 'RBF' 
                %Set co.sigma1 using median heuristic, if needed:
                    if isnan(co.sigma1)
                        co.sigma1 = median_heuristic(Y(1:ds(1),:));
                    end
                %pairwise distances, distance(i,j) ->  kernel(i,j):
                    K1 = sqdistance(Y(1:ds(1),:));
                    K1 = exp(-K1/(2*co.sigma1^2));
            otherwise
                error('Kernel1=?');
        end
    %K2:
        switch co.kernel2
            case 'RBF' 
                %Set co.sigma2 using median heuristic, if needed:
                    if isnan(co.sigma2)
                        co.sigma2 = median_heuristic(Y(ds(1)+1:ds(1)+ds(2),:));
                    end
                %pairwise distances, distance(i,j) ->  kernel(i,j):            
                    K2 = sqdistance(Y(ds(1)+1:ds(1)+ds(2),:));
                    K2 = exp(-K2/(2*co.sigma2^2));
             otherwise
                error('Kernel2=?');
        end
    %K3:
        switch co.kernel3
            case 'RBF' 
                %Set co.sigma3 using median heuristic, if needed:
                    if isnan(co.sigma3)
                        co.sigma3 = median_heuristic(Y(ds(1)+ds(2)+1:dY,:));
                    end                
                %pairwise distances, distance(i,j) ->  kernel(i,j):            
                   K3 = sqdistance(Y(ds(1)+ds(2)+1:dY,:));
                   K3 = exp(-K3/(2*co.sigma3^2));
            otherwise
                error('Kernel3=?');
        end
   
%The Hadamard product of the Gram matrices:
    prod_of_Ks = K1 .* K2 .* K3;
    
term1 = mean(mean(prod_of_Ks));
term2 = -2 * mean( mean(K1) .* mean(K2) .* mean(K3) );
term3 = mean(mean(K1)) * mean(mean(K2)) * mean(mean(K3));
I = term1 + term2 + term3;    
