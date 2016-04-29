function [H] = HShannon_expF_estimation(Y,co)
%function [H] = HShannon_expF_estimation(Y,co)
%Estimates the Shannon (H) of Y using maximum likelihood estimation (MLE) + analytical formula corresponding in the chosen exponential family.
%Assumption: k, the carrier measure is zero.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%    Frank Nielsen and Richard Nock. A closed-form expression for the Sharma-Mittal entropy of exponential families. Journal of Physics A: Mathematical and Theoretical, 45:032003, 2012. (analytical formula)

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

%MLE:
  np = expF_MLE(Y,co.distr);
  
term1 = expF_F(co.distr,np); %F(theta)
term2 = expF_np1_np2_mult(np,expF_gradF(co.distr,np)); %<theta,grad_F(theta)>
H =  term1 - term2; %assumption: k, the carrier measure is zero
