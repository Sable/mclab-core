function [perm_ICA] = clustering_UD0_CE_pairadditive_wrt_coordinates(S,ds)
%function [perm_ICA] = clustering_UD0_CE_pairadditive_wrt_coordinates(S,ds)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds)
%using (i) the 'CE' method and (ii) and (ii) similarity matrix S.
%
%Note:
% in case of different ISA formulations, one can use 'clustering_UD0_CE_general.m' to carry out the optimization.
%
%INPUT:
% s_ICA: s_ICA(:,t) is the t^th estimated realization of the ICA sources.
%    ds: subspace dimensions.
%OUTPUT:
%   perm_ICA: permutation of the ICA elements.
%
%REFERENCE:
%   This function is a simple adaptation of the works:
%       Zoltan Szabo, Barnabas Poczos, Andras Lorincz: Cross-Entropy Optimization for Independent Process Analysis. ICA 2006, pages 909-916. (adaptation to the ISA problem)
%       Reuven Y. Rubinstein, Dirk P. Kroese. The Cross-Entropy Method. Springer, 2004. (TSP problem; TSP=travelling salesman problem)
%   to the pairwise similarity (S) based (see cost_type = 'Ipairwise1d') ISA formulation.

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

%parameters:
    alpha = 0.4;%smoothing parameter
    rho = 1/10;%elite treshold
    c = 5;%number of CE samples = c x number of parameters to estimate
    %stopping criteria:
        tolerance_treshold = 0.005; %stop optimization if the change of P is smaller than tolerance_treshold
        tolerance_last = 7; %stop optimization if the last tolerance_last gamma values (=elite treshold) were equal
    
%initialization:
    D = size(S,1);%dim(s)
    num_of_CE_samples = c * D * (D-1); %c x number of parameters
    tours = zeros(D,num_of_CE_samples); %preallocation
    costs = zeros(num_of_CE_samples,1); %preallocation
    %transition probabilities (uniform):
        P = ones(D) / (D-1);
        P = P - diag(diag(P));
    P_elite = zeros(D); %preallocation
    costs_last = zeros(tolerance_last,1);
    it = 0;
    const_gammas = false;
    P_old = inf(D);%P in the last iteration
    %mask (corresponding to coordinate pairs from different subspaces):
        mask = ds_mask(ds);
    
disp('Clustering of the ICA elements (CE optimization): started.');

while (max(max(abs(P-P_old))) > tolerance_treshold) && (~const_gammas) %while P changed 'much' and the last gamma values were different in the last tolerance_last iterations
    %generate num_of_CE_samples samples and compute their costs:
        for k = 1 : num_of_CE_samples
            tour = TSP_tour_generation_via_node_transitions(P);
            tours(:,k) = tour;
            costs(k) = sum(sum(S(tour,tour).*mask));
        end
    %sort the samples according to their performances:
        [costs,I] = sort(costs);
        num_of_elites = floor(rho*num_of_CE_samples);  %best \rho percent is the elite
    %Estimation the transition probabilites via the elite samples:
        P_elite = TSP_estimate_elite_transition_probabilities(tours,num_of_elites,I);
    %P update:
        P_old = P; %last P (see the stopping criterion)
        P = alpha * P_elite + (1-alpha) * P;
    %stopping criterion (costs_last, const_gammas):
        %update the vector of previous costs:
            costs_last(1:end-1) = costs_last(2:end);
            costs_last(end) = costs(num_of_elites);
        const_gammas = (sum(costs_last==costs_last(end)) == tolerance_last); %true if the elite treshold (gamma) has not changed in the last tolerance_last iterations
    it = it + 1;        
    disp(strcat(['Iteration ', num2str(it),': ready [elite level(=gamma_t)=',num2str(costs_last(end)),'].']));
end
disp('Clustering of the ICA elements: ready.');
perm_ICA = tours(:,I(1));
