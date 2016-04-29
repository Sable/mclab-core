function [t1,t2,t3] = Edgeworth_t1_t2_t3(Y)
%function [t1,t2,t3] = Edgeworth_t1_t2_t3(Y)
%Computes the three kappa_ijk := E[x_i x_j x_k] based terms (t1,t2,t3) in the Edgeworth expansion based entropy estimator, see 'HShannon_Edgeworth_estimation.m'.
%
%INPUT:
%  Y: Y(:,t) is the t^th sample

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

d = size(Y,1);%dimension

%t1:
    t1 = 0;
    for i = 1 : d %d terms
        kappa_iii = mean(Y(i,:).^3); 
        t1 = t1 + kappa_iii^2;
    end
    
%t2:
    t2 = 0;
    for i = 1:d
    for j = [1:i-1,i+1:d] %j\ne i; 2*nchoosek(d,2) terms
        kappa_iij = mean(Y(i,:).^2 .* Y(j,:));
        t2 = t2 + kappa_iij^2;
    end
    end
    t2 = 3 * t2;
    
%t3:
    t3 = 0;
    for i = [1:d-2]%i<j<k; nchoosek(d,3) terms
    for j = [i+1:d-1]
    for k = [j+1:d]
        kappa_ijk = mean(Y(i,:).*Y(j,:).*Y(k,:));
        t3 = t3 + (kappa_ijk)^2;
    end
    end
    end
    t3 = t3 / 6;
