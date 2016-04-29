function [D] = Df_DChiSquare_estimation(Y1,Y2,co)
%function [D] = Df_DChiSquare_estimation(Y1,Y2,co)
%Estimates the f-divergence (D) of Y1 and Y2 using second-order Taylor expansion of f and Pearson chi square divergence.
%
%Note:
%  1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%  2)This is a meta method: the Pearson chi square divergence estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%    Frank Nielsen and Richard Nock. On the chi square and higher-order chi distances for approximating f-divergences. IEEE Signal Processing Letters, 2:10-13, 2014.
%    Neil S. Barnett, Pietro Cerone, Sever Silvestru Dragomir, and A. Sofo. Approximating Csiszar f-divergence by the use of Taylor's formula with integral remainder. Mathematical Inequalities and Applications, 5:417-432, 2002.

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

%verification:
    if size(Y1,1) ~= size(Y2,1)
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    
D = co.H/2 * D_estimation(Y1,Y2,co.member_co); 

