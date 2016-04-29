function [z1, vT, wT, bvect, vvT, vwT] = fisriss(theta, din, z)
% fisriss  - Fixed interval smoother for noise terms with missing data.
%    [z1, vT, wT, vz1, vvT, vwT] = fisriss(theta, din, z)
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
EQEt=E*Q*E';

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
z1 = ones(n,m)*NaN; pvect = zeros(n*l, 2*m); bvect = zeros(n*m, 2*m);
w = size(Q,1); v = size(R,1);
wT = zeros(n,w);
vT = zeros(n,v);

for t = 1:n  % filter loop
%
  % Construct H(t)
    trueobs = find(~isnan(z(t,1:m)'));
    nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
    if nmiss & nobs, Ht = Ht(trueobs,:); end
    Hb = Ht*H; Cb = Ht*C; if r, Db = Ht*D; end

    if nobs
    %
       if r, z1(t,trueobs) = (z(t,trueobs)' - Hb*x0 - Db*z(t,m+1:m+r)')';
       else  z1(t,trueobs) = (z(t,trueobs)' - Hb*x0)'; end

       B1  = Hb*P0*Hb' + Cb*R*Cb';
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1); end
       iB1 = (eye(size(U))/U)/U';

       K1  = ((Phi*P0*Hb' + E*S*Cb')/U)/U';
       Y = Hb*Phibb0;

       % save matrices for smoothing step
       pvect((t-1)*l+1:t*l,1:2*nobs) = [K1 Y'];
       bvect((t-1)*m+1:(t-1)*m+nobs,1:2*nobs) = [B1 iB1];

       if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1(t,trueobs)';
       else  x0  = Phi*x0 + K1*z1(t,trueobs)'; end
       Phib= Phi - K1*Hb;
       P0  = Phib*P0*Phib' + EQEt + K1*Cb*R*Cb'*K1' - K1*(E*S*Cb')' - E*S*Cb'*K1';

       WW  = WW + Y'*iB1*Y;
       WZ  = WZ + Y'*iB1*z1(t,trueobs)';
       Phibb0 = Phib*Phibb0;
    else
    %
       if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)';
       else  x0  = Phi*x0; end
       P0 = Phi*P0*Phi' + EQEt;
       Phibb0 = Phi*Phibb0;
    %
    end
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

QEt = Q*E'; StEt = S'*E';
NaNs = eye(m)*NaN;

for t = n:-1:1  % smoothing loop
%
   % Construct H(t)
   trueobs = find(~isnan(z(t,1:m)'));
   nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
   if nmiss & nobs, Ht = Ht(trueobs,:); end
   Hb = Ht*H; Cb = Ht*C; 

   if nobs
   %
      % get matrices from filter
      K1 = pvect((t-1)*l+1:t*l,1:nobs);
      Y  = pvect((t-1)*l+1:t*l,nobs+1:2*nobs)';
      B1 = bvect((t-1)*m+1:(t-1)*m+nobs,1:nobs);
      iB1= bvect((t-1)*m+1:(t-1)*m+nobs,nobs+1:2*nobs);
      bvect((t-1)*m+1:t*m,1:m) = NaNs;

      % smoother

      Aw = QEt - S*Cb'*K1'; Av = StEt - R*Cb'*K1';

      wT(t,:) = (Aw*(rt-Psi*x1T) + S*Cb'*iB1*(z1(t,trueobs)'-Y*x1T))';
      vT(t,:) = (Av*(rt-Psi*x1T) + R*Cb'*iB1*(z1(t,trueobs)'-Y*x1T))';

      Phib = Phi - K1*Hb;
      rt  = Hb'*iB1*z1(t,trueobs)' + Phib'*rt;

      z1(t,trueobs) = z1(t,trueobs) - (Y*x1T)';

      if nargout > 3
         Aww = Aw*Psi + S*Cb'*iB1*Y;
         Avv = Av*Psi + R*Cb'*iB1*Y;
         vwT((t-1)*w+1:t*w,:) = Q - Aw*Rt*Aw' - S*Cb'*iB1*Cb*S' + Aww*P1T*Aww';
         vvT((t-1)*v+1:t*v,:) = R - Av*Rt*Av' - R*Cb'*iB1*Cb*R  + Avv*P1T*Avv';
         bvect((t-1)*m+trueobs,trueobs) = B1 - Y*P1T*Y';
      end

      Psi = Hb'*iB1*Y + Phib'*Psi;
      Rt  = Hb'*iB1*Hb  + Phib'*Rt*Phib;
   %
   else
   %
      bvect((t-1)*m+1:t*m,1:m) = NaNs;

      % smoother

      Aw = QEt; Av = StEt;

      wT(t,:) = (Aw*(rt-Psi*x1T))';
      vT(t,:) = (Av*(rt-Psi*x1T))';

      rt  = Phi'*rt;

      if nargout > 3
         Aww = Aw*Psi;
         Aww = Aw*Psi;
         Avv = Av*Psi;
         vwT((t-1)*w+1:t*w,:) = Q - Aw*Rt*Aw' + Aww*P1T*Aww';
         vvT((t-1)*v+1:t*v,:) = R - Av*Rt*Av' + Avv*P1T*Avv';
      end

      Psi = Phi'*Psi;
      Rt  = Phi'*Rt*Phi;
   %
   end
%
end

if nargout > 3, bvect = bvect(:,1:m); end