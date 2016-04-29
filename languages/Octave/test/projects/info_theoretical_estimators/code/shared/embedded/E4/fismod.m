function [xvect, pvect, e] = fismod(theta, din, z)
% fismod   - Fixed interval smoother.
%    [xhat, px, e] = fismod(theta, din, z)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% xhat  < E[x(t)/T]
% px    < Var(x(t) - x(t/T))
% e     < innovations
%
% 11/3/97

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

scaleb = E4OPTION(2);
econd   = E4OPTION(4);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
n = size(z,1);
m = size(H,1);
r = size(Gam,2);
CRCt=C*R*C';  ESCt=E*S*C';  EQEt=E*Q*E';

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if size(z,2) ~= m+r, e4error(11); end

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(z(:,m+1:m+r))';
   else
      u0 = z(1,m+1:m+r)';
   end

   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, EQEt, 0, Gam*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, EQEt, 0);
end

if ~nonstat, iSigm = pinv(Sigm); end

l = size(Phi,1);
Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
V = eye(l);
P0 = zeros(l);
xvect = zeros(n, l+m ); pvect = zeros(n*l, 2*(l+m));
bvect = zeros(n*m, m);

for t = 1:n  % filter loop
%
    if r, z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
    else  z1  = z(t,:)' - H*x0; end

    B1  = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    Y = H*Phibb0;

    % save matrices for smoothing step
    xvect(t,:) = [x0' z1'];
    pvect((t-1)*l+1:t*l,:) = [P0 V K1 Y'];
    bvect((t-1)*m+1:t*m,:) = iB1;

    if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1;
    else  x0  = Phi*x0 + K1*z1; end
    Phib= Phi - K1*H;
    P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
    WW  = WW + Y'*iB1*Y;
    WZ  = WZ + Y'*iB1*z1;
    Phibb0 = Phib*Phibb0;
    V = Phi*V - K1*Y; 
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

Psi = zeros(l); rt = zeros(l,1); Rt = zeros(l);

for t = n:-1:2  % smoothing loop
%
   % get matrices from filter
   x0 = xvect(t,1:l)'; z1 = xvect(t,l+1:l+m)';
   P0 = pvect((t-1)*l+1:t*l,1:l);
   V  = pvect((t-1)*l+1:t*l,l+1:2*l);
   K1 = pvect((t-1)*l+1:t*l,2*l+1:2*l+m);
   Y  = pvect((t-1)*l+1:t*l,2*l+m+1:2*(l+m))';
   iB1= bvect((t-1)*m+1:t*m,:);

   % smoother
   Phib = Phi - K1*H;
   Psi = H'*iB1*Y + Phib'*Psi;
   rt  = H'*iB1*z1 + Phib'*rt;
   Rt  = H'*iB1*H  + Phib'*Rt*Phib;    
   VT = V - P0*Psi;
   xvect(t,1:l) = (x0 + P0*rt + VT*x1T)';
   pvect((t-1)*l+1:t*l,1:l) = P0 - P0*Rt*P0 + VT*P1T*VT';
%
end

pvect = pvect(:,1:l);
xvect = xvect(:,1:l);
xvect(1,:) = x1T' + xvect(1,:);
pvect(1:l,:) = P1T;

if r, e = z(:,1:m) - xvect*H' - z(:,m+1:m+r)*D';
else  e = z(:,1:m) - xvect*H'; end
