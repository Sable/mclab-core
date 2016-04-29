% Direct computation of Cauchy-Schwartz mutual information
%
% Author : Sohan Seth (sohan@cnel.ufl.edu) Date : Date: 01.06.2009

function val = qmi_cs_2(X,Y,kernelSize)

n = length(X); c = 1/(sqrt(2*pi)*kernelSize);
Kxx = ones(n,n); Kyy = ones(n,n);
for count1 = 1:n
    for count2 = count1+1:n
        Kxx(count1,count2) = exp(-(norm(X(count1,:) - X(count2,:)))^2/(2*kernelSize^2));
        Kxx(count2,count1) = Kxx(count1,count2);
        Kyy(count1,count2) = exp(-(norm(Y(count1,:) - Y(count2,:)))^2/(2*kernelSize^2));
        Kyy(count2,count1) = Kyy(count1,count2);
    end
end

Kxx = c * Kxx; Kyy = c* Kyy;

% a = 0;
% for count1 = 1:n
%     for count2 = 1:n
%         a = a + Kxx(count1,count2) * Kyy(count1,count2);
%     end
% end
% a = a/ n^2;

a = sum(sum((Kxx .* Kyy)))/n^2;

% b = 0;
% for count1 = 1:n
%     for count2 = 1:n
%         for count3 = 1:n
%             b = b + Kxx(count1,count2) * Kyy(count1,count3);
%         end
%     end
% end
% b = b/ n^3;

b = sum(sum(Kxx * Kyy))/n^3;

c = sum(Kxx(:)) * sum(Kyy(:)) / n^4;

val = log(a*c/b^2);