function [e,de] = sample_subspaces_Aw(num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces_Aw(num_of_comps,num_of_samples)
%Sampling from the 'Aw' dataset; number of subspaces: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_Aw(50,1000);
%
%REFERENCE:
%   Zoltan Szabo and Andras Lorincz. Real and Complex Independent Subspace Analysis by Generalized Variance. ICA Research Network International Workshop (ICARN), pp. 85-88, 2006.

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

symbols = generate_symbols('Aw');
[e,de] = sample_subspaces_alphabet(symbols,num_of_comps,num_of_samples);
