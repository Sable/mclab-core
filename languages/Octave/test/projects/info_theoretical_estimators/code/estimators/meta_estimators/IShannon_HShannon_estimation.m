function [I] = IShannon_HShannon_estimation(Y,ds,co)
%function [I] = IShannon_HShannon_estimation(Y,ds,co)
%Estimates Shannon mutual information (I) based on Shannon differential entropy. The estimation is carried out according to the relation: I(y^1,...,y^M) = \sum_{m=1}^MH(y^m)  - H([y^1,...,y^M]).
%
%Note:
%   1)We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%   2)This a meta method: the Shannon entropy estimator can be arbitrary.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE: 
%   Thomas M. Cover, Joy A. Thomas. Elements of Information Theory, John Wiley and Sons, New York, USA (1991).

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
    if sum(ds) ~= size(Y,1);
        error('The subspace dimensions are not compatible with Y.');
    end

%initialization:
    num_of_comps = length(ds);%number of subspaces=:M
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces

%I = - H([y^1,...,y^M]):
    I = -H_estimation(Y,co.member_co);
    
%I = I + \sum_{m=1}^MH(y^m):    
    for k = 1 : num_of_comps
        idx = [cum_ds(k) : cum_ds(k)+ds(k)-1];
        I = I + H_estimation(Y(idx,:),co.member_co);
    end

        
