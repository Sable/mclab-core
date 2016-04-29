%function [] = quick_test_Kpos_semidef()
%Quick test for the positive semi-definiteness of the Gram matrix determined by kernel K. In the test, uniform/normal variables are considered.

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
    distr = 'uniform'; %possibilities: 'uniform', 'normal' 
    d = 1; %dimension of the distribution
    num_of_distributions = 5; %number of distributions
    num_of_samples = 5000;%a given distribution is represented by num_of_samples samples
    %kernel used to evaluate the distributions:    
        %base:
            cost_name = 'expected'; %d>=1
            %cost_name = 'Bhattacharyya_kNN_k'; %d>=1
            %cost_name = 'PP_kNN_k'; %d>=1
        %meta:
            %cost_name = 'JS_DJS';  %d>=1
	        %cost_name = 'EJS_DJS'; %d>=1
	        %cost_name = 'JT_HJT';  %d>=1
	        %cost_name = 'EJR1_HR'; %d>=1
	        %cost_name = 'EJR2_DJR';%d>=1
	        %cost_name = 'EJT1_HT'; %d>=1
	        %cost_name = 'EJT2_DJT';%d>=1

%initialization:    
    num_of_samples_half = floor(num_of_samples/2);
    co = K_initialization(cost_name,1);
    Ys = {};

%generate samples from the distributions (Ys):    
    for n = 1 : num_of_distributions
        %generate samples from the n^{th} distribution (Y):
            switch distr
                case 'uniform'
                    %a,b:
                        a = -rand(d,1); b = rand(d,1);
                        %a = zeros(d,1); b = ones(,1);
                    %(random) linear transformation applied to the data:
                        A = rand(d); 
                        %A = eye(d);%do not transform the data
                    Y =  A * (rand(d,num_of_samples) .* repmat(b-a,1,num_of_samples) + repmat(a,1,num_of_samples)); %AxU[a,b]
                case 'normal'
                    %mean:
                        m = rand(d,1);
                        %m = zeros(d,1);
                    %(random) linear transformation applied to the data:
                        A = rand(d); 
                        %A = eye(d);  %do not transform the data
                    Y = A * randn(d,num_of_samples) + repmat(m,1,num_of_samples); %AxN(0,I)+m
            end
        Ys{n} = Y;
    end

%sampled distributions (Ys) -> Gram matrx ( G = [K(Ys{i},Ys{j})] ):
    G = compute_Gram_matrix(Ys,co);
    
%compute the eigenvalues of the Gram matrix (eigenvalues):
    eigenvalues = eig(G); 
    min_eig = min(eigenvalues); %minimal eigenvalue; ideally it is non-negative
    
%plot:    
    plot([1:num_of_distributions],eigenvalues);
    xlabel('Index of the sorted eigenvalues: i');
    ylabel('Eigenvalues of the (estimated) Gram matrix: \lambda_i');  
    title(strcat(['Minimal eigenvalue: ',num2str(min_eig)]));
   
