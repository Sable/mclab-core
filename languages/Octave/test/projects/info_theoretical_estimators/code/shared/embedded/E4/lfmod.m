function [f, innov, ssvect] = lfmod(theta, din, z)
% lfmod    - Computes the exact likelihood function of a general SS model.
%    [f, innov, ssvect] = lfmod(theta, din, z)
% theta    > parameter vector.
% din      > matrix which stores a description of the model dynamics.
% z        > matrix of observable variables.
% f        < value of the likelihood function.
% innov    < (optional) stores the sequence of innovations.
% ssvect   < (optional) stores the sequence of estimates of the state
%            vector.
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

if nargin < 3,  e4error(3); end

saveinn = 0; if nargout >= 2, saveinn = 1; end
savessv = 0; if nargout == 3, savessv = 1; end
filtk   = 0; if E4OPTION(1) == 1, filtk = 1; end
scaleb  = E4OPTION(2);
vcond = E4OPTION(3);
econd = E4OPTION(4);
zeps  = E4OPTION(15);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
n = size(z,1);
m = size(H,1);
r = size(Gam,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end
if econd == 2 & ~saveinn, MV = 1; else, MV = 0; end

if size(z,2) ~= m+r, e4error(11); end

if filtk
   if vcond == 4
      [x0, Sigm, ign, ign, ign, P0, iSigm, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
      P0 = zeros(size(Sigm));
   else
      [x0, P0] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   end
   CRCt=C*R*C';  ESCt=E*S*C';  EQEt=E*Q*E';
else
   if vcond == 4
      [x0, B0, K0, y0, M0, Sigm, iSigm, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   else
      [x0, B0, K0, y0, M0] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   end
   B1 = B0; K1 = K0; M1 = M0; y1 = y0;
end

if MV | vcond == 4
   l = size(Phi,1);
   Phibb0 = eye(l,l);
   WW  = zeros(l,l); WZ = zeros(l,1);
end

if savessv
  ssvect = zeros(n+1,size(x0,1)); ssvect(1,:) = x0';
end
if saveinn, innov = zeros(n, m); end 

ff = 0.0;

for t = 1:n  % main loop
%
    if r, z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
    else  z1  = z(t,:)' - H*x0; end
    if saveinn, innov(t,:) = z1'; end
    if filtk, B1  = H*P0*H' + CRCt; end

    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    if filtk
    %
       K1  = ((Phi*P0*H' + ESCt)/U)/U';
       if r, x1  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1;
       else  x1  = Phi*x0 + K1*z1; end
       Phib= Phi - K1*H;
       P1  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
    %
    else
    %
       Hy0 = H*y0;
       if t > 1
       %
          K1  = K0 + (Phi - K0*H)*y0*M0*Hy0'*iB1;
          if vcond == 1
             y1  = (Phi - K0*H)*y0;
             M1  = M0 - M0*Hy0'*iB1*Hy0*M0;
          else
             y1  = (Phi - K1*H)*y0;
             M1  = M0 + M0*Hy0'*iB0*Hy0*M0;
          end
          B0 = B1;
       %
       end

       Phib= Phi - K1*H;
       if r, x1  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1;
       else  x1  = Phi*x0 + K1*z1; end
       B1  = B0 + H*y1*M1*y1'*H';
    %
    end

    if savessv, ssvect(t+1,:) = x1'; end
    z1U = z1'/U;
    
    if MV | vcond == 4 
    %   
       HPhi= H*Phibb0;
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1;
       Phibb0 = Phib*Phibb0;
    %
    end

    ff  = ff + 2*sum(log(diag(U))) + trace(z1U*z1U');

    if filtk, P0  = P1; x0  = x1; 
    else K0 = K1; x0 = x1; y0 = y1; M0 = M1; iB0 = iB1; end
%   
end  % t

if MV | vcond == 4
   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
end

if vcond == 4
%
   if ~nonstat  % stationary system
   %
      [M S M] = svd(Sigm);
      k = find(diag(S) < zeps);
      if size(k,1) < l
         if size(k,1)
         %
            M = M(:,1:k(1)-1);
            Sigm = S(1:k(1)-1,1:k(1)-1);
            WW = M'*WW*M;
            WZ = M'*WZ;
         %
         end
         M = chol(Sigm);
         T = chol(eye(size(M,1))+M*WW*M');
         ff = ff + 2*sum(log(diag(T)));

         if MV
            ff = ff -WZ'*pinv(WW, zeps)*WZ;
         elseif econd ~= 2
            ff = ff - sum((T'\(M*WZ)).^2);
         end
      end
   %
   elseif nonstat == 2  % non stationary system
   %
      T = cholp(WW);
      ff = ff + 2*sum(log(diag(T))) - sum((T'\WZ).^2);
   %
   else  % partially stationary
   %  
      S = svd(Sigm);
      T = cholp(iSigm + WW);
      ff = ff + 2*sum(log(diag(T))) + sum(log(S(S > zeps)));

      if econd == 2
         T = cholp(WW);
      end
      ff = ff - sum((T'\WZ).^2);
   %
   end
%
elseif MV
     ff = ff - WZ'*pinv(WW,zeps)*WZ;
end

f = 0.5*(ff + n*m*log(2*pi));
