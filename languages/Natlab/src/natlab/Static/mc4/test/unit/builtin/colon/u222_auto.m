function [result] = u222_auto(n)
  x0 = ([0.62290169489]*n > .5);
  x1 = double([0.134364244112]*n);
  x2 = single([0.844421851525]*n);
  result = colon(x0,x1,x2);
end
