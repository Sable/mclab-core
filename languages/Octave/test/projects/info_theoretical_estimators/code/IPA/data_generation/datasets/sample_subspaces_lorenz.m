function  [e,de] = sample_subspaces_lorenz(num_of_comps,num_of_samples)
%function  [e,de] = sample_subspaces_lorenz(num_of_comps,num_of_samples)
%Sampling from the 'lorenz' dataset; number of subspaces: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_lorenz(3,1000);
%
%REFERENCE: 
%   Zoltan Szabo, Barnabas Poczos, and Andras Lorincz. Auto-Regressive Independent Process Analysis without Combinatorial Efforts. Pattern Analysis and Applications, 13:1-13, 2010. 

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

t = linspace(0,50,num_of_samples);
max_num_of_comps = 3;

if num_of_comps > max_num_of_comps 
    error(strcat('The number of components must be <=',num2str(max_num_of_comps),'.'));
else
    %query for the current working environment:
        environment_Matlab = working_environment_Matlab;
    e = zeros(3*num_of_comps,num_of_samples);%preallocation
    for k = 1 : num_of_comps
        %set the parameters of the Lorenz's differential equation:
            switch k
                case 1
                    e0 = [-1;3;4];%initial point
                    a = 10; b = 27; c = 8/3;
                case 2
                    e0 = [5;2;-4];
                    a = 5; b = 30; c = 5/3;
                case 3
                    e0 = [0.1;-0.3;2];
                    a = 8; b = 35; c = 18/3;
            end
        %e_temp:
            if environment_Matlab%Matlab
                [t,e_temp] = ode45(@lorenz,t,e0,[],a,b,c);
            else%Octave
                [t,e_temp] = ode45(@lorenz,t,e0,a,b,c);%odepkg
            end
            e_temp = e_temp.';
        e(3*k-2:3*k,:) = e_temp;
    end
    de = ones(num_of_comps,1) * 3;
end

%-------------------
function edot = lorenz(t,e,a,b,c)
edot = [a * (e(2)-e(1));...
        e(1) * (b-e(3)) - e(2);...
        e(1) * e(2) - c * e(3)]; 
