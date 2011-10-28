function [result] = u247_auto(n)
  x0 = ([0.62290169489]*n > .5);
  x1 = ([0.62290169489]*n > .5);
  x2 = double([0.134364244112]*n);
  result = colon(x0,x1,x2);
end
