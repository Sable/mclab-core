function [P,nonstat] = lyapunov(Phi, Q, one)
% lyapunov - Solves the Lyapunov equation P = Phi*P*Phi' + Q
%    [P, nonstat] = lyapunov(Phi, Q, one)
% one, nonstat > If there is any eigenvalue greater than or equal to
% 'one', the system is not solved and nonstat takes value 1 if the
% system is partially non-stationary or 2 if all its poles are on or
% outside the unitary circle.
%
% 6/3/97

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

if nargin < 3, one = E4OPTION(12); end

% Usually Phi matrix is companion an then it's an unreduced lower Hessenberg form
% For this reason the RSF of Phi' is obtained

[U, Phi] = schur(Phi');
[U, Phi] = rsf2csf(U,Phi);
Phi = Phi';

n = size(Phi,1);   
nuroots = size(find(abs(diag(Phi)) >= one),1);
P = zeros(n);

if nuroots > 0
   if nuroots == n
      nonstat = 2;
   else
      nonstat = 1;
   end
   return;
end

nonstat = 0;
n = size(Phi,1);   

Q = U' * Q * U;

for j = 1:n
    for i = j:n
    %
        d = 1 - Phi(i,i)*Phi(j,j)';
        suma = Phi(i,1:i) * P(1:i,1:j) * Phi(j,1:j)' + Q(i,j);
        if (abs(suma) < eps)
           P(i,j) = 0;
        elseif (abs(d) < eps)
           e4error(19);
        else
           P(i,j) = suma / d;
           P(j,i) = P(i,j)';
        end
    %
    end
end

P = real(U * P * U');