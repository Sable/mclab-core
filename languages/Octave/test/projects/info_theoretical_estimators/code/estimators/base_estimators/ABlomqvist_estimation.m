function [A] = ABlomqvist_estimation(Y,ds,co)
%function [A] = ABlomqvist_estimation(Y,ds,co)
%Estimates the multivariate extension of Blomqvist's beta (medial correlation coefficient). 
%
%We use the naming convention 'A<name>_estimation' to ease embedding new association estimator methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: association estimator object.
%
%REFERENCE: 
%   Friedrich Schmid, Rafael Schmidt, Thomas Blumentritt, Sandra Gaiser, and Martin Ruppert. Copula Theory and Its Applications, Chapter Copula based Measures of Multivariate Association. Lecture Notes in Statistics. Springer, 2010. (multidimensional case, length(ds)>=2)
%   Manuel Ubeda-Flores. Multivariate versions of Blomqvist's beta and Spearman's footrule. Annals of the Institute of Statistical Mathematics, 57:781-788, 2005.
%   Nils Blomqvist. On a measure of dependence between two random variables. The Annals of Mathematical Statistics, 21:593-600, 1950. (2D case, statistical properties)
%   Frederick Mosteller. On some useful ''inefficient'' statistics. Annals of Mathematical Statistics, 17:377--408, 1946. (2D case, def)

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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if sum(ds) ~= size(Y,1);
        error('The subspace dimensions are not compatible with Y.');
    end
    if ~one_dimensional_problem(ds)
        error('The subspaces must be one-dimensional for this estimator.');
    end

[d,num_of_samples] = size(Y);
U = copula_transformation(Y);

h = 2^(d-1) / (2^(d-1)-1); %h(d)
C1 = mean(all(U<=1/2,1)); %C(1/2)
C2 = mean(all(U>1/2,1)); %\bar{C}(1/2)
A = h * ( C1 + C2 - 2^(1-d) );
