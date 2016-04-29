function [zhat, bt, xvect, pvect] = fismiss(theta, din, z)
% fismiss  - Fixed interval smoother for models with missing data.
%    [zhat, pz, xhat, px] = fismiss(theta, din, z)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables. This matrix may include missing data
%         in the column corresponding to the endogenous variables.
% zhat  < E[z(t)/T]
% pz    < Var(z(t) - z(t/T))
% xhat  < E[x(t)/T]
% px    < Var(x(t) - x(t/T))
%
% 27/3/97

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

scaleb  = E4OPTION(2);
econd   = E4OPTION(4);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
n=size(z,1);
m = size(H,1);
r = size(Gam,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if size(z,2) ~= m+r, e4error(11); end

l = size(Phi,1);
Phib = [Phi zeros(l,m); H zeros(m,m)];
if r, Gamb = [Gam; D]; else  Gamb = []; end
v =  size(R,1);
w = size(Q,1);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(z(:,m+1:m+r))';
   else
      u0 = z(1,m+1:m+r)';
   end

   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0);
end

if ~nonstat, iSigm = pinv(Sigm); end

Sigm = [Sigm zeros(l,m); zeros(m,l+m)];
iSigm = [iSigm zeros(l,m); zeros(m,l+m)];
x0 = [x0; zeros(m,1)];
   
Eb = [E zeros(l,v); zeros(m,w) C];
Hb = [H zeros(m,m)]; Db = D; Cb = C;
Qb = [Q S; S' R]; Rb = R; Sb = [S; R];
Phi = Phib; Gam = Gamb; E = Eb; H = Hb; D = Db; C = Cb;
Q   = Qb;   S   = Sb;   R = Rb;
clear Phib Gamb Eb Hb Db Cb Qb Sb Rb
ltr = l; l = size(Phi,1); I = eye(l,l);

EQEt=E*Q*E';

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
V = eye(l);
P0 = zeros(l);
xvect = zeros(n, l+m ); pvect = zeros(n*l, 2*(l+m));
bvect = zeros(n*m, m);

for t = 1:n  % filter loop
%
  % Construct H(t)
    trueobs = find(~isnan(z(t,1:m)'));
    nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
    if nmiss & nobs, Ht = Ht(trueobs,:); end
    Hb = Ht*H; Cb = Ht*C; if r, Db = Ht*D; end

    if nobs
    %
        if r, z1  = z(t,trueobs)' - Hb*x0 - Db*z(t,m+1:m+r)';
        else  z1  = z(t,trueobs)' - Hb*x0; end

        B1  = Hb*P0*Hb' + Cb*R*Cb';
        if scaleb, U = cholp(B1, abs(B1));
        else       U = cholp(B1); end
        iB1 = (eye(size(U))/U)/U';

        K1  = ((Phi*P0*Hb' + E*S*Cb')/U)/U';
        Y = Hb*Phibb0;

        % save matrices for smoothing step
        xvect(t,1:l+nobs) = [x0' z1'];
        pvect((t-1)*l+1:t*l,1:2*(l+nobs)) = [P0 V K1 Y'];
        bvect((t-1)*m+1:(t-1)*m+nobs,1:nobs) = iB1;

        if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1;
        else  x0  = Phi*x0 + K1*z1; end
        Phib= Phi - K1*Hb;
        P0  = Phib*P0*Phib' + EQEt + K1*Cb*R*Cb'*K1' - K1*(E*S*Cb')' - E*S*Cb'*K1';
        WW  = WW + Y'*iB1*Y;
        WZ  = WZ + Y'*iB1*z1;
        Phibb0 = Phib*Phibb0;
        V = Phi*V - K1*Y;
    else
    %
        % save matrices for smoothing step
        xvect(t,1:l) = x0';
        pvect((t-1)*l+1:t*l,1:2*l) = [P0 V];

        if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)';
        else  x0  = Phi*x0; end
        P0 = Phi*P0*Phi' + EQEt;
        Phibb0 = Phi*Phibb0;
        V = Phi*V;
    %
    end
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

Psi = zeros(l); rt = zeros(l,1); Rt = zeros(l);

bt = zeros(n*m,m);
P0 = P0 + V*P1T*V';
x0 = x0 + V*x1T;
bt((n-1)*m+1:n*m,:) = P0(ltr+1:l,ltr+1:l);
zhat = x0(ltr+1:l)';

for t = n:-1:2  % smoothing loop
%
  % Construct H(t)
    trueobs = find(~isnan(z(t,1:m)'));
    nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
    if nmiss & nobs, Ht = Ht(trueobs,:); end
    Hb = Ht*H;

    % get matrices from filter
    x0 = xvect(t,1:l)'; z1 = xvect(t,l+1:l+nobs)';
    P0 = pvect((t-1)*l+1:t*l,1:l);
    V  = pvect((t-1)*l+1:t*l,l+1:2*l);
    K1 = pvect((t-1)*l+1:t*l,2*l+1:2*l+nobs);
    Y  = pvect((t-1)*l+1:t*l,2*l+nobs+1:2*(l+nobs))';
    iB1= bvect((t-1)*m+1:(t-1)*m+nobs,1:nobs);

    if nobs
    %
       Phib = Phi - K1*Hb;
       Psi = Hb'*iB1*Y + Phib'*Psi;
       rt  = Hb'*iB1*z1 + Phib'*rt;
       Rt  = Hb'*iB1*Hb  + Phib'*Rt*Phib;
    %
    else
    %
       Psi = Phi'*Psi;
       rt  = Phi'*rt;
       Rt  = Phi'*Rt*Phi;
    %
    end

    VT = V - P0*Psi;
    PT = P0 - P0*Rt*P0 + VT*P1T*VT';

    xvect(t,1:l) = (x0 + P0*rt + VT*x1T)';
    pvect(n*l-ltr*(n-t+1)+1:n*l-ltr*(n-t),1:ltr) = PT(1:ltr,1:ltr);
    bt((t-2)*m+1:(t-1)*m,:) = PT(ltr+1:l,ltr+1:l);
%
end

pvect = pvect(m*n+1:n*l,1:ltr);
pvect(1:ltr,:) = P1T(1:ltr,1:ltr);
zhat = [xvect(2:n,ltr+1:l);zhat];
xvect = xvect(:,1:ltr);
xvect(1,:) = x1T(1:ltr)' + xvect(1,:);
