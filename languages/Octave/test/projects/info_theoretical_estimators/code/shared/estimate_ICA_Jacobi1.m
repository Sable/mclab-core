function [e_hat,W_hat] = estimate_ICA_Jacobi1(x,ICA)
%function [e_hat,W_hat] = estimate_ICA_Jacobi1(x,ICA)
%Performs ICA on the whitened signal 'x' using Jacobi (also known as Givens) rotation based optimization.
%
%INPUT:
%   x: x(:,t) is the t^th sample.
%   ICA: solver for independent component analysis. 
%      Mandatory fields:
%         ICA.cost_type: cost type ('sumH' or 'I'). 
%         ICA.cost_name: cost name (an entropy/mutual information cost name).
%      Examples: 
%         ICA.cost_type = 'sumH'; ICA.cost_name = 'Renyi_kNN_1tok';
%         ICA.cost_type = 'I'; ICA.cost_name = 'KCCA';
%OUTPUT:
%   e_hat: estimated ICA elements; e_hat(:,t) is the t^th sample.
%   W_hat: estimated ICA demixing matrix.
%
%REFERENCE:
%  Sergey Kirshner and Barnabas Poczos. ICA and ISA Using Schweizer-Wolff Measure of Dependence. International Conference on Machine Learning (ICML), pages 464-471, 2008. (The optimization technique of this paper has been adapted to general entropy/mutual information estimators.)

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

%initialization:
    %initialization of the entropy/mutual information estimator:
        %verification:
            if ~strcmp(ICA.cost_type,'sumH') && ~strcmp(ICA.cost_type,'I')
                error('ICA.cost type is not correct, it should be: sumH or I.');
            end
        co = co_initialization(ICA.cost_type,ICA.cost_name,0);%'0': multiplicative constant is not important
    d = size(x,1); %dimension
    %parameters you may would like to play with (you can also pass them via the ICA input structure):
        num_of_sweeps = d;  %for fixed level the number of sweeps    
        num_of_angles = 90; %for fixed sweep the (maximal) number of angles; the maximum is attained for the last level                
        num_of_levels = 3; %number of levels
    e_hat = x; %estimated source
    W_hat = eye(d); %estimated demixing matrix
        
for level = 1 : num_of_levels
    %angles for the actual level:
        nangles = ceil(num_of_angles / 2^(num_of_levels-level));
        angles = pi/2 * [0:nangles-1]/nangles;
    for sweep = 1 : num_of_sweeps
        %disp:
            disp(strcat(['Jacobi1 optimization, level: ',num2str(level),'(/',num2str(num_of_levels),')', ' -> sweep: ',num2str(sweep),'(/',num2str(num_of_sweeps),').']));            
        %sweep through over all (i1,i2) pairs [i1<i2]:
            for i1 = 1 : d-1
            for i2 = i1+1 : d
                cost_opt = Inf;
                %optimal angle (angle_opt):
                    for angle = angles
                        R = rotation_matrix(angle);
                        %cost of 'R * e_hat([i1,i2],:))':
                            cost = cost_Jacobi_coordinate_pair(R * e_hat([i1,i2],:),co,ICA.cost_type);
                        if cost < cost_opt %the rotation seems to be useful
                            cost_opt = cost;
                            angle_opt = angle;
                        end
                    end
                %update the estimated demixing matrix (W_hat), source (e_hat):
                    R = rotation_matrix(angle_opt);%R optimal
                    W_hat([i1,i2],:) = R * W_hat([i1,i2],:);
                    e_hat([i1,i2],:) = R * e_hat([i1,i2],:); 
            end
            end
    end
end
