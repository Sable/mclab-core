function [I] = IGV_estimation(Y,ds,co)
%function [I] = IGV_estimation(Y,ds,co)
%Estimates the generalized variance (I). 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)). 
%  co: mutual information estimator object.
%
%REFERENCE: 
%   Zoltan Szabo and Andras Lorincz. Real and Complex Independent Subspace Analysis by Generalized Variance. ICA Research Network International Workshop (ICARN), pages 85-88, 2006.

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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if sum(ds) ~= size(Y,1);
        error('The subspace dimensions are not compatible with Y.');
    end
    if ~one_dimensional_problem(ds) || length(ds)~=2
        error('There must be 2 pieces of one-dimensional subspaces (coordinates) for this estimator.');
    end

%initialization:
    fs = IGV_dependency_functions;
    num_of_functions = length(fs);
    num_of_samples = size(Y,2);
    I = 0;

%query for the current working environment:
    environment_Matlab = working_environment_Matlab;
        
%I computation:
    for k = 1 : num_of_functions %pick the k^th function (fs{k})
        fY = feval(fs{k},Y).';
        if strcmp(co.dependency,'cov')
            c = (fY(:,1)-mean(fY(:,1))).' * (fY(:,2)-mean(fY(:,2))) / (num_of_samples-1);
            I = I + c.^2; %cov instead of corr
        else %corr/cor
            if environment_Matlab%Matlab
                I = I + (corr(fY(:,1),fY(:,2))).^2; %corr instead of cov        
            else%Octave
                I = I + (cor(fY(:,1),fY(:,2))).^2; %cor (and not 'corr') instead of cov        
            end
        end
    end    

