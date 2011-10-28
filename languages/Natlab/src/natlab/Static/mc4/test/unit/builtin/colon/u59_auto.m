function [result] = u59_auto(n)
  x0 = single([0.844421851525]*n);
  x1 = uint64([0.237964627092]*n);
  x2 = ([0.62290169489]*n > .5);
  result = colon(x0,x1,x2);
end
