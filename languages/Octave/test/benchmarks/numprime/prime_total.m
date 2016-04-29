function total = prime_total( n )
%PRIME_TOTAL Summary of this function goes here
%   Detailed explanation goes here
total =0;
%parfor
for i=2:n
    prime = 1;
    for j= 2:sqrt(i)
        if (mod(i,j) == 0)
            prime = 0;
            %break;
        end
    end
    total = total + prime;
end

end

