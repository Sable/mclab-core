% Direct computation of Correntropy coefficient
%
% Author : Sohan Seth (sohan@cnel.ufl.edu) Date : Date: 01.06.2009

function val = correncoef_2(X,Y,kernelSize)

n = length(X);
Kxx = ones(n,n); Kyy = ones(n,n);
for count1 = 1:n
    for count2 = count1+1:n
        Kxx(count1,count2) = exp(-(norm(X(count1,:) - X(count2,:)))^2/(2*kernelSize^2));
        Kxx(count2,count1) = Kxx(count1,count2);
        Kyy(count1,count2) = exp(-(norm(Y(count1,:) - Y(count2,:)))^2/(2*kernelSize^2));
        Kyy(count2,count1) = Kyy(count1,count2);
    end
end

Kxy = ones(n,n);
for count1 = 1:n
    for count2 =1:n
        Kxy(count1,count2) = exp(-(norm(X(count1,:) - Y(count2,:)))^2/(2*kernelSize^2));
    end
end

A  = sum(exp(-((X - Y)).^2/(2*kernelSize^2)))/n;

val = (A - sum(Kxy(:))/n^2)/sqrt((1 - sum(Kxx(:))/n^2)*(1 - sum(Kyy(:))/n^2));