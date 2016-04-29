function C = matmul(A,B,m,k,n)
    C=zeros(m,n);
    for j = 1:n
        for h = 1:k
            for i = 1:m
                C(i,j) = C(i,j)+A(i,h)*B(h,j);
            end
        end
    end

end
