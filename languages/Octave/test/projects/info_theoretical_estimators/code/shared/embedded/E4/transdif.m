function [yt] = transdif(y,lambda,d,ds,s)
% transdif - Differences and performs the Box-Cox transformation for a series set.
%    yt = transdif(y,lambda, d, ds, s)
%  y      > (nxk) data matrix.
%  lambda > parameter of the Box-Cox transformation.
%  d      > number of regular differences (1-B)^d.
%  ds     > (Sx1) matrix containing the number of seasonal differences
%           (1-B^s(S))^ds(S) to be applied to each series.
%  s      > (rx1) matrix containing the seasonal periods.
%  yt     < (n-d-sum(ds*s))xk matrix of transformed data.
%
% 7/3/97

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

if nargin == 1, yt = y; return; end
if nargin < 5
   s = 1;
   if nargin < 4
     ds = 0; dst = ds;
     if nargin < 3, d = 0; end
   end
end
s = fix(s(:)); ds = fix(ds(:));

if any(ds>0)
    if any(s < 1) | any(ds <= 0), e4error(11); end 
    dst = sum(ds.*s);
else
    ds = 0; dst = ds; s = 1;
end

[n, m] = size(y);
if n < d+dst+1, e4error(7); end
yt = zeros(n-d-dst,m);

toly = E4OPTION(11);
rpol = [1]; spol = [1];
if d
    regpol = [1 -1];
    for j=1:d,  rpol = conv(regpol, rpol);  end
end
if ds
    for j=1:size(ds,1)
        seaspol= [1 zeros(1,s(j)-1) -1];
        for k=1:ds(j), spol = conv(seaspol, spol); end
    end
end
difp = conv(rpol,spol);

if lambda ~= 1.0
    miny = min(y);
    ndx  = (miny <= 0);
    if any(ndx)
        y(:, ndx) = y(:, ndx) + ones(n,1)*abs(miny(ndx)) + toly;
    end
    if   lambda==0.0, yt1 = log(y);
    else              yt1 = (y.^lambda - 1)/lambda;
    end
else
    yt1 = y;
end

%%%% Cambiado el 29/1/98
if d+dst
    diford = find(difp ~= 0);
    for i=1:m
       [yl, ys] = lagser(yt1(:,i), diford-1);
       yt(:,i)  = yl*difp(diford)';
    end
else
    yt = yt1;
end
