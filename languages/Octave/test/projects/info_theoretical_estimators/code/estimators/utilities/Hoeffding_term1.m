function [term1] = Hoeffding_term1(U)
%function [term1] = Hoeffding_term1(U)
%Estimates the first term (term1) in Hoeffding's Phi based on the copula transformation (U).
%
%INPUT:
%   U: U(:,t) is the t^th sample.
%NOTE:
%  For problems with high number of samples, this computation can be somewhat slow + memory consuming; acceleration possibility: compile 'Hoeffding_term1.cpp'.

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

[d,num_of_samples] = size(U);

cumsumMi = 1 - bsxfun(@max,U(1,:).',U(1,:));
for i = 2 : d
    cumsumMi = cumsumMi .* (1 - bsxfun(@max,U(i,:).',U(i,:)));
end

term1 = sum(sum(cumsumMi)) / num_of_samples^2;

