function [squared_distances,indices] = kNN_squared_distances(Y,Q,co,Y_equals_to_Q)
%function [squared_distances,indices] = kNN_squared_distances(Y,Q,co,Y_equals_to_Q)
%Computes the k-nearest neighbor distances of each point (Q(:,t)) in
%matrix Q from samples (Y(:,t)) in matrix Y. The number of samples in Q (size(Q,2)) and Y (size(Y,2)) can be different. Parameters in the cost object (co), can be used to fine-tune the kNN search (k,...).
%Y_equals_to_Q is a flag indicating whether Y is equal to Q or not: true (=1), false (=0).
%
%OUTPUT:
%   squared_distances: squared distances of the nearest neighbors; max(co.k) x size(Q,2).
%   indices: indices(i,j) = i^th nearest neighbor from the samples {Y(:,t),t=1,2,...} to the j^th query point (Q(:,j)); max(co.k) x size(Q,2); int32.
%EXAMPLE:
%   [squared_distances,indices] = kNN_squared_distances(Y,Y,co,1);
%   [squared_distances,indices] = kNN_squared_distances(Y,Q,co,0);

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

switch co.kNNmethod
    case 'knnFP1'%fast pairwise distance computation and C++ partial sort
        if Y_equals_to_Q %'Y==Q' => exclude the points themselves
            [squared_distances,indices] = knn(Q, Y, max(co.k)+1);%assumption below:max(co.k)+1 <= size(Q,1)[=size(Y,1)]
            squared_distances = squared_distances(2:end,:);
            indices = int32(indices(2:end,:));
        else
            [squared_distances,indices] = knn(Q, Y, max(co.k));
            indices = int32(indices);
        end
    case 'knnFP2'%fast pairwise distance computation
        sq = sqdistance(Y,Q);%fast squared distance computation
        [S,I] = sort(sq);
        if Y_equals_to_Q %'Y==Q' => exclude the points themselves
            squared_distances = S([1:max(co.k)]+1,:);
            indices = int32(I([1:max(co.k)]+1,:));
        else
            squared_distances = S(1:max(co.k),:);
            indices = int32(I(1:max(co.k),:));
        end
    case 'knnsearch' %Statistics Toolbox:Matlab
        if Y_equals_to_Q %'Y==Q' => exclude the points themselves
            [indices,distances] = knnsearch(Y.',Q.','K',max(co.k)+1,'NSMethod',co.NSmethod); %[double,...
            indices = int32(indices(:,2:end).');%.': to be compatible with 'ANN'
            squared_distances = (distances(:,2:end).').^2;%distances -> squared distances; .': to be compatible with 'ANN'
        else
            [indices,distances] = knnsearch(Y.',Q.','K',max(co.k),'NSMethod',co.NSmethod); %[double,...
            indices = int32(indices.');%.': to be compatible with 'ANN'
            squared_distances = (distances.').^2;%distances -> squared distances; .': to be compatible with 'ANN'
        end
    case 'ANN'%ANN library/wrapper
        if working_environment_Matlab
            ann_object = ann(Y);
            [indices, squared_distances] = ksearch(ann_object, Q, max(co.k), co.epsi,0);%'0': nearest neighbors do not include the points themselves. max(co.k) is used in 'HRenyi_kNN_S_estimation.m'
            ann_object = close(ann_object);
        else
            [indices,squared_distances] = ann_octave(Y,Q,co,Y_equals_to_Q);
        end
    otherwise
        error('kNN method=?');
end
