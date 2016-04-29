%function [] = demo_fAR_IPA()
%fAR-IPA (fAR=Functional AutoRegressive, IPA=Independent Process Analysis) illustration.
%
%Model (fAR-IPA):
%   s(t) = f(s(t-1),...,s(t-L)) + e(t), s:hidden source, f:unknown, e:ISA source (see 'demo_ISA.m').
%   x(t) = A * s(t), A:invertible.
%Task: x -> A (or W=A^{-1}),f,s,e.

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
        num_of_comps = 2;%number of components/subspaces in sampling  
        num_of_samples = 10*1000;%number of samples
        L = 1; %fAR order (tested intensively for L=1 only: 'recursive_Nadaraya_Watson_estimator.m') 
    %ISA:    
        unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
        %ICA solver (see 'estimate_ICA.m'):
            ICA.opt_type = 'fastICA';
        %ISA solver (clustering of the ICA elements):
            ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
            ISA.cost_name = 'Renyi_kNN_k'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
            ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
            %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
    %fAR:
        fAR.beta_normalized = 1/8; %/in (0,1)
        fAR.method = 'recursiveNW'; %fAR estimation method, see 'estimate_fAR.m'
        fAR.L = L; %fAR order
        
%data generation (x,A,s,ds,num_of_comps):
    [x,A,s,e,de,num_of_comps] = generate_fAR_IPA(data_type,num_of_comps,num_of_samples,L);
    
%estimation (e_hat,W_hat,de_hat,s_hat): 
    [e_hat,W_hat,de_hat,s_hat] = estimate_fAR_IPA(x,ICA,ISA,unknown_dimensions,de,fAR);

%result:
    %global matrix(G):
        G = W_hat * A;
        hinton_diagram(G,'global matrix (G=WA)');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M');
    