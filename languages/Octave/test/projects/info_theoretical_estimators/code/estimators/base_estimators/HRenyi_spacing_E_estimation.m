function [H] = HRenyi_spacing_E_estimation(Y,co)
%function [H] = HRenyi_spacing_E_estimation(Y,co)
%Estimates the Renyi entropy (H) of Y using an extension of the 'empiric entropy estimation of order m' method.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods. 
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Mark P. Wachowiak, Renata Smolikova, Georgia D. Tourassi, and Adel S. Elmaghraby. Estimation of generalized entropies with sample spacing. Pattern Analysis and Applications 8: 95-101, 2005.

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

[d,num_of_samples] = size(Y);

%verification:
    if d~=1
        error('The samples must be one-dimensional for this estimator.');
    end
    
m = floor(sqrt(num_of_samples));%m/num_of_samples->0, m,num_of_samples->infty; m<num_of_samples/2
Y_sorted = sort(Y);
Y_sorted = [repmat(Y_sorted(1),1,m),Y_sorted,repmat(Y_sorted(end),1,m)];
d1 = diff(Y_sorted)/2; %(y_(k)-y_(k-1))/2
    
diff1 = ev(d1,1,m-1,m);
diff2 = ev(d1,0,num_of_samples-m,m) + ev(d1,m,num_of_samples,m);
diff3 = ev(d1,num_of_samples+1-m,num_of_samples-1,m);
    
dm = Y_sorted([1+m:num_of_samples+m]+m) - Y_sorted([1-m:num_of_samples-m]+m);
dm_inv = 2./dm;%2/[y_(k+m)-y_(k-m)]
    
idiff1 = cumsum(ev(dm_inv,1-m,-1,m));
idiff2_temp = ev(dm_inv,1-m,num_of_samples-m,m);%L-sum
idiff2 = Lsum(idiff2_temp,m);
idiff3 = fliplr(cumsum(fliplr( ev(dm_inv,num_of_samples+2-2*m,num_of_samples-m,m) )));

term1 = sum(diff1.*(idiff1.^co.alpha));
term2 = sum(diff2.*(idiff2.^co.alpha));
term3 = sum(diff3.*(idiff3.^co.alpha));
    
H = term1 + term2 + term3;
H = log(H/num_of_samples^co.alpha) / (1-co.alpha);
    
%-------------------
function [v] = ev(v0,start_idx,end_idx,off)
%Extraction of the [start_idx:end_idx] coordinates from vector v0, with
%offset off.

v = v0([start_idx:end_idx]+off);

function[v] = Lsum(v,L)
%[v_1,...,v_T] -> [v_1+..._v_L,v_2+...+v_{2+L-1},...,v_{K-L+1}...+v_K], K=length(v)

f = filter(ones(1,L),1,v);
v = f(L:end);

