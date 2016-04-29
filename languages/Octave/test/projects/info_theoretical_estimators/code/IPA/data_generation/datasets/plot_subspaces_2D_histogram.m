function [h] = plot_subspaces_2D_histogram(s,S)
%function [h] = plot_subspaces_2D_histogram(s,S)
%Plots the histogram of signal s having 2-dimensional components. The
%density of the components are of taken (scaled) to be of size SxS.
% 
%INPUT:
%   s: s(:,t) is the t^th sample.
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
    num_of_comps = size(s,1)/2;
    normalization_needed = 1;
    
%plot:    
    h = figure;
    for k = 1 : num_of_comps
        %samples from the k^th subspace
            x = s(2*k-1,:);
            y = s(2*k,:);
        if normalization_needed %data points -> [1,S]^2:
                    %min_x,min_y,max_x,max_y:
                        [m,I_min] = min([x;y]');
                        [m,I_max] = max([x;y]');
                        x_min = x(I_min(1));
                        y_min = y(I_min(2));
                        x_max = x(I_max(1));
                        y_max = y(I_max(2));
                    %translation of the bottom-left data point of the point cloud to (0,0), and scaling to size S*S:
                        x = (x - x_min) / (x_max - x_min) * (S-1) + 1;
                        y = (y - y_min) / (y_max - y_min) * (S-1) + 1;
                %rounding:
                    x = round(x);
                    y = round(y);
        end
        %histogram:
           Ind = sub2ind([S,S],x,y);
           H = hist(Ind,[1:S^2]);
           P_hat = reshape(H,S,S);
        %plot:    
           subplot(1,num_of_comps,k);%to a single row
           imshow(P_hat,[]);    
    end
