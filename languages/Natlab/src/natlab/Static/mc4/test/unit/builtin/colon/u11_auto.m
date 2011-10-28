function [result] = u11_auto(n)
  x0 = double([0.134364244112]*n);
  x1 = ([0.62290169489]*n > .5);
  result = colon(x0,x1);
end
