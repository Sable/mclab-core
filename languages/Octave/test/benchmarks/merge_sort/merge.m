function [C] = merge(A, B, len_a, len_b)
    C = zeros(1, len_a + len_b);
    i = 1;
    j = 1;
    k = 1;
    while i <= len_a && j <= len_b
        if A(i) <= B(j)
            C(k) = A(i);
            i = i+1;
            k = k+1;
        else
            C(k) = B(j);
            j = j+1;
            k = k+1;
        end
    end
    
    while i <= len_a
        C(k) = A(i);
        i = i+1;
        k = k+1;
    end
    
    while j <= len_b
        C(k) = B(j);
        j = j+1;
        k = k+1;
    end
end

