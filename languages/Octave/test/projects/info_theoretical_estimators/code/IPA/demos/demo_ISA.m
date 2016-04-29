%function [] = demo_ISA()
%ISA (Independent Subspace Analysis) illustration.
%
%Model (ISA):
%   x = Ae, A:invertible, e=[e^1,...,e^M] (e^m:d_m-dimensional), I(e^1,...,e^M)=0, e_t: i.i.d. in time t, at most one of the e^m-s is Gaussian.
%Task: x -> A (or W=A^{-1}),e.

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
        %I:
            data_type = 'Aw';%see 'sample_subspaces.m'
            num_of_comps = 4;%number of components/subspaces in sampling
        %II:
            %data_type = 'multi2-3-4-geom';%see 'sample_subspaces.m'
            %num_of_comps = 3;%number of components/subspaces in sampling; '3'<->numbers in '2-3-4'
        %III:
            %data_type = {'multi2-spherical','multi3-spherical'};%exotic, combined datasets
            %num_of_comps = [1,1];%vector containing the number of the subspaces of given types (data_type{k})
        num_of_samples = 2*1000;%number of samples
    %estimation:    
        unknown_dimensions = 0;%0: '{d_m}_{m=1}^M: known'; 1: 'M is known'
        %ICA solver (see 'estimate_ICA.m'):
            %I:
                ICA.opt_type = 'fastICA'; 
            %II:
                %ICA.opt_type = 'EASI';
            %III:
                %ICA.opt_type = 'Jacobi1';
                %ICA.cost_type = 'sumH' or 'I', examples:
                    %i:
                        %ICA.cost_type = 'I';
                        %ICA.cost_name = 'KCCA'; %you can change it to any mutual information estimator
                    %ii:
                        %ICA.cost_type = 'sumH';
                        %ICA.cost_name = 'Renyi_kNN_1tok'; %you can change it to any entropy estimator
             %IV:                        
                %ICA.opt_type = 'Jacobi2';
                %ICA.cost_type = 'sumH' or 'I', examples:
                    %i:
                        %ICA.cost_type = 'I';
                        %ICA.cost_name = 'KCCA'; %you can change it to any mutual information estimator
                    %ii:
                        %ICA.cost_type = 'sumH';
                        %ICA.cost_name = 'Renyi_kNN_1tok'; %you can change it to any entropy estimator
        %ISA solver (clustering of the ICA elements):
            ISA.cost_type = 'sumH'; %'I','sumH', 'sum-I','Irecursive', 'Ipairwise', 'Ipairwise1d'
            ISA.cost_name = 'Renyi_kNN_1tok'; %example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are Renyi entropies estimated via kNN methods ('Renyi_kNN_1tok').
            ISA.opt_type = 'greedy';%optimization type: 'greedy', 'CE', 'exhaustive', 'NCut', 'SP1', 'SP2', 'SP3'
            %Many combinations are allowed for ISA.cost_type, ISA.cost_name and ISA.opt_type, see 'clustering_UD0.m', 'clustering_UD1.m'.
        
%data generation (x,A,e,de,num_of_comps):
    [x,A,e,de,num_of_comps] = generate_ISA(data_type,num_of_comps,num_of_samples);
    
%estimation (s_hat,W_hat,de_hat): 
    [e_hat,W_hat,de_hat] = estimate_ISA(x,ICA,ISA,unknown_dimensions,de,size(x,1));
  
%result:
    %global matrix(G):
        G = W_hat * A;
        hinton_diagram(G,'global matrix (G=WA)');%ideally: block-scaling matrix
    %performance of G:
        Amari_index = Amari_index_ISA(G,de,'subspace-dim-proportional',2),
    h = plot_subspaces(e_hat,data_type,'estimated subspaces (\hat{e}^m), m=1,...,M');
