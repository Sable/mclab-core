function [R, mu]  = cholp( H, S)
% cholp    - Computes the perturbed Cholesky factor R for the symmetric
% matrix H, such that R'*R = H + mu*I.
%    [R, mu]  = cholp(H, S)
% This function is identical to HESSP, but their tolerances may be adapted to
% work with other matrices (mainly variance-covariance matrices).
%
% 11/3/97

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

% MODELHESS. Dennis & Schnabel, pp. 315

scaling = 0;
if nargin == 2, 
    scaling = 1;
    if   any(size(S) == 1), S = S(:);
    else S = diag(S); end
end

[n,m] = size(H);
if n ~= m
   e4warn(14);
   n  = min(n,m);
   H  = H(1:n,1:n);
end

if scaling
   if size(S,1) >= n
       S = S(1:n,:);
       d = diag(1./S);
       H = d*H*d';
   else
       scaling = 0;
   end
end

sqrteps = sqrt(eps);
maxdiag = max(diag(H));
mindiag = min(diag(H));
maxpos  = max(0.0, maxdiag);

if mindiag <= sqrteps*maxpos
   mu = 2*(maxpos-mindiag)*sqrteps - mindiag;
   maxdiag = maxdiag + mu;
else
   mu = 0.0;
end

maxoff = 0.0;
if n > 1;
   maxoff1    = zeros(n-1,1);
   for i=1:n-1, maxoff1(i) = max(abs(H(i,i+1:n))); end
   maxoff = max(maxoff1);
end

if (maxoff*(1+2*sqrteps)) > maxdiag
   mu = mu + (maxoff-maxdiag) + (2*sqrteps*maxoff);
   maxdiag = maxoff*(1+2*sqrteps);
end
if maxdiag == 0
   mu = 1.0;
   maxdiag = 1.0;
end
if mu > 0
   H = H + mu*eye(n,n);
end
maxoffl = sqrt(max(maxdiag, maxoff/n));

[L, maxadd] = choldc(H, maxoffl);

if maxadd > 0
   maxev = H(1,1);
   minev = H(1,1);
   for i=1:n
       offrow = sum(abs(H(i,i+1:n))) + sum(abs(H(1:i-1,i)));
       maxev  = max(maxev, H(i,i) + offrow);
       minev  = min(minev, H(i,i) - offrow);
   end
   sdd   = (maxev - minev)*sqrteps - minev;
   sdd   = max(sdd, 0.0);
   mu    = min(maxadd, sdd);
   H     = H + mu*eye(n,n);
   [L, maxadd] = choldc(H, 0.0);
end

if scaling
   d = diag(S);
   L = L*d;
end

R  = L';  % Same return as MATLAB's chol function
