function [co] = IShannon_HShannon_initialization(mult,post_init)
%function [co] = IShannon_HShannon_initialization(mult)
%function [co] = IShannon_HShannon_initialization(mult,post_init)
%Initialization of the "meta" Shannon mutual information estimator based on Shannon differential entropy.
%Mutual information is estimated using the relation: (*) I(y^1,...,y^M) = \sum_{m=1}^MH(y^m)  - H([y^1,...,y^M]).
%
%Note:
%   1)The estimator is treated as a cost object (co). 
%   2)We use the naming convention 'I<name>_initialization' to ease embedding new mutual information estimation methods.
%   3)This is a meta method: the Shannon entropy estimator can be arbitrary.
%   4)Shannon Mutual information is also known in the literature as the special case of total correlation [1] or multi-information [2] when the number of subspaces is M=2.
%
%INPUT:
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').
%   post_init: {field_name1,field_value1,field_name2,field_value2,...}; cell array containing the names and the values of the cost object fields that are to be used
%   (instead of their default values). For further details, see 'post_initialization.m'.
%OUTPUT:
%   co: cost object (structure).
%REFERENCE:
%   [1] Satori Watanabe. Information theoretical analysis of multivariate correlation. IBM Journal of Research and Development, 4:66-82, 1960.
%   [2] Milan Studeny and Jirina Vejnarova. The multiinformation function as a tool for measuring stochastic dependence. In Learning in Graphical Models, pages 261-296, 1998.

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
    co.name = 'Shannon_HShannon';
    co.mult = mult;
    
%other fields:
    co.member_name = 'Shannon_kNN_k'; %you can change it to any Shannon entropy estimator capable of 'multiplicative 
	%factor correct' H estimation (mult=1) below. Note: Renyi entropy (H_{R,alpha}) also gives in limit the Shannon entropy (H): H_{R,alpha} -> H, as alpha -> 1.

%post initialization (put it _before_ initialization of the members in case of a meta estimator):    
    if nargin==2 %there are given (name,value) cost object fields
        co = post_initialization(co,post_init);
    end  
    
%initialization of the member(s):
    co.member_co = H_initialization(co.member_name,1);%'1': since we use the relation '(*)' (multiplicative factors in entropy estimations are NOT allowed).
   
    
