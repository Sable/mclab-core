function [S] = I_similarity_matrix(Y,co)
%function [S] = I_similarity_matrix(Y,co)
%Computes a similarity matrix for a particular one-dimensional mutual information estimator given in cost object co. 
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%OUTPUT:
%   S: similarity matrix, S = [S_ij], S_ij = I(y_i,y_j).

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

disp('Pairwise similarity matrix computation: started.');
%similarity matrix(S):
    switch co.name
        case 'GV'
            S = IGV_similarity_matrix(Y,co);
        otherwise
            D = size(Y,1);
            for k1 = 1 : D %k1^th coordinate
            for k2 = k1 : D %k2^th coordinate
                S(k1,k2) = I_estimation(Y([k1,k2],:),[1;1],co);%I(y_{k1},y_{k2})
                S(k2,k1) = S(k1,k2);%symmetry; assumption
            end
            end
    end
disp('Pairwise similarity matrix computation: ready.');

%Verify that S_ij >=0 (negative values may appear, e.g, in case of (i) an entropy estimator with an additive constant, or (ii) small number of samples.
    nonnegativeS = ( sum(sum(S>=0)) == prod(size(S)) );
    if ~nonnegativeS
        warning('Similarity matrix S contains negative elements. Possible reasons: entropy estimator with an additive constant or small number of samples.');
        disp('We apply the correction: S -> S - min_ij(S).');
        S = S - min(min(S));
    end
    
%plot the obtained similarity matrix:
    hinton_diagram(S,'similarity matrix (S=[I(s_{ICA,i},s_{ICA,j})])');
