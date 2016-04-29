function [h]  = plot_subspaces_3D_binary(s)
%function [h]  = plot_subspaces_3D_binary(s)
%Plots signal s having 3-dimensional components.
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

h = figure;
%number of subplots in x and y direction (xd,yd):
    d = floor(size(s,1)/3);
    xd = ceil(sqrt(d));
    yd = ceil(d/xd);
%plot:        
    for k = 1 : d
       subplot(xd,yd,k);
       plot_subspaces_3D_binary_one_comp(s(3*k-2,:),s(3*k-1,:),s(3*k,:));
    end

%---------------------------------------
function [] = plot_subspaces_3D_binary_one_comp(x_coo,y_coo,z_coo)
%Plots a single 3D component.

%plot3(x_coo,y_coo,z_coo,'k.','MarkerSize',1);%you could be interested in playing with MarkerSize
plot3(x_coo,y_coo,z_coo,'k.');

axis equal;
axis off;
set(gca,'XTickMode','manual','XTick',[], ...
        'YTickMode','manual','YTick',[],...
        'ZTickMode','manual','ZTick',[]);