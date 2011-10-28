function [result] = u111_auto(n)
  x0 = int8([0.956034271889]*n);
  x1 = single([0.844421851525]*n);
  x2 = uint64([0.237964627092]*n);
  result = colon(x0,x1,x2);
end
