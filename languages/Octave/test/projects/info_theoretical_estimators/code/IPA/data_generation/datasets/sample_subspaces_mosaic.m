function [e,de] = sample_subspaces_mosaic(num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces_mosaic(num_of_comps,num_of_samples)
%Sampling from the 'mosaic' dataset; number of subspaces: num_of_comps; number of samples: num_of_samples.
%In this case, mosaic images act as densities, i.e, the probability of a position (x,y)
%is proportional to its pixel intensity.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_mosaic(4,1000);

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

max_num_of_comps = 4;

if num_of_comps > max_num_of_comps 
    error(strcat('The number of components must be <=',num2str(max_num_of_comps),'.'));
else
    e = zeros(2*num_of_comps,num_of_samples);%preallocation
    S = 100; %the images are of size SxS    
    for k = 1 : num_of_comps
        %P(=density function):
            switch k
                case 1
                    P = mosaic_density_1;
                case 2
                    P = mosaic_density_2;
                case 3
                    P = mosaic_density_3;
                case 4
                    P = mosaic_density_4;
            end
            P_v = P(:);
        %density -> sample generation(positions):    
            Ind = discrete_nonuniform_sampling(P_v,num_of_samples,1);
            [x,y] = ind2sub([S,S],Ind); %x,y: row vectors 
        %samples from the kth subspace:
            e(2*k-1,:) = x;
            e(2*k,:) = y;
        end
    de = 2 * ones(num_of_comps,1);
end

%--------------------
function [A] = mosaic_density_1()
c = linspace(0.1,1,50);
A0 = zeros(50);
for k = 1:7:50-1
    A0(k:end,k:end) = ones(50-k+1,50-k+1) * c(k);
end
A = -invariance_to_90_degree_rotation(rot90(rot90(A0)));
A = image2density(A);
%---------------------
function [A] = mosaic_density_2()
A0 = ones(50);
A0_corner =  triu(ones(20)); s1 = size(A0_corner,1); s2 = size(A0_corner,2);
A0(1:s1,end-s2+1:end) = 0.8*A0_corner;
A0([1:s1]+s1/2,[end-s2+1:end]-s2/2) = 0.7*A0_corner;
A0([1:s1]+s1,[end-s2+1:end]-s2) = 0.4*A0_corner;
A0([1:s1]+3*s1/2,[end-s2+1:end]-3*s2/2) = 0.2*A0_corner;
A = invariance_to_90_degree_rotation(A0);
A = image2density(A);
%---------------------
function [A] = mosaic_density_3()
A0 = zeros(50,50);
A0_corner =  1-rot90(tril(ones(20)));
A0(1:20,1:20) = A0_corner;
A0([1:20]+5,[1:20]+5) = 0.9*A0_corner;
A0([1:20]+10,[1:20]+10) = 0.8*A0_corner;
A0([1:20]+15,[1:20]+15) = 0.7*A0_corner;
A0([1:20]+20,[1:20]+20) = 0.6*A0_corner;
A0([1:20]+25,[1:20]+25) = 0.5*A0_corner;
A0([1:20]+30,[1:20]+30) = 0.4*A0_corner;
A = -invariance_to_90_degree_rotation(A0);
A = image2density(A);
%---------------------
function [A] = mosaic_density_4()

A0 = zeros(50);
C = ones(15);

A0([11:25],[1:15]) = C;
A0([11:25]+5,[1:15]+5) = 0.8*C;
A0([11:25]+10,[1:15]+10) = 0.65*C;
A0([11:25]+15,[1:15]+15) = 0.5*C;
A0([11:25]+20,[1:15]+20) = 0.35*C;
A0([11:25]+25,[1:15]+25) = 0.2*C;

A0([11:25]+15,[36:50]) = C;
A0([11:25]+10,[36:50]-5) = 0.8*C;
A0([11:25]+5,[36:50]-10) = 0.65*C;
A0([11:25]+0,[36:50]-15) = 0.5*C;
A0([11:25]-5,[36:50]-20) = 0.35*C;
A0([11:25]-10,[36:50]-25) = 0.2*C;
A = invariance_to_90_degree_rotation(A0);
A = image2density(A);
%---------------------
function [A] = invariance_to_90_degree_rotation(A0)
A1 = rot90(A0);
A2 = rot90(A1);
A3 = rot90(A2);
A = [A1,A0;A2,A3];
%---------------------
function [P] = image2density(I)
P = I - min(min(I));
P = P / sum(sum(P));
