function [X Y] = split(A, n)
    mid = floor(n / 2);
    X = A(1:mid);
    Y = A(mid+1:n);
end

