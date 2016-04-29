function [P_elite] = TSP_estimate_elite_transition_probabilities(tours,num_of_elites,I)
%function [P_elite] = TSP_estimate_elite_transition_probabilities(tours,num_of_elites,I)
%Estimates the probability of the k1->k2 transitions (P_elite) via the elite samples, in case of  TSP (travelling salesman problem).
%
%INPUT:
%   tours: generated CE samples; tours(:,k): is the k^th samples.
%   I: I(k) is the cost of the k^th best (=having smallest cost) samples.
%   num_of_elites: number of  elite samples.

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

%initialization:
    D = size(tours,1);
    coos = [1:D];    

for k1 = 1 : D
    for k2 = 1 : D%k1->k2
        freq = 0;
        for k = 1 : num_of_elites
            k1n = coos(tours(:,I(k))==k1);%the k1 node occured the the k1n^th position
            if ( (k1n <D) && (tours(k1n+1,I(k))==k2)) ||...
               ( (k1n==D) && (tours(1,I(k))==k2))
                freq = freq + 1;
            end
        end
        P_elite(k1,k2) = freq / num_of_elites;
    end
end