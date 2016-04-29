function [Phil, Gaml, El, Hl, Dl, Cl, Ql, Sl, Rl,Hli,Cli] = ufaggr(th, dh)

%[Phil, Gaml, El, Hl, Dl, Cl, Ql, Sl, Rl] = ufaggr(th, dh)
% User function for aggregation
% The aggregated model includes a header with the following aggregate
% structure:
% Type of aggregation: 1 stock, 0 flow
% Vector mx1 indicating aggregate series (1 aggregated, 0 non aggregated)

zeps = .001;
[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(dh);
% type of aggregation: 1 stock, 0 flow
typeaggr = dh(H_D+1);
maggr = dh(H_D+2:H_D+szpriv(2));
dh = dh(H_D+szpriv(2)+1:size(dh,1));

[H_D, type, m, r, s0, n, np, userflag, userf, innov, szpriv] = e4gthead(dh);
[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(th, dh);

if n & ~innov(1)
   [E,Q] = sstoinn(Phi, E, H, C, Q, S, R);
   C = eye(m);
end


Gaml = [];
Dl = [];

U = cholp(Q);

% 1)low frequency formultion

n2 = size(Phi,1);
Phil = Phi^s;
sE = size(E,2);
sC = size(C,2);
Hl = zeros(m,n2);
Hli = zeros(m*s,n2);
El = zeros(n, sE*s);
Cl = zeros(m,sC*s);
Cli = zeros(m*s,sC*s);
Ql = eye(s*sE);

if r
   Gaml = zeros(n2,r*s);
   Dl = zeros(m,r*s);
   Dli = zeros(m*s,r*s);
end

Phi0 = eye(n2);
C = C*U';
Cl0 = C;
Cli0 = C;
E = E*U';
if r, Dl0 = D; Dli0 = D; end

for i=1:s
    Hli((i-1)*m+1:i*m,:) = H*Phi0;
    Hl = Hl + H*Phi0;
    Phi0E = Phi0*E;
    El(:,(s-i)*sE+1:(s-i+1)*sE)= Phi0E;
    Cl(:,(s-i)*sC+1:(s-i+1)*sC)= Cl0;

    Cli((s-i)*m+1:s*m,(s-i)*sC+1:(s-i+1)*sC)= Cli0;

    if r 
       Phi0G = Phi0*Gam;
       Gaml(:,(s-i)*r+1:(s-i+1)*r)= Phi0G;
       Dl(:,(s-i)*r+1:(s-i+1)*r)= Dl0;
       Dli((s-i)*m+1:s*m,(s-i)*r+1:(s-i+1)*r)= Dli0;
       Dl0 = Dl0 + H*Phi0G;
       Dli0 = [Dli0;H*Phi0G];
    end

    Cl0 = Cl0+H*Phi0E;
    Cli0 = [Cli0;H*Phi0E];
    Phi0 = Phi0*Phi;
end

Cl2 = [];
Dl2 = [];
Hl2 = [];

for i=1:m
    if maggr(i)
       if typeaggr
          pos = (s-1)*m+i;
          Hl2 = [Hl2;Hli(pos,:)];
          if r, Dl2 = [Dl2;Dli(pos,:)]; end
          Cl2 = [Cl2;Cli(pos,:)];
       else
          Hl2 = [Hl2;Hl(i,:)];
          if r, Dl2 = [Dl2;Dl(i,:)]; end
          Cl2 = [Cl2;Cl(i,:)];           
       end
    else
       Hl2 = [Hl2;Hli(i:m:m*s,:)];
       if r, Dl2 = [Dl2;Dli(i:m:m*s,:)]; end
       Cl2 = [Cl2;Cli(i:m:m*s,:)];
    end
end

Hl = Hl2;
Dl = Dl2;
Cl = Cl2;

Sl = Ql; Rl = Ql;
