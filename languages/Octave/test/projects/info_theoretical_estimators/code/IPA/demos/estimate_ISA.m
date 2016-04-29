function [e_hat,W_hat,de_hat] = estimate_ISA(x,ICA,ISA,unknown_dimensions,de,dim_reduction)
%function [e_hat,W_hat,de_hat] = estimate_ISA(x,ICA,ISA,unknown_dimensions,de,dim_reduction)
%Estimates the ISA model. Method: ICA + clustering of the ICA elements (=ISA separation theorem).
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   ICA: solver for independent component analysis, see 'estimate_ICA.m'.
%   ISA: solver for independent subspace analysis (=clustering of the ICA elements). ISA.cost_type, ISA.cost_name, ISA.opt_type: cost type, cost name, optimization type. Example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok', ISA.opt_type = 'greedy' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are estimated Renyi entropies via kNN methods ('Renyi_kNN_1tok') and the optimization is greedy; see also 'demo_ISA.m'
%   unknown_dimensions: '0' means 'the subspace dimensions are known'; '1' means 'the number of the subspaces are known' (but the individual dimensions are unknown).
%   de: 
%       1)in case of 'unknown_dimensions = 0': 'de' contains the subspace dimensions.
%       2)in case of 'unknown_dimensions = 1': the length of 'de' must be equal to the number of subspaces, but the coordinates of the vector can be arbitrary.
%   dim_reduction: dim(x) = size(x,1) >= dim_reduction; if '>' holds, perform dimension reduction to dimension dim_reduction, too.
%OUTPUT: 
%   e_hat: e_hat(:,t) is the estimated ISA source at time t.
%   W_hat: estimated ISA demixing matrix.
%   de_hat: in case of known subspace dimensions ('unknown_dimensions = 0') de_hat = de; else it contains the estimated subspace dimensions; ordered increasingly.
%
%REFERENCE:
%   Jason A. Palmer and Scott Makeig. Contrast functions for independent subspace analysis. In International conference on Latent Variable Analysis and Signal Separation (LVA/ICA), pages 115-122, 2012. (exciting, alternative proof idea for deflation methods)
%   Zoltan Szabo, Barnabas Poczos, Andras Lorincz: Undercomplete Blind Subspace Deconvolution. Journal of Machine Learning Research 8(May):1063-1095, 2007. (proof; sufficient conditions for the ISA separation theorem)
%   Jean-Francois Cardoso. Multidimensional independent component analysis. International Conference on Acoustics, Speech, and Signal Processing (ICASSP), pages 1941-1944, 1998. (conjecture)
%   Lieven De Lathauwer, Bart De Moor, and Joos Vandewalle. Fetal electrocardiogram extraction by source subspace separation. In IEEE SP/Athos Workshop on Higher-Order Statistics, pages 134-138, 1995. ('conjecture')

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

disp('ISA estimation: started.');

%ICA step (e_ICA,W_ICA):
    [e_ICA,W_ICA] = estimate_ICA(x,ICA,dim_reduction);
    
%clustering of the ICA elements (=permutation search; perm_ICA):
    switch unknown_dimensions
        case 0
            perm_ICA = clustering_UD0(e_ICA,de,ISA.opt_type,ISA.cost_type,ISA.cost_name);%'UD0': unknown_dimensions=0
            de_hat = de;%it is given
        case 1
            if strcmp(ISA.cost_type,'Ipairwise1d')
                num_of_comps = length(de);
                [perm_ICA,de_hat] = clustering_UD1(e_ICA,num_of_comps,ISA.opt_type,ISA.cost_name);%'UD1': unknown_dimensions=1                    
                [de_hat,per] = sort_subspaces_dimensions(de_hat);%sort the subspaces in increasing order with respect to their dimensions.
                perm_ICA = perm_ICA(per);%apply permutation 'perm_ICA' and then permutation 'per'
            else
                error('cost type=?');
            end
        otherwise
            error('unknown dimensions=?');
    end

%estimated ISA source(e_hat), ISA demixing matrix (W_hat):        
    e_hat = e_ICA(perm_ICA,:);
    W_hat = W_ICA(perm_ICA,:); 
    
disp('ISA estimation: ready.');
