function [result] = u93_auto(n)
  x0 = double([0.134364244112]*n);
  x1 = uint64([0.237964627092]*n);
  x2 = uint64([0.237964627092]*n);
  result = colon(x0,x1,x2);
end
