function [ dts, corrm, varm, Im ] = iper(theta, din, z, per1)
% imod     - Computes the exact information matrix for periodic models.
%    [dts, corrm, varm, Im] = imod(theta, din, z, per1)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% per1  > first seasonal period.
% dts   < vector of standard deviations.
% corrm < correlation matrix.
% varm  < covariance matrix.
% Im    < information matrix
% If Im is not positive definite, it is perturbed before computing
% the rest of the matrices.
%
% 11/3/97

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

if nargin < 3, e4error(3); end
if nargin < 4, per1 = 1; end

oldfil = E4OPTION(1); E4OPTION(1) = 1;
scaleb = E4OPTION(2);
zeps   = E4OPTION(15);

[kn, Phi1, Gam1, E1, H1, D1, Q1] = thd2ssp(theta, din);
[H_D, type, m, r, s] = e4gthead(din);
m = size(Q1,2);
r=max([size(Gam1,2), size(D1,2)]);
if size(z,2) ~= m+r, e4error(11); end

n = size(z,1);

ns = fix(n/s); rs = rem(n,s);
rss = ones(s,1)*ns;
rss(per1:min(rs-1+per1,s)) = ones(min(rs,s-per1+1),1) + ns;
rss(1:max(rs-1-s+per1,0)) = ones(max(rs-1-s+per1,0),1) + ns;

kl = cumsum([1;kn]);
kl = [kl(1:s)'; (kl(2:s+1)-1)'];
km = [[1:m:(s-1)*m+1]; [m:m:s*m]];
kn = [kn(s);kn];
l  = kn(per1);
lmax = size(Phi1,2);
lsum = size(Phi1,1);

Phi0 = eye(l);
Q0   = zeros(l);
Gam0 = zeros(l,r);

if size(theta,2) > 1  % look for fixed parameters in theta
   index = find(theta(:,2) == 0);
else
   index = (1:size(theta,1))';
end

np = size(index,1);
Im = zeros(np, np);

dPhi1 = zeros(lsum*np, lmax);
dGam1 = zeros(lsum*np, r);
dE1   = zeros(lsum*np, m);
dH1   = zeros(m*s*np, lmax);
dD1   = zeros(m*s*np, r);
dQ1   = zeros(m*s*np, m);

dx0i = zeros(lmax*np,1);
dP0i  = zeros(lmax*np,l);
dB1i  = zeros(m*np,m);
dPhi0 = zeros(l*np,lmax);
dGam0 = zeros(l*np,r);
dQ0 = zeros(l*np,l);

%if MV
%   x00 = x0;
%   P00 = P0;
%   Phibb0 = eye(p,p);
%   dPhibb0 = zeros(p*np,p);
%   WW  = zeros(p,p); WZ = zeros(p,1);
%   dWW = zeros(p*np,p); dWZ = zeros(p*np,1);
%   dz1i = zeros(m*np,1);
%end

dK1i = zeros(lmax*np,m);
Pc11 = zeros(l, l);
Pc21 = zeros(lmax*np,lmax);
Pc23 = zeros(lmax*(np+1)*np/2, lmax);

for i = 1:np
%
    j = i-1;

    [dPhi1(j*lsum+1:i*lsum,:),dGam,dE1(j*lsum+1:i*lsum,:),...
     dH1(j*s*m+1:i*s*m,:),dD,dQ1(j*s*m+1:i*s*m,:)] = ssdvper(theta,din,index(i));

    if r
       dGam1(j*lsum+1:i*lsum,:) = dGam;
       dD1(j*s*m+1:i*s*m,:) = dD;
    end
%
end


%% Condiciones iniciales

k = per1-1;

for i=1:s
%
    if k == 0, k = s; end
    E = E1(kl(1,k):kl(2,k),:); Q = Q1(km(1,k):km(2,k),:);
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    Q0 = Q0 + Phi0*E*Q*E'*Phi0';
    if r, Gam0 = Gam0 + Phi0*Gam1(kl(1,k):kl(2,k),:); end

    for kk=1:np
       ll = kk-1; 
       dPhi = dPhi1(ll*lsum+kl(1,k):ll*lsum+kl(2,k),1:kn(k));
       dE = dE1(ll*lsum+kl(1,k):ll*lsum+kl(2,k),:);
       dQ = dQ1(ll*m*s+km(1,k):ll*m*s+km(2,k),:);
       dQ00 = dPhi0(ll*l+1:kk*l,1:kn(k+1))*E*Q*E'*Phi0' + Phi0*dE*Q*E'*Phi0';
       dQ0(ll*l+1:kk*l,:) = dQ0(ll*l+1:kk*l,:) + Phi0*E*dQ*E'*Phi0' + dQ00 + dQ00';

       if r
          dGam = dGam1(ll*lsum+kl(1,k):ll*lsum+kl(2,k),:);
          dGam0(ll*l+1:kk*l,:) = dGam0(ll*l+1:kk*l,:) + dPhi0(ll*l+1:kk*l,1:kn(k+1))*Gam1(kl(1,k):kl(2,k),:) + Phi0*dGam;
       end           
   
       dPhi0(ll*l+1:kk*l,1:kn(k)) = dPhi0(ll*l+1:kk*l,1:kn(k+1))*Phi1(kl(1,k):kl(2,k),1:kn(k)) + Phi0*dPhi;
    end

    Phi0 = Phi0*Phi;
    k = k-1;
