function [zhat, bt] = aggrmod(theta, din, z, per, m1)
% aggrmod  - Fixed interval smoother for temporal disaggregation of a series set.
%    [zhat, bt] = aggrmod(theta, din, z, per, m1)
% theta > parameter vector.
% din   > matrix which stores a description of the disaggregated model
%         dynamics.
% z     > matrix of observable variables.
% per   > number of observations that add up to an aggregate.
% m1    > the first m columns of z contain the endogenous variables. Of these
%         m columns, the first m1 are the ones that include aggregated
%         information (the m1 parameter is optional. If not specified it is
%         assumed that m1=m).
% zhat  < E[z(t)/T]
% bt    < Var[z(t)/T]
%
% 9/4/97

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

if nargin < 5, m1 = 0; end

scaleb  = E4OPTION(2);
econd   = E4OPTION(4);
zeps    = E4OPTION(15);

n=size(z,1);
if rem(n,per) ~= 0, e4error(30); end

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
m = size(H,1);
if m1 < 1, m1 = m; end
r=max([size(Gam,2), size(D,2)]);
l = size(Phi,1); I = eye(m1);

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

   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0);
end

if ~nonstat, iSigm = pinv(Sigm); end

Phi = [[Phi;H; zeros((per-1)*m1,l)] [zeros(l+m,m);[I zeros(m1,m-m1)];zeros((per-2)*m1,m)] [zeros(l+m+m1,(per-2)*m1);eye((per-2)*m1)] zeros(l+m+(per-1)*m1,m1)];
H = [H zeros(m,m+(per-1)*m1)];
H(1:m1,l+1:l+m1) = I;
for i=1:per-2
    H(1:m1,l+m+(i-1)*m1+1:l+m+i*m1) = I;
end

if r, Gam = [Gam;D;zeros((per-1)*m1,r)]; end

E = [[E;zeros(m+(per-1)*m1,size(E,2))] [zeros(l,size(C,2));C;zeros((per-1)*m1,size(C,2))]];
Q = [Q S; S' R];
S = [S; R];

ltr = l; l = size(Phi,1); I = eye(l,l);

Sigm = [Sigm zeros(ltr,l-ltr); zeros(l-ltr,l)];
iSigm = [iSigm zeros(ltr,l-ltr); zeros(l-ltr,l)];
x0 = [x0; zeros(l-ltr,1)];

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

if ~nonstat & trace(Sigm) < zeps
   P1T = zeros(l);
else
   P1T = pinv(iSigm + WW);
end

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

Psi = zeros(l); rt = zeros(l,1); Rt = zeros(l);

bt = zeros(n,m1);
P0 = P0 + V*P1T*V';
x0 = x0 + V*x1T;

bt = zeros(n,m1);
zhat = zeros(n,m1);
k = [ltr+1:ltr+m1 ltr+m+1:ltr+m+m1*(per-1)];
bt(n:-1:n-per+1,:)   = reshape(diag(P0(k,k)),m1,per)';
zhat(n:-1:n-per+1,:) = reshape(x0(k),m1,per)';

for t = n:-1:per+1  % smoothing loop
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

    if rem(t-1,per) == 0
    %
       VT = V - P0*Psi;
       PT = P0 - P0*Rt*P0 + VT*P1T*VT';
       xT = x0 + P0*rt + VT*x1T;

       bt(t-1:-1:t-per,:)   = reshape(diag(PT(k,k)),m1,per)';
       zhat(t-1:-1:t-per,:) = reshape(xT(k),m1,per)';
    %
    end
%
end