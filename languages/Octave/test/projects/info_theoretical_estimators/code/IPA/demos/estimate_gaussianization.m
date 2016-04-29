function [s_transformed] = estimate_gaussianization(s,gaussianization)
%function [s_transformed] = estimate_gaussianization(s,gaussianization)
%Gaussianization of the variable s using rank technique.
%
%INPUT:
%   s(:,t): variable at time t.
%   gaussianization.method: gaussianization method. Possibilities: 'rank'.
%OUTPUT:
%   s_transformed(:,t): variable at time t, transformed variant of s.
%
%REFERENCE:
%   'rank': Andreas Ziehe, Motoaki Kawanabe, Stefan Harmeling, Klaus-Robert Muller. Blind separation of postnonlinear mixtures using linearizing transformations and temporal decorrelation. Journal of Machine Learning Research 4 (2003) 1319-1338.

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

method = gaussianization.method;

%initialize method-specific parameters:
    switch method
        case 'rank'
            c = gaussianization.c;
        otherwise
            error('Gaussianization method=?');
    end

%initialization:
    [D,num_of_samples] = size(s);
    s_transformed = zeros(D,num_of_samples); %size(s)
    
disp('Gaussianization: started.');

for k = 1 : D%gaussianize every row of s separately
    %r:=rank(s_k):
        [s2,I] = sort(s(k,:)); [s3,r] = sort(I);
        s_transformed(k,:) = norminv(c*r/(num_of_samples+1)+(1-c)/2,0,1);
end

disp('Gaussianization: ready.');
