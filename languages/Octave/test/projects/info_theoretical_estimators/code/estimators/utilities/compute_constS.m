function [cS] = compute_constS(co_alpha,co_k,d,num_of_samples)
%function [cS] = compute_constS(co_alpha,co_k,d,num_of_samples)
%
%Compute the normalizer constant to the generalized kNN ({1,...,k}, S) based Renyi entropy estimators; see 'HRenyi_kNN_1tok_estimation.m', 'HRenyi_kNN_S_estimation.m'.
%
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

cS = 0;
for k = co_k
    cS = cS + gamma(k+1-co_alpha) / gamma(k);
end
V = volume_of_the_unit_ball(d);
cS = ((num_of_samples-1) / num_of_samples * V)^(co_alpha-1) * cS;
