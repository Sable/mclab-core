function y = simmod(theta, din, n, u)
% simmod   - Simulates a model in THD representation.
%    y = simmod(theta, din, N, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% N     > number of observations of the simulated series.
% u     > input series (only if the model includes inputs).
% userf > user function (only for user models).
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
econd   = E4OPTION(4);

if nargin < 3,  e4error(3); end

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
l = size(Phi,1); m = size(H,1); r=max([size(Gam,2), size(D,2)]);
if r
   if nargin < 4, e4error(3); end
   if any(size(u) ~= [n r]), e4error(11); end
end

[U S U] = svd([Q S; S' R]);

sq = size(Q,1);
sr = size(R,1)+sq;

randn('seed',sum(100*clock));
a = randn(n,sr);
a = U * sqrt(S) * a';

y = zeros(n,m);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(u)';
   else
      u0 = u(1,:)';
   end

   [x, P0] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x, P0] =  djccl(Phi, E*Q*E', 0);
end

[U S U] = svd(P0);
x = x + U * sqrt(S)*randn(l,1);

for i = 1:n  % main loop
%
    if r
    %
       y(i,:) = (H*x + D*u(i,:)' + C*a(sq+1:sr,i))';
       x = Phi * x + Gam * u(i,:)' + E*a(1:sq,i);
    %
    else
    %
       y(i,:) = (H*x + C*a(sq+1:sr,i))';
       x = Phi * x + E*a(1:sq,i);
    %
    end
%
end  % i
