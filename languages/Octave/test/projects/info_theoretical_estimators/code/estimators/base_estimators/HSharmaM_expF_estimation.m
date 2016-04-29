function [H] = HSharmaM_expF_estimation(Y,co)
%function [H] = HSharmaM_expF_estimation(Y,co)
%Estimates the Sharma-Mittal entropy (H) of Y using maximum likelihood estimation (MLE) + analytical formula associated to the chosen exponential family.
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
%    Frank Nielsen and Richard Nock. A closed-form expression for the Sharma-Mittal entropy of exponential families. Journal of Physics A: Mathematical and Theoretical, 45:032003, 2012. (analytical formulas for the Sharma-Mittal entropy in the exponential family)
%    Ethem Akturk, Baris Bagci, and Ramazan Sever. Is Sharma-Mittal entropy really a step beyond Tsallis and Renyi entropies? Technical report, 2007. http://arxiv.org/abs/cond-mat/0703277. (Sharma-Mittal entropy)
%    Bhudev D. Sharma and Dharam P. Mittal. New nonadditive measures of inaccuracy. Journal of Mathematical Sciences, 10:122-133, 1975. (Sharma-Mittal entropy)

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
  
%I_alpha (analytical formula):  
  F1 = expF_F(co.distr,expF_np_mult(np,co.alpha)); %F(alpha*np); assumption: 'alpha*np' belongs to the natural space. This holds, for example, if the natural space is a convex *cone*.
  F2 = co.alpha * expF_F(co.distr,np); %alpha*F(np)
  I_alpha = exp(F1-F2); %assumption: k, the carrier measure is zero
    
H = ( I_alpha^( (1-co.beta)/(1-co.alpha) )  - 1 )/ (1 - co.beta);
