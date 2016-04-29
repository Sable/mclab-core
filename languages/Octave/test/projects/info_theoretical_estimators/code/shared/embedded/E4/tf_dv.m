function [F, dF, A, dA, V, dV, W, dW, D, dD] = tf_dv(theta,din,di)
% tf_dv    - Computes the partial derivatives of the parameter matrices
% in a transfer function (TF) model with respect to the i-th parameter
% of the theta vector.
%   [F, dF, A, dA, V, dV, W, dW, D, dD] = tf_dv(theta, din, i)
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

if (di < 1) | (di > size(theta,1)), e4error(2); end

[FR, FS, AR, AS, V, W, D] = thd2tf2(theta, din,1);
dtheta = zeros(size(theta,1),1);
dtheta(di) = 1;
[dFR, dFS, dAR, dAS, dV, dW dD] = thd2tf2(dtheta, din, 1);

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if type ~=3, e4error(14); end

ze = [zeros(1,s-1) 1];

F = conv([1 FR],[1 kron(FS,ze)]);
dF = conv([0 dFR],[1 kron(FS,ze)]) + conv([1 FR],[0 kron(dFS,ze)]);
A = conv([1 AR],[1 kron(AS,ze)]);
dA = conv([0 dAR],[1 kron(AS,ze)]) + conv([1 AR],[0 kron(dAS,ze)]);
D = [ones(r,1) D];
dD = [zeros(r,1) dD];

if ~E4OPTION(5)
   if min(eig(V)) <= 0
      Vu = cholp(V);
      V  = Vu'*Vu;
   end
else
    dV = dV * V' + V * dV';
    V = V*V';  
end