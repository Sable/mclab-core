function [yl, ys] = lagser(y, ll)
% lagser   - Generates lags and leads from a data matrix.
%    [yl, ys] = lagser(y, ll)
%  y   > nxk series.
%  ll  > 1xl lags-leads list (>0 lags, <0 leads).
%  yl  < (n-ml)x(mxl) lagged and/or lead series, where ml=maxlag+abs(maxlead).
%  ys  < (n-ml)x(m) original series with the last ml observations supressed.
% From y = [y1 y2 ... ym], ll = [1, ... ml]
% yl=[y1(-1) ... ym(-1) y1(-2) ... ym(-2) ... y1(-ml) ... ym(-ml)]
% if ll is scalar, yl = [y1(-ll) ... ym(-ll)].
% 
% 6/3/97

% Copyright (C) 1997 Jaime Terceiro
% 
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2, or (at your option)
% any later version.
% 
% This program is distributed in the hope that it will be useful, but
% WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% General Public License for more details. 
% 
% You should have received a copy of the GNU General Public License
% along with this file.  If not, write to the Free Software Foundation,
% 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

if nargin < 2, e4error(3); end
[n, m] = size(y);
ll = sort(fix(ll(:))); nl = size(ll,1);
if any(size(y,1) <= 0), e4error(7); end
if max(abs(ll)) >= n, e4error(17); end
if isempty(ll), yl = []; ys = []; return; end

yl = zeros(n, m*nl);
for i=1:nl
    l = abs(ll(i)); cols = (i-1)*m+1:i*m;
    if ll(i) >= 0 % LAG
        yl(l+1:n, cols) = y(1:n-l, :);
    else % LEAD
        yl(1:n-l, cols) = y(l+1:n, :);
    end
end

% Get only valid rows for ys and yl
if nargout == 2
    minl = min(ll); maxl = max(ll);
    if minl >= 0 
        okr = maxl+1:n;
    elseif maxl <= 0
        okr = 1:n-abs(minl);
    else
       okr = abs(maxl)+1:n+minl;
    end
    yl = yl(okr,:);
    ys = y(okr,:);
end
