function [e_hat,W_hat] = estimate_ICA_EASI(x,dim_reduction)
%function [e_hat,W_hat] = estimate_ICA_EASI(x,dim_reduction)
%ICA estimation using the EASI (equivariant adaptive separation via independence) method. The function copes with real/complex observations.
%
%INPUT:
%   x: x(:,t) is the t^th observation sample.
%  dim_reduction: <=dim(x)[= size(x,1)]; in case of '<', dimension reduction is also carried out.
%OUTPUT:
%   e_hat: estimated ICA elements; e_hat(:,t) is the t^th sample.
%   W_hat: estimated ICA demixing matrix.
%
%REFERENCE: 
%   Jean-Francois Cardoso and Beate Hvam Laheld. Equivariant adaptive source separation. IEEE Transactions on Signal Processing, 44:3017-3030, 1996.

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
    [d,num_of_samples] = size(x); %dimension, number of samples
    %demixing matrix (W_hat):
        if isreal(x) %real case
            W_hat = randn(dim_reduction,d);
        else %complex case
            W_hat = randn(dim_reduction,d) + i * randn(dim_reduction,d);
        end
    %learning rates:
        learning_rates = 2./[1:num_of_samples];%2/t    
        %learning_rates = 0.01*ones(num_of_samples,1); %constant learning rate
    I = eye(dim_reduction);%identity matrix
    
%demixing matrix estimation (W_hat):
    for t = 1 : num_of_samples
        learning_rate = learning_rates(t);
        y = W_hat * x(:,t); %actual observation
        %H (normalized EASI):
            diady = y * y';
            %some g(y) possibilities:
                gy = diag(diady).*y; 
                %gy =  y .* diag(0.1*diady).*diag(diady);
                %gy	=  y .* sqrt(diag(diady));
                %gy	=  y .* log(diag(diady));
                %gy = - y .* (diag(diady)<0.9);
            gyy = gy * y';
            H = (diady - I) / (1 + learning_rate * (y'*y)) + (gyy - gyy') / ( 1 + learning_rate * abs(y' * gy));
        W_hat = W_hat - learning_rate * H * W_hat;
    end
    
%source estimation (e_hat):
    e_hat = W_hat * x;
