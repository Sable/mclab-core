function h = kdpee (arr, mins, maxs, zcut)
% function h = kdpee (arr)
% function h = kdpee (arr, mins, maxs)
% function h = kdpee (arr, mins, maxs, zcut)
% 
% Entropy estimator using k-d partitioning. Returns a value in nats.
%
%   "arr" is a matrix of data points, 1 row per datum.
%
% The algorithm can run on data with known OR unknown support.
%   (Support is assumed to be extent of data in the latter case.)
% If the support is known, pass it in as vectors "mins" and "maxs".
% If the support is not known simply don't supply "mins" or "maxs".
%
%   kdpee is free software: you can redistribute it and/or modify
%   it under the terms of the GNU General Public License as published by
%   the Free Software Foundation, either version 3 of the License, or
%   (at your option) any later version.


if (nargin < 4)
	zcut = 1.96;
end

n = size(arr, 1);    % num data points
d = size(arr, 2);    % dimensionality

%if (n < (2^d))
%	fprintf(1, 'kdpee() warning: n < 2^d, meaning you might not have enough data to make an entropy estimate for the dimensionality\n');
%end

if(nargin < 3)
    % Take limits from data, should have tendency to underestimate support
    %           but seems to work well
	mins = min(arr, [], 1);
	maxs = max(arr, [], 1);
end


h = kdpeemex(arr, mins, maxs, zcut);
