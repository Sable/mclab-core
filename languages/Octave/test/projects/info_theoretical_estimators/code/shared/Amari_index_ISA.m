function [amari_ind] = Amari_index_ISA(G,ds,weighting_scheme,p)
%function [amari_ind] = Amari_index_ISA(G,ds,weighting_scheme,p)
%Measures the ds x ds block-permutation (also called block-scaling)
%property of the square matrix G, using p-norm. The performance measure is normalized to 
%[0,1]: 0 - best (perfect block-permutation matrix), 1 - worst case (g^{ij}-s are equal, i.e., their value do not depend on i and j).
%
%INPUT:
%   weighting_scheme: 
%      1)'uniform' or 'subspace-dim-proportional' (see matrix V below). 
%      2)If all the subspace dimensions are equal [ds(1)=ds(2)=...=ds(end)], then the 2 schemes give the same result.
%   ds: ISA subspace dimensions. Its elements are assumed to be in increasing order:  ds(1)<=ds(2)<=...
%EXAMPLE:
%   amari_ind = Amari_index_ISA([0,2;-4,0],[1,1],'uniform',2); traditional Amari-index; result:0.
%   amari_ind = Amari_index_ISA(blkdiag(rand(2),rand(3),rand(3)),[2,3,3],'uniform',2); result:0 (block-diagonal matrix).
%   amari_ind = Amari_index_ISA(blkdiag(rand(2),[zeros(3),rand(3);rand(3),zeros(3)]),[2,3,3],'uniform',2); result:0. (block-permutation matrix)
%   amari_ind = Amari_index_ISA(blkdiag(rand(2),[zeros(3),rand(3);rand(3),zeros(3)]),[2,3,3],'subspace-dim-proportional',2); result:0. (block-permutation matrix)
%   amari_ind = Amari_index_ISA([4 -2 2; 2 1 1; 2 1 1],[1,2],'uniform',1); %result:1.
%   amari_ind = Amari_index_ISA([5 -3 4; 3 5/2 5/2; 4 5/2 5/2],[1,2],'uniform',2); %result:1.
%REFERENCE:
%  	Zoltan Szabo, Barnabas Poczos: Nonparametric Independent Process Analysis. EUSIPCO-2011, pages 1718-1722. (general d_m-dimensional subspaces, ISA).
%   Shun-ichi Amari, Andrzej Cichocki, and Howard H. Yang. A new learning algorithm for blind signal separation. NIPS '96, pages 757�763. (one-dimensional case, i.e., ICA).

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

ds = ds(:);%column vector
G_shrinked = sum_blocks(G,ds,p);
%V (V_{ij}>0; Amari(V)=Amari(positive_const*V); the resulting performance is invariant to positive scaling factors):
    switch weighting_scheme
        case 'uniform'
            V = ones(length(ds));%V_{ij}=1
        case 'subspace-dim-proportional'
            V = 1./(ds*ds.'); %V_{ij} = 1 / (ds(i)*ds(j))
        otherwise 
            error('weighting scheme=?');
    end
G_shrinked_weighted = G_shrinked .* V; %G_shrinked_weighted=[g^{ij}]
amari_ind = Amari_index(G_shrinked_weighted);

%---------------------
function [G_shrinked] = sum_blocks(G,ds,p)
%Shrinks the ds(i) x ds(j) sized submatrices of the square matrix G (G^{ij}) and computes the
%p-norm of G^{ij}(:) =: G_shrinked(i,j).
%
%INPUT:
%	G: square matrix of size sum(ds) x sum(ds).
%  ds: subspace dimensions.
%   p: positive.

num_of_comps = length(ds);
G_shrinked = zeros(num_of_comps);
cds = cumsum(ds);
cds = [0;cds(:)];

for m1 = 1 : num_of_comps
    for m2 = 1 : num_of_comps
        block = G(cds(m1)+1:cds(m1+1),cds(m2)+1:cds(m2+1));
        G_shrinked(m1,m2) = sum(abs(block(:)).^p)^(1/p);%=||block(:)||_p
    end
end
%----------------------
function [amari_ind] = Amari_index(G)
%Measures the permutation (also called scaling) property of the square
%matrix G. The performance measure is normalized to [0,1]: 0 - best (perfect permutation matrix), 1 - worst case.

G = abs(G);
num_of_coordinates = size(G,1); %;number of hidden elements.
v = [sum(G')./max(G'), sum(G)./max(G)] - 1; %dim(v)=2*num_of_coordinates
amari_ind = mean(v) / (num_of_coordinates-1); 