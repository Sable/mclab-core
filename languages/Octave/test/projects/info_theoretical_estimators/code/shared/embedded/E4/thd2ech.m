function [F, A, V, G] = thd2ech(theta, din)
% thd2ech - Converts a THD model to ECHELON notation.
%    [F, A, V, G] = thd2str(theta, din)
% For the model:
%   F(B)y(t) = G(B)u(t) + A(B)e(t)
% this function returns the matrices:
%   F = [F0 F1 F2 ... Fk], A = [A0 A1 A2 ... Ak],
%   G = [G0 G1 G2 ... Gk], V = V(e(t))
%   k = max(p, q, g)
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

[H_D, type, m] = e4gthead(din);
[F, A, V, G] = thd2str(theta, din(H_D+m+1:size(din,1)));
A(1:m,1:m) = F(1:m,1:m);