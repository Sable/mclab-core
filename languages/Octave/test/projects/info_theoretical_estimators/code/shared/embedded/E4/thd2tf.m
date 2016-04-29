function [F, A, V, W, D] = thd2tf(theta, din)
% thd2tf   - Converts a THD model to the equivalent transfer function (TF) model.
%    [F, A, V, W, D] = thd2tf(theta, din)
% For the model:
%   y(t) = [w1(B)/d1(B)]u1(t) + ... + [wr(B)/dr(B)]ur(t) + N(t)
%   F(B) N(t) = A(B) e(t)
% returns:
%   F = [1 f1 ... fk]         A = [1 a1 ... ak] 
%   W = [w1(B); ...; wr(B)]   D = [d1(B); ...; dr(B)]
%   V = V[e(t)]
% The procedure also checks that V is positive definite.
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

[FR, FS, AR, AS, V, W, D] = thd2tf2(theta, din);
[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if type ~= 3, e4error(14); end
ze = [zeros(1,s-1) 1];

F = conv([1 FR],[1 kron(FS,ze)]);
A = conv([1 AR],[1 kron(AS,ze)]);
D = [ones(r,1) D];