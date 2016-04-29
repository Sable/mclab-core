function [h] = plot_subspaces(s,data_type,fig_name)
%function [h] = plot_subspaces(s,data_type,fig_name)
%Plots signal s of given data type (data_type) and sets the name of the resulting figure (fig_name).
%
%INPUT:
%   s: s(:,t) is the t^th sample.
%   data_type: see 'sample_subspaces.m'
%OUTPUT:
%   h: handle of the figure (NaN means: no plot performed).
%EXAMPLE:
%   [h] = plot_subspaces(s,'3D-geom','hidden subspaces');

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

if strcmp(class(data_type),'char')
    switch data_type
        case {'ABC','GreekABC','Aw','multi2-geom','IFS','multi2-2-geom','multi2-2-2-geom','multi2-2-2-2-geom'}
            h = plot_subspaces_2D_binary(s); 
        case 'mosaic'
            S = 100;
            h = plot_subspaces_2D_histogram(s,S);
        case {'3D-geom','multi3-geom','multi3-3-geom','multi3-3-3-geom','multi3-3-3-3-geom'}
            h = plot_subspaces_3D_binary(s);
        case 'ikeda'
            h = plot_subspaces_2D_trajectory(s);
        case 'lorenz'
            h = plot_subspaces_3D_trajectory(s);
        otherwise
            disp('There is no drawing procedure associated to this data type.');        
            h = NaN;
    end
else
    h = NaN;
end

%set the name of the figure:
    if ~isnan(h)
        set(h,'Name',fig_name);
    end
