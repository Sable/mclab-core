function [Dtemp4] = expF_Dtemp4(distr,np1,np2,a,b)
%function [Dtemp4] = expF_Dtemp4(distr,np1,np2,a,b)
%Computes Dtemp4 = \int [f_1(u)]^a [f_2(u)]^b du in the chosen exponential family (distr), where np1 and np2 are the natural parameters associated to f_1 and f_2, respectively.
%Assumption: a + b = 1 and 'a * np1 + b * np2' belongs to the natural space. This holds, for example, if the natural space is affine. 
%
%REFERENCE: 
%   Frank Nielsen and Richard Nock. On the chi square and higher-order chi distances for approximating f-divergences. IEEE Signal Processing Letters, 2:10–13, 2014.

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

%D ingredients:
    F0 = expF_F(distr,expF_np1_np2_add(expF_np_mult(np1,a), expF_np_mult(np2,b))); %F(a*np1 + b*np2)
    F1 = a * expF_F(distr,np1); %a*F(np1)
    F2 = b * expF_F(distr,np2); %b*F(np2)

Dtemp4 = exp(F0 - (F1+F2));
