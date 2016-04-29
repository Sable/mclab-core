function [yf, Bf, vf] = foregarc(theta, din, z, T, u)
% foregarc - Forecasts a model with GARCH effects in the error term.
%    [yf, Bf, vf] = foregarc(theta, din, z, k, u)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% k     > Number of forecasts.
% u     > forecasts of the exogenous variables (if any).
% yf    < (kxm) matrix of forecasts for the endogenous variables.
% Bf    < covariance matrix of the forecast errors.
% vf    < the expectation of the conditional covariances of the errors.
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

global E4OPTION

if nargin < 4,  e4error(3); end

scaleb  = E4OPTION(2);
econd = E4OPTION(4);

[H_D, type] = e4gthead(din);
is_diag = rem(type,100); % uncorrelated errors

[Phi, Gam, E, H, D, C, Q, Phig, Gamg, Eg, Hg, Dg] = garch2ss(theta, din);
% C matrix ignored
n  = size(z,1);
l  = size(Phi,1);
m  = size(H,1);
r  = max([size(Gam,2), size(D,2)]);
rg = max([size(Gamg,2), size(Dg,2)]);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if r
   if nargin < 5, e4error(3); end
   if any(size(u) ~= [T r+rg]), e4error(11); end
end

if size(z,2) ~= m+r+rg, e4error(11); end

if is_diag
   Q0 = diag(Q);
   Q00 = Q - diag(Q0);
else, Q0 = vech(Q); end
x0g = zeros(size(Phig,1),1);

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

if econd == 2
   WW  = zeros(l,l); WZ = zeros(l,1);
   Phib = Phi - E*H;
   Phibb0 = eye(l,l);
end

for t = 1:n % filter loop
%
    if r
    %
       z1 = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
       x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1;
    %
    else
    %
       z1 = z(t,1:m)' - H*x0;
       x0  = Phi*x0 + E*z1;
    %
    end

    if rg
    %
       Q1 = Q0 + Hg*x0g + Dg*z(t,m+r+1:m+r+rg)';
       if is_diag
          z1g  = diag(z1*z1') - Q1;
       else
          z1g  = vech(z1*z1') - Q1;
       end
       x0g  = Phig*x0g + Gamg*z(t,m+r+1:m+r+rg)' + Eg*z1g;
    %
    else
    %
       Q1 = Q0 + Hg*x0g;
       if is_diag
          z1g  = diag(z1*z1') - Q1;
       else
          z1g  = vech(z1*z1') - Q1;
       end
       x0g  = Phig*x0g + Eg*z1g;
    %
    end

    if econd == 2    
       if is_diag, Q = diag(Q1) + Q00;
       else, Q = vech2m(Q1,m); end
       if scaleb, U = cholp(Q, abs(Q));
       else       U = cholp(Q); end
       iQ = (eye(size(U))/U)/U';

       HPhi= H*Phibb0;  
       WW  = WW + HPhi'*iQ*HPhi;
       WZ  = WZ + HPhi'*iQ*z1;
       Phibb0 = Phib*Phibb0;
    end
end

yf = zeros(T,m);
Bf = zeros(T*m,m);
vf = zeros(T*m,m);
P0 = zeros(l);

if econd == 2   % maximum likelihood initial conditions
%
  if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
  x0 = x0 + Phibb0 * pinv(WW)*WZ;
%
end   

for t = 1:T   % forecast loop
%
    if rg
    %
       Q1 = Q0 + Hg*x0g + Dg*u(t,r+1:r+rg)';
       x0g  = Phig*x0g + Gamg*u(t,r+1:r+rg)';
    %
    else
    %
       Q1 = Q0 + Hg*x0g;
       x0g  = Phig*x0g;
    %
    end

    if is_diag, Q = diag(Q1) + Q00;
    else, Q = vech2m(Q1,m); end

    B1  = H*P0*H' + C*Q*C';

    vf((t-1)*m+1:t*m,:) = Q;
    Bf((t-1)*m+1:t*m,:) = B1;
   
    if r
       yf(t,:) = (H*x0 + D*u(t,1:r)')';
       x0  = Phi*x0 + Gam*u(t,1:r)';
    else
       yf(t,:) = (H*x0)';
       x0  = Phi*x0;
    end

    P0  = Phi*P0*Phi' + E*Q*E';
end
