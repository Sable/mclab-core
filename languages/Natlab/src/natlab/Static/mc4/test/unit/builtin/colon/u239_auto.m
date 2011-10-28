function [result] = u239_auto(n)
  x0 = ([0.62290169489]*n > .5);
  x1 = uint64([0.237964627092]*n);
  x2 = ([0.62290169489]*n > .5);
  result = colon(x0,x1,x2);
end
