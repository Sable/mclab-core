function [L] = compute_length_HRenyi_kNN_S(Y,co)
%function [L] = compute_length_HRenyi_kNN_S(Y,co)
%Computes the length (L) associated to the 'Renyi_kNN_S' Renyi entropy estimator.
%
%INPUT:
%    Y: Y(:,t) is the t^th sample.
%   co: cost object.

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

d = size(Y,1);
squared_distances = kNN_squared_distances(Y,Y,co,1);
gam = d * (1-co.alpha);

L = sum(sum(replace_Infs_with_max(squared_distances(co.k,:).^(gam/2)),1)); %'gam/2' <= squared; S=co.k
%Note: if "squared_distances(co.k,:).^(gam/2)" contains Inf elements (this may accidentally happen in small
%dimensions in case of large sample number, e.g., for d=1, T=10000), then the Inf-s are replaced with
%the maximal, non-Inf element.
