function [result] = u3_auto(n)
  x0 = single([0.844421851525]*n);
  x1 = uint64([0.237964627092]*n);
  result = colon(x0,x1);
end
