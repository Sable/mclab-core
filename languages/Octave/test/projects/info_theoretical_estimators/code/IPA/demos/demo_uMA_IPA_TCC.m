%function [] = demo_uMA_IPA_TCC()
%uMA-IPA (undercomplete Moving Average Independent Process Analysis; MA-IPA = BSSD = Blind Subspace Deconvolution) illustration. Method: TCC, see 'estimate_uMA_IPA_TCC.m'.
%
%Model (uMA-IPA):
%   x(t) = \sum_{i=0}^L H_i e(t-i), e: ISA source, dim(x) > dim(e), and H[z] = \sum_{i=0}^L H_i z^{-i} has a polynomial matrix left inverse (such inverse exists under quite mild conditions in the undercomplete case).
%or in short
%   x = H[z]e.
%Task: x -> e.

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

%clear start:
    clear all; close all;
    
%parameters:
    %dataset:
        data_type = 'Aw';%see 'sample_subspaces.m'
        num_of_comps = 3;%number of components/subspaces in sampling
        num_of_samples = 5*1000;%number of samples
        undercompleteness = 1; %dim(x) = ceil((undercompleteness+1) x dim(s))
        MA.type = 'randn'; %type of the convolution, see 'MA_polynomial.m'.
        MA.L = 1;%length of the convolution; H_0,...,H_{L}: L+1 H_j matrices        
    %ISA:    
        unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
        %ICA solver (see 'estimate_ICA.m'):
            ICA.opt_type = 'fastICA';
        %ISA solver (clustering of the ICA elements):
            ISA.cost_type = 'Ipairwise1d'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
            ISA.cost_name = 'GV'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
            ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
            %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
    
%data generation (x,H,e,de,num_of_comps):
    [x,H,e,de,num_of_comps] = generate_MA_IPA(data_type,num_of_comps,num_of_samples,undercompleteness,MA);
    
%estimation via the TCC method (e_hat,W_hat,de_hat,L2):
    [e_hat,W_hat,de_hat,L2] = estimate_uMA_IPA_TCC(x,MA.L,ICA,ISA,unknown_dimensions,de);

%result:
    %mixing matrix and dimenions(de) associated to the TCC based method:
        A = mixing_matrix_TCC(H,de,L2);%A=A(H,...)
        de = kron(de,ones(MA.L+L2,1));%the subspaces are recovered L+L2-times, in the ideal case
    %global matrix(G):
        G = W_hat * A;
        hinton_diagram(G,'global matrix (G=WA)');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M'); %ideally: L+L2 rotated versions of each component (e^m)
