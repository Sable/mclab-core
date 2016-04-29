function theta2 = e4prees1(theta, din, z)
%
% 7/3/97

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

if nargin < 3,  e4error(3); end

% innovations?
[H_D, type, m, r, s, n, np, userflag, userf, innov] = e4gthead(din);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
N = size(z,1);
n = size(Phi,1);
m = size(H,1);
r = max([size(Gam,2), size(D,2)]);
if size(z,2) ~= m+r, e4error(11); end

if any(abs(eig(Phi)) >= E4OPTION(12)), stat = 0; else stat = 1; end
if stat | innov(1), offs = 0; else, offs = 1; end

i = max(round(log(N)),ceil(n/m)+2+offs);

if N < 2*i*(m+r+1-stat)-1, j = max(fix((N-i*(m+r+1)+1)/(m+r+1)),ceil(n/m)+1); else j = i; end
ij = i+j;

if N < ij*(m+r+1-stat)-1
   i = ceil(n/m)+1+offs; j = i; ij = i+j;
   if N < i*(m+r+1-stat)+j*(r+1-stat)-1, e4error(35); end
end

missing = any(any(isnan(z(:,1:m))));
if missing
   z2 = z;
   for h=1:m
       k = find(isnan(z(:,h)));
       sk = size(k,1);
       if sk
          k2 = find(k-[min(0,k(1)-2);k(1:sk-1)] > 1);
       
          if size(k2,1) <= 1
             if k(1) == 1, z1 = z(k(sk)+1,h);
             elseif k(sk) == N, z1 = z(k(1)-1,h);
             else, z1 = (z(k(sk)+1,h) + z(k(1)-1,h))/2; end
             z2(k,h) = z1*ones(sk,1);
          else
             if k(1) == 1, z1 = z(k(k2(2)-1)+1,h);
             else, z1 = (z(k(k2(2)-1)+1,h) + z(k(1)-1,h))/2; end
             z2(k(k2(1)):k(k2(2)-1),h) = z1*ones(k(k2(2)-1)-k(k2(1))+1,1);

             for l=2:size(k2,1)-1
                 z2(k(k2(l)):k(k2(l+1)-1),h) = (z(k(k2(l))-1,h)+z(k(k2(l+1)-1)+1,h))/2*ones(k(k2(l+1)-1)-k(k2(l))+1,1);
             end
 
             if k(sk) == N, z1 = z(k(k2(l+1))-1,h);
             else, z1 = (z(k(sk)+1,h) + z(k(k2(l+1))-1,h))/2; end
             z2(k(k2(l+1)):k(sk),h) = z1*ones(k(sk)-k(k2(l+1))+1,1);
          end
       end
   end
end
   
if r
   Yi = blkhkel(z(:,1:m), ij, 1, stat);
   N = min(size(Yi,2), size(z,1));
   if missing
      K  = any(isnan(Yi));
      Yi = blkhkel(z2(:,1:m)*N/(N-sum(K)/2), ij, 1, stat);
      Ui = blkhkel(z(:,m+1:m+r)*N/(N-sum(K)/2), ij, 1, stat);
      Yi(:,K) = Yi(:,K)/2;
      Ui(:,K) = Ui(:,K)/2;
   else
      Ui = blkhkel(z(:,m+1:m+r), ij, 1, stat);
   end
   [Q, R] = qr([Ui(r*i+1:r*ij,:);Ui(1:r*i,:);Yi]', 0);
   ix = zeros(5,2);
   ix(:,1) = [1; j*r+1; ij*r+1; ij*r+i*m+1; ij*r+(i+1)*m+1];
   ix(:,2) = [ix(2:5,1)-1; ij*(m+r)];
   R = R'/sqrt(N);
else
   Yi = blkhkel(z, ij, 1, stat);
   N = min(size(Yi,2), size(z,1));
   if missing
      K  = any(isnan(Yi));
      Yi = blkhkel(z2(:,1:m)*N/(N-sum(K)/2), ij, 1, stat);
      Yi(:,K) = Yi(:,K)/2;
   end
   [Q, R] = qr(Yi', 0);
   R = R'/sqrt(N);
   ix = zeros(3,2);
   if stat | innov(1)
      ix(:,1) = [1; i*m+1; (i+1)*m+1];
   else
      ix(:,1) = [m+1; i*m+1; (i+1)*m+1];
   end
   ix(:,2) = [ix(2:3,1)-1; ij*m];
end

if size(R,1) > size(R,2)
   R = [R zeros(size(R,1), size(R,1)-size(R,2))];
   pond = 0;
else
   if stat | innov(1), pond = 1; else, pond = 0; end
end

sth = size(theta,1);
if size(theta,2) == 1, theta2 = [theta zeros(sth,1)]; else, theta2 = theta; end

index = e4ds(theta,din);
index = ~(~index);

if innov(1)
   theta2(:,2) = theta2(:,2) | index(:,2);
end

k = find(theta2(:,2)==0);

if size(k,1) > 0
   verbose = E4OPTION(10);
   E4OPTION(10) = 0; % verbose no
   if innov(1)
      theta2(k,1) = -.5*ones(size(k,1),1);
      theta2 = e4min('e4prees2', theta2, '', din, R, ix, [i j pond N]);
   else
      theta2(k,1) = -.5*ones(size(k,1),1);
      if sum(index(k,1)) < size(k,1)
         theta2(k,2) = index(k,1);
         theta2 = e4min('e4prees3', theta2, '', din, R, ix, [i j pond N]);
      end
      [f, KB] = e4prees3(theta2, din, R, ix, [i j pond N]);
      theta2(k(index(k,2)),1) = trace(KB(n+1:n+m,:))*ones(sum(index(k,2)),1);
      theta2(k,2) = ~index(k,1);
      if sum(~index(k,1)) < size(k,1)
         theta2 = e4min('e4prees4',theta2, '',din, KB, [i j pond N]);
      end
   end
   E4OPTION(10) = verbose;
else
   pond = 0;
end

if innov(1)
   if pond
      [f, B] = e4prees2(theta2,din,R,ix,[i j pond N]);
   else
      if missing
         [f e] = lfmiss(theta2,din,z);
      else
         [f e] = lffast(theta2,din,z);
      end
      B = e'*e/size(z,1);
   end

   if E4OPTION(5) == 1, B = cholp(B)'; end
   theta2 = e4noise(theta2,din, B);
end

if size(theta,2) == 1, theta2 = theta2(:,1); else, theta2(:,2) = theta(:,2); end
