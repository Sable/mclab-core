function y = simsc(theta, din, n, u)
% simsc    - Simulates a time-varying parameters model.
%    y = simsc(theta, din, N, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% N     > number of observations.
% u     > observations of the inputs.
% y     < simulated series.
% This function uses MATLAB's random number generators.
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
econd   = E4OPTION(4);

if nargin < 3,  e4error(3); end

[Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din);
l = size(Phi,1); m = size(Hv,1); 
r = max([size(Gam,2), size(Dv,2)+size(Df,2)]);

if r
   if nargin < 4, e4error(3); end
   if any(size(u) ~= [n m+r]), e4error(11); end
end

[U S U] = svd([Q S; S' R]);

sq = size(Q,1);
sr = size(R,1)+sq;

randn('seed',sum(100*clock));
a = randn(n,sr);
a = U * sqrt(S) * a';

y = zeros(n,1);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(u(:,m+1:m+r))';
   else
      u0 = u(1,m+1:m+r)';
   end

   [x, P0] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x, P0] =  djccl(Phi, E*Q*E', 0);
end

[U S U] = svd(P0);
x = x + U * sqrt(S)*randn(l,1);

for i = 1:n  %main loop
%
  % Construct time varying observer
    H = [u(i,1:m)*Hv Hf]; C = [u(i,1:m)*Cv Cf];
    if r, D = [u(i,1:m)*Dv Df]; end

    if r
    %
       y(i,:) = (H*x + D*u(i,m+1:m+r)' + C*a(sq+1:sr,i))';
       x = Phi * x + Gam * u(i,m+1:m+r)' + E*a(1:sq,i);
    %
    else
    %
       y(i,:) = (H*x + C*a(sq+1:sr,i))';
       x = Phi * x + E*a(1:sq,i);
    %
    end
%
end  % i
