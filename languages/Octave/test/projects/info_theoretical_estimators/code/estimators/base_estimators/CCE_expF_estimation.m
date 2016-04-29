function [CE] = CCE_expF_estimation(Y1,Y2,co)
%function [CE] = CCE_expF_estimation(Y1,Y2,co)
%Estimates the cross-entropy (CE) of Y1 and Y2 using maximum likelihood estimation (MLE) + analytical formula associated to the chosen exponential family.
%Assumption: k, the carrier measure is zero.
%
%We use the naming convention 'C<name>_estimation' to ease embedding new cross quantity estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: cross quantity estimator object.
%
%REFERENCE: 
%    Frank Nielsen and Richard Nock. Entropies and cross-entropies of exponential families. In IEEE International Conference on Image Processing (ICIP), pages 3621–3624, 2010.

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

%MLE:
  np1 = expF_MLE(Y1,co.distr);
  np2 = expF_MLE(Y2,co.distr);

%the two terms of CE:  
  term1 = expF_F(co.distr,np2); %F(np2)
  term2 = expF_np1_np2_mult(np2,expF_gradF(co.distr,np1)); %<np2,nabla F(np1)>
  
CE = term1 - term2;

