function [z1, vT, wT, bvect, vvT, vwT] = fisres(theta, din, z)
% fisres   - Fixed interval smoother for noise terms.
%    [z1, vT, wT, vz1, vvT, vwT] = fisres(theta, din, z)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% z1    < innovations
% vT    < E[v(t)/T] smoothed observation error
% wT    < E[w(t)/T] smoothed state error
% vz1   < Var(z1)
% vvT   < Var(v(t) - v(t/T))
% vwT   < Var(w(t) - w(t/T))
%
% 14/1/98

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
P0 = zeros(l);
z1 = zeros(n,m); pvect = zeros(n*l, 2*m); bvect = zeros(n*m, 2*m);
w = size(Q,1); v = size(R,1);
wT = zeros(n,w);
vT = zeros(n,v);

for t = 1:n  % filter loop
%
    if r, z1(t,:)  = (z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)')';
    else  z1(t,:)  = (z(t,:)' - H*x0)'; end

    B1  = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    Y = H*Phibb0;

    % save matrices for smoothing step
    pvect((t-1)*l+1:t*l,:) = [K1 Y'];
    bvect((t-1)*m+1:t*m,:) = [B1 iB1];

    if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1(t,:)';
    else  x0  = Phi*x0 + K1*z1(t,:)'; end
    Phib= Phi - K1*H;
    P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
    WW  = WW + Y'*iB1*Y;
    WZ  = WZ + Y'*iB1*z1(t,:)';
    Phibb0 = Phib*Phibb0;
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

Psi = zeros(l); rt = zeros(l,1); Rt = zeros(l);

if nargout > 3
   vwT = zeros(w*n,w);
   vvT = zeros(v*n,v);
end

QEt = Q*E'; SCt = S*C'; StEt = (E*S)'; RCt = R*C';

for t = n:-1:1  % smoothing loop
%
   % get matrices from filter
   K1 = pvect((t-1)*l+1:t*l,1:m);
   Y  = pvect((t-1)*l+1:t*l,m+1:2*m)';
   B1 = bvect((t-1)*m+1:t*m,1:m);
   iB1= bvect((t-1)*m+1:t*m,m+1:2*m);

   % smoother

   Aw = QEt - SCt*K1'; Av = StEt - RCt*K1';

   wT(t,:) = (Aw*(rt-Psi*x1T) + SCt*iB1*(z1(t,:)'-Y*x1T))';
   vT(t,:) = (Av*(rt-Psi*x1T) + RCt*iB1*(z1(t,:)'-Y*x1T))';

   Phib = Phi - K1*H;
   rt  = H'*iB1*z1(t,:)' + Phib'*rt;

   z1(t,:) = z1(t,:) - (Y*x1T)';

   if nargout > 3
      Aww = Aw*Psi + SCt*iB1*Y;
      Avv = Av*Psi + RCt*iB1*Y;
      vwT((t-1)*w+1:t*w,:) = Q - Aw*Rt*Aw' - SCt*iB1*SCt' + Aww*P1T*Aww';
      vvT((t-1)*v+1:t*v,:) = R - Av*Rt*Av' - RCt*iB1*RCt' + Avv*P1T*Avv';
      bvect((t-1)*m+1:t*m,1:m) = B1 - Y*P1T*Y';
   end

   Psi = H'*iB1*Y + Phib'*Psi;
   Rt  = H'*iB1*H  + Phib'*Rt*Phib;    
%
end

if nargout > 3, bvect = bvect(:,1:m); end