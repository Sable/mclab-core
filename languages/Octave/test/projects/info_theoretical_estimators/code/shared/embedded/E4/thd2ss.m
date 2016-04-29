function [Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din)
% thd2ss   - Returns the SS matrices from model in THD format (all models).
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din)
% The SS model formulation is:
%    x(t+1) = Phi·x(t) + Gam·u(t) + E·w(t)
%    z(t)   = H·x(t)   + D·u(t)   + C·v(t)
%    V(w(t) v(t)) = [Q S; S' R]
% In composite models or models without exogenous variables Gam = [], D = [].
%
% 29/4/97

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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if userflag
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2own(theta, din, userf(1,:));
   return;
end

if type < 4 % simple model
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2sss(theta, din);
   return;
end

if type == 4 % echelon
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssh(theta, din);
   return;
end

if type == 7
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssss(theta, din);
   return;
end

if fix(type/10) == 2 % nested
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssn(theta, din);
   return;
end

if fix(type/10) == 3 % components
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssc(theta, din);
   return;
end

e4error(14);