function [D] = DSharmaM_expF_estimation(Y1,Y2,co)
%function [D] = DSharmaM_expF_estimation(Y1,Y2,co)
%Estimates the Sharma-Mittal divergence (D) of Y1 and Y2 using maximum likelihood estimation (MLE) + analytical formula associated to the chosen exponential family. 
%Assumption: 'alpha * np1 + (1-alpha) * np2' belongs to the natural space [<== convexity of the natural space, if alpha \in (0,1)] 
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Frank Nielsen and Richard Nock. A closed-form expression for the Sharma-Mittal entropy of exponential families. Journal of Physics A: Mathematical and Theoretical, 45:032003, 2012. (analytical formula for the Sharma-Mittal divergence in the exponential family)
%   Marco Massi. A step beyond Tsallis and Renyi entropies. Physics Letters A, 338:217-224, 2005. (Sharma-Mittal divergence definition)

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

%J (Jensen difference divergence), C:
    %a,b,distr:
        a = co.alpha;
        b = co.beta;
        distr = co.distr;

    J1 = a * expF_F(distr,np1) + (1-a) * expF_F(distr,np2); %alpha * F(np1) + (1 - alpha) * F(np2);
    term1 = expF_np_mult(np1,a); %alpha * np1
    term2 = expF_np_mult(np2,1-a); %(1-alpha) * np2
    J2 = expF_F(distr,expF_np1_np2_add(term1, term2)); %F( alpha * np1 + (1-alpha) * np2 ); sufficient: 'alpha * np1 + (1-alpha) * np2' belongs to the natural space [<== convexity of the natural space, if alpha \in (0,1)] 
    J = J1 - J2;
    C = exp(-J);
    
%D:
    D = (C^((1-b)/(1-a)) - 1) / (b-1);
