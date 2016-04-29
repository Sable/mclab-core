function y = simgarch(theta, din, n, u)
% simgarch - Simulates a model with GARCH structure in the variance of the error.
%    y = simgarch(theta, din, N, u)
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% N      > number of observations for the simulated series.
% u      > input series (only if the model includes inputs).
% y      < simulated series.
% This function uses MATLAB's random number generators.
%
%  28/4/97

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
econd   = E4OPTION(4);

if nargin < 3,  e4error(3); end

[H_D, type] = e4gthead(din);
is_diag = rem(type,100); % uncorrelated errors

[Phi, Gam, E, H, D, C, Q, Phig, Gamg, Eg, Hg, Dg] = garch2ss(theta, din);
l = size(Phi,1); m = size(H,1); 
r = max([size(Gam,2), size(D,2)]);
rg= max([size(Gamg,2), size(Dg,2)]);

if r
   if nargin < 4, e4error(3); end
   if any(size(u) ~= [n r+rg]), e4error(11); end
end

if is_diag
   Q0 = diag(Q);
   Q00 = Q - diag(Q0);
else, Q0 = vech(Q); end
x0g = zeros(size(Phig,1),1);

randn('seed',sum(100*clock));
a = randn(m,n);

y = zeros(n,m);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(u(:,1:r))';
   else
      u0 = u(1,1:r)';
   end

   [x, P0] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x, P0] =  djccl(Phi, E*Q*E', 0);
end

[V S V] = svd(P0);
x = x + V * sqrt(S)*randn(l,1);

for i = 1:n  %v main loop
%
    if rg
       Q1 = Q0 + Hg*x0g + Dg*u(i,r+1:r+rg)';
    else
       Q1 = Q0 + Hg*x0g;
    end

    if is_diag, Q = diag(Q1) + Q00; else, Q = vech2m(Q1,m); end

    U = cholp(Q);
    v = U' * a(:,i);
  
    if r
    %
       y(i,:) = (H*x + D*u(i,1:r)' + C*v)';
       x = Phi * x + Gam * u(i,1:r)' + E*v;
    %
    else
    %
       y(i,:) = (H*x + C*v)';
       x = Phi * x + E*v;
    %
    end

    if is_diag
       z1g  = diag(v*v') - Q1;
    else
       z1g  = vech(v*v') - Q1;
    end

    if rg
       x0g  = Phig*x0g + Gamg*u(i,r+1:r+rg)' + Eg*z1g;
    else
       x0g  = Phig*x0g + Eg*z1g;
    end
%
end  % i