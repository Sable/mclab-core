function [yf, Bf] = foreper(theta, din, z, T, per1, u)
% foreper - Forecasts the endogenous variables of a periodic model in THD format.
%    [yf, Bf] = foreper(theta, din, z, k, per1, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% k     > number of forecasts.
% per1  > initial period.
% u     > forecasts of the exogenous variables (if any).
% yf    < (kxm) matrix of forecasts.
% Bf    < forecasts variances stored as a column of k (mxm) blocks.
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

if nargin < 4,  e4error(3); end
if nargin < 6,  per1 = 1; end

scaleb  = E4OPTION(2);

zeps = E4OPTION(15);

n=size(z,1);

[kn, Phi1, Gam1, E1, H1, D1, Q1] = thd2ssp(theta, din);
[H_D, type, m, r, s] = e4gthead(din);
if size(z,2) ~= m+r, e4error(11); end

iQ1 = zeros(size(Q1));
U1  = zeros(size(Q1));
Phib1 = zeros(size(Phi1));

kl = cumsum([1;kn]);
kl = [kl(1:s)'; (kl(2:s+1)-1)'];
km = [[1:m:(s-1)*m+1]; [m:m:s*m]];
kn = [kn(s);kn];
l  = kn(per1);

Phi0 = eye(l);
Q0   = zeros(l);
Gam0 = zeros(l,r);

for i=1:s
    if scaleb, U = cholp(Q1(km(1,i):km(2,i),:), abs(Q1(km(1,i):km(2,i),:)));
    else       U = cholp(Q1(km(1,i):km(2,i),:)); end
    iQ1(km(1,i):km(2,i),:) = (eye(size(U))/U)/U';
    U1(km(1,i):km(2,i),:) = U;
    Phib1(kl(1,i):kl(2,i),1:kn(i)) = Phi1(kl(1,i):kl(2,i),1:kn(i)) - E1(kl(1,i):kl(2,i),:)*H1(km(1,i):km(2,i),1:kn(i));
end

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
x0 = zeros(l,1);

k = per1;

for t = 1:n
%
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    Phib= Phib1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    iQ = iQ1(km(1,k):km(2,k),:);

    if r
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);
       z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
       x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1;
    else
       z1  = z(t,:)' - H*x0;
       x0  = Phi*x0 + E*z1;
    end
 
    HPhi= H*Phibb0;
    WW  = WW + HPhi'*iQ*HPhi;
    WZ  = WZ + HPhi'*iQ*z1;
    Phibb0 = Phib*Phibb0;

    if k == s, k = 1; else, k=k+1; end
end

if any(isnan([WW WZ])) | any(isinf([WW WZ]))
   B1, WW, WZ
   e4error(25);
end

x0 = x0 + Phibb0 * pinv(WW)*WZ;

P0 = zeros(kn(k));
yf = zeros(T,m);
Bf = zeros(T*m,m);

for t = 1:T  % forecast loop
%
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    Q   = Q1(km(1,k):km(2,k),:);

    B1  = H*P0*H' + Q;

    Bf((t-1)*m+1:t*m,:) = B1;
    
    if r
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);
       yf(t,:) = (H*x0 + D*u(t,:)')';
       x0  = Phi*x0 + Gam*u(t,:)';
    else
       yf(t,:) = (H*x0)';
       x0  = Phi*x0;
    end

    P0  = Phi*P0*Phi' + E*Q*E';

    if k == s, k = 1; else, k=k+1; end
end
