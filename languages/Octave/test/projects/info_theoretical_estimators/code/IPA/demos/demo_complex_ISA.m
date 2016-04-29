%function [] = demo_complex_ISA()
%Complex ISA (Independent Subspace Analysis) illustration. Method: complex -> real transformation, +real ISA.
%
%Model (complex ISA):
%   x = Ae, A:invertible, e=[e^1,...,e^M] (e^m:d_m-dimensional, i.e., e^m in C^{d_m}), I(C2R_vector(e)^1,...,C2R_vector(e)^M)=0, e_t: i.i.d. in time t, at most one of the C2R_vector(e^m)-s is Gaussian.
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
        data_type = 'multi4-geom';%see 'sample_subspaces.m'
        num_of_comps = 2;%number of components/subspaces in sampling
        num_of_samples = 5*1000;%number of samples
    %(real) ISA:    
        unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
        %ICA solver (see 'estimate_ICA.m'):
            ICA.opt_type = 'fastICA';
        %ISA solver (clustering of the ICA elements):
            ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
            ISA.cost_name = 'Renyi_kNN_k'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
            ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
            %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
        
%data generation (x,A,e,de,num_of_comps):
    [x,A,e,de,num_of_comps] = generate_complex_ISA(data_type,num_of_comps,num_of_samples);
    
%estimation (e_real_hat,W_real_hat,de_real_hat,e_hat):
    [e_real_hat,W_real_hat,de_real_hat,e_hat] = estimate_complex_ISA(x,ICA,ISA,unknown_dimensions,de,size(x,1));

%result:
    %global matrix(G):
        A_real = C2R_matrix(A);
        G = W_real_hat * A_real;
        hinton_diagram(G,'global matrix (G=W\phi_M(A))');%ideally: block-scaling matrix with (2d_m) x (2d_m) sized blocks.
    %performance of G:
        de_real = 2 * de;
        Amari_index = Amari_index_ISA(G,de_real,'subspace-dim-proportional',2),
 