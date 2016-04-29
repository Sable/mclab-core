function main(size)
    tic();
    max_length = 0;
    max_num = 0;
    for i = 1:size
        length = collatz(i);
        if length > max_length
            max_length = length;
            max_num = i;
        end
    end
    t = toc();
    disp('OUT');
    disp(max_num);
    disp(max_length);
    disp('TIME');
    disp(t);
end
