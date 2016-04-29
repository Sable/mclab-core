function [H] = HRPensemble_estimation(Y,co)
%function [H] = HRPensemble_estimation(Y,co)
%Estimates entropy (H) from the average of H estimations on RP-ed (random projection) groups of samples.
%
%Note:
%   1)We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%   2)This a meta method: the applied entorpy estimator can be arbitrary.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Zoltan Szabo, Andras Lorincz: Fast Parallel Estimation of High Dimensional Information Theoretical Quantities with Low Dimensional Random Projection Ensembles. International Conference on Independent Component Analysis and Signal Separation (ICA), pages 146-153, 2009.

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

%initialization:
    g = co.group_size;    
    [d,num_of_samples] = size(Y);
    %verification (co.dim_RP<=d):
        if co.dim_RP > d
            error('RP dimension must be <= dimension of the samples.');
        end
    d_RP = co.dim_RP;
    num_of_groups = floor(num_of_samples/g);
    
H = 0;    
for k = 1 : num_of_groups
	R = randn(d_RP,d) / sqrt(d_RP);
    H = H + H_estimation(R*Y(:,(k-1)*g+1:k*g),co.member_co);
end
H = H / num_of_groups;
