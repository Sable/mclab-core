function [tour] = TSP_tour_generation_via_node_transitions(P)
%function [tour] = TSP_tour_generation_via_node_transitions(P)
%Generation of random tour (=permutation) using the node transition algorithm.
%
%INPUT:
%   P: =[p_{ij}] = [probability(j|i)] % assumed to be a square matrix with diag(P)=0.
%
%REFERENCE:
%   Reuven Y. Rubinstein, Dirk P. Kroese. The Cross-Entropy Method. Springer, 2004. (Algorithm 4.1.1 on page 175)

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

D = size(P,1);%length of the tour

%the first element of the tour (j_star) is chosen uniformly from {1,...,D}:
    j_star = randi(D,1,1);
    tour = zeros(D,1);  
    tour(1) = j_star;
    U = false(D,1);%included nodes in the tour: 'U(i)=true' means that the i^th node has been included
    U(j_star) = true;

for k = 2 : D%tour(k)=...
    j_star = discrete_nonuniform_sampling(P(j_star,:),1,1);
    %sum of the elements in the j_star^th row in U (=:s), and zero them out:
        s = sum(P(j_star,U),2);
        P(j_star,U) = 0;
    %the new node of the tour is j_star:
        U(j_star) = true;
        tour(k) = j_star;        
    %normalize the transition density on the not selected nodes (~U) from node j_star:
        %Matlab:
            %P(j_star,~U) = P(j_star,~U) / (1-s);
        %Matlab/Octave (else one obtains a divided by zero error in Octave):
            if k~=D
                P(j_star,~U) = P(j_star,~U) / (1-s);
            end
end
