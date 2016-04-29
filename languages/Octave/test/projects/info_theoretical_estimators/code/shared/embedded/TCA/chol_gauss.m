function [G, Pvec] = chol_gauss(x,sigma,tol)

% CHOL_GAUSS - incomplete Cholesky decomposition of the Gram matrix defined
%                by data x, with the Gaussiab kernel with width sigma
%                Symmetric pivoting is used and the algorithms stops 
%                when the sum of the remaining pivots is less than TOL.
% 

% CHOL_GAUSS returns returns an uPvecer triangular matrix G and a permutation 
% matrix P such that P'*A*P=G*G'.

% P is ONLY stored as a reordering vector PVEC such that 
%                    A(Pvec,Pvec)= G*G' 
% consequently, to find a matrix R such that A=R*R', you should do
% [a,Pvec]=sort(Pvec); R=G(Pvec,:);

% Copyright (c) Francis R. Bach, 2002.
%
% Modification (Zoltan Szabo, Sep. 2012): accelerated by 'sqdistance' (faster pairwise distance computation).

n=size(x,2);
Pvec= 1:n;
I = [];
%calculates diagonal elements (all equal to 1 for gaussian kernels)
diagG=ones(n,1);
i=1;
G=[];

while ((sum(diagG(i:n))>tol)) 
   G=[G zeros(n,1)];
   % find best new element
   if i>1
      [diagmax,jast]=max(diagG(i:n));
      jast=jast+i-1;
      %updates permutation
      Pvec( [i jast] ) = Pvec( [jast i] );
      % updates all elements of G due to new permutation
      G([i jast],1:i)=G([ jast i],1:i);
      % do the cholesky update
      
      
   else
      jast=1;
   end
   
   
   
   G(i,i)=diagG(jast); %A(Pvec(i),Pvec(i));
   G(i,i)=sqrt(G(i,i));
   if (i<n)
      %calculates newAcol=A(Pvec((i+1):n),Pvec(i))
      newAcol = exp(-.5/sigma^2*sqdistance(x(:, Pvec((i+1):n) ),x(:,Pvec(i))));
      if (i>1)
         G((i+1):n,i)=1/G(i,i)*( newAcol - G((i+1):n,1:(i-1))*(G(i,1:(i-1)))');
      else
         G((i+1):n,i)=1/G(i,i)*newAcol;
      end
      
   end
   
   % updates diagonal elements
   if (i<n) 
      diagG((i+1):n)=ones(n-i,1)-sum(   G((i+1):n,1:i).^2,2  );
   end
   i=i+1;
end