% The following function computes the cross information potential between
%               two vectors X and Y using a double exponential kernel
%
% Input:   X and Y real valued COLUMN vectors of same length
%				kSize is a scalar for the kernel size, i.e. K(x,y) =  exp(-|x-y|/kSize)
%
% Output: cip contains the cross information potential
%
% Default:  kSize = 1.
%
% Author: IL "Memming" Park (memming@cnel.ufl.edu)	Date: 15.07.2009

function [cip] = cipexp(X,Y,kSize)

if nargin == 2
	kSize = 1;
end

X = X ./ kSize; Y = Y ./ kSize;

X_sort = sort(X); Y_sort = sort(Y);

X_pos_exp = exp(X_sort); X_neg_exp = exp(-X_sort);
Y_pos_exp = exp(Y_sort); Y_neg_exp = exp(-Y_sort);

Y_pos_cum_sum_exp  = cumsum(Y_pos_exp);
Y_neg_cum_sum_exp  = flipud(cumsum(flipud(Y_neg_exp)));

Y_sort = [Y_sort; Inf];
cip = 0;
yidx = 0; % no y is smaller than x yet
for xidx = 1:length(X)
    x = X_sort(xidx);

    % at the end of the next Y_sort(yidx) >= X_sort(xidx)
    while Y_sort(yidx+1) <= x
        yidx = yidx + 1;
    end

    if yidx == 0
        cip = cip + Y_neg_cum_sum_exp(1) * X_pos_exp(xidx);
    elseif yidx == length(Y)
        cip = cip + Y_pos_cum_sum_exp(end) * X_neg_exp(xidx);
    else
        cip = cip + Y_pos_cum_sum_exp(yidx) * X_neg_exp(xidx) + Y_neg_cum_sum_exp(yidx+1) * X_pos_exp(xidx);
    end
end
cip = cip / (length(X) * length(Y));