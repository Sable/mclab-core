function [mixture] = mixture_distribution(varargin)
%function [mixture] = mixture_distribution(varargin)
%function [mixture] = mixture_distribution(X1,...,XM,w)
%Sampling from the mixture distribution, where samples from the individual distributions and the mixing weights are given.
%
%INPUT:
%   Xm: samples from the m^{th} distribution; Xm(:,t) is the t^th sample (m=1,...,M).
%   w: mixing weights. Assumption: length(w)=M, w>0, sum(w)=1. 
%OUTPUT:
%   mixture: samples from the mixture distribution. mixture(:,i) ~ w(1) * p_x1 + ...+ w(M) * p_xM, where p_xm denotes the distribution of Xm (m=1,...,M).

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

%varargin -> num_of_comps, Xs, ds, Ts, ws:
    num_of_comps = nargin-1;
    Xs = cell(num_of_comps,1); %{X1,...,XM}
    ds = zeros(num_of_comps,1); %[d1,...,dM], dimensions
    Ts = zeros(num_of_comps,1); %[T1,...,TM], number of samples
    for k = 1 : num_of_comps
        Xs{k} = varargin{k};
        ds(k) = size(Xs{k},1);
        Ts(k) = size(Xs{k},2);
    end
    ws = varargin{nargin}(:);%column vector

%verification:
    if ~all(ds(1)==ds) %needed: d1=...=dM
        error('The distributions must be of the same dimension.');
    end
    if ~all(ws>0)
         error('The mixture coefficients must be positive.');
    end
    if sum(ws)~=1
        error('The mixture coefficients must sum to one.');
    end
	if length(ws)~=num_of_comps
		error('The number of the weights and the number of Xm-s must be equal.');
    end
    
%take the maximal number of samples (T) for which 'T*w1<=T1, ..., T*wM<=TM', then Tm:=floor(T*wm), i.e. compute the trimmed number of samples:
    T = min(Ts./ws);
    Tws = floor(T.*ws);
    
%mix Xm-s:    
    num_of_samples = sum(Tws);
    cum_Tws = cumsum([1;Tws(1:end-1)]);%1,T_1+1,T_1+T_2+1,... = starting indices of the Xm-s
    mixture = zeros(ds(1),num_of_samples);
    for k = 1 : num_of_comps
        T = Tws(k);
        mixture(:,cum_Tws(k):cum_Tws(k)+T-1) = Xs{k}(:,1:T); %trim the 'irrelevant' part, the result is added to the mixture
    end
    mixture = mixture(:,randperm(num_of_samples)); %permute the samples to obtain the mixture (the weights have been taken into account in the trimming part)
       
