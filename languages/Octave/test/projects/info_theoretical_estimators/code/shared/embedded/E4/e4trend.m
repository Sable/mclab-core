function [trend,season,cycle,irreg,thetat,dint,ixmodes,xhat] = e4trend(theta,din,y,toinnov)
% e4trend - Decomposes a vector of time series into trend, seasonal, cycle and irregular components
%    [trend,season,cycle,irreg,thetat,dint,ixmodes,xhat] = e4trend(theta,din,y,toinnov)
% theta   > parameter vector.
% din     > matrix which stores a description of the model dynamics.
% y       > matrix of time series.
% toinnov > logical flag:
%             toinnov=1, transforms the model to obtain exact estimates
%             toinnov=0 (default) not transforms
% trend   < smoothed estimates of trend (nonstationary) components
% season  < smoothed estimates of seasonal components
% cycle   < smoothed estimates of cycle (stationary) components
% irreg   < smoothed estimates of the irregular components
% thetat, dint < theta-din values corresponding to the block-diagonal SS model
% ixmodes < indexes for the different states:
%           1 trend states
%           2 seasonal states
%           3 cycle states
% xhat    < smoothed states of the block-diagonal SS model%
% 21/4/99

% Copyright (C) 1999 Jaime Terceiro
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
zeps = .0001;
one = .99;
if nargin < 4, toinnov = 0; end

if sum(sum(isnan(y))) > 0, missing = 1; else missing = 0; end
[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta,din);

if toinnov & ~innov(1),[E,Q] = sstoinn(Phi, E, H, C, Q, S, R); end
[Phit, iT, T, eigen] = bkdiag(Phi);
Et = T*E;
Ht = H*iT;
if r, Gamt = T*Gam; else, Gamt = []; end

if E4OPTION(5)
   Q = cholp(Q)';
   R = cholp(R)';
end

if innov(1) | toinnov
   [thetat,dint] = ss2thd(Phit,Gamt,Et,Ht,D,eye(m),Q);
else
   [thetat,dint] = ss2thd(Phit,Gamt,Et,Ht,D,C,Q,S,R);
end

if n
   per = zeros(n,1);
   a = real(eigen);
   b = abs(imag(eigen));
   k = abs(a) < zeps & b > zeps;
   per(k) = 4*ones(sum(k),1);
   k2 = b < zeps;
   k3 = sign(a) < 0 & k2;
   per(k3) = 2*ones(sum(k3),1);
   k3 = ~(k | k2);
   if sum(k3)
      at = atan(b(k3)./a(k3));
      k2 = at < 0;
      at(k2) = at(k2) + pi;
      per(k3) = 2*pi./at;
   end

   ktrend = real(eigen) > one & ~per;
   sper = s./(1:(floor(s/2)));
   kseason = ~ones(n,1);

   for i=1:size(sper,2)
       kseason = kseason | (per - zeps < sper(i) & per + zeps > sper(i));
   end

   kcycle = ~(kseason | ktrend);
else
   ktrend = []; kcycle = []; kseason = [];
end


if missing
   [zhat, pz, xhat, px] = fismiss(thetat,dint,y);
   if ~isempty(D) 
      irreg = zhat - xhat*Ht' - y(:,m+1:m+r)*D';
   else
      irreg = zhat - xhat*Ht';
   end
else
   [xhat, px, irreg] = fismod(thetat,dint,y);
end
N = size(y,1);
if ~sum(ktrend), trend = zeros(N,m); else, trend = xhat(:,ktrend)*Ht(:,ktrend)'; end
if ~sum(kseason), season = zeros(N,m); else, season = xhat(:,kseason)*Ht(:,kseason)'; end
if ~sum(kcycle), cycle = zeros(N,m); else, cycle = xhat(:,kcycle)*Ht(:,kcycle)'; end
ixmodes = zeros(size(Phi,1),1);
ixmodes(ktrend) = ones(sum(ktrend),1);
ixmodes(kseason) = ones(sum(kseason),1)*2;
ixmodes(~ixmodes) = ones(sum(~ixmodes),1)*3;
