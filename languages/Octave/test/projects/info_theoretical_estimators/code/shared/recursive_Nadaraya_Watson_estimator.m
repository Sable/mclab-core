function [V_hat] = recursive_Nadaraya_Watson_estimator(U,V,fAR)
%function [V_hat] = recursive_Nadaraya_Watson_estimator(U,V,fAR)
%Recursive Nadaraya-Watson nonparametric estimator.
%
%INPUT:
%   U: U(:,t) is the t^th input sample.
%   V: V(:,t) is the t^th output sample. The task is to learn the g: U(:,t) -> V(:,t) mapping.
%   fAR: parameters of the recursive Nadaraya Watson estimator; fAR.beta_normalized \in (0,1).
%OUTPUT:
%   V_hat: \hat{g}(U)
%
%REFERENCE:
%   Nadine Hilgert and Bruno Portier. Strong uniform consistency and asymptotic normality of a kernel based error density estimator in functional autoregressive models. Statistical Inference for Stochastic Processes 2012, DOI: 10.1007/s11203-012-9065-7. (first order fAR processes, i.e., L=1)

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
    [D,T] = size(V);%T=size(U,2)
    bet = fAR.beta_normalized / D; 
    V_hat = zeros(size(V));
    c1 = [1:T].^(bet*D); %row vector
    c2G =  [1:T].^(2*bet); %row vector; 'G'<->'Gaussian'

%computation [K(u)=K1d(||u||): spherical construction]:   
    k = 0;
    for u = U
        %Gaussian kernel:
            temp = c1.* exp(-1/2 * sum((u(:,ones(T,1))-U).^2,1) .* c2G); %u(:,ones(T,1)) = [u,...,u], where u is column vector; u(ones(1,D),:) = [u;...;u]
        temp = temp / sum(temp);%row vector
        k = k + 1;
        V_hat(:,k) = V * temp.';
    end
