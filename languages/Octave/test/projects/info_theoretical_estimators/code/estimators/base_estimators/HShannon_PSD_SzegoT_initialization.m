function [co] = HShannon_PSD_SzegoT_initialization(mult,post_init)
%function [co] = HShannon_PSD_SzegoT_initialization(mult)
%function [co] = HShannon_PSD_SzegoT_initialization(mult,post_init)
%Initialization of power spectral density representation and Szego's theorem based Shannon differential entropy (H) estimator.
%
%Note:
%   1)The estimator is treated as a cost object (co).
%   2)We use the naming convention 'H<name>_initialization' to ease embedding new entropy estimation methods.
%
%INPUT:
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').
%OUTPUT:
%   co: cost object (structure).

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

%mandatory fields (following the template structure of the estimators to make uniform usage of the estimators possible):
    co.name = 'Shannon_PSD_SzegoT';
    co.mult = mult;  
    
%other fields:    
    co.p_max = 10; %maximal AR order examined
    co.K = 10 * co.p_max; %size of the Toeplitz matrix (KxK)
            
%post initialization (put it _before_ initialization of the members in case of a meta estimator):    
    if nargin==2 %there are given (name,value) cost object fields
        co = post_initialization(co,post_init);
    end  