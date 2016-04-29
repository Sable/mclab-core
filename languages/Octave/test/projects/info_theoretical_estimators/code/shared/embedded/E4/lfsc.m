function [f, innov, ssvect] = lfsc(theta, din, z)
% LFSC     - Computes the likelihood function of a time-varying parameters model.
%    [f, innov, ssvect] = lfsc(theta, din, z)
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% z      > matrix of observable variables.
% f      < value of the likelihood function.
% innov  < (optional) stores the sequence of innovations.
% ssvect < (optional) stores the sequence of values of the state vector.
%
% 2/4/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if nargin < 3,  e4error(3); end

saveinn = 0; if nargout >= 2, saveinn = 1; end
savessv = 0; if nargout == 3, savessv = 1; end
scaleb = E4OPTION(2);
vcond  = E4OPTION(3);
econd  = E4OPTION(4);
zeps   = E4OPTION(15);

[Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din);
n = size(z,1);
m = size(Hv,1);
r = size(Gam,2);
rv = size(Dv,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end
if econd == 2 & ~saveinn, MV = 1; else, MV = 0; end

if size(z,2) ~= m+r+1, e4error(11); end

if vcond == 4
   [x0, Sigm, ign, ign, ign, P0, iSigm, nonstat] = lfscini(Phi,Gam,E,Hv,Dv,Cv,Q,S,R,Hf,Df,Cf,z,MV);
   P0 = zeros(size(Sigm));
else
   [x0, P0] = lfscini(Phi,Gam,E,Hv,Dv,Cv,Q,S,R,Hf,Df,Cf,z,MV);
end
EQEt=E*Q*E';

if MV | vcond == 4
   l = size(Phi,1);
   Phibb0 = eye(l,l);
   WW  = zeros(l,l); WZ = zeros(l,1);
end

if savessv
  ssvect = zeros(n+1,size(x0,1)); ssvect(1,:) = x0';
end
if saveinn, innov = zeros(n, 1); end 

ff = 0.0;
D = Df;

for t = 1:n  % main loop
%
  % Construct time varying observer
    H = [z(t,2:m+1)*Hv Hf]; C = [z(t,2:m+1)*Cv Cf];
    if r & rv, D = [z(t,2:m+1)*Dv Df]; end
    CRCt = C*R*C'; ESCt = E*S*C';

    if r, z1  = z(t,1)' - H*x0 - D*z(t,m+2:m+r+1)';
    else  z1  = z(t,1)' - H*x0; end
    if saveinn, innov(t,:) = z1'; end

    B1  = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = ((Phi*P0*H' + ESCt)/U)/U';
    if r, x1  = Phi*x0 + Gam*z(t,m+2:m+r+1)' + K1*z1;
    else  x1  = Phi*x0 + K1*z1; end
    Phib= Phi - K1*H;
    P1  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';    
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
    P0  = P1; x0  = x1; 
%
end  % t

if MV | vcond == 4
   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
end

if vcond == 4  % stationary system
%
   if ~nonstat
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

% m = 1;
f = 0.5*(ff + n*log(2*pi));
