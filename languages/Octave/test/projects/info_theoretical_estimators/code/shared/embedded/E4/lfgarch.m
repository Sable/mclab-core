function [f, z1, zh, x0, QT] = lfgarch(theta, din, z)
% lfgarch  - Exact likelihood function of a sample conditional to the parameters
% parameters of an ARMAX, TF or ESTR model with GARCH effects in the error term.
%    [f, innov, hominnov, ssvect,QT] = lfgarch(theta, din, z)
% theta    > parameter vector.
% din      > matrix which stores a description of the model dynamics.
% z        > matrix of observable variables.
% f        < value of the likelihood function.
% innov    < (optional) stores the sequence of innovations.
% hominnov < (optional) stores the sequence of standarized innovations.
% ssvect   < (optional) stores the sequence of values of the state vector.
% QT       < (optional) stores the sequence of conditional variances.
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
scaleb = E4OPTION(2);
econd  = E4OPTION(4);
zeps   = E4OPTION(15);

[H_D, type] = e4gthead(din);
is_diag = rem(type,100); % uncorrelated errors

[Phi, Gam, E, H, D, C, Q, Phig, Gamg, Eg, Hg, Dg] = garch2ss(theta, din);
% C matrix ignored
n  = size(z,1); l  = size(Phi,1); m  = size(H,1);
r  = max([size(Gam,2), size(D,2)]);
rg = max([size(Gamg,2), size(Dg,2)]);
if econd == 5
   if r, econd = 2; else, econd = 3; end;
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

WW  = zeros(l,l); WZ = zeros(l,1);
Phib = Phi - E*H;
Phibb0 = eye(l,l);

if saveinn, N = eye(l); end

ff = 0.0;

for t = 1:n
    if r
    %
       z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
       x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1;
    %
    else
    %
       z1  = z(t,1:m)' - H*x0;
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

    if is_diag
       Q = diag(Q1) + Q00;
    else 
       Q = vech2m(Q1,m);
    end

    if scaleb, U = cholp(Q, abs(Q));
    else       U = cholp(Q); end
    iQ = (eye(size(U))/U)/U';

    HPhi= H*Phibb0; 
    WW  = WW + HPhi'*iQ*HPhi;
    WZ  = WZ + HPhi'*iQ*z1;
    Phibb0 = Phib*Phibb0;

    z1U = z1'/U;
    ff  = ff + 2*sum(log(diag(U))) + z1U*z1U';
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

if ~nonstat   % stationary system
%
   [Ns S Ns] = svd(Sigm);
   k = find(diag(S) < zeps);
   if size(k,1) < l
      if size(k,1)
      %   
         N = Ns(:,1:k(1)-1);
         Sigm = S(1:k(1)-1,1:k(1)-1);
         WW = N'*WW*N;
         WZ = N'*WZ;
      %
      end
   
      if size(k,1) < l 
         M = chol(Sigm);
         T = cholp(eye(size(M,1))+M*WW*M');
         ff = ff + 2*sum(log(diag(T)));
         if econd == 2
            T = cholp(WW);
            ff = ff - sum((T'\WZ).^2);
         else
            ff = ff - sum((T'\(M*WZ)).^2);
         end
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

f = 0.5*(ff + n*m*log(2*pi));

if saveinn
%
   z1 = zeros(m,n);
   if nargout > 2, zh = zeros(n,m); end
   x0 = zeros(l,n+1);
   x0g = zeros(size(x0g));
   x0(:,1) = N*pinv(WW)*WZ;
   QT = zeros(n*m,m);

   for t = 1:n
       if r
       %
          z1(:,t)  = z(t,1:m)' - H*x0(:,t) - D*z(t,m+1:m+r)';
          x0(:,t+1)  = Phi*x0(:,t) + Gam*z(t,m+1:m+r)' + E*z1(:,t);
       %
       else
       %
          z1(:,t)  = z(t,:)' - H*x0(:,t);
          x0(:,t+1)  = Phi*x0(:,t) + E*z1(:,t);
       %
       end

       if nargout > 2
          if rg
          %
             Q1 = Q0 + Hg*x0g + Dg*z(t,m+r+1:m+r+rg)';
             if is_diag
                z1g  = diag(z1(:, t)*z1(:,t)') - Q1;
             else
                z1g  = vech(z1(:, t)*z1(:,t)') - Q1;
             end
             x0g  = Phig*x0g + Gamg*z(t,m+r+1:m+r+rg)' + Eg*z1g;
          %
          else
          %
             Q1 = Q0 + Hg*x0g;
             if is_diag
                z1g  = diag(z1(:, t)*z1(:,t)') - Q1;
             else
                z1g  = vech(z1(:, t)*z1(:,t)') - Q1;
             end

             x0g  = Phig*x0g + Eg*z1g;
          %
          end
    
          if is_diag
             Q = diag(Q1) + Q00;
          else
             Q = vech2m(Q1,m);
          end 
          QT((t-1)*m+1:t*m,:) = Q;
          if scaleb, U = cholp(Q, abs(Q));
          else       U = cholp(Q); end
          iU = eye(m)/U;
          zh(t,:) = z1(:,t)'*iU;
       end
   %
   end  % t

   z1 = z1';
   x0 = x0';
%
end
