function [z1, vT, wT, bvect, vvT, vwT] = fisrpss(theta, din, z, per1)
% FISRISS  - Fixed interval smoother for noise terms with missing data.
%    [z1, vT, wT, vz1, vvT, vwT] = fisrpss(theta, din, z, per1)
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
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if nargin < 3,  e4error(3); end

scaleb = E4OPTION(2);
econd   = E4OPTION(4);

[kn, Phi1, Gam1, E1, H1, D1, Q1] = thd2ssp(theta, din);
[H_D, type, m, r, s] = e4gthead(din);
if size(z,2) ~= m+r, e4error(11); end
n = size(z,1);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

ns = fix(n/s); rs = rem(n,s);
rss = ones(s,1)*ns;
rss(per1:min(rs-1+per1,s)) = ones(min(rs,s-per1+1),1) + ns;
rss(1:max(rs-1-s+per1,0)) = ones(max(rs-1-s+per1,0),1) + ns;

kl = cumsum([1;kn]);
kl = [kl(1:s)'; (kl(2:s+1)-1)'];
km = [[1:m:(s-1)*m+1]; [m:m:s*m]];
kc = kn;
kn = [kn(s);kn];
l  = kn(per1);
lmax = max(kc);

Phi0 = eye(l);
Q0   = zeros(l);
Gam0 = zeros(l,r);

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

if ~nonstat, iSigm = pinv(Sigm); end

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
P0 = zeros(l);
z1 = ones(n,m)*NaN; pvect = zeros(n*lmax, 2*m); bvect = zeros(n*m, 2*m);
w = m; v = m;
wT = zeros(n,w);
vT = zeros(n,v);
C = eye(m);

k = per1;
l1 = l;

for t = 1:n  % filter loop
%
    l  = kc(k);
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    Q   = Q1(km(1,k):km(2,k),:);
    if r
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);
    end
    S = Q;    
    R = Q;
        

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
       pvect((t-1)*lmax+1:(t-1)*lmax+l,1:nobs) = K1;    
       pvect((t-1)*lmax+1:(t-1)*lmax+l1,nobs+1:2*nobs) = Y';       
       bvect((t-1)*m+1:(t-1)*m+nobs,1:2*nobs) = [B1 iB1];

       if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + K1*z1(t,trueobs)';
       else  x0  = Phi*x0 + K1*z1(t,trueobs)'; end
       Phib= Phi - K1*Hb;
       P0  = Phib*P0*Phib' + E*Q*E' + K1*Cb*R*Cb'*K1' - K1*(E*S*Cb')' - E*S*Cb'*K1';

       WW  = WW + Y'*iB1*Y;
       WZ  = WZ + Y'*iB1*z1(t,trueobs)';
       Phibb0 = Phib*Phibb0;
    else
    %
       if r, x0  = Phi*x0 + Gam*z(t,m+1:m+r)';
       else  x0  = Phi*x0; end
       P0 = Phi*P0*Phi' + E*Q*E';
       Phibb0 = Phi*Phibb0;
    %
    end
    
    if k == s, k = 1; else, k=k+1; end

%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

if nargout > 3
   vwT = zeros(w*n,w);
   vvT = zeros(v*n,v);
end

NaNs = eye(m)*NaN;

k = k - 1;
l  = kc(k);
Psi = zeros(l,l1); rt = zeros(l,1); Rt = zeros(l);

for t = n:-1:1  % smoothing loop
%
    if k == 0, k = s; end
    l  = kc(k);
    Phi = Phi1(kl(1,k):kl(2,k),1:kn(k));
    E   = E1(kl(1,k):kl(2,k),:);
    H   = H1(km(1,k):km(2,k),1:kn(k));
    Q   = Q1(km(1,k):km(2,k),:);
    if r
       Gam = Gam1(kl(1,k):kl(2,k),:);
       D   = D1(km(1,k):km(2,k),:);
    end
    S = Q;    
    R = Q;

   % Construct H(t)
   trueobs = find(~isnan(z(t,1:m)'));
   nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
   if nmiss & nobs, Ht = Ht(trueobs,:); end
   Hb = Ht*H; Cb = Ht*C; 

   if nobs
   %
      % get matrices from filter
      K1 = pvect((t-1)*lmax+1:(t-1)*lmax+l,1:nobs);
      Y  = pvect((t-1)*lmax+1:(t-1)*lmax+l1,nobs+1:2*nobs)';
      B1 = bvect((t-1)*m+1:(t-1)*m+nobs,1:nobs);
      iB1= bvect((t-1)*m+1:(t-1)*m+nobs,nobs+1:2*nobs);
      bvect((t-1)*m+1:t*m,1:m) = NaNs;
      % smoother

      Aw = Q*E' - S*Cb'*K1'; Av = S'*E' - R*Cb'*K1';

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

      Aw = Q*E'; Av = S'*E';

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
   k = k-1;
%
end

if nargout > 3, bvect = bvect(:,1:m); end