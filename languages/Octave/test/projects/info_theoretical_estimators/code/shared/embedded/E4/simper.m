function y = simper(theta, din, n, u)
% simper  - Simulates a periodic model in THD representation.
%    y = simper(theta, din, N, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% N     > number of observations of the simulated series.
% u     > input series (only if the model includes inputs).
% y     < simulated series.
% This function uses MATLAB's random number generators.
%
%  7/3/97

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
if nargin < 3,  e4error(3); end
scaleb  = E4OPTION(2);
econd   = E4OPTION(4);

per1 = 1;

[kn, Phi1, Gam1, E1, H1, D1, Q1] = thd2ssp(theta, din);
[H_D, type, m, r, s] = e4gthead(din);
if r
   if nargin < 4, e4error(3); end
   if any(size(u) ~= [n r]), e4error(11); end
end

kl = cumsum([1;kn]);
kl = [kl(1:s)'; (kl(2:s+1)-1)'];
km = [[1:m:(s-1)*m+1]; [m:m:s*m]];
kn = [kn(s);kn];
l  = kn(per1);

Phi0 = eye(l);
Q0   = zeros(l);
Gam0 = zeros(l,r);
U1  = zeros(size(Q1));

k = per1-1;

for i=1:s
    if k == 0, k = s; end
    Q0 = Q0 + Phi0*E1(kl(1,k):kl(2,k),:)*Q1(km(1,k):km(2,k),:)*E1(kl(1,k):kl(2,k),:)'*Phi0';
    if r, Gam0 = Gam0 + Phi0*Gam1(kl(1,k):kl(2,k),:); end   
    Phi0 = Phi0*Phi1(kl(1,k):kl(2,k),1:kn(k));
    k = k-1;

    if scaleb, U = cholp(Q1(km(1,i):km(2,i),:), abs(Q1(km(1,i):km(2,i),:)))';
    else       U = cholp(Q1(km(1,i):km(2,i),:))'; end
    U1(km(1,i):km(2,i),:) = U;
end

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(u)';
   else
      u0 = u(1,:)';
   end

   [x, P0] =  djccl(Phi0, Q0, 0, Gam0*u0);
%
else
   [x, P0] =  djccl(Phi0, Q0, 0);
end

randn('seed',sum(100*clock));
a = randn(n,m)';

y = zeros(n,m);

[U S U] = svd(P0);
x = x + U * sqrt(S)*randn(l,1);

k = per1;

for i = 1:n  % main loop
%
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    e   = U1(km(1,k):km(2,k),:)*a(:,i);

    if r
    %
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);

       y(i,:) = (H*x + D*u(i,:)' + e)';
       x = Phi * x + Gam*u(i,:)' + E*e;
    %
    else
    %
       y(i,:) = (H*x + e)';
       x = Phi * x + E*e;
    %
    end

    if k == s, k = 1; else, k=k+1; end
%
end  % i
