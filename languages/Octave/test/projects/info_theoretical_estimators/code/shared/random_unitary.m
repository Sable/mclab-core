function [U] = random_unitary(D)
%function [U] = random_unitary(D)
%Generates a DxD random(=uniformly distributed according to the Haar measure) unitary (U).
%
%REFERENCE:
%   Alan Edelman, N. Raj Rao. Random matrix theory. Acta Numerica (2005), pp. 1-65.

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

X = (randn(D) + i*randn(D))/sqrt(2);
[Q,R] = qr(X);
U = Q * diag(sign(diag(R)));
