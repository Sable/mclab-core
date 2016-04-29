function [zhat, bt, e] = fissc(theta, din, z)
% FISSC    - Fixed-interval smoother for time-varying parameters models.
%    [beta, Vbeta, e] = fissc(theta, din, z)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% beta  < smoothed values of the parameters.
% Vbeta < covariance matrix of the parameters.
% e     < residuals.
%
% 30/5/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if nargin < 3,  e4error(3); end

scaleb  = E4OPTION(2);
econd   = E4OPTION(4);

[Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din);
n = size(z,1); m = size(Hv,1);
r = size(Gam,2);
rv = size(Dv,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if size(z,2) ~= m+r+1, e4error(11); end

l = size(Phi,1);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(z(:,m+2:m+r+1))';
   else
      u0 = z(1,m+2:m+r+1)';
   end

   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x0, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0);
end

if ~nonstat, iSigm = pinv(Sigm); end

Sigm = [Sigm zeros(l,m); zeros(m,l+m)];
iSigm = [iSigm zeros(l,m); zeros(m,l+m)];
x0 = [x0; zeros(m,1)];

Phi = [Phi zeros(l,m); Hv zeros(m,size(Hf,2)+m)];
if r, Gam = [Gam; [Dv zeros(size(Hv,1),size(Df,2))]]; else  Gam = []; end
v   =  size(Cv,2);
E = [E zeros(l,v); zeros(m,size(E,2)) Cv];
Q = [Q S(:,1:v); S(:,1:v)' R(1:v,1:v)]; S = [S; R(1:v,:)];
ltr = l; l = size(Phi,1); I = eye(l,l);

EQEt=E*Q*E';

Phibb0 = eye(l);
WW  = zeros(l); WZ = zeros(l,1);
V = eye(l);
P0 = zeros(l);
xvect = zeros(n, l+1 ); pvect = zeros(n*l, 2*(l+1));
bvect = zeros(n, 1);
D = Df;

for t = 1:n  % filter loop
%
  % Construct time varying observer
    H = [z(t,2:m+1)*Hv Hf zeros(1,m)]; C = [z(t,2:m+1)*Cv Cf];
    if r & rv, D = [z(t,2:m+1)*Dv Df]; end
    CRCt = C*R*C'; ESCt = E*S*C';

    if r, z1  = z(t,1)' - H*x0 - D*z(t,m+2:m+r+1)';
    else  z1  = z(t,1)' - H*x0; end

    B1  = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    Y = H*Phibb0;

    % save matrices for smoothing step
    xvect(t,:) = [x0' z1'];
    pvect((t-1)*l+1:t*l,:) = [P0 V K1 Y'];
    bvect(t) = iB1;

    if r, x0  = Phi*x0 + Gam*z(t,m+2:m+r+1)' + K1*z1;
    else  x0  = Phi*x0 + K1*z1; end
    Phib= Phi - K1*H;
    P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
    WW  = WW + Y'*iB1*Y;
    WZ  = WZ + Y'*iB1*z1;
    Phibb0 = Phib*Phibb0;
    V = Phi*V - K1*Y;
%
end  % t

if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end

P1T = pinv(iSigm + WW);

if econd ~= 2, x1T = P1T*WZ; else, x1T = pinv(WW)*WZ; end

Psi = zeros(l); rt = zeros(l,1); Rt = zeros(l);

bt = zeros(n*m,m);
P0 = P0 + V*P1T*V';
x0 = x0 + V*x1T;
bt((n-1)*m+1:n*m,:) = P0(ltr+1:l,ltr+1:l);
zhat = x0(ltr+1:l)';

for t = n:-1:2  % Smoother x(t/T), P(t/T)
%
   % Construct time varying observer
   H = [z(t,2:m+1)*Hv Hf zeros(1,m)];

   % get matrices from filter
   x0 = xvect(t,1:l)'; z1 = xvect(t,l+1:l+1)';
   P0 = pvect((t-1)*l+1:t*l,1:l);
   V  = pvect((t-1)*l+1:t*l,l+1:2*l);
   K1 = pvect((t-1)*l+1:t*l,2*l+1:2*l+1);
   Y  = pvect((t-1)*l+1:t*l,2*(l+1):2*(l+1))';
   iB1= bvect(t);

   % smoother
   Phib = Phi - K1*H;
   Psi = H'*iB1*Y + Phib'*Psi;
   rt  = H'*iB1*z1 + Phib'*rt;
   Rt  = H'*iB1*H  + Phib'*Rt*Phib;    
   VT = V - P0*Psi;
   PT = P0 - P0*Rt*P0 + VT*P1T*VT';

   xvect(t,1:l) = (x0 + P0*rt + VT*x1T)';
   bt((t-2)*m+1:(t-1)*m,:) = PT(ltr+1:l,ltr+1:l);
%
end

zhat = [xvect(2:n,ltr+1:l);zhat];
xvect(1,1:ltr) = x1T(1:ltr)' + xvect(1,1:ltr);

if m > 1
   e = z(:,1) - sum((z(:,2:m+1).*zhat)')';
else
   e = z(:,1) - z(:,2:m+1).*zhat;
end   
if size(Df,1), e = e - z(:,m+size(Dv,2)+2:m+r+1)*Df'; end
if size(Hf,1), e = e - xvect(:,size(Hv,2)+1:ltr)*Hf'; end
