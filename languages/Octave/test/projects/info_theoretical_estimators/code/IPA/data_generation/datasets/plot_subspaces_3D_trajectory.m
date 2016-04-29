function [h] = plot_subspaces_3D_trajectory(s)
%function [h] = plot_subspaces_3D_trajectory(s)
%Plots the trajectory of signal s, composed of 3D components.  
%
%INPUT:
%   s(:,t): t^th sample point.
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

%parameter:
    plot_method = 1;%'1':with gray transitions; nice, but can be slow (in case of large sample numbers); '2':ugly, but fast

%initialization:
    [D,num_of_samples] = size(s);
    num_of_comps = D / 3;%3D subspaces
    
h = figure;    
%sets the graphics toolkit to 'fltk' for the current figure; in Octave:
    if ~working_environment_Matlab%Octave
        gt = get(h,'__graphics_toolkit__');
        if ~strcmp(gt,'fltk')
            graphics_toolkit('fltk');  close; h=figure;
            gt_changed = 1;%graphics toolkit changed
        end
    end
    
for k = 1 : num_of_comps
    subplot(1,num_of_comps,k);
    if plot_method == 1
        disp('Plotting via clinep; it may take for a while...');
        clinep(s(3*k-2,:),s(3*k-1,:),s(3*k,:),[1:num_of_samples]/num_of_samples,1); 
        colormap(gray);
    else%=2
        plot3(s(3*k-2,:),s(3*k-1,:),s(3*k,:)); 
    end
    axis off;
end
    
if ~working_environment_Matlab && gt_changed
    graphics_toolkit(gt);
end