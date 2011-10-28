function [result] = u67_auto(n)
  x0 = single([0.844421851525]*n);
  x1 = ([0.62290169489]*n > .5);
  x2 = double([0.134364244112]*n);
  result = colon(x0,x1,x2);
end
