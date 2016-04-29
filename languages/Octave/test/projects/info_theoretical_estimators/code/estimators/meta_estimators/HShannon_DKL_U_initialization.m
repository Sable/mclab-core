function [co] = HShannon_DKL_U_initialization(mult,post_init)
%function [co] = HShannon_DKL_U_initialization(mult)
%function [co] = HShannon_DKL_U_initialization(mult,post_init)
%Initialization of the "meta" Shannon entropy estimator defined according to the relation H(Y) = -D(Y',U) + log(\prod_i(b_i-a_i)), where y\in[a,b] = \times_{i=1}^d[a_i,b_i], D is the Kullback-Leibler divergence, Y' = linearly transformed version of Y to [0,1]^d, and U is the uniform distribution on [0,1]^d.
%
%Note:
%   1)The estimator is treated as a cost object (co).
%   2)We use the naming convention 'H<name>_initialization' to ease embedding new entropy estimation methods.
%   3)This is a meta method: the Kullback-Leibler divergence estimator can be arbitrary.
%
%INPUT:
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').
%   post_init: {field_name1,field_value1,field_name2,field_value2,...}; cell array containing the names and the values of the cost object fields that are to be used
%   (instead of their default values). For further details, see 'post_initialization.m'.
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
    co.name = 'Shannon_DKL_U';
    co.mult = mult;
    
%other fields:
    co.member_name = 'KL_kNN_k'; %you can change it to any Kullback-Leibler divergence estimator

%post initialization (put it _before_ initialization of the members in case of a meta estimator):    
    if nargin==2 %there are given (name,value) cost object fields
        co = post_initialization(co,post_init);
    end  
    
%initialization of the member(s):
    co.member_co = D_initialization(co.member_name,mult);
