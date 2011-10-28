function [result] = u174_auto(n)
  x0 = uint64([0.237964627092]*n);
  x1 = ([0.62290169489]*n > .5);
  x2 = single([0.844421851525]*n);
  result = colon(x0,x1,x2);
end
