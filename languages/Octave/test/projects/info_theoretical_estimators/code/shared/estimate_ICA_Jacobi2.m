function [e_hat,W_hat] = estimate_ICA_Jacobi2(x,ICA)
%function [e_hat,W_hat] = estimate_ICA_Jacobi2(x,ICA)
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
%  Erik Learned-Miller and John W. Fisher, III. ICA using spacings estimates of entropy. Journal of Machine Learning Research, vol. 4, pp. 1271-1295, 2003. (The optimization technique of this paper has been adapted to general entropy/mutual information estimators.)

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
    e_hat = x; %estimated source
    W_hat = eye(d); %estimated demixing matrix
    %parameters you may would like to play with (you can also pass them via the ICA input structure):
        num_of_sweeps = d-1; %number of sweeps
        num_of_angles = 150; %for fixed sweep the (maximal) number of angles; the maximum is attained for the last sweep
        
num_of_angles_min = num_of_angles / 1.3^(ceil(num_of_sweeps/2));

for sweep = 1 : num_of_sweeps
    %sweep -> nangles:
        if sweep > (num_of_sweeps/2) %increase the number of angles exponentially for the second half of the sweeps
            t = ceil(sweep - num_of_sweeps/2);%0.5->1, 1->1; Z+0.5->Z+1, Z->Z
            nangles = floor(num_of_angles_min * 1.3^t);
        else %constant number of angles (<=30)
            nangles = max(30,floor(num_of_angles_min));
        end
    %nangles -> angles:
        angles = pi/2 * [0:nangles-1]/nangles;
    %disp:
       disp(strcat(['Jacobi2 optimization -> sweep: ',num2str(sweep),'(/',num2str(num_of_sweeps),').']));
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
