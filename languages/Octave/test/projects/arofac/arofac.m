function [EVU,EVV,fX] = arofac(C,repetitions,iterations)
%arofac       Approximate Rank One FACtorization of tensors
%              outputs single candidate rank-one-components
%
%usage
%  [EVU,EVV] = arofac(C,repetitions,interations)
%
%input
%  C              (m,n,num)-tensor of degree 3, m<=n
% optional:
%  repetitions    number of candiate rank-one-components; default=100
%  iterations     number of iterations for Lanczos/exponentiation step; default=200
%
%output
%  EVU            (m x repetitions)-matrix of candiate rank-one-components
%  EVV            (n x repetitions)-matrix of candiate rank-one-components
%
%authors
%  franz.j.kiraly(at)tu-berlin.de
%  andreas.ziehe(at)tu-berlin.de

%Copyright (c) 2013, Franz J. Kiraly, Andreas Ziehe
%All rights reserved.
%see license.txt

m = size(C,1);
n = size(C,2);

rk=m;

num = size(C,3);

nd = m*n;

EVU = zeros(m,repetitions);
EVV = zeros(n,repetitions);

if nargin<2
    
    repetitions=100;
    
end

if nargin<3
    
    iterations=200;
    
end


fX=zeros(repetitions,iterations);

%if 0
Q = zeros(num, nd);

for i=1:num
    Q(i,:) = reshape(C(:,:,i),1,nd);
end


[UQ,SQ,VQ] = svd(Q);
Q=SQ(1:rk,1:rk)*VQ(:,1:rk)';

for rep=1:repetitions
        
    if mod(rep,10) == 0
        if mod(rep,100)== 0
            fprintf('|\n')
        else
            fprintf(':')
        end
    else
        fprintf('.')
    end
    
    P=eye(rk,rk);
    P=P(:,randperm(rk));
    Q=P*Q;
    
    Qinv = pinv(Q);
    
    init =randn(1,rk-1);
    
    x=[1 init];
    
    
    for k=1:iterations,
        
        A=reshape(x*Q,m,n);
        
        %projection
        AA=A*A'*A;
        
        x=reshape(AA,1,nd)*Qinv;
        
        
        
        x=x/x(1); %dehomogenize
        
        if mod(k,20) == 0
            fX(rep,k)=disttorkone(reshape(x*Q,m,n));
        else
            fX(rep,k) = NaN;
        end
        
        if fX(rep,k)<1e-22,  
            break
        end
        
    end
    
    rkonemat = reshape(x*Q,m,n);
    
    [U,D,V] = svd(rkonemat);
    [NOTUSED,maxi] = max(abs(diag(D)));
    
    EVU(:,rep) = U(:,maxi);
    EVV(:,rep) = V(:,maxi);
    
    
end

end

function dist = disttorkone(mat)
%disttorkone(mat)
% normalized Frobenius distance to rank one approximation of matrix mat
%
%usage
%  dist = disttorkone(mat)
%
%input
%  mat     m x n matrix
%
%output
%  dist    Frobenius distance of mat to its rank one approximation,
%          divided by the Frobenius norm of mat
%
%authors
%  franz.j.kiraly(at)tu-berlin.de
%  andreas.ziehe(at)tu-berlin.de

[m,n]=size(mat);

if m==n
    d=m;
    [NOTUSED,D] = eig(mat);
    Es = sort(abs(diag(D)))/norm(mat);
    dist = Es(1:d-1)'*Es(1:d-1);
    
else
    d = min(m,n);
    [NOTUSED1,D,NOTUSED] = svd(mat);
    Es = sort(abs(diag(D)))/norm(mat);
    dist = Es(1:d-1)'*Es(1:d-1);
    
end

end
