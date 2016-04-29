function [H] = HTsallis_kNN_k_estimation(Y,co)
%function [H] = HTsallis_kNN_k_estimation(Y,co)
%Estimates the Tsallis entropy (H) of Y using the kNN method (S={k}).
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Nikolai Leonenko, Luc Pronzato, and Vippal Savani. A class of Renyi information estimators for multidimensional densities. Annals of Statistics, 36(5):2153-2182, 2008.

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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

I_alpha = estimate_Ialpha(Y,co);
H = (1-I_alpha) / (co.alpha-1);
