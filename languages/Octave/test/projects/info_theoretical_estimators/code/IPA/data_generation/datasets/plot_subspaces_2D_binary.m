function [h]  = plot_subspaces_2D_binary(s)
%function [h]  = plot_subspaces_2D_binary(s)
%Plots signal s having 2-dimensional components.
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
    d = floor(size(s,1)/2);
    yd = ceil(sqrt(d));
    xd = ceil(d/yd);
%plot:
    for k = 1 : d
       subplot(xd,yd,k);
       plot_subspaces_2D_binary_one_comp(s(2*k-1,:),s(2*k,:));
    end

%---------------------------------------------------
function [] = plot_subspaces_2D_binary_one_comp(x_coo,y_coo)
%Plots a single 2D component.

h = plot(x_coo,y_coo,'k*','MarkerSize',2); 
axis equal; 
axis ij;
axis([min(x_coo), max(x_coo), min(y_coo), max(y_coo)]);
grid off;
axis off;
set(gca,'XTickMode','manual','XTick',[], ...
        'YTickMode','manual','YTick',[]);
