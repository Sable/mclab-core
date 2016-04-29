function [x0, P0, iP0, nonstat, U1, U2,R, S, uroots, ldet, RC, RS] = djccl(Phi, EQEt, k, Gu0)
% DJCCL    - Computes the initial condition for the covariance of
% the state vector proposed by De Jong y Chu-Chun-Lin (1994).
%    [x0, P0, iP0, nonstat, U1, U2, R, S, uroots, ldet, RC, RS] = djccl(Phi, EQEt, k, Gu0)
% These conditions are valid for stationary or non stationary models.
%
% 5/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

uno  = E4OPTION(12);
tolM = E4OPTION(13);
tolQ = E4OPTION(14);
zeps = E4OPTION(15);

x0 = zeros(size(Phi,1),1);

[P Q U1 U2 R S]  = bkjordan(Phi, uno);

uroots = size(P,1);
ldet = 0;

if size(Q,1) > 0 & size(P,1) > 0
%
   nonstat = 1;
   M = lyapunov(Q, S*EQEt*S');
   P0 = U2*M*U2' + k*U1*U1';

   if nargout > 2
      [Ns Sv Ns] = svd(M);
      Sv = diag(Sv);
      k = find(Sv > zeps);
      ldet = -sum(log(Sv(k)));      
      iSv = diag(1./Sv(k));
      iP0 = S'*Ns(:,k)*iSv*Ns(:,k)'*S;
      RC = [R;Ns(:,k)'*S];
      RS = [U1 U2*Ns(:,k)];
   end

   if nargin > 3
      x0 = U2*pinv(eye(size(Q))-Q,tolQ)*S*Gu0;
   end
%
elseif size(P,1) > 0
%
   nonstat = 2;

   P0 = k*U1;   % U1 is the identity matrix
   if nargout > 2
      iP0 = zeros(size(P0));
   end
   RC = R;
   RS = U1;
%
else
%
   nonstat = 0;
   P0 = lyapunov(Q, EQEt);
   [Ns Sv Ns] = svd(P0);
   Sv = diag(Sv);
   k = find(Sv > zeps);
   RC = Ns(:,k)';
   RS = RC';
   
   if nargin > 3
      x0 = pinv(eye(size(Q))-Q,tolQ)*Gu0;
   end
   iP0 = [];
%
end