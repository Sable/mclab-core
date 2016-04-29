function [C] = compute_dCov_dCor_statistics(X,a)
%function [C] = compute_dCov_dCor_statistics(X,a)
%Computes the statistics (C; matrix A or B in the paper) to distance covariance/correlation.
%
%INPUT:
%   X: X(:,t) is the t^th sample.
%   a: alpha.

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

d = (sqdistance(X)).^(a/2);%squared pairwise distances => alpha/2
ck = mean(d,2);
cl = mean(d,1);
c = mean(ck); %=mean(cl)

C = bsxfun(@minus,bsxfun(@minus,d,ck),cl) + c;