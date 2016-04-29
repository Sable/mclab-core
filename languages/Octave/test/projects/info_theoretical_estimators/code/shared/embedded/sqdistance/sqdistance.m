function D = sqdistance(A, B, M)
% Compute square Euclidean or Mahalanobis distances between all pair of vectors.
%   A: d x n1 data matrix
%   B: d x n2 data matrix
%   M: d x d  Mahalanobis matrix
%   D: n1 x n2 pairwise square distance matrix
% Written by Michael Chen (sth4nth@gmail.com).
if nargin == 1
    A = bsxfun(@minus,A,mean(A,2));
    S = full(dot(A,A,1));
    D = bsxfun(@plus,S,S')-full(2*(A'*A));
    %Modified by Zoltan Szabo (Aug 11, 2013; "http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ac (dot) uk").
    %Reason: diag(D) can contain small, but _negative_ elements; this is what we correct by setting the diagonal elements to the correct zero value:
        s = size(D,1);
        I = [1:s];
        linear_indices = sub2ind([s,s],I,I);
        D(linear_indices) = 0;
elseif nargin == 2
    assert(size(A,1)==size(B,1));
    D = bsxfun(@plus,full(dot(B,B,1)),full(dot(A,A,1))')-full(2*(A'*B));
elseif nargin == 3
    assert(size(A,1)==size(B,1));
    R = chol(M);
    RA = R*A;
    RB = R*B;
    D = bsxfun(@plus,full(dot(RB,RB,1)),full(dot(RA,RA,1))')-full(2*(RA'*RB));
end
