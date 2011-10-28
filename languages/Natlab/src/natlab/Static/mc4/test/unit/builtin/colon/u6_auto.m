function [result] = u6_auto(n)
  x0 = double([0.134364244112]*n);
  x1 = single([0.844421851525]*n);
  result = colon(x0,x1);
end
