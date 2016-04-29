function [X,Y,idx_train,idx_val,idx_test,X_parameter] = generate_supervised_entropy_dataset(L,N,L_train,L_val,L_test)
%function [X,Y,idx_train,idx_val,idx_test,X_parameter] = generate_supervised_entropy_dataset(L,N,L_train,L_val,L_test)
%Generates input-output data for supervised entropy learning.
%
%OUTPUT:
%   X: X{k} = samples from the k^th bag; one column of X{k} is one sample.
%   Y: Y(k) = real label of the k^th bag.
%      idx_train,idx_val,idx_test: training, validation, test indices [X_train = X(idx_train); X_val = X(idx_val); X_test = X(idx_test); training, validation, test input bags.  
%      Y_train = Y(idx_train); Y_val =  Y(idx_val); Y_test = Y(idx_test): training, validation, test output values.]
%   X_parameter: vector of rotation angles. 

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

%verification:
    if L~= L_train + L_val + L_test
        error('L = L_train + L_val + L_test: is needed.');
    end

%initialization:
    X = cell(L,1);  %inputs (bags representing the input distributions)
    Y = zeros(L,1); %output labels
    
%X,Y,X_parameter:
    %X_parameter:
        d = 2;%dimenson
        A = rand(d);
        X_parameter = linspace(0,pi,L);%rotation angles
    %X,Y:
       for nL = 1 : L
            %A_nL:
                A_nL = rotation_matrix(X_parameter(nL)) * A;
            %X:    
                X{nL} = A_nL * randn(d,N); %bag representing a rotated normal distribution
            %Y (entropy of the first coordinate):
                M = A_nL * A_nL.'; 
                s = M(1,1);
                Y(nL) = 1/2 * log(2*pi*exp(1)*s^2);
       end
           
%idx_train,idx_val,idx_test:
    p = randperm(L_train + L_val + L_test); %random permutation of the samples (to avoid orderedness)
    idx_train = p([1 : L_train]);
    idx_val = p([L_train + 1 : L_train + L_val]);
    idx_test = p([L_train + L_val + 1 : L_train + L_val + L_test]);             
    
%plot the generated dataset:
    figure;
       plot(X_parameter,Y); xlabel('Rotation angle'); ylabel('Entropy of the first coordinate (entire dataset)');
    figure;
       plot(X_parameter(idx_train),Y(idx_train),'r*'); hold on; xlabel('Rotation angle'); ylabel('Entropy of the first coordinate');
       plot(X_parameter(idx_val),Y(idx_val),'g*');
       plot(X_parameter(idx_test),Y(idx_test),'m*');
       legend({'training','validation','testing'});
    