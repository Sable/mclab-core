function z_hat = babai(R,y)
  %%
  %   compute the Babai estimation
  %   find a sub-optimal solution for min_z ||R*z-y||_2
  %   R - an upper triangular real matrix of n-by-n
  %   y - a real vector of n-by-1
  %   z_hat - resulting integer vector
  %
  n=length(y);
  z_hat=zeros(n,1);
  z_hat(n)=round(y(n)./R(n,n));

  for k=n-1:-1:1
    par=R(k,k+1:n)*z_hat(k+1:n);
    ck=(y(k)-par)./R(k,k);
    z_hat(k)=round(ck);
  end

end
