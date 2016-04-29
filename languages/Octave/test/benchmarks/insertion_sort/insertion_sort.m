function A = insertion_sort(A, n)
    for i = 2:n
        j = i;
        while j > 1 && A(j) < A(j-1)
            t = A(j);
            A(j) = A(j-1);
            A(j-1) = t;
            j = j-1;
        end
    end
end

