%function [] = demo_mAR_IPA()
%mAR-IPA (AutoRegressive Independent Process Analysis with missing observations) illustration.
%
%Model (mAR-IPA):
%   F[z]s = e, F[z]=I-F_1*z^1-...-F_L*z^L, F[z]:stable, e:ISA source (see 'demo_ISA.m'),
%       x = As, A:invertible,
%       y = M(x): observation.
%Task: y -> A (or W=A^{-1}),s,F[z],e.

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
            num_of_samples = 5*1000;%number of samples    
            L = 1; %AR order 
            F_lambda = 0.7; %AR stability parameter, 0<F_lambda<1, see 'generate_AR_polynomial.m'
        p = 0.05; %p=P(x(i,t) is not observable)
    %estimation:
        %mARfit:
            mAR.L = L;%mAR order; can be vector, too; in that case the 'best' AR order is chosen according to SBC, see 'estimate_mAR.m'.
            mAR.method = 'subspace';%mAR estimation method, see 'estimate_mAR.m'
        %ISA:
            unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
            %ICA solver (see 'estimate_ICA.m'):
                ICA.opt_type = 'fastICA';
            %ISA solver (clustering of the ICA elements):
                ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
                ISA.cost_name = 'Renyi_kNN_k'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
                ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
                %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'
        
%data generation (y,x,A,s,e,F,de):
    [y,x,A,s,e,Fs,de,Fx] = generate_mAR_IPA(data_type,num_of_comps,num_of_samples,L,F_lambda,p);
    
%estimation (s_hat,e_hat,W_hat,de_hat,Fx_hat,Fs_hat):
    [s_hat,e_hat,W_hat,de_hat,Fx_hat,Fs_hat] = estimate_mAR_IPA(y,mAR,ICA,ISA,unknown_dimensions,de);
    
%result:
    %global matrix(G):
        G = W_hat * A; %G * s = s_hat; G2 = s_hat/s;
        hinton_diagram(G,'global matrix (G=WA)');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M');
    %dynamics:
        %Fx,Fx_hat, %ideally: Fx = Fx_hat    
        %Fs_hat, Fs_hat_temp = basis_transformation_AR(Fs,G,inv(G)), %ideally: these two matrices are equal