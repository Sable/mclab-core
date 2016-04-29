function drv_fft(scale)
%Driver for fast fourier transform
%input is n randomly generated complex numbers stored in an array of size 2*n
%n must be a power of 2
% transf=fft_four1(data,n,1) computes forward transform
% 1/n * fft_four1(transf,n,-1) computes backward tranform

n=1024*2^round(scale);
data=randn(1,2*n);

%'computing the forward transform'

out=fft_four1(data,n,1);

end
