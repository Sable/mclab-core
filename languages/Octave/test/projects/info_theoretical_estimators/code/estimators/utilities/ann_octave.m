function [indices,squared_distances] = ann_octave(Y,Q,co,Y_equals_to_Q)
%function [indices,squared_distances] = ann_octave(Y,Q,co,Y_equals_to_Q)
%Computes the k-nearest neighbor distances of each point (Q(:,t)) in
%matrix Q from samples (Y(:,t)) in matrix Y. The number of samples in Q (size(Q,2)) and Y (size(Y,2)) can be different. Parameters in the cost object (co), can be used to fine-tune the kNN search (k,...).
%Y_equals_to_Q is a flag indicating whether Y is equal to Q or not: true (=1), false (=0).
%The computations are based on the ann Octave bindings.
%
%OUTPUT:
%   squared_distances: squared distances of the nearest neighbors; max(co.k) x size(Q,2).
%   indices: indices(i,j) = i^th nearest neighbor from the samples {Y(:,t),t=1,2,...} to the j^th query point (Q(:,j)); max(co.k) x size(Q,2); int32.
%EXAMPLE:
%   [indices,squared_distances] = ann_octave(Y,Y,co,1);
%   [indices,squared_distances] = ann_octave(Y,Q,co,0);

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
   kmax = max(co.k);
   [d,num_of_samplesY] = size(Y);
   num_of_samplesQ = size(Q,2);
   %preallocation:
      indices = zeros(kmax,num_of_samplesQ,'int32');
      squared_distances = zeros(kmax,num_of_samplesQ);
      
%create kd-tree (from Y):
    kd = new_ANNkd_tree(Y.');%t^th row = t^th sample
    
%queries (from Q):
    nq = 0;
    for q = Q
       if Y_equals_to_Q
             [idx,dist] = kd.annkSearch(q,kmax+1,co.epsi);
       else
             [idx,dist] = kd.annkSearch(q,kmax,co.epsi);
       end
       nq = nq + 1;	
       if Y_equals_to_Q
            indices(:,nq) = int32(idx(2:end)+1);
            squared_distances(:,nq) = dist(2:end);
       else
            indices(:,nq) = int32(idx(:)+1);
            squared_distances(:,nq) = dist(:);
       end
    end
    
%delete kd-tree:    
    delete_ANNkd_tree(kd);
