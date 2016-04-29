function [D] = DBMMD_DMMD_Ustat_estimation(Y1,Y2,co)
%function [D] = DBMMD_DMMD_Ustat_estimation(Y1,Y2,co)
%Estimates divergence (D) of Y1 and Y2 using the MMD (maximum mean discrepancy) method, average of U-statistics, i.e., the samples are partitioned into blocks ('B').
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the U-statistics based MMD estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Wojciech Zaremba, Arthur Gretton, and Matthew Blaschko. B-tests: Low variance kernel two-sample tests. In Advances in Neural Information Processing Systems (NIPS), pages 755–763, 2013.

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    if num_of_samplesY1~=num_of_samplesY2
        warning('There must be equal number of samples in Y1 and Y2 for this estimator. Minimum of the sample numbers has been taken.');
    end
    
num_of_samples = min(num_of_samplesY1,num_of_samplesY2);
B = floor(sqrt(num_of_samples));
num_of_blocks = floor(num_of_samples/B);

D = 0;
for k = 1 : num_of_blocks
    D = D + D_estimation(Y1(:,(k-1)*B+1:k*B),Y2(:,(k-1)*B+1:k*B),co.member_co);
end    

D = D / num_of_blocks;
