function [Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din)
% SC2SS    - Converts the THD representation of a time-varying parameters
% model to the corresponding SS formulation.
%    [Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2ss(theta, din)
%
% 30/4/97
% Copyright (c) Jaime Terceiro, 1997

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if type ~= 200, e4error(14); end

if userflag
   [Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2own(theta, din,userf(1,:));
   return;
end

[H_D, type, m, r, s, l, np, userflag, userf, isinnov, szpriv] = e4gthead(din(H_D+1:2*H_D));
[Phi, Gam, E, Hv, Dv, Cv, Q, S, R] = thd2ss(theta(1:np,:), din(H_D+1:2*H_D+szpriv(1)));

if size(din,1) == 2*H_D+szpriv(1)
   Hf = []; Df = []; Cf = [];
else
   [H_D, type, mf, rf, s, lf, npf, userflag, userf, isinnov, szprivf] = e4gthead(din(2*H_D+szpriv(1)+1:3*H_D+szpriv(1)));
   [Phif, Gamf, Ef, Hf, Df, Cf, Qf, Sf, Rf] = thd2ss(theta(np+1:np+npf,:), din(2*H_D+szpriv(1)+1:size(din,1)));

   if lf
      l = max(l,1);
      Phi = [Phi zeros(l,lf); zeros(lf,l) Phif];
      Gam = [Gam zeros(l,rf); zeros(lf,r) Gamf];
      E   = [E zeros(l,size(Ef,2)); zeros(lf,size(E,2)) Ef];
      Q   = [Q zeros(size(Q,1),size(Qf,2)); zeros(size(Qf,1),size(Q,2)) Qf];         
      S   = [S zeros(size(S,1),size(Sf,2)); zeros(size(Sf,1),size(S,2)) Sf];
   else
      if rf, Gam = [Gam zeros(size(Phi,1),rf)]; end
      Hf = [];
      S =  [S  zeros(size(S,1),size(Sf,2))];
   end
   R   = [R zeros(size(R,1),size(Rf,2)); zeros(size(Rf,1),size(R,2)) Rf];
end