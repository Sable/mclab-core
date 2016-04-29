function [L] = compute_length_HRenyi_MST(Y,co)
%function [L] = compute_length_HRenyi_MST(Y,co)
%Computes the length (L) associated to the 'Renyi_MST' Renyi entropy estimator.
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
gam = d * (1-co.alpha);
W = sqdistance(Y).^(gam/2); %[ (||Y(:,i)-Y(:,j)||_2).^gam ]

%W->L:
    L = compute_MST(W,co.MSTmethod);
