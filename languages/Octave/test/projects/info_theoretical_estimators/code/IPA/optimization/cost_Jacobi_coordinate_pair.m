function [cost] = cost_Jacobi_coordinate_pair(Y,co,cost_type)
%function [cost] = cost_Jacobi_coordinate_pair(Y,co,cost_type)
%Estimates the ICA cost (cost) of signal Y (2xnumber of samples) for a given cost object (co) and cost type ('I' or 'sumH').
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.

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

%verification:
    if size(Y,1)~=2;
        error('The size of Y is not correct, it should be: 2 x number of samples.');
    end

switch cost_type
    case 'sumH'
        H1 = H_estimation(Y(1,:),co);
        H2 = H_estimation(Y(2,:),co);
        cost = H1 + H2;
    case 'I'
        cost = I_estimation(Y,[1;1],co);
    otherwise
        error('cost type=?');
end
