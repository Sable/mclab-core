function [theta, din, thetalab] = tf2thd(FR,FS,AR,AS,V,s,W,D)
% tf2thd   - Converts a transfer function (TF) model to the THD format.
%    [theta, din, lab] = tf2thd(FR, FS, AR, AS, V, s, W, D)
% The input TF is:
%  y(t) = [w1(B)/d1(B)]u1(t) + ... + [wr(B)/dr(B)]ur(t) + N(t)
%  FR(B)·FS(B) N(t) = AR(B)·AS(B) e(t)
% where FR, FS, AR, AS are the polynomials of an univariate ARMA model
%  W  = [w1(B); ...; wr(B)]  D  = [d1(B); ...; dr(B)]
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

% DIN STRUCTURE OF A TF MODEL
% din(1:H_D) : common header
% din(H_D+1:H_D+6): private header
%    din(H_D+1:H_D+6): p, P, q, Q, gw, gd
% din(H_D+7:?) : vectorized matrices

if (nargin < 7), e4error(3); end
if (nargin < 8), D = []; end
s = round(s); if (s <= 0), s = 1; end
if any(size(V) ~= 1), e4error(4); end
if isnan(V), e4error(5); end

m = 1; diagv = 1;
r = size(W,1);
if r == 0, e4error(24); end

if size(D,1) == 0, D = NaN*ones(r,1); end
if r ~= size(D,1), e4error(5); end

if any([size(FR,1) size(FS,1) size(AR,1) size(AS,1)] > 1), e4error(5); end

armax   = size(FR,2);     mamax = size(AR,2);
sarmax  = size(FS,2);    smamax = size(AS,2);
inpmax1 = size(W,2) -1; inpmax2 = size(D,2);

kr = zeros(r,2);

for i=1:r
    k = max(find(~isnan(W(i,:))));
    if isempty(k), e4error(5); end
    kr(i,1) = k-1;
    k = max(find(~isnan(D(i,:))));
    if ~isempty(k), kr(i,2) = k; end
end

din = zeros(6,1);
din(1:6) = [armax; sarmax; mamax; smamax; inpmax1; inpmax2];
k = sum(kr(:,2));
k = max([mamax+smamax*s;armax+sarmax*s+[0; kr(:,1)-kr(:,2)]])+k;

D = D';
W = W';
vars  = e4strmat('FR','FS','AR','AS','W','D','V');
init = ones(7,1);
blocks = zeros(7,1);
blocks(5:6) = [1;1];

theta = [];
thetalab = [];

for i = 1:7
    [theta0, din0, labl] = e4vec(eval(vars(i,:)), deblank(vars(i,:)), 0, blocks(i),init(i));
    theta = [theta;theta0];
    if size(labl,1) > 0, thetalab = e4strmat(thetalab, labl); end
    din = [din;size(theta0,1);din0];
end

if size(theta,1) < 1, e4error(5); end
thetalab = thetalab(2:size(thetalab,1), :);
din0 = e4sthead(3, 1, r, s, k, size(theta,1), 0, [], [1 size(din,1)-size(din0,1)], [size(din,1); 6]);
din = [din0;din];