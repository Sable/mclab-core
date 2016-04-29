function [validation_slice,test_slice] = compute_slices(rp_v,G,G_train,L_train,Y_train,Y_val,Y_test,idx_train,idx_val,idx_test)
%function [validation_slice,test_slice] = compute_slices(rp_v,G,G_train,L_train,Y_train,Y_val,Y_test,idx_train,idx_val,idx_test)
%Computes the validation and the test slices.
%Note: The code supports 'Matlab: parallel computing', see below (parfor).

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

validation_slice = zeros(length(rp_v),1);%column vector
test_slice = validation_slice; %=0
for nrp = 1 : length(rp_v)  %use this line without 'Matlab: parallel computing'
%parfor nrp = 1 : length(rp_v) %use this line with 'Matlab: parallel computing'
    rp = rp_v(nrp);
    %left:
        A = real(inv(G_train + L_train * rp * eye(L_train)));%real(): 'eps' imaginary values may appear
        left = Y_train.' * A; %left hand side of the inner product; row vector
    %Y_predicted_val, Y_predicted_test:
        Y_predicted_val = ( left * G(idx_train,idx_val) ).';  %column vector
        Y_predicted_test = ( left * G(idx_train,idx_test) ).';%column vector
    %update validation_surface:
        validation_slice(nrp) = RMSE(Y_val,Y_predicted_val);
        test_slice(nrp) = RMSE(Y_test,Y_predicted_test);
end
