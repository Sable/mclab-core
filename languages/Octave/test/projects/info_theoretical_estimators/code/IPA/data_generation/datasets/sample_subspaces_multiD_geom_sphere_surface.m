function [e] = sample_subspaces_multiD_geom_sphere_surface(d,num_of_samples)
%function [e] = sample_subspaces_multiD_geom_sphere_surface(d,num_of_samples)
%Sampling from a random variable distributed uniformly on the surface of the d-dimensional unit sphere.
%
%INPUT:
%	num_of_samples: number of samples to be generated.
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%EXAMPLE:
%   e = sample_subspaces_multiD_geom_sphere_surface(4,1000);

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

%L_2-normalized N(0,I) samples:
    e = randn(d,num_of_samples);
    norm_e = sqrt(sum(e.^2,1));
    e = e ./ repmat(norm_e,d,1);
