function [Q] = random_orthogonal(D)
%function [Q] = random_orthogonal(D)
%Generates a DxD random orthogonal matrix (Q).
%
%The generation is based on following fact:
%Let A be a matrix whose elements are independently, normally distributed: A_ij~N(0,sigma^2).
%Let A=QR be the QR factorization of A normalized so that the diagonal elements of R are positive.
%Then Q is uniformly (Haar) distributed on the orthogonal group.

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

A = randn(D);
[Q,R] = qr(A);
Q = Q * diag(sign(diag(R)));

