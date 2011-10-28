function [result] = u249_auto(n)
  x0 = ([0.62290169489]*n > .5);
  x1 = ([0.62290169489]*n > .5);
  x2 = uint64([0.237964627092]*n);
  result = colon(x0,x1,x2);
end
