function [result] = u162_auto(n)
  x0 = uint64([0.237964627092]*n);
  x1 = uint64([0.237964627092]*n);
  x2 = single([0.844421851525]*n);
  result = colon(x0,x1,x2);
end
