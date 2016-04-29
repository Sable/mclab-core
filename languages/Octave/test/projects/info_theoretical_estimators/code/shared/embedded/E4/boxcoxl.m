function [f] = boxcoxl(lambda, y)
% boxcoxl  - Evaluates the likelihood function for a value of the
% lambda parameter of the Box-Cox transformation under normality.
%    f = boxcoxl(lambda, y)
% The transformation is defined by:
%   x(l) = / ln(y+m)            l = 0
%          \ [(y+m)^l -1]/l     l~= 0
%  m = 0                   if y > 0
%  m = abs(min(y))+toly    if y <= 0
%
% 5/3/97

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

global E4OPTION

if nargin < 2, e4error(3); end
if any(size(lambda) ~= 1), e4rror(32); end
[n,m]   = size(y);
if m > 1, e4error(6); end
if n <=1, e4error(7); end
toly = E4OPTION(11);

miny = min(y);
if miny <= 0, y = y + abs(miny) + toly; end

if lambda,  yt = (y.^lambda - 1)/lambda;
else        yt = log(y);
end

f  = (n/2)*log(var(yt)) + (1-lambda)*sum(log(y));