%
end

[x0, P0] = lfmodini(Phi0,Gam0,eye(l),[],[],[],Q0,[],[],[], 1);


if r %% | MV
   xc  = x0;
   dxc = zeros(lmax*np,1);
   dzc = zeros(m*np,1);
else
   Pc11 = Pc11 + x0*x0';
end

dGam00 = [];

for i = 1:np

    j = i-1;

    if r, dGam00 = dGam0(j*l+1:i*l,1:l); end
 
    [dx0i(j*lmax+1:j*lmax+l,:), dP0i(j*lmax+1:j*lmax+l,1:l)] = ...
       gmodini(Phi0,dPhi0(j*l+1:i*l,1:l),...
               Gam0, dGam00,eye(l),zeros(l),...
               [],[],[],[],[],[],...
               Q0,dQ0(j*l+1:i*l,1:l),[],[],[],[],[],1);

    if ~r %% & ~MV
    %
        Pc21(j*lmax+1:j*lmax+l,1:l) = dx0i(j*lmax+1:j*lmax+l,:)*x0';

        for kk=1:i
            ll = kk-1;
            ptri = (j*i/2 + ll)*lmax + 1;
            ptrf = ptri + l - 1;

            Pc23(ptri:ptrf,:) = dx0i(j*lmax+1:j*lmax+l,:)*dx0i(ll*lmax+1:ll*lmax+l,:)';
        end
    %
    else  %% if ~MV
       dxc(j*lmax+1:j*lmax+l,:) = dx0i(j*lmax+1:j*lmax+l,:);
    end
%
end

k = per1;

for t = 1:n  % main loop
%
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    Q   = Q1(km(1,k):km(2,k),:);

    B1   = H*P0*H' + Q;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = (Phi*P0*H' + E*Q)*iB1;
    Phib  = Phi - K1*H;
    if r
       Gam   = Gam1(kl(1,k):kl(2,k),:);
       D     = D1(km(1,k):km(2,k),:);
       Gamb   = Gam  - K1*D;
    end

