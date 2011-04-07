function [time, output, valid] = drv_mbrt(scale)

% computes mandelbrot set

N = round(6000*sqrt(scale)); % set N
Nmax = round(10^3*sqrt(scale)); % set Nmax

t1 = clock;

set=mandelbrot(N, Nmax);

t2 = clock;

% Compute the running time in seconds
time = (t2-t1)*[0, 0, 86400, 3600, 60, 1]';

% Store the benchmark output
output = {mean(mean(set(:)))};

% Validate the result
t = Nmax*N;
if abs(sum(sum(set))/(t)-0.37429481997515) < 0.01;
	valid = 'PASS';
else
	valid = 'FAIL';
end

end



