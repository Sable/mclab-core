function [result] = u83_auto(n)
  x0 = double([0.134364244112]*n);
  x1 = double([0.134364244112]*n);
  x2 = ([0.62290169489]*n > .5);
  result = colon(x0,x1,x2);
end