%    if MV
%       HPhi= H*Phibb0;
%       if r
%          z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
%       else
%          z1  = z(t,:)' - H*x0;
%       end
%    end

    for i = 1:np
    %
        j = i-1;

        dPhi = dPhi1(j*lsum+kl(1,k):j*lsum+kl(2,k),1:kn(k));
        dE   = dE1(j*lsum+kl(1,k):j*lsum+kl(2,k),:);
        dH   = dH1(j*s*m+km(1,k):j*s*m+km(2,k),1:kn(k));
        dQ   = dQ1(j*s*m+km(1,k):j*s*m+km(2,k),:);

        dB1i(j*m+1:i*m,:) = dH*P0*H'+ H*dP0i(j*lmax+1:j*lmax+kn(k),1:kn(k))*H'+ H*P0*dH' + dQ;

        dK1i(j*lmax+1:j*lmax+kn(k+1),:) = (dPhi*P0*H' + Phi*dP0i(j*lmax+1:j*lmax+kn(k),1:kn(k))*H' + ...
                 Phi*P0*dH' + dE*Q + E*dQ - K1*dB1i(j*m+1:i*m,:))*iB1;

        dPhibi= dPhi - dK1i(j*lmax+1:j*lmax+kn(k+1),:)*H - K1*dH;

%        if MV
%           if r
%              dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:) - ...
%                                   dDi(j*sD+1:i*sD,:)*z(t,m+1:m+r)';
%
%              dGambi  = dGami(j*sGam+1:i*sGam,:) - dK1i(j*p+1:i*p,:)*D - K1*dDi(j*sD+1:i*sD,:);
% 
%              dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
%                                  dGambi*z(t,m+1:m+r)' + dK1i(j*p+1:i*p,:)*z(t,1:m)';
%           else
%              dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:);
%
%              dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
%                                  dK1i(j*p+1:i*p,:)*z(t,1:m)';
%           end
%        end

         dP0i(j*lmax+1:j*lmax+kn(k+1),1:kn(k+1)) = dPhibi*P0*Phib' + Phib*dP0i(j*lmax+1:j*lmax+kn(k),1:kn(k))*Phib' + Phib*P0*dPhibi' + ...
                    dE*Q*E' + E*dQ*E' + E*Q*dE' + dK1i(j*lmax+1:j*lmax+kn(k+1),:)*Q*K1' + ...
                    K1*dQ*K1' + K1*Q*dK1i(j*lmax+1:j*lmax+kn(k+1),:)' - ...
                    dK1i(j*lmax+1:j*lmax+kn(k+1),:)*Q*E' - K1*dQ*E' - K1*Q*dE' - ...
                    dE*Q*K1' - E*dQ*K1' - E*Q*dK1i(j*lmax+1:j*lmax+kn(k+1),:)';

         Phibi = dPhi - K1*dH;

         if r  %% & ~MV
            dGam   = dGam1(j*lsum+kl(1,k):j*lsum+kl(2,k),:);
            dD     = dD1(j*s*m+km(1,k):j*s*m+km(2,k),:);

            dzc(j*m+1:i*m,:) = -dH*xc -H*dxc(j*lmax+1:j*lmax+kn(k),:) - dD*z(t,m+1:m+r)';

            dxc(j*lmax+1:j*lmax+kn(k+1),:) = Phibi*xc + Phib*dxc(j*lmax+1:j*lmax+kn(k),:) + ...
                                              (dGam - K1*dD)*z(t,m+1:m+r)';
         end

         for kk=1:i
         %
             ll = kk-1;

             dPhij = dPhi1(ll*lsum+kl(1,k):ll*lsum+kl(2,k),1:kn(k));
             dEj   = dE1(ll*lsum+kl(1,k):ll*lsum+kl(2,k),:);
             dHj   = dH1(ll*s*m+km(1,k):ll*s*m+km(2,k),1:kn(k));
             dQj   = dQ1(ll*s*m+km(1,k):ll*s*m+km(2,k),:);

             ptri = (j*i/2 + ll)*lmax;

             Phibj = dPhij - K1*dHj;
 
             Bij = dH*Pc11*dHj' + H * Pc21(j*lmax+1:j*lmax+kn(k),1:kn(k))*dHj' + ...
                     dH*Pc21(ll*lmax+1:ll*lmax+kn(k),1:kn(k))'*H' + H*Pc23(ptri+1:ptri+kn(k),1:kn(k))*H';

             Pc23(ptri+1:ptri+kn(k+1),1:kn(k+1)) = Phibi*Pc11*Phibj' + Phib*Pc21(j*lmax+1:j*lmax+kn(k),1:kn(k))*Phibj' + ...
                         Phibi*Pc21(ll*lmax+1:ll*lmax+kn(k),1:kn(k))'*Phib' + Phib*Pc23(ptri+1:ptri+kn(k),1:kn(k))*Phib' + ...
                         dK1i(j*lmax+1:j*lmax+kn(k+1),:)*B1*dK1i(ll*lmax+1:ll*lmax+kn(k+1),:)';

             if r %%  & ~MV
                Im(i,kk) = Im(i,kk) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(ll*m+1:kk*m,:)'*iB1) ) + ...
                                          trace(iB1*(Bij + dzc(j*m+1:i*m,:)*dzc(ll*m+1:kk*m,:)'));
             else

                Im(i,kk) = Im(i,kk) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(ll*m+1:kk*m,:)'*iB1) ) + ...
                                          trace(iB1*Bij);
             end
          %
          end

%        if MV
%           dHPhi = dHi(j*m+1:i*m,:)*Phibb0 + H*dPhibb0(j*p+1:i*p,:);
%           dWW(j*p+1:i*p,:) = dWW(j*p+1:i*p,:) + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1i(j*m+1:i*m,:)*iB1*HPhi + HPhi'*iB1*dHPhi;
%           dWZ(j*p+1:i*p,:) = dWZ(j*p+1:i*p,:) + dHPhi'*iB1*z1 - HPhi'*iB1*dB1i(j*m+1:i*m,:)*iB1*z1 + HPhi'*iB1*dz1i(j*m+1:i*m,:);
%           dPhibb0(j*p+1:i*p,:) = dPhibi*Phibb0 + Phib*dPhibb0(j*p+1:i*p,:);
%        end
    %
    end  % i

%    if MV
%       WW  = WW + HPhi'*iB1*HPhi;
%       WZ  = WZ + HPhi'*iB1*z1;
%       Phibb0 = Phib*Phibb0;
%    end

%     if MV
%          if r
%             x0 = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,1:m)';
%          else
%             x0 = Phi*x0 + K1*(z(t,:)' - H*x0);
%          end
%       end

    for i=1:np
        j = i-1;
        dPhi = dPhi1(j*lsum+kl(1,k):j*lsum+kl(2,k),1:kn(k));
        dH   = dH1(j*s*m+km(1,k):j*s*m+km(2,k),1:kn(k));
        Phibi = dPhi - K1*dH;
        Pc21(j*lmax+1:j*lmax+kn(k+1),1:kn(k+1)) = Phibi*Pc11*Phi' + Phib*Pc21(j*lmax+1:j*lmax+kn(k),1:kn(k))*Phi' + dK1i(j*lmax+1:j*lmax+kn(k+1),:)*B1*K1';
    end

    Pc11 = Phi*Pc11*Phi' + K1*B1*K1';

    if r %% & ~MV
       xc = Phi*xc + Gam*z(t,m+1:m+r)';
    end

    P0 = Phib*P0*Phib' + E*Q*E' + K1*Q*K1' - ...
         K1*Q*E' - E*Q*K1';

    if k == s, k = 1; else, k=k+1; end
%
end % t

Im = Im + tril(Im,-1)';

if min(eig(Im)) <= 0, e4warn(8); end

varm = pinv(Im);

dts   = sqrt(diag(varm));
corrm = diag(1./dts)*varm*diag(1./dts);
E4OPTION(1) = oldfil;
