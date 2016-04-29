function main(size)
    m=size;
    k=size/2;
    n=size;

    A = rand(m,k);
    B = rand(k,n);

    tic();
    matmul(A, B, m, k, n);
    t = toc();

    disp('OUT');
    disp(1);
    disp('TIME');
    disp(t);
end
