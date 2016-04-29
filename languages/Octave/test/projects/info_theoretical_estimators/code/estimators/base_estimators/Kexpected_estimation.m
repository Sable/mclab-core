function [K] = Kexpected_estimation(Y1,Y2,co)
%function [K] = Kexpected_estimation(Y1,Y2,co)
%Estimates the expected kernel of two distributions from which we have samples, Y1 and Y2.
%
%We use the naming convention 'K<name>_estimation' to ease embedding new kernels on distributions.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: estimator object of a kernel on distributions.
%
%REFERENCE: 
%   Arthur Gretton, Karsten M. Borgwardt, Malte J. Rasch, Bernhard Scholkopf, and Alexander Smola. A kernel two-sample test. Journal of Machine Learning Research, 13:723–773, 2012.
%   Krikamol Muandet, Kenji Fukumizu, Francesco Dinuzzo, and Bernhard Scholkopf. Learning from distributions via support measure machines. In Advances in Neural Information Processing Systems (NIPS), pages 10-18, 2011.
%   Alain Berlinet and Christine Thomas-Agnan. Reproducing Kernel Hilbert Spaces in Probability and Statistics. Kluwer, 2004. (mean embedding)
%   Thomas Gartner, Peter A. Flach, Adam Kowalczyk, and Alexander Smola. Multi-instance kernels. In International Conference on Machine Learning (ICML), pages 179–186, 2002. (multi-instance/set/ensemble kernel)
%   David Haussler. Convolution kernels on discrete structures Technical report, Department of Computer Science, University of California at Santa Cruz, 1999. (convolution kernel -spec-> set kernel)

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

%dimension, number of samples:
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);

%verification:
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    
switch co.kernel
    case 'RBF' 
        %pairwise distances:
            kY1Y2 = sqdistance(Y1,Y2);
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = exp(-kY1Y2/(2*co.sigma^2));
    case 'exponential'
        %pairwise distances:
            kY1Y2 = sqdistance(Y1,Y2);
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = exp(-sqrt(kY1Y2)/(2*co.sigma^2)); %compared to RBF: ||.||_2^2 -> ||.||_2
    case 'Cauchy'
        %pairwise distances:
            kY1Y2 = sqdistance(Y1,Y2);
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = 1./(1+kY1Y2/co.sigma^2); 
    case 'student'
        %pairwise distances:
            kY1Y2 = sqdistance(Y1,Y2);
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = 1./(1+kY1Y2.^(co.d/2)); 
    case 'Matern3p2'
        %pairwise distances:
            kY1Y2 = sqrt(sqdistance(Y1,Y2));
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = ( 1 + sqrt(3) * kY1Y2 / co.l ) .* exp( -sqrt(3) * kY1Y2 / co.l );
    case 'Matern5p2'
        %pairwise distances:
            kY1Y2 = sqdistance(Y1,Y2);
        %distance(i,j) ->  kernel(i,j):
            kY1Y2 = ( 1 + sqrt(5) * sqrt(kY1Y2) / co.l + 5 * kY1Y2 / (3*co.l^2) ) .* exp( -sqrt(5) * sqrt(kY1Y2) / co.l );
    case 'poly2'
            kY1Y2 = (Y1.' * Y2 + co.c).^2;
    case 'poly3'
            kY1Y2 = (Y1.' * Y2 + co.c).^3;
    case 'ratquadr' 
            %pairwise distances:
                kY1Y2 = sqdistance(Y1,Y2);
            kY1Y2 = 1 - kY1Y2 ./ (kY1Y2 + co.c);
    case 'invmquadr'
            %pairwise distances:
                kY1Y2 = sqdistance(Y1,Y2);
            kY1Y2 = 1 ./ sqrt(kY1Y2 + co.c^2);
    otherwise
        error('Kernel=?');
end
    
K = sum(sum(kY1Y2)) / (num_of_samplesY1*num_of_samplesY2);
