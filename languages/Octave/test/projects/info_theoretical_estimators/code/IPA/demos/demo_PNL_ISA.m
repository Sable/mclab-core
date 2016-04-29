%function [] = demo_PNL_ISA()
%PNL ISA (Post Nonlinear Independent Subspace Analysis) illustration.
%
%Model (PNL ISA):
%   x = g(Ae), A:invertible, e: ISA source (see 'demo_ISA.m'), g: coordinate-wise acting, invertible function (=post nonlinearity).
%Task: x -> [g^{-1},A (or W=A^{-1})],e.

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
        num_of_comps = 6;%number of components/subspaces in sampling
        num_of_samples = 5*1000;%number of samples
    %ISA:    
        unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
        %ICA solver (see 'estimate_ICA.m'):
            ICA.opt_type = 'fastICA';
        %ISA solver (clustering of the ICA elements):
            ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
            ISA.cost_name = 'Renyi_kNN_k'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
            ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
            %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
    %gaussianization technique:
        gaussianization.method = 'rank';%For possibilites, see 'estimate_gaussianization.m'.
        gaussianization.c = 0.97; %parameter of the 'rank' method
        
%data generation (x_LIN,A,e,de,num_of_comps):
    [x_LIN,A,e,de,num_of_comps] = generate_ISA(data_type,num_of_comps,num_of_samples);
    %PNL mixing:
        gs = create_PNL_mixing_functions(sum(de));
        x_PNL = PNL_mixing(x_LIN,gs);
        h = plot_subspaces(x_PNL,data_type,'post nonlinear mixture (x=g(Ae))');
        
%estimation (W_hat,e_hat,de_hat):    
    [W_hat,e_hat,de_hat] = estimate_PNL_ISA(x_PNL,ICA,ISA,unknown_dimensions,de,gaussianization);

%result:
    %global matrix(G):
        %G: G * e = e_hat (XA=B => X=B/A)
            e_hat = E0(e_hat);
            G = e_hat / e;
            hinton_diagram(G,'global matrix (G: e -> \hat{e})');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M');    