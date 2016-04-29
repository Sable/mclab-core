function y = collatz(n)
    y = 0;
    while n ~= 1
        if mod(n, 2) == 0
            n = n / 2;
        else
            n = 3*n + 1;
        end
        y = y+1;
    end
end
