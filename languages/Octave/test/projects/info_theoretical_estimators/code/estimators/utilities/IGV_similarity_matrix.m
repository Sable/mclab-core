function [C] = IGV_similarity_matrix(Y,cost_parameters)
%function [C] = IGV_similarity_matrix(Y,cost_parameters)
%Creates the similarity matrix (C) of signal Y based on f-covariances/f-correlations, 
%i.e, generalized variances (GV).
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%   cost_parameters.dependency: 'cov' (f-covariance) or 'corr' (f-correlation).

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
    fs = IGV_dependency_functions;
    num_of_functions = length(fs);
    C =  zeros(size(Y,1));
    %query for the current working environment:
        environment_Matlab = working_environment_Matlab;
        
for k = 1 : num_of_functions %pick the k^th function (fs{k})
    fY = feval(fs{k},Y).';
    if strcmp(cost_parameters.dependency,'cov')
        C = C + (cov(fY)).^2; %cov instead of corr
    else %corr/cor
        if environment_Matlab%Matlab
            C = C + (corr(fY)).^2; %corr instead of cov        
        else%Octave
            C = C + (cor(fY)).^2; %cor (and not 'corr') instead of cov        
        end
    end
end
