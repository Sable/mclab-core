 function [C, U, V] = toydata_simdiag(m,n,num,noise,rk)
%toydata_simdiag      generate generic rectangular matrices of rank rk
%                      with common rank-one-nuclei
%
%usage
%  [C, U, V] = toydata_simdiag(m,n,num,noise,rk)
%
%input
%  (m,n)   dimension of matrices
%  num     number of matrices
%  noise   variance of centered symmetric Gaussian noise on the matrices; 
%             (no noise if set to zero or parameter omitted)
%  rk       rank of the matrices; default rk=min(m,n)
%output 
%  C   	   num-array of the (m x n) matrices
%           if m > n, m and n are exchanged so that m<=n
%  U       (m x rk)-matrix, columns are the rk normalized vectors 
%                           in the rank-one-decomposition, of size m
%  V       (n x rk)-matrix, columns are the rk normalized vectors 
%                           in the rank-one-decomposition, of size n
%
%author
%  franz.j.kiraly@tu-berlin.de

if m>n
    temp = n;
    n = m;
    m = temp;
end


if nargin<5
    rk = m;
end


U = zeros(m, rk);
V = zeros(n, rk);

for i=1:rk
   U(:,i)=randvec(m,1);
   V(:,i)=randvec(n,1);
end

C = zeros(m,n,num);
for i=1:num
    C(:,:,i)=U*diag(randn(rk,1))*V';
    C(:,:,i) = C(:,:,i) + noise*randn(m,n);
end

end


function [ vec ] = randvec(d,abs)
%randvec       random vector
%
%usage
%  vec=randvec(d,abs)
%
%input
%  d       vector dimension
%
% optional:
%  abs     absolute value of vec
%
%output 
%  vec     random real row vector with dimension d and 2-norm abs,
%           sampled uniformly from B^d, if no abs is specified
%           sampled uniformly from abs*S^d-1 else
%
%author
%  franz.kiraly(at)uni-ulm.de


vec = randn(1,d);

vec = vec/norm(vec);

 
if nargin > 1
    vec=abs*vec;
end

if nargin == 1
    abs=random('unif',0,1);
    abs=abs^(1/d);
    vec=vec*abs;
end

end