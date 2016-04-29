function [f, z1] = lfper(theta, din, z, per1)
% lfper  - Computes the exact likelihood function of a VARMAX, TF, ESTR
% periodic model
%    [f, innov, ssvect] = lfper(theta, din, z, per1)
% Under these conditions, this function outputs the same result that lfmod with
% vcond = 'idej'. It is valid for stationary and/or nonstationary models.
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% z      > matrix of observable variables.
% per1   > Starting period between 1 and s.
% f      < value of the likelihood function.
% innov  < (optional) stores the sequence of innovations.
% ssvect < (optional) stores the sequence of values of the state vector.
%
% 18/9/97

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
saveinn = 0; if nargout >= 2, saveinn = 1; end
savessv = 0; if nargout == 3, savessv = 1; end
filtk   = 0; if E4OPTION(1) == 1, filtk = 1; end
scaleb  = E4OPTION(2);
vcond = E4OPTION(3);
MV = 0;
econd = E4OPTION(4);
zeps = E4OPTION(15);

if nargin < 4, per1 = 1; end

n=size(z,1);

[kn, Phi1, Gam1, E1, H1, D1, Q1] = thd2ssp(theta, din);
[H_D, type, m, r, s] = e4gthead(din);
if size(z,2) ~= m+r, e4error(11); end

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

iQ1 = zeros(size(Q1));
U1  = zeros(size(Q1));
Phib1 = zeros(size(Phi1));

ns = fix(n/s); rs = rem(n,s);
rss = ones(s,1)*ns;
rss(per1:min(rs-1+per1,s)) = ones(min(rs,s-per1+1),1) + ns;
rss(1:max(rs-1-s+per1,0)) = ones(max(rs-1-s+per1,0),1) + ns;

ff  = 0;

kl = cumsum([1;kn]);
kl = [kl(1:s)'; (kl(2:s+1)-1)'];
km = [[1:m:(s-1)*m+1]; [m:m:s*m]];
kn = [kn(s);kn];
l  = kn(per1);

Phi0 = eye(l);
Q0   = zeros(l);
Gam0 = zeros(l,r);

for i=1:s
    if scaleb, U = cholp(Q1(km(1,i):km(2,i),:), abs(Q1(km(1,i):km(2,i),:)));
    else       U = cholp(Q1(km(1,i):km(2,i),:)); end
    iQ1(km(1,i):km(2,i),:) = (eye(size(U))/U)/U';
    U1(km(1,i):km(2,i),:) = U;
    ff  = ff + 2*rss(i)*sum(log(diag(U)));
    Phib1(kl(1,i):kl(2,i),1:kn(i)) = Phi1(kl(1,i):kl(2,i),1:kn(i)) - E1(kl(1,i):kl(2,i),:)*H1(km(1,i):km(2,i),1:kn(i));
end

% Cálculo de condiciones iniciales

k = per1-1;

for i=1:s
%
    if k == 0, k = s; end
    Q0 = Q0 + Phi0*E1(kl(1,k):kl(2,k),:)*Q1(km(1,k):km(2,k),:)*E1(kl(1,k):kl(2,k),:)'*Phi0';
    if r, Gam0 = Gam0 + Phi0*Gam1(kl(1,k):kl(2,k),:); end   
    Phi0 = Phi0*Phi1(kl(1,k):kl(2,k),1:kn(k));
    k = k-1;
%
end

if r & (econd == 1 | econd == 4)  % en presencia de variables exógenas conviene dejar las condiciones
%                                   iniciales en función de la media de las exógenas
%
   if econd == 1
      u0 = mean(z(:,m+1:m+r))';
   else
      u0 = z(1,m+1:m+r)';
   end
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi0, Q0, 0, Gam0*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi0, Q0, 0);
end

if saveinn, N = eye(l); end

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);

k = per1;

for t = 1:n

    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    Phib= Phib1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    Q   = Q1(km(1,k):km(2,k),:);
    U   = U1(km(1,k):km(2,k),:);    
    iQ = iQ1(km(1,k):km(2,k),:);

    if r
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);
       z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
       x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1;
    else
       z1  = z(t,:)' - H*x0;
       x0  = Phi*x0 + E*z1;
    end

    z1U = iQ*z1;
    
    HPhi= H*Phibb0;
    WW  = WW + HPhi'*iQ*HPhi;
    WZ  = WZ + HPhi'*z1U;
    Phibb0 = Phib*Phibb0;

    ff  = ff + z1'*z1U;

    if k == s, k = 1; else, k=k+1; end
end

if any(isnan([WW WZ])) | any(isinf([WW WZ]))
   B1, WW, WZ
   e4error(25);
else
   if ~nonstat
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
         M = cholp(Sigm);
         T = cholp(eye(size(M,1))+M*WW*M');
         ff = ff + 2*sum(log(diag(T)));


         if econd == 2
            ff = ff -WZ'*pinv(WW, zeps)*WZ;
         else
            ff = ff - sum((T'\(M*WZ)).^2);
         end
      end
   %
   elseif nonstat == 2  % sistemas puramente no estacionarios => isigma = 0
      T = cholp(WW);
      ff = ff + 2*sum(log(diag(T))) - sum((T'\WZ).^2);
   else
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
end

f = 0.5*(ff + n*m*log(2*pi));

k = per1;

if saveinn
%

%   x0 = zeros(kn(k),n+1);
   x0 = N*pinv(WW)*WZ;
   z1 = zeros(m,n);

   for t = 1:n

       Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
       E   = E1(kl(1,k):kl(2,k),:);
       H   = H1(km(1,k):km(2,k),1:kn(k));

      if r
      %
         Gam = Gam1(kl(1,k):kl(2,k),:);
         D   = D1(km(1,k):km(2,k),:);

         z1(:,t)  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
         x0 = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1(:,t);
      %
      else
      %
          z1(:,t)  = z(t,:)' - H*x0;
          x0  = Phi*x0 + E*z1(:,t);
      %
      end
      if k == s, k = 1; else, k=k+1; end
   end

   z1 = z1';
%
end