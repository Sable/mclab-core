function [B] = expF_Bregman_distance(distr,np1,np2)
%function [B] = expF_Bregman_distance(distr,np1,np2)
%Computes the Bregman distance (B) of the natural parameters np1 and np2. The Bregman distance is defined by the log-normalizer of the given exponential family (distr).

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

diff_nps = expF_np1_np2_subtract(np1,np2); %theta1 - theta2
B = expF_F(distr,np1) - expF_F(distr,np2) - expF_np1_np2_mult(diff_nps,expF_gradF(distr,np2)); %F(theta1) - F(theta2) - <theta1 - theta2, nabla F(theta2)>


