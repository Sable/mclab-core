function [cost_after,S1,S2,S3,S4] = cost_pairadditive_wrt_subspaces_restricted(s,ip,iq,ds,co)
%function [cost_after,S1,S2,S3,S4] = cost_pairadditive_wrt_subspaces_restricted(s,ip,iq,ds,co)
%Returns the cost (cost_after) and the associated parts (S1,S2,S3,S4) in similarity matrix
%S, provided that the subspace indices of the change are ip and iq; the
%dimensions of the subspaces are given in ds, and the estimation is carried
%out using the cost object co.

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

num_of_comps = length(ds);

S1 = S_part(s,ds,[1:ip-1],ip,co);
S2 = S_part(s,ds,ip,[ip+1:iq-1,iq+1:num_of_comps],co);
S3 = S_part(s,ds,[1:iq-1],iq,co);
S4 = S_part(s,ds,iq,[iq+1:num_of_comps],co);
cost_after = sum(S1) + sum(S2) + sum(S3) + sum(S4);

%----------------------
function [Si]  = S_part(s,ds,ind1,ind2,co)
%Computes the mutual information of the subspaces whose indices are given
%in vectors ind1 and ind2, in signal s having ds dimensional subspaces. For the
%estimation the co cost object is used.

L1 = length(ind1);
L2 = length(ind2);

if (L1==0) | (L2==0)
    Si = 0;%no contribution
else
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,... = starting indices of the subspaces.
    %Exactly one of the ind<i> arguments is vector, the other one is a scalar:
        if L1>1
            ind2 = ones(1,L1) * ind2;  L2 = L1;
            Si = zeros(L1,1);
        else
            ind1 = ones(1,L2) * ind1; L1 = L2;        
            Si = zeros(1,L2);
        end
    %mutual information estimation of the (ind1,ind2)^th subspace pair:
        for k = 1 : L1 %=L2
            ds_pair = [ds(ind1(k));ds(ind2(k))];
            i1 = [cum_ds(ind1(k)):cum_ds(ind1(k))+ds(ind1(k))-1];%coordinates of the first subspace in the pair
            i2 = [cum_ds(ind2(k)):cum_ds(ind2(k))+ds(ind2(k))-1];%coordinates of the second subspace in the pair
            Si(k) = I_estimation(s([i1,i2],:),ds_pair,co);
        end
end