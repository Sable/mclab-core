function [theta, din, thetalab] = arma2thd(FR,FS,AR,AS,V,s,G,r)
% arma2thd - Converts a VARMAX model to the THD format.
%    [theta, din, lab] = arma2thd(FR, FS, AR, AS, V, s, G, r)
% The input arguments corresponding to the model:
%   (I + FR1·B + ... +FRp·B^p)(I + FS1·B^s + ... + FSps·B^ps·s) y(t) = 
%   (G0 + G1·B + ... + Gt·B^l) u(t) +
%   (I + AR1·B + ... + ARq·B^q)(I + AS1·B^s + ... + ASqs·B^qs·s) a(t)
% are:
%   FR = [FR1 | ... | FRp]  FS = [FS1 | ... | FSps]
%   G  = [G0 | G1 | ... | Gg]
%   AR = [AR1 | ... | ARq]  AS = [AS1 | ... | ASqs]
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

% DIN STRUCTURE OF AN ARMAX MODEL
% din(1:H_D) : common header
% din(H_D+1:H_D+6): private header
%    din(H_D+1): diag flag for V
%    din(H_D+2:H_D+6): p, P, q, Q, g
% din(H_D+7:?) : vectorized matrices

if (nargin < 5) | (nargin > 6 & nargin ~= 8), e4error(3); end
if (nargin < 6), s = 1; end
s = round(s); if (s <= 0), s = 1; end
if any(any(isnan(V))), e4error(5); end

if (nargin < 7), r= 0; G = []; end
if isempty('G'), r = 0; end

diagv = any(size(V) == 1);
if diagv, V = diag(V(:));  diagv = 1; end
m = size(V,1);
if diagv, V = V + tril(ones(m)*NaN,-1) + triu(ones(m)*NaN,1); end

if (m <= 0), e4error(4); end
if any(rem([size(FR) size(AR) size(FS) size(AS)], m))
   e4error(5);
end
armax  = size(FR,2)/m;  mamax = size(AR,2)/m;
sarmax = size(FS,2)/m; smamax = size(AS,2)/m;
inpmax = 0;
if r
   if rem(size(G,2),r) | size(G,1) ~= m, e4error(5);
   else inpmax = size(G,2)/r -1; end
end

din = zeros(6,1);
if (size(V,2) == 1), din(1) = 1; end
din(2:6) = [armax; sarmax; mamax; smamax; inpmax];
k = max([armax+sarmax*s, mamax+smamax*s, inpmax]);

vars  = e4strmat('FR','FS','AR','AS','G','V');
issym = zeros(6,1);
issym(6) = 1;
init = ones(6,1);
init(5) = 0;
blocks = [ones(4,1)*m; r; 0];

theta = [];
thetalab = [];

for i = 1:6
    [theta0, din0, labl] = e4vec(eval(vars(i,:)), deblank(vars(i,:)), issym(i), blocks(i),init(i));
    theta = [theta;theta0];
    if size(labl,1) > 0, thetalab = e4strmat(thetalab, labl); end
    din = [din;size(theta0,1);din0];
end

if size(theta,1) < 1, e4error(5); end
thetalab = thetalab(2:size(thetalab,1), :);
din0 = e4sthead(min(k,1), m, r, s, k*m, size(theta,1), 0, [], [1 size(din,1)-size(din0,1)], [size(din,1); 6]);
din = [din0;din];

