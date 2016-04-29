function [H] = HTsallis_expF_estimation(Y,co)
%function [H] = HTsallis_expF_estimation(Y,co)
%Estimates the Tsallis entropy (H) of Y using maximum likelihood estimation (MLE) + analytical formula in the chosen exponential family.
%Assumptions: 
%   1)'alpha*np' belongs to the natural space. This holds, e.g., if the natural space is a convex *cone*.
%   2)k, the carrier measure is zero.
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
  
%F1,F2:  
  F1 = expF_F(co.distr,expF_np_mult(np,co.alpha)); %F(alpha*theta)
  F2 = co.alpha * expF_F(co.distr,np); %alpha*F(theta)
  
H = (exp(F1-F2) - 1) / (1 - co.alpha); %assumption: k, the carrier measure is zero
   
