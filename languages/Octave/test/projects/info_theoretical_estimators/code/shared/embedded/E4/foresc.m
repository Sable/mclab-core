function [yf, Bf, betaf, Pf] = foresc(theta, din, z, T, u)
% foresc   - Computes forecasts for a time-varying parameters model.
%    [yf, Bf, betaf, Pf] = foresc(theta, din, z, k, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables in the column order: endogenous 
%         variable, regressors associated with random coefficients,
%         exogenous variables for the changing parameters model and
%         exogenous variables for the noise model in the endogenous
%         variable equation.
% k     > number of forecasts.
% u     > forecasts of the exogenous variables (if any).
% yf    < (kx1) vector of forecasts for the endogenous variable.
% Bf    < covariance matrix of the forecasts.
% betaf < forecasts of the time-varying parameters.
% Pf    < covariances of the time-varying parameters forecasts,
%         stored as a column of k (mxm) blocks.
%
% 2/4/97

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

if nargin < 4,  e4error(3); end

scaleb  = E4OPTION(2);
econd   = E4OPTION(4);

[Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din);
l = size(Phi,1);
n = size(z,1);
m = size(Hv,1);
r = size(Gam,2);
rv = size(Dv,2);

EQEt=E*Q*E';
if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if r
   if nargin < 5, e4error(3); end
   if any(size(u) ~= [T m+r]), e4error(11); end
end
if size(z,2) ~= m+r+1, e4error(11); end

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(z(:,m+2:m+r+1))';
   else
      u0 = z(1,m+2:m+r+1)';
   end

   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, EQEt, 0, Gam*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, EQEt, 0);
end

if ~nonstat, iSigm = pinv(Sigm); end

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
V = eye(l);
P0 = zeros(l);
yf = zeros(T,1);
Bf = zeros(T,1);
betaf = zeros(T,m);
Pf = zeros(T*m,m);
D = Df;

for t = 1:n  % filter loop
%
  % Construct time varying observer
    H = [z(t,2:m+1)*Hv Hf]; C = [z(t,2:m+1)*Cv Cf];
    if r & rv, D = [z(t,2:m+1)*Dv Df]; end
    CRCt = C*R*C'; ESCt = E*S*C';

    if r, z1  = z(t,1)' - H*x0 - D*z(t,m+2:m+r+1)';
    else  z1  = z(t,1)' - H*x0; end

    B1  = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    Phib= Phi - K1*H;
    P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';

    if r, x0  = Phi*x0 + Gam*z(t,m+2:m+r+1)' + K1*z1;
    else  x0  = Phi*x0 + K1*z1; end

    Y = H*Phibb0;
    WW  = WW + Y'*iB1*Y;
    WZ  = WZ + Y'*iB1*z1;
    Phibb0 = Phib*Phibb0;
    V = Phi*V - K1*Y;
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

P0 = P0 + V*P1T*V';
x0 = x0 + V*x1T;

CvRCvt = Cv*R(1:size(Cv,2),1:size(Cv,2))*Cv';
l = size(Hv,2);
rv = size(Dv,2);

for t = 1:T   % forecast loop
%
  % Construct time varying observer
    H = [u(t,1:m)*Hv Hf]; C = [u(t,1:m)*Cv Cf];
    if r, D = [u(t,1:m)*Dv Df]; end
    CRCt = C*R*C';
 
    Bf(t) = H*P0*H' + CRCt;
    Pf((t-1)*m+1:t*m,:) = Hv*P0(1:l,1:l)*Hv' + CvRCvt;

    if rv
       betaf(t,:) = (Hv*x0(1:l,1) + Dv*u(t,m+1:m+rv))';
    else
       betaf(t,:) = (Hv*x0(1:l,1))';
    end
    
    if r
       yf(t,:) = (H*x0 + D*u(t,m+1:m+r)')';
       x0  = Phi*x0 + Gam*u(t,m+1:m+r)';
    else
       yf(t,:) = (H*x0)';
       x0  = Phi*x0;
    end

    P0  = Phi*P0*Phi' + EQEt;
end