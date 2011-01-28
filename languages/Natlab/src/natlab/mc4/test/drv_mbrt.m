function [time, output, valid] = drv_mbrt(scale)

% computes mandelbrot set

N = round(6000*sqrt(scale));
Nmax = round(10^3*sqrt(scale));

t1 = clock;

set=mandelbrot(N, Nmax);

t2 = clock;

% Compute the running time in seconds
time = (t2-t1)*[0, 0, 86400, 3600, 60, 1]';

% Store the benchmark output
output = {mean(mean(set(:)))};

% Validate the result
if abs(sum(sum(set))/(Nmax*N)-0.37429481997515) < 0.01;
	valid = 'PASS';
else
	valid = 'FAIL';
end

end

