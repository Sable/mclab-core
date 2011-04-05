function [time, output, valid] = drv_mbrt(scale)

% computes mandelbrot set

N = round(6000*sqrt(scale)); % set N
Nmax = round(10^3*sqrt(scale)); % set Nmax

t1 = clock;

% just
% a 
% bunch
% of comments
set=mandelbrot(N, Nmax);

t2 = clock;

% Compute the running time in seconds
time = (t2-t1)*[0, 0, 86400, 3600, 60, 1]';

% Store the benchmark output
%{
these are comments
on many 
lines
%}
output = {mean(mean(set(:)))};

% Validate the result
t = Nmax*N;
if abs(sum(sum(set))/(t)-0.37429481997515) < 0.01;
	valid = 'PASS';
else
	valid = 'FAIL';
end

for i = 1:100 % yay comment
    % word
    i = 2;
    % hiho
end % a comment

  ... this is a test
      
  foo ... and this too
      ;
end



