function ways = make_change(coins, curr, n, amount)
    if curr > n
        ways = 0;
    else
        coin = coins(curr);
        if coin > amount
            ways = make_change(coins, curr+1, n, amount);
        elseif coin == amount
            ways = 1 + make_change(coins, curr+1, n, amount);
        else
            ways = make_change(coins, curr, n, amount-coin) + make_change(coins, curr+1, n, amount);
        end
    end
end

