function [result] = u33_auto(n)
  x0 = ([0.62290169489]*n > .5);
  x1 = uint64([0.237964627092]*n);
  result = colon(x0,x1);
end
