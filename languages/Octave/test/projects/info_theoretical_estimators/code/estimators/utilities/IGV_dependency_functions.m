function [fs] = IGV_dependency_functions()
%function [fs] = IGV_dependency_functions()
%Creates functions for measuring f-covariance / f-correlation for the generalized variance (GV) measure.
%
%OUTPUT:
%   fs: cell array; Assumption: elements of fs can be applied by feval to a matrix (e.g.: functions operating coordinatewise).

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

f1 = 'cos';
f2 = @(t)cos(2*t); %Matlab-7
%f2 = inline('cos(2*t)','t'); %Matlab-6
    
fs = {f1,f2};
