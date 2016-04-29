function [h] = plot_subspaces_2D_trajectory(s)
%function [h] = plot_subspaces_2D_trajectory(s)
%Plots the trajectory of signal s, composed of 2-dimensional components.  
%
%INPUT:
%   s(:,t): t^th sample point
%OUTPUT:
%   h: handle of the figure.

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
    [num_of_comps,num_of_samples] = size(s);
    num_of_comps = num_of_comps / 2;

h = figure;    
for k = 1 : num_of_comps
    subplot(1,num_of_comps,k);
    plot(s(2*k-1,:),s(2*k,:));
    axis off;
end
    