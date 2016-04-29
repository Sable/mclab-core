function xcor=mutin(adata)
% computing mutual information between
% data(:,1) and data (:,2)
N=length(adata);
[ydat  idat]=sort(adata);
ydat=idat;
ydat(idat(:,1),1)=(1:N)';
ydat(idat(:,2),2)=(1:N)';
clear idat
xcor=0; npar=1; poc(1)=1; kon(1)=N;
poradi=1:N; NN=zeros(1,4);
marg=zeros(20,4);
marg(1,:)=[1 1 N N];
run=0;
while npar>0
    run=run+1;
    apoc=poc(npar);
    akon=kon(npar);
    apor=poradi(apoc:akon);
    Nex=length(apor);
    ave=floor((marg(npar,1:2)+marg(npar,3:4))/2);
    J1=(ydat(apor,1)<=ave(1));
    J2=(ydat(apor,2)<=ave(2));
    I=[J1&J2  J1&~J2  ~J1&J2  ~J1&~J2];
    amarg=[marg(npar,1:2) ave; marg(npar,1) ave(2)+1 ave(1) marg(npar,4)
         ave(1)+1 marg(npar,2)  marg(npar,3) ave(2); ave+[1 1] marg(npar,3:4)];
    NN=sum(I);
    tst=4*sum((NN-Nex/4*ones(1,4)).^2)/Nex;
    if (tst>7.8)|(run==1)
       npar=npar-1;
       for ind=1:4
            if NN(ind) > 2
                 npar=npar+1;
                 akon=apoc+NN(ind)-1;
                 poc(npar)=apoc; kon(npar)=akon;
                 marg(npar,:)=amarg(ind,:);
                 poradi(apoc:akon)=apor(find(I(:,ind)));
                 apoc=akon+1;
            else
                  if NN(ind) > 0
                     Nx=amarg(ind,3)-amarg(ind,1)+1;
                     Ny=amarg(ind,4)-amarg(ind,2)+1;
                     xcor=xcor+NN(ind)*log(NN(ind)/(Nx*Ny));
                  end
             end
       end
  else
       Nx=marg(npar,3)-marg(npar,1)+1;
       Ny=marg(npar,4)-marg(npar,2)+1;
       xcor=xcor+Nex*log(Nex/(Nx*Ny));
       npar=npar-1;
   end
end
xcor=xcor/N+log(N); 
