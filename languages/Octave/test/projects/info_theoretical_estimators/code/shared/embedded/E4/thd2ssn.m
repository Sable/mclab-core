function [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssn(theta, din)
% THD2SSN  - Returns the SS matrices from nested model in THD format.
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssn(theta, din)
%
% 18/9/97
% Copyright (c) Jaime Terceiro, 1997

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if fix(type/10) ~= 2, e4error(14); end

if userflag
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2own(theta, din, userf(1,:));
   return;
end
   mt = m; nt = n; rt = r;

   mode = rem(type,20);

   Phi = zeros(n);

   ptr = H_D+szpriv(2)+1;
   ptr2 = 1;
   sd = size(din,1);
   if mode
      H = []; E = []; Q = []; S = []; C = []; R = [];
      Gam = []; D = [];
      ec1 = 1; cc1 = 1;
   else
      H = zeros(m, n);
      E = zeros(n,m);
      if r, Gam0 = []; Gam = zeros(n,r); D = 0; else, Gam = []; D = []; end
      C = 1;
   end
   nc1 = 1;

   while ptr < sd
       [H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:sd));
       [Phi1, Gam1, E1, H1, D1, C1, Q1, S1, R1] = thd2ss(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1));

       nc2 = nc1+n-1;
       if mode % nesting observable inputs
          if nc1 > 1
             GamH1 = Gam*H1; GamC1 = Gam*C1; 
             if r, GamD1 = Gam*D1; else,  GamD1 = []; end
          else
             GamH1 = []; GamC1 = []; GamD1 = []; 
          end
          
          if ~isempty(D)
             DH1 = D*H1; DC1 = D*C1;
             if r, DD1 = D*D1; else, DD1 = [];  end
          else
             DH1 = []; DC1 = []; DD1 = []; 
          end
          
          if n
             Phi(1:nc2, nc1:nc2) = [GamH1;Phi1];
             H = [H DH1;zeros(m,size(H,2)) H1];
          end
          if isinnov(1)
             if n
                E = [E GamC1; zeros(n,size(E,2)) E1(1:n,:)];
             else, E = [E GamC1]; end
             Q = [Q zeros(size(Q,1),size(Q1,2)); zeros(size(Q1,1), size(Q,2)) Q1];
             S = [S zeros(size(S,1),size(S1,2)); zeros(size(S1,1), size(S,2)) S1];
          else
             if n
                E = [E GamC1 zeros(size(Gam,1),size(E1,2)); zeros(n,size(E,2)+size(C1,2)) E1(1:n,:)];
                Q = [Q zeros(size(Q,1),size(Q1,2)+size(R1,2)); zeros(size(R1,1), size(Q,2)) R1 zeros(size(R1,1),size(Q1,2));zeros(size(Q1,1), size(Q,2)+size(R1,2)) Q1];
                S = [S zeros(size(S,1),size(S1,2)); zeros(size(S1,1)+size(R1,1), size(S,2)) [R1;S1]];
             else
                E = [E GamC1];
                Q = [Q zeros(size(Q,1),size(R1,2)); zeros(size(R1,1), size(Q,2)) R1];
                S = [S zeros(size(S,1),size(S1,2)); zeros(size(R1,1), size(S,2)) R1];
             end
          end
          if r, Gam = [GamD1;Gam1(1:n,:)]; else, Gam = []; end
          C = [C DC1; zeros(m,size(C,2)) C1];
          R = [R zeros(size(R,1),size(R1,2)); zeros(size(R1,1), size(R,2)) R1];
          D = [DD1;D1];
      else
          if nc1 > 1
             E0H1 = E0*H1; E0C1 = E0*C1;
             if r, E0D1 = E0*D1; end
          else
             E0H1 = []; E0C1 = []; E0D1 = [];
          end
          if n
             Phi(1:nc2,nc1:nc2) = [E0H1;Phi1];
             H(:,nc1:nc2) = C*H1;
          end
          E(1:nc2,:) = [E0C1;E1(1:n,:)];

          if r
             if nc1 > 1
                Gam(1:nc2,:) = [Gam0+E0D1;Gam1(1:n,:)];
             else
                Gam(1:nc2,:) = Gam1(1:n,:);
             end
             Gam0 = Gam(1:nc2,:);
             D = D+C*D1;
          end
          C = C*C1;
          E0 = E(1:nc2,:);
      end
      nc1 = nc2+1;
      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np;

   end

   if ~mode, Q = Q1; S = Q; R = Q; end
   
   if ~nt
      Phi = 0; H = zeros(mt,1);
      if r, Gam = zeros(1,rt); end
      E = zeros(1,size(Q,1));
   end