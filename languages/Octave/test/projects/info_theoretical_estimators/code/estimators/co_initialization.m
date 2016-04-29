function [co] = co_initialization(cost_type,cost_name,mult)
%function [co] = co_initialization(cost_type,cost_name,mult)
%Returns a cost object (co), i.e, an entropy/mutual information estimator.
%
%In case of an entropy based ISA formulation we will need an entropy estimator; 
%in case of a mutual information based ISA formulation we will need a mutual information estimator.
%
%INPUT:
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').

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

switch cost_type
    case {'I','sum-I','Irecursive','Ipairwise','Ipairwise1d'}
        co = I_initialization(cost_name,mult);
    case {'sumH'}
        co = H_initialization(cost_name,mult);
    otherwise
        error('cost type=?');
end