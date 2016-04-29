function [ dts, corrm, varm, Im ] = imiss(theta, din, z, aprox)
% IMISS - Computes the exact information matrix under normality,
% allowing for missing values in the endogenous variables
% [dts, corrm, varm, Im] = imiss(theta, din, z, aprox)
% theta > parameter vector.
% din > matrix which stores a description of the model dynamics.
% z > matrix of observable variables.
% aprox > if the value of this parameter is not zero, computes the
% approximation given in Watson and Engle (1983).
% dts < vector of standard deviations.
% corrm < correlation matrix.
% varm < covariance matrix.
% Im < information matrix.
% If Im is not positive-definite, it is perturbed before computing
% the rest of the matrices.
%
% 27/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if nargin < 3, e4error(3); end

if nargin < 4, aprox = 0; elseif aprox, e4warn(7); end

oldfil = E4OPTION(1); E4OPTION(1) = 1;
scaleb = E4OPTION(2);
zeps   = E4OPTION(15);
econd  = E4OPTION(4);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
n = size(z,1);
m = size(H,1);
r = size(Gam,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end
if econd == 2 & ~aprox, MV = 1; else, MV = 0; end

if size(z,2) ~= m+r, e4error(11); end

[x0, P0] = lfmissin(Phi,Gam,E,H,D,C,Q,S,R,z, MV);
EQEt=E*Q*E';
H0 = H; D0 = D; C0 = C;

if size(theta,2) > 1  % look for fixed parameters in theta
   index = find(theta(:,2) == 0);
else
   index = (1:size(theta,1))';
end

np = size(index,1);
p  = size(P0,1);
Im = zeros(np, np);

dPhii = zeros(p*np, p);
sGam  = size(Gam,1);
dGami = zeros(sGam*np, size(Gam,2));
sE    = size(E,1);
dEi   = zeros(sE*np, size(E,2));
dHi   = zeros(m*np, p);
sD    = size(D,1);
dDi   = zeros(sD*np, size(D,2));
sC    = size(C,1);
dCi   = zeros(sC*np, size(C,2));
sQ    = size(Q,1);
dQi   = zeros(sQ*np, size(Q,2));
sS    = size(S,1);
dSi   = zeros(sS*np, size(S,2));
sR    = size(R,1);
dRi   = zeros(sR*np, size(R,2));

dx0i = zeros(p*np,1);
dP0i  = zeros(p*np,p);
dB1i  = zeros(m*np,m);

if MV
   x00 = x0;
   P00 = P0;
   Phibb0  = eye(p,p);
   dPhibb0 = zeros(p*np,p);
   WW  = zeros(p,p);    WZ  = zeros(p,1);
   dWW = zeros(p*np,p); dWZ = zeros(p*np,1);
end

if aprox | MV
   dz1i = zeros(m*np,1); 
end

if ~aprox
%
   dK1i =  zeros(p*np,m);
   Pc11 =  zeros(p, p);
   Pc21 =  zeros(p*np,p);
   Pc23 =  zeros(p*(np+1)*np/2, p);

   if r | MV
      xc  = x0;
      dxc = zeros(p*np,1);
      dzc = zeros(m*np,1);
   else
      Pc11 = Pc11 + x0*x0';
   end
%
end

for i = 1:np
%
    j = i-1;
    [dPhii(j*p+1:i*p,:),dGam,dEi(j*sE+1:i*sE,:),...
     dHi(j*m+1:i*m,:),dD,dCi(j*sC+1:i*sC,:),...
     dQi(j*sQ+1:i*sQ,:),dSi(j*sS+1:i*sS,:),dRi(j*sR+1:i*sR,:)] =...
         ss_dv(theta,din,index(i));

    if r
       dGami(j*sGam+1:i*sGam,:) = dGam;
       dDi(j*sD+1:i*sD,:) = dD;
    end

    [dx0i(j*p+1:i*p,:), dP0i(j*p+1:i*p,:)] = ...
       gmissin(Phi,dPhii(j*p+1:i*p,:),...
               Gam, dGami(j*sGam+1:i*sGam,:),E,dEi(j*sE+1:i*sE,:),...
               H,dHi(j*m+1:i*m,:),D,dDi(j*sD+1:i*sD,:),C,dCi(j*sC+1:i*sC,:),...
               Q,dQi(j*sQ+1:i*sQ,:),S,dSi(j*sS+1:i*sS,:),...
               R,dRi(j*sR+1:i*sR,:),z,MV);

    if ~aprox
    %
       if ~r & ~MV
       %      
          Pc21(j*p+1:i*p,:) = dx0i(j*p+1:i*p,:)*x0';

          for k=1:i
              l = k-1;
              ptri = (j*i/2 + l)*p + 1;
              ptrf = ptri + p - 1;
        
              Pc23(ptri:ptrf,:) = dx0i(j*p+1:i*p,:)*dx0i(l*p+1:k*p,:)';
          end
       %
       elseif ~MV
          dxc(j*p+1:i*p,:) = dx0i(j*p+1:i*p,:);
       end
    %
    end
%
end

for t = 1:n  % main loop
%
  % Construct H(t)
    trueobs = find(~isnan(z(t,1:m)'));
    nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
    if nmiss & nobs, Ht = Ht(trueobs,:); end
  
    if nobs
    %
       H = Ht*H0; C = Ht*C0;
       if r, D = Ht*D0; end

       B1   = H*P0*H' + C*R*C';
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1); end
       iB1 = (eye(size(U))/U)/U';

       K1   = (Phi*P0*H' + E*S*C')*iB1;
       Phib = Phi - K1*H;
       if r, Gamb = Gam  - K1*D; end

       if MV
          HPhi= H*Phibb0;
          if r
             z1  = z(t,trueobs)' - H*x0 - D*z(t,m+1:m+r)';
          else
             z1  = z(t,trueobs)' - H*x0;
          end
       %
       end

       for i = 1:np
       %
         j = i-1;

	     dH = Ht*dHi(j*m+1:i*m,:);
	     dC = Ht*dCi(j*sC+1:i*sC,:);
         if r, dD = Ht*dDi(j*sD+1:i*sD,:); end

         dB1i(j*m+1:j*m+nobs,1:nobs) = dH*P0*H'+ H*dP0i(j*p+1:i*p,:)*H'+ H*P0*dH' + ...
                                       dC*R*C' + C*dRi(j*sR+1:i*sR,:)*C' + C*R*dC';

         if aprox
         %
            dK1i= (dPhii(j*p+1:i*p,:)*P0*H' + Phi*dP0i(j*p+1:i*p,:)*H' + ...
                  Phi*P0*dH' + ...
                  dEi(j*sE+1:i*sE,:)*S*C' + E*dSi(j*sS+1:i*sS,:)*C' + ...
                  E*S*dC' - K1*dB1i(j*m+1:j*m+nobs,1:nobs))*iB1;

            dPhibi= dPhii(j*p+1:i*p,:) - dK1i*H - K1*dH;

            if r
               dz1i(j*m+1:j*m+nobs,:) = -dH*x0 -H*dx0i(j*p+1:i*p,:) - ...
                                         dD*z(t,m+1:m+r)';

               dGambi  = dGami(j*sGam+1:i*sGam,:) - dK1i*D - K1*dD;

               dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                   dGambi*z(t,m+1:m+r)' + dK1i*z(t,trueobs)';
            else
               dz1i(j*m+1:j*m+nobs,:) = -dH*x0 -H*dx0i(j*p+1:i*p,:);

               dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                   dK1i*z(t,trueobs)';
            end

            dP0i(j*p+1:i*p,:) = dPhibi*P0*Phib' + Phib*dP0i(j*p+1:i*p,:)*Phib' + Phib*P0*dPhibi' + ...
                     dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                     E*Q*dEi(j*sE+1:i*sE,:)' + dK1i*C*R*C'*K1' + ...
                     K1*dC*R*C'*K1' + K1*C*dRi(j*sR+1:i*sR,:)*C'*K1' + ...
                     K1*C*R*dC'*K1' + K1*C*R*C'*dK1i' - ...
                     dK1i*(E*S*C')' - K1*dC*S'*E' - ...
                     K1*C*dSi(j*sS+1:i*sS,:)'*E' - K1*C*S'*dEi(j*sE+1:i*sE,:)' - ...
                     dEi(j*sE+1:i*sE,:)*S*C'*K1' - E*dSi(j*sS+1:i*sS,:)*C'*K1' - ...
                     E*S*dC'*K1' - E*S*C'*dK1i';
         
            for k=1:i
                l = k-1;
                Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:j*m+nobs,1:nobs)'*iB1)*(dB1i(l*m+1:l*m+nobs,1:nobs)'*iB1) ) + ...
                 	                           (dz1i(j*m+1:j*m+nobs,:)'*iB1*dz1i(l*m+1:l*m+nobs,:));
            end
         %
         else
         %
            dK1i(j*p+1:i*p,1:nobs) = (dPhii(j*p+1:i*p,:)*P0*H' + Phi*dP0i(j*p+1:i*p,:)*H' + ...
                  Phi*P0*dH' + dEi(j*sE+1:i*sE,:)*S*C' + E*dSi(j*sS+1:i*sS,:)*C' + ...
                  E*S*dC' - K1*dB1i(j*m+1:j*m+nobs,1:nobs))*iB1;

            dPhibi= dPhii(j*p+1:i*p,:) - dK1i(j*p+1:i*p,1:nobs)*H - K1*dH;

            if MV
               if r
                 dz1i(j*m+1:j*m+nobs,:) = -dH*x0 -H*dx0i(j*p+1:i*p,:) - ...
                                      dD*z(t,m+1:m+r)';

                 dGambi  = dGami(j*sGam+1:i*sGam,:) - dK1i(j*p+1:i*p,1:nobs)*D - K1*dD;

                 dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                     dGambi*z(t,m+1:m+r)' + dK1i(j*p+1:i*p,1:nobs)*z(t,trueobs)';
               else
                 dz1i(j*m+1:j*m+nobs,:) = -dH*x0 -H*dx0i(j*p+1:i*p,:);

                 dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                     dK1i(j*p+1:i*p,1:nobs)*z(t,trueobs)';
               end
            end
 
            dP0i(j*p+1:i*p,:) = dPhibi*P0*Phib' + Phib*dP0i(j*p+1:i*p,:)*Phib' + Phib*P0*dPhibi' + ...
                     dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                     E*Q*dEi(j*sE+1:i*sE,:)' + dK1i(j*p+1:i*p,1:nobs)*C*R*C'*K1' + ...
                     K1*dC*R*C'*K1' + K1*C*dRi(j*sR+1:i*sR,:)*C'*K1' + ...
                     K1*C*R*dC'*K1' + K1*C*R*C'*dK1i(j*p+1:i*p,1:nobs)' - ...
                     dK1i(j*p+1:i*p,1:nobs)*(E*S*C')' - K1*dC*S'*E' - ...
                     K1*C*dSi(j*sS+1:i*sS,:)'*E' - K1*C*S'*dEi(j*sE+1:i*sE,:)' - ...
                     dEi(j*sE+1:i*sE,:)*S*C'*K1' - E*dSi(j*sS+1:i*sS,:)*C'*K1' - ...
                     E*S*dC'*K1' - E*S*C'*dK1i(j*p+1:i*p,1:nobs)';
            
       	    Phibi = dPhii(j*p+1:i*p,:) - K1*dH;

            if r & ~MV
       	       dzc(j*m+1:j*m+nobs,:) = -dH*xc -H*dxc(j*p+1:i*p,:) - ...
                                            dD*z(t,m+1:m+r)';

     	       dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:) + ...
                                  (dGami(j*sGam+1:i*sGam,:) - K1*dD)*z(t,m+1:m+r)';
       	    end

            for k=1:i
            %
    	        l = k-1;
                ptri = (j*i/2 + l)*p + 1;
                ptrf = ptri + p - 1;

       	        Phibj = dPhii(l*p+1:k*p,:) - K1*dH;
             
                Bij = dH*Pc11*dH' + ...
                      H * Pc21(j*p+1:i*p,:)*dH' + ...
                dH*Pc21(l*p+1:k*p,:)'*H' + ...
                      H*Pc23(ptri:ptrf,:)*H';

    	        Pc23(ptri:ptrf,:) = Phibi*Pc11*Phibj' + Phib*Pc21(j*p+1:i*p,:)*Phibj' + ...
                          Phibi*Pc21(l*p+1:k*p,:)'*Phib' + Phib*Pc23(ptri:ptrf,:)*Phib' + ...
                          dK1i(j*p+1:i*p,1:nobs)*B1*dK1i(l*p+1:k*p,1:nobs)';

    	        if r & ~MV
                   Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:j*m+nobs,1:nobs)'*iB1)*(dB1i(l*m+1:l*m+nobs,1:nobs)'*iB1) ) + ...
 	      				   trace(iB1*(Bij + dzc(j*m+1:j*m+nobs,:)*dzc(l*m+1:l*m+nobs,:)'));
	            else
                   Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:j*m+nobs,1:nobs)'*iB1)*(dB1i(l*m+1:l*m+nobs,1:nobs)'*iB1) ) + ...
 	   		   		   trace(iB1*Bij);
	            end
            %
            end
         %
         end

         if MV
            dHPhi = dH*Phibb0 + H*dPhibb0(j*p+1:i*p,:);
            dWW(j*p+1:i*p,:) = dWW(j*p+1:i*p,:) + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1i(j*m+1:j*m+nobs,1:nobs)*iB1*HPhi + HPhi'*iB1*dHPhi;
            dWZ(j*p+1:i*p,:) = dWZ(j*p+1:i*p,:) + dHPhi'*iB1*z1 - HPhi'*iB1*dB1i(j*m+1:j*m+nobs,1:nobs)*iB1*z1 + HPhi'*iB1*dz1i(j*m+1:j*m+nobs,:);
            dPhibb0(j*p+1:i*p,:) = dPhibi*Phibb0 + Phib*dPhibb0(j*p+1:i*p,:);
         end
       %
       end  % i

       if MV
          WW  = WW + HPhi'*iB1*HPhi;
          WZ  = WZ + HPhi'*iB1*z1;
          Phibb0 = Phib*Phibb0;
       end

       if aprox
          if r
             x0 = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,trueobs)';
          else
             x0 = Phi*x0 + K1*(z(t,trueobs)' - H*x0);
          end
       else
       %
          if MV
             if r
                x0 = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,trueobs)';
             else
                x0 = Phi*x0 + K1*(z(t,trueobs)' - H*x0);
             end
          end

          for i=1:np
              j = i-1;
              Phibi = dPhii(j*p+1:i*p,:) - K1*dH;
              Pc21(j*p+1:i*p,:) = Phibi*Pc11*Phi' + Phib*Pc21(j*p+1:i*p,:)*Phi' + dK1i(j*p+1:i*p,1:nobs)*B1*K1';
          end

          Pc11 = Phi*Pc11*Phi' + K1*B1*K1';

          if r & ~MV
             xc = Phi*xc + Gam*z(t,m+1:m+r)';
          end
       %
       end

       P0 = Phib*P0*Phib' + EQEt + K1*C*R*C'*K1' - ...
            K1*(E*S*C')' - E*S*C'*K1';
    %
    else
    %
       for i = 1:np
       %
         j = i-1;

         if aprox
         %
            dPhibi= dPhii(j*p+1:i*p,:);

            if r
               dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phi*dx0i(j*p+1:i*p,:) + ...
                                   dGami(j*sGam+1:i*sGam,:)*z(t,m+1:m+r)';
            else
               dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phi*dx0i(j*p+1:i*p,:);
            end

            dP0i(j*p+1:i*p,:) = dPhibi*P0*Phi' + Phi*dP0i(j*p+1:i*p,:)*Phi' + Phi*P0*dPhibi' + ...
                     dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                     E*Q*dEi(j*sE+1:i*sE,:)';
         %         
         else
         %
            dPhibi= dPhii(j*p+1:i*p,:);

            if MV
               if r
                 dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phi*dx0i(j*p+1:i*p,:) + ...
                                     dGami(j*sGam+1:i*sGam,:)*z(t,m+1:m+r)';
               else
                 dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phi*dx0i(j*p+1:i*p,:);
               end
            end
 
            dP0i(j*p+1:i*p,:) = dPhibi*P0*Phi' + Phi*dP0i(j*p+1:i*p,:)*Phi' + Phi*P0*dPhibi' + ...
                     dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                     E*Q*dEi(j*sE+1:i*sE,:)';

       	    Phibi = dPhii(j*p+1:i*p,:);

            if r & ~MV
     	       dxc(j*p+1:i*p,:) = Phibi*xc + Phi*dxc(j*p+1:i*p,:) + ...
                                  dGami(j*sGam+1:i*sGam,:)*z(t,m+1:m+r)';
   	        end

            for k=1:i
	        l = k-1;
                ptri = (j*i/2 + l)*p + 1;
                ptrf = ptri + p - 1;

       	        Phibj = dPhii(l*p+1:k*p,:);
             
    	        Pc23(ptri:ptrf,:) = Phibi*Pc11*Phibj' + Phi*Pc21(j*p+1:i*p,:)*Phibj' + ...
                          Phibi*Pc21(l*p+1:k*p,:)'*Phi' + Phi*Pc23(ptri:ptrf,:)*Phi';
	        end
         %
         end

         if MV
            dPhibb0(j*p+1:i*p,:) = dPhibi*Phibb0 + Phi*dPhibb0(j*p+1:i*p,:);
         end
       %
       end  % i

       if MV
          Phibb0 = Phi*Phibb0;
       end

       if aprox
          if r
             x0 = Phi*x0 + Gam*z(t,m+1:m+r)';
          else
             x0 = Phi*x0;
          end
       else
       %
          if MV
             if r
                x0 = Phi*x0 + Gam*z(t,m+1:m+r)';
             else
                x0 = Phi*x0;
             end
          end

          for i=1:np
              j = i-1;
              Phibi = dPhii(j*p+1:i*p,:);
              Pc21(j*p+1:i*p,:) = Phibi*Pc11*Phi' + Phi*Pc21(j*p+1:i*p,:)*Phi';
          end

          Pc11 = Phi*Pc11*Phi';

          if r & ~MV
             xc = Phi*xc + Gam*z(t,m+1:m+r)';
          end
       %
       end

       P0 = Phi*P0*Phi' + EQEt;
    %
    end
%
end  % t


if MV
%
   WW = pinv(WW, zeps);
   xc = WW*WZ;
   for i = 1:np
       j = i-1;
       dxc(j*p+1:i*p,:) = WW*(dWZ(j*p+1:i*p,:)-dWW(j*p+1:i*p,:)*xc);
   end
   xc = xc + x00;
   P0 = P00;

   for t = 1:n
   %
     % Construct H(t)
       trueobs = find(~isnan(z(t,1:m)'));
       nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
       if nmiss & nobs, Ht = Ht(trueobs,:); end
  
       if nobs
       %
          H = Ht*H0; C = Ht*C0; 
          if r, D = Ht*D0; end

          B1   = H*P0*H' + C*R*C';
          if scaleb, U = cholp(B1, abs(B1));
          else       U = cholp(B1); end
          iB1 = (eye(size(U))/U)/U';

          K1  = (Phi*P0*H' + E*S*C')*iB1;
          Phib  = Phi - K1*H;
          if r, Gamb   = Gam  - K1*D; end

          for i = 1:np
          %
              j = i-1;

       	      dH = Ht*dHi(j*m+1:i*m,:);
    	      dC = Ht*dCi(j*sC+1:i*sC,:);
              if r, dD = Ht*dDi(j*sD+1:i*sD,:); end

              Phibi = dPhii(j*p+1:i*p,:) - K1*dH;

              if r
     	         dzc(j*m+1:j*m+nobs,:) = -dH*xc -H*dxc(j*p+1:i*p,:) - ...
                                     dD*z(t,m+1:m+r)';

     	         dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:) + ...
                                    (dGami(j*sGam+1:i*sGam,:) - K1*dD)*z(t,m+1:m+r)';
              else
    	         dzc(j*m+1:j*m+nobs,:) = -dH*xc -H*dxc(j*p+1:i*p,:);
       	         dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:);
	          end

    	      for k = 1:i
                l = k-1;
                Im(i,k) = Im(i,k) + dzc(l*m+1:l*m+nobs,:)'*iB1*dzc(j*m+1:j*m+nobs,:);
              end
          %
          end

          P0 = Phib*P0*Phib' + EQEt + K1*C*R*C'*K1' - ...
               K1*(E*S*C')' - E*S*C'*K1';
       %
       else
       %
          for i = 1:np
          %
              j = i-1;

              Phibi = dPhii(j*p+1:i*p,:);

              if r
     	         dxc(j*p+1:i*p,:) = Phibi*xc + Phi*dxc(j*p+1:i*p,:) + ...
                                    dGami(j*sGam+1:i*sGam,:)*z(t,m+1:m+r)';
              else
   	             dxc(j*p+1:i*p,:) = Phibi*xc + Phi*dxc(j*p+1:i*p,:);
    	      end
          %
          end

          P0 = Phi*P0*Phi' + EQEt;
       %
       end

       if r
          xc = Phi*xc + Gam*z(t,m+1:m+r)';
       else
          xc = Phi*xc;
       end
   %
   end
%
end

Im = Im + tril(Im,-1)';

if min(eig(Im)) <= 0, e4warn(8); end

varm = pinv(Im);

dts   = sqrt(diag(varm));
corrm = diag(1./dts)*varm*diag(1./dts);
E4OPTION(1) = oldfil;
