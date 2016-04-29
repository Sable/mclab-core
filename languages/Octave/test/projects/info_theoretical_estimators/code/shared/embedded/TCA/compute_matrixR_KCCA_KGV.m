function [Rkappa] = compute_matrixR_KCCA_KGV(x,ds,co)
%R computation of the KCCA/KGV method.
%This function is an adaptation to the subspace case (ds(i)>=1) of contrast_tca_kgv.m (i) written by Francis Bach, and (ii) being part of the TCA package ("http://www.di.ens.fr/~fbach/tca/index.htm").
%
%INPUT:
%   x: x(:,t) is the t^th sample.
%  ds: subspace dimensions.
%  co: cost object (structure).

%Initialization:
    N = size(x,2);%number of samples
    m = length(ds);%number of subspaces
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).

Rkappa = [];
sizes = [];

for i=1:m
    ind = [cum_ds(i):cum_ds(i)+ds(i)-1];
    %cholesky decomposition:
          [G,Pvec] = chol_gauss(x(ind,:),co.sigma,N*co.eta);
    [a,Pvec]=sort(Pvec);
    G=centerpartial(G(Pvec,:));
    
    % regularization (see paper for details)
    [A,D]=eig(G'*G);
    D=diag(D);
    indexes=find(D>=N*co.eta & isreal(D)); %removes small eigenvalues
    [newinds,order]=sort(D(indexes));
    order=flipud(order);
    neig=length(indexes);
    indexes=indexes(order(1:neig));  
    if (isempty(indexes)), indexes=[1]; end
    D=D(indexes);
    V=G*(A(:,indexes)*diag(sqrt(1./(D))));
    Us{i}=V;
    Lambdas{i}=D;
    Dr=D;
    for j=1:length(D)
        Dr(j)=D(j)/(N*co.kappa+D(j));
    end
    Drs{i}=Dr;
    sizes(i)=size(Drs{i},1);
end

% calculate Rkappa
Rkappa=eye(sum(sizes));
starts=cumsum([1 sizes]);
starts(m+1)=[];
for i=2:m
    for j=1:(i-1)
        newbottom=diag(Drs{i})*(Us{i}'*Us{j})*diag(Drs{j});
        Rkappa(starts(i):starts(i)+sizes(i)-1,starts(j):starts(j)+sizes(j)-1)=newbottom;
        Rkappa(starts(j):starts(j)+sizes(j)-1,starts(i):starts(i)+sizes(i)-1)=newbottom';
    end
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function G2=centerpartial(G1)
% CENTERPARTIAL - Center a gram matrix of the form K=G*G'

[N,NG]=size(G1);
G2 = G1 - repmat(mean(G1,1),N,1);
