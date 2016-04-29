function f = e4prees4(theta, din, KB, ij)
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

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);

n = size(Phi,1);
m = size(H,1);

i = ij(1);

CRCt = C*R*C'; ESCt = E*S*C'; EQEt = E*Q*E';
[P0, nonstat] = lyapunov(Phi,EQEt);

if nonstat, P0 = zeros(n); end

for j=1:i+1
    B1  = H*P0*H' + CRCt;
    U = cholp(B1);
    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    Phib= Phi - K1*H;
    P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
end

f = norm(KB-[K1;B1],'fro');

