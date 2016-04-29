function [x0,Ao,Bo,Co,Do,P0, iP0, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV,KF)
% lfmodini - Computes initial conditions for LFMOD.
%    [x0,Ao,Bo,Co,Do,P0,iP0,nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV,KF)
% Phi, Gam, E, H, D, C, Q, S, R > SS model matrices.
% z       > matrix of observable variables.
% MV      > MV=1 if the ML estimation of x0 will be done during the
%             likelihood function evaluation.
% KF      > KF=1 use Kalman filter only.
% x0      < initial state vector.
%           Kalman  Chandrasekhar
% Ao      <   P0         B0
% Bo      <   []         K0
% Co      <   []         y0
% Do      <   []         M0
% P0      < covariance of the initial state vector.
% iP0     < inverse of P0 (can be rank-defficient if P0 ->INF).
% nonstat < nonstat= 0 if the system is stationary, nonstat= 1 if the
%           system is partially nonstationary and nonstat= 2 if the
%           system is nonstationary.
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

if nargin < 12, KF = 0; end
if nargin < 11, MV = 0; end

zeps = E4OPTION(15);
if E4OPTION(1) == 1 | KF, filtk = 1; else, filtk = 0; end
vcond  = E4OPTION(3);
econd  = E4OPTION(4);
scaleb = E4OPTION(2);

n = size(z,1); l = size(Phi,1);
m = size(H,1); r = size(Gam,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

x0  = zeros(l,1); P0  = zeros(l,l);

if econd == 2 & MV == 0   % Maximum likelihood estimator of the initial state vector
%
   Phibb0 = eye(l,l);
   x0s = x0;
   CRCt = C*R*C'; ESCt = E*S*C'; EQEt = E*Q*E';
   WW  = zeros(l,l); WZ = zeros(l,1);

   for t=1:n
   %
       B1  = H*P0*H' + CRCt;
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1); end
       iB1 = (eye(size(U))/U)/U';
       K1  = (Phi*P0*H' + ESCt)*iB1;

       if r
          z1s  = z(t,1:m)' - D*z(t,m+1:m+r)' - H*x0s; 
          x0s  = Phi*x0s + Gam*z(t,m+1:m+r)' + K1*z1s;   
       else
          z1s  = z(t,1:m)' - H*x0s; 
          x0s  = Phi*x0s + K1*z1s;
       end

       Phib= Phi - K1*H; 
       P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
       HPhi= H*Phibb0;
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1s;
       Phibb0 = Phib*Phibb0;
   %
   end  % t

   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
   x0 = pinv(WW,zeps)*WZ;  P0 = zeros(l,l);
%
end

if r &  econd == 1
   u0 = mean(z(:,m+1:m+r))';
elseif r & econd == 4
   u0 = z(1,m+1:m+r)';
end

if vcond == 1
%
  [P0, nonstat] = lyapunov(Phi, E*Q*E');

  if nonstat > 0  % non stationary
  %
     if ~filtk, e4error(26); end
     e4warn(6);
     vcond = 3;  % Change of initial conditions
  %
  elseif r & (econd == 1 | econd == 4)
     x0 = pinv(eye(l)-Phi,zeps)*Gam*u0;
  end
%
end

if vcond >= 3
%
  if vcond == 4, k = 0; else, k = E4OPTION(16); end

  if r & (econd == 1 | econd == 4)
     [x0, P0, iP0, nonstat] =  djccl(Phi, E*Q*E', k, Gam*u0);
  else
     [ignore, P0, iP0, nonstat] =  djccl(Phi, E*Q*E', k);
  end
%
else
  iP0 = []; nonstat = [];
end
     
if filtk
   Ao = P0; Bo = []; Co = []; Do = [];
else
%
   if vcond == 1
      B0 = H*P0*H' + C*R*C';
      if scaleb, U = cholp(B0, abs(B0));
      else       U = cholp(B0); end
      iB0 = ((eye(size(B0))/U)/U');
      y0 = Phi*P0*H' + E*S*C';
      K0 = y0*iB0; M0 = -iB0;
   else
      B0 = C*R*C';
      if scaleb, U = cholp(B0, abs(B0));
      else       U = cholp(B0); end
      iB0 = ((eye(size(B0))/U)/U');
      y0 = E;
      K0 = E*S*C'*iB0; M0 = Q - S*C'*inv(C*R*C')*C*S';
   end
   Ao = B0; Bo = K0; Co = y0; Do = M0;
end
