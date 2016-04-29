%function [] = demo_ARX_IPA()
%ARX-IPA (ARX = AutoRegressive with eXogenous input, IPA = Independent Process Analysis) illustration.
%
%Model:
%   s(t+1) = \sum_{i=0}^{Ls-1} F_i s(t-i)+\sum_{j=0}^{Lu-1} B_j u(t+1-j)+e(t+1),
%   x(t) = As(t),
%or in short
%   F[z]s = B[z]u+e, F[z] = I - \sum_{i=0}^{Ls-1} F_i z^{i+1}:stable, B[z] = \sum_{j=0}^{Lu-1} B_j z^j, e:ISA source (see 'demo_ISA.m'),
%   x = As, A: invertible.
%Task: x,u -> A (or W=A^{-1}),s,F[z],B[z],e.

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
        %driving noise(e):
            data_type = 'Aw';%see 'sample_subspaces.m'
            num_of_comps = 3;%number of components/subspaces in sampling
        %hidden source(s):
            num_of_samples = 1*1000;%number of samples    
            Ls = 1; %F_0,..._F_{Ls-1},Ls=number of F_i-s; >=1
            Lu = 1; %B_0,...,B_{Lu-1},Lu=number of B_j-s; >=1
            F_lambda = 0.7; %AR stability parameter, 0<F_lambda<1, see 'generate_AR_polynomial.m'
        %dimension of the control:
            du = 4;
    %estimation:            
        u_size = 0.2; %size of the control; box constraint, i.e., |u_i| <= u_size
        %ISA:
            unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
            %ICA solver (see 'estimate_ICA.m'):
                ICA.opt_type = 'fastICA';
            %ISA solver (clustering of the ICA elements):
                ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
                ISA.cost_name = 'Renyi_kNN_k'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
                ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
                %Many of combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
        %ARX solver:                
            ARX.method = 'NIW'; %ARX identification method; see 'estimate_ARX_IPA.m'
            ARX.Lx = Ls; %AR order
            ARX.Lu = Lu; %control ('X') order
            
%data generation (e,de,Fs,Bs,A,Fx,Bx,Ae,du):
    [e,de,Fs,Bs,A,Fx,Bx,Ae,du] = generate_ARX_IPA_parameters(data_type,num_of_comps,num_of_samples,Ls,F_lambda,Lu,du);

%estimation (e_hat,de_hat,Fs_hat,Bs_hat,W_hat,Fx_hat,Bx_hat,s_hat): 
    [e_hat,de_hat,Fs_hat,Bs_hat,W_hat,Fx_hat,Bx_hat,s_hat] = estimate_ARX_IPA(Fx,Bx,Ae,sum(de),du,u_size,ICA,ISA,unknown_dimensions,de,ARX); %note: Fx,Bx, and Ae are used for observation generation only.

%result:
    %Fx,Fx_hat, %ideally: these two matrices are equal
    %Bx,Bx_hat, %ideally: these two matrices are equal
    %global matrix(G):
        G = W_hat * A;
        hinton_diagram(G,'global matrix (G=WA)');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M');
    