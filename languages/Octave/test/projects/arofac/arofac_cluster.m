function [EVU,EVV,estrank] = arofac_cluster(C,repetitions,bandwidth)
%arofac       Approximate Rank One FACtorization of tensors
%              uses mean shift clustering to obtain rank-one-components
%
%usage
%  [EVU,EVV,estrank,err] = arofac_cluster(C,repetitions,bandwidth)
%
%input
%  C              (m,n,num)-tensor of degree 3, m<=n
%optional:
%  repetitions    number of candiate rank-one-components; default=100
%  bandwidth      for rank estimation by mean shift clustering; default=0.1
%
%output
%
%  EVU            (m x estrank)-matrix of clustered rank-one-components
%  EVV            (n x estrank)-matrix of clustered rank-one-components
%  estrank        the estimated rank
%
%uses
%  MeanShiftCluster by Bryan Feldman available at
%  http://www.mathworks.com/matlabcentral/fileexchange/10161-mean-shift-clustering/content/MeanShiftCluster.m
%authors
%  franz.j.kiraly(at)tu-berlin.de
%  andreas.ziehe(at)tu-berlin.de

%Copyright (c) 2013, Franz J. Kiraly, Andreas Ziehe
%All rights reserved.
%see license.txt


if nargin<2
    repetitions=100;    
end

if nargin <3,
 bandwidth=0.1;
end

[m,n,k]=size(C);
rk=min(min(m,n),k);

[EVUs,EVVs]=arofac(C,repetitions);

EVUsgn=repmat(sign(EVUs(1,:)),m,1);
EVVsgn=repmat(sign(EVVs(1,:)),n,1);

EVUs=EVUs.*EVUsgn;
EVVs=EVVs.*EVVsgn;


X=[EVUs;EVVs];

[cents] = MeanShiftCluster(X,bandwidth);


EVU=cents(1:m,:);
EVV=cents((m+1):(m+n),:);

estrank = size(cents,2);  %rank equals number of cluster centers

end