function [ways] = make_change(coins, n, amount)
    N = n+1;
    A = amount+1;
    T = zeros(N, A);
    % Initialize first column to 1
    for i = 1:N
        T(i, 1) = 1;
    end
    
    for i = 2:N
        for j = 2:A
            T(i, j) = T(i, j) + T(i-1, j);
            left_idx = (j-1) - coins(i-1);
            if left_idx >= 0
                T(i, j) = T(i, j) + T(i, left_idx+1);
            end
        end
    end
    ways = T(N,A);
end

